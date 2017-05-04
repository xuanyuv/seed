package com.jadyer.seed.mpp.sdk.qq.model.template;

import java.util.HashMap;

/**
 * QQ模板消息对象
 * @create Dec 30, 2015 11:03:44 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class QQTemplateMsg {
    public static final String TEMPLATE_MSG_TYPE_VIEW = "view";
    public static final String TEMPLATE_MSG_TYPE_CLICK = "click";

    /**
     * 根据OpenID发送消息,使用接口send
     */
    private String tousername;

    /**
     * 指定消息模板的id(模板通过审核之后可获得)
     */
    private String templateid;

    /**
     * click/view(点击事件或者跳转)
     * @see 非必传
     */
    private String type;

    /**
     * 点击事件上报的关键字
     * @see 非必传
     */
    private String key;

    /**
     * 点击事件跳转的url
     * @see 非必传
     */
    private String url;

//    private String topcolor;

    /**
     * 指定传入消息模板的模板参数内容,每个模板参数包含两部分内容,即变量名:{变量值}
     * @see 用户在data参数中列出的变量名仅用于调用方辨识方便,接口parse数据时不做单独处理
     * @see 识别变量仅依序从前至后,严格按照参数前后顺序将内容填充拼接至模板内
     */
    private DataItem data;

    /**
     * 指定传入消息模板的模板参数内容,每个模板参数包含两部分内容,即变量名:{按钮类型  按钮名称  按钮数据}
     * @see 用户在button参数中列出的变量名仅用于调用方辨识方便,接口parse数据时不做单独处理
     * @see 识别变量仅依序从前至后,严格按照参数前后顺序将内容填充拼接至模板内
     * @see 默认最后一个按钮为主推按钮,只支持2-3个按钮,与1个按钮的POST数据格式不同
     * @see 非必传
     */
    private ButtonItem button;

    public String getTousername() {
        return tousername;
    }

    public void setTousername(String tousername) {
        this.tousername = tousername;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public String getTopcolor() {
//        return topcolor;
//    }
//
//    public void setTopcolor(String topcolor) {
//        this.topcolor = topcolor;
//    }

    public DataItem getData() {
        return data;
    }

    public void setData(DataItem data) {
        this.data = data;
    }

    public ButtonItem getButton() {
        return button;
    }

    public void setButton(ButtonItem button) {
        this.button = button;
    }

    public static class DataItem extends HashMap<String, DItem> {
        private static final long serialVersionUID = -7207266187674584823L;
        public DataItem() {}
        public DataItem(String key, DItem item) {
            this.put(key, item);
        }
    }

    public static class DItem {
        private String value;
//        private String color;
//        public DItem(String value, String color) {
//            this.value = value;
//            this.color = color;
//        }
        public DItem(String value) {
            this.value = value;
        }
//        public String getColor() {
//            return color;
//        }
//        public void setColor(String color) {
//            this.color = color;
//        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ButtonItem extends HashMap<String, BItem> {
        private static final long serialVersionUID = -7207266187674584823L;
        public ButtonItem() {}
        public ButtonItem(String key, BItem item) {
            this.put(key, item);
        }
    }

    public static class BItem {
        private String type;
        private String name;
        private String value;
        public BItem(String type, String name, String value) {
            this.type = type;
            this.name = name;
            this.value = value;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }
}