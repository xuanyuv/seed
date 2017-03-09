package com.jadyer.seed.boot;

import com.jadyer.seed.comm.util.LogUtil;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * 這裡參考了https://github.com/ulisesbocchio/jasypt-spring-boot
 * ----------------------------------------------------------------------------------------------
 * 下面演示一下Windows中的加解密
 * 根據http://www.jasypt.org/cli.html提示到http://www.jasypt.org/download.html下載到jasypt-1.9.2-dist.zip
 * 解壓jasypt-1.9.2-dist.zip得到文件夾，并進入目錄為C:\Users\Jadyer\Desktop\jasypt-1.9.2\bin的命令行窗口
 * encrypt.bat input=xuanyu password=jadyer stringOutputType=hexadecimal
 * decrypt.bat input=6C56AD5618744AFF9DEE50763D8D17D8 password=jadyer stringOutputType=hexadecimal
 * 注意：每一次生成的密文都是不同的，但都可以解密成xuanyu
 * ----------------------------------------------------------------------------------------------
 * 关于实现EnvironmentAware.java接口
 * setEnvironment()会在系统启动时被执行，可以通过它获取到系统环境变量和application配置文件中的变量
 * 然后就可以通过environment.getProperty()等方法获取系统环境变量或application配置文件中的变量
 * ----------------------------------------------------------------------------------------------
 * 关于@PropertySource注解
 * 它可以从配置文件中，读取对应的key-value，然后会放到Environment
 * 它可以读取'键值对'的配置文件，且支持读取多个配置文件中的属性值，读取后会放到Environment中
 * ----------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/14 13:22.
 */
@Configuration
@ConditionalOnClass(StringEncryptor.class)
@PropertySource(value={"${jasypt.file:classpath:config/encrypted.properties}"}, ignoreResourceNotFound=true)
public class JasyptConfiguration implements EnvironmentAware {
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    private String getProperty(Environment environment, String key, String defaultValue){
        if(null == environment.getProperty(key)){
            LogUtil.getLogger().info("Encryptor config not found for property {}, using default value: {}", key, defaultValue);
        }
        return environment.getProperty(key, defaultValue);
    }


    /**
     * <p>
     * BeanPostProcessorBeanFactoryPostProcessor區別可參考http://www.shouce.ren/api/spring2.5/ch03s07.html<br/>
     * 大致就是BeanFactoryPostProcessor用於Spring註冊BeanDefinition之後，實例化BeanDefinition之前，可修改Bean配置<br/>
     * 比如常見的配置數據庫連接池dataSource，用戶名和密碼放在獨立的一個配置文件中，然後用${jdbc.name}引用裏面的配置
     * </p>
     */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public BeanFactoryPostProcessor propertySourcesPostProcessor(){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        //Master Password used for Encryption/Decryption of properties.
        config.setPassword(this.getProperty(this.environment, "jasypt.encryptor.password", "https://jadyer.github.io/"));

        //Encryption/Decryption Algorithm to be used by Jasypt.
        //For more info on how to get available algorithms visit: <a href="http://www.jasypt.org/cli.html"/>Jasypt CLI Tools Page</a>.
        config.setAlgorithm(this.getProperty(this.environment, "jasypt.encryptor.algorithm", "PBEWithMD5AndDES"));

        //Number of hashing iterations to obtain the signing key.
        config.setKeyObtentionIterations(this.getProperty(this.environment, "jasypt.encryptor.keyObtentionIterations", "1000"));

        //The size of the pool of encryptors to be created.
        config.setPoolSize(this.getProperty(this.environment, "jasypt.encryptor.poolSize", "1"));

        //The name of the {@link java.security.Provider} implementation to be used by the encryptor for obtaining the encryption algorithm.
        config.setProviderName(this.getProperty(this.environment, "jasypt.encryptor.providerName", "SunJCE"));

        //A {@link org.jasypt.salt.SaltGenerator} implementation to be used by the encryptor.
        config.setSaltGeneratorClassName(this.getProperty(this.environment, "jasypt.encryptor.saltGeneratorClassname", "org.jasypt.salt.RandomSaltGenerator"));

        //Specify the form in which String output will be encoded. {@code "base64"} or {@code "hexadecimal"}.
        config.setStringOutputType(this.getProperty(this.environment, "jasypt.encryptor.stringOutputType", "hexadecimal"));

        encryptor.setConfig(config);
        return new EnableEncryptablePropertySourcesPostProcessor(encryptor);
    }


    private class EnableEncryptablePropertySourcesPostProcessor implements BeanFactoryPostProcessor {
        private StringEncryptor encryptor;
        EnableEncryptablePropertySourcesPostProcessor(StringEncryptor encryptor){
            this.encryptor = encryptor;
        }
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            MutablePropertySources propertySources = ((ConfigurableEnvironment)environment).getPropertySources();
            for(org.springframework.core.env.PropertySource<?> obj : propertySources){
                if(obj instanceof ResourcePropertySource){
                    propertySources.replace(obj.getName(), new PropertySourceWrapper((ResourcePropertySource)obj));
                }
            }
        }
        private class PropertySourceWrapper extends MapPropertySource {
            PropertySourceWrapper(ResourcePropertySource propertySource) {
                super(propertySource.getName(), propertySource.getSource());
            }
            @Override
            public Object getProperty(String name) {
                Object value = super.getProperty(name);
                if(value instanceof String){
                    String stringValue = String.valueOf(value);
                    //查看isEncryptedValue()源碼可看到它是根據ENC()判斷是否需要解密的
                    if(PropertyValueEncryptionUtils.isEncryptedValue(stringValue)){
                        value = PropertyValueEncryptionUtils.decrypt(stringValue, encryptor);
                    }
                }
                //return super.getProperty(name);
                return value;
            }
        }
    }
}