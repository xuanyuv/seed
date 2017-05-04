package com.jadyer.seed.server.core;

/**
 * 客户端请求应答顶层接口
 * Created by 玄玉<http://jadyer.cn/> on 2012/12/23 19:25.
 */
public interface GenericAction {
    /**
     * 由其子类（即各接口业务实现类）负责具体的业务逻辑处理
     * <p>
     *     对于TCP请求，则该方法须返回Socket明文字符串
     *     对于HTTP请求，该方法须返回HTTP响应的完整报文的明文字符串
     * </p>
     * @param message 请求方发送的业务相关的原始报文
     * @return 响应给请求方（客户端）的字符串格式报文
     */
    String execute(String message);
}