/**
 * 目前设计存储于内存，但实际价值不高，其不适用多实例
 * 所以：要么直接存Redis，要么按照如下思路（类似于jfinal-weixin）
public interface ITokenCache {
    String get(String key);
    void set(String key, String accesstoken);
}
public class RedisTokenCache implements ITokenCache {
    private final String ACCESS_TOKEN_PREFIX = "mpp:weixin:accesstoken:";
    private JedisCluster jedisCluster;
    public RedisTokenCache(JedisCluster jedisCluster){
        this.jedisCluster = jedisCluster;
    }
    @Override
    public String get(String key) {
        return this.jedisCluster.get(ACCESS_TOKEN_PREFIX.concat(key));
    }
    @Override
    public void set(String key, String accesstoken) {
        //也可以定义一个AccessToken的实体类，存放accesstoken的几个属性，然后转成json存到redis
        this.jedisCluster.setex(ACCESS_TOKEN_PREFIX.concat(key), 7000 * 1000, accesstoken);
    }
}
public class MemoryTokenCache implements ITokenCache {
    private static final String FLAG_WEIXIN_ACCESSTOKEN = "weixin_access_token_";
    private static final String FLAG_WEIXIN_JSAPI_TICKET = "weixin_jsapi_ticket_";
    private static final String FLAG_WEIXIN_ACCESSTOKEN_EXPIRETIME = FLAG_WEIXIN_ACCESSTOKEN + "expire_time_";
    private static final String FLAG_WEIXIN_JSAPI_TICKET_EXPIRETIME = FLAG_WEIXIN_JSAPI_TICKET + "expire_time_";
    private static ConcurrentHashMap<String, Object> tokenMap = new ConcurrentHashMap<>();
    @Override
    public String get(String key) {
        return this.tokenMap.get(ACCESS_TOKEN_PREFIX.concat(key));
    }
    @Override
    public void set(String key, String accesstoken) {
        this.tokenMap.put(ACCESS_TOKEN_PREFIX.concat(key), 7000 * 1000, accesstoken);
    }
}
public class TokenHolder {
    private static ITokenCache tokenCache = new MemoryTokenCache();
    public static void setTokenCache(ITokenCache tokenCache) {
        TokenHolder.tokenCache = tokenCache;
    }
    public static ITokenCache getTokenCache() {
        return TokenHolder.tokenCache;
    }
}
使用前TokenHolder.setTokenCache(new RedisTokenCache(jedisCluster))
然后再ITokenCache cache = TokenHolder.getTokenCache()
接下来就可以cache.set()或者get()数据了
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/1 11:46.
 */
package com.jadyer.seed.mpp.sdk.weixin.helper;