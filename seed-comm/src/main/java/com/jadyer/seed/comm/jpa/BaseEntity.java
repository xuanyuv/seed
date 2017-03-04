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

/**
 * 关于@MappedSuperclass注解
 * 1、标注该注解的类将不是一个完整的实体类，它将不会映射到数据库表，但其属性都将映射到其子类的数据库字段中
 * 2、标注该注解的类还可以直接标注@EntityListeners实体监听器，其作用范围仅在其子类中，并且实体监听器同样可以被其子类继承或重载
 * 3、标注该注解的类属性最好为protected或default，以保证其同一个包下的子类可以直接调用它的属性，便于实体监听器或带参数构造函数的操作
 * Created by 玄玉<https://jadyer.github.io/> on 2017/2/28 18:36.
 */
@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> implements Persistable<ID> {
	private static final long serialVersionUID = 5563689039804450746L;
	/**
	 * 主键（如果这里不加@GeneratedValue那么Save()时生成的insert就包括id字段）
	 */
	//@Id
	//@SequenceGenerator(name="SEQUENCE_QUARTZ_NAME", sequenceName="SEQUENCE_QUARTZ", allocationSize=1)
	//@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQUENCE_QUARTZ_NAME")
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
    //@org.springframework.data.annotation.CreatedDate
    private Date createTime = new Date();

    @Column(name="update_time", updatable=false, insertable=false)
	@Basic(fetch=FetchType.LAZY)
	//@org.springframework.data.annotation.LastModifiedDate
    private Date updateTime;

    @Override
    public boolean isNew() {
        return null == this.id;
    }

    @Override
    public ID getId() {
    	return id;
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