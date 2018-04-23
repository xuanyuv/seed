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
        //打印入参
        Object[] objs = invocation.getArguments();
        List<Object> argsList = new ArrayList<>();
        for(Object obj : objs){
            if(obj instanceof ServletRequest || obj instanceof ServletResponse){
                continue;
            }
            argsList.add(obj);
        }
        SerializerFeature[] serializerFeatures = new SerializerFeature[5];
        serializerFeatures[0] = SerializerFeature.WriteMapNullValue;
        serializerFeatures[1] = SerializerFeature.WriteNullListAsEmpty;
        serializerFeatures[2] = SerializerFeature.WriteNullNumberAsZero;
        serializerFeatures[3] = SerializerFeature.WriteNullStringAsEmpty;
        serializerFeatures[4] = SerializerFeature.WriteNullBooleanAsFalse;
        log.info("{}()被调用，入参为{}", methodInfo, JSON.toJSONStringWithDateFormat(argsList, JSON.DEFFAULT_DATE_FORMAT, serializerFeatures));
        //表单验证
        for(Object obj : objs){
            //if(null!=obj && obj.getClass().getName().startsWith("com.jadyer.seed.open.model")){
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
        //打印出参
        long endTime = System.currentTimeMillis();
        String returnInfo;
        if(null == respData){
            returnInfo = "";
        }else if(respData instanceof ServletRequest) {
            returnInfo = "ServletRequest";
        }else if(respData instanceof ServletResponse) {
            returnInfo = "ServletResponse";
        }else if(respData.getClass().isAssignableFrom(ResponseEntity.class)) {
            returnInfo = "ResponseEntity";
        }else{
            returnInfo = JSON.toJSONStringWithDateFormat(respData, JSON.DEFFAULT_DATE_FORMAT, serializerFeatures);
        }
        log.info("{}()被调用，出参为{}，Duration[{}]ms", methodInfo, returnInfo, endTime-startTime);
        log.info("---------------------------------------------------------------------------------------------");
        return respData;
    }
}