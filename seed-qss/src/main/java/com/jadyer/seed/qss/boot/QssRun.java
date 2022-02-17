package com.jadyer.seed.qss.boot;

import com.jadyer.seed.comm.boot.BootRunHelper;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/3/4 18:39.
 */
@SpringBootApplication(scanBasePackages="com.jadyer.seed")
public class QssRun extends BootRunHelper {
    public static void main(String[] args) {
        BootRunHelper.run(args, QssRun.class);
    }
}