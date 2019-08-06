package com.jadyer.seed.controller.batch.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_person")
public class Person extends BaseEntity<Long> {
    private static final long serialVersionUID = 6978021496961670970L;
    @Size(min=2, max=16)
    private String realName;
    private int age;
    private Date birthDay;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }
}