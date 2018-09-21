package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.MinaUtil;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * Server端单元测试类
 * Created by 玄玉<https://jadyer.cn/> on 2012/11/21 16:45.
 */
public class ServerTest {
    /**
     * 模拟Client发起TCP请求本系统
     */
    @Test
    public void TCPRequestTest(){
        String message = "0003961000510110199201209222240000020120922000069347814303000700000813``中国联通交费充值`为号码18655228826交费充值100.00元`UDP1209222238312219411`10000```20120922`chinaunicom-payFeeOnline`UTF-8`20120922223831`MD5`20120922020103806276`1`02`10000`20120922223954`20120922`BOCO_B2C```http://192.168.20.2:5545/ecpay/pay/elecChnlFrontPayRspBackAction.action`1`立即支付,交易成功`";
        System.out.println("发送的报文长度[" + message.getBytes(StandardCharsets.UTF_8).length + "]");
        String respData = MinaUtil.sendTCPMessage(message, "127.0.0.1", 9901, "UTF-8");
        System.out.println("收到服务端反馈[" + respData + "]");
    }


    /**
     * 模拟Client发起HTTP_GET请求本系统
     */
    @Test
    public void HTTPGetRequestTest(){
        String reqURL = "http://127.0.0.1:8000/notify_boc?username=admin&password=123456";
        String respData = HTTPUtil.get(reqURL);
        System.out.println("收到服务端反馈[" + respData + "]");
    }


    /**
     * 模拟Client发起HTTP_POST请求本系统
     */
    @Test
    public void HTTPPostRequestTest(){
        String reqURL = "http://127.0.0.1:8000/notify_boc";
        String sendData = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String respData = HTTPUtil.post(reqURL, sendData, null);
        System.out.println("收到服务端反馈[" + respData + "]");
    }
}