package com.jadyer.seed.boot.ratelimiter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisNoScriptException;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis+Lua结合TokenBucket算法实现的RateLimiter
 * <ul>
 *     <li>可以考虑做成注解</li>
 *     <li>public @interface RateLimiter {}</li>
 *     <li>@RateLimiter(limit=2, timeout=5000) @GetMapping("/test") public void get(int id){}</li>
 *     <li>
 *         @Configuration
 *         class MyWebMvcConfigurer implements WebMvcConfigurer {
 *             @Autowired private JedisPool jedisPool;
 *             @Override
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
 *     <li>参考了以下网站</li>
 *     <li>http://jinnianshilongnian.iteye.com/blog/2305117</li>
 *     <li>https://zhuanlan.zhihu.com/p/20872901</li>
 *     <li>http://www.kissyu.org/2016/08/13/限流算法总结/</li>
 * </ul>
 * <ul>
 *     <li>需要注意不同节点间的操作</li>
 *     <li>https://www.v2ex.com/t/186712</li>
 *     <li>http://stackoverflow.com/questions/38234507/redis-cluster-update-keys-in-different-node-with-lua-script</li>
 * </ul>
 * Created by 玄玉<https://jadyer.cn/> on 2016/9/18 12:22.
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
        //    throw new IllegalArgumentException("lua脚本加载失败", e);
        //}
        //脚本注释见同包下的ratelimiter.lua
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
     *         <li>可以理解为：该速率指的是每多少时间段填充一个Token</li>
     *         <li>假设limit=3，intervalInMills=10000ms，此时intervalPerPermit=3333ms，即需要每3.3s往桶里填充一个Token</li>
     *         <li>所以若需要计算某段时间內应该填充的Token数，就应该用这个时间段的毫秒数除以该参数</li>
     *         <li>比如计算5s內这个时间段，需要填充的Token数，就是Math.floor(5000/3333=1.5)=1，即5s內需填充1个Token</li>
     *     </ul>
     * </p>
     * @param limit           TokenBucket桶最大容量
     * @param intervalInMills TokenBucket桶容量消耗的最少时间，也即限流的流量间隔的毫秒数（可以理解为：intervalInMills时间段內，请求次数不能超过limit）
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
//TokenBucket算法实现（易引发RaceCondition）
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
        //Redis中不存在TokenBucket，那就新建一个，并设置其Token数为最大值减一（去掉了这次请求获取的Token）
        if(counter.size() == 0) {
            TokenBucket tokenBucket = new TokenBucket(System.currentTimeMillis(), limit - 1);
            jedisCluster.hmset(key, tokenBucket.toHash());
            return true;
        }
        //Redis中已存在TokenBucket，则判断其上一次加入Token的时间到当前时间的间隔，与它的interval关系
        TokenBucket tokenBucket = TokenBucket.fromHash(counter);
        long refillTime = System.currentTimeMillis();
        long intervalSinceLast = refillTime - tokenBucket.getLastRefillTime();
        long currentTokensRemaining;
        if(intervalSinceLast > intervalInMills){
            //时间间隔大于interval，则将Bucket的Token值重置为最大值减一（是在下面減的一）后更新到TokenBucket
            currentTokensRemaining = limit;
        }else{
            //时间间隔不大于interval，则计算本次的Token补充量并更新到TokenBucket
            long grantedTokens = (long)(intervalSinceLast/intervalPerPermit);
            System.out.println("gen token = [" + grantedTokens + "]");
            //使用Math.min()取二者中较小的，保证其不超过令牌桶的最大容量
            currentTokensRemaining = Math.min(grantedTokens + tokenBucket.getTokensRemaining(), limit);
        }
        tokenBucket.setLastRefillTime(refillTime);
        //零说明沒有可用的Token
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