package com.jadyer.seed.boot.event;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/4/10 16:34.
 */
@Component
public class ApplicationStartupCommandRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        LogUtil.getLogger().info("应用启动成功......" + JSON.toJSONString(args));
    }
}