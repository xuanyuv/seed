package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLinkMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLocationMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInTextMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutCustomServiceMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;

/**
 * 用于将消息转发到多客服的Adapter
 * @see 对WeixinMsgController部分方法提供默认实现,以便开发者可以只关注需要处理的抽象方法
 * @create Oct 19, 2015 10:56:56 AM
 * @author 玄玉<https://jadyer.github.io/>
 */
public abstract class WeixinMsgControllerCustomServiceAdapter extends WeixinMsgControllerAdapter {
	@Override
	protected abstract WeixinOutMsg processInMenuEventMsg(WeixinInMenuEventMsg inMenuEventMsg);

	/**
	 * 处理收到的文本消息
	 * @see 默认将消息转发到多客服
	 */
	@Override
	protected WeixinOutMsg processInTextMsg(WeixinInTextMsg inTextMsg) {
		return new WeixinOutCustomServiceMsg(inTextMsg);
	}

	/**
	 * 处理收到的图片消息
	 * @see 默认将消息转发到多客服
	 */
	@Override
	protected WeixinOutMsg processInImageMsg(WeixinInImageMsg inImageMsg) {
		return new WeixinOutCustomServiceMsg(inImageMsg);
	}

	/**
	 * 处理收到的地址位置消息
	 * @see 默认将消息转发到多客服
	 */
	@Override
	protected WeixinOutMsg processInLocationMsg(WeixinInLocationMsg inLocationMsg) {
		return new WeixinOutCustomServiceMsg(inLocationMsg);
	}

	/**
	 * 处理收到的链接消息
	 * @see 默认将消息转发到多客服
	 */
	@Override
	protected WeixinOutMsg processInLinkMsg(WeixinInLinkMsg inLinkMsg) {
		return new WeixinOutCustomServiceMsg(inLinkMsg);
	}
}