package com.jadyer.seed.boot;

import com.jadyer.seed.comm.util.LogUtil;
import com.xxl.job.core.executor.XxlJobExecutor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * XXL-Job执行器配置
 * -------------------------------------------------------------------------------------------------------------
 * 执行器作用：接收“调度中心”的调度并执行（执行器可直接部署，也可将其集成到现有业务项目中）
 * -------------------------------------------------------------------------------------------------------------
 * 【实战】
 * 1、下载源码https://github.com/xuxueli/xxl-job，后打包得到xxl-job-admin.war，并部署至Tomcat
 * 2、部署执行器（springboot的执行器在集成到现有业务项目中时，它监听的端口不能与现有业务项目端口相同，否则会端口绑定失败）
 * 部署完执行器，http://127.0.0.1:8080/xxl-job-admin/jobgroup执行器管理界面没有显示我们部署的执行器
 * 所以对于首次操作，需要先手工增加执行器（注册方式选择自动注册），过一小会儿就会看到执行器的机器地址显示进来了
 * -------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2018/1/8 16:56.
 */
//@Configuration
@ConfigurationProperties(prefix=XxlJobConfiguration.PREFIX)
public class XxlJobConfiguration {
    static final String PREFIX = "xxljob";
    //调度中心部署地址（执行器将会使用该地址进行“执行器心跳注册”和“任务结果回调”。若调度中心集群部署存在多个地址，则用半角逗号分隔）
    private String adminAddresses;
    //执行器地址信息包含appname、ip、port，它是执行器心跳注册分组的依据（地址信息用于“调度中心请求并触发任务”和“执行器注册”）
    private String executorAppname;
    //执行器IP（空表示自动获取IP。多网卡时可手动设置指定IP，手动设置IP时将会绑定Host）
    //private String executorIp;
    //执行器端口（未配置则默认为9999。单机部署多个执行器时，注意要配置不同执行器端口）
    private int executorPort;
    //执行器通讯TOKEN（非空时启用）
    //private String executorAccessToken;
    //执行器运行日志文件存储的磁盘位置（需要对该路径拥有读写权限）
    private String executorLogPath;
    //执行器Log文件定期清理功能，指定日志保存天数，日志文件过期自动删除（限制至少保持3天，否则功能不生效）
    private int executorLogRetentionDays;

    @Bean(initMethod="start", destroyMethod="destroy")
    public XxlJobExecutor xxlJobExecutor() {
        LogUtil.getLogger().info("XXL-Job 配置初始化...");
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setAdminAddresses(adminAddresses);
        xxlJobExecutor.setAppName(executorAppname);
        xxlJobExecutor.setIp("127.0.0.1");
        xxlJobExecutor.setPort(executorPort);
        //xxlJobExecutor.setAccessToken(accessToken);
        xxlJobExecutor.setLogPath(executorLogPath);
        xxlJobExecutor.setLogRetentionDays(executorLogRetentionDays);
        return xxlJobExecutor;
    }

    public String getAdminAddresses() {
        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public String getExecutorAppname() {
        return executorAppname;
    }

    public void setExecutorAppname(String executorAppname) {
        this.executorAppname = executorAppname;
    }

    public int getExecutorPort() {
        return executorPort;
    }

    public void setExecutorPort(int executorPort) {
        this.executorPort = executorPort;
    }

    public String getExecutorLogPath() {
        return executorLogPath;
    }

    public void setExecutorLogPath(String executorLogPath) {
        this.executorLogPath = executorLogPath;
    }

    public int getExecutorLogRetentionDays() {
        return executorLogRetentionDays;
    }

    public void setExecutorLogRetentionDays(int executorLogRetentionDays) {
        this.executorLogRetentionDays = executorLogRetentionDays;
    }
}