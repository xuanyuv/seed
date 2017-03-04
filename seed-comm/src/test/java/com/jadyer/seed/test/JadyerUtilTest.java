package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.JadyerUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Enumeration;
import java.util.Properties;

public class JadyerUtilTest {
	@Test
	public void commonTest(){
		Properties properties = System.getProperties();
		Enumeration<Object> enums = properties.keys();
		while(enums.hasMoreElements()){
			Object key = enums.nextElement();
			System.out.println(key + "=" + properties.get(key));
		}
		System.out.println("---------" + System.getProperty("java.io.tmpdir"));
	}


	/**
	 * return和try{}finally{}的先后执行测试
	 * 控制台输出：11执行了---22执行了---aa11
	 */
	@Test
	public void finallyReturnTest(){
		System.out.println(FinallyReturn.print());
	}
	private static class FinallyReturn{
		@SuppressWarnings("UnusedAssignment")
		static String print(){
			String str = "aa";
			try{
				str = str + "11";
				System.out.println("11执行了");
				//throw new IllegalArgumentException("非法参数");
				return str;
			}catch(Exception e){
				throw new RuntimeException("测试ReturnFinally时发生异常-->[" + e.getMessage() + "]");
			}finally{
				str = str + "22";
				System.out.println("22执行了");
				//return str;
			}
		}
	}


	/**
	 * 日期20160513转换2016-05-13测试
	 */
	@Test
	public void getDetailDateTest(){
		Assert.assertEquals("2016-05-13", JadyerUtil.getDetailDate("20160513"));
	}


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
			JadyerUtil.beanCopyProperties(user11, user22);
		}
		System.out.println("耗时[" + (System.currentTimeMillis()-startTime) +"]ms转换完毕，得到" + ReflectionToStringBuilder.toString(user22));
	}


	class User{
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
	public class UserDetail extends User{
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