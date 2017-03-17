package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 回复图文消息
 * @see 回复单图文时添加一个WeixinOutNewsMsg.WeixinNews即可,多图文就添加做个
 * @create Oct 18, 2015 6:04:05 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinOutNewsMsg extends WeixinOutMsg {
	private List<WeixinNews> articles = new ArrayList<WeixinNews>();

	public WeixinOutNewsMsg(WeixinInMsg inMsg) {
		super(inMsg);
		this.msgType = "news";
	}

	public int getArticleCount() {
		return articles.size();
	}

	public List<WeixinNews> getArticles() {
		return articles;
	}

	public WeixinOutNewsMsg addNews(String title, String description, String picUrl, String url) {
		this.articles.add(new WeixinNews(title, description, picUrl, url));
		return this;
	}

	/**
	 * 图文消息
	 * @create Oct 18, 2015 5:57:18 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static class WeixinNews {
		/**
		 * 图文消息标题(不是必须)
		 */
		private String title;

		/**
		 * 图文消息描述(不是必须)
		 */
		private String description;

		/**
		 * 图片链接(不是必须,支持JPG/PNG格式,较好的效果为大图360*200,小图200*200)
		 */
		private String picUrl;

		/**
		 * 点击图文消息跳转链接(不是必须)
		 */
		private String url;

		public WeixinNews(String title, String description, String picUrl, String url) {
			this.title = title;
			this.description = description;
			this.picUrl = picUrl;
			this.url = url;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getPicUrl() {
			return picUrl;
		}

		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}