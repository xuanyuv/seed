package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.BeanUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BeanUtilTest {
    @Test
    public void beanCopyPropertiesTest() {
        UserDetail user11 = new UserDetail();
        user11.setId(12L);
        user11.setName("我是玄玉");
        user11.setSex("male");
        user11.setSmile(true);
        System.out.println(ReflectionToStringBuilder.toString(BeanUtil.copyProperties(user11, UserDetail.class)));
        List<UserDetail> userList = new ArrayList<>();
        UserDetail user01 = new UserDetail();
        user01.setId(1L);
        user01.setName("我是玄玉");
        user01.setSex("male");
        user01.setSmile(true);
        UserDetail user02 = new UserDetail();
        user02.setId(2L);
        user02.setName("我是玄玉");
        user02.setSex("male");
        user02.setSmile(true);
        UserDetail user03 = new UserDetail();
        user03.setId(3L);
        user03.setName("我是玄玉");
        user03.setSex("male");
        user03.setSmile(true);
        userList.add(user01);
        userList.add(user02);
        userList.add(user03);
        for(UserDetail user : BeanUtil.copyPropertiesForList(userList, UserDetail.class)){
            System.out.println(ReflectionToStringBuilder.toString(user));
        }
    }


    public static class User {
        private Long id;
        private String name;
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    public static class UserDetail extends User {
        private String sex;
        private boolean smile;
        public String getSex() {
            return sex;
        }
        public void setSex(String sex) {
            this.sex = sex;
        }
        public boolean isSmile() {
            return smile;
        }
        public void setSmile(boolean smile) {
            this.smile = smile;
        }
    }
}