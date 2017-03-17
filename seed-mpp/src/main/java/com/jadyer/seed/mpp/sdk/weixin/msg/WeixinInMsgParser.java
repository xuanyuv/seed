package com.jadyer.seed.mpp.sdk.weixin.msg;

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
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 微信服务器请求消息解析器
 * @create Oct 18, 2015 12:53:31 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinInMsgParser{
	private WeixinInMsgParser(){}

	/**
	 * 解析微信服务器请求的xml报文体为com.jadyer.sdk.weixin.msg.in.WeixinInMsg对象
	 * @see 若无法识别请求的MsgType或解析xml出错,会抛出RuntimeException
	 * @create Oct 18, 2015 12:59:48 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static WeixinInMsg parse(String xml) {
		try {
			return doParse(xml);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}


	private static WeixinInMsg doParse(String xml) throws DocumentException {
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
		if("link".equals(msgType)){
			return parseInLinkMsg(root, toUserName, fromUserName, createTime, msgType);
		}
		if("event".equals(msgType)){
			return parseInEventMsg(root, toUserName, fromUserName, createTime, msgType);
		}
		throw new RuntimeException("未知的消息类型" + msgType + ", 请查阅微信公众平台开发者文档http://mp.weixin.qq.com/wiki/home/index.html.");
	}


	private static WeixinInMsg parseInTextMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
		WeixinInTextMsg msg = new WeixinInTextMsg(toUserName, fromUserName, createTime, msgType);
		msg.setContent(root.elementText("Content"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}


	private static WeixinInMsg parseInImageMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
		WeixinInImageMsg msg = new WeixinInImageMsg(toUserName, fromUserName, createTime, msgType);
		msg.setPicUrl(root.elementText("PicUrl"));
		msg.setMediaId(root.elementText("MediaId"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}


	private static WeixinInMsg parseInLocationMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
		WeixinInLocationMsg msg = new WeixinInLocationMsg(toUserName, fromUserName, createTime, msgType);
		msg.setLocation_X(root.elementText("Location_X"));
		msg.setLocation_Y(root.elementText("Location_Y"));
		msg.setScale(root.elementText("Scale"));
		msg.setLabel(root.elementText("Label"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}


	private static WeixinInMsg parseInLinkMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
		WeixinInLinkMsg msg = new WeixinInLinkMsg(toUserName, fromUserName, createTime, msgType);
		msg.setTitle(root.elementText("Title"));
		msg.setDescription(root.elementText("Description"));
		msg.setUrl(root.elementText("Url"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}


	private static WeixinInMsg parseInEventMsg(Element root, String toUserName, String fromUserName, long createTime, String msgType) {
		String event = root.elementText("Event");
		String eventKey = root.elementText("EventKey");
		//包括二维码扫描关注在内的关注/取消关注事件(二维码扫描关注事件与扫描带参数二维码事件是不一样的)
		if(("subscribe".equals(event) || "unsubscribe".equals(event)) && StringUtils.isBlank(eventKey)){
			return new WeixinInFollowEventMsg(toUserName, fromUserName, createTime, msgType, event);
		}
		//扫描带参数二维码事件之用户未关注时,进行关注后的事件推送
		if("subscribe".equals(event) && StringUtils.isNotBlank(eventKey) && eventKey.startsWith("qrscene_")){
			WeixinInQrcodeEventMsg e = new WeixinInQrcodeEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setEventKey(eventKey);
			e.setTicket(root.elementText("Ticket"));
			return e;
		}
		//扫描带参数二维码事件之用户已关注时的事件推送
		if("SCAN".equals(event)){
			WeixinInQrcodeEventMsg e = new WeixinInQrcodeEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setEventKey(eventKey);
			e.setTicket(root.elementText("Ticket"));
			return e;
		}
		//自定义菜单事件之点击菜单拉取消息时的事件推送
		if("CLICK".equals(event)){
			WeixinInMenuEventMsg e = new WeixinInMenuEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setEventKey(eventKey);
			return e;
		}
		//自定义菜单事件之点击菜单跳转链接时的事件推送
		if("VIEW".equals(event)){
			WeixinInMenuEventMsg e = new WeixinInMenuEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setEventKey(eventKey);
			return e;
		}
		//多客服接入会话事件
		if("kf_create_session".equals(event)){
			WeixinInCustomServiceEventMsg e = new WeixinInCustomServiceEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setKfAccount(root.elementText("KfAccount"));
			return e;
		}
		//多客服关闭会话事件
		if("kf_close_session".equals(event)){
			WeixinInCustomServiceEventMsg e = new WeixinInCustomServiceEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setKfAccount(root.elementText("KfAccount"));
			return e;
		}
		//多客服转接会话事件
		if("kf_switch_session".equals(event)){
			WeixinInCustomServiceEventMsg e = new WeixinInCustomServiceEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setKfAccount(root.elementText("KfAccount"));
			e.setToKfAccount(root.elementText("ToKfAccount"));
			return e;
		}
		//模板消息送达事件或模板消息阅读回执事件
		if("TEMPLATESENDJOBFINISH".equals(event) || "TEMPLATEFANMSGREAD".equals(event)){
			WeixinInTemplateEventMsg e = new WeixinInTemplateEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setMsgID(root.elementText("MsgID"));
			e.setStatus(root.elementText("Status"));
			return e;
		}
		//上報地理位置事件
		if("LOCATION".equals(event)){
			WeixinInLocationEventMsg e = new WeixinInLocationEventMsg(toUserName, fromUserName, createTime, msgType, event);
			e.setLatitude(root.elementText("Latitude"));
			e.setLongitude(root.elementText("Longitude"));
			e.setPrecision(root.elementText("Precision"));
			return e;
		}
		throw new RuntimeException("未知的事件类型" + event + ", 请查阅微信公众平台开发者文档http://mp.weixin.qq.com/wiki/home/index.html.");
	}
}