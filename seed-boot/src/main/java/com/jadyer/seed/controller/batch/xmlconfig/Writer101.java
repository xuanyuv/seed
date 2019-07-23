package com.jadyer.seed.controller.batch.xmlconfig;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.Person;
import com.jadyer.seed.controller.batch.model.PersonRepository;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.item.ItemWriter;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/11/13 10:40.
 */
public class Writer101 implements ItemWriter<Person> {
    @Resource
    private PersonRepository personRepository;

    @Override
    public void write(List<? extends Person> items) {
        for(Person obj : items){
            LogUtil.getLogger().info("写入DB：" + ReflectionToStringBuilder.toString(personRepository.saveAndFlush(obj)));
        }
    }
}