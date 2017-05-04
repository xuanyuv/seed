package com.jadyer.seed.mpp.sdk.qq.model.custom;

/**
 * 单发图文消息
 * @create Nov 28, 2015 9:40:51 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class QQCustomNewsMsg extends QQCustomMsg {
    /**
     * 消息类型(文本为text,图片为image,语音为voice,视频消息为video,音乐消息为music,图文消息为mpnews)
     */
    private String msgtype;

    /**
     * 封装图文消息内容的对象
     */
    private MPNews mpnews;

    public QQCustomNewsMsg(String tousername, MPNews mpnews) {
        super(tousername);
        this.mpnews = mpnews;
        this.msgtype = "mpnews";
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public MPNews getMpnews() {
        return mpnews;
    }

    public void setMpnews(MPNews mpnews) {
        this.mpnews = mpnews;
    }

    public static class MPNews{
        private Article[] articles;
        public MPNews(Article[] articles) {
            this.articles = articles;
        }
        public Article[] getArticles() {
            return articles;
        }
        public static class Article{
            /**
             * 标题
             */
            private String title;

            /**
             * 图文消息的封面图片素材id(必须是永久mediaID)
             */
            private String thumb_media_id;

            /**
             * 作者
             */
            private String author;

            /**
             * 图文消息的摘要,仅有单图文消息才有摘要,多图文此处为空
             */
            private String digest;

            /**
             * 是否显示封面,0为false即不显示,1为true即显示
             */
            private String show_cover_pic;

            /**
             * 图文消息的具体内容,支持HTML标签,必须少于2万字符,小于1M,且此处会去除JS
             */
            private String content;

            /**
             * 图文消息的原文地址,即点击"阅读原文"后的URL
             */
            private String content_source_url;

            public Article(String title, String thumb_media_id, String author, String digest, String show_cover_pic, String content, String content_source_url) {
                this.title = title;
                this.thumb_media_id = thumb_media_id;
                this.author = author;
                this.digest = digest;
                this.show_cover_pic = show_cover_pic;
                this.content = content;
                this.content_source_url = content_source_url;
            }
            public String getTitle() {
                return title;
            }
            public void setTitle(String title) {
                this.title = title;
            }
            public String getThumb_media_id() {
                return thumb_media_id;
            }
            public void setThumb_media_id(String thumb_media_id) {
                this.thumb_media_id = thumb_media_id;
            }
            public String getAuthor() {
                return author;
            }
            public void setAuthor(String author) {
                this.author = author;
            }
            public String getDigest() {
                return digest;
            }
            public void setDigest(String digest) {
                this.digest = digest;
            }
            public String getShow_cover_pic() {
                return show_cover_pic;
            }
            public void setShow_cover_pic(String show_cover_pic) {
                this.show_cover_pic = show_cover_pic;
            }
            public String getContent() {
                return content;
            }
            public void setContent(String content) {
                this.content = content;
            }
            public String getContent_source_url() {
                return content_source_url;
            }
            public void setContent_source_url(String content_source_url) {
                this.content_source_url = content_source_url;
            }
        }
    }
}