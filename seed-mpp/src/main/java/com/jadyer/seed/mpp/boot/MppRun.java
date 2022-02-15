package com.jadyer.seed.mpp.boot;

import com.jadyer.seed.comm.boot.BootRunHelper;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="com.jadyer.seed")
public class MppRun extends BootRunHelper {
    public static void main(String[] args) {
        BootRunHelper.run(args, MppRun.class);
    }
}