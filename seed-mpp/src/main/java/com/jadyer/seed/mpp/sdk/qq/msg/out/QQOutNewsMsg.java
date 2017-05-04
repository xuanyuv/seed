package com.jadyer.seed.mpp.sdk.qq.msg.out;

import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 回复图文消息
 * @see 回复单图文时添加一个QQOutNewsMsg.QQNews即可,多图文就添加做个
 * @create Oct 18, 2015 6:04:05 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class QQOutNewsMsg extends QQOutMsg {
    private List<QQNews> articles = new ArrayList<QQNews>();

    public QQOutNewsMsg(QQInMsg inMsg) {
        super(inMsg);
        this.msgType = "news";
    }

    public int getArticleCount() {
        return articles.size();
    }

    public List<QQNews> getArticles() {
        return articles;
    }

    public QQOutNewsMsg addNews(String title, String description, String picUrl, String url) {
        this.articles.add(new QQNews(title, description, picUrl, url));
        return this;
    }

    /**
     * 图文消息
     * @create Oct 18, 2015 5:57:18 PM
     * @author 玄玉<http://jadyer.cn/>
     */
    public static class QQNews {
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

        public QQNews(String title, String description, String picUrl, String url) {
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