package com.jadyer.seed.controller.batch.xmlconfig;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.GlobalData;
import com.jadyer.seed.controller.batch.model.Person;
import com.jadyer.seed.controller.batch.model.PersonEX;
import com.jadyer.seed.controller.batch.model.PersonRepository;
import org.springframework.batch.item.ItemProcessor;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/23 20:38.
 */
public class Processor401 implements ItemProcessor<Person, GlobalData> {
    @Resource
    private PersonRepository personRepository;

    @Override
    public GlobalData process(Person item) {
        Optional<Person> personOptional = personRepository.findById(item.getId());
        if(!personOptional.isPresent()){
            throw new RuntimeException("不存在的数据：personId=[" + item + "]");
        }
        Person person = personOptional.get();
        GlobalData globalData = new GlobalData();
        globalData.setPerson(person);
        globalData.setPersonEX(new PersonEX(person.getRealName()));
        LogUtil.getLogger().info("得到401数据：{}", JSON.toJSONString(globalData));
        return globalData;
    }
}