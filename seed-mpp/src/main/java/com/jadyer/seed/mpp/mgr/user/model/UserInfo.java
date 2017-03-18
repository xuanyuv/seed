package com.jadyer.seed.mpp.mgr.user.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_user_info")
public class UserInfo extends BaseEntity<Long> {
	private static final long serialVersionUID = -2122414521974718376L;
	private int pid;
	private String username;
	private String password;
	private String uuid;
	private int mptype;
	private String mpid;
	private String mpno;
	private String mpname;
	private String appid;
	private String appsecret;
	@Column(name="bind_status")
	private int bindStatus;
	@Column(name="bind_time")
	private Date bindTime;

	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getMptype() {
		return mptype;
	}
	public void setMptype(int mptype) {
		this.mptype = mptype;
	}
	public String getMpid() {
		return mpid;
	}
	public void setMpid(String mpid) {
		this.mpid = mpid;
	}
	public String getMpno() {
		return mpno;
	}
	public void setMpno(String mpno) {
		this.mpno = mpno;
	}
	public String getMpname() {
		return mpname;
	}
	public void setMpname(String mpname) {
		this.mpname = mpname;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	public int getBindStatus() {
		return bindStatus;
	}
	public void setBindStatus(int bindStatus) {
		this.bindStatus = bindStatus;
	}
	public Date getBindTime() {
		return bindTime;
	}
	public void setBindTime(Date bindTime) {
		this.bindTime = bindTime;
	}
}