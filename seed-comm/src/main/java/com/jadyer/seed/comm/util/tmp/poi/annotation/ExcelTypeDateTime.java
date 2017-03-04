package com.jadyer.seed.comm.util.tmp.poi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTypeDateTime {
	/**
	 * 源日期时间格式化模式
	 * <li>日期类型(Date/Calendar)：不需要指定</li>
	 * <li>字符串类型(String)：必须指定</li>
	 */
	public String sourcePattern() default "";

	/**
	 * 目标日期时间的格式化模式
	 * <li>日期类型(Date/Calendar)：必须指定</li>
	 * <li>字符串类型(String)：必须指定</li>
	 */
	public String targetPattern();
}