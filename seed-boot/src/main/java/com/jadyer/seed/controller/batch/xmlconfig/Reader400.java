package com.jadyer.seed.controller.batch.xmlconfig;

import com.jadyer.seed.controller.batch.model.Person;
import com.jadyer.seed.controller.batch.model.PersonRepository;
import org.springframework.batch.item.ItemReader;

import javax.annotation.Resource;

/**
 * 空的Reader：啥都不做
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/23 20:40.
 */
public class Reader400 implements ItemReader<Long> {
    @Resource
    private PersonRepository personRepository;

    @Override
    public Long read() {
        for(Person obj : personRepository.findAll()){
            if(obj.getRealName().length() < 10){
                return obj.getId();
            }
        }
        //返回null时，该Step就不会再执行了
        //接下来就看该Step有没有配置next，有则去执行next，无则结束任务
        return null;
    }
}