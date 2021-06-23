package com.jadyer.seed.controller.batch.xmlconfig;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.GlobalData;
import com.jadyer.seed.controller.batch.model.PersonRepository;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.item.ItemWriter;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/23 20:50.
 */
public class Writer400 implements ItemWriter<GlobalData> {
    @Resource
    private PersonRepository personRepository;

    @Override
    public void write(List<? extends GlobalData> items) {
        LogUtil.getLogger().info("得到400待写入的数据：{}", JSON.toJSONString(items));
        for(GlobalData obj : items){
            if(obj.getPerson().getRealName().contains("卢云")){
                // 注：这里虽然也能读到更新后的数据，但实际读到的是缓存中的，数据库里面还是旧的
                // 注：此时事务并未提交，仅当chunk-commit-interval的记录数都执行完，才会提交事务
                LogUtil.getLogger().info("读取到当前数据库的状态：{}", JSON.toJSONString(personRepository.findById(490223833928978432L)));
            }
            LogUtil.getLogger().info("写入DB：" + ReflectionToStringBuilder.toString(personRepository.saveAndFlush(obj.getPerson())));
        }
    }
}