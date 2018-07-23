package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.BeanUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;

public class BeanUtilTest {
    /**
     * 通过反射实现的属性拷贝方法测试
     */
    @Test
    public void beanCopyPropertiesTest(){
        UserDetail user11 = new UserDetail();
        UserDetail user22 = new UserDetail();
        user11.setId(12);
        user11.setName("我是玄玉");
        user11.setSex("male");
        long startTime = System.currentTimeMillis();
        for(int i=0; i<10000000; i++){
            BeanUtil.copyProperties(user11, user22);
        }
        System.out.println("耗时[" + (System.currentTimeMillis()-startTime) +"]ms转换完毕，得到" + ReflectionToStringBuilder.toString(user22));
    }


    class User{
        private int id;
        private String name;
        public int getId() {
            return id;
        }
        void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        void setName(String name) {
            this.name = name;
        }
    }
    public class UserDetail extends User{
        private String sex;
        public String getSex() {
            return sex;
        }
        public void setSex(String sex) {
            this.sex = sex;
        }
    }
}