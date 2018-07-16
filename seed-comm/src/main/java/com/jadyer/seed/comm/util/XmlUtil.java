package com.jadyer.seed.comm.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * XML工具类
 * ------------------------------------------------------------------------------------------------
 * @version v1.0
 * @history v1.0-->新建此类并添加了四个方法：xml和map互转、美化xml、转义xml
 * ------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/8 16:44.
 */
public final class XmlUtil {
    private XmlUtil(){}

    /**
     * XML转Map
     * <p>
     *     示例字符串：<xml><name>鬼谷子</name></xml>，则转换后的Map只有一个key=name，value=鬼谷子
     *     示例字符串：<xml><name>鬼谷子</name><aa><bb>老子</bb></aa></xml>，则得到Map有两个key：name和aa，值分别为鬼谷子和老子
     * </p>
     * <p>
     *     微信支付相关的XXE漏洞：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=23_5
     * </p>
     */
    public static Map<String, String> xmlToMap(String xmlStr){
        Map<String, String> dataMap = new HashMap<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(IOUtils.toInputStream(xmlStr, StandardCharsets.UTF_8));
            //获得文档的根元素节点
            Element rootElement = document.getDocumentElement();
            //解析XML时要注意：节点与节点之间的空格文本，也算是根元素节点的孩子的组成部分
            NodeList childNodeList = rootElement.getChildNodes();
            for(int i=0; i<childNodeList.getLength(); i++){
                Node node = childNodeList.item(i);
                if(node instanceof Element){
                    dataMap.put(node.getNodeName(), node.getTextContent());
                }
            }
            /*
            <xml>
                <return_code><![CDATA[SUCCESS]]></return_code>
                <hblist>
                    <hbinfo>
                        <openid><![CDATA[oHkLxtzmyHXX6FW_cAWo_orTSRXs]]></openid>
                        <amount>100</amount>
                        <rcv_time><![CDATA[2016-08-08 21:49:46]]></rcv_time>
                    </hbinfo>
                    <hbinfo>
                        <openid><![CDATA[oHkLxtzmyHXX6FW_cAWo_abCDEFg]]></openid>
                        <amount>200</amount>
                        <rcv_time><![CDATA[2017-07-28 14:30:00]]></rcv_time>
                    </hbinfo>
                </hblist>
            </xml>
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
            */
        }catch(Exception e){
            LogUtil.getLogger().error("xml字符串转Map时发生异常，堆栈轨迹如下", e);
        }
        return dataMap;
    }


    /**
     * Map转XML
     * <p>
     *     该方法会将Map中非空值的键值对转为xml字符串，转换后的示例如下
     *     <xml>
     *         <name>鬼谷子</name>
     *         <address>鬼谷</address>
     *         <teacher>老子</teacher>
     *     </xml>
     *     注：目前只会转到一级的xml，不会转成两级的
     * </p>
     */
    public static String mapToXml(Map<String, String> dataMap){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for(Map.Entry<String,String> entry : dataMap.entrySet()){
            if(StringUtils.isNotEmpty(entry.getValue())){
                sb.append("<").append(entry.getKey()).append(">").append(entry.getValue()).append("</").append(entry.getKey()).append(">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }


    /**
     * 转义XML字符串
     * @return String 过滤后的字符串
     */
    public static String escapeXml(String input) {
        if(StringUtils.isBlank(input)){
            return "";
        }
        input = input.replaceAll("&", "&amp;");
        input = input.replaceAll("<", "&lt;");
        input = input.replaceAll(">", "&gt;");
        input = input.replaceAll("\"", "&quot;");
        input = input.replaceAll("'", "&apos;");
        return input;
    }


    /**
     * 格式化XML格式的字符串
     * <p>
     *     格式化失败时返回的Map中，isPrettySuccess=no，prettyResultStr=堆栈信息
     *     格式化成功时返回的Map中，isPrettySuccess=yes，prettyResultStr=格式化后的字符串
     * </p>
     * @param xmlString 待格式化的XML字符串
     * @return 返回的Map中有两个字符串的key-value，分别为isPrettySuccess和prettyResultStr
     */
    public static Map<String, String> formatXMLString(String xmlString) {
        Map<String, String> resultMap = new HashMap<>();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        StringWriter writer = new StringWriter();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new StreamSource(new StringReader(xmlString)), new StreamResult(writer));
        } catch (TransformerException e) {
            resultMap.put("isPrettySuccess", "no");
            resultMap.put("prettyResultStr", ExceptionUtils.getStackTrace(e));
            return resultMap;
        }
        resultMap.put("isPrettySuccess", "yes");
        resultMap.put("prettyResultStr", writer.toString());
        return resultMap;
    }
}