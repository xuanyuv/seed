package com.jadyer.seed.comm.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * 处理加盐Hash操作密码的工具类
 * <p>
 *     参考了以下两篇文章<br>
 *     http://drops.wooyun.org/papers/1066<br>
 *     https://github.com/defuse/password-hashing/blob/master/PasswordStorage.java
 * </p>
 * @version 1.0
 * @history 1.0-->初始化，增加加盐Hash以及密码验证的方法
 * @update 2016/7/7 13:12.
 * Created by 玄玉<https://jadyer.github.io/> on 2016/7/7 9:37.
 */
public final class PasswordUtil {
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    //These constants may be changed without breaking existing hashes.
    private static final int SALT_BYTE_SIZE = 24;
    private static final int HASH_BYTE_SIZE = 24;
    private static final int PBKDF2_ITERATIONS = 1000;

    //These constants define the encoding and may not be changed.
    private static final int HASH_SECTIONS = 3;
    private static final int INDEX_ALGORITHM = 0;
    private static final int INDEX_SALT = 1;
    private static final int INDEX_PBKDF2 = 2;

    /**
     * Returns a salted PBKDF2 hash of the password.
     * @param password the password to hash
     * @return 返回加了随机盐的PBKDF2，返回格式为<code>algorithm:iterations:salt:hash</code>
     */
    public static String createHash(String password){
        //Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);
        //Hash the password
        //return createHash(password, new String(salt));
        String hash = createHash(password, new String(salt));
        //format: algorithm:iterations:salt:hash
        String[] parts = new String[HASH_SECTIONS];
        parts[INDEX_ALGORITHM] = "sha1";
        parts[INDEX_SALT]      = Hex.encodeHexString(salt);
        parts[INDEX_PBKDF2]    = hash;
        String result = "";
        for(String obj : parts){
            result = result + ":" + obj;
        }
        return result.substring(1);
    }


    /**
     * Returns a salted PBKDF2 hash of the password.
     * @param password the password to hash
     * @param salt     the salt
     * @return a salted PBKDF2 hash of the password
     */
    public static String createHash(String password, String salt){
        // Hash the password
        byte[] hash = pbkdf2(password.toCharArray(), salt.getBytes(), PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
        // 以16进制形式返回加盐Hash的结果
        return Hex.encodeHexString(hash);
    }


    /**
     * Verify a password using a hash.
     * @param password    the password to check
     * @param correctHash the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public static boolean verifyPassword(String password, String correctHash){
        // Decode the hash into its parameters
        String[] params = correctHash.split(":");
        if(HASH_SECTIONS != params.length){
            LogUtil.getLogger().warn("Fields are missing from the password hash.");
            return false;
        }
        //Currently, Java only supports SHA1.
        if(!"sha1".equals(params[INDEX_ALGORITHM])){
            LogUtil.getLogger().warn("Unsupported hash type.");
            return false;
        }
        byte[] salt;
        try {
            salt = Hex.decodeHex(params[INDEX_SALT].toCharArray());
        } catch (DecoderException e) {
            throw new RuntimeException("还原Salt失败", e);
        }
        return verifyPassword(password, new String(salt), params[INDEX_PBKDF2]);
    }


    /**
     * Verify a password using a hash.
     * @param password    the password to check
     * @param salt        the salt
     * @param correctHash the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public static boolean verifyPassword(String password, String salt, String correctHash){
        byte[] hash;
        try {
            hash = Hex.decodeHex(correctHash.toCharArray());
        } catch (DecoderException e) {
            throw new RuntimeException("还原Hash失败", e);
        }
        // Compute the hash of the provided password, using the same salt, iteration count, and hash length
        byte[] testHash = pbkdf2(password.toCharArray(), salt.getBytes(), PBKDF2_ITERATIONS, hash.length);
        // Compare the hashes in constant time. The password is correct if both hashes match.
        return slowEquals(hash, testHash);
    }


    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     * @param a the first byte array
     * @param b the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(byte[] a, byte[] b){
        int diff = a.length ^ b.length;
        for(int i=0; i<a.length && i<b.length; i++){
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }


    /**
     * Computes the PBKDF2 hash of a password.
     * @param password   the password to hash.
     * @param salt       the salt
     * @param iterations the iteration count (slowness factor)
     * @param bytes      the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes){
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("加盐Hash失败", e);
        }
    }
}