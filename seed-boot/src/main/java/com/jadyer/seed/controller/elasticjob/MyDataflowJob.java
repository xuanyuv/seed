package com.jadyer.seed.controller.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.elasticjob.model.Person;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
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
        LogUtil.getLogger().info("分片总数：[{}]，运行在本作业服务器的分片序列号：[{}]", shardingContext.getShardingTotalCount(), shardingContext.getShardingItem());
        List<Person> personList = new ArrayList<>();
        switch(shardingContext.getShardingItem()){
            case 0 : personList = new ArrayList<>(); LogUtil.getLogger().info("//////////////000"); break; //get data from database by sharding item 0
            case 1 : personList = new ArrayList<>(); LogUtil.getLogger().info(">>>>>>>>>>>>>>111"); break; //get data from database by sharding item 1
            case 2 : personList = new ArrayList<>(); LogUtil.getLogger().info("<<<<<<<<<<<<<<222"); break; //get data from database by sharding item 2
            default: personList = new ArrayList<>(); LogUtil.getLogger().info("--------------333");
        }
        LogUtil.getLogger().info("数据获取完毕，准备返回");
        //流式处理数据只有fetchData方法的返回值为null或集合长度为空时，作业才停止抓取，否则作业将一直运行下去
        //非流式处理数据则只会在每次作业执行过程中执行一次fetchData方法和processData方法，随即完成本次作业
        if(JadyerUtil.isOddNumber(Integer.parseInt(DateFormatUtils.format(new Date(), "ss")))){
            return personList;
        }
        Person person01 = new Person();
        person01.setName("问清风");
        person01.setAge(99);
        Person person02 = new Person();
        person02.setName("水在天");
        person02.setAge(99);
        personList.add(person01);
        personList.add(person02);
        return personList;
    }


    /**
     * 经测：List<Person>参数为null或空长度时，该方法不会被调用（无论Dataflow当前的配置是否为流式处理）
     */
    @Override
    public void processData(ShardingContext shardingContext, List<Person> data) {
        //如果采用流式作业处理方式，建议processData处理数据后更新其状态，避免fetchData再次抓取到，从而使得作业永不停止
        //流式数据处理参照TbSchedule设计，适用于不间歇的数据处理
        LogUtil.getLogger().info("实体数量为-->{}", data.size());
    }
}