package com.jadyer.seed.controller.batch.xmlconfig;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.Person;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/22 20:27.
 */
public class Writer202 implements ItemWriter<Person> {
    @Override
    public void write(List<? extends Person> items) {
        LogUtil.getLogger().info("得到自定义的测试对象202==>{}", JSON.toJSONString(items));
    }
}