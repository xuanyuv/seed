package com.jadyer.seed.mpp.mgr;

import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.mgr.fans.FansInfoDao;
import com.jadyer.seed.mpp.mgr.fans.FansSaveThread;
import com.jadyer.seed.mpp.mgr.reply.ReplyInfoDao;
import com.jadyer.seed.mpp.mgr.reply.model.ReplyInfo;
import com.jadyer.seed.mpp.mgr.user.UserService;
import com.jadyer.seed.mpp.mgr.user.model.UserInfo;
import com.jadyer.seed.mpp.sdk.qq.constant.QQConstants;
import com.jadyer.seed.mpp.sdk.qq.controller.QQMsgController;
import com.jadyer.seed.mpp.sdk.qq.helper.QQHelper;
import com.jadyer.seed.mpp.sdk.qq.helper.QQTokenHolder;
import com.jadyer.seed.mpp.sdk.qq.model.QQErrorInfo;
import com.jadyer.seed.mpp.sdk.qq.model.template.QQTemplateMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInImageMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInLocationMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInTextMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.in.event.QQInTemplateEventMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.out.QQOutMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.out.QQOutNewsMsg;
import com.jadyer.seed.mpp.sdk.qq.msg.out.QQOutTextMsg;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping(value="/qq")
public class QQController extends QQMsgController {
	@Resource
	private UserService userService;
	@Resource
	private FansInfoDao fansInfoDao;
	@Resource
	private ReplyInfoDao replyInfoDao;

	@Override
	protected QQOutMsg processInTextMsg(QQInTextMsg inTextMsg) {
		//防伪
		UserInfo userInfo = userService.findByQqid(inTextMsg.getToUserName());
		if(null == userInfo){
			return new QQOutTextMsg(inTextMsg).setContent("该公众号未绑定");
		}
		//没绑定就提示绑定
		if("0".equals(userInfo.getBindStatus()) && !Constants.MPP_BIND_TEXT.equals(inTextMsg.getContent())){
			return new QQOutTextMsg(inTextMsg).setContent("账户未绑定\r请发送\"" + Constants.MPP_BIND_TEXT + "\"绑定");
		}
		//绑定
		if("0".equals(userInfo.getBindStatus()) && Constants.MPP_BIND_TEXT.equals(inTextMsg.getContent())){
			userInfo.setBindStatus("1");
			userInfo.setBindTime(new Date());
			userService.save(userInfo);
			return new QQOutTextMsg(inTextMsg).setContent("绑定完毕，升级成功");
		}
		//关键字查找(暂时只支持回复文本)
		ReplyInfo replyInfo = replyInfoDao.findByKeyword(userInfo.getId(), inTextMsg.getContent());
		if(null!=replyInfo && "0".equals(replyInfo.getType())){
			return new QQOutTextMsg(inTextMsg).setContent(replyInfo.getContent());
		}
		//否则原样返回
		//20151128183801测试发现QQ公众号暂时还不支持QQ表情的显示,但是支持在文本消息里写链接
		//return new QQOutTextMsg(inTextMsg).setContent("言毕，二人竟瞬息不见，步法之神令人叹绝。欲知后事如何，请访问<a href=\"http://blog.csdn.net/jadyer\">我的博客</a>[阴险]");
		return new QQOutTextMsg(inTextMsg).setContent(inTextMsg.getContent());
	}


	@Override
	protected QQOutMsg processInImageMsg(QQInImageMsg inImageMsg) {
		//QQOutNewsMsg outMsg = new QQOutNewsMsg(inImageMsg);
		//outMsg.addNews("查看刚才发送的图片", "第一个大图文描述", inImageMsg.getPicUrl(), inImageMsg.getPicUrl());
		//outMsg.addNews("点击访问我的博客", "第二个图文的描述", "http://img.my.csdn.net/uploads/201507/26/1437881866_3678.png", "http://blog.csdn.net/jadyer");
		//outMsg.addNews("点击访问我的Github", "第三个图文的描述", "http://img.my.csdn.net/uploads/201009/14/7892753_1284475095fyR0.jpg", "https://github.com/jadyer");
		//return outMsg;
		return new QQOutTextMsg(inImageMsg).setContent("<a href=\""+inImageMsg.getPicUrl()+"\">点此查看</a>刚才发送的图片");
	}


	@Override
	protected QQOutMsg processInLocationMsg(QQInLocationMsg inLocationMsg) {
		return new QQOutTextMsg(inLocationMsg).setContent(inLocationMsg.getLabel());
	}


	@Override
	protected QQOutMsg processInMenuEventMsg(QQInMenuEventMsg inMenuEventMsg) {
		//防伪
		UserInfo userInfo = userService.findByQqid(inMenuEventMsg.getToUserName());
		if(null == userInfo){
			return new QQOutTextMsg(inMenuEventMsg).setContent("该公众号未绑定");
		}
		//VIEW类的直接跳转过去了,CLICK类的暂定根据关键字回复
		if(QQInMenuEventMsg.EVENT_INMENU_CLICK.equals(inMenuEventMsg.getEvent())){
			ReplyInfo replyInfo = replyInfoDao.findByKeyword(userInfo.getId(), inMenuEventMsg.getEventKey());
			if(null == replyInfo){
				return new QQOutTextMsg(inMenuEventMsg).setContent("您刚才点击了菜单：" + inMenuEventMsg.getEventKey());
			}else{
				return new QQOutTextMsg(inMenuEventMsg).setContent(replyInfo.getContent());
			}
		}
		//返回特定的消息使得QQ服务器不会回复消息给用户手机上
		return new QQOutTextMsg(inMenuEventMsg).setContent(QQConstants.NOT_NEED_REPLY_FLAG);
	}


	@Override
	protected QQOutMsg processInFollowEventMsg(QQInFollowEventMsg inFollowEventMsg) {
		//防伪
		UserInfo userInfo = userService.findByQqid(inFollowEventMsg.getToUserName());
		if(null == userInfo){
			return new QQOutTextMsg(inFollowEventMsg).setContent("该公众号未绑定");
		}
		if(QQInFollowEventMsg.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEventMsg.getEvent())){
			//记录粉丝关注情况
			ExecutorService threadPool = Executors.newSingleThreadExecutor();
			threadPool.execute(new FansSaveThread(userInfo, inFollowEventMsg.getFromUserName()));
			threadPool.shutdown();
			QQOutNewsMsg outMsg = new QQOutNewsMsg(inFollowEventMsg);
			outMsg.addNews("欢迎关注", "更多精彩请访问我的博客", "http://img.my.csdn.net/uploads/201507/26/1437881866_3678.png", "http://blog.csdn.net/jadyer");
			return outMsg;
		}
		if(QQInFollowEventMsg.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEventMsg.getEvent())){
			fansInfoDao.updateSubscribe("0", userInfo.getId(), inFollowEventMsg.getFromUserName());
			LogUtil.getLogger().info("您的粉丝" + inFollowEventMsg.getFromUserName() + "取消关注了您");
		}
		//返回特定的消息使得QQ服务器不会继续发送通知
		return new QQOutTextMsg(inFollowEventMsg).setContent(QQConstants.NOT_NEED_REPLY_FLAG);
	}


	@Override
	protected QQOutMsg processInTemplateEventMsg(QQInTemplateEventMsg inTemplateEventMsg) {
		if(QQInTemplateEventMsg.EVENT_INTEMPLATE_TEMPLATEFANMSGREAD.equals(inTemplateEventMsg.getEvent())){
			LogUtil.getLogger().info("模板消息msgid={}阅读成功", inTemplateEventMsg.getMsgID());
		}
		if(QQInTemplateEventMsg.EVENT_INTEMPLATE_TEMPLATESENDJOBFINISH.equals(inTemplateEventMsg.getEvent())){
			if(QQInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_SUCCESS.equals(inTemplateEventMsg.getStatus())){
				LogUtil.getLogger().info("模板消息msgid={}送达成功", inTemplateEventMsg.getMsgID());
			}
			if(QQInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_BLOCK.equals(inTemplateEventMsg.getStatus())){
				LogUtil.getLogger().info("模板消息msgid={}由于{}而送达失败", inTemplateEventMsg.getMsgID(), "用户拒收（用户设置拒绝接收公众号消息）");
			}
			if(QQInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_FAILED.equals(inTemplateEventMsg.getStatus())){
				LogUtil.getLogger().info("模板消息msgid={}由于{}而送达失败", inTemplateEventMsg.getMsgID(), "其它原因");
			}
		}
		return new QQOutTextMsg(inTemplateEventMsg).setContent(QQConstants.NOT_NEED_REPLY_FLAG);
	}


	/**
	 * 网页授权静默获取粉丝openid
	 * @create Dec 24, 2015 12:10:25 AM
	 * @author 玄玉<http://blog.csdn.net/jadyer>
	 */
	@ResponseBody
	@RequestMapping(value="/getopenid")
	public String getopenid(String openid){
		return "your openid is [" + openid + "]";
	}


	/**
	 * 单发主动推模板消息
	 * @see {"button":{"url":{"name":"test","type":"view","value":"https://github.com/jadyer/JadyerSDK/"}},"data":{"keynote4":{"value":"通路无双"},"keynote3":{"value":"789"},"first":{"value":"天下无敌任我行"},"keynote2":{"value":"456"},"end":{"value":"随心所欲陪你玩"},"keynote1":{"value":"123"}},"templateid":"mytemplateid","tousername":"E12D231CFC30438FB6970B0C7669C101"}
	 * @see {"button":{"url":{"name":"test","type":"view","value":"https://github.com/jadyer/JadyerSDK/"}},"data":{"keynote4":{"value":"通路无双"},"keynote3":{"value":"789"},"first":{"value":"天下无敌任我行"},"keynote2":{"value":"456"},"end":{"value":"随心所欲陪你玩"},"keynote1":{"value":"123"}},"templateid":"mytemplateid","tousername":"E12D231CFC30438FB6970B0C7669C101","type":"view","url":"http://blog.csdn.net/jadyer"}
	 * @create Dec 30, 2015 11:38:36 PM
	 * @author 玄玉<http://blog.csdn.net/jadyer>
	 */
	@ResponseBody
	@RequestMapping(value="/pushQQTemplateMsgToFans")
	public QQErrorInfo pushQQTemplateMsgToFans(String appid, String appsecret, String openid, String templateid){
		QQTokenHolder.setQQAppidAppsecret(appid, appsecret);
		QQTemplateMsg.ButtonItem button = new QQTemplateMsg.ButtonItem();
		button.put("url", new QQTemplateMsg.BItem(QQTemplateMsg.TEMPLATE_MSG_TYPE_VIEW, "test", "https://github.com/jadyer/JadyerSDK/"));
		QQTemplateMsg.DataItem data = new QQTemplateMsg.DataItem();
		data.put("first", new QQTemplateMsg.DItem("天下无敌任我行"));
		data.put("end", new QQTemplateMsg.DItem("随心所欲陪你玩"));
		data.put("keynote1", new QQTemplateMsg.DItem("123"));
		data.put("keynote2", new QQTemplateMsg.DItem("456"));
		data.put("keynote3", new QQTemplateMsg.DItem("789"));
		data.put("keynote4", new QQTemplateMsg.DItem("通路无双"));
		QQTemplateMsg templateMsg = new QQTemplateMsg();
		templateMsg.setTousername(openid);
		templateMsg.setTemplateid(templateid);
		//templateMsg.setType(QQTemplateMsg.TEMPLATE_MSG_TYPE_VIEW);
		//templateMsg.setUrl("http://blog.csdn.net/jadyer");
		templateMsg.setData(data);
		templateMsg.setButton(button);
		return QQHelper.pushQQTemplateMsgToFans(QQTokenHolder.getQQAccessToken(appid), templateMsg);
	}


//	/**
//	 * 设置自定义菜单
//	 * @see http://127.0.0.1/demo/createQQMenu?appid=123&appsecret=123
//	 * @create Nov 28, 2015 9:02:22 PM
//	 * @author 玄玉<http://blog.csdn.net/jadyer>
//	 */
//	@ResponseBody
//	@RequestMapping(value="/createQQMenu")
//	public QQErrorInfo createQQMenu(String appid, String appsecret){
//		QQTokenHolder.setQQAppidAppsecret(appid, appsecret);
//		QQSubViewButton btn11 = new QQSubViewButton("我的博客", "http://blog.csdn.net/jadyer");
//		QQSubViewButton btn22 = new QQSubViewButton("我的GitHub", "https://github.com/jadyer");
//		QQSubClickButton btn33 = new QQSubClickButton("历史上的今天", "123abc");
//		QQSubClickButton btn44 = new QQSubClickButton("天气预报", "456");
//		QQSubClickButton btn55 = new QQSubClickButton("幽默笑话", "joke");
//		QQSuperButton sbtn11 = new QQSuperButton("个人中心", new QQButton[]{btn11, btn22});
//		QQSuperButton sbtn22 = new QQSuperButton("休闲驿站", new QQButton[]{btn33, btn44});
//		QQMenu menu = new QQMenu(new QQButton[]{sbtn11, btn55, sbtn22});
//		return QQHelper.createQQMenu(QQTokenHolder.getQQAccessToken(appid), menu);
//	}
//
//
//	/**
//	 * 拉取粉丝信息
//	 * @see http://127.0.0.1/demo/getQQFansInfo?appid=123&appsecret=123&openid=123
//	 * @create Nov 28, 2015 9:59:01 PM
//	 * @author 玄玉<http://blog.csdn.net/jadyer>
//	 */
//	@ResponseBody
//	@RequestMapping(value="/getQQFansInfo")
//	public QQFansInfo getQQFansInfo(String appid, String appsecret, String openid){
//		QQTokenHolder.setQQAppidAppsecret(appid, appsecret);
//		return QQHelper.getQQFansInfo(QQTokenHolder.getQQAccessToken(appid), openid);
//	}
//
//
//	/**
//	 * 单发主动推消息
//	 * @see 暂不支持
//	 * @see http://127.0.0.1/demo/pushQQMsgToFans?appid=123&appsecret=123&openid=123
//	 * @create Nov 28, 2015 9:54:55 PM
//	 * @author 玄玉<http://blog.csdn.net/jadyer>
//	 */
//	@ResponseBody
//	@RequestMapping(value="/pushQQMsgToFans")
//	public QQErrorInfo pushQQMsgToFans(String appid, String appsecret, String openid){
//		QQTokenHolder.setQQAppid(appid);
//		QQTokenHolder.setQQAppsecret(appsecret);
////		//推图文消息
////		QQCustomNewsMsg.MPNews.Article article11 = new Article("", "", "", "欢迎访问玄玉博客", "玄玉博客是一个开放态度的Java生态圈", "http://avatar.csdn.net/6/0/B/1_jadyer.jpg", "http://blog.csdn.net/jadyer");
////		QQCustomNewsMsg.MPNews.Article article22 = new Article("", "", "", "玄玉微信SDK", "玄玉微信SDK是一个正在研发中的SDK", "http://img.my.csdn.net/uploads/201507/26/1437881866_3678.png", "https://github.com/jadyer");
////		QQCustomNewsMsg customNewsMsg = new QQCustomNewsMsg(openid, new MPNews(new Article[]{article11, article22}));
////		return QQHelper.pushQQMsgToFans(QQTokenHolder.getQQAccessToken(), customNewsMsg);
//		//推文本消息
//		QQCustomTextMsg customTextMsg = new QQCustomTextMsg(openid, new Text("这是一条主动推给单个粉丝的测试消息"));
//		return QQHelper.pushQQMsgToFans(QQTokenHolder.getQQAccessToken(), customTextMsg);
//	}
}