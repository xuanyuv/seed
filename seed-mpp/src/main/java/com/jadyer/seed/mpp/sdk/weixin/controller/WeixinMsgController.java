package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.RequestUtil;
import com.jadyer.seed.mpp.sdk.weixin.msg.WeixinInMsgParser;
import com.jadyer.seed.mpp.sdk.weixin.msg.WeixinOutMsgXmlBuilder;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLinkMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLocationMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInTextMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInCustomServiceEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInLocationEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInQrcodeEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInTemplateEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 接收微信服务器消息，自动解析成com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg
 * 并分发到相应的处理方法，得到处理后的com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg并回复给微信服务器
 */
//注意：从语法上讲，这里类名不需要public，但我们需要让它在SpringMVC作用下暴露接口出去，所以一定要public
public abstract class WeixinMsgController {
    @RequestMapping(value="/{uuid}")
    public void index(@PathVariable String uuid, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        PrintWriter out = response.getWriter();
        String reqBodyMsg = RequestUtil.extractHttpServletRequestBodyMessage(request);
        LogUtil.getLogger().info("收到微信服务器消息如下\n{}", RequestUtil.extractHttpServletRequestHeaderMessage(request)+"\n"+reqBodyMsg);
        //验签
        if(!this.verifySignature(DigestUtils.md5Hex(uuid+"https://jadyer.cn/"), request)){
            out.write("verify signature failed");
            out.flush();
            out.close();
            return;
        }
        //GET过来的请求表示更新开发者服务器URL
        if("GET".equalsIgnoreCase(request.getMethod())){
            out.write(request.getParameter("echostr"));
            out.flush();
            out.close();
            return;
        }
        //POST过来的请求表示微信服务器请求通信
        //https://github.com/chanjarster/weixin-java-tools/wiki/MP_%E6%B6%88%E6%81%AF%E7%9A%84%E5%8A%A0%E8%A7%A3%E5%AF%86
        //http://www.lai18.com/content/1437764.html
        //http://www.dannysite.com/blog/213/
        //http://git.devzeng.com/blog/wechat-qiyehao-developement-with-url-config-and-verify.html
        //http://chn0515.blog.51cto.com/10646281/1691201
        //String aeskey = "WhyEzMHrSKfV5HbsVf5DjZfV3yx7bsJ7TUivKdeeH22";
        //decrypt(aeskey);
        WeixinInMsg inMsg = WeixinInMsgParser.parse(reqBodyMsg);
        WeixinOutMsg outMsg = new WeixinOutMsg();
        if(inMsg instanceof WeixinInTextMsg){
            outMsg = this.processInTextMsg((WeixinInTextMsg)inMsg);
        }
        if(inMsg instanceof WeixinInImageMsg){
            outMsg = this.processInImageMsg((WeixinInImageMsg)inMsg);
        }
        if(inMsg instanceof WeixinInLocationMsg){
            outMsg = this.processInLocationMsg((WeixinInLocationMsg)inMsg);
        }
        if(inMsg instanceof WeixinInLinkMsg){
            outMsg = this.processInLinkMsg((WeixinInLinkMsg)inMsg);
        }
        if(inMsg instanceof WeixinInFollowEventMsg){
            outMsg = this.processInFollowEventMsg((WeixinInFollowEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInQrcodeEventMsg){
            outMsg = this.processInQrcodeEventMsg((WeixinInQrcodeEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInMenuEventMsg){
            outMsg = this.processInMenuEventMsg((WeixinInMenuEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInCustomServiceEventMsg){
            outMsg = this.processInCustomServiceEventMsg((WeixinInCustomServiceEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInTemplateEventMsg){
            outMsg = this.processInTemplateEventMsg((WeixinInTemplateEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInLocationEventMsg){
            outMsg = this.processInLocationEventMsg((WeixinInLocationEventMsg)inMsg);
        }
        String outMsgXml = WeixinOutMsgXmlBuilder.build(outMsg);
        out.write(outMsgXml);
        out.flush();
        out.close();
        LogUtil.getLogger().info("应答微信服务器消息-->{}", outMsgXml);
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
    protected abstract WeixinOutMsg processInTextMsg(WeixinInTextMsg inTextMsg);


    /**
     * 处理收到的图片消息
     */
    protected abstract WeixinOutMsg processInImageMsg(WeixinInImageMsg inImageMsg);


    /**
     * 处理收到的地址位置消息
     */
    protected abstract WeixinOutMsg processInLocationMsg(WeixinInLocationMsg inLocationMsg);


    /**
     * 处理收到的链接消息
     */
    protected abstract WeixinOutMsg processInLinkMsg(WeixinInLinkMsg inLinkMsg);


    /**
     * 处理收到的关注/取消关注事件
     */
    protected abstract WeixinOutMsg processInFollowEventMsg(WeixinInFollowEventMsg inFollowEventMsg);


    /**
     * 处理收到的扫描带参数二维码事件
     */
    protected abstract WeixinOutMsg processInQrcodeEventMsg(WeixinInQrcodeEventMsg inQrcodeEventMsg);


    /**
     * 处理自定义菜单拉取消息/跳转链接的事件
     * <p>
     *     经测试：对于VIEW类型的URL跳转类，不会推到开发者服务器而是直接跳过去
     * </p>
     */
    protected abstract WeixinOutMsg processInMenuEventMsg(WeixinInMenuEventMsg inMenuEventMsg);


    /**
     * 处理多客服接入会话/关闭会话/转接会话的事件
     */
    protected abstract WeixinOutMsg processInCustomServiceEventMsg(WeixinInCustomServiceEventMsg inCustomServiceEventMsg);


    /**
     * 处理模板消息送达事件
     */
    protected abstract WeixinOutMsg processInTemplateEventMsg(WeixinInTemplateEventMsg inTemplateEventMsg);


    /**
     * 上報地理位置事件
     */
    protected abstract WeixinOutMsg processInLocationEventMsg(WeixinInLocationEventMsg inLocationEventMsg);
}