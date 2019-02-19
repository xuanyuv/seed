package com.jadyer.seed.test;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.test.model.Java8StreamInfo;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        //这里以625247012888xxxx举例
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
                        //前12位算出来的相加之和是51
                        if((sum_i + sum_k + j + m + 51) % 10 == 0){
                            //if(i!=4 && m!=4 && i==j){
                                System.out.println(i + "" + j + "" + k + "" + m);
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
                //noinspection UnusedAssignment
                str += "22";
                System.out.println("22执行了");
            }
            return str;
        }
    }


    @Test
    public void java8StreamTest(){
        Java8StreamInfo info01 = new Java8StreamInfo(3, new BigDecimal("20.56"));
        Java8StreamInfo info02 = new Java8StreamInfo(6, new BigDecimal("29.44"));
        Java8StreamInfo info03 = new Java8StreamInfo(3, new BigDecimal("38.88"));
        List<Java8StreamInfo> dataList = Arrays.asList(info03, info01, info02);
        System.out.println("-----------------------------------------------------------------------------------------");
        //过滤
        List<Java8StreamInfo> newDataList = dataList.stream().filter(x -> new BigDecimal("30").compareTo(x.getLoanAmt())>0).collect(Collectors.toList());
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
        System.out.println("-----------------------------------------------------------------------------------------");
        //获取某个字段的列表（未去重）
        List<Integer> loanTermList = dataList.stream().map(Java8StreamInfo::getLoanTerm).collect(Collectors.toList());
        loanTermList.forEach(loanTerm -> System.out.println("期数列表的数据：" + loanTerm));
        System.out.println("-----------------------------------------------------------------------------------------");
        //求和
        BigDecimal allAmt = dataList.stream().map(Java8StreamInfo::getLoanAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("所有金额之和为：" + allAmt);
        System.out.println("-----------------------------------------------------------------------------------------");
        //最大值
        Optional<Java8StreamInfo> infoOptional = dataList.stream().max(Comparator.comparing(Java8StreamInfo::getLoanAmt));
        System.out.println("金额最大的数据：" + JSON.toJSONString(infoOptional.orElseThrow(RuntimeException::new)));
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}