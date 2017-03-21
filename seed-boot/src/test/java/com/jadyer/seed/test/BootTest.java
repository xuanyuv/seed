package com.jadyer.seed.test;

import com.jadyer.seed.boot.BootStrap;
import com.jadyer.seed.boot.ratelimiter.RateLimiterLua;
import com.jadyer.seed.boot.ratelimiter.RateLimiterLuaV2;
import org.apache.commons.lang3.RandomStringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 演示spring-boot-starter-test的几个用法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=BootStrap.class)
public class BootTest {
	@Resource
	private JedisCluster jedisCluster;
	@Resource
	private RateLimiterLuaV2 rateLimiterLuaV2;

	/**
	 * Jasypt加解密屬性文件
	 */
	@Test
	public void jasyptTest(){
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword("https://jadyer.github.io/");
		config.setAlgorithm("PBEWithMD5AndDES");
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("hexadecimal");
		encryptor.setConfig(config);
		String encryptStr = encryptor.encrypt("xuanyu");
		System.out.println("encryptStr=" + encryptStr);
		String decryptStr = encryptor.decrypt(encryptStr);
		Assert.assertEquals("xuanyu", decryptStr);
	}


	/**
	 * 测试redis3.x的几个操作
	 */
	@Test
	public void redisTest(){
		String redisKey = RandomStringUtils.randomNumeric(6);
		Map<String, String> hash = new HashMap<>();
		hash.put("测试key01", "这是有效期为12分钟的内容01");
		hash.put("测试key02", "这是有效期为12分钟的内容02");
		jedisCluster.hmset(redisKey, hash);
		jedisCluster.expire(redisKey, 60 * 12);
		Map<String, String> values = jedisCluster.hgetAll(redisKey);
		for(Map.Entry<String, String> entry : values.entrySet()){
			System.out.println(entry.getKey() + "-->" + entry.getValue());
		}
		System.out.println("-------------------");
		hash.put("测试key01", "这是有效期为12分钟的内容11");
		hash.put("测试key02", "这是有效期为12分钟的内容22");
		jedisCluster.hmset(redisKey, hash);
		values = jedisCluster.hgetAll(redisKey);
		for(Map.Entry<String, String> entry : values.entrySet()){
			System.out.println(entry.getKey() + "-->" + entry.getValue());
		}
	}


	/**
	 * 通过lua脚步，根据pattern，获取Redis中的所有key
	 */
	@Test
	public void redisGetallkey(){
		System.out.println(this.jedisCluster.eval("return redis.call('keys', ARGV[1])", 0, "youqian*"));
	}


	/**
	 * 限流測試
	 */
	@Test
	public void ratelimiterTest() throws InterruptedException {
		String identity = "msxf.apply.get";
		RateLimiterLua rateLimiterLua = new RateLimiterLua(jedisCluster, 3, 10000);
		for(int i=0; i<3; i++){
			System.out.println(rateLimiterLua.access(identity));
		}
		//三個循環之後，Token已用完，此時獲取到的就是false
		System.out.println(rateLimiterLua.access(identity));
		//平均3.3s才會加一個Token，所以這裡睡眠小於3.3s的，下一次獲取到的都是false
		TimeUnit.SECONDS.sleep(3);
		System.out.println(rateLimiterLua.access(identity));
		//删除本次测试生成的数据
		jedisCluster.del("rate:limiter:" + identity);
	}


	/**
	 * 限流測試
	 */
	@Test
	public void ratelimiterV2Test() throws InterruptedException {
		String identity = "msxf.apply.submit";
		rateLimiterLuaV2.setLimitRule(identity, 3, 10000);
		for(int i=0; i<3; i++){
			System.out.println(rateLimiterLuaV2.access(identity));
		}
		//三个循环之后，Token已用完，此时获取到的就是false
		System.out.println(rateLimiterLuaV2.access(identity));
		//平均3.3s才会加一个Token，所以这里睡眠小于3.3s的，下一次获取到的都是false
		TimeUnit.SECONDS.sleep(4);
		System.out.println(rateLimiterLuaV2.access(identity));
		//删除本次测试生成的数据
		jedisCluster.del("rate:limiter:" + identity);
		jedisCluster.del("rate:limiter:rule:" + identity);
	}
}