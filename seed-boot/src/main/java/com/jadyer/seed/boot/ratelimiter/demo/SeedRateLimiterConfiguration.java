package com.jadyer.seed.boot.ratelimiter.demo;

import com.jadyer.seed.comm.util.LogUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisNoScriptException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 限流处理器
 * Created by 玄玉<https://jadyer.cn/> on 2021/7/19 18:56.
 */
@Aspect
// @Configuration
@ConditionalOnClass({RedissonClient.class})
@ConfigurationProperties(prefix="redis")
public class SeedRateLimiterConfiguration {
    @Resource
    private JedisCluster jedisCluster;
    private static String luaScript;
    private String luaScriptSHA1;
    private static final String RATELIMITER_PREFIX = SeedRateLimiter.class.getSimpleName() + ":";
    private ExpressionParser parser = new SpelExpressionParser();
    private StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();

    static {
        luaScript = """
                local key = KEYS[1]
                local limit, interval, intervalPerPermit, refillTime = tonumber(ARGV[1]), tonumber(ARGV[2]), tonumber(ARGV[3]), tonumber(ARGV[4])
                                
                local currentTokens
                local bucket = redis.call('hgetall', key)
                                
                if table.maxn(bucket) == 0 then
                    currentTokens = limit
                    redis.call('hset', key, 'lastRefillTime', refillTime)
                elseif table.maxn(bucket) == 4 then
                    local lastRefillTime, tokensRemaining = tonumber(bucket[2]), tonumber(bucket[4])
                    if refillTime > lastRefillTime then
                        local intervalSinceLast = refillTime - lastRefillTime
                        if intervalSinceLast > interval then
                            currentTokens = limit
                            redis.call('hset', key, 'lastRefillTime', refillTime)
                        else
                            local grantedTokens = math.floor(intervalSinceLast / intervalPerPermit)
                            if grantedTokens > 0 then
                                local padMillis = math.fmod(intervalSinceLast, intervalPerPermit)
                                redis.call('hset', key, 'lastRefillTime', refillTime - padMillis)
                            end
                            currentTokens = math.min(grantedTokens + tokensRemaining, limit)
                        end
                    else
                        currentTokens = tokensRemaining
                    end
                else
                    error("Size of bucket is " .. table.maxn(bucket) .. ", Should Be 0 or 4.")
                end
                                
                assert(currentTokens >= 0)
                                
                if currentTokens == 0 then
                    redis.call('hset', key, 'tokensRemaining', currentTokens)
                    return 0
                else
                    redis.call('hset', key, 'tokensRemaining', currentTokens - 1)
                    return 1
                end
                """;
    }


    @PostConstruct
    public void initLuaScript(){
        this.luaScriptSHA1 = this.jedisCluster.scriptLoad(luaScript, "ratelimiter");
    }


    @Around("@annotation(com.jadyer.seed.boot.ratelimiter.demo.SeedRateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        if(!method.isAnnotationPresent(GetMapping.class) && !method.isAnnotationPresent(PostMapping.class) && !method.isAnnotationPresent(RequestMapping.class)){
            return joinPoint.proceed();
        }
        SeedRateLimiter grateLimiterSeed = method.getAnnotation(SeedRateLimiter.class);
        String key = RATELIMITER_PREFIX + this.parseSpringEL(grateLimiterSeed, joinPoint);
        this.setLimitRule(key, grateLimiterSeed.tps(), 1000);
        if(this.access(key)){
            return joinPoint.proceed();
        }
        LogUtil.getLogger().error("资源[{}]限流-->未通过", key);
        return null;
    }


    private String parseSpringEL(SeedRateLimiter grateLimiterSeed, ProceedingJoinPoint joinPoint) {
        if(!grateLimiterSeed.key().contains("#") && !grateLimiterSeed.key().contains("'")){
            return grateLimiterSeed.key();
        }
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();
        String[] params = discoverer.getParameterNames(method);
        if(ObjectUtils.isEmpty(params)){
            return grateLimiterSeed.key();
        }
        EvaluationContext context = new StandardEvaluationContext();
        for(int i=0; i<params.length; i++){
            context.setVariable(params[i], args[i]);
        }
        return parser.parseExpression(grateLimiterSeed.key()).getValue(context, String.class);
    }


    private void setLimitRule(String identity, long limit, long intervalInMills){
        Map<String, String> limitRuleMap = new HashMap<>();
        limitRuleMap.put("limit", String.valueOf(limit));
        limitRuleMap.put("intervalInMills", String.valueOf(intervalInMills));
        this.jedisCluster.hmset("rate:limiter:rule:" + identity, limitRuleMap);
    }


    private boolean access(String identity) {
        String bucketKey = "rate:limiter:" + identity;
        String bucketRuleKey = "rate:limiter:rule:" + identity;
        Map<String, String> limitRuleMap = jedisCluster.hgetAll(bucketRuleKey);
        if(null==limitRuleMap || limitRuleMap.isEmpty()){
            return true;
        }
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
        long limitFlag;
        try {
            limitFlag = (long)this.jedisCluster.evalsha(this.luaScriptSHA1, 1, bucketKey,
                    limitRuleMap.get("limit"),
                    limitRuleMap.get("intervalInMills"),
                    String.valueOf(intervalInMills / limit),
                    String.valueOf(System.currentTimeMillis()));
        } catch (JedisNoScriptException e) {
            limitFlag = (long)this.jedisCluster.eval(luaScript, 1, bucketKey,
                    limitRuleMap.get("limit"),
                    limitRuleMap.get("intervalInMills"),
                    String.valueOf(intervalInMills / limit),
                    String.valueOf(System.currentTimeMillis()));
        }
        return 1L == limitFlag;
    }
}