package com.jadyer.seed.comm.jpa;

import org.springframework.data.domain.Persistable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * -------------------------------------------------------------------------------------------------------------------
 * 关于@MappedSuperclass
 * 1、标注该注解的类将不是一个完整的实体类，它将不会映射到数据库表，但其属性都将映射到其子类的数据库字段中
 * 2、标注该注解的类还可以直接标注@EntityListeners实体监听器，其作用范围仅在其子类中，并且实体监听器同样可以被其子类继承或重载
 * 3、标注该注解的类属性最好为protected或default，以保证其同一个包下的子类可直接调用它的属性，便于实体监听器或带参数构造函数的操作
 * -------------------------------------------------------------------------------------------------------------------
 * 审计功能【待验证】
 * 比较常见的相关注解有：@CreatedBy、@LastModifiedBy、@CreatedDate、@LastModifiedDate
 * 启用审计功能后，这四个注解才能发挥作用，启用步骤如下
 * 1. 引入spring-aspects.jar
 * 2. 如果使用Java配置的话，在配置类上使用@EnableJpaAuditing（若使用XML配置则添加<jpa:auditing/>）
 * 3. 最后在实体类上添加@EntityListeners(AuditingEntityListener.class)
 * -------------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2017/2/28 18:36.
 */
@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> implements Persistable<ID> {
    private static final long serialVersionUID = 5563689039804450746L;
    /**
     * 主键（如果这里不加@GeneratedValue那么Save()时生成的insert就包括id字段）
     * ----------------------------------------------------------------------------------------------
     * 这里主键生成策略设置了GenerationType.AUTO
     * SpringBoot1.0下运行良好，SpringBoot2.0下会报错：Table 'xxx.hibernate_sequence' doesn't exist
     * 这时有两种解决办法：
     * 一个是设置GenerationType.IDENTITY
     * 另外就是在application.yml里设置spring.jpa.properties.hibernate.id.new_generator_mappings=false
     * 其实在Hibernate4.X里面这个属性默认是true，到了5.X变成false啦
     * 详见：https://stackoverflow.com/questions/32968527/hibernate-sequence-doesnt-exist
     * ----------------------------------------------------------------------------------------------
     */
    // @Id
    // @SequenceGenerator(name="SEQUENCE_QUARTZ_NAME", sequenceName="SEQUENCE_QUARTZ", allocationSize=1)
    // @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENCE_QUARTZ_NAME")
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private ID id;

    /**
     * 创建时间
     * 1、若未定义@Column那么JPA会认为数据库字段名与该Field相同，所以二者不同时就要显式指定@Column
     * 2、fetch=FetchType.LAZY用于指定该字段延迟加载：即只有在访问该属性时，才会把它的数据装载进内存中
     * 3、为字段指定默认值可直接写为Date createTime = new Date()，其通常用于Save()所生成的insert语句
     *   即前台不传createTime时，Controller接收的实体对象的createTime属性值，已被自动赋为这里的默认值
     */
    @Column(name="create_time", updatable=false)
    @Basic(fetch= FetchType.LAZY)
    // @org.springframework.data.annotation.CreatedDate
    private Date createTime = new Date();

    @Column(name="update_time", updatable=false, insertable=false)
    @Basic(fetch=FetchType.LAZY)
    // @org.springframework.data.annotation.LastModifiedDate
    private Date updateTime;

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o==null || getClass()!=o.getClass()){
            return false;
        }
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean isNew() {
        return null == this.id;
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public void setId(ID id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}