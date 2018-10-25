package com.jadyer.seed.qss.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 定时任务执行记录
 * Generated from seed-simcoder by 玄玉<https://jadyer.cn/> on 2018/07/20 10:04.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_schedule_log")
public class ScheduleLog extends BaseEntity<Long> {
    private static final long serialVersionUID = 9192798569617183288L;
    /** 任务ID，对应t_schedule_task#id */
    @NotNull
    @Min(1)
    @Column(name="task_id")
    private Long taskId;
    /** 定时任务的应用名称 */
    @NotBlank
    @Size(max=32)
    private String appname;
    /** 定时任务名称 */
    @NotBlank
    @Size(max=32)
    private String name;
    /** 定时任务URL */
    @NotBlank
    @Size(max=512)
    private String url;
    /** 定时任务触发时间 */
    @Column(name="fire_time")
    private Date fireTime;
    /** 定时任务所耗时间 */
    private Long duration;
    /** 定时任务返回结果 */
    @Column(name="resp_data")
    private String respData;

    public Long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getAppname() {
        return this.appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getFireTime() {
        return this.fireTime;
    }

    public void setFireTime(Date fireTime) {
        this.fireTime = fireTime;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getRespData() {
        return this.respData;
    }

    public void setRespData(String respData) {
        this.respData = respData;
    }

    public ScheduleLog() {}

    private ScheduleLog(Long taskId, String appname, String name, String url, Date fireTime, Long duration, String respData) {
        this.taskId = taskId;
        this.appname = appname;
        this.name = name;
        this.url = url;
        this.fireTime = fireTime;
        this.duration = duration;
        this.respData = respData;
    }

    public static ScheduleLogBuilder builder() {
        return new ScheduleLogBuilder();
    }

    public static class ScheduleLogBuilder {
        private Long taskId;
        private String appname;
        private String name;
        private String url;
        private Date fireTime;
        private Long duration;
        private String respData;
        public ScheduleLog build(){
            return new ScheduleLog(this.taskId, this.appname, this.name, this.url, this.fireTime, this.duration, this.respData);
        }
        public ScheduleLogBuilder taskId(Long taskId){
            this.taskId = taskId;
            return this;
        }
        public ScheduleLogBuilder appname(String appname){
            this.appname = appname;
            return this;
        }
        public ScheduleLogBuilder name(String name){
            this.name = name;
            return this;
        }
        public ScheduleLogBuilder url(String url){
            this.url = url;
            return this;
        }
        public ScheduleLogBuilder fireTime(Date fireTime){
            this.fireTime = fireTime;
            return this;
        }
        public ScheduleLogBuilder duration(Long duration){
            this.duration = duration;
            return this;
        }
        public ScheduleLogBuilder respData(String respData){
            this.respData = respData;
            return this;
        }
    }
}