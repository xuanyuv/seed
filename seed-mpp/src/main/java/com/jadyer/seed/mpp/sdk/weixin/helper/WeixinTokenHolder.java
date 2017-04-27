package com.jadyer.seed.mpp.sdk.weixin.helper;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinOAuthAccessToken;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 微信公众平台Token持有器
 * @see 1.appid和appsecret是与Token息息相关的,故一并缓存于此处
 * @see 2.flag常量均加了appid是考虑到更换绑定的公众号时,获取到access_token是旧的,从而影响自定义菜单发布
 * @create Oct 29, 2015 8:11:50 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinTokenHolder {
    private static final String FLAG_WEIXIN_ACCESSTOKEN = "weixin_access_token_";
    private static final String FLAG_WEIXIN_JSAPI_TICKET = "weixin_jsapi_ticket_";
    private static final String FLAG_WEIXIN_ACCESSTOKEN_EXPIRETIME = FLAG_WEIXIN_ACCESSTOKEN + "expire_time_";
    private static final String FLAG_WEIXIN_JSAPI_TICKET_EXPIRETIME = FLAG_WEIXIN_JSAPI_TICKET + "expire_time_";
    private static final long WEIXIN_TOKEN_EXPIRE_TIME_MILLIS = 7000 * 1000;
    private static AtomicBoolean weixinAccessTokenRefreshing = new AtomicBoolean(false);
    private static AtomicBoolean weixinJSApiTicketRefreshing = new AtomicBoolean(false);
    private static ConcurrentHashMap<String, Object> tokenMap = new ConcurrentHashMap<>();

    private WeixinTokenHolder(){}

    /**
     * 登记微信appid和appsecret
     * @return 返回已登记的微信appsecret
     * @create Jan 3, 2016 3:07:12 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String setWeixinAppidAppsecret(String appid, String appsecret){
        for(Map.Entry<String,Object> entry : tokenMap.entrySet()){
            if(entry.getKey().endsWith("_"+appid)){
                tokenMap.remove(entry.getKey());
            }
        }
        tokenMap.put(appid, appsecret);
        return getWeixinAppsecret(appid);
    }


    /**
     * 获取已登记的微信appsecret
     * @create Jan 3, 2016 3:07:21 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getWeixinAppsecret(String appid){
        String appsecret = (String)tokenMap.get(appid);
        if(StringUtils.isBlank(appsecret)){
            throw new IllegalArgumentException("未登记微信appsecret");
        }
        return appsecret;
    }


    /**
     * 记录微信媒体文件存储在本地的完整路径
     * @param mediaId           微信媒体文件ID
     * @param localFileFullPath 微信媒体文件存储在本地的完整路径
     * @return 返回已设置的微信媒体文件存储在本地的完整路径
     * @create Nov 9, 2015 9:21:28 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String setMediaIdFilePath(String appid, String mediaId, String localFileFullPath){
        tokenMap.put(appid+"_"+mediaId, localFileFullPath);
        return getMediaIdFilePath(appid, mediaId);
    }


    /**
     * 获取微信媒体文件存储在本地的完整路径
     * @param mediaId 微信媒体文件ID
     * @return 返回微信媒体文件存储在本地的完整路径
     * @create Nov 9, 2015 9:21:52 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getMediaIdFilePath(String appid, String mediaId){
        String localFileFullPath = (String)tokenMap.get(appid+"_"+mediaId);
        if(StringUtils.isBlank(localFileFullPath)){
            throw new IllegalArgumentException("不存在的本地媒体文件appid="+appid+",mediaId="+mediaId);
        }
        return localFileFullPath;
    }


    /**
     * 获取微信access_token
     * @see 这里只缓存7000s,详细介绍见http://mp.weixin.qq.com/wiki/11/0e4b294685f817b95cbed85ba5e82b8f.html
     * @see 7000s到期时,一个请求在更新access_token的过程中,另一个请求进来时,其取到的是旧的access_token(200s内都是有效的)
     * @create Oct 29, 2015 8:13:24 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getWeixinAccessToken(String appid){
        Long expireTime = (Long)tokenMap.get(FLAG_WEIXIN_ACCESSTOKEN_EXPIRETIME + appid);
        if(null!=expireTime && expireTime>=System.currentTimeMillis()){
            return (String)tokenMap.get(FLAG_WEIXIN_ACCESSTOKEN + appid);
        }
        if(weixinAccessTokenRefreshing.compareAndSet(false, true)){
            String accessToken;
            try {
                accessToken = WeixinHelper.getWeixinAccessToken(appid, getWeixinAppsecret(appid));
                tokenMap.put(FLAG_WEIXIN_ACCESSTOKEN + appid, accessToken);
                tokenMap.put(FLAG_WEIXIN_ACCESSTOKEN_EXPIRETIME + appid, System.currentTimeMillis()+WEIXIN_TOKEN_EXPIRE_TIME_MILLIS);
            } catch (Exception e) {
                LogUtil.getLogger().error("获取微信appid=["+appid+"]的access_token失败", e);
            }
            weixinAccessTokenRefreshing.set(false);
        }
        return (String)tokenMap.get(FLAG_WEIXIN_ACCESSTOKEN + appid);
    }


    /**
     * 获取微信jsapi_ticket
     * @see 这里只缓存7000s,详细介绍见http://mp.weixin.qq.com/wiki/7/aaa137b55fb2e0456bf8dd9148dd613f.html
     * @create Oct 29, 2015 9:55:33 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getWeixinJSApiTicket(String appid){
        Long expireTime = (Long)tokenMap.get(FLAG_WEIXIN_JSAPI_TICKET_EXPIRETIME + appid);
        if(null!=expireTime && expireTime>=System.currentTimeMillis()){
            return (String)tokenMap.get(FLAG_WEIXIN_JSAPI_TICKET + appid);
        }
        if(weixinJSApiTicketRefreshing.compareAndSet(false, true)){
            String jsapiTicket;
            try {
                jsapiTicket = WeixinHelper.getWeixinJSApiTicket(getWeixinAccessToken(appid));
                tokenMap.put(FLAG_WEIXIN_JSAPI_TICKET + appid, jsapiTicket);
                tokenMap.put(FLAG_WEIXIN_JSAPI_TICKET_EXPIRETIME + appid, System.currentTimeMillis()+WEIXIN_TOKEN_EXPIRE_TIME_MILLIS);
            } catch (Exception e) {
                LogUtil.getLogger().error("获取微信appid=["+appid+"]的jsapi_ticket失败", e);
            }
            weixinJSApiTicketRefreshing.set(false);
        }
        return (String)tokenMap.get(FLAG_WEIXIN_JSAPI_TICKET + appid);
    }


    /**
     * 通过code换取网页授权access_token
     * @see 这里只缓存7000s,详细介绍见http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
     * @param code 换取access_token的有效期为5分钟的票据
     * @return 返回获取到的网页access_token(获取失败时的应答码也在该返回中)
     * @create Oct 29, 2015 9:32:01 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static WeixinOAuthAccessToken getWeixinOAuthAccessToken(String appid, String code){
        //Long expireTime = (Long)tokenMap.get(FLAG_WEIXIN_OAUTH_ACCESSTOKEN_EXPIRETIME + appid);
        //if(null!=expireTime && expireTime>=System.currentTimeMillis()){
        //    return (WeixinOAuthAccessToken)tokenMap.get(FLAG_WEIXIN_OAUTH_ACCESSTOKEN + appid);
        //}
        //if(weixinOAuthAccessTokenRefreshing.compareAndSet(false, true)){
        //    WeixinOAuthAccessToken weixinOauthAccessToken = WeixinHelper.getWeixinOAuthAccessToken(appid, getWeixinAppsecret(appid), code);
        //    tokenMap.put(FLAG_WEIXIN_OAUTH_ACCESSTOKEN + appid, weixinOauthAccessToken);
        //    tokenMap.put(FLAG_WEIXIN_OAUTH_ACCESSTOKEN_EXPIRETIME + appid, System.currentTimeMillis()+WEIXIN_TOKEN_EXPIRE_TIME_MILLIS);
        //    weixinOAuthAccessTokenRefreshing.set(false);
        //}
        //return (WeixinOAuthAccessToken)tokenMap.get(FLAG_WEIXIN_OAUTH_ACCESSTOKEN + appid);
        return WeixinHelper.getWeixinOAuthAccessToken(appid, getWeixinAppsecret(appid), code);
    }
}