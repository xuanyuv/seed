package com.jadyer.seed.simulator.cronbuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用ThreadLocal以空间换时间解决SimpleDateFormat线程安全问题
 */
public class DateFormatUtil {
    private static final ThreadLocal<Map<String, DateFormat>> threadLocal = new ThreadLocal<Map<String, DateFormat>>(){
        @Override
        protected Map<String, DateFormat> initialValue(){
            return new HashMap<>();
        }
    };

    private static DateFormat getDateFormat(String pattern){
        DateFormat dateFormat = threadLocal.get().get(pattern);
        if(null == dateFormat){
            dateFormat = new SimpleDateFormat(pattern);
            threadLocal.get().put(pattern, dateFormat);
        }
        return dateFormat;
    }

    public static Date parse(String pattern, String textDate) throws ParseException {
        return getDateFormat(pattern).parse(textDate);
    }

    static String format(String pattern, Date date){
        return getDateFormat(pattern).format(date);
    }
}