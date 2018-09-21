package com.jadyer.seed.mpp.sdk.weixin.msg;

import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutCustomServiceMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutNewsMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutTextMsg;

/**
 * 回复微信公众号消息的构建器
 * @create Oct 18, 2015 2:01:23 PM
 * @author 玄玉<https://jadyer.cn/>
 */
public class WeixinOutMsgXmlBuilder {
    private WeixinOutMsgXmlBuilder(){}

    //public static void write(WeixinOutMsg outMsg, HttpServletResponse response){
    //    try{
    //        PrintWriter out = response.getWriter();
    //        out.write(build(outMsg));
    //        out.flush();
    //        out.close();
    //    }catch(IOException e){
    //        throw new RuntimeException(e);
    //    }
    //}


    public static String build(WeixinOutMsg outMsg){
        if(null == outMsg){
            throw new IllegalArgumentException("空的回复消息");
        }
        if("text".equals(outMsg.getMsgType())){
            return buildOutTextMsg((WeixinOutTextMsg)outMsg);
        }
        if("image".equals(outMsg.getMsgType())){
            return buildOutImageMsg((WeixinOutImageMsg)outMsg);
        }
        if("news".equals(outMsg.getMsgType())){
            return buildOutNewsMsg((WeixinOutNewsMsg)outMsg);
        }
        if("transfer_customer_service".equals(outMsg.getMsgType())){
            return buildOutCustomServiceMsg((WeixinOutCustomServiceMsg)outMsg);
        }
        throw new RuntimeException("未知的消息类型" + outMsg.getMsgType() + ", 请查阅微信公众平台开发者文档http://mp.weixin.qq.com/wiki/home/index.html.");
    }


    /**
     * 回复文本消息
     * @see -----------------------------------------------------------------------------------------------------------
     * @see 1.汉字直接传入即可,不需要手工编码,微信上回显的不会乱码(微信服务器POST消息时用的是UTF-8编码,响应消息时也要UTF-8编码)
     * @see 2.若想在文本中写链接,那么传入"欢迎访问<a href=\"https://jadyer.cn/\">我的博客</a>"即可
     * @see 3.下面是开发者服务器回复给微信服务器的XML报文格式
     * @see <xml>
     * @see     <ToUserName><![CDATA[toUser]]></ToUserName>
     * @see     <FromUserName><![CDATA[fromUser]]></FromUserName>
     * @see     <CreateTime>12345678</CreateTime>
     * @see     <MsgType><![CDATA[text]]></MsgType>
     * @see     <Content><![CDATA[你好]]></Content>
     * @see </xml>
     * @see -----------------------------------------------------------------------------------------------------------
     */
    private static String buildOutTextMsg(WeixinOutTextMsg outTexgMsg){
        if(WeixinConstants.NOT_NEED_REPLY_FLAG.equals(outTexgMsg.getContent())){
            return WeixinConstants.NOT_NEED_REPLY_FLAG;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>")
          .append("<ToUserName><![CDATA[").append(outTexgMsg.getToUserName()).append("]]></ToUserName>")
          .append("<FromUserName><![CDATA[").append(outTexgMsg.getFromUserName()).append("]]></FromUserName>")
          .append("<CreateTime>").append(outTexgMsg.getCreateTime()).append("</CreateTime>")
          .append("<MsgType><![CDATA[").append(outTexgMsg.getMsgType()).append("]]></MsgType>")
          .append("<Content><![CDATA[").append(outTexgMsg.getContent()).append("]]></Content>")
          .append("</xml>");
        return sb.toString();
    }


    /**
     * 回复图片消息
     * @see -----------------------------------------------------------------------------------------------------------
     * @see 1.未认证的订阅号没有上传永久图片素材接口的权限,但粉丝主动发图片给公众号,开发者服务器能在后台看到图片的MediaId
     * @see 2.下面是开发者服务器回复给微信服务器的XML报文格式
     * @see <xml>
     * @see     <ToUserName><![CDATA[toUser]]></ToUserName>
     * @see     <FromUserName><![CDATA[fromUser]]></FromUserName>
     * @see     <CreateTime>12345678</CreateTime>
     * @see     <MsgType><![CDATA[image]]></MsgType>
     * @see     <Image>
     * @see         <MediaId><![CDATA[media_id]]></MediaId>
     * @see     </Image>
     * @see </xml>
     * @see -----------------------------------------------------------------------------------------------------------
     */
    private static String buildOutImageMsg(WeixinOutImageMsg outImageMsg){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>")
          .append("<ToUserName><![CDATA[").append(outImageMsg.getToUserName()).append("]]></ToUserName>")
          .append("<FromUserName><![CDATA[").append(outImageMsg.getFromUserName()).append("]]></FromUserName>")
          .append("<CreateTime>").append(outImageMsg.getCreateTime()).append("</CreateTime>")
          .append("<MsgType><![CDATA[").append(outImageMsg.getMsgType()).append("]]></MsgType>")
          .append("<Image>")
          .append("<MediaId><![CDATA[").append(outImageMsg.getMediaId()).append("]]></MediaId>")
          .append("</Image>")
          .append("</xml>");
        return sb.toString();
    }


    /**
     * 回复图文消息
     * @see -----------------------------------------------------------------------------------------------------------
     * @see <xml>
     * @see     <ToUserName><![CDATA[toUser]]></ToUserName>
     * @see     <FromUserName><![CDATA[fromUser]]></FromUserName>
     * @see     <CreateTime>12345678</CreateTime>
     * @see     <MsgType><![CDATA[news]]></MsgType>
     * @see     <ArticleCount>2</ArticleCount>
     * @see     <Articles>
     * @see         <item>
     * @see             <Title><![CDATA[title1]]></Title>
     * @see             <Description><![CDATA[description1]]></Description>
     * @see             <PicUrl><![CDATA[picurl]]></PicUrl>
     * @see             <Url><![CDATA[url]]></Url>
     * @see         </item>
     * @see         <item>
     * @see             <Title><![CDATA[title]]></Title>
     * @see             <Description><![CDATA[description]]></Description>
     * @see             <PicUrl><![CDATA[picurl]]></PicUrl>
     * @see             <Url><![CDATA[url]]></Url>
     * @see         </item>
     * @see     </Articles>
     * @see </xml>
     * @see -----------------------------------------------------------------------------------------------------------
     */
    private static String buildOutNewsMsg(WeixinOutNewsMsg outNewsMsg){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>")
          .append("<ToUserName><![CDATA[").append(outNewsMsg.getToUserName()).append("]]></ToUserName>")
          .append("<FromUserName><![CDATA[").append(outNewsMsg.getFromUserName()).append("]]></FromUserName>")
          .append("<CreateTime>").append(outNewsMsg.getCreateTime()).append("</CreateTime>")
          .append("<MsgType><![CDATA[").append(outNewsMsg.getMsgType()).append("]]></MsgType>")
          .append("<ArticleCount>").append(outNewsMsg.getArticleCount()).append("</ArticleCount>")
          .append("<Articles>");
        if(!outNewsMsg.getArticles().isEmpty()){
            for(WeixinOutNewsMsg.WeixinNews news : outNewsMsg.getArticles()){
                sb.append("<item>")
                  .append("<Title><![CDATA[").append(news.getTitle()).append("]]></Title>")
                  .append("<Description><![CDATA[").append(news.getDescription()).append("]]></Description>")
                  .append("<PicUrl><![CDATA[").append(news.getPicUrl()).append("]]></PicUrl>")
                  .append("<Url><![CDATA[").append(news.getUrl()).append("]]></Url>")
                  .append("</item>");
            }
        }
        sb.append("</Articles>")
          .append("</xml>");
        return sb.toString();
    }


    /**
     * 转发多客服消息
     * @see -----------------------------------------------------------------------------------------------------------
     * @see <xml>
     * @see     <ToUserName><![CDATA[toUser]]></ToUserName>
     * @see     <FromUserName><![CDATA[fromUser]]></FromUserName>
     * @see     <CreateTime>1399197672</CreateTime>
     * @see     <MsgType><![CDATA[transfer_customer_service]]></MsgType>
     * @see </xml>
     * @see -----------------------------------------------------------------------------------------------------------
     */
    private static String buildOutCustomServiceMsg(WeixinOutCustomServiceMsg outCustomServiceMsg){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>")
          .append("<ToUserName><![CDATA[").append(outCustomServiceMsg.getToUserName()).append("]]></ToUserName>")
          .append("<FromUserName><![CDATA[").append(outCustomServiceMsg.getFromUserName()).append("]]></FromUserName>")
          .append("<CreateTime>").append(outCustomServiceMsg.getCreateTime()).append("</CreateTime>")
          .append("<MsgType><![CDATA[").append(outCustomServiceMsg.getMsgType()).append("]]></MsgType>")
          .append("</xml>");
        return sb.toString();
    }
}