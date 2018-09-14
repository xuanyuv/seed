package com.jadyer.seed.test;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/9/14 18:02.
 */
public class ZipUtilTest {
    @Test
    public void zipTest(){
        File f1 = new File("C:\\Users\\hongyu.lu\\Desktop\\11\\放款通知书_20180914.pdf");
        File f2 = new File("C:\\Users\\hongyu.lu\\Desktop\\11\\招商银行放款文件_20180914.xls");
        File f3 = new File("C:\\Users\\hongyu.lu\\Desktop\\11\\资本项目账户资金支付命令函_20180914.pdf");
        cn.hutool.core.util.ZipUtil.zip(new File("C:\\Users\\hongyu.lu\\Desktop\\1111.zip"), StandardCharsets.UTF_8, false, f1, f2, f3);
    }
}