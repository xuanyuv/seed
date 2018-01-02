package com.jadyer.seed.controller.tmp.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.jadyer.seed.controller.tmp.elasticjob.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Dataflow类型作业
 * ------------------------------------------------------------------------
 * Dataflow类型用于处理数据流，需实现DataflowJob接口
 * 该接口提供2个方法可供覆盖，分别用于抓取(fetchData)和处理(processData)数据
 * 可通过DataflowJobConfiguration配置是否流式处理
 * ------------------------------------------------------------------------
 */
public class MyDataflowJob implements DataflowJob<Person> {
    @Override
    public List<Person> fetchData(ShardingContext shardingContext) {
        List<Person> personList = new ArrayList<>();
        switch(shardingContext.getShardingItem()){
            case 0 : personList = new ArrayList<>(); break; //get data from database by sharding item 0
            case 1 : personList = new ArrayList<>(); break; //get data from database by sharding item 1
            case 2 : personList = new ArrayList<>(); break; //get data from database by sharding item 2
        }
        //流式处理数据只有fetchData方法的返回值为null或集合长度为空时，作业才停止抓取，否则作业将一直运行下去
        //非流式处理数据则只会在每次作业执行过程中执行一次fetchData方法和processData方法，随即完成本次作业
        return personList;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<Person> data) {
        //如果采用流式作业处理方式，建议processData处理数据后更新其状态，避免fetchData再次抓取到，从而使得作业永不停止
        //流式数据处理参照TbSchedule设计，适用于不间歇的数据处理
    }
}