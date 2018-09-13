package com.jadyer.seed.comm.annotation.lock.cluster;

import com.jadyer.seed.comm.util.JadyerUtil;
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
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Seed分布式锁处理器
 * ---------------------------------------------------------------------------------------------------
 * Redisson官方讲解：https://github.com/redisson/redisson/wiki/目录
 * Redisson实例配置：https://github.com/redisson/redisson/wiki/2.-配置方法#26-单redis节点模式
 * ---------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2018/6/5 10:00.
 */
@Aspect
@Configuration
@ConditionalOnClass({RedissonClient.class})
@ConditionalOnProperty(name="redisson")
@ConfigurationProperties(prefix="redisson")
public class SeedLockConfiguration implements EnvironmentAware {
    /** 节点地址：[host:port] */
    private List<String> nodes = new ArrayList<>();
    /** 监控锁的看门狗超时，单位：毫秒（默认值：30000） */
    private int lockWatchdogTimeout;
    /** 最小空闲连接数（默认值：32） */
    private int connectionMinimumIdleSize;
    /** 连接池大小（默认值：64） */
    private int connectionPoolSize;
    /** 连接超时，单位：毫秒（默认值：10000） */
    private int connectTimeout;
    /** 数据库编号（默认值：0） */
    private int database;
    /** 密码（默认值：null） */
    private String password;
    private Environment environment;
    private static final String LOCK_PREFIX = "seedLock:";
    public static final List<RedissonClient> redissonClientList = new ArrayList<>();
    private ExpressionParser parser = new SpelExpressionParser();
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    @PostConstruct
    public void initRedissonClientList(){
        //空判断：防止接入SpringCloud后二次初始化
        if(!redissonClientList.isEmpty()){
            return;
        }
        for(String node : nodes){
            Config config = new Config();
            if(lockWatchdogTimeout > 0){
                config.setLockWatchdogTimeout(lockWatchdogTimeout);
            }
            SingleServerConfig singleConfig = config.useSingleServer().setAddress(node);
            if(connectionMinimumIdleSize > 0){
                singleConfig.setConnectionMinimumIdleSize(connectionMinimumIdleSize);
            }
            if(connectionPoolSize > 0){
                singleConfig.setConnectionPoolSize(connectionPoolSize);
            }
            if(connectTimeout > 0){
                singleConfig.setConnectTimeout(connectTimeout);
            }
            if(database > 0){
                singleConfig.setDatabase(database);
            }
            if(StringUtils.isNotBlank(password)){
                singleConfig.setPassword(password);
            }
            redissonClientList.add(Redisson.create(config));
        }
    }


    @Around("@annotation(com.jadyer.seed.comm.annotation.lock.cluster.SeedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //计算上锁的key
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        SeedLock seedLock = method.getAnnotation(SeedLock.class);
        if(StringUtils.isBlank(seedLock.key())){
            LogUtil.getLogger().error("资源加锁-->失败：空的key");
            return null;
        }
        String key = LOCK_PREFIX + (StringUtils.isBlank(seedLock.appname())?"":this.getPropertyFromEnv(seedLock.appname())+":") + this.parseSpringEL(seedLock.key(), method, joinPoint.getArgs());
        //加锁
        RLock[] rLocks = new RLock[redissonClientList.size()];
        for(int i=0; i<redissonClientList.size(); i++){
            rLocks[i] = redissonClientList.get(i).getLock(key);
        }
        RedissonRedLock redLock = null;
        try{
            try {
                //new RedissonRedLock(rLocks)可能发生异常：比如应用正在启动中，就来调用这里
                //Caused by: java.lang.IllegalArgumentException: Lock objects are not defined
                redLock = new RedissonRedLock(rLocks);
                if(!redLock.tryLock(seedLock.waitTime(), seedLock.leaseTime(), seedLock.unit())){
                    LogUtil.getLogger().error("资源[{}]加锁-->失败", key);
                    return null;
                }
            } catch (InterruptedException | IllegalArgumentException e) {
                LogUtil.getLogger().error("资源[{}]加锁-->失败：{}", key, JadyerUtil.extractStackTraceCausedBy(e));
                return null;
            }
            LogUtil.getLogger().info("资源[{}]加锁-->成功", key);
            return joinPoint.proceed();
        }finally{
            if(null != redLock){
                redLock.unlock();
            }
            LogUtil.getLogger().info("资源[{}]解锁-->完毕", key);
        }
    }


    private String getPropertyFromEnv(String prop){
        if(StringUtils.isBlank(prop)){
            return "";
        }
        if(prop.startsWith("${") && prop.endsWith("}")){
            prop = prop.substring(2, prop.length()-1);
            prop = this.environment.getProperty(prop);
        }
        return prop;
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


    public List<String> getNodes() {
        return nodes;
    }

    public int getLockWatchdogTimeout() {
        return lockWatchdogTimeout;
    }

    public void setLockWatchdogTimeout(int lockWatchdogTimeout) {
        this.lockWatchdogTimeout = lockWatchdogTimeout;
    }

    public int getConnectionMinimumIdleSize() {
        return connectionMinimumIdleSize;
    }

    public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
        this.connectionMinimumIdleSize = connectionMinimumIdleSize;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}