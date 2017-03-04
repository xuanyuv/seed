package com.jadyer.seed.open.util;

import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/8 19:52.
 */
public final class OpenUtil {
    private OpenUtil() {}

    /**
     * 字符串转为字节数组
     * <p>该方法默认以ISO-8859-1转码,若想自己指定字符集,可以调用{@link #getBytes(String,String)}</p>
     */
    public static byte[] getBytes(String data) {
        return getBytes(data, "ISO-8859-1");
    }


    /**
     * 字符串转为字节数组
     * <p>如果系统不支持所传入的{@code charset}字符集,则按照系统默认字符集进行转换</p>
     */
    public static byte[] getBytes(String data, String charset) {
        data = (null == data ? "" : data);
        if (StringUtils.isBlank(charset)) {
            return data.getBytes();
        }
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            LogUtil.getAppLogger().error("将字符串[" + data + "]转为byte[]时发生异常:系统不支持该字符集[" + charset + "]");
            return data.getBytes();
        }
    }


    /**
     * 获取Map中的属性
     * <p>
     * 由于Map.toString()打印出来的参数值对,是横着一排的...参数多的时候,不便于查看各参数值<br/>
     * 故此仿照commons-lang3.jar中的ReflectionToStringBuilder.toString()编写了本方法
     * </p>
     * @return String key11=value11 \n key22=value22 \n key33=value33 \n......
     */
    public static String buildStringFromMap(Map<String, String> map){
        if(null==map || map.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(map.getClass().getName()).append("@").append(map.hashCode()).append("[");
        for(Map.Entry<String,String> entry : map.entrySet()){
            sb.append("\n").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.append("\n]").toString();
    }


    /**
     * 获取Map中的属性
     * <p>
     * 该方法的参数适用于打印{@code Map<String, String[]>}的情况<br/>
     * 由于{@code Map.toString()}打印出来的参数值对,是横着一排的...参数多的时候,不便于查看各参数值<br/>
     * 故此仿照commons-lang3.jar中的{@code ReflectionToStringBuilder.toString()}编写了本方法
     * </p>
     * @return String key11=value11 \n key22=value22 \n key33=value33 \n......
     */
    public static String buildStringFromMapWithStringArray(Map<String, String[]> map) {
        if(null==map || map.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(map.getClass().getName()).append("@").append(map.hashCode()).append("[");
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            sb.append("\n").append(entry.getKey()).append("=").append(Arrays.toString(entry.getValue()));
        }
        return sb.append("\n]").toString();
    }


    /**
     * HttpServletRequest参数值转为JavaBean
     * <p>
     * 该方法目前只能处理所有属性均为String的JavaBean<br/>
     * 且只能处理当前类,暂不能处理父类和子类,且类属性只能是String
     * </p>
     * @create Dec 17, 2015 4:44:47 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static <T> T requestToBean(HttpServletRequest request, Class<T> beanClass) {
        try {
            T bean = beanClass.newInstance();
            // getFields()能获取到父类和子类中所有public的属性
            for (Field field : beanClass.getDeclaredFields()) {
                // 构造setter方法
                String methodName = "set" + StringUtils.capitalize(field.getName());
                try {
                    // 执行setter方法
                    beanClass.getMethod(methodName, String.class).invoke(bean, URLDecoder.decode(request.getParameter(field.getName()), "UTF-8"));
                } catch (Exception e) {
                    // ignore exception
                }
            }
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}