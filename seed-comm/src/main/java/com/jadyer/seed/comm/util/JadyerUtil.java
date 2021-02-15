/*
 *                    .::::.
 *                  .::::::::.
 *                 :::::::::::  FUCK YOU
 *             ..:::::::::::'
 *           '::::::::::::'
 *             .::::::::::
 *        '::::::::::::::..
 *             ..::::::::::::.
 *           ``::::::::::::::::
 *            ::::``:::::::::'        .:::.
 *           ::::'   ':::::'       .::::::::.
 *         .::::'      ::::     .:::::::'::::.
 *        .:::'       :::::  .:::::::::' ':::::.
 *       .::'        :::::.:::::::::'      ':::::.
 *      .::'         ::::::::::::::'         ``::::.
 *  ...:::           ::::::::::::'              ``::.
 * ```` ':.            ':::::::::'                  ::::..
 *                    '.:::::'                    ':'````..
 *
 *
 *
 **************************************************************
 *                                                            *
 *   .=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-.       *
 *    |                     ______                     |      *
 *    |                  .-"      "-.                  |      *
 *    |                 /            \                 |      *
 *    |     _          |              |          _     |      *
 *    |    ( \         |,  .-.  .-.  ,|         / )    |      *
 *    |     > "=._     | )(__/  \__)( |     _.=" <     |      *
 *    |    (_/"=._"=._ |/     /\     \| _.="_.="\_)    |      *
 *    |           "=._"(_     ^^     _)"_.="           |      *
 *    |               "=\__|IIIIII|__/="               |      *
 *    |              _.="| \IIIIII/ |"=._              |      *
 *    |    _     _.="_.="\          /"=._"=._     _    |      *
 *    |   ( \_.="_.="     `--------`     "=._"=._/ )    |      *
 *    |    > _.="                            "=._ <    |      *
 *    |   (_/                                    \_)   |      *
 *    |                                                |      *
 *    '-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-='      *
 *                                                            *
 *          LASCIATE OGNI SPERANZA, VOI CH'ENTRATE            *
 **************************************************************
 *
 *
 *
 *                    _ooOoo_
 *                   o8888888o
 *                   88" . "88
 *                   (| -_- |)
 *                   O\  =  /O
 *                ____/`---'\____
 *              .'  \\|     |//  `.
 *             /  \\|||  :  |||//  \
 *            /  _||||| -:- |||||-  \
 *            |   | \\\  -  /// |   |
 *            | \_|  ''\---/''  |   |
 *            \  .-\__  `-`  ___/-. /
 *          ___`. .'  /--.--\  `. . __
 *       ."" '<  `.___\_<|>_/___.'  >'"".
 *      | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *      \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                    `=---='
 * 
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *            佛祖保佑       永无BUG
 *            心外无法       法外无心
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 */

/*
=======================================================
                .----.
             _.'__    `.
         .--(^)(^^)---/#\
       .' @          /###\
       :         ,   #####
        `-..__.-' _.-\###/
              `;_:    `"'
            .'"""""`.
           /,  ya ,\\
          //狗神保佑  \\
          `-._______.-'
          ___`. | .'___
         (______|______)
=======================================================
 */

package com.jadyer.seed.comm.util;

import com.jadyer.seed.comm.constant.SeedConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 玄玉的开发工具类
 * ---------------------------------------------------------------------------------------------------------------
 * @version v3.22
 * @history v3.22-->增加mergeFile()：多文件合并为单文件
 * @history v3.21-->增加extractStackTraceCausedBy()：提取堆栈轨迹中表示真正错误提示的的Caused by: 部分
 * @history v3.20-->移动若干网络请求的相关方法至{@link RequestUtil}
 * @history v3.19-->移除buildSequenceNo()，可用{@link IDUtil}代替
 * @history v3.18-->增加escapeXSS()：XSS过滤
 * @history v3.17-->增加randomNumeric()、randomAlphabetic()：生成随机字符串，以替代已不推荐使用的RandomStringUtils
 * @history v3.16-->增加leftPadUseZero()：字符串左补零
 * @history v3.15-->增加getFullContextPath()：获取应用的完整根地址，并移动两个XML方法至{@link XmlUtil}
 * @history v3.14-->移动requestToBean()、beanCopyProperties()至{@link BeanUtil}，并移除若干重复造轮子的方法
 * @history v3.13-->增加获取本周第一天、判断是否本周第一天、判断是否本月第一天的三个方法
 * @history v3.12-->增加getPID()：获取应用运行进程的PID
 * @history v3.11-->增加hexToBytes():十六进制字符串转为byte[]
 * @history v3.10-->增加beanCopyProperties()：通过反射实现的JavaBean之间属性拷贝
 * @history v3.9-->修正打印入参为java.util.Map时可能引发的NullPointerException
 * @history v3.8-->增加：getDetailDate()，并修正部分细节
 * @history v3.7-->add method of escapeEmoji() for escape Emoji to *
 * @history v3.6-->add method of bytesToHex() for convert byte to hex
 * @history v3.5-->增加requestToBean()：将HttpServletRequest参数值转为JavaBean
 * @history v3.4-->增加extractHttpServletRequestMessage()：提取HTTP请求完整报文
 * @history v3.3-->增加isAjaxRequest()：判断是否为Ajax请求
 * @history v3.2-->增加getCurrentWeekStartDate()、getCurrentWeekEndDate()：获取本周开始和结束的时间
 * @history v3.1-->修改captureScreen()：增加是否自动打开生成的抓屏图片的功能
 * @history v3.0-->重命名htmlEscape()为escapeHtml()，并新增：escapeXml()
 * @history v2.9-->新增getIncreaseDate()：计算指定日期相隔一定天数后的日期
 * @history v2.8-->移除加解密相关方法到CodecUtil类中，增加了模拟OracleSequence、抓屏、格式化XML字符串、统计代码行数等方法
 * @history v2.7-->新增extractStackTrace()：提取堆栈信息
 * @history v2.6-->重命名了若干方法：使之更形象，并删除了：getStringSimple()、getStringForInt()
 * @history v2.5-->新增getStringForInt()：将阿拉伯字节数组转为整型数值
 * @history v2.4-->新增genAESEncrypt()、genAESDecrypt()：AES加解密
 * @history v2.3-->修改rightPadForByte()、leftPadForByte()：左右填充的字符为0x00
 * @history v2.2-->新增isNotEmpty()：判断输入的字符串或字节数组是否为非空
 * @history v2.1-->新增formatToHexStringWithASCII()：格式化字节数组为十六进制字符串
 * @history v2.0-->局部的StringBuffer一律StringBuilder之（本思路提示自坦克<captmjc@gmail.com>）
 * @history v1.5-->新增getStringSimple()：获取一个字符串的简明效果，返回的字符串格式类似于：abcd***hijk
 * @history v1.4-->新增getHexSign()：根据指定的签名密钥和算法签名Map<String, String>
 * @history v1.3-->修改getSysJournalNo()实现细节为：java.util.UUID.randomUUID()
 * @history v1.2-->新增getString()：字节数组转为字符串
 * @history v1.1-->新增getHexSign()：通过指定算法签名字符串
 * ---------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2012/12/22 19:00.
 */
public final class JadyerUtil {
    private JadyerUtil(){}

    /**
     * 生成指定长度的，由纯数字组成的，随机字符串
     * <p>
     *     注意：返回的字符串的首字符，不会是零
     * </p>
     */
    public static String randomNumeric(final int count) {
        //RandomStringUtils这个类不推荐使用了，用RandomStringGenerator来代替
        //return RandomStringUtils.randomNumeric(count);
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', '9').build();
        String str = generator.generate(count);
        while(str.startsWith("0")){
            str = generator.generate(count);
        }
        return str;
    }


    /**
     * 生成指定长度的，由纯小写字母组成的，随机字符串
     */
    public static String randomAlphabetic(final int count) {
        return new RandomStringGenerator.Builder().withinRange('a', 'z').build().generate(count);
    }


    /**
     * 构建serialVersionUID
     */
    public static long buildSerialVersionUID(){
        long serialVersionUID = new Random().nextLong();
        return serialVersionUID>0 ? serialVersionUID : -serialVersionUID;
    }


    /**
     * 多文件合并为单文件
     * @param destPath 合并后的单文件完整路径（含后缀）
     */
    public static void mergeFile(List<File> fileList, String destPath){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(destPath));
            for(File obj : fileList){
                BufferedReader br = new BufferedReader(new FileReader(obj));
                for(String line; (line=br.readLine())!=null;){
                    bw.write(line);
                    bw.newLine();
                }
                br.close();
            }
            bw.close();
        }catch(IOException e){
            throw new RuntimeException("多文件合并时出错", e);
        }
    }


    /**
     * 获取Map中的属性
     * <p>
     *     由于Map.toString()打印出来的参数值对，是横着一排的...参数多的时候，不便于查看各个值
     *     故此仿照commons-lang3.jar中的{@link org.apache.commons.lang3.builder.ReflectionToStringBuilder#toString(Object)}编写了本方法
     * </p>
     * <p>
     *     目前只支持Map<String,String>、Map<String,String[]>、Map<String,byte[]>三种类型
     * </p>
     */
    public static String buildStringFromMap(Map<String, ?> map){
        if(null==map || map.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(map.getClass().getName()).append("@").append(map.hashCode()).append("[");
        for(Map.Entry<String, ?> entry : map.entrySet()){
            sb.append("\n").append(entry.getKey()).append("=");
            //打印方式随值类型不同而不同
            Object value = entry.getValue();
            if(value instanceof String){
                sb.append(value);
            }
            if(value instanceof String[]){
                sb.append(Arrays.toString((String[])value));
            }
            if(value instanceof byte[]){
                sb.append(new String((byte[])value));
            }
        }
        return sb.append("\n]").toString();
    }


    /**
     * 获取实体类中的属性
     * <p>
     *     本方法用到了反射，其适用于所有的属性类型均为byte[]的JavaBean
     *     具体用途描述见{@link #buildStringFromMap(Map)}
     * </p>
     * @return String key11=value11 \n key22=value22 \n key33=value33 \n......
     */
    public static String buildStringFromJavaBeanOfByte(Object bean){
        if(null == bean){
            return "";
        }
        //局部的StringBuffer一律StringBuilder之
        StringBuilder sb = new StringBuilder();
        sb.append(bean.getClass().getName()).append("@").append(bean.hashCode()).append("[");
        for(Field field : bean.getClass().getDeclaredFields()){
            //构造getter方法
            String methodName = "get" + StringUtils.capitalize(field.getName());
            Object fieldValue;
            try{
                //执行getter方法,获取其返回值
                fieldValue = bean.getClass().getDeclaredMethod(methodName).invoke(bean);
            }catch(Exception e){
                //一旦发生异常，便将属性值置为UnKnown，故此处没必要一一捕获所有异常
                sb.append("\n").append(field.getName()).append("=UnKnown");
                continue;
            }
            if(fieldValue == null){
                sb.append("\n").append(field.getName()).append("=null");
            }else{
                sb.append("\n").append(field.getName()).append("=").append(new String((byte[])fieldValue));
            }
        }
        return sb.append("\n]").toString();
    }


    /**
     * 通过ASCII码将十进制的字节数组格式化为十六进制字符串
     * <p>
     *     使用说明详见{@link #buildHexStringWithASCII(byte[], int, int)}
     * </p>
     */
    public static String buildHexStringWithASCII(byte[] data){
        return buildHexStringWithASCII(data, 0, data.length);
    }


    /**
     * 通过ASCII码将十进制的字节数组格式化为十六进制字符串
     * <ul>
     *     <li>该方法常用于字符串的十六进制打印，打印时左侧为十六进制数值，右侧为对应的字符串原文</li>
     *     <li>在构造右侧的字符串原文时，该方法内部使用的是平台的默认字符集，来解码byte[]数组</li>
     *     <li>该方法在将字节转为十六进制时，默认使用的是{@link java.util.Locale#getDefault()}</li>
     *     <li>详见{@link java.lang.String#format(String, Object...)}方法和{@link java.lang.String#String(byte[], int, int)}构造方法</li>
     * </ul>
     * @param data   十进制的字节数组
     * @param offset 数组下标，标记从数组的第几个字节开始格式化输出
     * @param length 格式长度，其不得大于数组长度，否则抛出java.lang.ArrayIndexOutOfBoundsException
     * @return 格式化后的十六进制字符串
     */
    private static String buildHexStringWithASCII(byte[] data, int offset, int length){
        if(ArrayUtils.isEmpty(data)){
            return "";
        }
        int end = offset + length;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append("\r\n------------------------------------------------------------------------");
        boolean chineseCutFlag = false;
        for(int i=offset; i<end; i+=16){
            //X或x表示将结果格式化为十六进制整数
            sb.append(String.format("\r\n%04X: ", i-offset));
            sb2.setLength(0);
            for(int j=i; j<i+16; j++){
                if(j < end){
                    byte b = data[j];
                    if(b >= 0){ //ENG ASCII
                        sb.append(String.format("%02X ", b));
                        if(b<32 || b>126){ //不可见字符
                            sb2.append(" ");
                        }else{
                            sb2.append((char)b);
                        }
                    }else{ //CHA ASCII
                        if(j == i+15){ //汉字前半个字节
                            sb.append(String.format("%02X ", data[j]));
                            chineseCutFlag = true;
                            String s = new String(data, j, 2);
                            sb2.append(s);
                        }else if(j == i&&chineseCutFlag){ //后半个字节
                            sb.append(String.format("%02X ", data[j]));
                            chineseCutFlag = false;
                            String s = new String(data, j, 1);
                            sb2.append(s);
                        }else{
                            sb.append(String.format("%02X %02X ", data[j], data[j + 1]));
                            String s = new String(data, j, 2);
                            sb2.append(s);
                            j++;
                        }
                    }
                }else{
                    sb.append("   ");
                }
            }
            sb.append("| ");
            sb.append(sb2.toString());
        }
        sb.append("\r\n------------------------------------------------------------------------");
        return sb.toString();
    }


    /**
     * 通过ASCII码将十六进制的字节数组格式化为十六进制字符串
     * <p>
     *     使用说明详见{@link #buildHexStringWithASCII(byte[], int, int)}
     * </p>
     */
    public static String buildHexStringWithASCIIForHex(byte[] hexData, int offset, int length){
        if(ArrayUtils.isEmpty(hexData)){
            return "";
        }
        byte[] data = new byte[hexData.length];
        for (int i = 0; i < data.length; i++) {
            //获取16进制数的ASCII值,比如16进制的41对应ASCII的65
            data[i] = Integer.valueOf(""+hexData[i], 16).byteValue();
        }
        return buildHexStringWithASCII(data, offset, length);
    }


    /**
     * convert byte to hex
     * <p>
     *     等效于{@link org.apache.commons.codec.binary.Hex#encodeHexString(byte[])}
     * </p>
     */
    public static String bytesToHex(byte[] in, boolean toLowerCase){
        //String hex = new BigInteger(1, in).toString(16);
        //int paddingLength = (in.length * 2) - hex.length();
        //if(paddingLength > 0){
        //    return String.format("%0" + paddingLength + "d", 0) + hex;
        //}else{
        //    return hex;
        //}
        //上面注释的是另一种经过验证ok的写法
        final StringBuilder sb = new StringBuilder();
        for(byte b : in){
            sb.append(String.format(toLowerCase ? "%02x" : "%02X", b));
        }
        return sb.toString();
    }


    /**
     * convert hex to byte
     * <p>
     *     等效于{@link org.apache.commons.codec.binary.Hex#decodeHex(char[])}
     *     本例的参数可传hex.toCharArray()
     * </p>
     */
    public static byte[] hexToBytes(String hex){
        byte[] binary = new byte[hex.length() / 2];
        for(int i=0; i<binary.length; i++){
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return binary;
    }


    /**
     * 字符串类型的数字转为整形数组
     */
    public static int[] intToIntArray(String number){
        int[] intArray = new int[number.length()];
        for(int i=0; i<number.length(); i++){
            intArray[i] = number.charAt(i) - '0';
        }
        return intArray;
    }


    /**
     * 判断一个整数是否为奇数
     * @see 1.本方法中 0/-0/-2/2 都不是奇数，-1/1/-3/3 都是奇数
     * @see 2.算术运算和逻辑运行要比乘除运算更高效，计算的结果也会更快
     * @see 3.如果使用 num%2 == 1 作为判断条件，那么负奇数的话就不适用了
     * @return true--是奇数，false--不是奇数
     */
    public static boolean isOddNumber(int num){
        return (num & 1) != 0;
    }


    /**
     * 计算两个整数的百分比
     * @param x        分子
     * @param y        分母
     * @param fraction 指定计算结果保留到小数点后几位
     * @return 返回类似这样的字符串"66.67%"
     */
    public static String getPercent(int x, int y, int fraction){
        double fenZi = x * 1.0;
        double fenMu = y * 1.0;
        double number = fenZi / fenMu;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(fraction);
        return nf.format(number);
    }


    /**
     * 字符串右补字节
     * <ul>
     *     <li>鉴于该方法常用于构造响应给支付平台相关系统的响应报文头，故其默认采用0x00右补字节且总字节长度为100字节</li>
     *     <li>若想自己指定所补字节，可以使用{@link #rightPadUseByte(String, int, int, String)}</li>
     * </ul>
     */
    public static String rightPadUseByte(String str){
        return rightPadUseByte(str, 100, 0, "UTF-8");
    }


    /**
     * 字符串右补字节
     * <ul>
     *     <li>若str对应的byte[]长度不小于size，则按照size截取str对应的byte[]，而非原样返回str</li>
     *     <li>所以size参数很关键...事实上之所以这么处理，是由于支付处理系统接口文档规定了字段的最大长度</li>
     *     <li>若对普通字符串进行右补字符，建议{@link org.apache.commons.lang.StringUtils#rightPad(String, int, String)}</li>
     * </ul>
     * @param size          该参数指的不是字符串长度，而是字符串所对应的byte[]长度
     * @param padStrByASCII 该值为所补字节的ASCII码，如32表示空格，48表示0，64表示@等
     * @param charset       由右补字节后的字节数组生成新字符串时所采用的字符集
     */
    public static String rightPadUseByte(String str, int size, int padStrByASCII, String charset){
        byte[] srcByte = str.getBytes();
        byte[] destByte;
        if(srcByte.length >= size){
            destByte = Arrays.copyOf(srcByte, size);
        }else{
            destByte = Arrays.copyOf(srcByte, size);
            Arrays.fill(destByte, srcByte.length, size, (byte)padStrByASCII);
        }
        return StringUtils.toEncodedString(destByte, Charset.forName(charset));
    }


    /**
     * 字符串左补字节
     * <ul>
     *     <li>该方法默认采用0x00左补字节</li>
     *     <li>若想自己指定所补字节,可以使用{@link #leftPadUseByte(String, int, int, String)}</li>
     * </ul>
     */
    public static String leftPadUseByte(String str, int length, String charset){
        return leftPadUseByte(str, length, 0, "UTF-8");
    }


    /**
     * 字符串左补字节
     * <ul>
     *     <li>若str对应的byte[]长度不小于length，则按照length截取str对应的byte[]，而非原样返回str</li>
     *     <li>所以length参数很关键...事实上之所以这么处理，是由于支付处理系统接口文档规定了字段的最大长度</li>
     * </ul>
     * @param padStrByASCII 该值为所补字节的ASCII码，如32表示空格，48表示0，64表示@
     * @param charset       由左补字节后的字节数组生成新字符串时所采用的字符集
     */
    public static String leftPadUseByte(String str, int length, int padStrByASCII, String charset){
        byte[] srcByte = str.getBytes();
        byte[] destByte = new byte[length];
        Arrays.fill(destByte, (byte)padStrByASCII);
        if(srcByte.length >= length){
            System.arraycopy(srcByte, 0, destByte, 0, length);
        }else{
            System.arraycopy(srcByte, 0, destByte, length-srcByte.length, srcByte.length);
        }
        return StringUtils.toEncodedString(destByte, Charset.forName(charset));
    }


    /**
     * 字符串左补零
     * @param str    待补零的字符串
     * @param length 补零后的总长度
     * @return 假设str=3，size=4，则返回0003
     */
    public static String leftPadUseZero(String str, int length){
        char[] srcArray = str.toCharArray();
        char[] destArray = new char[length];
        Arrays.fill(destArray, '0');
        if(srcArray.length >= length){
            System.arraycopy(srcArray, 0, destArray, 0, length);
        }else{
            System.arraycopy(srcArray, 0, destArray, length-srcArray.length, srcArray.length);
        }
        return String.valueOf(destArray);
    }


    /**
     * XSS过滤
     */
    public static String escapeXSS(String input) {
        if(StringUtils.isEmpty(input)){
            return "";
        }
        try {
            input = URLDecoder.decode(input, SeedConstants.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            //ingore
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<input.length(); i++){
            char c = input.charAt(i);
            switch(c){
                case '>' : sb.append('＞'); break;
                case '<' : sb.append('＜'); break;
                case '\'': sb.append('‘'); break;
                case '\"': sb.append('“'); break;
                case '&' : sb.append('＆'); break;
                case '\\': sb.append('＼'); break;
                case '#' : sb.append('＃'); break;
                case '(' : sb.append('（'); break;
                case ')' : sb.append('）'); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * 转义HTML字符串
     * <ul>
     *     <li>对输入参数中的敏感字符进行过滤替换，防止用户利用JavaScript等方式输入恶意代码</li>
     *     <li>String input = <img src='http://t1.baidu.com/it/fm=0&gp=0.jpg'/></li>
     *     <li>HtmlUtils.htmlEscape(input);         //from spring.jar</li>
     *     <li>StringEscapeUtils.escapeHtml(input); //from commons-lang.jar</li>
     *     <li>尽管Spring和Apache都提供了字符转义的方法，但Apache的StringEscapeUtils功能要更强大一些</li>
     *     <li>StringEscapeUtils提供了对HTML,Java,JavaScript,SQL,XML等字符的转义和反转义</li>
     *     <li>但二者在转义HTML字符时，都不会对单引号和空格进行转义，而本方法则提供了对它们的转义</li>
     * </ul>
     * @return String 过滤后的字符串
     */
    public static String escapeHtml(String input) {
        if(StringUtils.isBlank(input)){
            return "";
        }
        input = input.replaceAll("&", "&amp;");
        input = input.replaceAll("<", "&lt;");
        input = input.replaceAll(">", "&gt;");
        input = input.replaceAll(" ", "&nbsp;");
        input = input.replaceAll("'", "&#39;");   //IE暂不支持单引号的实体名称,而支持单引号的实体编号,故单引号转义成实体编号,其它字符转义成实体名称
        input = input.replaceAll("\"", "&quot;"); //双引号也需要转义，所以加一个斜线对其进行转义
        input = input.replaceAll("\n", "<br/>");  //不能把\n的过滤放在前面，因为还要对<和>过滤，这样就会导致<br/>失效了
        return input;
    }


    /**
     * 转义emoji表情为*星号
     * <p>
     *     现在的APP或者微信已经广泛支持Emoji表情了，但是MySQL的UTF8编码对Emoji的支持却不是很好
     *     所以通常会遇到这样的异常提示Incorrect string value: '\xF0\x90\x8D\x83...' for column
     *     原因是MySQL的UTF8编码最多能支持3个字节，而Emoji表情字符所使用的UTF8编码很多都是4个甚至6个字节
     * </p>
     * ----------------------------------------------------------------------------------------------
     * 解决方案有两种
     * 1、使用utf8mb4的MySQL编码存储表情字符（不过在浏览器显示时，这些表情字符显示的是一个空心的方框）
     * 2、过滤表情字符
     * 第一种方案需要注意很多：比如MySQL版本、MySQL的表和数据库配置、MySQL Connector的版本等等
     * 所以写了这个第二种方案的转义方法
     * ----------------------------------------------------------------------------------------------
     */
    public static String escapeEmoji(String emoji){
        if(StringUtils.isNotBlank(emoji)){
            return emoji.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
        }else{
            return emoji;
        }
    }


    /**
     * 抓屏方法
     * <p>
     *     该方法抓的是全屏，并且当传入的fileName参数为空时会将抓屏图片默认保存到用户桌面上
     * </p>
     * @param fileName        抓屏后的图片保存名称（含保存路径及后缀），传空时会把图片自动保存到桌面
     * @param isAutoOpenImage 是否自动打开图片
     * @return 抓屏成功返回true，反之false
     */
    public static boolean captureScreen(String fileName, boolean isAutoOpenImage){
        if(StringUtils.isBlank(fileName)){
            String desktop = FileSystemView.getFileSystemView().getHomeDirectory().getPath();
            String separator = System.getProperty("file.separator");
            String imageName = "截屏_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";
            fileName = desktop + separator + imageName;
        }
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);
        File file = new File(fileName);
        //获取屏幕大小
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        try {
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, fileSuffix, file);
            //自动打开图片
            if(isAutoOpenImage){
                if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)){
                    Desktop.getDesktop().open(file);
                }
            }
        } catch (AWTException | IOException e) {
            return false;
        }
        return true;
    }


    /**
     * 提取堆栈信息
     * <p>
     *     等价于{@link org.apache.commons.lang3.exception.ExceptionUtils#getStackTrace(Throwable)}
     * </p>
     */
    public static String extractStackTrace(Throwable cause){
        //ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        //cause.printStackTrace(new PrintStream(byteArrayOut));
        //return byteArrayOut.toString();
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            cause.printStackTrace(pw);
            return sw.toString();
        }
    }


    /**
     * 提取堆栈轨迹中的真实异常
     */
    public static String extractStackTraceCausedBy(Throwable cause){
        String allMsg = extractStackTrace(cause);
        if(allMsg.contains("Caused by: ")){
            allMsg = allMsg.substring(allMsg.lastIndexOf("Caused by: ") + 11);
        }
        if(allMsg.contains(": ")){
            allMsg = allMsg.substring(allMsg.indexOf(": ")+2, allMsg.indexOf("\n"));
        }else{
            allMsg = allMsg.substring(0, allMsg.indexOf("\n"));
        }
        if(allMsg.endsWith("\r")){
            allMsg = allMsg.substring(0, allMsg.length()-1);
        }
        return allMsg;
    }


    /**
     * 统计代码行数
     * -------------------------------------------------------------------------------------------------------------
     * 1)目前仅支持*.java;*.xml;*.properties;*.jsp;*.htm;*.html六种文件格式
     * 2)在统计jsp或htm或html文件时，也会将文件中的js或css标签里面的代码统计进去，即也会正确计算页面中js或css代码的注释或空行等
     * 3)要注意两种特殊情况，对于这种特殊情况，本方法也进行了处理
     *   <style type="text/css"><!--css code//--></style>
     *   <script type="text/javascript"><!--js code//--></script>
     * 4)本方法也支持统计*.js和*.css文件中的代码行数，只不过实际中js和css文件通常是现成的，只有很少的一部分才是程序员自己写的代码
     *   而这一部分代码通常都写在jsp或html页面中，故本方法未统计*.js和*.css文件
     *   如需统计，则只需初始化一下js和css的注释标记，并在允许的文件类型列表中将js和css添加进去即可
     * 5)本方法会将统计结果放到参数resultMap中
     * 6)不可在本方法中为resultMap的几个键的值设定初始值，因为本方法内部存在递归操作...但可以在调用方传入参数前初始化，如下所示
     *   Map<String, Integer> resultMap = new HashMap<String, Integer>();
     *   resultMap.put("total", 0);
     *   resultMap.put("code", 0);
     *   resultMap.put("comment", 0);
     *   resultMap.put("blank", 0);
     *   然后在调用本方法时传进来即可:JadyerUtil.getCodeLineCounts(file, resultMap);
     *   待本方法执行完毕后，传进来的resultMap里面就有值了，调用方就可以获取到里面的值进行业务处理了
     * -------------------------------------------------------------------------------------------------------------
     * @param codeFile  待统计的File类，可以是具体的文件或目录
     * @param resultMap 用于记录统计结果，键为total、code、comment、blank
     */
    public static void getCodeLineCounts(File codeFile, Map<String, Integer> resultMap){
        boolean isLoopOver = false;
        if(codeFile.isDirectory()){
            int loopCount = 0;
            int fileCount = codeFile.listFiles().length;
            for(File file : codeFile.listFiles()){
                //排除这几类文件--->[.classpath][.project][.myhibernatedata][.mymetadata][.springBeans]
                //排除这几类文件夹-->[.settings文件夹][.myeclipse文件夹][.svn文件夹]
                if(file.getName().startsWith(".")){
                    loopCount++;
                    continue;
                }
                getCodeLineCounts(file, resultMap);
                loopCount++;
                if(loopCount == fileCount){
                    isLoopOver = true;
                }
            }
        }
        if(isLoopOver){
            return;
        }
        //只统计*.java;*.xml;*.properties;*.jsp;*.htm;*.html六种文件格式
        List<String> allowFileTypeList = new ArrayList<>();
        allowFileTypeList.add("java");
        allowFileTypeList.add("xml");
        allowFileTypeList.add("properties");
        allowFileTypeList.add("jsp");
        allowFileTypeList.add("htm");
        allowFileTypeList.add("html");
        String codeFileSuffix = codeFile.getName().substring(codeFile.getName().lastIndexOf('.')+1);
        if(!allowFileTypeList.contains(codeFileSuffix)){
            return;
        }
        /*
         * 初始化代码中的注释标记
         */
        int countsTotal = 0;    //合计行数
        int countsCode = 0;     //实际代码的行数
        int countsComment = 0;  //注释的行数
        int countsBlank = 0;    //空行的行数
        String content;         //按行读取到的内容
        boolean isReadInComments = false;            //用于标记是否已读到注释行中
        int multiCommentSuffixIndex = 0;             //用于记录已读取到的多行标记的起始字符的下标
        String[] multiCommentPrefix = new String[1]; //多行注释的起始字符
        String[] multiCommentSuffix = new String[1]; //多行注释的结尾字符
        String singleCommentPrefix;                  //单行注释的起始字符
        switch (codeFileSuffix) {
            case "java":
                multiCommentPrefix[0] = "/*";
                multiCommentSuffix[0] = "*/";
                singleCommentPrefix = "//";
                break;
            case "xml":
                multiCommentPrefix[0] = "<!--";
                multiCommentSuffix[0] = "-->";
                singleCommentPrefix = "https://jadyer.cn/";
                break;
            case "properties":
                multiCommentPrefix[0] = "https://jadyer.cn/";
                multiCommentSuffix[0] = "https://jadyer.cn/";
                singleCommentPrefix = "#";
                break;
            case "jsp":
                multiCommentPrefix = new String[3]; //注意多行标记的起始和结尾字符的下标,应该是一一对应关系
                multiCommentSuffix = new String[3]; //如此以便于下面判断已读取的起始字符所对应的结尾字符的下标,即上面定义的multiCommentSuffixIndex参数
                multiCommentPrefix[0] = "<!--";     //匹配JSP文件中的多行注释
                multiCommentSuffix[0] = "-->";      //实际处理时应注意<style type="text/css"><!--css code//--></style>以及<script type="text/javascript"><!--js code//--></script>
                multiCommentPrefix[1] = "/*";       //匹配JSP文件中java或js代码的单行或多行注释,以及css代码的单行注释
                multiCommentSuffix[1] = "*/";
                multiCommentPrefix[2] = "<%--";     //匹配JSP文件中的多行注释
                multiCommentSuffix[2] = "--%>";
                singleCommentPrefix = "//";         //匹配JSP文件中java或js代码的单行注释
                break;
            case "htm":
            case "html":
                multiCommentPrefix = new String[2];
                multiCommentSuffix = new String[2];
                multiCommentPrefix[0] = "<!--";
                multiCommentSuffix[0] = "-->";
                multiCommentPrefix[1] = "/*";
                multiCommentSuffix[1] = "*/";
                singleCommentPrefix = "//";
                break;
            default:
                multiCommentPrefix[0] = "https://jadyer.cn/";
                multiCommentSuffix[0] = "https://jadyer.cn/";
                singleCommentPrefix = "https://jadyer.cn/";
                break;
        }
        /*
         * 开始统计
         */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(codeFile), "UTF-8"));
            while(null != (content=br.readLine())){
                countsTotal++;
                content = content.trim();
                if(0 == content.length()){
                    countsBlank++;
                    continue; //空行读取完毕,就不需要再往下判断了
                }
                //特殊处理<style type="text/css"><!--css code//--></style>
                //以及<script type="text/javascript"><!--js code//--></script>
                //并使用[!isReadInComments]过滤调多行注释中含有的css和js标记的情况
                if(!isReadInComments && StringUtils.equalsAny(codeFileSuffix, "jsp", "htm", "html")){
                    //css和js标记的起始标签分别有不止一种的写法,故startsWith
                    if(content.startsWith("<style") || content.startsWith("<script")){
                        //这里之所以取下标为0的元素,是因为上面在初始化多行的注释标记时,均将页面中的<!---->注释标记放置在第0个元素
                        multiCommentPrefix[0] = "https://jadyer.cn/";
                        multiCommentSuffix[0] = "https://jadyer.cn/";
                    }
                    if(StringUtils.equalsAny(content, "</style>", "</script>")){
                        multiCommentPrefix[0] = "<!--";
                        multiCommentSuffix[0] = "-->";
                    }
                }
                if(isReadInComments){
                    countsComment++;
                }
                for(int i=0; i<multiCommentPrefix.length; i++){
                    //多加一个[!isReadInComments]判断是为了防止有人在多行注释中使用其它的多行注释标记再进行多行注释,如下面是一个JSP中的例子
                    //<%--
                    //这是JSP中的多行标记
                    //<!--这是JSP中的另一种多行标记-->
                    //这是JSP中的多行标记
                    //--%>
                    //另外multiCommentSuffixIndex的初始值设置为任何都可以,也不需要在这里重新设置其为初始值,因为有了[!isReadInComments]限制
                    if(!isReadInComments && content.startsWith(multiCommentPrefix[i])){
                        isReadInComments = true;
                        countsComment++;
                        multiCommentSuffixIndex = i;
                        //这里不能continue,因为它是用来处理多行注释标记的或者是由多行注释编写的单行注释,如/*This is comment*/
                        //应该在找到多行标记的结尾标记时,再continue
                        //continue;
                    }
                }
                //这里一定要多加一个[isReadInComments]过滤,否则很有可能当读到css中恰好注释在css代码之后的注释时
                //走到这里的循环中,导致本次读取到的content明明是code,却被continue
                //即css中的代码类似这样[float:left; /*css comment*/],而之前恰好读取过类似这样的/*comment*/注释
                //导致multiCommentSuffix[multiCommentSuffixIndex]取到的值就是[*/]
                //另外multiCommentSuffixIndex的初始值设置为任何都可以,也不需要在这里重新设置其为初始值,因为有了[isReadInComments]限制
                if(isReadInComments && content.endsWith(multiCommentSuffix[multiCommentSuffixIndex])){
                    isReadInComments = false;
                    continue;
                }
                if(!isReadInComments){
                    if(content.startsWith(singleCommentPrefix)){
                        countsComment++;
                    }else{
                        countsCode++;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        } finally {
            if(null != br){
                try {
                    br.close();
                } catch (IOException e) {
                    //nothing to do
                }
            }
        }
        resultMap.put("total", (resultMap.get("total")==null ? 0 : resultMap.get("total")) + countsTotal);
        resultMap.put("code", (resultMap.get("code")==null ? 0 : resultMap.get("code")) + countsCode);
        resultMap.put("comment", (resultMap.get("comment")==null ? 0 : resultMap.get("comment")) + countsComment);
        resultMap.put("blank", (resultMap.get("blank")==null ? 0 : resultMap.get("blank")) + countsBlank);
    }
}