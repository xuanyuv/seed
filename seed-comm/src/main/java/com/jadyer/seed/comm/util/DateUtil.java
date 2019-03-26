package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 * ----------------------------------------------------------------------------------------------------------------
 * @version v1.3
 * @version v1.3-->增加getDistanceDay()方法，用於計算兩個日期相隔的天數
 * @version v1.2-->增加getCrossDayList()方法，用于获取两个日期之间的所有日期列表
 * @history v1.1-->增加根据日期获得星期的方法getWeekName()
 * @history v1.0-->新建不添加若干方法
 * ----------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2017/5/19 11:05.
 */
public final class DateUtil {
    private DateUtil(){}

    /**
     * 判断是否月初
     */
    public static boolean isFirstDayOfMonth(Date date){
        return "01".equals(DateFormatUtils.format(date, "dd"));
    }


    /**
     * 判断是否月末
     */
    public static boolean isEndDayOfMonth(Date date){
        return isFirstDayOfMonth(DateUtils.addDays(new Date(), 1));
    }


    /**
     * 判断是否本周第一天
     */
    public static boolean isFirstDayOfWeek(){
        return 0 == DateUtils.truncatedCompareTo(new Date(), getFirstDayOfWeek(), Calendar.DAY_OF_MONTH);
    }


    /**
     * 获取本周第一天
     */
    public static Date getFirstDayOfWeek(){
        //使用默认时区和语言环境获得一个基于当前时间的日历
        Calendar cal = Calendar.getInstance();
        //设置一个星期的第一天是哪一天（也可以用cal.add(Calendar.DAY_OF_MONTH, -1)，二者都是解决周日时获取到的是下一周的情况）
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        //将给定的日历字段设置为给定值
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }


    /**
     * 获取前一天日期yyyyMMdd
     * @see 经测试，针对闰年02月份或跨年等情况，该代码仍有效。测试代码如下
     * @see calendar.set(Calendar.YEAR, 2013);
     * @see calendar.set(Calendar.MONTH, 0);
     * @see calendar.set(Calendar.DATE, 1);
     * @see 测试时，将其放到<code>calendar.add(Calendar.DATE, -1);</code>前面即可
     * @return 返回的日期格式为yyyyMMdd
     */
    public static String getYestoday(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    }


    /**
     * 获取格式化的详细日期
     * @param dateStr yyyyMMdd格式的日期字符串
     * @return yyyy-MM-dd格式的日期字符串
     */
    public static String getDetailDate(String dateStr){
        try {
            return String.format("%tF", DateUtils.parseDate(dateStr, "yyyyMMdd"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取当前的日期yyyyMMdd
     */
    public static String getCurrentDate(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }


    /**
     * 获取当前的时间yyyyMMddHHmmss
     */
    public static String getCurrentTime(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }


    /**
     * 获取本周开始的时间
     */
    public static Date getCurrentWeekStartDate(){
        Calendar currentDate = Calendar.getInstance();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return currentDate.getTime();
    }


    /**
     * 获取本周结束的时间
     */
    public static Date getCurrentWeekEndDate(){
        Calendar currentDate = Calendar.getInstance();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return currentDate.getTime();
    }


    /**
     * 计算两个日期的相差时间
     * ---------------------------------------------------------------------------------------------------------------
     * 等价于以下两个方法
     * org.apache.commons.lang3.time.DurationFormatUtils.formatDuration(time, "dd HH:mm:ss.SSS")
     * org.apache.commons.lang3.time.DurationFormatUtils.formatDuration(time, "dd'天'HH'小时'mm'分钟'ss'秒'SSS'毫秒'")
     * ---------------------------------------------------------------------------------------------------------------
     * @param begin 起始日期
     * @param end   终止日期
     * @return xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(Date begin, Date end) {
        long time = end.getTime() - begin.getTime();
        long day = time / (24 * 60 * 60 * 1000);
        long hour = (time / (60 * 60 * 1000) - day * 24);
        long minute = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);
        return day + "天" + hour + "小时" + minute + "分" + second + "秒";
    }


    /**
     * 计算两个日期相隔的天数
     * -----------------------------------------------------------------------------------
     * 等价于下面的方法
     * org.apache.commons.lang3.time.DurationFormatUtils.formatPeriod(time, "d")
     * 關於相隔和相差（getDistanceTime()）
     * 1、开始日期20170824235959，结束日期20170825000000，差一秒，但其相差天數==0，相隔天數==1
     * 2、开始日期20170824110000，结束日期20170825105959，差一秒，但其相差天數==0，相隔天數==1
     * 3、开始日期20170824110000，结束日期20170825110000，相 同，但其相差天數==1，相隔天數==1
     * -----------------------------------------------------------------------------------
     * @param begin 起始日期
     * @param end   终止日期
     * @return 相隔的天數
     */
    public static long getDistanceDay(Date begin, Date end) {
        String pattern = "yyyyMMdd";
        try {
            begin = DateUtils.parseDate(DateFormatUtils.format(begin, pattern), pattern);
            end = DateUtils.parseDate(DateFormatUtils.format(end, pattern), pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long time = end.getTime() - begin.getTime();
        return time / (24 * 60 * 60 * 1000);
    }


    /**
     * 计算两个日期相隔的月数
     * -----------------------------------------------------------------------------------
     * org.apache.commons.lang3.time.DurationFormatUtils.formatPeriod(time, "M")
     * 20190218 10:20 - 20190318 ： return 0
     * 20190218 10:20 - 20190319 ： return 1
     * 20190218 - 20190316       ： return 0
     * 20190218 - 20190318       ： return 1
     * 20190218 - 20190515       ： return 2
     * 20190218 - 20190519       ： return 3
     * 20190131 - 20190228       ： return 0
     * 20190131 - 20190430       ： return 2
     * 如果是算两个日期相隔的月数，那就把第二个参数format传小写的"y"
     * 若传的format="y-M-d"，那么formatPeriod()返回的字符串就是"0-0-0"格式的，split截取即可
     * 但此时要注意，比如计算20190218到20190318，算出来的就是0-1-0，所以第二个参数format很重要
     * -----------------------------------------------------------------------------------
     * Comment by 玄玉<https://jadyer.cn/> on 2019/3/26 16:58.
     */
    public static long getDistanceMonth(Date begin, Date end) {
        String period = DurationFormatUtils.formatPeriod(begin.getTime(), end.getTime(), "M");
        //处理 [20190131 - 20190228] 的情况
        if(isEndDayOfMonth(end) && Integer.parseInt(DateFormatUtils.format(begin, "dd"))>Integer.parseInt(DateFormatUtils.format(end, "dd"))){
            return Long.parseLong(period) + 1;
        }else{
            return Long.parseLong(period);
        }
    }


    /**
     * 获取指定日期相隔一定天数后的日期
     * @see 该方法等效于org.apache.commons.lang3.time.DateUtils.addDays(startDate, days)
     * @param startDate 参照日期
     * @param days      相隔的天数，正数时则往后计算，负数则往前计算
     */
    public static Date getIncreaseDate(Date startDate, int days){
        final Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }


    /**
     * 根据日期获得星期
     * @param date 字符串格式的日期，可传入20170719或者2017-07-19
     * @return 星期日、星期一、星期二、星期三、星期四、星期五、星期六
     */
    public static String getWeekName(String dateStr){
        dateStr = dateStr.replaceAll("-", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("无效入参，格式应为20170719或者2017-07-19");
        }
        return getWeekName(date);
    }


    /**
     * 根据日期获得星期
     * @return 星期日、星期一、星期二、星期三、星期四、星期五、星期六
     */
    public static String getWeekName(Date date){
        String[] weekNames = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekNameIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(weekNameIndex < 0){
            weekNameIndex = 0;
        }
        return weekNames[weekNameIndex];
    }


    /**
     * 获取两个日期之间的所有日期列表
     * @param startDate 起始日期，格式为yyyyMMdd
     * @param  endDate  结束日期，格式为yyyyMMdd
     * @return 返回值不包含起始日期和结束日期
     */
    public static List<Integer> getCrossDayList(String startDate, String endDate){
        List<Integer> dayList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();
        try {
            startDay.setTime(sdf.parse(startDate));
            endDay.setTime(sdf.parse(endDate));
        } catch (ParseException e) {
            throw new IllegalArgumentException("无效入参：" + e.getMessage());
        }
        //起始日期大于等于结束日期，则返回空列表
        if(startDay.compareTo(endDay) >= 0){
            return dayList;
        }
        while(true){
            //日期+1，并判断是否到达了结束日
            startDay.add(Calendar.DATE, 1);
            if(startDay.compareTo(endDay) == 0){
                break;
            }
            dayList.add(Integer.parseInt(DateFormatUtils.format(startDay.getTime(), "yyyyMMdd")));
        }
        return dayList;
    }
}