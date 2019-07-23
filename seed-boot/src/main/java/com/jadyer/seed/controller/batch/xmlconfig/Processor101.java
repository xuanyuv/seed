package com.jadyer.seed.controller.batch.xmlconfig;

import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.controller.batch.model.Person;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/11/13 10:39.
 */
public class Processor101 implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person item) {
        String validateResult = ValidatorUtil.validate(item);
        if(StringUtils.isNotEmpty(validateResult)){
            throw new SeedException("数据校验未通过-->" + validateResult);
        }
        LogUtil.getLogger().info("读取到-->{}", ReflectionToStringBuilder.toString(item));
        item.setAge(item.getAge() + 101);
        item.setRealName(item.getRealName() + "-101");
        LogUtil.getLogger().info("转换后==>{}", ReflectionToStringBuilder.toString(item));
        return item;
    }
}