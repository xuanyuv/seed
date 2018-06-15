package com.jadyer.seed.qss.boot;

import com.jadyer.seed.comm.annotation.SeedLock;
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
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
 * Redisson官方讲解：https://github.com/redisson/redisson/wiki/目录
 * Redisson实例配置：https://github.com/redisson/redisson/wiki/2.-配置方法#26-单redis节点模式
 * ---------------------------------------------------------------------------------------------------
 * 1、@SpringBootApplication(scanBasePackages="com.jadyer.seed")
 * 2、引入"org.redisson:redisson:3.7.1"
 * 3、配置文件配置以下属性
 *    redisson:
 *      lockWatchdogTimeout: 10000
 *      connectionMinimumIdleSize: 16
 *      connectionPoolSize: 32
 *      connectTimeout: 3000
 *      password: xuanyu
 *      nodes:
 *        - redis://192.168.2.210:7000
 *        - redis://192.168.2.210:7001
 *        - redis://192.168.2.210:7002
 *        - redis://192.168.2.210:7003
 *        - redis://192.168.2.210:7004
 *        - redis://192.168.2.210:7005
 * 4、@SeedLock("#userMsg.name")
 *    public CommResult<Map<String, Object>> prop(int id, UserMsg userMsg){ // do business... }
 * ---------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2018/6/5 10:00.
 */
@Configuration
@Aspect
@Component
@ConditionalOnClass({RedissonClient.class})
@ConfigurationProperties(prefix="redisson")
public class SeedLockConfiguration {
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
    private static final List<RedissonClient> redissonClientList = new ArrayList<>();
    private ExpressionParser parser = new SpelExpressionParser();
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

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


    @Around("@annotation(com.jadyer.seed.comm.annotation.SeedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //计算上锁的key
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        SeedLock seedLock = method.getAnnotation(SeedLock.class);
        if(StringUtils.isBlank(seedLock.key())){
            LogUtil.getLogger().error("资源[]加锁-->失败：空的key");
            return null;
        }
        String key = "seedLock:" + this.parseSpringEL(seedLock.key(), method, joinPoint.getArgs());
        //加锁
        RLock[] rLocks = new RLock[redissonClientList.size()];
        for(int i=0; i<redissonClientList.size(); i++){
            rLocks[i] = redissonClientList.get(i).getLock(key);
        }
        RedissonRedLock redLock = new RedissonRedLock(rLocks);
        try{
            if(!redLock.tryLock(seedLock.waitTime(), seedLock.leaseTime(), seedLock.unit())){
                LogUtil.getLogger().error("资源[{}]加锁-->失败", key);
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