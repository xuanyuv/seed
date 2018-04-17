package com.jadyer.seed.comm.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.log.annotation.EnableAutoValid;
import com.jadyer.seed.comm.util.ValidatorUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2018/4/17 13:39.
 */
class LogAspect implements MethodInterceptor {
    private static Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object respData;
        long startTime = System.currentTimeMillis();
        //类名.方法名（不含类的包名），举例：MppController.bind
        String methodInfo = invocation.getThis().getClass().getSimpleName() + "." + invocation.getMethod().getName();
        //获取入参
        Object[] objs = invocation.getArguments();
        log.info("{}()被调用，入参为{}", (methodInfo), printArgs(objs));
        //表单验证
        for(Object obj : objs){
            if(obj.getClass().isAnnotationPresent(EnableAutoValid.class)){
                String validateResult = ValidatorUtil.validate(obj);
                log.info("{}()的表单-->{}", methodInfo, StringUtils.isBlank(validateResult)?"验证通过":"验证未通过");
                if (StringUtils.isNotBlank(validateResult)) {
                    throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), validateResult);
                }
            }
        }
        //执行方法
        respData = invocation.proceed();
        long endTime = System.currentTimeMillis();
        String returnInfo;
        if(null!=respData && respData.getClass().isAssignableFrom(ResponseEntity.class)){
            returnInfo = "ResponseEntity";
        }else{
            returnInfo = JSON.toJSONStringWithDateFormat(respData, JSON.DEFFAULT_DATE_FORMAT, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);
        }
        log.info("{}()被调用，出参为{}，Duration[{}]ms", methodInfo, returnInfo, endTime-startTime);
        log.info("---------------------------------------------------------------------------------------------");
        return respData;
    }

    private String printArgs(Object... objs) {
        List<Object> argsList = new ArrayList<>();
        for(Object obj : objs){
            if(obj instanceof ServletResponse || obj instanceof ServletRequest){
                continue;
            }
            argsList.add(obj);
        }
        return JSON.toJSONStringWithDateFormat(argsList, JSON.DEFFAULT_DATE_FORMAT, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);
    }
}