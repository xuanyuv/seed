package com.jadyer.seed.controller.batch.xml;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.Person;
import com.jadyer.seed.controller.batch.model.PersonRepository;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/11/13 10:40.
 */
@Component
public class W1001 implements ItemWriter<Person> {
    @Resource
    private PersonRepository personRepository;

    @Override
    public void write(List<? extends Person> items) throws Exception {
        for(Person obj : items){
            LogUtil.getLogger().info("写入DB：" + ReflectionToStringBuilder.toString(personRepository.saveAndFlush(obj)));
        }
    }
}