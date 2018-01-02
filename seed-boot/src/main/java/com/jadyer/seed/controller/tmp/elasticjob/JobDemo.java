package com.jadyer.seed.controller.tmp.elasticjob;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/12/11 16:39.
 */
public class JobDemo {
    /**
     * 创建作业配置
     * @param type 1--简单作业，2--数据流作业
     */
    private static LiteJobConfiguration createJobConfiguration(int type) {
        if(1 == type){
            //定义作业核心配置
            JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder("demoSimpleJob", "0/15 * * * * ?", 10).build();
            //定义SIMPLE类型配置
            SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig, JobDemo.class.getCanonicalName());
            //定义Lite作业根配置
            return LiteJobConfiguration.newBuilder(simpleJobConfig).build();
        }
        //定义作业核心配置
        JobCoreConfiguration dataflowCoreConfig = JobCoreConfiguration.newBuilder("demoDataflowJob", "0/30 * * * * ?", 10).build();
        //定义DATAFLOW类型配置
        DataflowJobConfiguration dataflowJobConfig = new DataflowJobConfiguration(dataflowCoreConfig, JobDemo.class.getCanonicalName(), true);
        //定义Lite作业根配置
        return LiteJobConfiguration.newBuilder(dataflowJobConfig).build();
    }


    private static CoordinatorRegistryCenter createRegistryCenter(){
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("zk_host:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }


    public static void main(String[] args) {
        new JobScheduler(createRegistryCenter(), createJobConfiguration(1)).init();
    }
}