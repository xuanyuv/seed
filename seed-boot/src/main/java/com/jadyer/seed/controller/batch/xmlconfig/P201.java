package com.jadyer.seed.controller.batch.xmlconfig;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.Person;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/12/14 19:02.
 */
public class P201 implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person item) {
        LogUtil.getLogger().info("读取到-->{}", ReflectionToStringBuilder.toString(item));
        item.setAge(item.getAge() - 2);
        item.setRealName(item.getRealName().replaceAll("00", "22"));
        LogUtil.getLogger().info("转换后==>{}", ReflectionToStringBuilder.toString(item));
        return item;
    }
}