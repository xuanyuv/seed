package com.jadyer.seed.server.core;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Server端HTTP协议解码器
 * <p>
 *     用于解码接收到的HTTP请求报文，目前只支持HTTP/1.0和HTTP/1.1
 * </p>
 * Created by 玄玉<http://jadyer.cn/> on 2012/09/03 22:27.
 */
public class ServerProtocolHTTPDecoder implements MessageDecoder {
    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if(in.remaining() < 5){
            return MessageDecoderResult.NEED_DATA;
        }
        if(session.getLocalAddress().toString().contains(":" + ConfigUtil.INSTANCE.getProperty("server.port.http"))){
            return this.isComplete(in) ? MessageDecoderResult.OK : MessageDecoderResult.NEED_DATA;
        }else{
            return MessageDecoderResult.NOT_OK;
        }
    }

    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        byte[] message = new byte[in.limit()];
        in.get(message);
        String fullMessage = StringUtils.toEncodedString(message, Charset.forName(SeedConstants.DEFAULT_CHARSET));
        Token token = new Token();
        token.setBusiCharset(SeedConstants.DEFAULT_CHARSET);
        token.setBusiType(Token.BUSI_TYPE_HTTP);
        token.setFullMessage(fullMessage);
        if(fullMessage.startsWith("GET")){
            if(fullMessage.startsWith("GET / HTTP/1.1\r\n") || fullMessage.startsWith("GET / HTTP/1.0\r\n")){
                token.setBusiCode("/");
            }else if(fullMessage.startsWith("GET /favicon.ico HTTP/1.1\r\n") || fullMessage.startsWith("GET /favicon.ico HTTP/1.0\r\n")){
                token.setBusiCode("/favicon.ico");
            }else{
                //GET /login?aa=bb&cc=dd&ee=ff HTTP/1.1
                if(fullMessage.substring(4, fullMessage.indexOf("\r\n")).contains("?")){
                    token.setBusiCode(fullMessage.substring(4, fullMessage.indexOf("?")));
                    token.setBusiMessage(fullMessage.substring(fullMessage.indexOf("?")+1, this.getHTTPVersionPosition(fullMessage)-1));
                //GET /login HTTP/1.1
                }else{
                    token.setBusiCode(fullMessage.substring(4, this.getHTTPVersionPosition(fullMessage)-1));
                }
            }
        }else if(fullMessage.startsWith("POST")){
            //先获取到请求报文头中的Content-Length
            int contentLength = 0;
            if(fullMessage.contains("Content-Length:")){
                String msgLenFlag = fullMessage.substring(fullMessage.indexOf("Content-Length:") + 15);
                if(msgLenFlag.contains("\r\n")){
                    contentLength = Integer.parseInt(msgLenFlag.substring(0, msgLenFlag.indexOf("\r\n")).trim());
                    if(contentLength > 0){
                        token.setBusiMessage(fullMessage.split("\r\n\r\n")[1]);
                    }
                }
            }
            //POST /login?aa=bb&cc=dd&ee=ff HTTP/1.1
            //特别说明一下:此时报文体本应该是空的,即Content-Length=0,但不能排除对方偏偏在报文体中也传了参数
            //特别说明一下:所以这里的处理手段是busiMessage=请求URL中的参数串 + "`" + 报文体中的参数串(如果存在报文体的话)
            if(fullMessage.substring(5, fullMessage.indexOf("\r\n")).contains("?")){
                token.setBusiCode(fullMessage.substring(5, fullMessage.indexOf("?")));
                String urlParam = fullMessage.substring(fullMessage.indexOf("?")+1, this.getHTTPVersionPosition(fullMessage)-1);
                if(contentLength > 0){
                    token.setBusiMessage(urlParam + "`" + fullMessage.split("\r\n\r\n")[1]);
                }else{
                    token.setBusiMessage(urlParam);
                }
            //POST /login HTTP/1.1
            }else{
                token.setBusiCode(fullMessage.substring(5, this.getHTTPVersionPosition(fullMessage)-1));
            }
        }
        out.write(token);
        return MessageDecoderResult.OK;
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        //暂时什么都不做
    }

    /**
     * 校验HTTP请求报文是否已完整接收
     * 目前仅授理GET和POST请求
     * ----------------------------------------------------------------------------------------------
     * GET /notify_yeepay?p1_MerId=11&r0_Cmd=Buy&r1_Code=1&r2_TrxId=22 HTTP/1.1^M
     * Content-Type: application/x-www-form-urlencoded; charset=GBK^M
     * Cache-Control: no-cache^M
     * Pragma: no-cache^M
     * User-Agent: Java/1.5.0_14^M
     * Host: 123.125.97.248^M
     * Accept: text/html, image/gif, image/jpeg, *; q=.2, 星号/*; q=.2^M
     * Connection: keep-alive^M
     * ^M
     * ----------------------------------------------------------------------------------------------
     * POST /tra/trade/noCardNoPassword.htm HTTP/1.1^M
     * Content-Type: application/x-www-form-urlencoded;charset=GB18030^M
     * Cache-Control: no-cache^M
     * Pragma: no-cache^M
     * User-Agent: Java/1.6.0_24^M
     * Host: 192.168.20.1^M
     * Accept: text/html, image/gif, image/jpeg, *; q=.2, 星号/*; q=.2^M
     * Connection: keep-alive^M
     * Content-Length: 541^M
     * ^M
     * cooBankNo=CMBC_CREDIT&signType=MD5&amount=499900&orderValidityNum=15&CVVNo=255
     * ----------------------------------------------------------------------------------------------
     * 至于上面所列的GET和POST请求原始报文中为何会出现^M
     * 我的博客上有详细说明：http://jadyer.cn/2012/11/22/linux-crlf/
     * ----------------------------------------------------------------------------------------------
     * @param in 装载HTTP请求报文的IoBuffer
     */
    private boolean isComplete(IoBuffer in){
        /*
         * 先获取HTTP请求的原始报文
         */
        byte[] messages = new byte[in.limit()];
        in.get(messages);
        String message = StringUtils.toEncodedString(messages, Charset.forName(SeedConstants.DEFAULT_CHARSET));
        /*
         * 授理GET请求
         */
        if(message.startsWith("GET")){
            return message.endsWith("\r\n\r\n");
        }
        /*
         * 授理POST请求
         */
        if(message.startsWith("POST")){
            if(message.contains("Content-Length:")){
                //取Content-Length后的字符串
                String msgLenFlag = message.substring(message.indexOf("Content-Length:") + 15);
                if(msgLenFlag.contains("\r\n")){
                    //取Content-Length值
                    int contentLength = Integer.parseInt(msgLenFlag.substring(0, msgLenFlag.indexOf("\r\n")).trim());
                    if(contentLength == 0){
                        return true;
                    }else if(contentLength >= 0){
                        //取HTTP_POST请求报文体
                        String messageBody = message.split("\r\n\r\n")[1];
                        try {
                            if(contentLength == messageBody.getBytes(SeedConstants.DEFAULT_CHARSET).length){
                                return true;
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new IllegalArgumentException("将HTTP_POST请求报文体转为byte[]时发生异常：Unsupported Encoding-->[" + SeedConstants.DEFAULT_CHARSET + "]");
                        }
                    }
                }
            }
        }
        /*
         * 仅授理GET和POST请求
         */
        return false;
    }

    /**
     * 获取请求报文中HTTP版本号的位置
     * @param reqMsg 请求报文
     */
    private int getHTTPVersionPosition(String reqMsg){
        if(reqMsg.contains("HTTP/1.0\r\n")){
            return reqMsg.indexOf("HTTP/1.0\r\n");
        }else if(reqMsg.contains("HTTP/1.1\r\n")){
            return reqMsg.indexOf("HTTP/1.1\r\n");
        }else{
            return 22;
        }
    }
}