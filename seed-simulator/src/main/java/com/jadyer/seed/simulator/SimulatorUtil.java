package com.jadyer.seed.simulator;

import com.jadyer.seed.comm.util.JadyerUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimulatorUtil {
	private static final int TCP_SO_TIMEOUT       = 90000;
	private static final int TCP_CONNECT_TIMEOUT  = 90000;
	private static final int HTTP_SO_TIMEOUT      = 90000;
	private static final int HTTP_CONNECT_TIMEOUT = 90000;

	private SimulatorUtil(){}
	
	/**
	 * 发送HTTP_POST请求
	 * @see you can see {@link SimulatorUtil#sendPostRequestBySocket(String, String, String)}
	 * @param reqURL     请求地址
	 * @param reqParams  请求报文
	 * @param reqCharset 请求报文的编码字符集,注意该参数不可传入""或null
	 * @return 应答Map有三个key,reqFullData--HTTP请求完整报文,respFullData--HTTP响应完整报文,respMsgHex-->HTTP响应的原始字节的十六进制表示
	 */
	public static Map<String, String> sendPostRequestBySocket(String reqURL, Map<String, String> reqParams, String reqCharset) throws IOException{
		StringBuilder reqData = new StringBuilder();
		for (Map.Entry<String, String> entry : reqParams.entrySet()) {
			reqData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		if (reqData.length() > 0) {
			reqData.setLength(reqData.length() - 1); //删除最后一个&符号
		}
		return sendPostRequestBySocket(reqURL, reqData.toString(), reqCharset);
	}
	

	/**
	 * 发送HTTP_POST请求
	 * @see 1)本方法是通过<code>java.net.Socket.Socket</code>实现HTTP_POST请求的发送的
	 * @see 2)本方法默认的连接超时和读取超时均为90秒
	 * @see 3)请求参数含中文时,无需<code>URLEncoder.encode(value, reqCharset)</code>可直接传入,该方法内部会自动encode
	 * @see 4)解码响应正文时,默认取响应头[Content-Type=text/html; charset=GBK]字符集,若无Content-Type,则使用UTF-8解码
	 * @see 5)该方法的请求和应答报文略
	 * @param reqURL     请求地址
	 * @param reqData    请求报文,多个参数则应拼接为param11=value11&22=value22&33=value33的形式
	 * @param reqCharset 请求报文的编码字符集,注意该参数不可传入""或null
	 * @return 应答Map有三个key,reqFullData--HTTP请求完整报文,respFullData--HTTP响应完整报文,respMsgHex-->HTTP响应的原始字节的十六进制表示
	 */
	public static Map<String, String> sendPostRequestBySocket(String reqURL, String reqData, String reqCharset) throws IOException{
		Map<String, String> respMap = new HashMap<String, String>();
		OutputStream out = null; //写
		InputStream in = null;   //读
		Socket socket = null;    //客户机
		String respCharset = "UTF-8";
		String respMsgHex = "";
		String respFullData = "";
		StringBuilder reqFullData = new StringBuilder();
		try {
			URL sendURL = new URL(reqURL);
			String host = sendURL.getHost();
			int port = sendURL.getPort()==-1 ? 80 : sendURL.getPort();
			/**
			 * 创建Socket
			 * @see ---------------------------------------------------------------------------------------------------
			 * @see 通过有参构造方法创建Socket对象时,客户机就已经发出了网络连接请求,连接成功则返回Socket对象,反之抛IOException
			 * @see 客户端在连接服务器时,也要进行通讯,客户端也需要分配一个端口,这个端口在客户端程序中不曾指定
			 * @see 这时就由客户端操作系统自动分配一个空闲的端口,默认的是自动的连续分配
			 * @see 如服务器端一直运行着,而客户端不停的重复运行,就会发现默认分配的端口是连续分配的
			 * @see 即使客户端程序已经退出了,系统也没有立即重复使用先前的端口
			 * @see socket = new Socket(host, port);
			 * @see ---------------------------------------------------------------------------------------------------
			 * @see 不过,可以通过下面的方式显式的设定客户端的IP和Port
			 * @see socket = new Socket(host, port, InetAddress.getByName("127.0.0.1"), 8765);
			 * @see ---------------------------------------------------------------------------------------------------
			 */
			socket = new Socket();
			/**
			 * 设置Socket属性
			 */
			//true表示关闭Socket的缓冲,立即发送数据..其默认值为false
			//若Socket的底层实现不支持TCP_NODELAY选项,则会抛出SocketException
			socket.setTcpNoDelay(true);
			//表示是否允许重用Socket所绑定的本地地址
			socket.setReuseAddress(true);
			//表示接收数据时的等待超时时间,单位毫秒..其默认值为0,表示会无限等待,永远不会超时
			//当通过Socket的输入流读数据时,如果还没有数据,就会等待
			//超时后会抛出SocketTimeoutException,且抛出该异常后Socket仍然是连接的,可以尝试再次读数据
			socket.setSoTimeout(HTTP_SO_TIMEOUT);
			//表示当执行Socket.close()时,是否立即关闭底层的Socket
			//这里设置为当Socket关闭后,底层Socket延迟5秒后再关闭,而5秒后所有未发送完的剩余数据也会被丢弃
			//默认情况下,执行Socket.close()方法,该方法会立即返回,但底层的Socket实际上并不立即关闭
			//它会延迟一段时间,直到发送完所有剩余的数据,才会真正关闭Socket,断开连接
			//Tips:当程序通过输出流写数据时,仅仅表示程序向网络提交了一批数据,由网络负责输送到接收方
			//Tips:当程序关闭Socket,有可能这批数据还在网络上传输,还未到达接收方
			//Tips:这里所说的"未发送完的剩余数据"就是指这种还在网络上传输,未被接收方接收的数据
			socket.setSoLinger(true, 5);
			//表示发送数据的缓冲区的大小
			socket.setSendBufferSize(1024);
			//表示接收数据的缓冲区的大小
			socket.setReceiveBufferSize(1024);
			//表示对于长时间处于空闲状态(连接的两端没有互相传送数据)的Socket,是否要自动把它关闭,true为是
			//其默认值为false,表示TCP不会监视连接是否有效,不活动的客户端可能会永久存在下去,而不会注意到服务器已经崩溃
			socket.setKeepAlive(true);
			//表示是否支持发送一个字节的TCP紧急数据,socket.sendUrgentData(data)用于发送一个字节的TCP紧急数据
			//其默认为false,即接收方收到紧急数据时不作任何处理,直接将其丢弃..若用户希望发送紧急数据,则应设其为true
			//设为true后,接收方会把收到的紧急数据与普通数据放在同样的队列中
			socket.setOOBInline(true);
			//该方法用于设置服务类型,以下代码请求高可靠性和最小延迟传输服务(把0x04与0x10进行位或运算)
			//Socket类用4个整数表示服务类型
			//0x02:低成本(二进制的倒数第二位为1)
			//0x04:高可靠性(二进制的倒数第三位为1)
			//0x08:最高吞吐量(二进制的倒数第四位为1)
			//0x10:最小延迟(二进制的倒数第五位为1)
			socket.setTrafficClass(0x04 | 0x10);
			//该方法用于设定连接时间,延迟,带宽的相对重要性(该方法的三个参数表示网络传输数据的3项指标)
			//connectionTime--该参数表示用最少时间建立连接
			//latency---------该参数表示最小延迟
			//bandwidth-------该参数表示最高带宽
			//可以为这些参数赋予任意整数值,这些整数之间的相对大小就决定了相应参数的相对重要性
			//如这里设置的就是---最高带宽最重要,其次是最小连接时间,最后是最小延迟
			socket.setPerformancePreferences(2, 1, 3);
			/**
			 * 连接服务端
			 */
			//客户端的Socket构造方法请求与服务器连接时,可能要等待一段时间
			//默认的Socket构造方法会一直等待下去,直到连接成功,或者出现异常
			//若欲设定这个等待时间,就要像下面这样使用不带参数的Socket构造方法,单位是毫秒
			//若超过下面设置的30秒等待建立连接的超时时间,则会抛出SocketTimeoutException
			//注意:如果超时时间设为0,则表示永远不会超时
			socket.connect(new InetSocketAddress(host, port), HTTP_CONNECT_TIMEOUT);
			//获取本地绑定的端口(每一个请求都会在本地绑定一个端口,再通过该端口发出去,即/127.0.0.1:50804 => /127.0.0.1:9901)
			//int localBindPort = socket.getLocalPort();
			/**
			 * 构造HTTP请求报文
			 */
			reqData = URLEncoder.encode(reqData, reqCharset);
			reqFullData.append("POST ").append(sendURL.getPath()).append(" HTTP/1.1\r\n");
			reqFullData.append("Cache-Control: no-cache\r\n");
			reqFullData.append("Pragma: no-cache\r\n");
			reqFullData.append("User-Agent: JavaSocket/").append(System.getProperty("java.version")).append("\r\n");
			reqFullData.append("Host: ").append(sendURL.getHost()).append("\r\n");
			reqFullData.append("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\r\n");
			reqFullData.append("Connection: keep-alive\r\n");
			reqFullData.append("Content-Type: application/x-www-form-urlencoded; charset=").append(reqCharset).append("\r\n");
			reqFullData.append("Content-Length: ").append(reqData.getBytes().length).append("\r\n");
			reqFullData.append("\r\n");
			reqFullData.append(reqData);
			/**
			 * 发送HTTP请求
			 */
			out = socket.getOutputStream();
			//这里针对getBytes()补充一下
			//之所以没有在该方法中指明字符集(包括上面头信息组装Content-Length的时候)
			//是因为在组装请求报文时,已URLEncoder.encode(),得到的都是非中文的英文字母符号等等
			//此时再getBytes()无论是否指明字符集,得到的都是内容一样的字节数组
			out.write(reqFullData.toString().getBytes());
			/**
			 * 接收HTTP响应
			 */
			in = socket.getInputStream();
			//事实上就像JDK的API所述:Closing a ByteArrayOutputStream has no effect
			//查询ByteArrayOutputStream.close()的源码会发现,它没有做任何事情,所以其close()与否是无所谓的
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int len = -1;
			while((len=in.read(buffer)) != -1){
				//将读取到的字节写到ByteArrayOutputStream中
				//所以最终ByteArrayOutputStream的字节数应该等于HTTP响应报文的整体长度,而大于HTTP响应正文的长度
				bytesOut.write(buffer, 0, len);
			}
			//响应的原始字节数组
			byte[] respBuffer = bytesOut.toByteArray();
			respMsgHex = JadyerUtil.buildHexStringWithASCII(respBuffer);
			/**
			 * 获取Content-Type中的charset值(Content-Type: text/html; charset=GBK)
			 */
			int from = 0;
			int to = 0;
			for(int i=0; i<respBuffer.length; i++){
				if((respBuffer[i]==99||respBuffer[i]==67) && (respBuffer[i+1]==111||respBuffer[i+1]==79) && (respBuffer[i+2]==110||respBuffer[i+2]==78) && (respBuffer[i+3]==116||respBuffer[i+3]==84) && (respBuffer[i+4]==101||respBuffer[i+4]==69) && (respBuffer[i+5]==110||respBuffer[i+5]==78) && (respBuffer[i+6]==116||respBuffer[i+6]==84) && respBuffer[i+7]==45 && (respBuffer[i+8]==84||respBuffer[i+8]==116) && (respBuffer[i+9]==121||respBuffer[i+9]==89) && (respBuffer[i+10]==112||respBuffer[i+10]==80) && (respBuffer[i+11]==101||respBuffer[i+11]==69)){
					from = i;
					//既然匹配到了Content-Type,那就一定不会匹配到我们想到的\r\n,所以就直接跳到下一次循环中喽..
					continue;
				}
				if(from>0 && to==0 && respBuffer[i]==13 && respBuffer[i+1]==10){
					//一定要加to==0限制,因为可能存在Content-Type后面还有其它的头信息
					to = i;
					//既然得到了你想得到的,那就不要再循环啦,徒做无用功而已
					break;
				}
			}
			//解码HTTP响应头中的Content-Type
			byte[] headerByte = Arrays.copyOfRange(respBuffer, from, to);
			//HTTP响应头信息无中文,用啥解码都可以
			String contentType = new String(headerByte);
			//提取charset值
			if(contentType.toLowerCase().contains("charset")){
				respCharset = contentType.substring(contentType.lastIndexOf("=") + 1).trim();
			}
			/**
			 * 解码HTTP响应的完整报文
			 */
			respFullData = bytesOut.toString(respCharset);
		} catch (Exception e) {
			respFullData = JadyerUtil.extractStackTrace(e);
		} finally {
			if (null!=socket && socket.isConnected() && !socket.isClosed()) {
				try {
					//此时socket的输出流和输入流也都会被关闭
					//值得注意的是:先后调用Socket的shutdownInput()和shutdownOutput()方法
					//值得注意的是:仅仅关闭了输入流和输出流,并不等价于调用Socket.close()方法
					//通信结束后,仍然要调用Socket.close()方法,因为只有该方法才会释放Socket占用的资源,如占用的本地端口等
					socket.close();
				} catch (IOException e) {
					//System.err.println("关闭客户机Socket时发生异常,堆栈信息如下");
					//e.printStackTrace();
					throw new IOException(e);
				}
			}
		}
		respMap.put("reqFullData", reqFullData.toString());
		respMap.put("respFullData", respFullData);
		respMap.put("respMsgHex", respMsgHex);
		return respMap;
	}
}