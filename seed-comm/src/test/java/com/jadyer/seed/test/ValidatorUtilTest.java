package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.ValidatorUtil;
import org.junit.Test;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class ValidatorUtilTest {
    @Test
    public void emptyObjectTest(){
        ValidateUser user = new ValidateUser();
        System.out.println(ValidatorUtil.validate(user));
    }


    /**
     * hibernate.validator测试
     */
    @Test
    public void validatorUtilTest(){
        ValidateUser user = new ValidateUser();
        //user.setName("铁面生");
        System.out.println(ValidatorUtil.validate(user));
        System.out.println("------------------------------------------");
        ValidateUserDetail userDetail = new ValidateUserDetail();
        userDetail.setId(2);
        //userDetail.setSex("M");
        System.out.println(ValidatorUtil.validate(userDetail));
        System.out.println("------------------------------------------");
        ValidateUser user22 = new ValidateUser();
        user22.setId(2);
        List<ValidateUser> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user22);
        System.out.println(ValidatorUtil.validate(userList));
    }


    static class ValidateUser{
        @Min(1)
        private int id;
        @NotBlank
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


    public static class ValidateUserDetail extends ValidateUser {
        @NotBlank
        @Pattern(regexp="^M|F$", message="性别只能传M或F")
        private String sex;
        public String getSex() {
            return sex;
        }
        void setSex(String sex) {
            this.sex = sex;
        }
    }
}