package com.jadyer.seed.controller.elasticjob;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.jadyer.seed.comm.util.LogUtil;

/**
 * -----------------------------------------------------------------------------------
 * 注意：该监听器为每台作业节点均执行的监听，即此时不需要考虑全局分布式任务是否完成
 * 若需要分布式场景中仅单一节点执行的监听，详见http://elasticjob.io/docs/elastic-job-lite/02-guide/job-listener/
 * -----------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2018/1/3 15:43.
 */
public class MyListener implements ElasticJobListener {
    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        LogUtil.getLogger().info("监听到作业执行之前的事件--->>");
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        LogUtil.getLogger().info("监听到作业执行之后的事件<<---");
    }
}