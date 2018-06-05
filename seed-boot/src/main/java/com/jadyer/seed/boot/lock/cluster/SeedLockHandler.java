package com.jadyer.seed.boot.lock.cluster;

import com.jadyer.seed.boot.RedissonConfiguration;
import com.jadyer.seed.boot.lock.cluster.annotation.SeedLock;
import com.jadyer.seed.comm.util.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 【待测试】
 * Created by 玄玉<http://jadyer.cn/> on 2018/6/5 10:00.
 */
@Aspect
@Component
public class SeedLockHandler {
    private ExpressionParser parser = new SpelExpressionParser();
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Around("@annotation(com.jadyer.seed.boot.lock.cluster.annotation.SeedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //计算上锁的key
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        SeedLock seedLock = method.getAnnotation(SeedLock.class);
        String key = this.parseSpringEL(seedLock.value(), method, joinPoint.getArgs());
        //加锁
        List<RLock> rLockList = new ArrayList<>();
        for(RedissonClient client : RedissonConfiguration.getRedissonClientList()){
            rLockList.add(client.getLock(key));
        }
        RedissonRedLock redLock = new RedissonRedLock((RLock[]) rLockList.toArray());
        if(!redLock.tryLock(seedLock.waitTime(), seedLock.leaseTime(), seedLock.unit())){
            LogUtil.getLogger().info("资源[{}]加锁-->失败", key);
            return null;
        }
        LogUtil.getLogger().info("资源[{}]加锁-->成功", key);
        try{
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
     */
    private String parseSpringEL(String key, Method method, Object[] args) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for(int i=0; i<params.length; i++){
            context.setVariable(params[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }
}