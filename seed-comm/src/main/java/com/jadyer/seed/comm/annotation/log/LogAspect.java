package com.jadyer.seed.comm.annotation.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/4/17 13:39.
 */
class LogAspect implements MethodInterceptor {
    private static SerializerFeature[] serializerFeatures = new SerializerFeature[8];
    static {
        serializerFeatures[0] = SerializerFeature.QuoteFieldNames;
        serializerFeatures[1] = SerializerFeature.WriteMapNullValue;
        serializerFeatures[2] = SerializerFeature.WriteNullListAsEmpty;
        serializerFeatures[3] = SerializerFeature.WriteNullNumberAsZero;
        serializerFeatures[4] = SerializerFeature.WriteNullStringAsEmpty;
        serializerFeatures[5] = SerializerFeature.WriteNullBooleanAsFalse;
        serializerFeatures[6] = SerializerFeature.WriteDateUseDateFormat;
        serializerFeatures[7] = SerializerFeature.DisableCircularReferenceDetect;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object respData = null;
        long startTime = System.currentTimeMillis();
        // 类名.方法名（不含类的包名），举例：MppController.bind
        String methodInfo = invocation.getThis().getClass().getSimpleName() + "." + invocation.getMethod().getName();
        // 打印入参（对于入参为MultipartFile类型等，可以在实体类使用@com.alibaba.fastjson.annotation.JSONField(serialize=false)标识不序列化）
        Object[] objs = invocation.getArguments();
        List<Object> argsList = new ArrayList<>();
        for(Object obj : objs){
            if(obj instanceof ServletRequest || obj instanceof ServletResponse || obj instanceof MultipartFile){
                continue;
            }
            argsList.add(obj);
        }
        LogUtil.getLogger().info("{}()被调用，入参为{}", methodInfo, JSON.toJSONStringWithDateFormat(argsList, JSON.DEFFAULT_DATE_FORMAT));
        // 表单验证
        for(Object obj : argsList){
            if(null!=obj && ((obj.getClass().isAnnotationPresent(EnableFormValid.class) || invocation.getMethod().isAnnotationPresent(EnableFormValid.class)) || invocation.getThis().getClass().isAnnotationPresent(EnableFormValid.class))){
                String validateResult = ValidatorUtil.validate(obj);
                LogUtil.getLogger().info("{}()的表单-->{}", methodInfo, StringUtils.isBlank(validateResult)?"验证通过":"验证未通过");
                if (StringUtils.isNotBlank(validateResult)) {
                    respData = CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), validateResult);
                }
            }
        }
        if(null == respData){
            try{
                // 执行方法
                respData = invocation.proceed();
            }catch(Throwable cause){
                // 异常处理
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(null == attributes){
                    return invocation.proceed();
                }
                LogUtil.getLogger().info("Exception Occured URL=" + attributes.getRequest().getRequestURL() + "，堆栈轨迹如下", cause);
                int code;
                String msg = cause.getMessage();
                if(cause instanceof SeedException){
                    code = ((SeedException)cause).getCode();
                }else{
                    code = CodeEnum.SYSTEM_ERROR.getCode();
                    msg = JadyerUtil.extractStackTraceCausedBy(cause);
                }
                respData = CommResult.fail(code, msg);
            }
        }
        // 打印出参并返回
        long endTime = System.currentTimeMillis();
        String returnInfo;
        if(null == respData){
            returnInfo = "<null>";
        }else if(respData instanceof InputStream) {
            returnInfo = "<java.io.InputStream>";
        }else if(respData instanceof ServletRequest) {
            returnInfo = "<javax.servlet.ServletRequest>";
        }else if(respData instanceof ServletResponse) {
            returnInfo = "<javax.servlet.ServletResponse>";
        }else if(respData.getClass().isAssignableFrom(ResponseEntity.class)) {
            returnInfo = "<org.springframework.http.ResponseEntity>";
        }else{
            returnInfo = JSON.toJSONStringWithDateFormat(respData, JSON.DEFFAULT_DATE_FORMAT, serializerFeatures);
        }
        LogUtil.getLogger().info("{}()被调用，出参为{}，Duration[{}]ms", methodInfo, returnInfo, endTime-startTime);
        LogUtil.getLogger().info("---------------------------------------------------------------------------------------------");
        return respData;
    }
}