/**
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

/**
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.filechooser.FileSystemView;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玄玉的开发工具类
 * @version v3.13
 * @history v3.13-->增加获取本周第一天、判断是否本周第一天、判断是否本月第一天的三个方法
 * @history v3.12-->增加获取应用运行进程的PID的方法getPID()
 * @history v3.11-->增加十六进制字符串转为byte[]的方法hexToBytes()
 * @history v3.10-->增加通过反射实现的JavaBean之间属性拷贝的方法beanCopyProperties()
 * @history v3.9-->修正打印入参为java.util.Map时可能引发的NullPointerException
 * @history v3.8-->修正部分细节并增加<code>getDetailDate(dateStr)</code>方法
 * @history v3.7-->add method of <code>escapeEmoji()</code> for escape Emoji to *
 * @history v3.6-->add method of <code>bytesToHex()</code> for convert byte to hex
 * @history v3.5-->增加requestToBean()用于将HttpServletRequest参数值转为JavaBean的方法
 * @history v3.4-->增加extractHttpServletRequestMessage用于提取HTTP请求完整报文的两个方法
 * @history v3.3-->增加isAjaxRequest()用于判断是否为Ajax请求的方法
 * @history v3.2-->增加getCurrentWeekStartDate()和getCurrentWeekEndDate()用于获取本周开始和结束的时间
 * @history v3.1-->修改<code>captureScreen()</code>方法增加是否自动打开生成的抓屏图片的功能
 * @history v3.0-->修改<code>htmlEscape()</code>方法名为<code>escapeHtml()</code>,并新增<code>escapeXml</code>方法
 * @history v2.9-->新增<code>getIncreaseDate()</code>用于计算指定日期相隔一定天数后的日期
 * @history v2.8-->移除加解密相关方法到CodecUtil类中,增加了模拟OracleSequence、抓屏、格式化XML字符串、统计代码行数等方法
 * @history v2.7-->新增<code>extractStackTrace()</code>用于提取堆栈信息的方法
 * @history v2.6-->重命名了若干方法,使之更形象,通俗易懂,并删除了getStringSimple()、getStringForInt()等方法
 * @history v2.5-->新增<code> getStringForInt()</code>用于将阿拉伯字节数组转为整型数值的方法
 * @history v2.4-->新增<code>genAESEncrypt()和genAESDecrypt()</code>用于AES加解密的方法
 * @history v2.3-->修改<code>rightPadForByte(),leftPadForByte()</code>方法左右填充的字符为0x00
 * @history v2.2-->新增<code>isNotEmpty()</code>用于判断输入的字符串或字节数组是否为非空的方法
 * @history v2.1-->新增<code>formatToHexStringWithASCII()</code>用于格式化字节数组为十六进制字符串的方法
 * @history v2.0-->局部的StringBuffer一律StringBuilder之(本思路提示自坦克<captmjc@gmail.com>)
 * @history v1.5-->新增<code>getStringSimple()</code>获取一个字符串的简明效果,返回的字符串格式类似于"abcd***hijk"
 * @history v1.4-->新增<code>getHexSign()</code>根据指定的签名密钥和算法签名Map<String,String>
 * @history v1.3-->修改<code>getSysJournalNo()</code>实现细节为<code>java.util.UUID.randomUUID()</code>
 * @history v1.2-->新增<code>getString()</code>字节数组转为字符串方法
 * @history v1.1-->新增<code>getHexSign()</code>通过指定算法签名字符串方法
 * Created by 玄玉<https://jadyer.github.io/> on 2012/12/22 19:00.
 */
public final class JadyerUtil {
	private static BigInteger sequenceNo = new BigInteger("0");
	private static BigInteger maxSequenceNo = new BigInteger("999999999");
	
	private JadyerUtil(){}
	
	/**
	 * 获取序列号
	 * @see 实现类似于OracleSequence的效果
	 */
	public synchronized static String buildSequenceNo(){
		if(sequenceNo.subtract(maxSequenceNo).signum() >= 0){
			sequenceNo = new BigInteger("0");
			return sequenceNo.toString();
		}
		sequenceNo = sequenceNo.add(BigInteger.ONE);
		return sequenceNo.toString();
	}


	/**
	 * 获取流水号
	 */
	public static String buildSerialNo(){
		//RandomStringUtils.randomAlphanumeric(20);
		return RandomStringUtils.randomNumeric(20);
	}


	/**
	 * 获取Map中的属性
	 * <p>
	 *     由于Map.toString()打印出来的参数值对，是横着一排的...参数多的时候，不便于查看各参数值
	 *     故此仿照commons-lang3.jar中的ReflectionToStringBuilder.toString()编写了本方法
	 * </p>
	 * @return String key11=value11 \n key22=value22 \n key33=value33 \n......
	 */
	public static String buildStringFromMap(Map<String, String> map){
		if(null==map || map.isEmpty()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(map.getClass().getName()).append("@").append(map.hashCode()).append("[");
		for(Map.Entry<String,String> entry : map.entrySet()){
			sb.append("\n").append(entry.getKey()).append("=").append(entry.getValue());
		}
		return sb.append("\n]").toString();
	}


	/**
	 * 获取Map中的属性
	 * <p>
	 *     该方法的参数适用于打印Map<String, String[]>的情况，用途描述见{@link #buildStringFromMap(Map)}
	 * </p>
	 * @return String key11=value11 \n key22=value22 \n key33=value33 \n......
	 */
	public static String buildStringFromMapWithStringArray(Map<String, String[]> map){
		if(null==map || map.isEmpty()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(map.getClass().getName()).append("@").append(map.hashCode()).append("[");
		for(Map.Entry<String,String[]> entry : map.entrySet()){
			sb.append("\n").append(entry.getKey()).append("=").append(Arrays.toString(entry.getValue()));
		}
		return sb.append("\n]").toString();
	}


	/**
	 * 获取Map中的属性
	 * <p>
	 *     该方法的参数适用于打印Map<String, byte[]>的情况，用途描述见{@link #buildStringFromMap(Map)}
	 * </p>
	 * @return String key11=value11 \n key22=value22 \n key33=value33 \n......
	 */
	public static String buildStringFromMapWithByteArray(Map<String, byte[]> map){
		if(null==map || map.isEmpty()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(map.getClass().getName()).append("@").append(map.hashCode()).append("[");
		for(Map.Entry<String,byte[]> entry : map.entrySet()){
			sb.append("\n").append(entry.getKey()).append("=").append(new String(entry.getValue()));
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
	public static String buildStringFromObjectWithByte(Object obj){
		if(null == obj){
			return "";
		}
		//局部的StringBuffer一律StringBuilder之
		StringBuilder sb = new StringBuilder();
		sb.append(obj.getClass().getName()).append("@").append(obj.hashCode()).append("[");
		for(Field field : obj.getClass().getDeclaredFields()){
			//构造getter方法
			String methodName = "get" + StringUtils.capitalize(field.getName());
			Object fieldValue;
			try{
				//执行getter方法,获取其返回值
				fieldValue = obj.getClass().getDeclaredMethod(methodName).invoke(obj);
			}catch(Exception e){
				//一旦发生异常,便将属性值置为UnKnown,故此处没必要一一捕获所有异常
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
	 * @see 该方法会将字节数组中的所有字节均格式化为字符串
	 * @see 使用说明详见<code>formatToHexStringWithASCII(byte[], int, int)</code>方法
	 */
	public static String buildHexStringWithASCII(byte[] data){
		return buildHexStringWithASCII(data, 0, data.length);
	}
	
	
	/**
	 * 通过ASCII码将十进制的字节数组格式化为十六进制字符串
	 * @see 该方法常用于字符串的十六进制打印,打印时左侧为十六进制数值,右侧为对应的字符串原文
	 * @see 在构造右侧的字符串原文时,该方法内部使用的是平台的默认字符集,来解码byte[]数组
	 * @see 该方法在将字节转为十六进制时,默认使用的是<code>java.util.Locale.getDefault()</code>
	 * @see 详见String.format(String, Object...)方法和new String(byte[], int, int)构造方法
	 * @param data   十进制的字节数组
	 * @param offset 数组下标,标记从数组的第几个字节开始格式化输出
	 * @param length 格式长度,其不得大于数组长度,否则抛出java.lang.ArrayIndexOutOfBoundsException
	 * @return 格式化后的十六进制字符串
	 */
	private static String buildHexStringWithASCII(byte[] data, int offset, int length){
		if(isEmpty(data)){
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
	 * @see 使用说明详见<code>formatToHexStringWithASCII(byte[], int, int)</code>方法
	 */
	public static String buildHexStringWithASCIIForHex(byte[] hexData, int offset, int length){
		if(isEmpty(hexData)){
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
	 * <p>等效于org.apache.commons.codec.binary.Hex.encodeHexString(in, true)</p>
	 */
	public static String bytesToHex(byte[] in, boolean toLowerCase){
		//String hex = new BigInteger(1, in).toString(16);
		//int paddingLength = (in.length * 2) - hex.length();
		//if(paddingLength > 0){
		//	return String.format("%0" + paddingLength + "d", 0) + hex;
		//}else{
		//	return hex;
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
	 * <p>等效于org.apache.commons.codec.binary.Hex.decodeHex(hex.toCharArray())</p>
	 */
	public static byte[] hexToBytes(String hex){
		byte[] binary = new byte[hex.length() / 2];
		for(int i=0; i<binary.length; i++){
			binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
		}
		return binary;
	}


	/**
	 * 判断一个整数是否为奇数
	 * @see 1.本方法中0/-0/-2/2都不是奇数,-1/1/-3/3都是奇数
	 * @see 2.算术运算和逻辑运行要比乘除运算更高效,计算的结果也会更快
	 * @see 3.如果使用num % 2 == 1作为判断条件,那么负奇数的话就不适用了
	 * @return true--是奇数,false--不是奇数
	 */
	public static boolean isOddNumber(int num){
		return (num & 1) != 0;
	}


	/**
	 * 判断输入的字符串参数是否为空
	 * @return boolean 空则返回true,非空则flase
	 */
	public static boolean isEmpty(String input) {
		return null==input || 0==input.length() || 0==input.replaceAll("\\s", "").length();
	}
	
	
	/**
	 * 判断输入的字节数组是否为空
	 * @return boolean 空则返回true,非空则flase
	 */
	public static boolean isEmpty(byte[] bytes){
		return null==bytes || 0==bytes.length;
	}
	
	
	/**
	 * 判断输入的字符串参数是否为非空
	 * @return boolean 非空则返回true,空则flase
	 */
	public static boolean isNotEmpty(String input){
		return !isEmpty(input);
	}
	
	
	/**
	 * 判断输入的字节数组是否为非空
	 * @return boolean 非空则返回true,空则flase
	 */
	public static boolean isNotEmpty(byte[] bytes){
		return !isEmpty(bytes);
	}


	/**
	 * 判断是否为Ajax请求
	 * @create Nov 1, 2015 1:30:55 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static boolean isAjaxRequest(HttpServletRequest request){
		String requestType = request.getHeader("X-Requested-With");
		if(null!=requestType && "XMLHttpRequest".equals(requestType)){
			return true;
		}
		requestType = request.getHeader("x-requested-with");
		return null!=requestType && "XMLHttpRequest".equals(requestType);
	}


	/**
	 * HttpServletRequest参数值转为JavaBean
	 * @see 该方法目前只能处理所有属性均为String的JavaBean
	 * @see 且只能处理当前类,暂不能处理父类和子类
	 * @see 且类属性只能是String
	 * @create Dec 17, 2015 4:44:47 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static <T> T requestToBean(HttpServletRequest request, Class<T> beanClass){
		try{
			T bean = beanClass.newInstance();
			//getFields()能获取到父类和子类中所有public的属性
			//getDeclaredFields()能获取到类的所有属性（不受访问权限控制，也不包括父类）
			for(Field field : beanClass.getDeclaredFields()){
				//构造setter方法
				String methodName = "set" + StringUtils.capitalize(field.getName());
				try{
					//执行setter方法
					beanClass.getMethod(methodName, String.class).invoke(bean, URLDecode(request.getParameter(field.getName())));
				}catch(Exception e){
					//ignore exception
				}
			}
			return bean;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}


	/**
	 * 采用反射实现的JavaBean属性拷贝
	 * <ul>
	 *     <li>实测拷贝效率由低到高依次为（最快的目前是cglib，也是推荐采用的）</li>
	 *     <li>net.sf.cglib.beans.BeanCopier.copy()</li>
	 *     <li>org.springframework.beans.BeanUtils.copyProperties()</li>
	 *     <li>com.jadyer.engine.common.util.JadyerUtil.beanCopyProperties()</li>
	 *     <li>org.apache.commons.beanutils.BeanUtils.copyProperties()</li>
	 * </ul>
	 * <p>
	 *     曾试过优化一下BeanCopier，把这个对象放到全局的ConcurrentHashMap<String, BeanCopier>里面<br>
	 *     放进去的beanCopier对象就是BeanCopier.create(source.getClass(), target.getClass(), false)<br>
	 *     意味着只要是从相同的source拷贝属性给target，就不用每次create()，而是直接从ConcurrentHashMap中取<br>
	 *     不过测试发现，放到ConcurrentHashMap之后的效率反倒不如每次都BeanCopier.create()，以后有时间再研究吧
	 * </p>
	 * <p>
	 *     另外，这里自己写的反射获取Setter有点复杂了好像，可以参考上面的requestToBean()方法<br>
	 *     以后真正用的时候再改，今天没心情......
	 * </p>
	 * @create 2016/6/16 16:35
     */
	public static <T, E> void beanCopyProperties(T source, E target){
		//采用Cglib实现
		//net.sf.cglib.beans.BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
		//beanCopier.copy(source, target, null);
		//采用Spring实现
		//org.springframework.beans.BeanUtils.copyProperties(source, target);
		//采用反射实现
		Method[] sourceMethods = source.getClass().getDeclaredMethods();
		Method[] targetMethods = target.getClass().getDeclaredMethods();
		for(Method sourceMethod : sourceMethods){
			if(sourceMethod.getName().startsWith("get") || sourceMethod.getName().startsWith("is")){
				//得到源对象的Setter
				String sourceFieldName;
				if(sourceMethod.getName().startsWith("get")){
					sourceFieldName = "set" + sourceMethod.getName().substring(3);
				}else{
					sourceFieldName = "set" + sourceMethod.getName().substring(2);
				}
				for(Method targetMethod : targetMethods){
					if(targetMethod.getName().equals(sourceFieldName)){
						//参数类型和返回类型判断
						if(sourceMethod.getReturnType().isAssignableFrom(targetMethod.getParameterTypes()[0])){
							try {
								targetMethod.invoke(target, sourceMethod.invoke(source));
								break;
							} catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException("属性拷贝失败", e);
							}
						}
					}
				}
			}
		}
	}


	/**
	 * 字符串转为字节数组
	 * @see 该方法默认以ISO-8859-1转码
	 * @see 若想自己指定字符集,可以使用<code>getBytes(String str, String charset)</code>方法
	 */
	public static byte[] getBytes(String data){
		return getBytes(data, "ISO-8859-1");
	}
	
	
	/**
	 * 字符串转为字节数组
	 * @see 如果系统不支持所传入的<code>charset</code>字符集,则按照系统默认字符集进行转换
	 */
	public static byte[] getBytes(String data, String charset){
		data = (data==null ? "" : data);
		if(isEmpty(charset)){
			return data.getBytes();
		}
		try {
			return data.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			LogUtil.getLogger().error("将字符串[" + data + "]转为byte[]时发生异常:系统不支持该字符集[" + charset + "]");
			return data.getBytes();
		}
	}
	

	/**
	 * 字节数组转为字符串
	 * @see 该方法默认以ISO-8859-1转码
	 * @see 若想自己指定字符集,可以使用<code>getString(byte[] data, String charset)</code>方法
	 */
	public static String getString(byte[] data){
		return getString(data, "ISO-8859-1");
	}
	
	
	/**
	 * 字节数组转为字符串
	 * @see 如果系统不支持所传入的<code>charset</code>字符集,则按照系统默认字符集进行转换
	 */
	public static String getString(byte[] data, String charset){
		if(isEmpty(data)){
			return "";
		}
		if(isEmpty(charset)){
			return new String(data);
		}
		try {
			return new String(data, charset);
		} catch (UnsupportedEncodingException e) {
			LogUtil.getLogger().error("将byte数组[" + Arrays.toString(data) + "]转为String时发生异常:系统不支持该字符集[" + charset + "]");
			return new String(data);
		}
	}
	
	
	/**
	 * 获取当前方法的名字
	 */
	public static String getCurrentMethodName(){
		return Thread.currentThread().getStackTrace()[1].getMethodName();
	}


	/**
	 * 判断是否本月第一天
	 */
	public static boolean isFirstDayOfMonth(){
		return "01".equals(DateFormatUtils.format(new Date(), "dd"));
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
	 * 获取指定日期相隔一定天数后的日期
	 * @see 该方法等效于org.apache.commons.lang3.time.DateUtils.addDays(startDate, days)
	 * @param startDate 参照日期
	 * @param days      相隔的天数,正数时则往后计算,负数则往前计算
	 */
	public static Date getIncreaseDate(Date startDate, int days){
		final Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
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
	 * 字符编码
	 * @see 该方法默认会以UTF-8编码字符串
	 * @see 若想自己指定字符集,可以使用<code>encode(String chinese, String charset)</code>方法
	 */
	public static String URLEncode(String chinese){
		return URLEncode(chinese, "UTF-8");
	}
	
	
	/**
	 * 字符编码
	 * @see 该方法通常用于对中文进行编码
	 * @see 若系统不支持指定的编码字符集,则直接将<code>chinese</code>原样返回
	 */
	public static String URLEncode(String chinese, String charset){
		chinese = (chinese==null ? "" : chinese);
		try {
			return URLEncoder.encode(chinese, charset);
		} catch (UnsupportedEncodingException e) {
			LogUtil.getLogger().error("编码字符串[" + chinese + "]时发生异常:系统不支持该字符集[" + charset + "]");
			return chinese;
		}
	}
	
	
	/**
	 * 字符解码
	 * @see 该方法默认会以UTF-8解码字符串
	 * @see 若想自己指定字符集,可以使用<code>decode(String chinese, String charset)</code>方法
	 */
	public static String URLDecode(String chinese){
		return URLDecode(chinese, "UTF-8");
	}
	
	
	/**
	 * 字符解码
	 * @see 该方法通常用于对中文进行解码
	 * @see 若系统不支持指定的解码字符集,则直接将<code>chinese</code>原样返回
	 */
	public static String URLDecode(String chinese, String charset){
		chinese = (chinese==null ? "" : chinese);
		try {
			return URLDecoder.decode(chinese, charset);
		} catch (UnsupportedEncodingException e) {
			LogUtil.getLogger().error("解码字符串[" + chinese + "]时发生异常:系统不支持该字符集[" + charset + "]");
			return chinese;
		}
	}
	
	
	/**
	 * 金额元转分
	 * @see 注意:该方法可处理贰仟万以内的金额,且若有小数位,则不限小数位的长度
	 * @see 注意:如果你的金额达到了贰仟万以上,则不推荐使用该方法,否则计算出来的结果会令人大吃一惊
	 * @param amount  金额的元进制字符串
	 * @return String 金额的分进制字符串
	 */
	public static String moneyYuanToFen(String amount){
		if(isEmpty(amount)){
			return amount;
		}
		//传入的金额字符串代表的是一个整数
		if(!amount.contains(".")){
			return Integer.parseInt(amount) * 100 + "";
		}
		//传入的金额字符串里面含小数点-->取小数点前面的字符串,并将之转换成单位为分的整数表示
		int money_fen = Integer.parseInt(amount.substring(0, amount.indexOf("."))) * 100;
		//取到小数点后面的字符串
		String pointBehind = (amount.substring(amount.indexOf(".") + 1));
		//amount=12.3
		if(pointBehind.length() == 1){
			return money_fen + Integer.parseInt(pointBehind)*10 + "";
		}
		//小数点后面的第一位字符串的整数表示
		int pointString_1 = Integer.parseInt(pointBehind.substring(0, 1));
		//小数点后面的第二位字符串的整数表示
		int pointString_2 = Integer.parseInt(pointBehind.substring(1, 2));
		//amount==12.03,amount=12.00,amount=12.30
		if(pointString_1 == 0){
			return money_fen + pointString_2 + "";
		}else{
			return money_fen + pointString_1*10 + pointString_2 + "";
		}
	}
	
	
	/**
	 * 金额元转分
	 * @see 该方法会将金额中小数点后面的数值,四舍五入后只保留两位....如12.345-->12.35
	 * @see 注意:该方法可处理贰仟万以内的金额
	 * @see 注意:如果你的金额达到了贰仟万以上,则非常不建议使用该方法,否则计算出来的结果会令人大吃一惊
	 * @param amount  金额的元进制字符串
	 * @return String 金额的分进制字符串
	 */
	public static String moneyYuanToFenByRound(String amount){
		if(isEmpty(amount)){
			return amount;
		}
		if(!amount.contains(".")){
			return Integer.parseInt(amount) * 100 + "";
		}
		int money_fen = Integer.parseInt(amount.substring(0, amount.indexOf("."))) * 100;
		String pointBehind = (amount.substring(amount.indexOf(".") + 1));
		if(pointBehind.length() == 1){
			return money_fen + Integer.parseInt(pointBehind)*10 + "";
		}
		int pointString_1 = Integer.parseInt(pointBehind.substring(0, 1));
		int pointString_2 = Integer.parseInt(pointBehind.substring(1, 2));
		//下面这种方式用于处理pointBehind=245,286,295,298,995,998等需要四舍五入的情况
		if(pointBehind.length() > 2){
			int pointString_3 = Integer.parseInt(pointBehind.substring(2, 3));
			if(pointString_3 >= 5){
				if(pointString_2 == 9){
					if(pointString_1 == 9){
						money_fen = money_fen + 100;
						pointString_1 = 0;
						pointString_2 = 0;
					}else{
						pointString_1 = pointString_1 + 1;
						pointString_2 = 0;
					}
				}else{
					pointString_2 = pointString_2 + 1;
				}
			}
		}
		if(pointString_1 == 0){
			return money_fen + pointString_2 + "";
		}else{
			return money_fen + pointString_1*10 + pointString_2 + "";
		}
	}
	
	
	/**
	 * 金额分转元
	 * @see 注意:如果传入的参数中含小数点,则直接原样返回
	 * @see 该方法返回的金额字符串格式为<code>00.00</code>,其整数位有且至少有一个,小数位有且长度固定为2
	 * @param amount  金额的分进制字符串
	 * @return String 金额的元进制字符串
	 */
	public static String moneyFenToYuan(String amount){
		if(isEmpty(amount)){
			return amount;
		}
		if(amount.contains(".")){
			return amount;
		}
		if(amount.length() == 1){
			return "0.0" + amount;
		}else if(amount.length() == 2){
			return "0." + amount;
		}else{
			return amount.substring(0, amount.length()-2) + "." + amount.substring(amount.length()-2);
		}
	}
	
	
	/**
	 * 字符串右补字节
	 * @see 鉴于该方法常用于构造响应给支付平台相关系统的响应报文头,故其默认采用0x00右补字节且总字节长度为100字节
	 * @see 若想自己指定所补字节,可以使用<code>rightPadForByte(String str, int size, int padStrByASCII)</code>方法
	 */
	public static String rightPadUseByte(String str){
		return rightPadUseByte(str, 100, 0, "UTF-8");
	}


	/**
	 * 字符串右补字节
	 * @see 若str对应的byte[]长度不小于size,则按照size截取str对应的byte[],而非原样返回str
	 * @see 所以size参数很关键..事实上之所以这么处理,是由于支付处理系统接口文档规定了字段的最大长度
	 * @see 若对普通字符串进行右补字符,建议org.apache.commons.lang.StringUtils.rightPad(...)
	 * @param size          该参数指的不是字符串长度,而是字符串所对应的byte[]长度
	 * @param padStrByASCII 该值为所补字节的ASCII码,如32表示空格,48表示0,64表示@等
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
		return getString(destByte, charset);
	}


	/**
	 * 字符串左补字节
	 * @see 该方法默认采用0x00左补字节
	 * @see 若想自己指定所补字节,可以使用<code>leftPadForByte(String str, int size, int padStrByASCII)</code>方法
	 */
	public static String leftPadUseByte(String str, int size, String charset){
		return leftPadUseByte(str, size, 0, "UTF-8");
	}


	/**
	 * 字符串左补字节
	 * @see 若str对应的byte[]长度不小于size,则按照size截取str对应的byte[],而非原样返回str
	 * @see 所以size参数很关键..事实上之所以这么处理,是由于支付处理系统接口文档规定了字段的最大长度
	 * @param padStrByASCII 该值为所补字节的ASCII码,如32表示空格,48表示0,64表示@等
	 * @param charset       由左补字节后的字节数组生成新字符串时所采用的字符集
	 */
	public static String leftPadUseByte(String str, int size, int padStrByASCII, String charset){
		byte[] srcByte = str.getBytes();
		byte[] destByte = new byte[size];
		Arrays.fill(destByte, (byte)padStrByASCII);
		if(srcByte.length >= size){
			System.arraycopy(srcByte, 0, destByte, 0, size);
		}else{
			System.arraycopy(srcByte, 0, destByte, size-srcByte.length, srcByte.length);
		}
		return getString(destByte, charset);
	}


	/**
	 * 转义HTML字符串
	 * @see 对输入参数中的敏感字符进行过滤替换,防止用户利用JavaScript等方式输入恶意代码
	 * @see String input = <img src='http://t1.baidu.com/it/fm=0&gp=0.jpg'/>
	 * @see HtmlUtils.htmlEscape(input);         //from spring.jar
	 * @see StringEscapeUtils.escapeHtml(input); //from commons-lang.jar
	 * @see 尽管Spring和Apache都提供了字符转义的方法,但Apache的StringEscapeUtils功能要更强大一些
	 * @see StringEscapeUtils提供了对HTML,Java,JavaScript,SQL,XML等字符的转义和反转义
	 * @see 但二者在转义HTML字符时,都不会对单引号和空格进行转义,而本方法则提供了对它们的转义
	 * @return String 过滤后的字符串
	 */
	public static String escapeHtml(String input) {
		if(isEmpty(input)){
			return input;
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
	 * 转义XML字符串
	 * @return String 过滤后的字符串
	 */
	public static String escapeXml(String input) {
		if(isEmpty(input)){
			return input;
		}
		input = input.replaceAll("&", "&amp;");
		input = input.replaceAll("<", "&lt;");
		input = input.replaceAll(">", "&gt;");
		input = input.replaceAll("\"", "&quot;");
		input = input.replaceAll("'", "&apos;");
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
	 * 格式化XML格式的字符串
	 * <p>
	 *     格式化失败时返回的Map中，isPrettySuccess=no，prettyResultStr=堆栈信息
	 *     格式化成功时返回的Map中，isPrettySuccess=yes，prettyResultStr=格式化后的字符串
	 * </p>
	 * @param xmlString 待格式化的XML字符串
	 * @return 返回的Map中有两个字符串的key-value，分别为isPrettySuccess和prettyResultStr
	 */
	public static Map<String, String> formatXMLString(String xmlString) {
		Map<String, String> resultMap = new HashMap<>();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", new Integer(2));
		StringWriter writer = new StringWriter();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new StreamSource(new StringReader(xmlString)), new StreamResult(writer));
		} catch (TransformerException e) {
			resultMap.put("isPrettySuccess", "no");
			resultMap.put("prettyResultStr", extractStackTrace(e));
			return resultMap;
		}
		resultMap.put("isPrettySuccess", "yes");
		resultMap.put("prettyResultStr", writer.toString());
		return resultMap;
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
		if(isEmpty(fileName)){
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
	 *     等价于org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(cause);
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
	 * 提取收到的HttpServletRequest请求报文头消息
	 * <p>
	 *     该方法默认使用了UTF-8解析请求消息
	 *     解析过程中发生异常时会抛出RuntimeException
	 * </p>
	 */
	public static String extractHttpServletRequestHeaderMessage(HttpServletRequest request){
		StringBuilder sb = new StringBuilder();
		sb.append(request.getMethod()).append(" ").append(request.getRequestURI()).append(null==request.getQueryString()?"":"?"+request.getQueryString()).append(" ").append(request.getProtocol()).append("\n");
		String headerName;
		for(Enumeration<String> obj = request.getHeaderNames(); obj.hasMoreElements();){
			headerName = obj.nextElement();
			sb.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
		}
		return sb.toString();
	}


	/**
	 * 提取收到的HttpServletRequest请求报文体消息
	 * <p>
	 *     该方法默认使用了UTF-8解析请求消息
	 *     解析过程中发生异常时会抛出RuntimeException
	 * </p>
	 */
	public static String extractHttpServletRequestBodyMessage(HttpServletRequest request){
		try{
			request.setCharacterEncoding("UTF-8");
		}catch(UnsupportedEncodingException e1){
			//ignore
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try{
			br = request.getReader();
			for(String line; (line=br.readLine())!=null;){
				sb.append(line).append("\n");
			}
		}catch(IOException e){
			throw new RuntimeException(e);
		}finally {
			if(null != br){
				IOUtils.closeQuietly(br);
			}
		}
		return sb.toString();
	}


	/**
	 * 统计代码行数
	 * -------------------------------------------------------------------------------------------------------------
	 * 1)目前仅支持*.java;*.xml;*.properties;*.jsp;*.htm;*.html六种文件格式
	 * 2)在统计jsp或htm或html文件时,也会将文件中的js或css标签里面的代码统计进去,即也会正确计算页面中js或css代码的注释或空行等
	 * 3)要注意两种特殊情况,对于这种特殊情况,本方法也进行了处理
	 *   <style type="text/css"><!--css code//--></style>
	 *   <script type="text/javascript"><!--js code//--></script>
	 * 4)本方法也支持统计*.js和*.css文件中的代码行数,只不过实际中js和css文件通常是现成的,只有很少的一部分才是程序员自己写的代码
	 *   而这一部分代码通常都写在jsp或html页面中,故本方法未统计*.js和*.css文件
	 *   如需统计,则只需初始化一下js和css的注释标记,并在允许的文件类型列表中将js和css添加进去即可
	 * 5)本方法会将统计结果放到参数resultMap中
	 * 6)不可在本方法中为resultMap的几个键的值设定初始值,因为本方法内部存在递归操作...但可以在调用方传入参数前初始化,如下所示
	 *   Map<String, Integer> resultMap = new HashMap<String, Integer>();
	 *   resultMap.put("total", 0);
	 *   resultMap.put("code", 0);
	 *   resultMap.put("comment", 0);
	 *   resultMap.put("blank", 0);
	 *   然后在调用本方法时传进来即可:JadyerUtil.getCodeLineCounts(file, resultMap);
	 *   待本方法执行完毕后,传进来的resultMap里面就有值了,调用方就可以获取到里面的值进行业务处理了
	 * -------------------------------------------------------------------------------------------------------------
	 * @param codeFile  待统计的File类,可以是具体的文件或目录
	 * @param resultMap 用于记录统计结果,键为total,code,comment,blank
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
		/**
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
				singleCommentPrefix = "http://blog.csdn.net/jadyer";
				break;
			case "properties":
				multiCommentPrefix[0] = "http://blog.csdn.net/jadyer";
				multiCommentSuffix[0] = "http://blog.csdn.net/jadyer";
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
				multiCommentPrefix[0] = "http://blog.csdn.net/jadyer";
				multiCommentSuffix[0] = "http://blog.csdn.net/jadyer";
				singleCommentPrefix = "http://blog.csdn.net/jadyer";
				break;
		}
		/**
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
				if(!isReadInComments && (codeFileSuffix.equals("jsp") || codeFileSuffix.equals("htm") || codeFileSuffix.equals("html"))){
					//css和js标记的起始标签分别有不止一种的写法,故startsWith
					if(content.startsWith("<style") || content.startsWith("<script")){
						//这里之所以取下标为0的元素,是因为上面在初始化多行的注释标记时,均将页面中的<!---->注释标记放置在第0个元素
						multiCommentPrefix[0] = "http://blog.csdn.net/jadyer";
						multiCommentSuffix[0] = "http://blog.csdn.net/jadyer";
					}
					if(content.equals("</style>") || content.equals("</script>")){
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


	/**
	 * 获取应用运行进程的PID
     */
	public static String getPID(){
		String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		return jvmName.split("@")[0];
	}
}