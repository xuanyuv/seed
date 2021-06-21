package com.jadyer.seed.server.core;

import com.jadyer.seed.comm.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import java.nio.charset.StandardCharsets;

/**
 * Server端TCP协议解码器
 * <p>
 *     用于解码接收到的TCP请求报文
 * </p>
 * ------------------------------------------------------------------------------------------------------
 * 当收到数据包时，程序首先会执行decodable()方法，通过读取数据判断当前数据包是否可进行decode
 * 当decodable()方法返回MessageDecoderResult.OK时，接着会调用decode()方法，正式decode数据包
 * 在decode()方法进行读取操作会影响数据包的大小，decode需要判断协议中哪些已经decode完，哪些还没decode
 * decode完成后，通过ProtocolDecoderOutput.write()输出，并返回MessageDecoderResult.OK表示decode完毕
 * ------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2012/12/21 14:52.
 */
public class ServerProtocolTCPDecoder implements MessageDecoder {
    /**
     * 该方法相当于预读取,用于判断是否是可用的解码器,这里对IoBuffer读取不会影响数据包的大小
     * 该方法结束后IoBuffer会复原,所以不必担心调用该方法时,position已经不在缓冲区起始位置
     */
    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        //in.remaining()=in.limit()-in.position();
        if(in.remaining() < 6){
            return MessageDecoderResult.NEED_DATA;
        }
        if(session.getLocalAddress().toString().contains(":" + ConfigUtil.INSTANCE.getProperty("server.port.tcp"))){
            byte[] messageLength = new byte[6];
            in.get(messageLength);
            if(in.limit() >= Integer.parseInt(StringUtils.toEncodedString(messageLength, StandardCharsets.UTF_8))){
                return MessageDecoderResult.OK;
            }else{
                return MessageDecoderResult.NEED_DATA;
            }
        }else{
            return MessageDecoderResult.NOT_OK;
        }
    }

    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        byte[] message = new byte[in.limit()];
        in.get(message);
        String fullMessage = StringUtils.toEncodedString(message, StandardCharsets.UTF_8);
        Token token = new Token();
        token.setBusiCharset(StandardCharsets.UTF_8.displayName());
        token.setBusiType(Token.BUSI_TYPE_TCP);
        token.setBusiCode(fullMessage.substring(6, 11));
        token.setBusiMessage(fullMessage);
        token.setFullMessage(fullMessage);
        out.write(token);
        return MessageDecoderResult.OK;
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        //暂时什么都不做
    }
}