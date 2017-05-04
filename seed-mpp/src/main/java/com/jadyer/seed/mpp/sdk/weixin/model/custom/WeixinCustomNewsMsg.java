package com.jadyer.seed.mpp.sdk.weixin.model.custom;

/**
 * 客服接口发送图文消息
 * @create Oct 18, 2015 10:46:59 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class WeixinCustomNewsMsg extends WeixinCustomMsg {
    /**
     * 消息类型(文本为text,图片为image,语音为voice,视频消息为video,音乐消息为music,图文消息为news,卡券为wxcard)
     */
    private String msgtype;

    /**
     * 封装图文消息内容的对象
     */
    private News news;

    public WeixinCustomNewsMsg(String touser, News news) {
        super(touser);
        this.news = news;
        this.msgtype = "news";
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public static class News{
        private Article[] articles;
        public News(Article[] articles) {
            this.articles = articles;
        }
        public Article[] getArticles() {
            return articles;
        }
        public static class Article{
            /**
             * 图文消息标题(不是必须)
             */
            private String title;

            /**
             * 图文消息描述(不是必须)
             */
            private String description;

            /**
             * 图文消息的图片链接(不是必须,支持JPG/PNG格式,较好的效果为大图640*320,小图80*80)
             */
            private String picurl;

            /**
             * 图文消息被点击后跳转的链接(不是必须)
             */
            private String url;

            public Article(String title, String description, String picurl, String url) {
                this.title = title;
                this.description = description;
                this.picurl = picurl;
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
            public String getPicurl() {
                return picurl;
            }
            public void setPicurl(String picurl) {
                this.picurl = picurl;
            }
            public String getUrl() {
                return url;
            }
            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}