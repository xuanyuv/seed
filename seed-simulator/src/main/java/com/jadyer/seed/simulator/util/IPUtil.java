package com.jadyer.seed.simulator.util;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取IP的工具类
 * @version v1.1
 * @history v1.1-->增加获取服务端IP的方法
 * @history v1.0-->增加获取客户端IP的方法
 * @update Aug 27, 2015 4:35:46 PM
 * @create 2015年4月14日 下午8:55:09
 * @author 玄玉<https://jadyer.github.io/>
 */
public final class IPUtil {
	private IPUtil(){}
	private static final Map<String, String> subnetMaskMap = new HashMap<>();
	static{
		subnetMaskMap.put("8", "255.0.0.0");
		subnetMaskMap.put("16", "255.255.0.0");
		subnetMaskMap.put("24", "255.255.255.0");
		subnetMaskMap.put("128", "(::1/128");
		subnetMaskMap.put("10", "fe80::203:baff:fe27:1243/10");
	}

	/**
	 * 获取客户端IP
	 * @see --------------------------------------------------------------------------------------------------
	 * @see 1.JSP中获取客户端IP地址的方法是request.getRemoteAddr(),这种方法在大部分情况下都是有效的
	 * @see   但是在通过了Apache,Squid等反向代理软件后,获取到的就不是客户端的真实IP地址了
	 * @see 2.经过代理后,由于在客户端和服务端之前增加了中间层,因此服务器无法直接拿到客户端IP
	 * @see   但在转发请求的HTTP头中增加了X-FORWARDED-FOR,用以跟踪原有的客户端IP地址和原来客户端请求的服务器地址
	 * @see 3.经过多级反向代理后,X-FORWARDED-FOR的值就会不止一个,而是一串IP值,那么有效IP便是第一个非unknown的字符串
	 * @see 更多详细介绍见http://dpn525.iteye.com/blog/1132318
	 * @see --------------------------------------------------------------------------------------------------
	 */
	public static String getClientIP(HttpServletRequest request){
		String IP = request.getHeader("x-forwarded-for");
		if(null==IP || 0==IP.length() || "unknown".equalsIgnoreCase(IP)){
			IP = request.getHeader("Proxy-Client-IP");
		}
		if(null==IP || 0==IP.length() || "unknown".equalsIgnoreCase(IP)){
			IP = request.getHeader("WL-Proxy-Client-IP");
		}
		if(null==IP || 0==IP.length() || "unknown".equalsIgnoreCase(IP)){
			IP = request.getRemoteAddr();
		}
		//对于通过多个代理的情况,第一个IP为客户端真实IP
		//多个IP会按照','分割('***.***.***.***'.length()=15)
		if(null!=IP && IP.length()>15){
			if(IP.indexOf(",") > 0){
				IP = IP.substring(0, IP.indexOf(","));
			}
		}
		return IP;
	}


	/**
	 * 获取客户端IP的另一种写法
	 * @see 暂不推荐使用,因为已经有{@link IPUtil#getClientIP(HttpServletRequest)}方法了
	 */
	@Deprecated
	public static String getClientIP_(HttpServletRequest request){
		String IP = null;
		Enumeration<String> enu = request.getHeaderNames();
		while(enu.hasMoreElements()){
			String name = enu.nextElement();
			if(name.equalsIgnoreCase("X-Forwarded-For")){
				IP = request.getHeader(name);
			}else if(name.equalsIgnoreCase("Proxy-Client-IP")){
				IP = request.getHeader(name);
			}else if(name.equalsIgnoreCase("WL-Proxy-Client-IP")){
				IP = request.getHeader(name);
			}
			if(null!=IP && 0!=IP.length()){
				break;
			}
		}
		if(null==IP || 0==IP.length()){
			IP = request.getRemoteAddr();
		}
		return IP;
	}


	/**
	 * 获取服务器IP的另一种写法
	 * @see 暂不推荐使用,因为已经有{@link IPUtil#getServerIP()}方法了
	 * @see 另外,对于该方法,单网卡时返回"/192.168.2.173",多网卡时返回"/192.168.2.173/192.168.56.1"
	 */
	@Deprecated
	public static String getServerIP_(){
		String serverIP = "";
		try{
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			while(nets.hasMoreElements()){
				NetworkInterface net = nets.nextElement();
				Enumeration<InetAddress> addresses = net.getInetAddresses();
				while(addresses.hasMoreElements()){
					InetAddress IP = addresses.nextElement();
					if(null!=IP && IP instanceof Inet4Address && !IP.getHostAddress().equals("127.0.0.1")){
						serverIP += IP.toString();
					}
				}
			}
		}catch(SocketException e){
			LogUtil.getAppLogger().error("服务器IP地址获取失败,堆栈轨迹如下", e);
			serverIP = "服务器IP地址获取失败";
		}
		return serverIP;
	}


	/**
	 * 获取服务器IP
	 * @see 1.InetAddress.getLocalHost()在Windows下能获取到服务器IP,但Linux下获取到的就是127.0.0.1
	 * @see 2.java.net.InterfaceAddress是JDK6.0新增的
	 * @see 3.单网卡时会返回其IP,多网卡时会返回第一块网卡的IP
	 * @create Aug 28, 2015 5:37:24 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static String getServerIP(){
		String serverIP = "";
		try{
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			tag:while(nets.hasMoreElements()){
				NetworkInterface net = nets.nextElement();
				if(null != net.getHardwareAddress()){
					List<InterfaceAddress> addressList = net.getInterfaceAddresses();
					for(InterfaceAddress obj : addressList){
						InetAddress IP = obj.getAddress();
						if(null!=IP && IP instanceof Inet4Address && !IP.getHostAddress().equals("127.0.0.1")){
							System.out.println("IP=[" + IP.getHostAddress() +"]的子网掩码为=[" + subnetMaskMap.get(String.valueOf(obj.getNetworkPrefixLength())) + "]");
							serverIP = IP.getHostAddress();
							break tag;
						}
					}
				}
			}
		}catch(SocketException e){
			LogUtil.getAppLogger().error("服务器IP地址获取失败,堆栈轨迹如下", e);
			serverIP = "服务器IP地址获取失败";
		}
		return serverIP;
	}
}