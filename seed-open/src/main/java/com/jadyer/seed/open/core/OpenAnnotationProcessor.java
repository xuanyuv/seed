package com.jadyer.seed.open.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.SpringContextHolder;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.open.model.ReqData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Open注解处理器
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/19 19:20.
 */
enum OpenAnnotationProcessor {
    INSTANCE;

    private String appidParent = "100";
    private ConcurrentHashMap<String, Method> methodMap = new ConcurrentHashMap<>();

    OpenAnnotationProcessor(){
        Class<?> serviceClazz = this.getBean(appidParent).getClass();
        for(Method obj : serviceClazz.getDeclaredMethods()){
            OpenMethod openMethod = obj.getAnnotation(OpenMethod.class);
            if(null != openMethod){
                //缓存所有业务实现类的根类中的方法
                methodMap.put(appidParent + "-" + (StringUtils.isNotBlank(openMethod.value())?openMethod.value():openMethod.methodName()), obj);
            }
        }
    }

    Object process(ReqData reqData, HttpServletRequest request, HttpServletResponse response){
        Method method = this.getMethod(reqData.getAppid(), reqData.getMethod());
        //获取该方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        try {
            //该方法没有参数
            if(parameterTypes.length == 0){
                return method.invoke(this.getBean(reqData.getAppid()), (Object)null);
            }
            //该方法有参数
            List<Object> paramList = new ArrayList<>();
            for(Class<?> obj : parameterTypes){
                if(obj == HttpServletRequest.class){
                    paramList.add(request);
                }
                if(obj == HttpServletResponse.class){
                    paramList.add(response);
                }
                if(obj == ReqData.class){
                    paramList.add(reqData);
                }
                //将参数值提供给实际的接收对象中（也就是ReqData的子类）
                if(ReqData.class.isAssignableFrom(obj)){
                    Map<String, String> dataMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
                    Map<String, String> fullMap = BeanUtil.beanToMap(reqData);
                    fullMap.putAll(dataMap);
                    Object fullObject = BeanUtil.mapTobean(fullMap, obj);
                    // 表单验证
                    String validMsg = ValidatorUtil.validate(fullObject);
                    if(StringUtils.isNotBlank(validMsg)){
                        return CommResult.fail(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), validMsg);
                    }
                    // 把对象传给具体的Service
                    paramList.add(fullObject);
                }
            }
            //目前要求该方法参数，不能使用非这三种的
            if(paramList.size() != parameterTypes.length){
                throw new SeedException(CodeEnum.SYSTEM_BUSY);
            }
            return method.invoke(this.getBean(reqData.getAppid()), paramList.toArray());
        } catch (Throwable t) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY, t);
        }
    }


    /**
     * 获取某个appid指定的接口方法名对应的java.lang.reflect.Method对象
     * @param appid  客户端的appid
     * @param method 客户端请求的方法名
     */
    private Method getMethod(String appid, String methodName){
        String methodKey = appid + "-" + methodName;
        if(methodMap.containsKey(methodKey)){
            return methodMap.get(methodKey);
        }
        Method method = null;
        //获取各个appid对应的业务实现类
        Class<?> serviceClazz = this.getBean(appid).getClass();
        //迭代本类中的所有public、protected、default、private的方法（不包括父类中的）
        for(Method obj : serviceClazz.getDeclaredMethods()){
            OpenMethod openMethod = obj.getAnnotation(OpenMethod.class);
            //null表示该方法未标注OpenMethod注解
            if(null == openMethod){
                continue;
            }
            //缓存起来
            methodMap.put(appid + "-" + methodName, obj);
            //记录入参中的接口方法所对应的Method，以便于缓存所有Method之后，将之返回
            if(StringUtils.equalsAny(methodName, openMethod.value(), openMethod.methodName())){
                method = obj;
            }
        }
        if(null == method){
            //本类中未定义，那就到父类去找一下，找不到再抛异常
            method = methodMap.get(appidParent + "-" + methodName);
            if(null == method){
                throw new SeedException(CodeEnum.OPEN_UNGRANT_API);
            }
        }
        return method;
    }


    /**
     * 根据appid获取对应的业务处理类对象
     */
    private Object getBean(String appid){
        try{
            return SpringContextHolder.getBean("routerService" + appid);
        }catch(NoSuchBeanDefinitionException e){
            //org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'routerService971' available
            throw new SeedException(CodeEnum.OPEN_APPID_NO_IMPL);
        }
    }
}