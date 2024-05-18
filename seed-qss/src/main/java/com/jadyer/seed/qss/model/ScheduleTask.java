package com.jadyer.seed.qss.model;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

/**
 * 定时任务信息表
 * -----------------------------------------------------------------------------------------------------------
 * http://www.oracle.com/technetwork/cn/java/toplink-jpa-annotations-100895-zhs.html
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/
 * -----------------------------------------------------------------------------------------------------------
 * 关于@DynamicInsert的用法
 * 使用该注解（默认值为true）后，JpaRepository.save(Entity)时会根据Entity属性值是否为null来动态生成insert语句
 * 对于值为null的属性，它就不会被加入到动态生成的insert语句中，其类似于MyBatis-Generator生成的insertSelective
 * 注意这里说的“值为null”指的是显式或隐式的设置属性值为null,而对于字符串的"null"或""则会加入到insert语句
 * -----------------------------------------------------------------------------------------------------------
 * 关于@DynamicUpdate的用法
 * Sping-Data-JPA没有提供update()方法，取而代之的是，它提供的save()的真正含义是saveOrUpdate（也叫upsert）
 * 其判断依据就是主键是否有值：比如主键为int或Integer，当发现其值为0或null时，它就认为是insert，否则就是update
 * update时，Sping-Data-JPA会先根据主键到数据库查一次所有字段的值，再将待更新的Entity与查询到的字段值进行对比
 * 从动态生成的UPDATE语句可以看出：其默认会更新所有字段，而使用了@DynamicUpdate就只会更新上一步对比的不一样值的字段
 * 但若待更新的Entity属性值与查询到的字段值都一样的话，那么无论请求多少次，Sping-Data-JPA都不会向数据库发起更新请求
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2015/08/08 20:18.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_schedule_task")
public class ScheduleTask extends BaseEntity<Long> {
    private static final long serialVersionUID = 6239479172908393534L;

    /** 定时任务的应用名称 */
    private String appname;

    /** 定时任务名称 */
    private String name;

    /** 定时任务执行的CronExpression */
    private String cron;

    /** 定时任务URL */
    private String url;

    /** 定时任务状态：0-停止、1-启动、2-挂起、3-恢复 */
    private int status = SeedConstants.QSS_STATUS_STOP;

    /** 定时任务是否允许并行执行：0-不允许、1-允许 */
    private int concurrent = SeedConstants.QSS_CONCURRENT_NO;

    /** 定时任务下次触发时间 */
    @Column(name="next_fire_time")
    private Date nextFireTime;

    /** 定时任务上次触发时间 */
    @Column(name="previous_fire_time")
    private Date previousFireTime;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(int concurrent) {
        this.concurrent = concurrent;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }
}