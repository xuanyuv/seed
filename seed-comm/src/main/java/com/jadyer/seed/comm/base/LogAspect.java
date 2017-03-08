package com.jadyer.seed.comm.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jadyer.seed.comm.util.IPUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志记录切面器
 * <ul>
 *     <li>
 *         Around("execution(* com.jadyer.seed..*Controller.*(..))")<br/>
 *         测试发现Controller中只要有一个方法是public则其所有方法都会被切，当所有方法都不是public时才都不会被切
 *     </li>
 *     <li>
 *         Around("execution(public * com.jadyer.seed..*Controller.*(..))")<br/>
 *         此时就只会切public的方法，而不会受其它方法修饰符的影响，推荐使用
 *     </li>
 * </ul>
 * Created by 玄玉<https://jadyer.github.io/> on 2015/8/18 9:49.
 */
@Aspect
@Component
public class LogAspect {
	@Around("execution(public * com.jadyer.seed..*Controller.*(..))")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		Object respData;
		long startTime = System.currentTimeMillis();
		String className = joinPoint.getTarget().getClass().getSimpleName(); //获取类名(这里只切面了Controller类)
		String methodName = joinPoint.getSignature().getName();              //获取方法名
		String methodInfo = className + "." + methodName;                    //组织类名.方法名
		//Object[] objs = joinPoint.getArgs();                               //获取方法参数
		//String paramInfo = com.alibaba.fastjson.JSON.toJSONString(args);
		/*
		if(RouterController.class.getSimpleName().equals(className)){
			return joinPoint.proceed();
		}
		*/
		/*
		 * 打印Controller入参
		 * 1.也可以使用@Resource注入private HttpServletRequest request;再加上setRequest()即可
		 *   当使用@Resource注入HttpServletRequest时，在JUnit中通过new ClassPathXmlApplicationContext("applicationContext.xml")加载Spring时会报告下面的异常
		 *   org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type [javax.servlet.http.HttpServletRequest] found for dependency
		 *   所以这里通过RequestContextHolder来获取HttpServletRequest
		 * 2.当上传文件时，由于表单设置了enctype="multipart/form-data"，会将表单用其它的文本域与file域一起作为流提交
		 *   所以此时request.getParameter()是无法获取到表单中的文本域的，这时可以借助文件上传组件来获取比如org.apache.commons.fileupload.FileItem
		 */
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		LogUtil.getLogger().info("{}()-->{}被调用, 客户端IP={}, 入参为[{}]", methodInfo, request.getRequestURI(), IPUtil.getClientIP(request), JadyerUtil.buildStringFromMapWithStringArray(request.getParameterMap()));
		/*
		 * 表单验证
		 */
		//Object[] objs = joinPoint.getArgs();
		//for (Object obj : objs) {
		//	if (null != obj && obj.getClass().getName().startsWith("com.jadyer.seed.ucs")) {
		//		LogUtil.getLogger().info("{}()-->{}被调用, 客户端IP={}, 得到的表单参数为{}", methodInfo, request.getRequestURI(), IPUtil.getClientIP(request), ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE));
		//		String validateResult = ValidatorUtil.validate(obj);
		//		LogUtil.getLogger().info("{}()-->{}的表单-->{}", methodInfo, request.getRequestURI(), StringUtils.isBlank(validateResult)?"验证通过":"验证未通过");
		//		if (StringUtils.isNotBlank(validateResult)) {
		//			throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), validateResult);
		//		}
		//	}
		//}
		/*
		 * 执行Controller的方法
		 */
		respData = joinPoint.proceed();
		long endTime = System.currentTimeMillis();
		String returnInfo;
		if(null!=respData && respData.getClass().isAssignableFrom(ResponseEntity.class)){
			returnInfo = "ResponseEntity";
		}else{
			returnInfo = JSON.toJSONStringWithDateFormat(respData, JSON.DEFFAULT_DATE_FORMAT, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);
		}
		LogUtil.getLogger().info("{}()-->{}被调用, 出参为[{}], Duration[{}]ms", methodInfo, request.getRequestURI(), returnInfo, endTime-startTime);
		LogUtil.getLogger().info("---------------------------------------------------------------------------------------------");
		//注意這里一定要原封不动的返回joinPoint.proceed()结果，若返回JSON.toJSONString(respData)则会报告下面的异常
		//java.lang.String cannot be cast to com.jadyer.seed.comm.constant.CommonResult
		//这是由于JSON.toJSONString(respData)得到的是字符串，而实际Controller方法里面返回的是CommonResult对象
		return respData;
	}
}