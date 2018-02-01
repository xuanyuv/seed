package com.jadyer.seed.boot.remoting.client;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.remoting.httpinvoker.HttpComponentsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 用于创建SpringHttpInvoker服务的客户端实例的帮助类
 * -------------------------------------------------------------------------------------
 * 关于客户端调用服务端的SpringHttpInvoker服务，共有以下两种方式
 * 其中host.ifs是配置在application.yml中的，或在properties中然后被@PropertySource注解读取到
 * -------------------------------------------------------------------------------------
 * @Service
 * public class FileService {
 *     @RemoteClient("${host.ifs}")
 *     private IFSService ifsService;
 * }
 * -------------------------------------------------------------------------------------
 * @Component
 * public class RemotingConfig {
 *     @Value("${host.ifs}")
 *     private String ifsHost;
 *     @Resource
 *     private RemoteClientBuilder remoteClientBuilder;
 *     @Bean
 *     public IFSService getIFSService(){
 *         return this.remoteClientBuilder.build(IFSService.class, this.ifsHost);
 *     }
 * }
 * @Service
 * public class FileService {
 *     @Resource
 *     private IFSService ifsService;
 * }
 * -------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2016/11/21 16:36.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 2)
public class RemoteClientBuilder implements EnvironmentAware {
    /** 连接池的最大连接数 */
    @Value("${remote.maxTotalConnect:0}")
    private int maxTotalConnect;
    /** 单个主机的最大连接数 */
    @Value("${remote.maxConnectPerRoute:200}")
    private int maxConnectPerRoute;
    /** 连接超时默认2s */
    @Value("${remote.connectTimeout:2000}")
    private int connectTimeout;
    /** 读取超时默认30s */
    @Value("${remote.readTimeout:30000}")
    private int readTimeout;
    /** 连接池剔除空闲连接的间隔时间默认10s */
    @Value("${remote.maxIdleTime:10000}")
    private int maxIdleTime;
    private Environment environment;
    private HttpInvokerRequestExecutor httpInvokerRequestExecutor;
    private final Map<String, Object> clientMap = new ConcurrentHashMap<>();

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    private HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
        if(null != this.httpInvokerRequestExecutor){
            return this.httpInvokerRequestExecutor;
        }
        //maxTotalConnect不大于零就使用Simple模式（JavaSE标准API）
        if(this.maxTotalConnect <= 0){
            SimpleHttpInvokerRequestExecutor executor = new SimpleHttpInvokerRequestExecutor();
            executor.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
            executor.setConnectTimeout(this.connectTimeout);
            executor.setReadTimeout(this.readTimeout);
            this.httpInvokerRequestExecutor = executor;
            return executor;
        }
        //maxTotalConnect大于零就使用HttpComponents
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager poolingManager = new PoolingHttpClientConnectionManager(registry);
        poolingManager.setMaxTotal(this.maxTotalConnect);
        poolingManager.setDefaultMaxPerRoute(this.maxConnectPerRoute);
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(poolingManager)
                .evictExpiredConnections().evictIdleConnections(this.maxIdleTime, TimeUnit.MILLISECONDS)
                .build();
        //HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(this.maxTotalConnect).setMaxConnPerRoute(this.maxConnectPerRoute).build();
        HttpComponentsHttpInvokerRequestExecutor executor = new HttpComponentsHttpInvokerRequestExecutor();
        executor.setHttpClient(httpClient);
        executor.setConnectTimeout(this.connectTimeout);
        executor.setReadTimeout(this.readTimeout);
        this.httpInvokerRequestExecutor = executor;
        return executor;
    }


    /**
     * 创建远程服务的客户端实例
     * @param serviceInterface 远程服务接口的Class
     * @param serviceUrl       远程服务接口的地址，若传入'${host.ifs}'则会从系统变量中取值
     */
    public <T> T build(Class<T> serviceInterface, String serviceUrl) {
        Object value;
        synchronized(this.clientMap){
            //获取含包名的类名字符串
            //serviceInterface.getName()得到的结果就是：com.jadyer.service.IFSService
            //serviceInterface.getSimpleName()得到的结果就是：IFSService
            String key = serviceInterface.getName();
            //从缓存中获取@RemoteClient的实例
            value = this.clientMap.get(key);
            if(null == value){
                //计算URL（主要处理入参为'${}'形式的url，把它替换成环境变量里面配置的值）
                serviceUrl = serviceUrl.trim();
                if(serviceUrl.startsWith("${") && serviceUrl.endsWith("}")){
                    serviceUrl = serviceUrl.substring(2, serviceUrl.length()-1);
                    serviceUrl = this.environment.getProperty(serviceUrl);
                }
                //补全URL（得到的结果比如：http://1.1.1.1/remoting/IFSService）
                //这里的'remoting'和'IFSService'都是@RemoteService注解的属性值
                //'remoting'和'IFSService'的拼接方式是由服务端的RemoteServiceScannerRegistrar.java决定的
                String name = serviceInterface.getSimpleName();
                if(!serviceUrl.endsWith(name)){
                    if(serviceUrl.endsWith("/")){
                        serviceUrl = serviceUrl + name;
                    }else{
                        serviceUrl = serviceUrl + "/" + name;
                    }
                }
                //访问RMI服务时，需要声明一个指向服务的RmiProxyFactoryBean
                //访问Burlap服务时，需要声明一个指向服务的BurlapProxyFactoryBean
                //访问Hessian服务时，需要声明一个指向服务的HessianProxyFactoryBean
                //同样，访问httpinvoker服务时，也需要声明一个指向服务的HttpInvokerProxyFactoryBean，它用来代理所公开的服务
                HttpInvokerProxyFactoryBean factory = new HttpInvokerProxyFactoryBean();
                factory.setServiceUrl(serviceUrl);
                factory.setServiceInterface(serviceInterface);
                //默认情况下，HttpInvokerPropxy会使用Simple模式（JavaSE标准API）
                //可以通过设置setHttpInvokerRequestExecutor()属性来使用HttpComponents
                factory.setHttpInvokerRequestExecutor(this.getHttpInvokerRequestExecutor());
                factory.afterPropertiesSet();
                value = factory.getObject();
                //缓存@RemoteClient的实例
                this.clientMap.put(key, value);
            }else{
                System.out.println("Build remote client class:" + serviceInterface.getName() + " from cache");
            }
        }
        //noinspection unchecked
        return (T) value;
    }
}