package com.jadyer.seed.server.action;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.server.core.GenericAction;
import com.jadyer.seed.server.helper.MessageBuilder;
import org.springframework.stereotype.Controller;

/**
 * 中国银行网银结果通知
 * Created by 玄玉<http://jadyer.cn/> on 2013/9/3 20:40.
 */
@Controller
public class NetBankResultNotifyBOCAction implements GenericAction {
    @Override
    public String execute(String message) {
        LogUtil.getLogger().info("中行网银结果通知-->收到请求报文[" + message + "]");
        return MessageBuilder.buildHTTPResponseMessage("您已成功连接本系统HTTP服务器...");
    }
}