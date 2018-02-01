package com.jadyer.seed.boot;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RestTemplate配置
 * -------------------------------------------------------------------------------------
 * 启用该配置后，就可以在其它地方直接注入RestTemplate，如下所示
 * @Service
 * public class FileService {
 *     @Resource
 *     private RestTemplate restTemplate;
 * }
 * -------------------------------------------------------------------------------------
 * 如果服务端输出的是普通的http+json接口，可以封装成SDK给客户端调用，代码如下
 * 此时SDK中包含两个类：IFSService.java以及IFSServiceImpl.java
 * public interface IFSService {
 *     String upload(String bizCode, String filename, InputStream is);
 * }
 * public class IFSServiceImpl implements IFSService {
 *     private String host;
 *     private RestTemplate restTemplate;
 *     public IFSServiceImpl(String host, RestTemplate restTemplate){
 *         this.host = host;
 *         this.restTemplate = restTemplate;
 *     }
 *     @Override
 *     public String upload(String bizCode, String filename, InputStream is){
 *         File tmpFile = new java.io.File(System.getProperty("java.io.tmpdir") + filename);
 *         try{
 *             org.apache.commons.io.FileUtils.copyInputStreamToFile(is, tmpFile);
 *         }catch(IOException e){
 *             throw new RuntimeException("无效的文件流", e);
 *         }
 *         MultiValueMap<String, Object> paramMap = new org.springframework.util.LinkedMultiValueMap<String, Object>();
 *         paramMap.add("bizCode", bizCode);
 *         paramMap.add("fileData", new org.springframework.core.io.FileSystemResource(tmpFile));
 *         String result = this.restTemplate.postForObject(host+"/file/upload", paramMap, String.class);
 *         tmpFile.delete();
 *         return result
 *     }
 * }
 * 这个时候客户端调用服务端，就可以用下面这种写法（即此时客户端只需要RestTemplateConfiguration.java就行）
 * @Component
 * public class RemotingConfig {
 *     @Value("${host.ifs}")
 *     private String ifsHost;
 *     @Value("${host.qss}")
 *     private String qssHost;
 *     @Resource
 *     private RestTemplate restTemplate;
 *     @Bean
 *     public IFSService getIFSService(){
 *         return new IFSServiceImpl(this.ifsHost, this.restTemplate);
 *     }
 *     @Bean
 *     public QSSService getQSSService(){
 *         return new QSSServiceImpl(this.qssHost, this.restTemplate);
 *     }
 * }
 * @Service
 * public class FileService {
 *     @Resource
 *     private IFSService ifsService;
 *     @Resource
 *     private QSSService qssService;
 * }
 * 不过有个些缺点
 * 客户端若需要调用this.ifsService和this.qssService
 * 这种情况下没有办法单独针对某个Service设置超时时间等参数
 * 因为这时候所有的Http连接池都是由this.restTemplate统一管理的
 * -------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2016/11/21 17:47.
 */
//@Configuration
@ConditionalOnClass({RestTemplate.class, HttpClient.class})
public class RestTemplateConfiguration {
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

    private ClientHttpRequestFactory createFactory(){
        if(this.maxTotalConnect <= 0){
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(this.connectTimeout);
            factory.setReadTimeout(this.readTimeout);
            return factory;
        }
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(this.maxTotalConnect)
                .setMaxConnPerRoute(this.maxConnectPerRoute)
                .evictExpiredConnections().evictIdleConnections(this.maxIdleTime, TimeUnit.MILLISECONDS)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(this.connectTimeout);
        factory.setReadTimeout(this.readTimeout);
        return factory;
    }


    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate getRestTemplate(){
        RestTemplate restTemplate = new RestTemplate(this.createFactory());
        //解决中文乱码问题
        //由于RestTemplate.postForObject()使用的StringHttpMessageConverter默认编码是ISO-8859-1，所以中文会乱码
        //所以我们要移除默认的StringHttpMessageConverter，再添加新的由UTF-8编码的StringHttpMessageConverter
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> converterTarget = null;
        for(HttpMessageConverter<?> item : converterList){
            if(StringHttpMessageConverter.class == item.getClass()){
                converterTarget = item;
                break;
            }
        }
        if(null != converterTarget){
            converterList.remove(converterTarget);
        }
        converterList.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        //由于converterList是restTemplate对象的全局变量的引用
        //所以不用restTemplate.setMessageConverters(converterList);
        return restTemplate;
    }
}