package com.jadyer.seed.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 读取配置文件的一种方式
 * -----------------------------------------------------------------------------------------------------------
 * 默认会到application.properties或application.yml中找配置属性，也可通过locations属性指定配置文件
 * @ConfigurationProperties(prefix=BootProperties.PREFIX, locations="classpath:config.properties")
 * -----------------------------------------------------------------------------------------------------------
 * 关于BootProperties的使用，有两种方式
 * 二者不同就在于BootProperties类上面有没有标注@Component
 * 没有的话就需要在使用的地方通过@EnableConfigurationProperties(BootProperties.class)声明一下才行
 * 注意：两个方式都需要@Resource或者@Autowired引入BootProperties对象
 * 1.@Component
 *   @ConfigurationProperties(prefix=BootProperties.PREFIX)
 *   public class BootProperties{}
 *   然后在需要使用的Controller中通过private @Resource BootProperties bootProperties即可
 * 2.@ConfigurationProperties(prefix=BootProperties.PREFIX)
 *   public class BootProperties{}
 *   然后在需要使用的Controller中通过以下方式使用
 *   @RestController
 *   @RequestMapping(value="/api")
 *   @EnableConfigurationProperties(BootProperties.class)
 *   public class DemoController{private @Resource BootProperties bootProperties}即可
 * -----------------------------------------------------------------------------------------------------------
 * 关于List的获取
 * 属性名必须与配置文件中的相同（比如这里的addressList）
 * 它可以不提供setter，但必须提供getter，且必须为public（即便该属性只给自己类里面被调用）
 * 注意：只是List、Map等等是这样，其它类型的属性必须同时提供setter和getter
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2015/11/29 15:30.
 */
@Component
@ConfigurationProperties(prefix=BootProperties.PREFIX)
public class BootProperties {
    static final String PREFIX = "scan.base";
    private String packages;
    private String detailInfo;
    private String secretName;
    private List<String> addressList = new ArrayList<>();

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public String getSecretName() {
        return secretName;
    }

    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getDetailInfo() {
        return detailInfo;
    }
    public List<String> getAddressList() {
        return addressList;
    }
}