package com.jadyer.seed.comm.tmp;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpAddressUtils {
    private final static Logger log = LoggerFactory.getLogger(IpAddressUtils.class);

    private static final Logger logger = LoggerFactory.getLogger(IpAddressUtils.class);

    private static final Pattern IP_PATTERN = Pattern.compile("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$");

    private static final Pattern PRIVATE_IP_PATTERN = Pattern.compile("127\\.0\\.0\\.1");

    /**
     * 获取正确的客户端ip地址
     * @param request - 客户端请求
     * @return ip地址，可能返回null
     */
    public static String getIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                // 本机配置的IP
                ip = doGetLocalIp();
            }
        }
        log.info("客户端:"+ip);
        if (ip != null && ip.trim().length() > 0) {
            int index = ip.indexOf(',');
            ip = (index != -1) ? ip.substring(0, index) : ip;
        }

        return ip;
    }

    /**
     * 判断是否是一个IP地址格式
     */
    public static boolean isIP(String ip) {
        if (StringUtils.isBlank(ip))
            return false;
        Matcher matcher = IP_PATTERN.matcher(ip);
        return matcher.matches();
    }

    /**
     * 判断是否是一个回环IP
     */
    public static boolean isLoopbackIP(String ip) {
        if (StringUtils.isBlank(ip))
            return false;
        Matcher matcher = PRIVATE_IP_PATTERN.matcher(ip);
        return matcher.matches();
    }


    protected static String doGetLocalIp() {
        String ip = null;
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows")) {
                InetAddress localHost = InetAddress.getLocalHost();
                ip = localHost.getHostAddress();
            }
            // Linux
            else {
                ip = getLinuxIpAddress();
            }
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("The LocalIpAddress (1) Is {}", ip);
        return ip;
    }

    private static String getLinuxIpAddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> interfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
//				if (ni.getName().equals("eth0")) {
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address instanceof Inet6Address)
                        continue;
                    ip = address.getHostAddress();
                    if (null == ip || !isIP(ip) || !isLoopbackIP(ip))
                        continue;
                    return ip;
                }
//					break;
//				}
            }
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
        }
        return ip;
    }
}
