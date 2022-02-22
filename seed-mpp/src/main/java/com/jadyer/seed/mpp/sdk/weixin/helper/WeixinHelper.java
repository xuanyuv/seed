package com.jadyer.seed.mpp.sdk.weixin.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.CodecUtil;
import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.XmlUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinCodeEnum;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinPayCodeEnum;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinErrorInfo;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinFansInfo;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinOAuthAccessToken;
import com.jadyer.seed.mpp.sdk.weixin.model.custom.WeixinCustomMsg;
import com.jadyer.seed.mpp.sdk.weixin.model.menu.WeixinMenu;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayCloseorderReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayCloseorderRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayDownloadbillReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayDownloadbillRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayOrderqueryReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayOrderqueryRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundqueryReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundqueryRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayUnifiedorderReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayUnifiedorderRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackGethbinfoReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackGethbinfoRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackSendReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackSendRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplate;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WeixinHelper {
    private WeixinHelper(){}

    /**
     * 获取微信的access_token
     * @see 默认修饰符default即只有同包中的类才可使用
     * @see {"access_token":"8DF72J-d_u3XIaq22e_HUY_fe5wfdoj6awnq2wDrk5v05zf1yEuUhUdtfX7yqB5wAJ1edwGrgAyJvinZTXl2RamjsqDOIg4L1humLuj32Oo","expires_in":7200}
     * @see {"errcode":40125,"errmsg":"invalid appsecret, view more at http:\/\/t.cn\/RAEkdVq hint: [M5_jKa0125vr22]"}
     * @return 获取失败时将抛出Exception
     */
    static String getWeixinAccessToken(String appid, String appsecret) throws IllegalAccessException {
        String reqURL = WeixinConstants.URL_WEIXIN_GET_ACCESSTOKEN.replace(WeixinConstants.URL_PLACEHOLDER_APPID, appid).replace(WeixinConstants.URL_PLACEHOLDER_APPSECRET, appsecret);
        String respData = HTTPUtil.post(reqURL, null, null);
        LogUtil.getLogger().info("获取微信access_token,微信应答报文为-->{}", respData);
        Map<String, String> map = JSON.parseObject(respData, new TypeReference<Map<String, String>>(){});
        if(respData.contains("access_token")){
            return map.get("access_token");
        }
        String errmsg = WeixinCodeEnum.getMsgByCode(Integer.parseInt((map.get("errcode"))));
        if(StringUtils.isBlank(errmsg)){
            errmsg = map.get("errmsg");
        }
        throw new IllegalAccessException(errmsg);
    }


    /**
     * 获取微信jsapi_ticket
     * @see http://mp.weixin.qq.com/wiki/7/aaa137b55fb2e0456bf8dd9148dd613f.html
     * @see {"errcode":40001,"errmsg":"invalid credential, access_token is invalid or not latest hint: [3sEnya0653vr23]"}
     * @see {"errcode":0,"errmsg":"ok","ticket":"sM4AOVdWfPE4DxkXGEs8VDqmMJ5Cg8sos8UXyJqPG4FpcrJtLcmFoV69dhqNmiQdoF1HjamNrYH9c8S9r4B_MA","expires_in":7200}
     * @return 获取失败时将抛出Exception
     * Created by 玄玉<https://jadyer.cn/> on 2015/10/29 21:45.
     */
    static String getWeixinJSApiTicket(String accesstoken) throws IllegalAccessException {
        String reqURL = WeixinConstants.URL_WEIXIN_GET_JSAPI_TICKET.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String respData = HTTPUtil.post(reqURL, null, null);
        LogUtil.getLogger().info("获取微信jsapi_ticket,微信应答报文为-->{}", respData);
        Map<String, String> map = JSON.parseObject(respData, new TypeReference<Map<String, String>>(){});
        if("0".equals(map.get("errcode"))){
            return map.get("ticket");
        }
        String errmsg = WeixinCodeEnum.getMsgByCode(Integer.parseInt((map.get("errcode"))));
        if(StringUtils.isBlank(errmsg)){
            errmsg = map.get("errmsg");
        }
        throw new IllegalAccessException(errmsg);
    }


    /**
     * 通过code换取微信网页授权access_token
     * @param appid     微信公众号AppID
     * @param appsecret 微信公众号AppSecret
     * @param code      换取access_token的有效期为5分钟的票据
     * @return 返回获取到的网页access_token（获取失败时的应答码也在该返回中）
     */
    static WeixinOAuthAccessToken getWeixinOAuthAccessToken(String appid, String appsecret, String code){
        String reqURL = WeixinConstants.URL_WEIXIN_OAUTH2_GET_ACCESSTOKEN.replace(WeixinConstants.URL_PLACEHOLDER_APPID, appid)
                                                                      .replace(WeixinConstants.URL_PLACEHOLDER_APPSECRET, appsecret)
                                                                      .replace(WeixinConstants.URL_PLACEHOLDER_CODE, code);
        String respData = HTTPUtil.post(reqURL, null, null);
        LogUtil.getLogger().info("获取微信网页access_token，微信应答报文为-->{}", respData);
        WeixinOAuthAccessToken weixinOauthAccessToken = JSON.parseObject(respData, WeixinOAuthAccessToken.class);
        if(weixinOauthAccessToken.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMsgByCode(weixinOauthAccessToken.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                weixinOauthAccessToken.setErrmsg(errmsg);
            }
        }
        return weixinOauthAccessToken;
    }


    /**
     * 构建网页授权获取用户信息的获取Code地址
     * @param appid       微信公众号AppID
     * @param scope       应用授权作用域(snsapi_base或snsapi_userinfo)
     * @param state       重定向后会带上state参数(开发者可以填写a-zA-Z0-9的参数值,最多128字节)
     * @param redirectURI 授权后重定向的回调链接地址(请使用urlencode对链接进行处理)
     */
    public static String buildWeixinOAuthCodeURL(String appid, String scope, String state, String redirectURI){
        try {
            return WeixinConstants.URL_WEIXIN_OAUTH2_GET_CODE.replace(WeixinConstants.URL_PLACEHOLDER_APPID, appid)
                                                          .replace(WeixinConstants.URL_PLACEHOLDER_SCOPE, scope)
                                                          .replace(WeixinConstants.URL_PLACEHOLDER_STATE, state)
                                                          .replace(WeixinConstants.URL_PLACEHOLDER_REDIRECT_URI, URLEncoder.encode(redirectURI, StandardCharsets.UTF_8.displayName()));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    /**
     * 创建自定义菜单
     * @see -----------------------------------------------------------------------------------------------------------
     * @see 1.自定义菜单最多包括3个一级菜单,每个一级菜单最多包含5个二级菜单
     * @see 2.一级菜单最多4个汉字,二级菜单最多7个汉字,多出来的部分将会以"..."代替
     * @see 3.由于微信客户端缓存,创建的菜单需24小时微信客户端才会展现,测试时可尝试取消关注公众账号后再次关注后查看效果
     * @see 4.修改菜单时(修改内容或菜单数量等)不需要删除菜单,直接调用创建接口即可,微信会自动覆盖以前创建的菜单
     * @see -----------------------------------------------------------------------------------------------------------
     */
    public static WeixinErrorInfo createWeixinMenu(String accesstoken, WeixinMenu menu){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_CREATE_MENU.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String reqData = JSON.toJSONString(menu);
        LogUtil.getLogger().info("自定义菜单创建-->待发送的JSON为{}", reqData);
        String respData = HTTPUtil.post(reqURL, reqData, null);
        LogUtil.getLogger().info("自定义菜单创建-->微信应答JSON为{}", respData);
        WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
        if(errinfo.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMsgByCode(errinfo.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                errinfo.setErrmsg(errmsg);
            }
        }
        return errinfo;
    }


    /**
     * 创建自定义菜单
     * @see String menuJson = "{\"button\":[{\"type\":\"view\", \"name\":\"我的博客\", \"url\":\"https://jadyer.cn/\"}, {\"type\":\"click\", \"name\":\"今日歌曲\", \"key\":\"V1001_TODAY_MUSIC\"}, {\"name\":\"个人中心\", \"sub_button\": [{\"type\":\"view\", \"name\":\"搜索\", \"url\":\"http://www.soso.com/\"}, {\"type\":\"view\", \"name\":\"视频\", \"url\":\"http://v.qq.com/\"}, {\"type\":\"click\", \"name\":\"赞一下我们\", \"key\":\"V1001_GOOD\"}]}]}";
     * @see String menuJson = "{\"button\":[{\"type\":\"view\", \"name\":\"我的博客\", \"url\":\"https://jadyer.cn/\"}, {\"name\":\"个人中心\", \"sub_button\": [{\"type\":\"view\", \"name\":\"搜索\", \"url\":\"http://www.soso.com/\"}, {\"type\":\"view\", \"name\":\"视频\", \"url\":\"http://v.qq.com/\"}, {\"type\":\"click\", \"name\":\"赞一下我们\", \"key\":\"V1001_GOOD\"}]}]}";
     */
    public static WeixinErrorInfo createWeixinMenu(String accesstoken, String menuJson){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_CREATE_MENU.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        LogUtil.getLogger().info("自定义菜单创建-->待发送的JSON为{}", menuJson);
        String respData = HTTPUtil.post(reqURL, menuJson, null);
        LogUtil.getLogger().info("自定义菜单创建-->微信应答JSON为{}", respData);
        WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
        if(errinfo.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMsgByCode(errinfo.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                errinfo.setErrmsg(errmsg);
            }
        }
        return errinfo;
    }


    /**
     * 获取用户基本信息
     * @see 微信服务器的应答报文是下面这样的，一般Content-Type里面编码都用charset，它竟然用encoding
     * @see HTTP/1.1 200 OK
     * @see Server: nginx/1.8.0
     * @see Date: Wed, 21 Oct 2015 03:56:53 GMT
     * @see Content-Type: application/json; encoding=utf-8
     * @see Content-Length: 357
     * @see Connection: keep-alive
     * @see
     * @see {"subscribe":1,"openid":"o3SHot22_IqkUI7DpahNv-KBiFIs","nickname":"玄玉","sex":1,"language":"en","city":"江北","province":"重庆","country":"中国","headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/Sa1DhFzJREXnSqZKc2Y2AficBdiaaiauFNBbiakfO7fJkf8Cp3oLgJQhbgkwmlN3co2aJr9iabEKJq5jsZYup3gibaVCHD5W13XRmR\/0","subscribe_time":1445398219,"remark":"","groupid":0}
     */
    public static WeixinFansInfo getWeixinFansInfo(String accesstoken, String openid){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_FANSINFO.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken).replace(WeixinConstants.URL_PLACEHOLDER_OPENID, openid);
        String respData = HTTPUtil.post(reqURL, null, null);
        return JSON.parseObject(respData, WeixinFansInfo.class);
    }


    /**
     * 客服接口主动推消息
     * @see http://mp.weixin.qq.com/wiki/1/70a29afed17f56d537c833f89be979c9.html
     * @see 目前只要粉丝在48小时内与公众号发生过互动，那么均可通过该接口主动推消息给粉丝
     * @see 注意：如果需要以某个客服帐号来发消息，需要在请求JSON中加入customservice参数，这里暂未指定customservice
     */
    public static WeixinErrorInfo pushWeixinMsgToFans(String accesstoken, WeixinCustomMsg customMsg){
        String reqURL = WeixinConstants.URL_WEIXIN_CUSTOM_PUSH_MESSAGE.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String reqData = JSON.toJSONString(customMsg);
        LogUtil.getLogger().info("客服接口主动推消息-->待发送的JSON为{}", reqData);
        String respData = HTTPUtil.post(reqURL, reqData, null);
        LogUtil.getLogger().info("客服接口主动推消息-->微信应答JSON为{}", respData);
        WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
        if(errinfo.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMsgByCode(errinfo.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                errinfo.setErrmsg(errmsg);
            }
        }
        return errinfo;
    }


    /**
     * 单发主动推模板消息
     */
    public static WeixinErrorInfo pushWeixinTemplateMsgToFans(String accesstoken, WeixinTemplateMsg templateMsg){
        String reqURL = WeixinConstants.URL_WEIXIN_TEMPLATE_PUSH_MESSAGE.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String reqData = JSON.toJSONString(templateMsg);
        LogUtil.getLogger().info("单发主动推模板消息-->发送的JSON为{}", reqData);
        String respData = HTTPUtil.post(reqURL, reqData, "application/json; charset="+ StandardCharsets.UTF_8);
        LogUtil.getLogger().info("单发主动推模板消息-->微信应答JSON为{}", respData);
        WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
        if(errinfo.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMsgByCode(errinfo.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                errinfo.setErrmsg(errmsg);
            }
        }
        return errinfo;
    }


    /**
     * 获取模板消息列表
     */
    public static List<WeixinTemplate> getWeixinTemplateList(String accesstoken){
        String reqURL = WeixinConstants.URL_WEIXIN_TEMPLATE_GETALL.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String respData = HTTPUtil.post(reqURL, null, null);
        LogUtil.getLogger().info("获取微信模板消息列表，微信应答报文为-->{}", respData);
        Map<String, String> map = JSON.parseObject(respData, new TypeReference<Map<String, String>>(){});
        String templateListStr = map.get("template_list");
        if(StringUtils.isBlank(templateListStr)){
            return new ArrayList<>();
        }
        return JSON.parseArray(templateListStr, WeixinTemplate.class);
    }


    /**
     * 获取临时素材
     * @see http://mp.weixin.qq.com/wiki/11/07b6b76a6b6e8848e855a435d5e34a5f.html
     * @return 获取成功时返回文件保存在本地的路径,获取失败时将抛出RuntimeException
     * Created by 玄玉<https://jadyer.cn/> on 2015/10/30 16:02.
     */
    public static String downloadWeixinTempMediaFile(String accesstoken, String mediaId){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_TEMP_MEDIA_FILE.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken).replace(WeixinConstants.URL_PLACEHOLDER_MEDIAID, mediaId);
        Map<String, String> resultMap = HTTPUtil.download(reqURL, null);
        if("no".equals(resultMap.get("isSuccess"))){
            Map<String, String> errmap = JSON.parseObject(resultMap.get("failReason"), new TypeReference<Map<String, String>>(){});
            String errmsg = WeixinCodeEnum.getMsgByCode(Integer.parseInt((errmap.get("errcode"))));
            if(StringUtils.isBlank(errmsg)){
                errmsg = errmap.get("errmsg");
            }
            throw new RuntimeException("下载微信临时素材" + mediaId + "失败-->" + errmsg);
        }
        return resultMap.get("fullPath");
    }


    /**
     * 创建二维码ticket
     * @see http://mp.weixin.qq.com/wiki/18/167e7d94df85d8389df6c94a7a8f78ba.html
     * @see {"action_name":"QR_LIMIT_STR_SCENE","action_info":{"scene":{"scene_str":"xuanyuabc"}}}
     * @see {"ticket":"gQHy8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL1pVeHIyaFhsOTJfT29HTWdZV1FaAAIE_6jNVgMEAAAAAA==","url":"http:\/\/weixin.qq.com\/q\/ZUxr2hXl92_OoGMgYWQZ"}
     * @param type          二维码类型,0--临时二维码,1--永久二维码,2--永久字符串二维码
     * @param expireSeconds 二维码临时有效的时间,单位为秒,最大不超过2592000s,即30天,不填则默认有效期为30s
     * @param sceneId       二维码参数场景值ID,临时二维码时为32位非0整型,永久二维码时值为1--100000
     * @param sceneStr      二维码参数场景值ID,字符串形式的ID,字符串类型,长度限制为1到64,仅永久二维码支持此字段
     * Created by 玄玉<https://jadyer.cn/> on 2016/2/22 22:33.
     */
    public static String createQrcodeTicket(String accesstoken, int type, int expireSeconds, long sceneId, String sceneStr){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_QRCODE_TICKET.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String reqData;
        if(type == 0){
            reqData = "{\"expire_seconds\":" + expireSeconds + ",\"action_name\":\"QR_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":" + sceneId + "}}}";
        }else if(type == 1){
            reqData = "{\"action_name\":\"QR_LIMIT_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":" + sceneId + "}}}";
        }else if(type == 2){
            reqData = "{\"action_name\":\"QR_LIMIT_STR_SCENE\",\"action_info\":{\"scene\":{\"scene_str\":\"" + sceneStr + "\"}}}";
        }else{
            throw new IllegalArgumentException("无法识别的二维码类型-->[" + type + "]");
        }
        LogUtil.getLogger().info("创建二维码ticket-->待发送的JSON为{}", reqData);
        String respData = HTTPUtil.post(reqURL, reqData, null);
        LogUtil.getLogger().info("创建二维码ticket-->微信应答JSON为{}", respData);
        if(respData.contains("ticket")){
            return JSON.parseObject(respData, new TypeReference<Map<String, String>>(){}).get("ticket");
        }else{
            WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
            if(errinfo.getErrcode() != 0){
                String errmsg = WeixinCodeEnum.getMsgByCode(errinfo.getErrcode());
                if(StringUtils.isBlank(errmsg)){
                    errmsg = errinfo.getErrmsg();
                }
                throw new RuntimeException(errmsg);
            }
            throw new RuntimeException("获取微信二维码时遇到未知异常");
        }
    }


    /**
     * 微信支付--公众号支付--验证接口返回的报文是否表示交易成功
     * <p>
     *     return_code和result_code任意一个不为SUCCESS，则方法会抛出{@link IllegalArgumentException}
     * </p>
     */
    public static void payVerifyIfSuccess(Map<String, String> dataMap){
        if(!StringUtils.equals("SUCCESS", dataMap.get("return_code"))){
            throw new RuntimeException(dataMap.get("return_msg"));
        }
        if(!StringUtils.equals("SUCCESS", dataMap.get("result_code"))){
            String err_code = dataMap.get("err_code");
            String err_code_des = dataMap.get("err_code_des");
            throw new RuntimeException(StringUtils.isNotBlank(err_code_des) ? err_code_des : WeixinPayCodeEnum.getMsgByCode(err_code));
        }
    }


    /**
     * 微信支付--公众号支付--验签
     * <ul>
     *     <li>目前该方法主要用在微信公众号支付中，对于微信推送或被动回复内容的验签</li>
     *     <li>验签未通过时，方法内部会抛出{@link com.jadyer.seed.comm.exception.SeedException#SeedException(int, String)}</li>
     * </ul>
     * Created by 玄玉<https://jadyer.cn/> on 2017/7/8 18:50.
     */
    public static void payVerifySign(Map<String, String> dataMap, String appid){
        //注意：微信红包--发放普通红包和查询红包记录--两个接口返回的<xml>都没有<sign>标签
        if(dataMap.containsKey("sign")){
            String sign_calc;
            if(StringUtils.isBlank(dataMap.get("sign_type"))){
                sign_calc = CodecUtil.buildHexSign(dataMap, "UTF-8", "MD5", WeixinTokenHolder.getWeixinMchkey(appid));
                if(!StringUtils.equals(dataMap.get("sign"), sign_calc.toUpperCase())){
                    sign_calc = CodecUtil.buildHmacSign(dataMap, WeixinTokenHolder.getWeixinMchkey(appid), "HmacSHA256");
                    if(!StringUtils.equals(dataMap.get("sign"), sign_calc.toUpperCase())){
                        throw new IllegalArgumentException("验签未通过");
                    }
                }
            }else{
                String sign_type = dataMap.get("sign_type");
                if(StringUtils.equals("MD5", sign_type)){
                    sign_calc = CodecUtil.buildHexSign(dataMap, "UTF-8", sign_type, WeixinTokenHolder.getWeixinMchkey(appid));
                }else if(StringUtils.equals("HMAC-SHA256", sign_type)){
                    sign_calc = CodecUtil.buildHmacSign(dataMap, WeixinTokenHolder.getWeixinMchkey(appid), "HmacSHA256");
                }else{
                    throw new IllegalArgumentException("不支持的签名算法=["+sign_type+"]");
                }
                if(!StringUtils.equals(dataMap.get("sign"), sign_calc.toUpperCase())){
                    throw new IllegalArgumentException("验签未通过");
                }
            }
        }
    }


    private static String paySign(Map<String, String> reqDataMap, String appid){
        String sign_type = reqDataMap.get("sign_type");
        if(StringUtils.equals("MD5", sign_type)){
            return CodecUtil.buildHexSign(reqDataMap, "UTF-8", "MD5", WeixinTokenHolder.getWeixinMchkey(appid)).toUpperCase();
        }
        if(StringUtils.equals("HMAC-SHA256", sign_type)){
            return CodecUtil.buildHmacSign(reqDataMap, WeixinTokenHolder.getWeixinMchkey(appid), "HmacSHA256").toUpperCase();
        }
        throw new IllegalArgumentException("不支持的签名算法=["+sign_type+"]");
    }


    /**
     * 微信支付--公众号支付--统一下单
     * <p>
     *     该方法会判断接口返回报文中的状态是否成功，并验签（验签失败则直接抛RuntimeException）
     * </p>
     * @return 前台页面呼起微信支付所需的json数据实体
     */
    public static WeixinPayUnifiedorderRespData payUnifiedorder(WeixinPayUnifiedorderReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--统一下单接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        if(null != reqData.getScene_info()){
            reqDataMap.put("scene_info", JSON.toJSONString(new HashMap<String, WeixinPayUnifiedorderReqData.SceneInfo>(){
                private static final long serialVersionUID = -4476694297049282617L;
                {
                    put("store_info", reqData.getScene_info());
                }
            }));
        }
        reqDataMap.put("sign", paySign(reqDataMap, reqData.getAppid()));
        //发送请求
        String respXml = HTTPUtil.post(WeixinConstants.URL_WEIXIN_PAY_UNIFIEDORDER, XmlUtil.mapToXml(reqDataMap), null);
        //解析返回的xml字符串（交易是否成功、验签）
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        //构造前台页面呼起微信支付所需的数据
        WeixinPayUnifiedorderRespData respData = new WeixinPayUnifiedorderRespData();
        respData.setAppId(reqData.getAppid());
        respData.setTimeStamp(Long.toString(System.currentTimeMillis()/1000));
        respData.setNonceStr(RandomStringUtils.randomNumeric(16));
        respData.setPackage_("prepay_id=" + respXmlMap.get("prepay_id"));
        respData.setSignType("MD5");
        //处理特殊字段package_
        Map<String, String> respDataMap = BeanUtil.beanToMap(respData);
        respDataMap.remove("package_");
        respDataMap.put("package", respData.getPackage_());
        respData.setPaySign(CodecUtil.buildHexSign(respDataMap, "UTF-8", respData.getSignType(), WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        LogUtil.getLogger().info("微信支付--公众号支付--统一下单接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }


    /**
     * 微信支付--公众号支付--查询订单
     * <p>
     *     该方法会判断接口返回报文中的状态是否成功，并验签（验签失败则直接抛RuntimeException）
     * </p>
     */
    public static WeixinPayOrderqueryRespData payOrderquery(WeixinPayOrderqueryReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--查询订单接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", paySign(reqDataMap, reqData.getAppid()));
        //发送请求
        String respXml = HTTPUtil.post(WeixinConstants.URL_WEIXIN_PAY_ORDERQUERY, XmlUtil.mapToXml(reqDataMap), null);
        //解析返回的xml字符串（交易是否成功、验签）
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        //返回（注意处理多张代金券）
        WeixinPayOrderqueryRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinPayOrderqueryRespData.class);
        if(StringUtils.isNotBlank(respData.getCoupon_count())){
            List<WeixinPayOrderqueryRespData.CouponInfo> couponInfoList = respData.getCouponInfoList();
            for(int i=0; i<Integer.parseInt(respData.getCoupon_count()); i++){
                WeixinPayOrderqueryRespData.CouponInfo couponInfo = new WeixinPayOrderqueryRespData.CouponInfo();
                couponInfo.setCoupon_type(respXmlMap.get("coupon_type_" + i));
                couponInfo.setCoupon_id(respXmlMap.get("coupon_id_" + i));
                couponInfo.setCoupon_fee(respXmlMap.get("coupon_fee_" + i));
                couponInfoList.add(couponInfo);
            }
        }
        LogUtil.getLogger().info("微信支付--公众号支付--查询订单接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }


    /**
     * 微信支付--公众号支付--关闭订单
     * <p>
     *     该方法会判断接口返回报文中的状态是否成功，并验签（验签失败则直接抛RuntimeException）
     * </p>
     */
    public static WeixinPayCloseorderRespData payCloseorder(WeixinPayCloseorderReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--关闭订单接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", paySign(reqDataMap, reqData.getAppid()));
        //发送请求
        String respXml = HTTPUtil.post(WeixinConstants.URL_WEIXIN_PAY_CLOSEORDER, XmlUtil.mapToXml(reqDataMap), null);
        //解析返回的xml字符串（交易是否成功、验签）
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        WeixinPayCloseorderRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinPayCloseorderRespData.class);
        LogUtil.getLogger().info("微信支付--公众号支付--关闭订单接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }


    /**
     * 微信支付--公众号支付--申请退款
     * <p>
     *     该方法会判断接口返回报文中的状态是否成功，并验签（验签失败则直接抛RuntimeException）
     * </p>
     * @param filepath 证书文件路径（含文件名），比如：/app/p12/apiclient_cert.p12
     */
    public static WeixinPayRefundRespData payRefund(WeixinPayRefundReqData reqData, String filepath){
        LogUtil.getLogger().info("微信支付--公众号支付--申请退款接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", paySign(reqDataMap, reqData.getAppid()));
        //发送请求
        String respXml = HTTPUtil.postWithP12(WeixinConstants.URL_WEIXIN_PAY_REFUND, XmlUtil.mapToXml(reqDataMap), null, filepath, WeixinTokenHolder.getWeixinMchid(reqData.getAppid()));
        //解析返回的xml字符串（交易是否成功、验签）
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        //返回（注意处理多张退款代金券）
        WeixinPayRefundRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinPayRefundRespData.class);
        if(StringUtils.isNotBlank(respData.getCoupon_refund_count())){
            List<WeixinPayRefundRespData.CouponInfo> couponInfoList = respData.getCouponInfoList();
            for(int i=0; i<Integer.parseInt(respData.getCoupon_refund_count()); i++){
                WeixinPayRefundRespData.CouponInfo couponInfo = new WeixinPayRefundRespData.CouponInfo();
                couponInfo.setCoupon_type(respXmlMap.get("coupon_type_" + i));
                couponInfo.setCoupon_refund_id(respXmlMap.get("coupon_refund_id_" + i));
                couponInfo.setCoupon_refund_fee(respXmlMap.get("coupon_refund_fee_" + i));
                couponInfoList.add(couponInfo);
            }
        }
        LogUtil.getLogger().info("微信支付--公众号支付--申请退款接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }


    /**
     * 微信支付--公众号支付--查询退款
     * <p>
     *     该方法会判断接口返回报文中的状态是否成功，并验签（验签失败则直接抛RuntimeException）
     * </p>
     */
    public static WeixinPayRefundqueryRespData payRefundquery(WeixinPayRefundqueryReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--查询退款接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", paySign(reqDataMap, reqData.getAppid()));
        //发送请求
        String respXml = HTTPUtil.post(WeixinConstants.URL_WEIXIN_PAY_REFUNDQUERY, XmlUtil.mapToXml(reqDataMap), null);
        //解析返回的xml字符串（交易是否成功、验签）
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        //返回（注意处理多笔退款信息中的多张退款代金券）
        WeixinPayRefundqueryRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinPayRefundqueryRespData.class);
        if(StringUtils.isNotBlank(respData.getRefund_count())){
            List<WeixinPayRefundqueryRespData.RefundInfo> refundInfoList = respData.getRefundInfoList();
            for(int i=0; i<Integer.parseInt(respData.getRefund_count()); i++){
                WeixinPayRefundqueryRespData.RefundInfo refundInfo = new WeixinPayRefundqueryRespData.RefundInfo();
                refundInfo.setOut_refund_no(respXmlMap.get("out_refund_no_" + i));
                refundInfo.setRefund_id(respXmlMap.get("refund_id_" + i));
                refundInfo.setRefund_channel(respXmlMap.get("refund_channel_" + i));
                refundInfo.setRefund_fee(respXmlMap.get("refund_fee_" + i));
                refundInfo.setSettlement_refund_fee(respXmlMap.get("settlement_refund_fee_" + i));
                refundInfo.setCoupon_type(respXmlMap.get("coupon_type_" + i));
                refundInfo.setCoupon_refund_fee(respXmlMap.get("coupon_refund_fee_" + i));
                refundInfo.setRefund_status(respXmlMap.get("refund_status_" + i));
                refundInfo.setRefund_account(respXmlMap.get("refund_account_" + i));
                refundInfo.setRefund_recv_accout(respXmlMap.get("refund_recv_accout_" + i));
                refundInfo.setRefund_success_time(respXmlMap.get("refund_success_time_" + i));
                String coupon_refund_count = respXmlMap.get("coupon_refund_count_" + i);
                if(StringUtils.isNotBlank(coupon_refund_count)){
                    refundInfo.setCoupon_refund_count(coupon_refund_count);
                    List<WeixinPayRefundqueryRespData.RefundInfo.CouponRefundInfo> couponRefundInfoList = refundInfo.getCouponRefundInfoList();
                    for(int j=0; j<Integer.parseInt(coupon_refund_count); j++){
                        WeixinPayRefundqueryRespData.RefundInfo.CouponRefundInfo couponRefundInfo = new WeixinPayRefundqueryRespData.RefundInfo.CouponRefundInfo();
                        couponRefundInfo.setCoupon_refund_id(respXmlMap.get("coupon_refund_id_" + i + "_" + j));
                        couponRefundInfo.setCoupon_refund_fee(respXmlMap.get("coupon_refund_fee_" + i + "_" + j));
                        couponRefundInfoList.add(couponRefundInfo);
                    }
                }
                refundInfoList.add(refundInfo);
            }
        }
        LogUtil.getLogger().info("微信支付--公众号支付--查询退款接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }


    /**
     * 微信支付--公众号支付--下载对账单
     * <p>
     *     具体实现待补充
     * </p>
     */
    public static WeixinPayDownloadbillRespData payDownloadbill(WeixinPayDownloadbillReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--下载对账单接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", paySign(reqDataMap, reqData.getAppid()));
        //发送请求
        String respXml = HTTPUtil.post(WeixinConstants.URL_WEIXIN_PAY_DOWNLOADBILL, XmlUtil.mapToXml(reqDataMap), null);
        throw new RuntimeException("微信支付--公众号支付--下载对账单--接口实现待补充......");
    }


    /**
     * 微信红包--发放普通红包
     * <p>
     *     该方法会判断接口返回报文中的状态是否成功，并验签（验签失败则直接抛RuntimeException）
     * </p>
     * @param filepath 证书文件路径（含文件名），比如：/app/p12/apiclient_cert.p12
     */
    public static WeixinRedpackSendRespData redpackSend(WeixinRedpackSendReqData reqData, String filepath){
        LogUtil.getLogger().info("微信红包--发放普通红包接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", paySign(reqDataMap, reqData.getWxappid()));
        //发送请求
        String respXml = HTTPUtil.postWithP12(WeixinConstants.URL_WEIXIN_REDPACK_SEND, XmlUtil.mapToXml(reqDataMap), null, filepath, WeixinTokenHolder.getWeixinMchid(reqData.getWxappid()));
        //解析返回的xml字符串（交易是否成功、验签）
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getWxappid());
        //返回（注意处理多张代金券）
        WeixinRedpackSendRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinRedpackSendRespData.class);
        LogUtil.getLogger().info("微信红包--发放普通红包接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }


    /**
     * 微信红包--查询红包记录
     * <p>
     *     该方法会判断接口返回报文中的状态是否成功，并验签（验签失败则直接抛RuntimeException）
     * </p>
     * @param filepath 证书文件路径（含文件名），比如：/app/p12/apiclient_cert.p12
     */
    public static WeixinRedpackGethbinfoRespData redpackGethbinfo(WeixinRedpackGethbinfoReqData reqData, String filepath){
        LogUtil.getLogger().info("微信红包--查询红包记录接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", paySign(reqDataMap, reqData.getAppid()));
        //发送请求
        String respXml = HTTPUtil.postWithP12(WeixinConstants.URL_WEIXIN_REDPACK_GETHBINFO, XmlUtil.mapToXml(reqDataMap), null, filepath, WeixinTokenHolder.getWeixinMchid(reqData.getAppid()));
        //解析返回的xml字符串（交易是否成功、验签）
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        //返回（注意处理裂变红包的领取列表）
        WeixinRedpackGethbinfoRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinRedpackGethbinfoRespData.class);
        try{
            List<WeixinRedpackGethbinfoRespData.Hbinfo> hbinfoList = new ArrayList<>();
            NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(IOUtils.toInputStream(respXml, StandardCharsets.UTF_8)).getElementsByTagName("hbinfo");
            for(int i=0; i<nodeList.getLength(); i++){
                WeixinRedpackGethbinfoRespData.Hbinfo hbinfo = new WeixinRedpackGethbinfoRespData.Hbinfo();
                NodeList childNodes = nodeList.item(i).getChildNodes();
                for(int k=0; k<childNodes.getLength(); k++){
                    if(childNodes.item(k).getNodeType() == Node.ELEMENT_NODE){
                        if(StringUtils.equals("openid", childNodes.item(k).getNodeName())){
                            hbinfo.setOpenid(childNodes.item(k).getTextContent());
                        }
                        if(StringUtils.equals("amount", childNodes.item(k).getNodeName())){
                            hbinfo.setAmount(Integer.parseInt(childNodes.item(k).getTextContent()));
                        }
                        if(StringUtils.equals("rcv_time", childNodes.item(k).getNodeName())){
                            hbinfo.setRcv_time(childNodes.item(k).getTextContent());
                        }
                    }
                }
                hbinfoList.add(hbinfo);
            }
            respData.setHbinfolist(hbinfoList);
        }catch(Exception e){
            LogUtil.getLogger().info("微信红包--查询红包记录接口解析裂变红包的领取列表时，发生异常，堆栈轨迹如下", e);
        }
        LogUtil.getLogger().info("微信红包--查询红包记录接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }
}