package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 金额操作的工具类
 * -------------------------------------------------------------------------------
 * 目前只处理人民币（CNY）
 * -------------------------------------------------------------------------------
 * @version v1.1
 * @history v1.1-->重写金额元转分和分转元方法，并增加两个方法：金额格式化和区间判断
 * @history v1.0-->初建
 * Created by 玄玉<https://jadyer.github.io/> on 2017/5/19 11:45.
 */
public class MoneyUtil {
    private MoneyUtil(){}

    ///**
    // * 金额元转分
    // * <p>
    // *     该方法可处理贰仟万以内的金额，且若有小数位，则不限小数位的长度
    // *     若金额达到了贰仟万以上，则不可使用该方法，否则计算出来的结果会令人大吃一惊
    // * </p>
    // * @param amount  金额的元进制字符串
    // * @return String 金额的分进制字符串
    // */
    //public static String yuanToFen(String amount){
    //    if(StringUtils.isBlank(amount)){
    //        return "";
    //    }
    //    //传入的金额字符串代表的是一个整数
    //    if(!amount.contains(".")){
    //        return Integer.parseInt(amount) * 100 + "";
    //    }
    //    //传入的金额字符串里面含小数点-->取小数点前面的字符串，并将之转换成单位为分的整数表示
    //    int money_fen = Integer.parseInt(amount.substring(0, amount.indexOf("."))) * 100;
    //    //取到小数点后面的字符串
    //    String pointBehind = (amount.substring(amount.indexOf(".") + 1));
    //    //amount=12.3
    //    if(pointBehind.length() == 1){
    //        return money_fen + Integer.parseInt(pointBehind)*10 + "";
    //    }
    //    //小数点后面的第一位字符串的整数表示
    //    int pointString_1 = Integer.parseInt(pointBehind.substring(0, 1));
    //    //小数点后面的第二位字符串的整数表示
    //    int pointString_2 = Integer.parseInt(pointBehind.substring(1, 2));
    //    //amount==12.03,amount=12.00,amount=12.30
    //    if(pointString_1 == 0){
    //        return money_fen + pointString_2 + "";
    //    }else{
    //        return money_fen + pointString_1*10 + pointString_2 + "";
    //    }
    //}


    ///**
    // * 金额元转分
    // * <p>
    // *     该方法会将金额中小数点后面的数值,四舍五入后只保留两位....如12.345-->12.35
    // *     该方法可处理贰仟万以内的金额
    // *     若金额达到了贰仟万以上，则不可使用该方法，否则计算出来的结果会令人大吃一惊
    // * </p>
    // * @param amount  金额的元进制字符串
    // * @return String 金额的分进制字符串
    // */
    //public static String yuanToFenByRound(String amount){
    //    if(StringUtils.isBlank(amount)){
    //        return "";
    //    }
    //    if(!amount.contains(".")){
    //        return Integer.parseInt(amount) * 100 + "";
    //    }
    //    int money_fen = Integer.parseInt(amount.substring(0, amount.indexOf("."))) * 100;
    //    String pointBehind = (amount.substring(amount.indexOf(".") + 1));
    //    if(pointBehind.length() == 1){
    //        return money_fen + Integer.parseInt(pointBehind)*10 + "";
    //    }
    //    int pointString_1 = Integer.parseInt(pointBehind.substring(0, 1));
    //    int pointString_2 = Integer.parseInt(pointBehind.substring(1, 2));
    //    //下面这种方式用于处理pointBehind=245,286,295,298,995,998等需要四舍五入的情况
    //    if(pointBehind.length() > 2){
    //        int pointString_3 = Integer.parseInt(pointBehind.substring(2, 3));
    //        if(pointString_3 >= 5){
    //            if(pointString_2 == 9){
    //                if(pointString_1 == 9){
    //                    money_fen = money_fen + 100;
    //                    pointString_1 = 0;
    //                    pointString_2 = 0;
    //                }else{
    //                    pointString_1 = pointString_1 + 1;
    //                    pointString_2 = 0;
    //                }
    //            }else{
    //                pointString_2 = pointString_2 + 1;
    //            }
    //        }
    //    }
    //    if(pointString_1 == 0){
    //        return money_fen + pointString_2 + "";
    //    }else{
    //        return money_fen + pointString_1*10 + pointString_2 + "";
    //    }
    //}


    ///**
    // * 金额分转元
    // * <p>
    // *     如果传入的参数中含小数点，则直接原样返回
    // *     该方法返回的金额字符串格式为<code>00.00</code>，其整数位有且至少有一个，小数位有且长度固定为2
    // * </p>
    // * @param amount  金额的分进制字符串
    // * @return String 金额的元进制字符串
    // */
    //public static String fenToYuan(String amount){
    //    if(StringUtils.isBlank(amount)){
    //        return "";
    //    }
    //    if(amount.contains(".")){
    //        return amount;
    //    }
    //    if(amount.length() == 1){
    //        return "0.0" + amount;
    //    }else if(amount.length() == 2){
    //        return "0." + amount;
    //    }else{
    //        return amount.substring(0, amount.length()-2) + "." + amount.substring(amount.length()-2);
    //    }
    //}


    /**
     * 金额格式化（默认四舍五入，比如：12345678.987会被格式化为：12,345,678.99）
     */
    public static String format(String amount){
        if(StringUtils.isBlank(amount)){
            return "0.00";
        }
        return new DecimalFormat("##,###.00").format(Double.parseDouble(amount));
    }


    /**
     * 判断金额是否在某个区间内（包含相等的情况，即：min<=amount<=max）
     */
    public static boolean isBetween(String amount, String minAmount, String maxAmount){
        BigDecimal bigAmount = new BigDecimal(amount);
        BigDecimal bigMinAmount = new BigDecimal(minAmount);
        BigDecimal bigMaxAmount = new BigDecimal(maxAmount);
        return bigAmount.compareTo(bigMinAmount)>=0 && bigAmount.compareTo(bigMaxAmount)<=0;
    }


    /**
     * 金额元转分
     * @param amount  金额的元进制字符串
     * @return String 金额的分进制字符串
     */
    public static BigDecimal yuanToFen(String amount){
        if(StringUtils.isBlank(amount)){
            return new BigDecimal(0);
        }
        return new BigDecimal(amount).multiply(new BigDecimal(100));
    }


    /**
     * 金额分转元
     * @param amount  金额的分进制字符串
     * @return String 金额的元进制字符串（返回值固定两位小数且四舍五入，比如109.5会得到1.10）
     */
    public static BigDecimal fenToYuan(String amount){
        if(StringUtils.isBlank(amount)){
            return new BigDecimal(0);
        }
        return new BigDecimal(amount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }
}