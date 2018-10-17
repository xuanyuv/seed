package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 金额操作的工具类
 * -------------------------------------------------------------------------------
 * 目前只处理人民币（CNY）
 * -------------------------------------------------------------------------------
 * @version v1.3
 * @history v1.3-->金额转中文增加“零元整”的判断
 * @history v1.2-->增加金额转中文的方法
 * @history v1.1-->重写金额元转分和分转元方法，并增加两个方法：金额格式化和区间判断
 * @history v1.0-->初建
 * -------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2017/5/19 11:45.
 */
public class MoneyUtil {
    /** 转中文：大写数字 */
    private static final String[] NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    /** 转中文：整数部分的单位 */
    private static final String[] INT_UNIT = {"元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟"};
    /** 转中文：小数部分的单位 */
    private static final String[] DECIMAL_UNIT = {"角", "分", "厘"};

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


    /**
     * 金额转中文
     * 返回值举例：零元整、壹元整、壹厘、壹元壹分、玖亿捌仟柒佰陆拾伍万肆仟叁佰贰拾壹元整
     */
    public static String toChinese(String amount){
        if(StringUtils.isBlank(amount)){
            return "";
        }
        //去掉半角逗号“,”
        if(amount.contains(",")){
            amount = amount.replaceAll(",", "");
        }
        /*
         * 分离整数部分和小数部分
         */
        //整数部分数字
        String intNumberStr;
        //小数部分数字
        String decimalNumberStr;
        if(amount.contains(".")){
            intNumberStr = amount.substring(0, amount.indexOf("."));
            decimalNumberStr = amount.substring(amount.indexOf(".") + 1);
        } else {
            intNumberStr = amount;
            decimalNumberStr = "";
        }
        //判断“零元整”的情况
        if(("".equals(intNumberStr)||0==Long.parseLong(intNumberStr)) && ("".equals(decimalNumberStr)||0==Long.parseLong(decimalNumberStr))){
            return "零元整";
        }
        //整数部分需去掉首0，小数部分的0不用管（超出部分会被舍去，该方法只会计算到角、分、厘三位）
        if(!"".equals(intNumberStr)){
            intNumberStr = Long.toString(Long.parseLong(intNumberStr));
            if("0".equals(intNumberStr)){
                intNumberStr = "";
            }
        }
        //整数部分超出处理能力则直接返回
        //最多支持9999仟万个亿，比如：9999567890123456.7899就是：玖仟玖佰玖拾玖万伍仟陆佰柒拾捌亿玖仟零壹拾贰万叁仟肆佰伍拾陆元柒角捌分玖厘
        if(intNumberStr.length() > INT_UNIT.length){
            LogUtil.getLogger().info("超出处理能力[{}]", amount);
            return amount;
        }
        /*
         * 判断第5位数字的单位“万”是否应加
         */
        boolean isMust5 = false;
        int intNumberStrLen = intNumberStr.length();
        if(intNumberStrLen > 4){
            String subIntNumberStr;
            if(intNumberStrLen > 8){
                //取得从低位数，第5到第8位的字串
                subIntNumberStr = intNumberStr.substring(intNumberStrLen-8, intNumberStrLen-4);
            }else{
                subIntNumberStr = intNumberStr.substring(0, intNumberStrLen-4);
            }
            isMust5 = Integer.parseInt(subIntNumberStr) > 0;
        }
        /*
         * 计算整数部分的中文
         */
        StringBuilder intNumberChinese = new StringBuilder();
        //整数部分的整形数组
        int[] intArray = JadyerUtil.intToIntArray(intNumberStr);
        for(int i=0; i<intArray.length; i++){
            //0出现在关键位置：1234（万）5678（亿）9012（万）3456（元）
            //特殊情况：10（拾元、壹拾元、壹拾万元、拾万元）
            String key = "";
            if(intArray[i] == 0){
                if((intArray.length - i) == 13) {                 //万（亿）（必填）
                    key = INT_UNIT[4];
                }else if((intArray.length - i) == 9) {            //亿（必填）
                    key = INT_UNIT[8];
                }else if((intArray.length - i) == 5 && isMust5) { //万（不必填）
                    key = INT_UNIT[4];
                }else if((intArray.length - i) == 1) {            //元（必填）
                    key = INT_UNIT[0];
                }
                //0遇非0时补零，不包含最后一位
                if((intArray.length - i) > 1 && intArray[i + 1] != 0){
                    key += NUMBERS[0];
                }
            }
            intNumberChinese.append(intArray[i] == 0 ? key : (NUMBERS[intArray[i]] + INT_UNIT[intArray.length - i - 1]));
        }
        /*
         * 计算小数部分的中文
         */
        StringBuilder decimalNumberChinese = new StringBuilder();
        //小数部分的整形数组
        int[] decimalArray = JadyerUtil.intToIntArray(decimalNumberStr);
        for(int i=0; i<decimalArray.length; i++){
            //超出小数部分的单位的个数，则直接舍去
            //假设DECIMAL_UNIT={"角", "分", "厘", "毫", "丝"}，则说明该方法只计算到角、分、厘、毫、丝五位
            //假设描述黄金价格：1.234567，我们会说它是壹元贰角叁分肆厘伍毫陆丝
            if(i == DECIMAL_UNIT.length){
                break;
            }
            decimalNumberChinese.append(decimalArray[i]==0 ? "" : (NUMBERS[decimalArray[i]] + DECIMAL_UNIT[i]));
        }
        /*
         * 返回结果
         */
        String chinese = intNumberChinese.append(decimalNumberChinese).toString();
        return chinese.endsWith("元") ? chinese+"整" : chinese;
    }


    //TODO 待测试
    ///**
    // * 中文數字转阿拉伯数组【十万九千零六十  --> 109060】
    // */
    //private static int chineseNumber2Int(String chineseNumber){
    //    int result = 0;
    //    int temp = 1;//存放一个单位的数字如：十万
    //    int count = 0;//判断是否有chArr
    //    char[] cnArr = new char[]{'壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'};
    //    char[] chArr = new char[]{'拾', '佰', '仟', '万', '亿'};
    //    for (int i = 0; i < chineseNumber.length(); i++) {
    //        boolean b = true;//判断是否是chArr
    //        char c = chineseNumber.charAt(i);
    //        for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
    //            if (c == cnArr[j]) {
    //                if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
    //                    result += temp;
    //                    temp = 1;
    //                    count = 0;
    //                }
    //                // 下标+1，就是对应的值
    //                temp = j + 1;
    //                b = false;
    //                break;
    //            }
    //        }
    //        if(b){//单位{'十','百','千','万','亿'}
    //            for (int j = 0; j < chArr.length; j++) {
    //                if (c == chArr[j]) {
    //                    switch (j) {
    //                        case 0:
    //                            temp *= 10;
    //                            break;
    //                        case 1:
    //                            temp *= 100;
    //                            break;
    //                        case 2:
    //                            temp *= 1000;
    //                            break;
    //                        case 3:
    //                            temp *= 10000;
    //                            break;
    //                        case 4:
    //                            temp *= 100000000;
    //                            break;
    //                        default:
    //                            break;
    //                    }
    //                    count++;
    //                }
    //            }
    //        }
    //        if (i == chineseNumber.length() - 1) {//遍历到最后一个字符
    //            result += temp;
    //        }
    //    }
    //    return result;
    //}
}