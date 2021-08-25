package com.jadyer.seed.comm.util;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.whois.WhoisClient;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 网络请求工具类
 * ------------------------------------------------------------------------------------------------------
 * @version v3.1
 * @history v3.1-->增加writeToResponse()方法用于将内容输出到输出流
 * @history v3.0-->NetUtil升级为RequestUtil，并增加若干判断请求类型的方法
 * @history v2.0-->IPUtil升级为NetUtil，并增加whois()方法用于查询域名注册信息
 * @history v1.1-->增加获取服务端IP的方法
 * @history v1.0-->增加获取客户端IP的方法
 * ------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/21 16:34.
 */
public final class RequestUtil {
    private RequestUtil(){}

    /**
     * 获取客户端IP
     * --------------------------------------------------------------------------------------------------
     * 1.JSP中获取客户端IP地址的方法是request.getRemoteAddr()，这种方法在大部分情况下都是有效的
     *   但是在通过了Apache或Squid等反向代理软件后，获取到的就不是客户端的真实IP地址了
     * 2.经过代理后，由于在客户端和服务端之前增加了中间层，因此服务器无法直接拿到客户端IP
     *   但在转发请求的HTTP头中增加了X-FORWARDED-FOR，用以跟踪原有的客户端IP地址和原来客户端请求的服务器地址
     * 3.经过多级反向代理后，X-FORWARDED-FOR的值就会不止一个，而是一串IP值，那么有效IP便是第一个非unknown的字符串
     *   更多详细介绍见http://dpn525.iteye.com/blog/1132318
     * --------------------------------------------------------------------------------------------------
     */
    public static String getClientIP(HttpServletRequest request){
        String IP = request.getHeader("X-Requested-For");
        if(StringUtils.isBlank(IP) || "unknown".equalsIgnoreCase(IP)){
            IP = request.getHeader("X-Forwarded-For");
        }
        if(StringUtils.isBlank(IP) || "unknown".equalsIgnoreCase(IP)){
            IP = request.getHeader("Proxy-Client-IP");
        }
        if(StringUtils.isBlank(IP) || "unknown".equalsIgnoreCase(IP)){
            IP = request.getHeader("WL-Proxy-Client-IP");
        }
        if(StringUtils.isBlank(IP) || "unknown".equalsIgnoreCase(IP)){
            IP = request.getHeader("HTTP_CLIENT_IP");
        }
        if(StringUtils.isBlank(IP) || "unknown".equalsIgnoreCase(IP)){
            IP = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(StringUtils.isBlank(IP) || "unknown".equalsIgnoreCase(IP)){
            IP = request.getRemoteAddr();
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP（多IP时会按照','分隔）
        if(null!=IP && IP.contains(",")){
            for(String ips : IP.split(",")){
                if(!"unknown".equalsIgnoreCase(ips)){
                    IP = ips;
                    break;
                }
            }
        }
        return IP;
    }


    /**
     * 获取服务器IP
     * --------------------------------------------------------------------------------------------------
     * 1.InetAddress.getLocalHost()在Windows下能获取到服务器IP，但Linux下获取到的就是127.0.0.1
     * 2.java.net.InterfaceAddress是JDK6.0新增的
     * 3.单网卡时会返回其IP，多网卡时会返回第一块网卡的IP
     * --------------------------------------------------------------------------------------------------
     */
    public static String getServerIP(){
        String localAddress = "127.0.0.1";
        Map<String, String> SUBNET_MASK_MAP = new HashMap<String, String>(){
            private static final long serialVersionUID = 2303073623617570607L;
            {
                put("8", "255.0.0.0");
                put("16", "255.255.0.0");
                put("24", "255.255.255.0");
                put("128", "(::1/128");
                put("10", "fe80::203:baff:fe27:1243/10");
            }
        };
        String serverIP = "";
        try{
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            tag:while(nets.hasMoreElements()){
                NetworkInterface net = nets.nextElement();
                if(null != net.getHardwareAddress()){
                    List<InterfaceAddress> addressList = net.getInterfaceAddresses();
                    for(InterfaceAddress obj : addressList){
                        InetAddress IP = obj.getAddress();
                        if(IP instanceof Inet4Address && !localAddress.equals(IP.getHostAddress())){
                            serverIP = IP.getHostAddress();
                            LogUtil.getLogger().debug("IP=[{}]的子网掩码为=[{}]", serverIP, SUBNET_MASK_MAP.get(String.valueOf(obj.getNetworkPrefixLength())));
                            break tag;
                        }
                    }
                }
            }
        }catch(SocketException e){
            LogUtil.getLogger().error("服务器IP地址获取失败，堆栈轨迹如下", e);
            serverIP = "服务器IP地址获取失败";
        }
        return serverIP;
    }


    /**
     * 查询域名注册信息
     * @param domain 域名，可传入[oschina.net][www.oschina.net][http://www.oschina.net][https://www.oschina.net]
     */
    public static String whois(String domain){
        domain = domain.toLowerCase();
        domain = domain.replace("https://", "");
        domain = domain.replace("http://", "");
        if(StringUtils.startsWithIgnoreCase(domain, "www.")){
            domain = domain.substring(4);
        }
        WhoisClient whois = new WhoisClient();
        try{
            if(StringUtils.endsWithAny(domain, ".com", ".net", ".edu")){
                //连接whois查询服务器（默认whois.internic.net端口43）
                whois.connect(WhoisClient.DEFAULT_HOST);
            }else{
                //使用国家域名whois服务器
                whois.connect("whois.cnnic.cn");
            }
            return whois.query(domain);
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            try {
                //关闭连接
                whois.disconnect();
            } catch (IOException e) {
                LogUtil.getLogger().error("whois连接关闭时发生异常，堆栈轨迹如下", e);
            }
        }
    }


    /**
     * 获取应用运行进程的PID
     */
    public static String getPID(){
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return jvmName.split("@")[0];
    }


    /**
     * 获取当前方法的名字
     * 注意：有的场景下获取到的是代理的名字
     */
    public static String getCurrentMethodName(){
        return Thread.currentThread().getStackTrace()[1].getMethodName();
    }


    /**
     * 获取项目的本地路径
     * @return D:/Develop/Code/idea/seed（尾部不含斜线）
     */
    public static String getProjectPath(){
        return System.getProperty("user.dir").replace("\\", "/");
    }


    /**
     * 获取应用的完整根地址
     * @return https://jadyer.cn/mpp（尾部不含斜线）
     */
    public static String getFullContextPath(HttpServletRequest request){
        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme()).append("://").append(request.getServerName());
        if(80!=request.getServerPort() && 443!=request.getServerPort()){
            sb.append(":").append(request.getServerPort());
        }
        sb.append(request.getContextPath());
        return sb.toString();
    }


    /**
     * 将内容输出到输出流
     * @param data JSON字符串
     */
    public static void writeToResponse(String data, HttpServletResponse response) {
        byte[] datas = data.getBytes(StandardCharsets.UTF_8);
        // response.setHeader("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        // response.setHeader("Content-Length", datas.length+"");
        // 两种写法，二者等效
        response.setContentType("application/json; charset=" + StandardCharsets.UTF_8);
        response.setContentLength(datas.length);
        try(ServletOutputStream out = response.getOutputStream()){
            out.write(datas);
            out.flush();
        } catch (IOException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY, e);
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
        try(BufferedReader br = request.getReader()){
            for(String line; (line=br.readLine())!=null;){
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }


    /**
     * 判断是否为Ajax请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request){
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }


    /**
     * 判断是否为含附件的请求
     */
    public static boolean isMultipartRequest(HttpServletRequest request){
        return StringUtils.contains(request.getContentType(), "multipart");
    }


    /**
     * 判断是否为手机端QQ浏览器
     * -------------------------------------------------------------------------------------------------------
     * 1.IE-11.0.9600.17843
     *   User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko
     * 2.Chrome-46.0.2490.71 m (64-bit)
     *   User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36
     * 3.Windows-1.5.0.22（微信电脑版）
     *   User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 MicroMessenger/6.5.2.501 NetType/WIFI WindowsWechat
     * 4.IOS-QQ-6.1.0.496
     *   User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Mobile/11D201 QQ/6.1.0.496 Pixel/640 NetType/WIFI Mem/14
     * 5.IOS-WeChat-6.3.1
     *   User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Mobile/11D201 MicroMessenger/6.3.1 NetType/WIFI Language/en
     * 6.Android-WeChat-6.2.6
     *   User-Agent: Mozilla/5.0 (Linux; U; Android 4.4.2; zh-cn; H60-L01 Build/HDH60-L01) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.4 TBS/025469 Mobile Safari/533.1 MicroMessenger/6.2.5.54_re87237d.622 NetType/WIFI Language/zh_CN
     * -------------------------------------------------------------------------------------------------------
     */
    public static boolean isQQBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return null!=userAgent && userAgent.contains("QQ") && userAgent.contains("iPhone") && userAgent.contains("Android");
    }


    /**
     * 判断是否为手机端或PC端微信浏览器
     */
    public static boolean isWechatBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return null!=userAgent && userAgent.contains("MicroMessenger");
    }


    /**
     * 判断是否为PC端微信浏览器
     */
    public static boolean isWechatPCBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return null!=userAgent && userAgent.contains("WindowsWechat");
    }


    /**
     * 生成验证码图片，并输出给HttpServletResponse
     * ---------------------------------------------------------------------------
     * 生成图片的过程中，若发生异常则会抛出RuntimeException
     * ---------------------------------------------------------------------------
     * @param captchaType 验证码类型：1--纯数字，2--纯汉字
     * @return 返回生成的验证码字符串
     */
    public static String captcha(int captchaType, HttpServletResponse response){
        //设置不缓存
        response.setContentType("image/png");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, no-store");
        //response.setDateHeader("Expires", 0);
        long time = System.currentTimeMillis();
        response.setDateHeader("Last-Modified", time);
        response.setDateHeader("Date", time);
        response.setDateHeader("Expires", time);
        //创建随机类实例
        Random random = new Random();
        //定义图片尺寸
        //int width=60*captchaType, height=(captchaType==1)?20:30;
        int width=60*captchaType, height=20;
        //创建内存图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //获取图形上下文
        Graphics g = image.getGraphics();
        //设定背景色
        g.setColor(getRandColor(random, 200, 250));
        //设定图形的矩形坐标及尺寸
        g.fillRect(0, 0, width, height);
        StringBuilder sb = new StringBuilder();
        if(captchaType == 1){
            //图片背景随机产生50条干扰线作为噪点
            g.setColor(getRandColor(random, 160, 200));
            g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            for(int i=0; i<50; i++){
                int x11 = random.nextInt(width);
                int y11 = random.nextInt(height);
                int x22 = random.nextInt(width);
                int y22 = random.nextInt(height);
                g.drawLine(x11, y11, x11+x22, y11+y22);
            }
            //取随机产生的4个数字作为验证码
            //String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            //String str = "abcdefghkmnpqrstwxyABCDEFGHJKLMNPRSTWXYZ123456789";
            for(int i=0; i<4; i++){
                //String rand = String.valueOf(str.charAt(random.nextInt(62)));
                //String rand = String.valueOf(str.charAt(random.nextInt(49)));
                String rand = String.valueOf(random.nextInt(10));
                sb.append(rand);
                g.setColor(getRandColor(random, 10, 150));
                //将此数字画到图片上
                g.drawString(rand, 13*i+6, 16);
            }
        }else{
            //设定备选汉字
            String base = "\u7684\u4e00\u4e86\u662f\u6211\u4e0d\u5728\u4eba\u4eec\u6709\u6765\u4ed6\u8fd9\u4e0a\u7740" +
                    "\u4e2a\u5730\u5230\u5927\u91cc\u8bf4\u5c31\u53bb\u5b50\u5f97\u4e5f\u548c\u90a3\u8981\u4e0b" +
                    "\u770b\u5929\u65f6\u8fc7\u51fa\u5c0f\u4e48\u8d77\u4f60\u90fd\u628a\u597d\u8fd8\u591a\u6ca1" +
                    "\u4e3a\u53c8\u53ef\u5bb6\u5b66\u53ea\u4ee5\u4e3b\u4f1a\u6837\u5e74\u60f3\u751f\u540c\u8001" +
                    "\u4e2d\u5341\u4ece\u81ea\u9762\u524d\u5934\u9053\u5b83\u540e\u7136\u8d70\u5f88\u50cf\u89c1" +
                    "\u4e24\u7528\u5979\u56fd\u52a8\u8fdb\u6210\u56de\u4ec0\u8fb9\u4f5c\u5bf9\u5f00\u800c\u5df1" +
                    "\u4e9b\u73b0\u5c71\u6c11\u5019\u7ecf\u53d1\u5de5\u5411\u4e8b\u547d\u7ed9\u957f\u6c34\u51e0" +
                    "\u4e49\u4e09\u58f0\u4e8e\u9ad8\u624b\u77e5\u7406\u773c\u5fd7\u70b9\u5fc3\u6218\u4e8c\u95ee" +
                    "\u4f46\u8eab\u65b9\u5b9e\u5403\u505a\u53eb\u5f53\u4f4f\u542c\u9769\u6253\u5462\u771f\u5168" +
                    "\u624d\u56db\u5df2\u6240\u654c\u4e4b\u6700\u5149\u4ea7\u60c5\u8def\u5206\u603b\u6761\u767d" +
                    "\u8bdd\u4e1c\u5e2d\u6b21\u4eb2\u5982\u88ab\u82b1\u53e3\u653e\u513f\u5e38\u6c14\u4e94\u7b2c" +
                    "\u4f7f\u5199\u519b\u5427\u6587\u8fd0\u518d\u679c\u600e\u5b9a\u8bb8\u5feb\u660e\u884c\u56e0" +
                    "\u522b\u98de\u5916\u6811\u7269\u6d3b\u90e8\u95e8\u65e0\u5f80\u8239\u671b\u65b0\u5e26\u961f" +
                    "\u5148\u529b\u5b8c\u5374\u7ad9\u4ee3\u5458\u673a\u66f4\u4e5d\u60a8\u6bcf\u98ce\u7ea7\u8ddf" +
                    "\u7b11\u554a\u5b69\u4e07\u5c11\u76f4\u610f\u591c\u6bd4\u9636\u8fde\u8f66\u91cd\u4fbf\u6597" +
                    "\u9a6c\u54ea\u5316\u592a\u6307\u53d8\u793e\u4f3c\u58eb\u8005\u5e72\u77f3\u6ee1\u65e5\u51b3" +
                    "\u767e\u539f\u62ff\u7fa4\u7a76\u5404\u516d\u672c\u601d\u89e3\u7acb\u6cb3\u6751\u516b\u96be" +
                    "\u65e9\u8bba\u5417\u6839\u5171\u8ba9\u76f8\u7814\u4eca\u5176\u4e66\u5750\u63a5\u5e94\u5173" +
                    "\u4fe1\u89c9\u6b65\u53cd\u5904\u8bb0\u5c06\u5343\u627e\u4e89\u9886\u6216\u5e08\u7ed3\u5757" +
                    "\u8dd1\u8c01\u8349\u8d8a\u5b57\u52a0\u811a\u7d27\u7231\u7b49\u4e60\u9635\u6015\u6708\u9752" +
                    "\u534a\u706b\u6cd5\u9898\u5efa\u8d76\u4f4d\u5531\u6d77\u4e03\u5973\u4efb\u4ef6\u611f\u51c6" +
                    "\u5f20\u56e2\u5c4b\u79bb\u8272\u8138\u7247\u79d1\u5012\u775b\u5229\u4e16\u521a\u4e14\u7531" +
                    "\u9001\u5207\u661f\u5bfc\u665a\u8868\u591f\u6574\u8ba4\u54cd\u96ea\u6d41\u672a\u573a\u8be5" +
                    "\u5e76\u5e95\u6df1\u523b\u5e73\u4f1f\u5fd9\u63d0\u786e\u8fd1\u4eae\u8f7b\u8bb2\u519c\u53e4" +
                    "\u9ed1\u544a\u754c\u62c9\u540d\u5440\u571f\u6e05\u9633\u7167\u529e\u53f2\u6539\u5386\u8f6c" +
                    "\u753b\u9020\u5634\u6b64\u6cbb\u5317\u5fc5\u670d\u96e8\u7a7f\u5185\u8bc6\u9a8c\u4f20\u4e1a" +
                    "\u83dc\u722c\u7761\u5174\u5f62\u91cf\u54b1\u89c2\u82e6\u4f53\u4f17\u901a\u51b2\u5408\u7834" +
                    "\u53cb\u5ea6\u672f\u996d\u516c\u65c1\u623f\u6781\u5357\u67aa\u8bfb\u6c99\u5c81\u7ebf\u91ce" +
                    "\u575a\u7a7a\u6536\u7b97\u81f3\u653f\u57ce\u52b3\u843d\u94b1\u7279\u56f4\u5f1f\u80dc\u6559" +
                    "\u70ed\u5c55\u5305\u6b4c\u7c7b\u6e10\u5f3a\u6570\u4e61\u547c\u6027\u97f3\u7b54\u54e5\u9645" +
                    "\u65e7\u795e\u5ea7\u7ae0\u5e2e\u5566\u53d7\u7cfb\u4ee4\u8df3\u975e\u4f55\u725b\u53d6\u5165" +
                    "\u5cb8\u6562\u6389\u5ffd\u79cd\u88c5\u9876\u6025\u6797\u505c\u606f\u53e5\u533a\u8863\u822c" +
                    "\u62a5\u53f6\u538b\u6162\u53d4\u80cc\u7ec6";
            //图片背景增加噪点
            g.setColor(getRandColor(random, 160, 200));
            g.setFont(new Font("Times New Roman", Font.PLAIN, 14));
            for(int i=0; i<6; i++){
                g.drawString("*********************************************", 0, 5*(i+2));
            }
            //设定验证码汉字的备选字体{"宋体", "新宋体", "黑体", "楷体", "隶书"}
            String[] fontTypes = {"\u5b8b\u4f53", "\u65b0\u5b8b\u4f53", "\u9ed1\u4f53", "\u6977\u4f53", "\u96b6\u4e66"};
            //取随机产生的4个汉字作为验证码
            for(int i=0; i<4; i++){
                int start = random.nextInt(base.length());
                String rand = base.substring(start, start+1);
                sb.append(rand);
                g.setColor(getRandColor(random, 10, 150));
                g.setFont(new Font(fontTypes[random.nextInt(fontTypes.length)], Font.BOLD, 18+random.nextInt(4)));
                //将此汉字画到图片上
                g.drawString(rand, 24*i+10+random.nextInt(8), 24);
            }
        }
        //图像生效
        g.dispose();
        LogUtil.getLogger().debug("本次生成的验证码为：[{}]", sb.toString());
        /*
        //输出图像为Base64字符串：<img src="data:image/png;base64,xxxxxx">
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(image, "PNG", baos);
            return new HashMap<String, String>(){
                private static final long serialVersionUID = 6960237797379184414L;
                {
                    put("captcha", sb.toString());
                    put("imgSrcBase64", "data:image/png;base64," + Base64.encodeBase64String(baos.toByteArray()));
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
        //输出图像到页面：<img src="http://127.0.0.1/captcha">
        try(ServletOutputStream sos = response.getOutputStream()){
            ImageIO.write(image, "PNG", sos);
            sos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }


    /**
     * 生成给定范围内的随机颜色
     */
    private static Color getRandColor(Random random, int fc, int bc){
        if(fc > 255){
            fc = 255;
        }
        if(bc > 255){
            bc = 255;
        }
        int r = fc + random.nextInt(bc-fc);
        int g = fc + random.nextInt(bc-fc);
        int b = fc + random.nextInt(bc-fc);
        return new Color(r, g, b);
    }
}