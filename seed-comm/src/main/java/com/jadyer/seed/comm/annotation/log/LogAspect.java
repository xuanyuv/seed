package com.jadyer.seed.comm.annotation.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.ValidatorUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/4/17 13:39.
 */
class LogAspect implements MethodInterceptor {
    private static Logger log = LoggerFactory.getLogger(LogAspect.class);
    private static SerializerFeature[] serializerFeatures = new SerializerFeature[5];
    static {
        serializerFeatures[0] = SerializerFeature.WriteMapNullValue;
        serializerFeatures[1] = SerializerFeature.WriteNullListAsEmpty;
        serializerFeatures[2] = SerializerFeature.WriteNullNumberAsZero;
        serializerFeatures[3] = SerializerFeature.WriteNullStringAsEmpty;
        serializerFeatures[4] = SerializerFeature.WriteNullBooleanAsFalse;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object respData;
        long startTime = System.currentTimeMillis();
        //类名.方法名（不含类的包名），举例：MppController.bind
        String methodInfo = invocation.getThis().getClass().getSimpleName() + "." + invocation.getMethod().getName();
        //打印入参（对于入参为MultipartFile类型等，可以在实体类使用@com.alibaba.fastjson.annotation.JSONField(serialize=false)标识不序列化）
        Object[] objs = invocation.getArguments();
        List<Object> argsList = new ArrayList<>();
        for(Object obj : objs){
            if(obj instanceof ServletRequest || obj instanceof ServletResponse){
                continue;
            }
            argsList.add(obj);
        }
        log.info("{}()被调用，入参为{}", methodInfo, JSON.toJSONStringWithDateFormat(argsList, JSON.DEFFAULT_DATE_FORMAT, serializerFeatures));
        //表单验证
        for(Object obj : objs){
            if(null!=obj && (obj.getClass().isAnnotationPresent(EnableFormValid.class) || invocation.getMethod().isAnnotationPresent(EnableFormValid.class))){
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
            returnInfo = "<null>";
        }else if(respData instanceof ServletRequest) {
            returnInfo = "<javax.servlet.ServletRequest>";
        }else if(respData instanceof ServletResponse) {
            returnInfo = "<javax.servlet.ServletResponse>";
        }else if(respData instanceof InputStream) {
            returnInfo = "<java.io.InputStream>";
        }else if(respData.getClass().isAssignableFrom(ResponseEntity.class)) {
            returnInfo = "<org.springframework.http.ResponseEntity>";
        }else{
            returnInfo = JSON.toJSONStringWithDateFormat(respData, JSON.DEFFAULT_DATE_FORMAT, serializerFeatures);
        }
        log.info("{}()被调用，出参为{}，Duration[{}]ms", methodInfo, returnInfo, endTime-startTime);
        log.info("---------------------------------------------------------------------------------------------");
        return respData;
    }
}