package com.jadyer.seed.comm.util.tmp.poi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)          //指定该注解适用于field
@Retention(RetentionPolicy.RUNTIME) //编译程序将Annotation储存于class档中,可由JVM读入(故可搭配反射Reflection机制让JVM读取Annotation信息)
public @interface ExcelHeader {
	/**
	 * Excel表头的列序号
	 */
	public int column();
	/**
	 * Excel表头的列标题
	 */
	public String title();
}