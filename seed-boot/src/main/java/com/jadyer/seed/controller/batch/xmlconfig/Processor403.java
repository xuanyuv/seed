package com.jadyer.seed.controller.batch.xmlconfig;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.controller.batch.model.GlobalData;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/23 20:38.
 */
public class Processor403 implements ItemProcessor<GlobalData, GlobalData> {
    @Override
    public GlobalData process(GlobalData item) {
        LogUtil.getLogger().info("得到403待处理的数据：{}", JSON.toJSONString(item));
        item.getPerson().setRealName(item.getPerson().getRealName() + "403");
        item.getPersonEX().setRealName(item.getPersonEX().getRealName() + "403");
        return item;
    }
}