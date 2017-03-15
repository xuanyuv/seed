package com.jadyer.seed.server.core;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Server端协议编码器
 * <p>
 *     用于编码响应给Client的报文
 * </p>
 * Created by 玄玉<https://jadyer.github.io/> on 2012/12/21 13:28.
 */
@Component
public class ServerProtocolEncoder implements MessageEncoder<String> {
	@Value("${server.listening.port.tcp}")
	private String listeningTcpPort;
	@Value("${server.listening.port.http}")
	private String listeningHttpPort;

	@Override
	public void encode(IoSession session, String message, ProtocolEncoderOutput out) throws Exception {
		String charset;
		if(session.getLocalAddress().toString().contains(":" + listeningTcpPort)){
			charset = ServerProtocolTCPDecoder.DEFAULT_TCP_CHARSET;
		}else if(session.getLocalAddress().toString().contains(":" + listeningHttpPort)){
			charset = ServerProtocolHTTPDecoder.DEFAULT_HTTP_CHARSET;
		}else{
			charset = StandardCharsets.UTF_8.toString();
		}
		IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
		buffer.putString(message, Charset.forName(charset).newEncoder());
		buffer.flip();
		out.write(buffer);
	}
}