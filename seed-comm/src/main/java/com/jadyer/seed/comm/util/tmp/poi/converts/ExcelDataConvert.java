package com.jadyer.seed.comm.util.tmp.poi.converts;

import com.jadyer.seed.comm.util.tmp.poi.annotation.ExcelTypeCurrency;
import com.jadyer.seed.comm.util.tmp.poi.annotation.ExcelTypeDateTime;
import com.jadyer.seed.comm.util.tmp.poi.annotation.ExcelTypeNumber;
import com.jadyer.seed.comm.util.tmp.poi.annotation.ExcelTypePercentage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelDataConvert {
	/**
	 * 将数据转化为boolean类型
	 * @return 只有"true"和"1"才返回true,其它均返回false
	 */
	public static boolean convertToBoolean(Object data){
		return "true".equals(String.valueOf(data)) || "1".equals(String.valueOf(data));
	}

	
	/**
	 * 将数据转化为double类型
	 * @return 转换发生NumberFormatException异常时返回0,否则返回double类型的数值
	 */
	public static double convertToDouble(Object data) {
		double result = 0;
		try{
			result = Double.parseDouble(String.valueOf(data));
		}catch(NumberFormatException e){}
		return result;
	}

	
	/**
	 * 将数据转化为Float类型
	 * @return 转换发生NumberFormatException异常时返回0,否则返回double类型的数值
	 */
	public static double convertToFloat(Object data){
		double result = 0;
		try{
			result = Float.parseFloat(String.valueOf(data));
		}catch(NumberFormatException e){}
		return result;
	}

	
	/**
	 * 将数据转化为Long类型
	 * @return 转换发生NumberFormatException异常时返回0,否则返回double类型的数值
	 */
	public static double convertToLong(Object data) {
		double result = 0;
		try{
			result = Long.parseLong(String.valueOf(data));
		}catch(NumberFormatException e){}
		return result;
	}

	
	/**
	 * 将数据转化为Integer类型
	 * @return 转换发生NumberFormatException异常时返回0,否则返回double类型的数值
	 */
	public static double convertToInteger(Object data) {
		double result = 0;
		try{
			result = Integer.parseInt(String.valueOf(data));
		}catch(NumberFormatException e){}
		return result;
	}

	
	/**
	 * 转化为自定义格式的字符串
	 * @see 传入空data时会返回null
	 */
	public static String convertToDefinedType(String data, Field field) throws ParseException {
		if(StringUtils.isBlank(data)){
			return null;
		}
		ExcelTypeCurrency etc = field.getAnnotation(ExcelTypeCurrency.class);
		ExcelTypeDateTime etd = field.getAnnotation(ExcelTypeDateTime.class);
		ExcelTypeNumber etn = field.getAnnotation(ExcelTypeNumber.class);
		ExcelTypePercentage etp = field.getAnnotation(ExcelTypePercentage.class);
		//格式化数值
		if(null!=etn && !data.startsWith("0")){
			int decimalDigits = etn.decimalDigits();
			boolean thousandSeparator = etn.thousandSeparator();
			try{
				BigDecimal bd = new BigDecimal(data);
				bd = bd.setScale(decimalDigits, BigDecimal.ROUND_HALF_UP);
				data = bd.toString();
				if(thousandSeparator){
					String[] dataArray = data.split(".");
					char[] cs = dataArray[0].toCharArray();
					int length = cs.length;
					String temp = "";
					for(int i=length-1,j=1; i>=0; i--,j++){
						temp = cs[i] + temp;
						if(j%3==0 && i!=0){
							temp = "," + temp;
						}
					}
					data = temp + "." + dataArray[1];
				}
			}catch(Exception e){
				System.out.println(data + "is not a number, 堆栈轨迹如下");
				e.printStackTrace();
			}
		}
		//增加百分号
		if(null != etp){
			data += "%"; 
			System.out.println("ExcelTypePercentage:" + data);
		}
		//增加货币符号
		if(null != etc){
			data = etc.currencySymbol() + data;
			System.out.println("ExcelTypeCurrency:" + data);
		}
		//格式化时间
		if(null != etd){
			String sourcePattern = etd.sourcePattern();
			String targetPattern = etd.targetPattern();
			if(StringUtils.isNotBlank(sourcePattern) && StringUtils.isNotBlank(targetPattern)){
				Date date = new SimpleDateFormat(sourcePattern).parse(data);
				data = DateFormatUtils.format(date, targetPattern);
			}
		}
		return data;
	}
}