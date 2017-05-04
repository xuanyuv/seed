package com.jadyer.seed.mpp.sdk.qq.msg;

import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInImageMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInLocationMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInTextMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInTemplateEventMsg;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * QQ服务器请求消息解析器
 * @create Nov 26, 2015 7:27:24 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class QQInMsgParser{
    private QQInMsgParser(){}

    /**
     * 解析QQ服务器请求的xml报文体为com.jadyer.sdk.qq.msg.in.QQInMsg对象
     * @see 若无法识别请求的MsgType或解析xml出错,会抛出RuntimeException
     * @create Nov 26, 2015 7:28:14 PM
     * @author 玄玉<http://jadyer.cn/>
     */
    public static QQInMsg parse(String xml) {
        try {
            return doParse(xml);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }


    private static QQInMsg doParse(String xml) throws DocumentException {
        Document doc = DocumentHelper.parseText(xml);
        Element root = doc.getRootElement();
        String toUserName = root.elementText("ToUserName");
        String fromUserName = root.elementText("FromUserName");
        long createTime = Long.parseLong(root.elementText("CreateTime"));
        String msgType = root.elementText("MsgType");
        if("text".equals(msgType)){
            return parseInTextMsg(root, toUserName, fromUserName, createTime, msgType);
        }
        if("image".equals(msgType)){
            return parseInImageMsg(root, toUserName, fromUserName, createTime, msgType);
        }
        if("location".equals(msgType)){
            return parseInLocationMsg(root, toUserName, fromUserName, createTime, msgType);
        }
        if("event".equals(msgType)){
            return parseInEventMsg(root, toUserName, fromUserName, createTime, msgType);
        }
        throw new RuntimeException("未知的消息类型" + msgType + ", 请查阅QQ公众平台开发者文档http://mp.qq.com/");
    }


    private static QQInMsg parseInTextMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
        QQInTextMsg msg = new QQInTextMsg(toUserName, fromUserName, createTime, msgType);
        msg.setContent(root.elementText("Content"));
        msg.setMsgId(root.elementText("MsgId"));
        return msg;
    }


    private static QQInMsg parseInImageMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
        QQInImageMsg msg = new QQInImageMsg(toUserName, fromUserName, createTime, msgType);
        msg.setPicUrl(root.elementText("PicUrl"));
        //msg.setMediaId(root.elementText("MediaId"));
        msg.setMsgId(root.elementText("MsgId"));
        return msg;
    }


    private static QQInMsg parseInLocationMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
        QQInLocationMsg msg = new QQInLocationMsg(toUserName, fromUserName, createTime, msgType);
        msg.setLocation_X(root.elementText("Location_X"));
        msg.setLocation_Y(root.elementText("Location_Y"));
        msg.setScale(root.elementText("Scale"));
        msg.setLabel(root.elementText("Label"));
        msg.setMsgId(root.elementText("MsgId"));
        return msg;
    }


    private static QQInMsg parseInEventMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
        String event = root.elementText("Event");
        String eventKey = root.elementText("EventKey");
        //包括二维码扫描关注在内的关注/取消关注事件(二维码扫描关注事件与扫描带参数二维码事件是不一样的)
        if(("subscribe".equals(event) || "unsubscribe".equals(event)) && StringUtils.isBlank(eventKey)){
            return new QQInFollowEventMsg(toUserName, fromUserName, createTime, msgType, event);
        }
        //自定义菜单事件之点击菜单拉取消息时的事件推送
        if("CLICK".equals(event)){
            QQInMenuEventMsg e = new QQInMenuEventMsg(toUserName, fromUserName, createTime, msgType, event);
            e.setEventKey(eventKey);
            return e;
        }
        //自定义菜单事件之点击菜单跳转链接时的事件推送
        if("VIEW".equals(event)){
            QQInMenuEventMsg e = new QQInMenuEventMsg(toUserName, fromUserName, createTime, msgType, event);
            e.setEventKey(eventKey);
            return e;
        }
        //模板消息送达事件或模板消息阅读回执事件
        if("TEMPLATESENDJOBFINISH".equals(event) || "TEMPLATEFANMSGREAD".equals(event)){
            QQInTemplateEventMsg e = new QQInTemplateEventMsg(toUserName, fromUserName, createTime, msgType, event);
            e.setMsgID(root.elementText("MsgID"));
            e.setStatus(root.elementText("Status"));
        }
        throw new RuntimeException("未知的事件类型" + event + ", 请查阅QQ公众平台开发者文档http://mp.qq.com/");
    }
}