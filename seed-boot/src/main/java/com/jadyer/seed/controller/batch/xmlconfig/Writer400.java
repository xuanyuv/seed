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
            LogUtil.getLogger().info("写入DB：" + ReflectionToStringBuilder.toString(personRepository.saveAndFlush(obj.getPerson())));
        }
    }
}