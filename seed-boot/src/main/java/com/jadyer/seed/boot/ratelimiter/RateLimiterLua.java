package com.jadyer.seed.boot.ratelimiter;

import redis.clients.jedis.JedisCluster;

/**
 * Redis+Lua結合TokenBucket算法實現的RateLimiter
 * <ul>
 *     <li>參考了以下網站</li>
 *     <li>http://jinnianshilongnian.iteye.com/blog/2305117</li>
 *     <li>https://zhuanlan.zhihu.com/p/20872901</li>
 *     <li>http://www.kissyu.org/2016/08/13/限流算法总结/</li>
 * </ul>
 * <ul>
 *     <li>需要注意不同節點間的操作</li>
 *     <li>https://www.v2ex.com/t/186712</li>
 *     <li>http://stackoverflow.com/questions/38234507/redis-cluster-update-keys-in-different-node-with-lua-script</li>
 * </ul>
 * Created by 玄玉<https://jadyer.cn/> on 2016/9/18 12:22.
 */
public final class RateLimiterLua {
    private JedisCluster jedisCluster;
    private final String luaScriptSHA1;
    /**
     * TokenBucket桶最大容量
     */
    private final long limit;
    /**
     * TokenBucket桶容量消耗的最少時間，也即限流的流量間隔的毫秒數
     * <p>可以理解為：intervalInMills時間段內，請求次數不能超過limit</p>
     */
    private final long intervalInMills;
    /**
     * TokenBucket桶填充Token的速率
     * <ul>
     *     <li>可以理解為：該速率指的是每多少時間段填充一個Token</li>
     *     <li>假設limit=3，intervalInMills=10000ms，此時intervalPerPermit=3333ms，即需要每3.3s往桶里填充一個Token</li>
     *     <li>所以若需要計算某段時間內應該填充的Token數，就應該用這個時間段的毫秒數除以該參數</li>
     *     <li>比如計算5s內這個時間段，需要填充的Token數，就是Math.floor(5000/3333=1.5)=1，即5s內需填充1個Token</li>
     * </ul>
     */
    private final long intervalPerPermit;

    public RateLimiterLua(JedisCluster jedisCluster, long limit, long intervalInMills){
        //腳本注釋見同包下的ratelimiter.lua
        String luaScript = "local key = KEYS[1]\n" +
                "local limit, interval, intervalPerPermit, refillTime = tonumber(ARGV[1]), tonumber(ARGV[2]), tonumber(ARGV[3]), tonumber(ARGV[4])\n" +
                "\n" +
                "local currentTokens\n" +
                "local bucket = redis.call('hgetall', key)\n" +
                "\n" +
                "if table.maxn(bucket) == 0 then\n" +
                "    currentTokens = limit\n" +
                "    redis.call('hset', key, 'lastRefillTime', refillTime)\n" +
                "elseif table.maxn(bucket) == 4 then\n" +
                "    local lastRefillTime, tokensRemaining = tonumber(bucket[2]), tonumber(bucket[4])\n" +
                "    if refillTime > lastRefillTime then\n" +
                "        local intervalSinceLast = refillTime - lastRefillTime\n" +
                "        if intervalSinceLast > interval then\n" +
                "            currentTokens = limit\n" +
                "            redis.call('hset', key, 'lastRefillTime', refillTime)\n" +
                "        else\n" +
                "            local grantedTokens = math.floor(intervalSinceLast / intervalPerPermit)\n" +
                "            if grantedTokens > 0 then\n" +
                "                local padMillis = math.fmod(intervalSinceLast, intervalPerPermit)\n" +
                "                redis.call('hset', key, 'lastRefillTime', refillTime - padMillis)\n" +
                "            end\n" +
                "            currentTokens = math.min(grantedTokens + tokensRemaining, limit)\n" +
                "        end\n" +
                "    else\n" +
                "        currentTokens = tokensRemaining\n" +
                "    end\n" +
                "else\n" +
                "    error(\"Size of bucket is \" .. table.maxn(bucket) .. \", Should Be 0 or 4.\")\n" +
                "end\n" +
                "\n" +
                "assert(currentTokens >= 0)\n" +
                "\n" +
                "if currentTokens == 0 then\n" +
                "    redis.call('hset', key, 'tokensRemaining', currentTokens)\n" +
                "    return 0\n" +
                "else\n" +
                "    redis.call('hset', key, 'tokensRemaining', currentTokens - 1)\n" +
                "    return 1\n" +
                "end";
        this.jedisCluster = jedisCluster;
        this.luaScriptSHA1 = jedisCluster.scriptLoad(luaScript, "ratelimiter");
        //try {
        //    this.luaScriptSHA1 = jedisCluster.scriptLoad(new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("ratelimiter.lua"))), LUA_SCRIPT_KEY);
        //} catch (IOException e) {
        //    throw new IllegalArgumentException("lua腳本加載失敗", e);
        //}
        this.limit = limit;
        this.intervalInMills = intervalInMills;
        this.intervalPerPermit = intervalInMills / limit;
    }


    public final boolean access(final String identity){
        String bucketKey = "rate:limiter:" + identity;
        return 1L == (long)jedisCluster.evalsha(luaScriptSHA1, 1, bucketKey,
                String.valueOf(this.limit),
                String.valueOf(this.intervalInMills),
                String.valueOf(this.intervalPerPermit),
                String.valueOf(System.currentTimeMillis()));
    }
}


/*
import redis.clients.jedis.JedisCluster;
import java.util.HashMap;
import java.util.Map;
//TokenBucket算法實現（易引发RaceCondition）
//Created by 玄玉<https://jadyer.cn/> on 2016/9/12 10:41.
@Deprecated
public class RateLimiter {
    private JedisCluster jedisCluster;
    private long limit;
    private long intervalInMills;
    private double intervalPerPermit;
    public RateLimiter(JedisCluster jedisCluster, long limit, long intervalInMills){
        this.jedisCluster = jedisCluster;
        this.limit = limit;
        this.intervalInMills = intervalInMills;
        this.intervalPerPermit = intervalInMills / limit;
    }
    private String genBucketKey(String identity){
        return "rate:limiter:" + intervalInMills + ":" + limit + ":" + identity;
    }
    //单线程操作下才能保证正确性，需要这些操作原子性的话，最好使用lua
    private boolean access(String identity){
        //计算该请求的TokenBucket的key
        String key = this.genBucketKey(identity);
        Map<String, String> counter = jedisCluster.hgetAll(key);
        //Redis中不存在TokenBucket，那就新建一个，並设置其Token数为最大值减一（去掉了这次请求获取的Token）
        if(counter.size() == 0) {
            TokenBucket tokenBucket = new TokenBucket(System.currentTimeMillis(), limit - 1);
            jedisCluster.hmset(key, tokenBucket.toHash());
            return true;
        }
        //Redis中已存在TokenBucket，則判斷其上一次加入Token的時間到當前時間的間隔，與它的interval關係
        TokenBucket tokenBucket = TokenBucket.fromHash(counter);
        long refillTime = System.currentTimeMillis();
        long intervalSinceLast = refillTime - tokenBucket.getLastRefillTime();
        long currentTokensRemaining;
        if(intervalSinceLast > intervalInMills){
            //時間間隔大於interval，則将Bucket的Token值重置为最大值减一（是在下面減的一）後更新到TokenBucket
            currentTokensRemaining = limit;
        }else{
            //時間間隔不大於interval，則计算本次的Token补充量並更新到TokenBucket
            long grantedTokens = (long)(intervalSinceLast/intervalPerPermit);
            System.out.println("gen token = [" + grantedTokens + "]");
            //使用Math.min()取二者中較小的，保證其不超過令牌桶的最大容量
            currentTokensRemaining = Math.min(grantedTokens + tokenBucket.getTokensRemaining(), limit);
        }
        tokenBucket.setLastRefillTime(refillTime);
        //零說明沒有可用的Token
        if(currentTokensRemaining == 0){
            tokenBucket.setTokensRemaining(currentTokensRemaining);
            jedisCluster.hmset(key, tokenBucket.toHash());
            return false;
        }else{
            tokenBucket.setTokensRemaining(currentTokensRemaining - 1);
            jedisCluster.hmset(key, tokenBucket.toHash());
            return true;
        }
    }
    private static class TokenBucket{
        //最后一次补充Token的时间
        private long lastRefillTime;
        //Bucket中的剩余Token数量
        private long tokensRemaining;
        TokenBucket(long lastRefillTime, long tokensRemaining){
            this.lastRefillTime = lastRefillTime;
            this.tokensRemaining = tokensRemaining;
        }
        static TokenBucket fromHash(Map<String, String> hash){
            long lastRefillTime = Long.parseLong(hash.get("lastRefillTime"));
            int tokensRemaining = Integer.parseInt(hash.get("tokensRemaining"));
            return new TokenBucket(lastRefillTime, tokensRemaining);
        }
        Map<String, String> toHash(){
            Map<String, String> hash = new HashMap<>();
            hash.put("lastRefillTime", String.valueOf(lastRefillTime));
            hash.put("tokensRemaining", String.valueOf(tokensRemaining));
            return hash;
        }
        long getTokensRemaining() {
            return tokensRemaining;
        }
        void setTokensRemaining(long tokensRemaining) {
            this.tokensRemaining = tokensRemaining;
        }
        long getLastRefillTime() {
            return lastRefillTime;
        }
        void setLastRefillTime(long lastRefillTime) {
            this.lastRefillTime = lastRefillTime;
        }
    }
}
*/