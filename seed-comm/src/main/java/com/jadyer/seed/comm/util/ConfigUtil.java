package com.jadyer.seed.comm.util;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 配置文件读取工具类
 * -----------------------------------------------------------------------------------------------------------
 * 用法为ConfigUtil.INSTANCE.getProperty("jdbc.url")
 * 采用枚举的方式，也是Effective Java作者Josh Bloch提倡的方式
 * 它不仅能避免多线程同步问题，而且还能防止反序列化重新创建新的对象
 * -----------------------------------------------------------------------------------------------------------
 * @version v2.1
 * @history v2.1-->增加<code>getPropertyBySysKey()</code>方法，用于获取配置文件的键值中含系统属性时的值
 * @history v2.0-->采用枚举的方式实现单例
 * @history v1.0-->通过内部类实现单例
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2012/6/7 17:30.
 */
public enum ConfigUtil {
    INSTANCE;

    private Properties config;

    ConfigUtil(){
        config = new Properties();
        try {
            //config.load(ConfigUtil.class.getResourceAsStream("/config-"+System.getProperty("appenv.active")+".properties"));
            config.load(ConfigUtil.class.getResourceAsStream("/config.properties"));
            LogUtil.getLogger().info("Load /config.properties SUCCESS...");
        } catch (IOException e) {
            LogUtil.getLogger().error("Load /config.properties Error...", e);
            throw new ExceptionInInitializerError("加载系统配置文件失败...");
        }
    }

    public String getProperty(String key){
        return config.getProperty(key);
    }

    public int getPropertyForInt(String key){
        return Integer.parseInt(config.getProperty(key));
    }

    /**
     * 配置文件的键值中含系统属性时的获取方式
     * <p>
     *     若配置文件的某个键值含类似于${user.dir}的写法，如log=${user.dir}/app.log
     *     则可以通过该方法使用系统属性中user.dir的值，替换掉配置文件键值中的${user.dir}
     * </p>
     */
    public String getPropertyBySysKey(String key){
        String value = config.getProperty(key);
        if(null!=value && Pattern.compile("\\$\\{\\w+(\\.\\w+)*}").matcher(value).find()){
            String sysKey = value.substring(value.indexOf("${")+2, value.indexOf("}"));
            value = value.replace("${"+sysKey+"}", System.getProperty(sysKey));
        }
        return value;
    }
}
/*
public class ConfigUtil {
    private Properties config;
    //内部类实现单例
    //这样既能实现延迟加载,减少内存开销,又无多线程问题
    //这是一个类级的内部类,即静态的成员式内部类,该内部类的实例与外部类的实例没有绑定关系
    //而且只有被调用时才会装载,从而实现了延迟加载
    private static class SingletonHolder{
        //静态初始化器，由JVM来保证线程安全
        private static ConfigUtil instance = new ConfigUtil();
    }
    //获取ConfigUtil的实例对象
    //当该方法第一次被调用的时,它第一次读取SingletonHolder.instance,导致SingletonHolder类得到初始化
    //而SingletonHolder类在装载并被初始化的时,会初始化它的静态域,从而创建ConfigUtil的实例
    //由于是静态的域,因此只会被虚拟机在装载类的时候初始化一次,并由虚拟机来保证它的线程安全性
    //这个模式的优势在于:getInstance()方法并没有被同步,并且只是执行一个域的访问,因此延迟初始化并没有增加任何访问成本
    public static ConfigUtil getInstance(){
        return SingletonHolder.instance;
    }
    //克隆方法（禁止通过克隆创建新的对象）
    @Override
    public ConfigUtil clone() {
        return getInstance();
    }
    private ConfigUtil(){
        config = new Properties();
        try {
            config.load(ConfigUtil.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            System.out.println("Load /config.properties Error....");
            e.printStackTrace();
            throw new ExceptionInInitializerError("加载系统配置文件失败....");
        }
    }
    public String getProperty(String key){
        return config.getProperty(key);
    }
}
*/