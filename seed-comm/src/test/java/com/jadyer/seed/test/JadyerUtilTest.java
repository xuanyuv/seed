package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;

public class JadyerUtilTest {
    /**
     * 日期20160513转换2016-05-13测试
     */
    @Test
    public void getDetailDateTest(){
        Assert.assertEquals("2016-05-13", DateUtil.getDetailDate("20160513"));
    }
}