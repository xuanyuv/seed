package com.jadyer.seed.boot.ratelimiter;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisNoScriptException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis+Lua結合TokenBucket算法實現的RateLimiter
 * <ul>
 *     <li>可以考虑做成注解</li>
 *     <li>public @interface RateLimiter {}</li>
 *     <li>@RateLimiter(limit=2, timeout=5000) @GetMapping("/test") public void get(int id){}</li>
 *     <li>
 *         @Configuration
 *         class WebMvcConfigurer extends WebMvcConfigurerAdapter{
 *             @Autowired private JedisPool jedisPool;
 *             public void addInterceptors(InterceptorRegistry registry) {
 *                 registry.addInterceptor(new HandlerInterceptorAdapter() {
 *                     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
 *                         HandlerMethod handlerMethod = (HandlerMethod) handler;
 *                         Method method = handlerMethod.getMethod();
 *                         RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
 *                         if(null != rateLimiter){
 *                             int limit = rateLimiter.limit();
 *                             int timeout = rateLimiter.timeout();
 *                             return false;
 *                             return true;
 *                         }
 *                     }
 *                 }).addPathPatterns("/*");
 *             }
 *         }
 *     </li>
 * </ul>
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
 * Created by 玄玉<http://jadyer.cn/> on 2016/9/18 12:22.
 */
//@Component
public class RateLimiterLuaV2 {
    @Resource
    private JedisCluster jedisCluster;
    private static String luaScript;
    private String luaScriptSHA1;

    static {
        //try {
        //    this.luaScriptSHA1 = jedisCluster.scriptLoad(new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("ratelimiter.lua"))), LUA_SCRIPT_KEY);
        //} catch (IOException e) {
        //    throw new IllegalArgumentException("lua脚本加载失敗", e);
        //}
        //脚本注释見同包下的ratelimiter.lua
        luaScript = "local key = KEYS[1]\n" +
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
    }


    @PostConstruct
    public void initLuaScript(){
        this.luaScriptSHA1 = this.jedisCluster.scriptLoad(luaScript, "ratelimiter");
    }


    /**
     * 设置限流规则
     * <p>
     *     intervalPerPermit指的是TokenBucket桶填充Token的速率
     *     <ul>
     *         <li>可以理解為：該速率指的是每多少時間段填充一個Token</li>
     *         <li>假設limit=3，intervalInMills=10000ms，此時intervalPerPermit=3333ms，即需要每3.3s往桶里填充一個Token</li>
     *         <li>所以若需要計算某段時間內應該填充的Token數，就應該用這個時間段的毫秒數除以該參數</li>
     *         <li>比如計算5s內這個時間段，需要填充的Token數，就是Math.floor(5000/3333=1.5)=1，即5s內需填充1個Token</li>
     *     </ul>
     * </p>
     * @param limit           TokenBucket桶最大容量
     * @param intervalInMills TokenBucket桶容量消耗的最少時間，也即限流的流量間隔的毫秒數（可以理解為：intervalInMills時間段內，請求次數不能超過limit）
     */
    public void setLimitRule(String identity, long limit, long intervalInMills){
        Map<String, String> limitRuleMap = new HashMap<>();
        limitRuleMap.put("limit", String.valueOf(limit));
        limitRuleMap.put("intervalInMills", String.valueOf(intervalInMills));
        this.jedisCluster.hmset("rate:limiter:rule:" + identity, limitRuleMap);
    }


    /**
     * 限流
     * @return true表示允许请求
     */
    public boolean access(String identity) {
        String bucketKey = "rate:limiter:" + identity;
        String bucketRuleKey = "rate:limiter:rule:" + identity;
        Map<String, String> limitRuleMap = jedisCluster.hgetAll(bucketRuleKey);
        //未设置限流规则就表示放行请求
        if(null==limitRuleMap || limitRuleMap.isEmpty()){
            return true;
        }
        //只要有一个是零就表示阻止请求
        long limit;
        long intervalInMills;
        try{
            limit = Long.parseLong(limitRuleMap.get("limit"));
            intervalInMills = Long.parseLong(limitRuleMap.get("intervalInMills"));
            if(0==limit || 0==intervalInMills){
                return false;
            }
        }catch(Exception e){
            return false;
        }
        //处理TokenBucket
        long limitFlag;
        try {
            limitFlag = (long)this.jedisCluster.evalsha(this.luaScriptSHA1, 1, bucketKey,
                    limitRuleMap.get("limit"),
                    limitRuleMap.get("intervalInMills"),
                    String.valueOf(intervalInMills / limit),
                    String.valueOf(System.currentTimeMillis()));
        } catch (JedisNoScriptException e) {
            //测试环境遇到过这个提示redis.clients.jedis.exceptions.JedisNoScriptException: NOSCRIPT No matching script. Please use EVAL.
            //实际这个时候this.jedisCluster.scriptExists()返回的是true
            //后来发现还得先eval()，下次请求进来时才会自动走evalsha()
            //即只有首次请求才会走这里
            //但在com.jadyer.seed.boot.ratelimiter.RateLimiterLua.java里面就没有这个问题
            limitFlag = (long)this.jedisCluster.eval(luaScript, 1, bucketKey,
                    limitRuleMap.get("limit"),
                    limitRuleMap.get("intervalInMills"),
                    String.valueOf(intervalInMills / limit),
                    String.valueOf(System.currentTimeMillis()));
        }
        return 1L == limitFlag;
    }
}


/*
import redis.clients.jedis.JedisCluster;
import java.util.HashMap;
import java.util.Map;
//TokenBucket算法實現（易引发RaceCondition）
//Created by 玄玉<http://jadyer.cn/> on 2016/9/12 10:41.
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