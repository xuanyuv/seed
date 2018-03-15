package com.jadyer.seed.controller.batch.processor;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.controller.batch.model.Person;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * --------------------------------------------------------------------------------
 * 1.process()用于数据的转换与处理（比如写数据前，判断其是否已入库）
 * 2.ItemReader读到了多少个item，process()方法就会被调用多少次
 * 3.process()返回null时，SpringBatch将会忽略这个item，不将其发送给ItemWriter
 * --------------------------------------------------------------------------------
 * Comment by 玄玉<http://jadyer.cn/> on 2017/11/24 16:55.
 */
@Component
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person item) throws Exception {
        String validateResult = ValidatorUtil.validate(item);
        if(StringUtils.isNotEmpty(validateResult)){
            LogUtil.getLogger().error("数据校验未通过-->[{}]", validateResult);
            return null;
        }
        LogUtil.getLogger().info("读取到-->{}", ReflectionToStringBuilder.toString(item));
        item.setAge(item.getAge() * 2);
        item.setName(item.getName() + "00");
        LogUtil.getLogger().info("转换后==>{}", ReflectionToStringBuilder.toString(item));
        return item;
    }
}