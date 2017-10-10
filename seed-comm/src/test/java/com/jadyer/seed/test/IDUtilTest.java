package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.IDUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.junit.Test;

import java.util.HashSet;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/10/9 18:23.
 */
public class IDUtilTest {
    static HashSet<Long> idSet = new HashSet<>();

    /**
     * 测试每秒生产的ID数量
     */
    @Test
    public void perSecondProductNums(){
        long start = System.currentTimeMillis();
        int count = 0;
        for(int i=0; System.currentTimeMillis()-start<1000; i++,count=i){
            ////测试方法一：纯粹的生产ID，此时每秒生产的个数在两三百万左右（Win10且机械硬盘）
            //IDUtil.INSTANCE.nextId();
            //测试方法二：生产ID同时打印log，这种情况下生产ID的能力受限于log.info的吞吐，此时每秒生产的个数徘徊在一两万左右（Win10且机械硬盘）
            LogUtil.getLogger().info("{}", IDUtil.INSTANCE.nextId());
        }
        long end = System.currentTimeMillis()-start;
        System.out.println("本次生产的ID个数为" + count + "，耗时" + end + "ms");
    }
}