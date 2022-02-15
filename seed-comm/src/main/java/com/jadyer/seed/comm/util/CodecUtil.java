package com.jadyer.seed.comm.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * 加解密工具类
 * -----------------------------------------------------------------------------------------------------------
 * 这里关于DESede和DES的加解密代码,其实完全可以和AES加解密的代码一样
 * 只是在AES加解密代码里把相应算法改下(经测试是可行的,加解密均成功),之所以没这么做,也是考虑多了解一点加解密写法
 * -----------------------------------------------------------------------------------------------------------
 * Java中每个数据都有一个摘要,即数据指纹..无论这个数据有多大,它的指纹都是固定的128位,即16个字节
 * 我们可以使用Java中提供的java.security.MessageDigest工具类,得到随机数的数据摘要,即数据指纹
 * -----------------------------------------------------------------------------------------------------------
 * 全新算法：Base64编码
 * 任何数据经Base64算法编码后,都会返回明文的字符串..该算法有一个特点：它会把每三个字节,都变成四个字节
 * 比如00110010.11001101.00101001会被变成00001100.00101100.00110100.00101001
 * 也就是把原来的24bit平均分为四份,然后在每一份前面补两个零,以此凑成32bit,即四个字节
 * 改变之后的四个字节,每个字节的最小值就是00000000,最大值就是00111111,即最小为零,最大为63
 * 所以,经过Base64算法编码后,每个字节的最大值都不会超过64
 * 最后,Base64算法会查询它自己定制的码表,该码表记录的是0--63所对应键盘上的明文字符,最后将其返回
 * -----------------------------------------------------------------------------------------------------------
 * RSA密钥对
 * 可以在线生产密钥对：http://web.chacuo.net/netrsakeypair
 * 可以使用openssl工具来生成密钥对：https://cshall.alipay.com/support/help_detail.htm?help_id=397433
 * 这两种在线生成的RSA密钥对，本工具类的<code>rsa**()</code>方法都是支持的
 * 另外，AES与RSA结合加密可参考：https://wustrive2008.github.io/2015/08/21/开放接口的安全验证方案(AES+RSA)/
 * 公钥加密,私钥解密：可以保证信息只有我能收到（所有人都能看到密文，但只有我能解密看到明文）
 * 私钥签名,公钥验签：可以保证信息是我发出去的（所有人都能看到签名，但只有我签的才能被接收方验证通过）
 * -----------------------------------------------------------------------------------------------------------
 * RSA明文长度
 * RSA规定允许加密的最大明文字节数等于密钥长度值除以8再减去11,但签名时无此限制
 * 这个密钥长度值就是我们在生成密钥时,要求指定最少512bit的加密长度,比如<code>initRSAKey(int)</code>指定的参数
 * 比如1024bit密钥最多能加密117个字节的明文,UTF-8编码下每个汉字为3个字节,所以UTF-8编码的明文最多允许39个汉字
 * 同理2048bit密钥最多能加密245个字节的明文,UTF-8编码下即81个汉字和2个字母或其它符号
 * 以上描述的117和245均经过亲测,当超过RSA规定的117或245时会报告下面的异常
 * Caused by: javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes
 * Caused by: javax.crypto.IllegalBlockSizeException: Data must not be longer than 245 bytes
 * -----------------------------------------------------------------------------------------------------------
 * RSA密文长度
 * RSA规定允许解密的最大密文字节数等于密钥长度值除以8,但签名时无此限制
 * 比如1024bit密钥最多能解密128个字节的密文,同理2048bit密钥最多能加密256个字节的明文
 * 可以通过org.apache.commons.codec.binary.Base64.decodeBase64(data).length得到密文字节数
 * 不能直接data.getBytes().length原因是data本身是经过Base64编码后的字符串,所以要Base64解码才能得到真正的字节数组
 * 以上描述的128和256均经过亲测,当超过RSA规定的128或256时会报告下面的异常
 * Caused by: javax.crypto.IllegalBlockSizeException: Data must not be longer than 128 bytes
 * Caused by: javax.crypto.IllegalBlockSizeException: Data must not be longer than 256 bytes
 * -----------------------------------------------------------------------------------------------------------
 * @version v1.10
 * @history v1.10-->增加CRC32()
 * @history v1.9-->修改RSA、AES、DESede、DES算法的方法名，使之简洁易懂
 * @history v1.8-->修复buildAESPKCS7Decrypt()方法没有初始化BouncyCastleProvider的BUG
 * @history v1.7-->细化各方法参数注释，使之描述更清晰
 * @history v1.6-->RSA算法加解密方法增加分段加解密功能，理论上可加解密任意长度的明文或密文
 * @history v1.5-->增加RSA算法加解密及签名验签的方法
 * @history v1.4-->增加AES-PKCS7算法加解密数据的方法
 * @history v1.3-->增加buildHMacSign()的签名方法，目前支持<code>HMacSHA1,HMacSHA256,HMacSHA512,HMacMD5</code>算法
 * @history v1.2-->修改buildHexSign()方法，取消用于置顶返回字符串大小写的第四个参数，修改后默认返回大写字符串
 * @history v1.1-->增加AES,DES,DESede等算法的加解密方法
 * @history v1.0-->新增buildHexSign()的签名方法，目前支持<code>MD5,SHA,SHA1,SHA-1,SHA-256,SHA-384,SHA-512</code>算法
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2013/10/06 00:00.
 */
public final class CodecUtil {
    //密钥算法
    public static final String ALGORITHM_RSA = "RSA";
    public static final String ALGORITHM_RSA_SIGN = "SHA256WithRSA";
    public static final int ALGORITHM_RSA_PRIVATE_KEY_LENGTH = 2048;
    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_AES_PKCS7 = "AES";
    public static final String ALGORITHM_DES = "DES";
    public static final String ALGORITHM_DES_EDE = "DESede";
    //加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
    //工作模式有ECB--电子密码本模式,CBC--加密分组链接模式,CFB--加密反馈模式,OFB--输出反馈模式,CTR--计数器模式
    //其中ECB过于简单而不安全,已被弃用,相对的CBC模式是最安全的,http://www.moye.me/2015/06/14/cryptography_rsa/
    private static final String ALGORITHM_CIPHER_AES = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM_CIPHER_AES_PKCS7 = "AES/CBC/PKCS7Padding";
    private static final String ALGORITHM_CIPHER_DES = "DES/ECB/PKCS5Padding";
    private static final String ALGORITHM_CIPHER_DES_EDE = "DESede/ECB/PKCS5Padding";

    private CodecUtil(){}

    /**
     * 生成AES/CBC/PKCS7Padding专用的IV
     * ECB模式只用密钥即可对数据进行加解密，CBC模式需要添加一个参数IV
     * IV是一个16字节的数组，这里采用和IOS一样的构造方法，数据全为0
     */
    private static AlgorithmParameters initIV(){
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte)0x00);
        AlgorithmParameters params;
        try {
            params = AlgorithmParameters.getInstance(ALGORITHM_AES_PKCS7);
            params.init(new IvParameterSpec(iv));
        } catch (Exception e) {
            throw new IllegalArgumentException("生成"+ALGORITHM_CIPHER_AES_PKCS7+"专用的IV时失败", e);
        }
        return params;
    }


    /**
     * 初始化算法密钥
     * 目前algorithm参数可选值为AES,DES,DESede,输入其它值时会抛异常
     * 若系统无法识别algorithm会导致实例化密钥生成器失败,也会抛异常
     * @param algorithm      指定生成哪种算法的密钥
     * @param isPKCS7Padding 是否采用PKCS7Padding填充方式(需要BouncyCastle支持)
     * @return 经过Base64编码后的密钥字符串,对于AES-PKCS7Padding算法则返回16进制表示的密钥字符串
     */
    public static String initKey(String algorithm, boolean isPKCS7Padding){
        if(isPKCS7Padding){
            Security.addProvider(new BouncyCastleProvider());
        }
        //实例化密钥生成器
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + algorithm + "]");
        }
        //初始化密钥生成器：AES要求密钥长度为128,192,256位
        if(ALGORITHM_AES.equals(algorithm)/* || ALGORITHM_AES_PKCS7.equals(algorithm)*/){
            kg.init(128);
        }else if(ALGORITHM_DES.equals(algorithm)){
            kg.init(56);
        }else if(ALGORITHM_DES_EDE.equals(algorithm)){
            kg.init(168);
        }else{
            throw new IllegalArgumentException("Not supported algorithm-->[" + algorithm + "]");
        }
        //生成密钥
        SecretKey secretKey = kg.generateKey();
        //获取二进制密钥编码形式
        if(isPKCS7Padding){
            return Hex.encodeHexString(secretKey.getEncoded());
        }
        return Base64.encodeBase64URLSafeString(secretKey.getEncoded());
    }


    /**
     * 初始化RSA算法密钥对
     * @param keysize RSA1024已经不安全了，建议2048
     * @return 经过Base64编码后的公私钥Map，键名分别为publicKey和privateKey
     */
    public static Map<String, String> initRSAKey(int keysize){
        if(keysize != ALGORITHM_RSA_PRIVATE_KEY_LENGTH){
            throw new IllegalArgumentException("RSA1024已经不安全了，请使用"+ALGORITHM_RSA_PRIVATE_KEY_LENGTH+"初始化RSA密钥对");
        }
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + ALGORITHM_RSA + "]");
        }
        //初始化KeyPairGenerator对象,不要被initialize()源码表面上欺骗,其实这里声明的size是生效的
        kpg.initialize(ALGORITHM_RSA_PRIVATE_KEY_LENGTH);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        return keyPairMap;
    }


    /**
     * RSA算法分段加解密数据
     * @param cipher 初始化了加解密工作模式后的javax.crypto.Cipher对象
     * @param opmode 加解密模式，值为javax.crypto.Cipher.ENCRYPT_MODE/DECRYPT_MODE
     * @param data   待分段加解密的数据的字节数组
     * @return 加密或解密后得到的数据的字节数组
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas){
        int maxBlock;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8;
        }else{
            maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8 - 11;
        }
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            int offSet = 0;
            byte[] buff;
            int i = 0;
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            return out.toByteArray();
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
    }


    /**
     * RSA算法公钥加密数据
     * @param data 待加密的明文字符串
     * @param key  RSA公钥字符串
     * @return RSA公钥加密后的经过Base64编码的密文字符串
     */
    public static String rsaEncrypt(String data, String key){
        try{
            //通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            //encrypt
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(CHARSET)));
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * RSA算法私钥解密数据
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥解密后的明文字符串
     */
    public static String rsaDecrypt(String data, String key){
        try{
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //decrypt
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //return new String(cipher.doFinal(Base64.decodeBase64(data)), CHARSET);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data)), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * RSA算法私钥签名数据
     * 签名算法SHA1WithRSA已被废弃，推荐使用SHA256WithRSA
     * @param data 待签名的明文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥签名后的经过Base64编码的字符串
     */
    public static String rsaSign(String data, String key){
        try{
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //sign
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64URLSafeString(signature.sign());
        }catch(Exception e){
            throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * RSA算法公钥验签数据
     * @param data 参与签名的明文字符串
     * @param key  RSA公钥字符串
     * @param sign RSA签名得到的经过Base64编码的字符串
     * @return true--验签通过,false--验签未通过
     */
    public static boolean rsaVerify(String data, String key, String sign){
        try{
            //通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
            //verify
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decodeBase64(sign));
        }catch(Exception e){
            throw new RuntimeException("验签字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES算法加密数据
     * @param data 待加密的明文数据
     * @param key  AES密钥字符串
     * @return AES加密后的经过Base64编码的密文字符串,加密过程中遇到异常则抛出RuntimeException
     * */
    public static String aesEncrypt(String data, String key){
        try{
            //实例化Cipher对象,它用于完成实际的加密操作
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES);
            //还原密钥,并初始化Cipher对象,设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM_AES));
            //执行加密操作,加密后的结果通常都会用Base64编码进行传输
            //将Base64中的URL非法字符如'+','/','='转为其他字符,详见RFC3548
            return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES算法解密数据
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  AES密钥字符串
     * @return AES解密后的明文字符串,解密过程中遇到异常则抛出RuntimeException
     * */
    public static String aesDecrypt(String data, String key){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM_AES));
            return new String(cipher.doFinal(Base64.decodeBase64(data)), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES-PKCS7算法加密数据-IV版
     * -------------------------------------------------------------------------------------------
     * IV与非IV版区别在于：前端项目（vue3.0）的加解密包要求传偏移量，且是PKCS7Padding的，故编写此方法
     * 另外，本法中用到的密钥和偏移量，随机生成固定长度为16的字符串即可，不需要用initKey()方法来生成
     * -------------------------------------------------------------------------------------------
     * 前端项目（vue3.0）的加解密包，要求传入的密钥和偏移量，是十六进制的字符串，如下所示
     * const key = CryptoJS.enc.Utf8.parse("64326533316638663632633834643366");
     * 对应java的就是：Hex.encodeHexString("d2e31f8f62c84d3f".getBytes("UTF-8"));
     * -------------------------------------------------------------------------------------------
     * @param data 待加密的明文字符串
     * @param key  密钥，固定长度为16的字符串（字母数字混合均可）
     * @param iv   偏移量，固定长度为16的字符串（字母数字混合均可）
     * @return AES-PKCS7加密后的16进制表示的密文字符串，加密过程中遇到异常则抛出RuntimeException
     */
    public static String aesPKCS7Encrypt(String data, String key, String iv){
        try{
            Cipher cipher = aesPKCS7(key, iv, Cipher.ENCRYPT_MODE);
            return Hex.encodeHexString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES-PKCS7算法解密数据-IV版
     * @param data 待解密的16进制表示的密文字符串
     * @param key  密钥，固定长度为16的字符串（字母数字混合均可）
     * @param iv   偏移量，固定长度为16的字符串（字母数字混合均可）
     * @return AES-PKCS7解密后的明文字符串，解密过程中遇到异常则抛出RuntimeException
     */
    public static String aesPKCS7Decrypt(String data, String key, String iv){
        try {
            Cipher cipher = aesPKCS7(key, iv, Cipher.DECRYPT_MODE);
            return new String(cipher.doFinal(Hex.decodeHex(data.toCharArray())), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES-PKCS7算法解密数据-IV版-专用
     * @param key  密钥，固定长度为16的字符串（字母数字混合均可）
     * @param iv   偏移量，固定长度为16的字符串（字母数字混合均可）
     * @param mode 加解密模式，仅支持两个值：Cipher.ENCRYPT_MODE/DECRYPT_MODE
     * Comment by 玄玉<https://jadyer.cn/> on 2022/2/15 16:10.
     */
    private static Cipher aesPKCS7(String key, String iv, int mode) throws Exception {
        if(StringUtils.length(key)!=16 || StringUtils.length(iv)!=16){
            throw new RuntimeException("密钥或偏移量应为固定长度为16的字符串");
        }
        if(mode!=Cipher.ENCRYPT_MODE && mode!=Cipher.DECRYPT_MODE){
            throw new RuntimeException("加解密模式仅支持 " + Cipher.ENCRYPT_MODE + " 或 " + Cipher.DECRYPT_MODE);
        }
        Security.addProvider(new BouncyCastleProvider());
        Key secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM_AES_PKCS7);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES_PKCS7, "BC");
        cipher.init(mode, secretKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
        return cipher;
    }


    /**
     * AES-PKCS7算法加密数据
     * 兼容IOS中的SecKeyWrapper加解密（SecKeyWrapper采用的是PKCS7Padding填充方式）
     * @param data 待加密的明文字符串
     * @param key  AES密钥字符串
     * @return AES-PKCS7加密后的16进制表示的密文字符串，加密过程中遇到异常则抛出RuntimeException
     */
    public static String aesPKCS7Encrypt(String data, String key){
        Security.addProvider(new BouncyCastleProvider());
        try{
            SecretKey secretKey = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), ALGORITHM_AES_PKCS7);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, initIV());
            return Hex.encodeHexString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES-PKCS7算法解密数据
     * 兼容IOS中的SecKeyWrapper加解密（SecKeyWrapper采用的是PKCS7Padding填充方式）
     * @param data 待解密的16进制表示的密文字符串
     * @param key  AES密钥字符串
     * @return AES-PKCS7解密后的明文字符串，解密过程中遇到异常则抛出RuntimeException
     */
    public static String aesPKCS7Decrypt(String data, String key){
        Security.addProvider(new BouncyCastleProvider());
        try {
            SecretKey secretKey = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), ALGORITHM_AES_PKCS7);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES_PKCS7);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initIV());
            return new String(cipher.doFinal(Hex.decodeHex(data.toCharArray())), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * DES算法加密数据
     * @param data 待加密的明文字符串
     * @param key  密钥
     * @return 加密后的经过Base64编码的密文字符串,加密过程中遇到异常则抛出RuntimeException
     * */
    public static String desEncrypt(String data, String key){
        try{
            DESKeySpec dks = new DESKeySpec(Base64.decodeBase64(key));
            SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM_DES).generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_DES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * DES算法解密数据
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  密钥
     * @return 解密后的明文字符串,解密过程中遇到异常则抛出RuntimeException
     * */
    public static String desDecrypt(String data, String key){
        try {
            DESKeySpec dks = new DESKeySpec(Base64.decodeBase64(key));
            SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM_DES).generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_DES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(data)), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * DESede算法加密数据
     * @param data 待加密的明文字符串
     * @param key  密钥
     * @return 加密后的经过Base64编码的密文字符串,加密过程中遇到异常则抛出RuntimeException
     * */
    public static String desedeEncrypt(String data, String key){
        try{
            DESedeKeySpec dks = new DESedeKeySpec(Base64.decodeBase64(key));
            SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM_DES_EDE).generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_DES_EDE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * DESede算法解密数据
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  密钥
     * @return 解密后的明文字符串,解密过程中遇到异常则抛出RuntimeException
     * */
    public static String desedeDecrypt(String data, String key){
        try {
            DESedeKeySpec dks = new DESedeKeySpec(Base64.decodeBase64(key));
            SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM_DES_EDE).generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_DES_EDE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(data)), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * Hmac签名
     * 方法内部首先会过滤Map<String,String>参数中的部分键值对
     * 过滤规则为移除键名为cert、hmac、sign、signMsg及键值为null或键值长度为零的键值对
     * 过滤后会产生一个字符串，其格式为按照键名升序排序的key11=value11&key22=value22&key=signKey
     * 最后调用 {@link #buildHmacSign(String, String, String)} 签名，返回签名后的小写的十六进制字符串
     * @param param     待签名的Map<String,String>
     * @param key       签名用到的密钥字符串
     * @param algorithm 可传：HmacMD5、HmacSHA1、HmacSHA256、HmacSHA512
     * @return String algorithm digest as a lowerCase hex string
     */
    public static String buildHmacSign(Map<String, String> param, String key, String algorithm){
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(param.keySet());
        Collections.sort(keys);
        for(String obj : keys){
            String value = param.get(obj);
            if(StringUtils.equalsAnyIgnoreCase(obj, "cert", "hmac", "sign", "signMsg") || StringUtils.isEmpty(value)){
                continue;
            }
            sb.append(obj).append("=").append(value).append("&");
        }
        sb.append("key=").append(key);
        return buildHmacSign(sb.toString(), key, algorithm);
    }


    /**
     * Hmac签名
     * <p>
     *     2016-02-20 21:21 HmacMD5和HmacSHA1已经是不安全的了，不推荐使用
     * </p>
     * @param data      待签名的明文字符串
     * @param key       签名用到的密钥字符串
     * @param algorithm 可传：HmacMD5、HmacSHA1、HmacSHA256、HmacSHA512
     * @return String algorithm digest as a lowerCase hex string
     */
    public static String buildHmacSign(String data, String key, String algorithm){
        if("HmacMD5".equals(algorithm) || "HmacSHA1".equals(algorithm)){
            LogUtil.getLogger().warn("HmacMD5和HmacSHA1已经是不安全的了，不推荐使用");
        }
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("签名字符串[" + data + "]时发生异常：Invalid key-->[" + key + "]");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("签名字符串[" + data + "]时发生异常：No Such Algorithm-->[" + algorithm + "]");
        }
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        return Hex.encodeHexString(mac.doFinal(dataBytes));
    }


    /**
     * 根据指定的签名密钥和算法签名Map<String,String>
     * 方法内部首先会过滤Map<String,String>参数中的部分键值对
     * 过滤规则为移除键名为cert、hmac、sign、signMsg及键值为null或键值长度为零的键值对
     * 过滤后会产生一个字符串，其格式为按照键名升序排序的key11=value11&key22=value22&key=signKey
     * 最后调用 {@link #buildHexSign(String, String, String)} 签名，返回签名后的小写的十六进制字符串
     * @param param     待签名的Map<String,String>
     * @param charset   签名时转码用到的字符集
     * @param algorithm 可传：MD5、SHA、SHA1、SHA-1、SHA-256、SHA-384、SHA-512
     * @param signKey   签名用到的密钥
     * @return String algorithm digest as a lowerCase hex string
     */
    public static String buildHexSign(Map<String, String> param, String charset, String algorithm, String signKey){
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(param.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            String value = param.get(key);
            if(StringUtils.equalsAnyIgnoreCase(key, "cert", "hmac", "sign", "signMsg") || StringUtils.isEmpty(value)){
                continue;
            }
            sb.append(key).append("=").append(value).append("&");
        }
        sb.append("key=").append(signKey);
        return buildHexSign(sb.toString(), charset, algorithm);
    }


    /**
     * 通过指定算法签名字符串
     * <p>
     *     buildHexSign(data, "UTF-8", "MD5") == {@link org.apache.commons.codec.digest.DigestUtils#md5Hex(String)}
     * </p>
     * @param data        待签名数据
     * @param charset     字符串转码为byte[]时使用的字符集
     * @param algorithm   可传：MD5、SHA、SHA1、SHA-1、SHA-256、SHA-384、SHA-512
     * @return String algorithm digest as a lowerCase hex string
     */
    public static String buildHexSign(String data, String charset, String algorithm){
        LogUtil.getLogger().info("待签名字符串为-->[{}]", data);
        byte[] dataBytes;
        try {
            dataBytes = data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("将字符串[" + data + "]转为byte[]时发生异常：Unsupported Encoding-->[" + charset + "]");
        }
        byte[] algorithmData;
        try {
            //get an algorithm digest instance
            algorithmData = MessageDigest.getInstance(algorithm).digest(dataBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("签名字符串[" + data + "]时发生异常：No Such Algorithm-->[" + algorithm + "]");
        }
        char[] respData = new char[algorithmData.length << 1];
        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        //two characters form the hex value
        for(int i=0,j=0; i<algorithmData.length; i++){
            respData[j++] = digits[(0xF0 & algorithmData[i]) >>> 4];
            respData[j++] = digits[0x0F & algorithmData[i]];
        }
        String sign = new String(respData);
        LogUtil.getLogger().info("生成的签名值为-->[{}]", sign);
        return sign;
    }


    /**
     * 生成CRC32码
     * -------------------------------------------------------------------------------------------------
     * CRC32存在冲突的概率，比如以下 4 对字符串的CRC32就冲突了
     * htpqwklvynl = pojqhnulnxu
     * rcoadjtslsa = gcpshlifrsp
     * hydybfnyrpt = vtcdkivpjyt
     * cmudwvdvmnb = svurvjgsmog
     * 如果冲突了，那就只有改动一下原字符串（比如尾部添加特殊标记）重新生成了
     * -------------------------------------------------------------------------------------------------
     * @return 返回一个长度为10的数字
     * Comment by 玄玉<https://jadyer.cn/> on 2021/7/31 14:27.
     */
    public static long crc32(String data){
        // CRC32 crc32 = new CRC32();
        // crc32.update(data.getBytes());
        // return crc32.getValue();
        CRC32 crc32 = new CRC32();
        crc32.update(data.getBytes());
        StringBuilder sb = new StringBuilder(String.valueOf(crc32.getValue()));
        if(sb.length() < 10){
            for(int i=0,len=10-sb.length(); i<len; i++){
                sb.append("0");
            }
        }
        if(sb.length() > 10){
            for(int i=0,len=sb.length()-10; i<len; i++){
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return Long.parseLong(sb.toString());
    }
}