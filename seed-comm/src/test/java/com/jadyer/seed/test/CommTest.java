package com.jadyer.seed.test;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.test.model.Java8StreamInfo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/3/14 16:57.
 */
public class CommTest {
    @Test
    public void common() {
        System.out.println("java.io.tmpdir is " + System.getProperty("java.io.tmpdir"));
    }


    /**
     * 信用卡卡号生成规则
     * --------------------------------------
     * Luhn算法，也称为“模10”（Mod 10）算法
     * --------------------------------------
     * Comment by 玄玉<https://jadyer.cn/> on 2019/2/19 14:57.
     */
    @Test
    public void buildCreditCardNo(){
        //信用卡卡号前12位
        String preNo = "625247018888";
        char[] preNos = preNo.toCharArray();
        //信用卡卡号前12位算出来的相加之和
        int preSum = 0;
        for(int i=0,len=preNos.length; i<len; i++){
            int digit = Integer.parseInt(preNos[i] + "");
            if(!JadyerUtil.isOddNumber(i)){
                digit = digit * 2;
                if(digit > 9){
                    digit = digit - 9;
                }
            }
            preSum = preSum + digit;
        }
        //每行最多打印的个数
        int printCountMax = 17;
        //标记每行已打印的个数
        int printCountFlag = 0;
        //倒数第四位数字
        for(int i=0; i<10; i++){
            //倒数第三位数字
            for(int j=0; j<10; j++){
                //倒数第二位数字
                for(int k=0; k<10; k++){
                    //倒数第一位数字
                    for(int m=0; m<10; m++){
                        int sum_i = 0;
                        int sum_k = 0;
                        for(char aa : String.valueOf(i * 2).toCharArray()){
                            sum_i = sum_i + Integer.parseInt(aa + "");
                        }
                        for(char cc : String.valueOf(k * 2).toCharArray()){
                            sum_k = sum_k + Integer.parseInt(cc + "");
                        }
                        if((sum_i + sum_k + j + m + preSum) % 10 == 0){
                            //if(i==j && i!=4 && m!=4){
                                System.out.print("  " + i + "" + j + "" + k + "" + m);
                                printCountFlag++;
                                if(printCountFlag > printCountMax - 1){
                                    System.out.println();
                                    printCountFlag = 0;
                                }
                            //}
                        }
                    }
                }
            }
        }
    }


    /**
     * return和try{}finally{}的先后执行测试
     * 目前控制台输出：11执行了---22执行了---aa11...22
     */
    @Test
    public void finallyReturnTest(){
        System.out.println(FinallyReturn.print());
    }
    private static class FinallyReturn{
        static String print(){
            String str = "aa";
            try{
                str += "11";
                System.out.println("11执行了");
                throw new IllegalArgumentException("非法参数");
                //return str;
            }catch(Exception e){
                str += "...";
                //throw new RuntimeException("测试ReturnFinally时发生异常-->[" + e.getMessage() + "]");
            }finally{
                str += "22";
                System.out.println("22执行了");
            }
            return str;
        }
    }


    @Test
    public void java8StreamTest(){
        Java8StreamInfo info01 = new Java8StreamInfo(3, new BigDecimal("20.56"), 23L);
        Java8StreamInfo info02 = new Java8StreamInfo(6, new BigDecimal("29.44"), 26L);
        Java8StreamInfo info03 = new Java8StreamInfo(3, new BigDecimal("38.88"), 33L);
        List<Java8StreamInfo> dataList = Arrays.asList(info03, info01, info02);
        System.out.println("-----------------------------------------------------------------------------------------");
        //降序排列
        dataList.sort((o1, o2) -> o2.getLoanTerm() - o1.getLoanTerm());
        System.out.println("降序排序后得到数据：" + JSON.toJSONString(dataList));
        //升序排序
        dataList.sort(Comparator.comparingInt(Java8StreamInfo::getLoanTerm));
        System.out.println("升序排序后得到数据：" + JSON.toJSONString(dataList));
        System.out.println("-----------------------------------------------------------------------------------------");
        //过滤（注：过滤后得到List时，若空值，那么得到的会是空List，不是null）
        Java8StreamInfo newData = dataList.stream().filter(x -> new BigDecimal("22").compareTo(x.getLoanAmt())>0).findFirst().orElseThrow(NullPointerException::new);
        System.out.println("过滤后得到对象：" + JSON.toJSONString(newData));
        List<Java8StreamInfo> newDataList = dataList.stream().filter(x -> new BigDecimal("3").compareTo(x.getLoanAmt())>0).collect(Collectors.toList());
        newDataList.forEach(info -> System.out.println("过滤后得到数据：" + JSON.toJSONString(info)));
        newDataList.forEach(info -> {
            if(new BigDecimal("25").compareTo(info.getLoanAmt()) > 0){
                System.out.println("再过滤得到数据：" + JSON.toJSONString(info));
            }
        });
        System.out.println("-----------------------------------------------------------------------------------------");
        //根据期数分组后转Map
        Map<Integer, List<Java8StreamInfo>> dataMap = dataList.stream().collect(Collectors.groupingBy(Java8StreamInfo::getLoanTerm));
        dataMap.forEach((key, value) -> System.out.println("分组后得到数据：key=" + key + "，value=" + JSON.toJSONString(value)));
        Map<Long, BigDecimal> dataMap01 = dataList.stream().collect(Collectors.toMap(Java8StreamInfo::getLoanCount, Java8StreamInfo::getLoanAmt));
        System.out.println("分组后得到数据（value是对象的某个属性）：" + JSON.toJSONString(dataMap01));
        Map<Integer, BigDecimal> dataMap02 = dataList.stream().collect(Collectors.toMap(Java8StreamInfo::getLoanTerm, Java8StreamInfo::getLoanAmt, (key1, key2)->key2));
        System.out.println("分组后得到数据（value是对象的某个属性：key冲突时第二个覆盖第一个）：" + JSON.toJSONString(dataMap02));
        Map<Long, Java8StreamInfo> dataMap03 = dataList.stream().collect(Collectors.toMap(Java8StreamInfo::getLoanCount, Java8StreamInfo->Java8StreamInfo));
        System.out.println("分组后得到数据（value是对象本身：Java8StreamInfo->Java8StreamInfo是一个返回本身的lambda表达式）：" + JSON.toJSONString(dataMap03));
        Map<Long, Java8StreamInfo> dataMap04 = dataList.stream().collect(Collectors.toMap(Java8StreamInfo::getLoanCount, Function.identity()));
        System.out.println("分组后得到数据（value是对象本身：Function.identity()是简洁写法，也是返回对象本身）：" + JSON.toJSONString(dataMap03));
        System.out.println("-----------------------------------------------------------------------------------------");
        //获取某个字段的列表（注：若源List是一个空List，那么得到的也是空List，不是null）
        List<Integer> loanTermList = dataList.stream().map(Java8StreamInfo::getLoanTerm).collect(Collectors.toList());
        loanTermList.forEach(loanTerm -> System.out.println("期数列表的数据（未去重）：" + loanTerm));
        List<Integer> loanTermDistinctList = dataList.stream().map(Java8StreamInfo::getLoanTerm).distinct().collect(Collectors.toList());
        System.out.println("期数列表的数据（去重后）：" + JSON.toJSONString(loanTermDistinctList));
        List<String> loanTermStringDistinctList = dataList.stream().map(Java8StreamInfo::getLoanTerm).map(String::valueOf).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        System.out.println("期数列表的数据（去重后）（转String）：" + JSON.toJSONString(loanTermStringDistinctList));
        System.out.println("-----------------------------------------------------------------------------------------");
        //求和
        System.out.println("所有期数之和为：" + dataList.stream().mapToInt(Java8StreamInfo::getLoanTerm).sum());
        System.out.println("所有次数之和为：" + dataList.stream().mapToLong(x -> null==x.getLoanCount() ? 0L : x.getLoanCount()).sum());
        System.out.println("所有金额之和为：" + dataList.stream().map(Java8StreamInfo::getLoanAmt).reduce(BigDecimal.ZERO, BigDecimal::add));
        System.out.println("同期金额之和为：" + dataList.stream().filter(x -> x.getLoanTerm()==3).map(Java8StreamInfo::getLoanAmt).reduce(BigDecimal.ZERO, BigDecimal::add));
        System.out.println("-----------------------------------------------------------------------------------------");
        //最大值
        Optional<Java8StreamInfo> infoOptional = dataList.stream().max(Comparator.comparing(Java8StreamInfo::getLoanAmt));
        System.out.println("金额最大的数据：" + JSON.toJSONString(infoOptional.orElseThrow(RuntimeException::new)));
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}