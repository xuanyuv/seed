package com.jadyer.seed.controller.batch.xmlconfig;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.Person;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/22 20:25.
 */
public class Processor203 implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person item) {
        LogUtil.getLogger().info("读取到-->{}", JSON.toJSONString(item));
        item.setRealName(item.getRealName() + "203");
        LogUtil.getLogger().info("转换后==>{}", JSON.toJSONString(item));
        return item;
    }
}