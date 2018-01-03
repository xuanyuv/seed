package com.jadyer.seed.controller.elasticjob;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/12/11 16:39.
 */
@Component
public class JobDemo {
    @PostConstruct
    public void ElasticJobStart(){
        //new JobScheduler(createRegistryCenter(), createJobConfiguration(1)).init();
        new JobScheduler(createRegistryCenter(), createJobConfiguration(2), new MyListener()).init();
    }


    private static CoordinatorRegistryCenter createRegistryCenter(){
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("127.0.0.1:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }


    /**
     * 创建作业配置
     * @param type 1--简单作业，2--数据流作业
     */
    private static LiteJobConfiguration createJobConfiguration(int type) {
        if(1 == type){
            //定义作业核心配置
            JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder("MySimpleJob", "0/10 * * * * ?", 6).build();
            //定义SIMPLE类型配置
            SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig, MySimpleJob.class.getCanonicalName());
            //定义Lite作业根配置
            //return LiteJobConfiguration.newBuilder(simpleJobConfig).monitorPort(9888).build();
            return LiteJobConfiguration.newBuilder(simpleJobConfig).overwrite(true).build();
        }
        //定义作业核心配置
        JobCoreConfiguration dataflowCoreConfig = JobCoreConfiguration.newBuilder("MyDataflowJob", "0/15 * * * * ?", 5).build();
        //定义DATAFLOW类型配置
        DataflowJobConfiguration dataflowJobConfig = new DataflowJobConfiguration(dataflowCoreConfig, MyDataflowJob.class.getCanonicalName(), true);
        //定义Lite作业根配置
        //return LiteJobConfiguration.newBuilder(dataflowJobConfig).monitorPort(9888).build();
        return LiteJobConfiguration.newBuilder(dataflowJobConfig).overwrite(true).build();
    }
}