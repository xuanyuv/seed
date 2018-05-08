package com.jadyer.seed.server.helper;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.server.model.NetBankResultNotify;
import com.jadyer.seed.server.model.OrderResultNotify;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 组装TCP和HTTP报文的工具类
 * Created by 玄玉<http://jadyer.cn/> on 2012/12/25 22:12.
 */
public final class MessageBuilder {
    private MessageBuilder(){}

    /**
     * 字符串转为字节数组
     */
    private static byte[] getBytes(String data, String charset){
        data = (null==data ? "" : data);
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding-->[" + charset + "]");
        }
    }


    /**
     * 构建响应给支付处理系统的响应报文头
     * @param respCode 应答码
     * @param respDesc 应答描述,注意该参数的字节长度要小于等于100
     * @return 响应报文头字符串,即响应报文头的最初明文
     */
    private static String buildResponseMessageHead(String respCode, String respDesc){
        StringBuilder sb = new StringBuilder("000000"); //msgLen--------固长6,报文长度
        sb.append(respCode)                             //respCode------固长8,应答码
          .append(JadyerUtil.rightPadUseByte(respDesc)) //respDesc------固长100,应答描述
          .append(JadyerUtil.randomNumeric(20))  //respSerialNo--固长20,应答流水号
          .append(DateUtil.getCurrentTime())            //respTime------固长14,应答日期时间yyyyMMddhhmmss
          .append(DateUtil.getCurrentDate());           //accountDate---固长8,账务日期时间yyyMMdd
        return sb.toString();
    }


    /**
     * 构建发往支付处理系统的请求报文头
     * @param busiCode 业务码
     * @return 请求报文头字符串,即请求报文头的最初明文
     */
    private static String buildRequestMessageHead(String busiCode){
        StringBuilder sb = new StringBuilder("000000"); //msgLen--------固长6,报文长度
        sb.append(busiCode)                             //busiCode------固长5,消息码
          .append("206")                                //reqSysType----固长3,请求系统类型
          .append("00001")                              //reqSysCode----固长5,请求系统编码
          .append(DateUtil.getCurrentTime())            //reqTime-------固长14,请求日期时间yyyyMMddhhmmss
          .append("06")                                 //tradeChannel--固长2,交易渠道
          .append(JadyerUtil.randomNumeric(20)); //reqSerialNo---固长20,请求流水
        return sb.toString();
    }


    /**
     * 构建发往沃前置系统的请求报文头
     * @param busiCode 请求交易码
     * @return 请求报文头字符串,即请求报文头的最初明文
     */
    private static String buildWoPortalRequestMessageHead(String busiCode){
        StringBuilder sb = new StringBuilder("000000"); //msgLen--------固长6,报文长度
        sb.append(busiCode)                                 //busiCode------固长5,请求交易编码
          .append("001")                                    //reqSysType----固长3,请求系统类型
          .append("00001")                                  //reqSysCode----固长5,请求系统编码
          .append(DateUtil.getCurrentTime());             //reqTime-------固长14,请求时间yyyyMMddhhmmss
        return sb.toString();
    }


    /**
     * 构建待发送或待响应的完整报文
     * @see 该方法主要用于计算报文真实长度，并使用真实长度替换掉报文中原有的长度标识
     * @see 报文长度不足6位时，自动用0x30左侧填充
     * @param message 包含了报文头和报文体的完整TCP报文
     * @return 计算并修正了真实长度后的可以发送出去的完整报文
     */
    private static String buildFullMessage(String message){
        StringBuilder sb = new StringBuilder(String.valueOf(getBytes(message, SeedConstants.DEFAULT_CHARSET).length));
        while(sb.length() < 6){
            sb.insert(0, "0");
        }
        sb.append(message.substring(6));
        return sb.toString();
    }


    /**
     * 构建发往支付处理系统的网银结果通知接口请求报文
     * @see 该方法会自动构建一个含报文头的接口报文
     */
    public static String buildNetBankResultNotifyMessage(NetBankResultNotify nbrn){
        String messageHead = buildRequestMessageHead("78001");
        StringBuilder messageBody = new StringBuilder();
        messageBody.append(nbrn.getOrderNo()).append("`")        //网银网关流水号
                    .append(nbrn.getTradeResult()).append("`")   //处理结果标志
                    .append(nbrn.getBankDate()).append("`")      //收单机构处理日期
                    .append(nbrn.getBankSerialNo()).append("`")  //收单机构流水
                    .append(nbrn.getBankRespCode()).append("`")  //收单机构错误代码
                    .append(nbrn.getBankRespDesc()).append("`")  //收单机构错误描述
                    .append(nbrn.getBankAccountNo()).append("`") //银行帐户帐号
                    .append(nbrn.getBankCertifi()).append("`")   //收单机构证书(HEX描述)
                    .append(nbrn.getBankSignData()).append("`")  //收单机构签名
                    .append(nbrn.getNotifyType()).append("`")    //通知类型,自动补单固定为5
                    .append(nbrn.getTradeAmount()).append("`");  //支付金额,单位:分
        return buildFullMessage(messageHead + messageBody);
    }


    /**
     * 构建发往沃前置系统的订单支付后台通知接口请求报文
     * @see 该方法会自动构建一个含报文头的接口报文
     */
    public static String buildOrderResultNotifyMessage(OrderResultNotify orn){
        String messageHead = buildWoPortalRequestMessageHead("10005");
        StringBuilder messageBody = new StringBuilder();
        messageBody.append(orn.getMerNo()).append("`")
                    .append(orn.getGoodsID()).append("`")
                    .append(orn.getGoodsName()).append("`")
                    .append(orn.getGoodsDesc()).append("`")
                    .append(orn.getMerOrderNo()).append("`")
                    .append(orn.getOrderAmount()).append("`")
                    .append(orn.getMerUserID()).append("`")
                    .append(orn.getPhoneNo()).append("`")
                    .append(orn.getOrderDate()).append("`")
                    .append(orn.getOrderExtend()).append("`")
                    .append(orn.getCharset()).append("`")
                    .append(orn.getMerDate()).append("`")
                    .append(orn.getSignType()).append("`")
                    .append(orn.getPayNo()).append("`")
                    .append(orn.getDetailCount()).append("`")
                    .append(orn.getPayDetail()).append("`")
                    .append(orn.getAcceptTime()).append("`")
                    .append(orn.getAcountDate()).append("`")
                    .append(orn.getPayBankCode()).append("`")
                    .append(orn.getBankAcountName()).append("`")
                    .append(orn.getBankAcountNo()).append("`");
        return buildFullMessage(messageHead + messageBody);
    }


    /**
     * 构建响应给支付处理系统商户订单状态主动通知接口的响应报文
     * @param respCode 应答码
     * @return 待响应报文
     */
    public static String buildOrderResultNotifyResponseMessage(String respCode){
        String respDesc = null;
        String messageBody = null;
        if("99999999".equals(respCode)){
            respDesc = "订单结果通知:商户系统已成功接收到通知,无需再次通知";
            messageBody = "0`";
        }else if("00000000".equals(respCode)){
            respDesc = "订单结果通知:商户系统未成功接收到通知,需要继续通知";
            messageBody = "1`";
        }
        String messageHead = buildResponseMessageHead(respCode, respDesc);
        return buildFullMessage(messageHead + messageBody);
    }


    /**
     * 构建HTTP响应报文
     * @see 该方法默认构建的是HTTP响应码为200的响应报文
     * @param httpResponseMessageBody HTTP响应报文体
     * @return 包含了HTTP响应报文头和报文体的完整报文
     */
    public static String buildHTTPResponseMessage(String httpResponseMessageBody){
        return buildHTTPResponseMessage(HttpURLConnection.HTTP_OK, httpResponseMessageBody);
    }


    /**
     * 构建HTTP响应报文
     * @see 200--请求已成功,请求所希望的响应头或数据体将随此响应返回..即服务器已成功处理了请求
     * @see 400--由于包含语法错误,当前请求无法被服务器理解..除非进行修改,否则客户端不应该重复提交这个请求..即错误请求
     * @see 500--服务器遇到了一个未曾预料的状况,导致其无法完成对请求的处理..一般来说,该问题都会在服务器的程序码出错时出现..即服务器内部错误
     * @see 501--服务器不支持当前请求所需要的某个功能..当服务器无法识别请求的方法,且无法支持其对任何资源的请求时,可能返回此代码..即尚未实施
     * @param httpResponseCode        HTTP响应码
     * @param httpResponseMessageBody HTTP响应报文体
     * @return 包含了HTTP响应报文头和报文体的完整报文
     */
    public static String buildHTTPResponseMessage(int httpResponseCode, String httpResponseMessageBody){
        if(httpResponseCode == HttpURLConnection.HTTP_OK){
            StringBuilder sb = new StringBuilder();
            sb.append("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=");
            sb.append(SeedConstants.DEFAULT_CHARSET);
            sb.append("\r\nContent-Length: ");
            sb.append(getBytes(httpResponseMessageBody, SeedConstants.DEFAULT_CHARSET).length);
            sb.append("\r\n\r\n");
            sb.append(httpResponseMessageBody);
            return sb.toString();
        }
        if(httpResponseCode == HttpURLConnection.HTTP_BAD_REQUEST){
            return "HTTP/1.1 400 Bad Request";
        }
        if(httpResponseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){
            return "HTTP/1.1 500 Internal Server Error";
        }
        return "HTTP/1.1 501 Not Implemented";
    }


    /**
     * 获取TCP报文体
     * @param respData          对方请求or响应的完整报文
     * @param messageHeahLength 报文头长度,如支付处理响应的报文头长度固定为156,沃前置则为128
     */
    public static String getTCPMessageBody(String respData, int messageHeahLength){
        //计算报文体长度（报文总长度 - 报文头长度）
        int messageBodyLength = Integer.parseInt(respData.substring(0,6)) - messageHeahLength;
        byte[] messageBodys = new byte[messageBodyLength];
        System.arraycopy(getBytes(respData, SeedConstants.DEFAULT_CHARSET), messageHeahLength, messageBodys, 0, messageBodyLength);
        return StringUtils.toEncodedString(messageBodys, Charset.forName(SeedConstants.DEFAULT_CHARSET));
    }


    /**
     * 获取TCP响应报文头中的应答描述字段
     * @see 该方法返回的字符串为自动剔除原应答描述字段中的0x00字节后的字符串
     * @see 沃前置系统的响应报文中,其约定的报文头格式为[6位长度码+8位应答码+14位应答日期+100位应答描述]
     * @see 支付处理系统的响应报文,其约定的报文头格式为[6位长度码+8位应答码+100位应答描述+42位其它]
     * @param respData 对方响应的完整报文
     * @param length   报文头中自起始截止到应答描述字段时的长度,如支付处理的为114,沃前置则为128
     */
    public static String getTCPMessageHeadRespDesc(String respData, int length){
        byte[] srcByte = getBytes(respData, SeedConstants.DEFAULT_CHARSET);
        int respDesclength = 0;     //有效的应答描述字节长度
        if(srcByte[length-1] != 0){ //应答描述的字节长度为固长100,不足时0x00右侧自动补齐
            respDesclength = 100;
        }else{
            for(int i=0; i<srcByte.length; i++){
                if(srcByte[i]==0){                   //取到应答描述字节中第一个为0x00的下标
                    respDesclength = i-(length-100); //计算有效的应答描述字节长度
                    break;
                }
            }
        }
        byte[] destByte = new byte[respDesclength];
        System.arraycopy(srcByte, length-100, destByte, 0, respDesclength); //从源数组的应答描述起始下标开始拷贝
        return StringUtils.toEncodedString(destByte, Charset.forName(SeedConstants.DEFAULT_CHARSET));
    }


    /**
     * 获取服务器状态的相关信息
     */
    public static String getServerStatus(){
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Seed Server Status</title>\n");
        sb.append("<link rel=\"icon\" href=\"https://raw.githubusercontent.com/jadyer/seed/master/seed-scs/src/main/webapp/favicon.ico\" type=\"image/x-icon\"/>\n");
        sb.append("<link rel=\"shortcut icon\" href=\"https://raw.githubusercontent.com/jadyer/seed/master/seed-scs/src/main/webapp/favicon.ico\" type=\"image/x-icon\"/>\n");
        sb.append("<style media=\"screen\">\n");
        sb.append("body {background-color:#93bde6;}\n");
        sb.append("div.version {font-weight:bold; font-size:100%; margin-bottom:8px;}\n");
        sb.append("h1 {font-weight:bold; font-size:100%}\n");
        sb.append("option {padding:2px 24px 2px 4px;}\n");
        sb.append("input {margin:0px 0px 4px 12px;}\n");
        sb.append("table.data {font-size:90%; border-collapse:collapse; border:1px solid black;}\n");
        sb.append("table.data th {background:#bddeff; vertical-align:top; width:16em; text-align:left; padding-right:8px; font-weight:normal; border:1px solid black;}\n");
        sb.append("table.data td {background:#ffffff; vertical-align:top; padding:0px 2px 0px 2px; border:1px solid black;}\n");
        sb.append("td.null {background:yellow;}\n");
        sb.append("td.available {color:black;}\n");
        sb.append("td.active {color:red;}\n");
        sb.append("td.offline {color:blue;}\n");
        sb.append("div.drill-down {}\n");
        sb.append("ul {list-style:none; padding:0px; margin:0px; position:relative; font-size:90%;}\n");
        sb.append("li {padding:0px; margin:0px 4px 0px 0px; display:inline; border:1px solid black; border-width:1px 1px 0px 1px;}\n");
        sb.append("li.active {background:#bddeff;}\n");
        sb.append("li.inactive {background:#eeeeee;}\n");
        sb.append("li.disabled {background:#dddddd; color:#999999; padding:0px 4px 0px 4px;}\n");
        sb.append("a.quiet {color:black; text-decoration:none; padding:0px 4px 0px 4px; }\n");
        sb.append("a.quiet:hover {background:white;}\n");
        sb.append("</style></head>\n");
        sb.append("<body>\n");
        sb.append("<div class=\"version\">Seed Server Status （").append(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")).append("）</div>\n");
        sb.append("<ul><li class=\"active\">Details</li></ul>\n");
        sb.append("<table cellpadding=\"2\" cellspacing=\"0\" border=\"1\" class=\"data\">\n");
        sb.append("<tbody>\n");
        sb.append("<tr><th>").append("SYSTEM.NAME").append(":</th><td>").append("采用Mina编写的服务器（Seed Server）").append("</td></tr>\n");
        Properties properties = System.getProperties();
        Enumeration<Object> enums = properties.keys();
        while(enums.hasMoreElements()){
            Object key = enums.nextElement();
            if(properties.get(key).toString().length() > 128){
                sb.append("<tr><th>").append(key).append(":</th><td>").append(properties.get(key).toString().substring(0,128)).append(" ...").append("</td></tr>\n");
            }else{
                sb.append("<tr><th>").append(key).append(":</th><td>").append(properties.get(key)).append("</td></tr>\n");
            }
        }
        sb.append("</tbody></table></body></html>");
        return sb.toString();
    }
}