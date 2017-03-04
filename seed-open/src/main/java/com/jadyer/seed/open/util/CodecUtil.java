package com.jadyer.seed.open.util;

import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 加解密工具类
 * -----------------------------------------------------------------------------------------------------------
 * 完整版见https://github.com/jadyer/JadyerEngine/blob/master/JadyerEngine-common/src/main/java/com/jadyer/engine/common/util/CodecUtil.java
 * -----------------------------------------------------------------------------------------------------------
 * @version v1.5
 * @history v1.5-->加密和签名时增加UTF-8取字节数组
 * @history v1.4-->增加AES-PKCS7算法加解密数据的方法
 * @history v1.3-->增加buildHMacSign()的签名方法,目前支持<code>HMacSHA1,HMacSHA256,HMacSHA512,HMacMD5</code>算法
 * @history v1.2-->修改buildHexSign()方法,取消用于置顶返回字符串大小写的第四个参数,修改后默认返回大写字符串
 * @history v1.1-->增加AES,DES,DESede等算法的加解密方法
 * @history v1.0-->新增buildHexSign()的签名方法,目前支持<code>MD5,SHA,SHA1,SHA-1,SHA-256,SHA-384,SHA-512</code>算法
 * @update 2015-2-2 下午05:26:32
 * @create Oct 6, 2013 12:00:35 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public final class CodecUtil {
	private static final String CHARSET = "UTF-8";
	//密钥算法
	private static final String ALGORITHM_RSA = "RSA";
	private static final String ALGORITHM_RSA_SIGN = "SHA256WithRSA";
	private static final int ALGORITHM_RSA_PRIVATE_KEY_LENGTH = 2048;
	public static final String ALGORITHM_AES = "AES";
	private static final String ALGORITHM_AES_PKCS7 = "AES";
	private static final String ALGORITHM_DES = "DES";
	private static final String ALGORITHM_DESede = "DESede";
	//加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
	//工作模式有四种-->>ECB：电子密码本模式,CBC：加密分组链接模式,CFB：加密反馈模式,OFB：输出反馈模式
	private static final String ALGORITHM_CIPHER_AES = "AES/ECB/PKCS5Padding";
	private static final String ALGORITHM_CIPHER_AES_PKCS7 = "AES/CBC/PKCS7Padding";
	private static final String ALGORITHM_CIPHER_DES = "DES/ECB/PKCS5Padding";
	private static final String ALGORITHM_CIPHER_DESede = "DESede/ECB/PKCS5Padding";

	private CodecUtil(){}

	/**
	 * 初始化算法密钥
	 * @see 目前algorithm参数可选值为AES,DES,DESede,输入其它值时会返回<code>""</code>空字符串
	 * @see 若系统无法识别algorithm会导致实例化密钥生成器失败,此时也会返回<code>""</code>空字符串
	 * @param algorithm      指定生成哪种算法的密钥
	 * @param isPKCS7Padding 是否采用PKCS7Padding填充方式(需要BouncyCastle支持)
	 * @throws DecoderException 
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
			LogUtil.getAppLogger().error("实例化密钥生成器失败,系统不支持给定的[" + algorithm + "]算法,堆栈轨迹如下", e);
			return "";
		}
		//初始化密钥生成器:AES要求密钥长度为128,192,256位
		if(ALGORITHM_AES.equals(algorithm)){
			kg.init(128);
		}else if(ALGORITHM_AES_PKCS7.equals(algorithm)){
			kg.init(128);
		}else if(ALGORITHM_DES.equals(algorithm)){
			kg.init(56);
		}else if(ALGORITHM_DESede.equals(algorithm)){
			kg.init(168);
		}else{
			return "";
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
	 * @param keysize RSA1024已经不安全了,建议2048
	 * @return 经过Base64编码后的公私钥Map,键名分别为publicKey和privateKey
	 * @create Feb 20, 2016 7:34:41 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static Map<String, String> initRSAKey(int keysize){
		if(keysize != ALGORITHM_RSA_PRIVATE_KEY_LENGTH){
			throw new IllegalArgumentException("RSA1024已经不安全了,请使用"+ALGORITHM_RSA_PRIVATE_KEY_LENGTH+"初始化RSA密钥对");
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
	 * @param opmode 加解密模式,值为javax.crypto.Cipher.ENCRYPT_MODE/DECRYPT_MODE
	 * @param data   待分段加解密的数据的字节数组
	 * @return 加密或解密后得到的数据的字节数组
	 * @create Feb 21, 2016 1:37:21 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas){
		int maxBlock;
		if(opmode == Cipher.DECRYPT_MODE){
			maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8;
		}else{
			maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try{
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
		}catch(Exception e){
			throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
		}
		byte[] resultDatas = out.toByteArray();
		IOUtils.closeQuietly(out);
		return resultDatas;
	}


	/**
	 * RSA算法公钥加密数据
	 * @param data 待加密的明文字符串
	 * @param key  RSA公钥字符串
	 * @return RSA公钥加密后的经过Base64编码的密文字符串
	 * @create Feb 20, 2016 8:25:21 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static String buildRSAEncryptByPublicKey(String data, String key){
		try{
			//通过X509编码的Key指令获得公钥对象
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			Key publicKey = keyFactory.generatePublic(x509KeySpec);
			//encrypt
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			//return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(CHARSET)));
			return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET)));
		}catch(Exception e){
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}


	/**
	 * RSA算法私钥解密数据
	 * @param data 待解密的经过Base64编码的密文字符串
	 * @param key  RSA私钥字符串
	 * @return RSA私钥解密后的明文字符串
	 * @create Feb 20, 2016 8:33:22 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static String buildRSADecryptByPrivateKey(String data, String key){
		try{
			//通过PKCS#8编码的Key指令获得私钥对象
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			//decrypt
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			//return new String(cipher.doFinal(Base64.decodeBase64(data)), CHARSET);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data)), CHARSET);
		}catch(Exception e){
			throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
		}
	}


	/**
	 * RSA算法使用私钥对数据生成数字签名
	 * @see 注意签名算法SHA1WithRSA已被废弃,推荐使用SHA256WithRSA
	 * @param data 待签名的明文字符串
	 * @param key  RSA私钥字符串
	 * @return RSA私钥签名后的经过Base64编码的字符串
	 * @create Feb 20, 2016 8:43:49 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static String buildRSASignByPrivateKey(String data, String key){
		try{
			//通过PKCS#8编码的Key指令获得私钥对象
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			//sign
			Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
			signature.initSign(privateKey);
			signature.update(data.getBytes(CHARSET));
			return Base64.encodeBase64URLSafeString(signature.sign());
		}catch(Exception e){
			throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
		}
	}


	/**
	 * RSA算法使用公钥校验数字签名
	 * @param data 参与签名的明文字符串
	 * @param key  RSA公钥字符串
	 * @param sign RSA签名得到的经过Base64编码的字符串
	 * @return true--验签通过,false--验签未通过
	 * @create Feb 20, 2016 8:51:49 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static boolean buildRSAverifyByPublicKey(String data, String key, String sign){
		try{
			//通过X509编码的Key指令获得公钥对象
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
			//verify
			Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
			signature.initVerify(publicKey);
			signature.update(data.getBytes(CHARSET));
			return signature.verify(Base64.decodeBase64(sign));
		}catch(Exception e){
			throw new RuntimeException("验签字符串[" + data + "]时遇到异常", e);
		}
	}


	/**
	 * AES算法加密数据
	 * @param data 待加密数据
	 * @param key  密钥
	 * @return 加密后的数据,加密过程中遇到异常导致加密失败则返回<code>""</code>空字符串
	 * */
	public static String buildAESEncrypt(String data, String key){
		try{
			//实例化Cipher对象,它用于完成实际的加密操作
			Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES);
			//还原密钥,并初始化Cipher对象,设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM_AES));
			//执行加密操作,加密后的结果通常都会用Base64编码进行传输
			//将Base64中的URL非法字符如'+','/','='转为其他字符,详见RFC3548
			return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(CHARSET)));
		}catch(Exception e){
			LogUtil.getAppLogger().error("加密字符串[" + data + "]时遇到异常,堆栈轨迹如下", e);
			return "";
		}
	}


	/**
	 * AES算法解密数据 
	 * @param data 待解密数据
	 * @param key  密钥
	 * @return 解密后的数据,解密过程中遇到异常导致解密失败则返回<code>""</code>空字符串
	 * */
	public static String buildAESDecrypt(String data, String key){
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM_AES));
			return new String(cipher.doFinal(Base64.decodeBase64(data)), CHARSET);
		}catch(Exception e){
			LogUtil.getAppLogger().error("解密字符串[" + data + "]时遇到异常,堆栈轨迹如下", e);
			return "";
		}
	}


	/**
	 * Hmac签名
	 * @see Calculates the algorithm digest and returns the value as a hex string
	 * @see if system dosen't support this <code>algorithm</code>, return "" not null
	 * @param data      待签名数据
	 * @param key       签名用到的密钥
	 * @param algorithm 目前其有效值为<code>HmacSHA1,HmacSHA256,HmacSHA512,HmacMD5</code>
	 * @return String algorithm digest as a lowerCase hex string
	 * @create Nov 10, 2014 1:43:25 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static String buildHmacSign(String data, String key, String algorithm){
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
		Mac mac;
		try {
			mac = Mac.getInstance(algorithm);
			mac.init(secretKey);
		} catch (InvalidKeyException e) {
			LogUtil.getAppLogger().error("签名字符串[" + data + "]时发生异常:InvalidKey[" + key + "]");
			return "";
		} catch (NoSuchAlgorithmException e) {
			LogUtil.getAppLogger().error("签名字符串[" + data + "]时发生异常:System doesn't support this algorithm[" + algorithm + "]");
			return "";
		}
		return Hex.encodeHexString(mac.doFinal(OpenUtil.getBytes(data, CHARSET)));
	}
}