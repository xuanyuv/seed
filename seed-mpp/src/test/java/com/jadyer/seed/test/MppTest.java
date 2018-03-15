package com.jadyer.seed.test;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.XmlUtil;
import com.jadyer.seed.mpp.sdk.qq.helper.QQHelper;
import com.jadyer.seed.mpp.sdk.qq.model.custom.QQCustomTextMsg;
import com.jadyer.seed.mpp.sdk.qq.model.template.QQTemplateMsg;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MppTest {
    @Test
    public void weixinDemo() throws Exception {
        String respXml = "<xml>\n" +
                "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "<return_msg><![CDATA[OK]]></return_msg>\n" +
                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "<err_code><![CDATA[SUCCESS]]></err_code>\n" +
                "<err_code_des><![CDATA[OK]]></err_code_des>\n" +
                "<mch_billno><![CDATA[9010080799701411170000046603]]></mch_billno>\n" +
                "<mch_id><![CDATA[11475856]]></mch_id>\n" +
                "<detail_id><![CDATA[10000417012016080830956240040]]></detail_id>\n" +
                "<status><![CDATA[RECEIVED]]></status>\n" +
                "<send_type><![CDATA[ACTIVITY]]></send_type>\n" +
                "<hb_type><![CDATA[NORMAL]]></hb_type>\n" +
                "<total_num>1</total_num>\n" +
                "<total_amount>100</total_amount>\n" +
                "<send_time><![CDATA[2016-08-08 21:49:22]]></send_time>\n" +
                "<hblist>\n" +
                "<hbinfo>\n" +
                "<openid><![CDATA[oHkLxtzmyHXX6FW_cAWo_orTSRXs]]></openid>\n" +
                "<amount>100</amount>\n" +
                "<rcv_time><![CDATA[2016-08-08 21:49:46]]></rcv_time>\n" +
                "</hbinfo>\n" +
                "<hbinfo>\n" +
                "<openid><![CDATA[oHkLxtzmyHXX6FW_cAWo_orTSRXs___999]]></openid>\n" +
                "<amount>100</amount>\n" +
                "<rcv_time><![CDATA[2016-08-08 21:49:46]]></rcv_time>\n" +
                "</hbinfo>\n" +
                "</hblist>\n" +
                "</xml>";
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(IOUtils.toInputStream(respXml, StandardCharsets.UTF_8));
        //获取所有hbinfo节点的集合，并遍历之
        NodeList hbinfoList = document.getElementsByTagName("hbinfo");
        for(int i=0; i<hbinfoList.getLength(); i++){
            //获取一个hbinfo节点
            Node hbinfo = hbinfoList.item(i);
            //获取hbinfo节点的所有属性集合
            NamedNodeMap attrs = hbinfo.getAttributes();
            //遍历hbinfo节点的属性
            for(int j=0; i<attrs.getLength(); j++){
                //获取hbinfo节点的某一个属性
                Node attr = attrs.item(j);
                System.out.println("属性名==" + attr.getNodeName() + "，属性值==" + attr.getNodeValue());
            }
            //获取hbinfo节点的所有子节点，并遍历之
            NodeList childNodes = hbinfo.getChildNodes();
            for(int k=0; k<childNodes.getLength(); k++){
                //区分出text类型的node以及element类型的node
                if(childNodes.item(k).getNodeType() == Node.ELEMENT_NODE){
                    System.out.println("子节点属性名==" + childNodes.item(k).getNodeName() + "，子节点属性值==" + childNodes.item(k).getTextContent());
                }
            }
        }
        System.out.println(JadyerUtil.buildStringFromMap(respXmlMap));
    }


    /**
     * 模拟粉丝发消息给QQ公众号后，QQ服务器推消息给开发者服务器
     */
    @Test
    public void sendMsgToQQ(){
        String reqURL = "http://127.0.0.1/qq/e9293c3886c411e5bc85000c292d56c5?openId=E12D231CFC30438FB6970B0C7669C101&puin=2878591677";
        String reqData = "<xml><ToUserName><![CDATA[2878591677]]></ToUserName><FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101]]></FromUserName><CreateTime>1448703573</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[你好]]></Content><MsgId>875639142</MsgId></xml>";
        System.out.println("开发者服务器返回消息-->" + HTTPUtil.post(reqURL, reqData, null));
    }


    /**
     * 设置QQ自定义菜单
     */
    @Test
    public void createQQMenu(){
        String accesstoken = "c33c7270918c211d4fd29eb42ed7296c";
        String menu = "{\"button\":[{\"name\":\"个人中心\",\"sub_button\":[{\"name\":\"我的博客\",\"type\":\"view\",\"url\":\"http://jadyer.cn/\"},{\"name\":\"我的GitHub\",\"type\":\"view\",\"url\":\"https://github.com/jadyer\"}]},{\"key\":\"joke\",\"name\":\"幽默笑话\",\"type\":\"click\"},{\"name\":\"休闲驿站\",\"sub_button\":[{\"key\":\"123abc\",\"name\":\"历史上的今天\",\"type\":\"click\"},{\"key\":\"456\",\"name\":\"天气预报\",\"type\":\"click\"}]}]}";
        System.out.println(ReflectionToStringBuilder.toString(QQHelper.createQQMenu(accesstoken, menu), ToStringStyle.MULTI_LINE_STYLE));
    }


    /**
     * 模拟QQ公众号主动推文本消息给粉丝
     */
    @Test
    public void pushQQMsgToFans(){
        String openid = "E12D231CFC30438FB6970B0C7669C101";
        String accesstoken = "bf29c35030adfd6dc3544291ec0826af";
        QQCustomTextMsg customTextMsg = new QQCustomTextMsg(openid, new QQCustomTextMsg.Text("这是一条主动推给单个粉丝的测试消息"));
        QQHelper.pushQQMsgToFans(accesstoken, customTextMsg);
    }


    /**
     * QQ模板消息的json构造
     */
    @Test
    public void pushQQTemplateMsgToFans(){
        QQTemplateMsg.ButtonItem button = new QQTemplateMsg.ButtonItem();
        button.put("url", new QQTemplateMsg.BItem(QQTemplateMsg.TEMPLATE_MSG_TYPE_VIEW, "test", "https://github.com/jadyer/seed/"));
        QQTemplateMsg.DataItem data = new QQTemplateMsg.DataItem();
        data.put("first", new QQTemplateMsg.DItem("天下无敌任我行"));
        data.put("end", new QQTemplateMsg.DItem("随心所欲陪你玩"));
        data.put("keynote1", new QQTemplateMsg.DItem("123"));
        data.put("keynote2", new QQTemplateMsg.DItem("456"));
        data.put("keynote3", new QQTemplateMsg.DItem("789"));
        data.put("keynote4", new QQTemplateMsg.DItem("通路无双"));
        QQTemplateMsg templateMsg = new QQTemplateMsg();
        templateMsg.setTousername("myopenid");
        templateMsg.setTemplateid("mytemplateid");
        templateMsg.setType(QQTemplateMsg.TEMPLATE_MSG_TYPE_VIEW);
        templateMsg.setUrl("http://jadyer.cn/");
        templateMsg.setData(data);
        templateMsg.setButton(button);
        System.out.println(JSON.toJSONString(templateMsg));
    }


    /**
     * 微信模板消息的json构造
     */
    @Test
    public void pushWeixinTemplateMsgToFans(){
        WeixinTemplateMsg.DataItem data = new WeixinTemplateMsg.DataItem();
        data.put("first", new WeixinTemplateMsg.DItem("恭喜你购买成功！", "#173177"));
        data.put("remark", new WeixinTemplateMsg.DItem("欢迎再次购买！", "#173177"));
        data.put("keynote1", new WeixinTemplateMsg.DItem("巧克力"));
        data.put("keynote2", new WeixinTemplateMsg.DItem("39.8元"));
        data.put("keynote3", new WeixinTemplateMsg.DItem("2014年9月22日"));
        WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
        templateMsg.setTouser("myopenid");
        templateMsg.setTemplate_id("ZW-_PSe7k0SrVBjS4oqwuWcg6Yv7FjU0igYmy4ZYj4U");
        templateMsg.setUrl("http://jadyer.cn/");
        templateMsg.setData(data);
        System.out.println(JSON.toJSONString(templateMsg));
    }


    /**
     * 推送支付通知给微信
     */
    @Test
    public void pushPayNotifyToWeixin(){
        HTTPUtil.post("http://127.0.0.1/weixin/helper/pay/notify", "<xml><name>鬼谷子</name><aa><bb>老子</bb></aa></xml>", null);
    }
}