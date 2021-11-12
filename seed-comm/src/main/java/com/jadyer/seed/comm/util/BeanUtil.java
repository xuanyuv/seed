package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean工具类
 * @version v1.4
 * @history v1.4-->copyProperties()增加返回值并修改内部实现为Spring#BeanUtils，同时增加copyPropertiesForList()
 * @history v1.3-->mapTobean()方法增加除String外的int和long类型支持
 * @history v1.2-->增加mapTobean()方法
 * @history v1.1-->增加beanToMap()方法
 * @history v1.0-->初建
 * Created by 玄玉<https://jadyer.cn/> on 2017/5/18 17:22.
 */
public final class BeanUtil {
    private BeanUtil(){}

    /**
     * JavaBean属性拷贝
     * -------------------------------------------------------------------------------------
     * 用法：List<User> userList = BeanUtil.copyPropertiesForList(userDataList, User.class)
     * -------------------------------------------------------------------------------------
     */
    public static <E, T> List<T> copyPropertiesForList(List<E> sourceList, Class<T> targetClass) {
        List<T> targetList = new ArrayList<>();
        for(E e : sourceList){
            targetList.add(copyProperties(e, targetClass));
        }
        return targetList;
    }


    /**
     * JavaBean属性拷贝
     * -------------------------------------------------------------------------------------
     * 用法：User user = BeanUtil.copyProperties(userData, User.class)
     * -------------------------------------------------------------------------------------
     * 1、cglib 和 Spring 就足够了，Apache的还是算了
     * 2、使用时注意目标类应public，若内部类则应为静态内部类
     * -------------------------------------------------------------------------------------
     */
    public static <E, T> T copyProperties(E source, Class<T> targetClass) {
        if(null == source){
            return null;
        }
        T target;
        try {
            target = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("copyProperties时发生异常：目标类非public（内部类则应static），堆栈轨迹如下", e);
        }
        ////采用Cglib实现（实际使用时可以在Service类中全局缓存BeanCopier对象）
        //net.sf.cglib.beans.BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
        //beanCopier.copy(source, target, null);
        //采用Spring实现
        org.springframework.beans.BeanUtils.copyProperties(source, target);
        ////采用反射实现
        //try {
        //    for (Method sourceMethod : source.getClass().getMethods()) {
        //        if (!sourceMethod.getName().startsWith("get") && !sourceMethod.getName().startsWith("is")) {
        //            continue;
        //        }
        //        //得到源对象的Setter
        //        String sourceFieldName;
        //        if (sourceMethod.getName().startsWith("get")) {
        //            sourceFieldName = "set" + sourceMethod.getName().substring(3);
        //        } else {
        //            sourceFieldName = "set" + sourceMethod.getName().substring(2);
        //        }
        //        try {
        //            targetClass.getMethod(sourceFieldName, sourceMethod.getReturnType()).invoke(target, sourceMethod.invoke(source));
        //        } catch (NoSuchMethodException e) {
        //            //ignore exception
        //        }
        //    }
        //} catch (IllegalAccessException | InvocationTargetException e) {
        //    throw new RuntimeException("copyProperties失败，堆栈轨迹如下", e);
        //}
        return target;
    }


    /**
     * JavaBean属性拷贝
     */
    public static <E, T> T copyProperties(E source, T target) {
        if(null == source){
            return null;
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target);
        return target;
    }


    /**
     * HttpServletRequest参数值转为JavaBean
     * -------------------------------------------------------------------------------------
     * 1、类属性只能是String
     * 2、只能处理当前类，暂不能处理父类和子类
     * -------------------------------------------------------------------------------------
     */
    public static <T> T requestToBean(HttpServletRequest request, Class<T> beanClass){
        try{
            T bean = beanClass.newInstance();
            //getFields()能获取到父类和子类中所有public的属性
            //getDeclaredFields()能获取到类的所有属性（不受访问权限控制，也不包括父类）
            for(Field field : beanClass.getDeclaredFields()){
                //构造setter方法
                String methodName = "set" + StringUtils.capitalize(field.getName());
                //执行setter方法
                String fieldValue = request.getParameter(field.getName());
                fieldValue = null==fieldValue ? "" : fieldValue;
                try {
                    beanClass.getMethod(methodName, String.class).invoke(bean, URLDecoder.decode(fieldValue, "UTF-8"));
                } catch (NoSuchMethodException e) {
                    //ignore exception
                }
            }
            return bean;
        }catch(Exception e){
            throw new RuntimeException("requestToBean发生异常，堆栈轨迹如下", e);
        }
    }


    /**
     * Bean属性转为Map（key=属性名，value=属性值）
     * -------------------------------------------------------------------------------------
     * 另附注意事项：https://jadyer.cn/2013/09/24/spring-introspector-cleanup-listener/
     * -------------------------------------------------------------------------------------
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
     * -------------------------------------------------------------------------------------
     * Bean的属性值目前只支持String、int、Integer、long、Long，其它未做兼容
     * 另附注意事项：https://jadyer.cn/2013/09/24/spring-introspector-cleanup-listener/
     * -------------------------------------------------------------------------------------
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
                        case "int"               :
                        case "java.lang.Integer" : obj.getWriteMethod().invoke(bean, Integer.parseInt(dataMap.get(propertyName))); break;
                        case "long"              :
                        case "java.lang.Long"    : obj.getWriteMethod().invoke(bean, Long.parseLong(dataMap.get(propertyName)));   break;
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