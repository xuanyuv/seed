package com.jadyer.seed.comm;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * JedisPool工具类
 * ------------------------------------------------------------------------------------------------------------------
 * 由于@Resource注解不能注入一个static变量，故通过让它注入非static的setter方法的方式，把jedisPool对象赋给静态对象
 * ------------------------------------------------------------------------------------------------------------------
 * 本工具类的方法只是对JedisCommands原方法的一种包装，未修改原方法的入参出参及方法名
 * 用法举例：String name = JedisPoolUtil.get("Seed:Data:name")
 * ------------------------------------------------------------------------------------------------------------------
 * 注：使用时无需注入JedisPoolUtil，直接调用静态方法即可，但要保证JedisPoolUtil能被Spring扫描到
 * ------------------------------------------------------------------------------------------------------------------
 * @version v1.0
 * @history v1.0-->增加若干JedisCommands原方法的包装
 * Created by 玄玉<https://jadyer.cn/> on 2021/5/7 22:02.
 */
@Component
public final class JedisPoolHelper {
    @Resource
    public void setJedisPool(JedisPool jedisPool) {
        JedisPoolHelper.jedisPool = jedisPool;
    }
    private static JedisPool jedisPool;

    private JedisPoolHelper(){}

    public static String set(byte[] key, byte[] value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    public static String set(String key, String value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    public static String set(String key, String value, String nxxx, String expx, long time){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value, nxxx, expx, time);
        }
    }

    public static String set(String key, String value, String nxxx){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value, nxxx);
        }
    }

    public static String get(String key){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public static byte[] get(byte[] key){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public static Long expire(String key, int seconds){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, seconds);
        }
    }

    public static Long pexpire(String key, long milliseconds){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.pexpire(key, milliseconds);
        }
    }

    public static String getSet(String key, String value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getSet(key, value);
        }
    }

    public static Long setnx(String key, String value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setnx(key, value);
        }
    }

    public static String setex(String key, int seconds, String value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, seconds, value);
        }
    }

    public static String setex(byte[] key, int seconds, byte[] value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, seconds, value);
        }
    }

    public static Long incrBy(String key, long integer){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incrBy(key, integer);
        }
    }

    public static Long incr(String key){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        }
    }

    public static Long hset(String key, String field, String value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, field, value);
        }
    }

    public static String hget(String key, String field){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        }
    }

    public static Long hsetnx(String key, String field, String value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hsetnx(key, field, value);
        }
    }

    public static String hmset(String key, Map<String, String> hash){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hmset(key, hash);
        }
    }

    public static List<String> hmget(String key, String... fields){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hmget(key, fields);
        }
    }

    public static Map<String, String> hgetAll(String key){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    public static Long del(String key){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        }
    }

    public static Long publish(String channel, String message){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.publish(channel, message);
        }
    }

    public static void subscribe(JedisPubSub jedisPubSub, String... channels){
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(jedisPubSub, channels);
        }
    }
}