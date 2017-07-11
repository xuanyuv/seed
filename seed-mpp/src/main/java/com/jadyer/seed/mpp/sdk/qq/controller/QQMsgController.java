package com.jadyer.seed.mpp.sdk.qq.controller;

import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.qq.msg.QQInMsgParser;
import com.jadyer.seed.mpp.sdk.qq.msg.QQOutMsgXmlBuilder;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInImageMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInLocationMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInTextMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInTemplateEventMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.out.QQOutMsg;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * 接收QQ服务器消息，自动解析成com.jadyer.seed.mpp.sdk.qq.msg.in.QQInMsg
 * 并分发到相应的处理方法，得到处理后的com.jadyer.seed.mpp.sdk.qq.msg.out.QQOutMsg并回复给QQ服务器
 * Created by 玄玉<http://jadyer.cn/> on 2015/11/26 19:22.
 */
public abstract class QQMsgController {
    @RequestMapping(value="/{uuid}")
    public void index(@PathVariable String uuid, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(Constants.MPP_CHARSET_UTF8);
        PrintWriter out = response.getWriter();
        String reqBodyMsg = JadyerUtil.extractHttpServletRequestBodyMessage(request);
        LogUtil.getLogger().info("收到QQ服务器消息如下\n{}", JadyerUtil.extractHttpServletRequestHeaderMessage(request)+"\n"+reqBodyMsg);
        //GET过来的请求表示更新开发者服务器URL
        if("GET".equalsIgnoreCase(request.getMethod())){
            //验签
            if(!this.verifySignature(DigestUtils.md5Hex(uuid+"http://jadyer.cn/"), request)){
                out.write("verify signature failed");
                out.flush();
                out.close();
                return;
            }
            out.write(request.getParameter("echostr"));
            out.flush();
            out.close();
            return;
        }
        //POST过来的请求表示QQ服务器请求通信
        QQInMsg inMsg = QQInMsgParser.parse(reqBodyMsg);
        QQOutMsg outMsg = new QQOutMsg();
        if(inMsg instanceof QQInTextMsg){
            outMsg = this.processInTextMsg((QQInTextMsg)inMsg);
        }
        if(inMsg instanceof QQInImageMsg){
            outMsg = this.processInImageMsg((QQInImageMsg)inMsg);
        }
        if(inMsg instanceof QQInLocationMsg){
            outMsg = this.processInLocationMsg((QQInLocationMsg)inMsg);
        }
        if(inMsg instanceof QQInFollowEventMsg){
            outMsg = this.processInFollowEventMsg((QQInFollowEventMsg)inMsg);
        }
        if(inMsg instanceof QQInMenuEventMsg){
            outMsg = this.processInMenuEventMsg((QQInMenuEventMsg)inMsg);
        }
        if(inMsg instanceof QQInTemplateEventMsg){
            outMsg = this.processInTemplateEventMsg((QQInTemplateEventMsg)inMsg);
        }
        String outMsgXml = QQOutMsgXmlBuilder.build(outMsg);
        out.write(outMsgXml);
        out.flush();
        out.close();
        LogUtil.getLogger().info("应答QQ服务器消息-->{}", outMsgXml);
    }


    /**
     * 验签
     */
    private boolean verifySignature(String token, HttpServletRequest request){
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        if(StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp) || StringUtils.isBlank(nonce)){
            return false;
        }
        String[] signPlains = new String[]{token, nonce, timestamp};
        Arrays.sort(signPlains);
        return signature.equals(DigestUtils.sha1Hex(signPlains[0] + signPlains[1] + signPlains[2]));
    }


    /**
     * 处理收到的文本消息
     */
    protected abstract QQOutMsg processInTextMsg(QQInTextMsg inTextMsg);


    /**
     * 处理收到的图片消息
     */
    protected abstract QQOutMsg processInImageMsg(QQInImageMsg inImageMsg);


    /**
     * 处理收到的地址位置消息
     */
    protected abstract QQOutMsg processInLocationMsg(QQInLocationMsg inLocationMsg);


    /**
     * 处理收到的关注/取消关注事件
     */
    protected abstract QQOutMsg processInFollowEventMsg(QQInFollowEventMsg inFollowEventMsg);


    /**
     * 处理自定义菜单拉取消息/跳转链接的事件
     * <p>
     *     经测试：对于VIEW类型的URL跳转类，不会推到开发者服务器而是直接跳过去
     * </p>
     */
    protected abstract QQOutMsg processInMenuEventMsg(QQInMenuEventMsg inMenuEventMsg);


    /**
     * 处理模板消息送达事件
     */
    protected abstract QQOutMsg processInTemplateEventMsg(QQInTemplateEventMsg inTemplateEventMsg);
}