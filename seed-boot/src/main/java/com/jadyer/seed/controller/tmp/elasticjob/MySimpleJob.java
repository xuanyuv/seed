package com.jadyer.seed.controller.tmp.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.jadyer.seed.comm.util.LogUtil;

/**
 * Simple类型作业
 * --------------------------------------------------------
 * 意为简单实现，未经任何封装的类型。需实现SimpleJob接口
 * 该接口仅提供单一方法用于覆盖，此方法将定时执行
 * 与Quartz原生接口相似，但提供了弹性扩缩容和分片等功能
 * --------------------------------------------------------
 */
public class MySimpleJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        LogUtil.getLogger().info("分片总数：[{}]", shardingContext.getShardingTotalCount());
        LogUtil.getLogger().info("运行在本作业服务器的分片序列号：[{}]", shardingContext.getShardingItem());
        switch(shardingContext.getShardingItem()){
            case 0 : System.out.println("000"); break;
            case 1 : System.out.println("111"); break;
            case 2 : System.out.println("222"); break;
        }
    }
}