package com.jadyer.seed.controller.batch.xmlconfig;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.GlobalData;
import com.jadyer.seed.controller.batch.model.PersonRepository;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.item.ItemProcessor;

import javax.annotation.Resource;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/23 20:38.
 */
public class Processor404 implements ItemProcessor<GlobalData, GlobalData> {
    @Resource
    private PersonRepository personRepository;

    @Override
    public GlobalData process(GlobalData item) {
        LogUtil.getLogger().info("得到404待落库的数据：{}", JSON.toJSONString(item));

        // 注：这里虽然也能读到更新后的数据，但实际读到的是缓存中的，数据库里面还是旧的
        // 注：此时事务并未提交，仅当chunk-commit-interval的记录数都执行完，才会提交事务
        LogUtil.getLogger().info("读取到当前数据库的状态：{}", JSON.toJSONString(personRepository.findById(490223833928978432L)));

        LogUtil.getLogger().info("写入DB：" + ReflectionToStringBuilder.toString(personRepository.saveAndFlush(item.getPerson())));
        return item;
    }
}