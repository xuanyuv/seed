package com.jadyer.seed.test;

import org.junit.Test;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/14 16:57.
 */
public class CommTest {
    @Test
    public void common() {
        System.out.println("java.io.tmpdir is " + System.getProperty("java.io.tmpdir"));
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