package com.jadyer.seed.test;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/14 16:57.
 */
public class CommTest {
    @Test
    public void evnPropTest() throws ParseException {
        Date edate = DateUtils.parseDate("201708241600", "yyyyMMddHHmm");
        if(new Date().compareTo(edate)>=0 && new Date().compareTo(DateUtils.addMinutes(edate, 3))<=0){
            System.out.println("成功--");
        }else{
            System.out.println("失敗--");
        }
        if((new Date().compareTo(edate)>=0) && (new Date().compareTo(DateUtils.addMinutes(edate, 3))<=0)){
            System.out.println("成功");
        }else{
            System.out.println("失敗");
        }
    }


    /**
     * return和try{}finally{}的先后执行测试
     * 目前控制台输出：11执行了---22执行了---aa11...22
     */
    @Test
    public void finallyReturnTest(){
        System.out.println(FinallyReturn.print());
    }
    private static class FinallyReturn{
        static String print(){
            String str = "aa";
            try{
                str += "11";
                System.out.println("11执行了");
                throw new IllegalArgumentException("非法参数");
                //return str;
            }catch(Exception e){
                str += "...";
                //throw new RuntimeException("测试ReturnFinally时发生异常-->[" + e.getMessage() + "]");
            }finally{
                //noinspection UnusedAssignment
                str += "22";
                System.out.println("22执行了");
            }
            return str;
        }
    }
}