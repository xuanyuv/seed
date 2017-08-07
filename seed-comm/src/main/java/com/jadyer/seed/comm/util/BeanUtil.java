package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean工具类
 * @version v1.3
 * @history v1.3-->mapTobean()方法增加除String外的int和long类型支持
 * @history v1.2-->增加mapTobean()方法
 * @history v1.1-->增加beanToMap()方法
 * @history v1.0-->初建
 * Created by 玄玉<http://jadyer.cn/> on 2017/5/18 17:22.
 */
public final class BeanUtil {
    private BeanUtil(){}

    /**
     * HttpServletRequest参数值转为JavaBean
     * @see 该方法目前只能处理所有属性均为String的JavaBean
     * @see 且只能处理当前类,暂不能处理父类和子类
     * @see 且类属性只能是String
     */
    public static <T> T requestToBean(HttpServletRequest request, Class<T> beanClass){
        try{
            T bean = beanClass.newInstance();
            //getFields()能获取到父类和子类中所有public的属性
            //getDeclaredFields()能获取到类的所有属性（不受访问权限控制，也不包括父类）
            for(Field field : beanClass.getDeclaredFields()){
                //构造setter方法
                String methodName = "set" + StringUtils.capitalize(field.getName());
                try{
                    //执行setter方法
                    String fieldValue = request.getParameter(field.getName());
                    fieldValue = null==fieldValue ? "" : fieldValue;
                    beanClass.getMethod(methodName, String.class).invoke(bean, URLDecoder.decode(fieldValue, "UTF-8"));
                }catch(Exception e){
                    //ignore exception
                }
            }
            return bean;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    /**
     * 采用反射实现的JavaBean属性拷贝
     * <ul>
     *     <li>实测拷贝效率由低到高依次为（最快的目前是cglib，也是推荐采用的）</li>
     *     <li>net.sf.cglib.beans.BeanCopier.copy()</li>
     *     <li>org.springframework.beans.BeanUtils.copyProperties()</li>
     *     <li>com.jadyer.seed.comm.util.BeanUtil.copyProperties()</li>
     *     <li>org.apache.commons.beanutils.BeanUtils.copyProperties()</li>
     * </ul>
     * <p>
     *     曾试过优化一下BeanCopier，把这个对象放到全局的ConcurrentHashMap<String, BeanCopier>里面<br>
     *     放进去的beanCopier对象就是BeanCopier.create(source.getClass(), target.getClass(), false)<br>
     *     意味着只要是从相同的source拷贝属性给target，就不用每次create()，而是直接从ConcurrentHashMap中取<br>
     *     不过测试发现，放到ConcurrentHashMap之后的效率反倒不如每次都BeanCopier.create()，以后有时间再研究吧
     * </p>
     * <p>
     *     另外，这里自己写的反射获取Setter有点复杂了好像，可以参考上面的requestToBean()方法<br>
     *     以后真正用的时候再改，今天没心情......
     * </p>
     */
    public static <T, E> void copyProperties(T source, E target){
        //采用Cglib实现
        //net.sf.cglib.beans.BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
        //beanCopier.copy(source, target, null);
        //采用Spring实现
        //org.springframework.beans.BeanUtils.copyProperties(source, target);
        //采用反射实现
        Method[] sourceMethods = source.getClass().getDeclaredMethods();
        Method[] targetMethods = target.getClass().getDeclaredMethods();
        for(Method sourceMethod : sourceMethods){
            if(sourceMethod.getName().startsWith("get") || sourceMethod.getName().startsWith("is")){
                //得到源对象的Setter
                String sourceFieldName;
                if(sourceMethod.getName().startsWith("get")){
                    sourceFieldName = "set" + sourceMethod.getName().substring(3);
                }else{
                    sourceFieldName = "set" + sourceMethod.getName().substring(2);
                }
                for(Method targetMethod : targetMethods){
                    if(targetMethod.getName().equals(sourceFieldName)){
                        //参数类型和返回类型判断
                        if(sourceMethod.getReturnType().isAssignableFrom(targetMethod.getParameterTypes()[0])){
                            try {
                                targetMethod.invoke(target, sourceMethod.invoke(source));
                                break;
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("属性拷贝失败", e);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Bean属性转为Map（key=属性名，value=属性值）
     * <p>
     *     另附注意事项：http://jadyer.cn/2013/09/24/spring-introspector-cleanup-listener/
     * </p>
     */
    public static Map<String, String> beanToMap(Object bean) {
        if(null == bean){
            return new HashMap<>();
        }
        try {
            Map<String, String> dataMap = new HashMap<>();
            //得到Bean的属性、暴露的方法和事件
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            //得到属性描述
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for(PropertyDescriptor obj : propertyDescriptors){
                //得到属性名
                String propertyName = obj.getName();
                //过滤class属性
                if(!"class".equals(propertyName)){
                    ////获得setter
                    //Method setterMethod = obj.getWriteMethod();
                    //获得并执行getter
                    Object result = obj.getReadMethod().invoke(bean);
                    //放入Map
                    if(null != result){
                        dataMap.put(propertyName, result.toString());
                    }
                }
            }
            return dataMap;
        }catch(Exception e){
            throw new RuntimeException("beanToMap发生异常，堆栈轨迹如下", e);
        }
    }


    /**
     * Map转为Bean（根据key找到属性名，再将value作为属性值）
     * <ul>
     *     <li>注意：Bean的属性值目前只支持String、int、long，其它未做兼容</li>
     *     <li>另附注意事项：http://jadyer.cn/2013/09/24/spring-introspector-cleanup-listener/</li>
     * </ul>
     */
    public static <T> T mapTobean(Map<String, String> dataMap, Class<T> beanClass) {
        try{
            T bean = beanClass.newInstance();
            if(null==dataMap || dataMap.isEmpty()){
                return bean;
            }
            //得到Bean的属性、暴露的方法和事件
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            //得到属性描述
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for(PropertyDescriptor obj : propertyDescriptors){
                //得到属性名
                String propertyName = obj.getName();
                if(dataMap.containsKey(propertyName)){
                    //获得并执行setter
                    switch(obj.getPropertyType().getName()){
                        case "int"  : obj.getWriteMethod().invoke(bean, Integer.parseInt(dataMap.get(propertyName))); break;
                        case "long" : obj.getWriteMethod().invoke(bean, Long.parseLong(dataMap.get(propertyName)));   break;
                        default: obj.getWriteMethod().invoke(bean, dataMap.get(propertyName));
                    }
                }
            }
            return bean;
        }catch(Exception e){
            throw new RuntimeException("mapTobean发生异常，堆栈轨迹如下", e);
        }
    }
}