package com.jadyer.seed.boot.lock.cluster;

import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Seed分布式锁处理器
 * ---------------------------------------------------------------------------------------------------
 * 1、@SpringBootApplication(scanBasePackages="com.jadyer.seed")
 * 2、引入"org.redisson:redisson:3.7.1"
 * 3、配置文件配置以下属性
 *    redisson:
 *      password: xuanyu
 *      nodes:
 *        - 192.168.2.210:7000
 *        - 192.168.2.210:7001
 *        - 192.168.2.210:7002
 *        - 192.168.2.210:7003
 *        - 192.168.2.210:7004
 *        - 192.168.2.210:7005
 * 4、@SeedLock("#userMsg.name")
 *    public CommResult<Map<String, Object>> prop(int id, UserMsg userMsg){ // do business... }
 * ---------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2018/6/5 10:00.
 */
//@Configuration
@Aspect
@Component
@ConditionalOnClass({RedissonClient.class})
@ConfigurationProperties(prefix="redisson")
public class SeedLockConfiguration {
    private String password;
    private List<String> nodes = new ArrayList<>();
    private static final List<RedissonClient> redissonClientList = new ArrayList<>();
    private ExpressionParser parser = new SpelExpressionParser();
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @PostConstruct
    public void initRedissonClientList(){
        for (String node : nodes) {
            Config config = new Config();
            if(StringUtils.isBlank(password)){
                config.useSingleServer().setAddress("redis://" + node);
            }else{
                config.useSingleServer().setAddress("redis://" + node).setPassword(password);
            }
            redissonClientList.add(Redisson.create(config));
        }
    }

    @Around("@annotation(com.jadyer.seed.boot.lock.cluster.SeedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //计算上锁的key
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        SeedLock seedLock = method.getAnnotation(SeedLock.class);
        if(StringUtils.isBlank(seedLock.value())){
            LogUtil.getLogger().info("资源[]加锁-->失败：空的key");
            return null;
        }
        String key = "seedLock:" + this.parseSpringEL(seedLock.value(), method, joinPoint.getArgs());
        //加锁
        RLock[] rLocks = new RLock[redissonClientList.size()];
        for(int i=0; i<redissonClientList.size(); i++){
            rLocks[i] = redissonClientList.get(i).getLock(key);
        }
        RedissonRedLock redLock = new RedissonRedLock(rLocks);
        try{
            if(!redLock.tryLock(seedLock.waitTime(), seedLock.leaseTime(), seedLock.unit())){
                LogUtil.getLogger().info("资源[{}]加锁-->失败", key);
                return null;
            }
            LogUtil.getLogger().info("资源[{}]加锁-->成功", key);
            return joinPoint.proceed();
        }finally{
            redLock.unlock();
            LogUtil.getLogger().info("资源[{}]解锁-->完毕", key);
        }
    }


    /**
     * 解析SpringEL表达式
     * @param key    表达式
     * @param method 方法
     * @param args   方法参数
     * @return spel解析结果
     */
    private String parseSpringEL(String key, Method method, Object[] args) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for(int i=0; i<params.length; i++){
            context.setVariable(params[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public static List<RedissonClient> getRedissonClientList() {
        return redissonClientList;
    }
}