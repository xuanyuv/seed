package com.jadyer.seed.mpp.sdk.weixin.model.template;

import java.util.HashMap;

/**
 * 微信模板消息对象
 * <p>http://mp.weixin.qq.com/wiki/5/6dde9eaa909f83354e0094dc3ad99e05.html#.E5.8F.91.E9.80.81.E6.A8.A1.E6.9D.BF.E6.B6.88.E6.81.AF</p>
 * Created by 玄玉<https://jadyer.cn/> on 2016/5/18 17:35.
 */
public class WeixinTemplateMsg {
    private String touser;
    private String template_id;
    private String url;
    /**
     * 201605241640使用微信接口測試號mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login发現此屬性不支持
     */
    //private String topcolor;
    private DataItem data;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DataItem getData() {
        return data;
    }

    public void setData(DataItem data) {
        this.data = data;
    }

    public static class DataItem extends HashMap<String, DItem> {
        private static final long serialVersionUID = -5767034886837670403L;
        public DataItem() {}
        public DataItem(String key, DItem item) {
            this.put(key, item);
        }
    }

    public static class DItem {
        private String value;
        private String color;
        public DItem(String value) {
            this.value = value;
        }
        public DItem(String value, String color) {
            this.value = value;
            this.color = color;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}
