package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.IDUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/10/9 18:23.
 */
public class IDUtilTest {
    static HashSet<Long> idSet = new HashSet<>();

    /**
     * 测试每秒生产的ID数量
     */
    @Test
    public void perSecondProductNums(){
        long start = SystemClockUtil.INSTANCE.now();
        int count = 0;
        for(int i=0; SystemClockUtil.INSTANCE.now()-start<1000; i++,count=i){
            ////测试方法一：纯粹的生产ID，此时每秒生产的个数在两三百万左右（Win10且机械硬盘）
            //IDUtil.INSTANCE.nextId();
            //测试方法二：生产ID同时打印log，这种情况下生产ID的能力受限于log.info的吞吐，此时每秒生产的个数徘徊在一两万左右（Win10且机械硬盘）
            LogUtil.getLogger().info("{}", IDUtil.INSTANCE.nextId());
        }
        long end = SystemClockUtil.INSTANCE.now()-start;
        System.out.println("本次生产的ID个数为" + count + "，耗时" + end + "ms");
    }


    /**
     * 测试会不会生成重复的ID
     */
    @Test
    public void repeatTest(){
        Set<Long> idSet = new HashSet<>();
        for(int i=0; i<30000000; i++){
            long id = IDUtil.INSTANCE.nextId();
            if(!idSet.add(id)){
                System.out.println("id");
            }
        }
    }
}