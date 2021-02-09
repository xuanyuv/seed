package com.jadyer.seed.comm.annotation.log;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/4/17 13:24.
 */
@Configuration
public class LogConfiguration {
    @Bean
    public LogAdvisor logAdvisor() {
        return new LogAdvisor();
    }

    static class LogAdvisor extends AbstractPointcutAdvisor {
        private static final long serialVersionUID = 2375671248486443144L;
        @Override
        public Advice getAdvice() {
            return new LogAspect();
        }
        @Override
        public Pointcut getPointcut() {
            return new StaticMethodMatcherPointcut(){
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    //若类上面注解了DisableLog，则不打印日志（仅此类，不包括该类中被调用的类）
                    if(targetClass.isAnnotationPresent(DisableLog.class)){
                        return false;
                    }
                    //若方法上面注解了DisableLog，则不打印日志
                    if(method.isAnnotationPresent(DisableLog.class)){
                        return false;
                    }
                    //若类上面注解了EnableLog，则打印日志
                    if(targetClass.isAnnotationPresent(EnableLog.class)){
                        return true;
                    }
                    //若方法上面注解了EnableLog，则打印日志
                    if(method.isAnnotationPresent(EnableLog.class)){
                        return true;
                    }
                    //默认的：打印标注了RestController的类、以及ResponseBody的方法，的日志
                    return targetClass.isAnnotationPresent(RestController.class) || method.isAnnotationPresent(ResponseBody.class);
                }
            };
        }
    }
}