package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.RequestUtil;
import com.jadyer.seed.comm.util.XmlUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinOAuthAccessToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 接收微信服务器回调以及其它的辅助功能
 * Created by 玄玉<https://jadyer.cn/> on 2015/10/19 20:30.
 */
@Controller
@RequestMapping(value="/weixin/helper")
public class WeixinHelperController {
    /**
     * 获取网页access_token
     * @param appid 微信appid,通过它来支持多用户
     * @param code  微信服务器发放的,有效期为5分钟的,用于换取网页access_token的code
     * @param state 重定向到微信服务器时,由开发者服务器携带过去的参数,这里会原样带回
     * @return 获取失败则返回一个友好的HTML页面,获取成功后直接跳转到用户原本请求的资源
     */
    @RequestMapping(value="/oauth/{appid}")
    public String oauth(@PathVariable String appid, String code, String state, HttpServletResponse response) throws IOException{
        LogUtil.getLogger().info("收到微信服务器回调code=[{}], state=[{}]", code, state);
        if(StringUtils.isNotBlank(code)){
            WeixinOAuthAccessToken oauthAccessToken = WeixinTokenHolder.getWeixinOAuthAccessToken(appid, code);
            if(0==oauthAccessToken.getErrcode() && StringUtils.isNotBlank(oauthAccessToken.getOpenid())){
                /*
                 * 还原state携带过来的粉丝请求的原URL
                 * @see state=http://www.jadyer.com/mpp/weixin/getOpenid/openid=openid/test=7645
                 */
                //1.获取到URL中的非参数部分
                String uri = state.substring(0, state.indexOf("="));
                uri = uri.substring(0, uri.lastIndexOf("/"));
                //2.获取到URL中的参数部分(得到openid的方式为截取掉占位的,再追加真正的值)
                String params = state.substring(uri.length()+1);
                params = params.replaceAll("/", "&").replace("openid=openid", "openid="+oauthAccessToken.getOpenid());
                //3.拼接粉丝请求的原URL并跳转过去
                String fullURI = uri + "?" + params;
                LogUtil.getLogger().info("还原粉丝请求的资源得到state=[{}]", fullURI);
                response.sendRedirect(fullURI);
            }
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType("text/plain; charset=" + StandardCharsets.UTF_8);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();
        out.print("系统繁忙Unauthorized\r\n请联系您关注的微信公众号");
        out.flush();
        out.close();
        return null;
    }


    /**
     * JS-SDK权限验证的签名
     * http://mp.weixin.qq.com/wiki/7/aaa137b55fb2e0456bf8dd9148dd613f.html
     * 注意：这里使用的是noncestr，非nonceStr
     * @param url 当前网页的URL，不包含#及其后面部分
     * Created by 玄玉<https://jadyer.cn/> on 2015/10/29 22:11.
     */
    @ResponseBody
    @RequestMapping(value="/jssdk/sign")
    public Map<String, String> jssdkSign(String appid, String url) throws UnsupportedEncodingException{
        url = URLDecoder.decode(url, "UTF-8");
        String noncestr = RandomStringUtils.randomNumeric(16);
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        StringBuilder sb = new StringBuilder();
        sb.append("jsapi_ticket=").append(WeixinTokenHolder.getWeixinJSApiTicket(appid)).append("&")
                .append("noncestr=").append(noncestr).append("&")
                .append("timestamp=").append(timestamp).append("&")
                .append("url=").append(url);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("appid", appid);
        resultMap.put("timestamp", String.valueOf(timestamp));
        resultMap.put("noncestr", noncestr);
        resultMap.put("signature", DigestUtils.sha1Hex(sb.toString()));
        return resultMap;
    }


    /**
     * 下载微信临时媒体文件
     * @param mediaId 媒体文件ID
     */
    @RequestMapping(value="/tempMediaFile/get/{appid}/{mediaId}")
    public void tempMediaFileGet(@PathVariable String appid, @PathVariable String mediaId, HttpServletResponse response) throws Exception {
        String fullPath = WeixinHelper.downloadWeixinTempMediaFile(WeixinTokenHolder.getWeixinAccessToken(appid), mediaId);
        WeixinTokenHolder.setMediaIdFilePath(appid, mediaId, fullPath);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + new String(("get_"+FilenameUtils.getName(fullPath)).getBytes("UTF-8"), "ISO8859-1"));
        InputStream is = FileUtils.openInputStream(new File(fullPath));
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        byte[] buff = new byte[1024];
        int len;
        while((len=is.read(buff)) > -1){
            os.write(buff, 0, len);
        }
        os.flush();
        is.close();
        os.close();
    }


    /**
     * 删除存储在本地的微信临时媒体文件
     * @param fileFullPath 存储在本地的微信临时媒体文件的完整路径
     */
    @ResponseBody
    @RequestMapping(value="/tempMediaFile/delete/{appid}/{mediaId}")
    public boolean tempMediaFileDelete(@PathVariable String appid, @PathVariable String mediaId){
        String localFileFullPath = WeixinTokenHolder.getMediaIdFilePath(appid, mediaId);
        try {
            return new File(localFileFullPath).delete();
        } catch (Exception e) {
            LogUtil.getLogger().info("删除存储在本地的微信临时媒体文件mediaId=["+mediaId+"],fullPath=["+localFileFullPath+"]失败,堆栈轨迹如下", e);
            return false;
        }
    }


    /**
     * 获取微信二维码图片URL
     * @param type          二维码类型,0--临时二维码,1--永久二维码,2--永久字符串二维码
     * @param expireSeconds 二维码临时有效的时间,单位为秒,最大不超过2592000s,即30天,不填则默认有效期为30s
     * @param sceneId       二维码参数场景值ID,临时二维码时为32位非0整型,永久二维码时值为1--100000
     * @param sceneStr      二维码参数场景值ID,字符串形式的ID,字符串类型,长度限制为1到64,仅永久二维码支持此字段
     */
    @RequestMapping(value="/getQrcodeURL")
    public void getQrcodeURL(String appid, int type, String expireSeconds, String sceneId, String sceneStr, HttpServletResponse response) throws IOException{
        if(StringUtils.isBlank(expireSeconds)){
            expireSeconds = "2";
        }
        if(StringUtils.isBlank(sceneId)){
            sceneId = "2";
        }
        String ticket = WeixinHelper.createQrcodeTicket(WeixinTokenHolder.getWeixinAccessToken(appid), type, Integer.parseInt(expireSeconds), Long.parseLong(sceneId), sceneStr);
        String qrcodeURL = WeixinConstants.URL_WEIXIN_GET_QRCODE.replace(WeixinConstants.URL_PLACEHOLDER_QRCODE_TICKET, ticket);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType("text/plain; charset=" + StandardCharsets.UTF_8);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();
        out.print(qrcodeURL);
        out.flush();
        out.close();
    }


    /**
     * 微信支付--公众号支付--支付结果通知
     * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
     * <ul>
     *     <li>让这个方法不是public的，这样日志切面就不会读一次入参，否则会导致这里再读的时候独到的是[]</li>
     *     <li>
     *         这是读取到的入参示例（xml格式的字符串）
     *         <xml><appid><![CDATA[wxb4c63222cebf7762]]></appid>
     *         <attach><![CDATA[15]]></attach>
     *         <bank_type><![CDATA[CFT]]></bank_type>
     *         <cash_fee><![CDATA[1]]></cash_fee>
     *         <device_info><![CDATA[WEB]]></device_info>
     *         <fee_type><![CDATA[CNY]]></fee_type>
     *         <is_subscribe><![CDATA[Y]]></is_subscribe>
     *         <mch_id><![CDATA[1486165412]]></mch_id>
     *         <nonce_str><![CDATA[625776929102925810219305306908]]></nonce_str>
     *         <openid><![CDATA[ojZ6h1U3w-d-ueEdPv-UfttvdBcU]]></openid>
     *         <out_trade_no><![CDATA[201707240942476418778105452615]]></out_trade_no>
     *         <result_code><![CDATA[SUCCESS]]></result_code>
     *         <return_code><![CDATA[SUCCESS]]></return_code>
     *         <sign><![CDATA[8030A703149A82D8D1DD43A0D90B0D2C]]></sign>
     *         <time_end><![CDATA[20170724094315]]></time_end>
     *         <total_fee>1</total_fee>
     *         <trade_type><![CDATA[JSAPI]]></trade_type>
     *         <transaction_id><![CDATA[4000082001201707242363008429]]></transaction_id>
     *         </xml>
     *     </li>
     * </ul>
     */
    @RequestMapping("/pay/notify")
    void payNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reqData = RequestUtil.extractHttpServletRequestBodyMessage(request);
        LogUtil.getLogger().info("微信支付--公众号支付--支付结果通知-->收到报文-->[{}]", reqData);
        Map<String, String> dataMap = XmlUtil.xmlToMap(reqData);
        //验证交易是否成功、验签
        WeixinHelper.payVerifyIfSuccess(dataMap);
        WeixinHelper.payVerifySign(dataMap, dataMap.get("appid"));
        //校验金额
        if(!StringUtils.equals("数据库查到的商户订单金额", dataMap.get("total_fee"))){
            throw new IllegalArgumentException("微信公众号支付后台通知金额与商户订单金额不符");
        }
        //处理通知数据
        String appid = dataMap.get("appid");
        String transaction_id = dataMap.get("transaction_id");
        String time_end = dataMap.get("time_end");
        //应答成功结果
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType("text/plain; charset=" + StandardCharsets.UTF_8);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();
        LogUtil.getLogger().info("微信支付--公众号支付--支付结果通知-->应答内容为：表示成功的XML");
        out.print("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
        out.flush();
        out.close();
    }
}