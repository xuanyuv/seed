package com.jadyer.seed.comm.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 字节字符等工具类
 * -----------------------------------------------------------------------------------------------------------
 * @version v1.1
 * @history v1.1-->增加若干字节字符处理方法
 * @history v1.0-->初建并增加：byteToHex()
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2023/01/18 15:42.
 */
public class ByteUtil {
    private ByteUtil(){}

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
        for (int i=0,len=data.length; i<len; i++) {
            // 获取16进制数的ASCII值，比如16进制的41对应ASCII的65
            data[i] = Integer.valueOf(""+hexData[i], 16).byteValue();
        }
        return buildHexStringWithASCII(data, offset, length);
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
                            if(j+1 == end){
                                // 20230114新增：begin--->
                                sb.append(String.format("%02X    ", data[j]));
                                String s = new String(data, j, 1);
                                sb2.append(s);
                                // 20230114新增：end<-----
                            }else{
                                sb.append(String.format("%02X %02X ", data[j], data[j + 1]));
                                String s = new String(data, j, 2);
                                sb2.append(s);
                            }
                            j++;
                        }
                    }
                }else{
                    sb.append("   ");
                }
            }
            sb.append("| ");
            sb.append(sb2);
        }
        sb.append("\r\n------------------------------------------------------------------------");
        return sb.toString();
    }


    /**
     * convert byte to hex
     */
    public static String byteToHex(byte data){
        String hex = Integer.toHexString(data & 0xFF);
        if(hex.length() < 2){
            hex = "0" + hex;
        }
        return hex;
    }


    /**
     * convert bytes to hex
     * ----------------------------------------------------------------------------
     * 等效于{@link org.apache.commons.codec.binary.Hex#encodeHexString(byte[])}
     * ----------------------------------------------------------------------------
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
     * convert hex to bytes
     * ----------------------------------------------------------------------
     * 等效于{@link org.apache.commons.codec.binary.Hex#decodeHex(char[])}
     * ----------------------------------------------------------------------
     */
    public static byte[] hexToBytes(String hex){
        byte[] binary = new byte[hex.length() / 2];
        for(int i=0,len=binary.length; i<len; i++){
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return binary;
    }


    /**
     * 十六进制ASCII码转字符串
     * @param datas 待转换的字节数组
     */
    public static String asciiToString(byte[] datas){
        // 去掉尾部填充的0x00
        int j = 32;
        if (datas[datas.length-1] == 0x00) {
            for (int i=datas.length-1; i>=0; i--) {
                if(datas[i]==0x00 && i==0){
                    return "";
                }
                if(datas[i]==0x00 && datas[i-1]!=0x00){
                    j = i;
                    break;
                }
            }
        }
        // 得到有效数组（已去掉尾部填充的0x00）
        byte[] data02 = Arrays.copyOf(datas, j);
        return new String(data02, StandardCharsets.UTF_8);
    }


    /**
     * 字符串转十六进制ASCII码
     * @param data 待转换的字符串
     * @return 长度为32的字节数组（不足32则尾部补0x00）
     */
    public static byte[] stringToASCII(String data) {
        if(data.length() > 32){
            throw new RuntimeException("输入参数长度不能超过32");
        }
        char[] datas = data.toCharArray();
        byte[] asciis = new byte[datas.length];
        for (int i=0,len=datas.length; i<len; i++) {
            asciis[i] = String.valueOf(datas[i]).getBytes(StandardCharsets.US_ASCII)[0];
        }
        return Arrays.copyOf(asciis, 32);
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
     * 16进制字节数组中获取版本字节明文
     * ---------------------------------------------------------
     * 比如：0x00000101代表版本为1.0.1
     * ---------------------------------------------------------
     */
    public static String convertBytesForVersion(byte[] datas){
        int j = datas.length;
        if(datas[0] == 0x00){
            for (int i=0,len=datas.length; i<len; i++) {
                if(datas[i]==0x00 && datas[i+1]!=0x00){
                    j = i + 1;
                    break;
                }
            }
        }
        char[] versions = Hex.encodeHexString(Arrays.copyOfRange(datas, j, datas.length)).toCharArray();
        if(versions[0] == '0'){
            versions = Arrays.copyOfRange(versions, 1, versions.length);
        }
        StringBuilder sb = new StringBuilder();
        for (char version : versions) {
            sb.append(Integer.parseInt(String.valueOf(version), 16));
            sb.append(".");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }


    /**
     * 将十进制数值转为十六进制字节数组，并返回小端字节序
     * @param num 待转换的十进制数值
     * @param len 返回的字节数组大小（返回的长度不足时，尾部补0x00）
     */
    public static byte[] toHexBytesWithSmall(long num, int len){
        // 大端字节序
        byte[] bytes_big;
        if(num <= 0){
            bytes_big = new byte[]{0x00};
        }else{
            try {
                String hexStr = Long.toHexString(num);
                // 奇数长度时，头部补0
                if((hexStr.length() & 1) != 0){
                    hexStr = "0" + hexStr;
                }
                bytes_big = Hex.decodeHex(hexStr);
            } catch (DecoderException e) {
                throw new RuntimeException(e);
            }
        }
        // 转为小端字节序
        ArrayUtils.reverse(bytes_big);
        return Arrays.copyOf(bytes_big, len);
    }


    /**
     * 计算校验和
     * @param datas  待计算的16进制字节数组
     * @param length 校验和位数（返回的长度不足时，头部补0x00）
     * @return 校验和16进制字节数组
     */
    public static byte[] calcChecksum(byte[] datas, int length) {
        long checksum = 0;
        // 逐字节添加位数和
        for (byte data : datas) {
            checksum = checksum + (((long)data>=0) ? (long)data : ((long)data+256));
        }
        // 位数和转化为16进制字节数组（顺序的，即大端字节序）
        byte[] respDatas = new byte[length];
        for (int i=0; i<length; i++) {
            respDatas[length - i - 1] = (byte)(checksum >> (i * 8) & 0xff);
        }
        return respDatas;
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
}