package com.jadyer.seed.mpp.sdk.qq.helper;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.qq.model.QQOAuthAccessToken;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * QQ公众平台Token持有器
 * @see 1.appid和appsecret是与Token息息相关的,故一并缓存于此处
 * @see 2.flag常量均加了appid是考虑到更换绑定的公众号时,获取到access_token是旧的,从而影响自定义菜单发布
 * @create Nov 28, 2015 8:25:40 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class QQTokenHolder {
    private static final String FLAG_QQ_ACCESSTOKEN = "qq_access_token_";
    private static final String FLAG_QQ_JSAPI_TICKET = "qq_jsapi_ticket_";
    private static final String FLAG_QQ_ACCESSTOKEN_EXPIRETIME = FLAG_QQ_ACCESSTOKEN + "expire_time_";
    private static final String FLAG_QQ_JSAPI_TICKET_EXPIRETIME = FLAG_QQ_JSAPI_TICKET + "expire_time_";
    private static final long QQ_TOKEN_EXPIRE_TIME_MILLIS = 7000 * 1000;
    private static AtomicBoolean qqAccessTokenRefreshing = new AtomicBoolean(false);
    private static AtomicBoolean qqJSApiTicketRefreshing = new AtomicBoolean(false);
    private static ConcurrentHashMap<String, Object> tokenMap = new ConcurrentHashMap<String, Object>();

    private QQTokenHolder(){}

    /**
     * 登记QQappid和appsecret
     * @return 返回已登记的QQappsecret
     * @create Jan 3, 2016 7:09:04 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String setQQAppidAppsecret(String appid, String appsecret){
        for(Map.Entry<String,Object> entry : tokenMap.entrySet()){
            if(entry.getKey().endsWith("_"+appid)){
                tokenMap.remove(entry.getKey());
            }
        }
        tokenMap.put(appid, appsecret);
        return getQQAppsecret(appid);
    }


    /**
     * 获取已登记的QQappsecret
     * @create Jan 3, 2016 7:09:18 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getQQAppsecret(String appid){
        String appsecret = (String)tokenMap.get(appid);
        if(StringUtils.isBlank(appsecret)){
            throw new IllegalArgumentException("未登记QQappsecret");
        }
        return appsecret;
    }


    /**
     * 记录QQ媒体文件存储在本地的完整路径
     * @param mediaId           QQ媒体文件ID
     * @param localFileFullPath QQ媒体文件存储在本地的完整路径
     * @return 返回已设置的QQ媒体文件存储在本地的完整路径
     * @create Nov 28, 2015 8:27:41 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String setMediaIdFilePath(String appid, String mediaId, String localFileFullPath){
        tokenMap.put(appid+"_"+mediaId, localFileFullPath);
        return getMediaIdFilePath(appid, mediaId);
    }


    /**
     * 获取QQ媒体文件存储在本地的完整路径
     * @param mediaId QQ媒体文件ID
     * @return 返回QQ媒体文件存储在本地的完整路径
     * @create Nov 28, 2015 8:28:36 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getMediaIdFilePath(String appid, String mediaId){
        String localFileFullPath = (String)tokenMap.get(appid+"_"+mediaId);
        if(StringUtils.isBlank(localFileFullPath)){
            throw new IllegalArgumentException("不存在的本地媒体文件appid="+appid+",mediaId=" + mediaId);
        }
        return localFileFullPath;
    }


    /**
     * 获取QQaccess_token
     * @see 这里只缓存7000s,详细介绍见<<QQ公众号API文档.pdf>>-20150907版
     * @see 7000s到期时,一个请求在更新access_token的过程中,另一个请求进来时,其取到的是旧的access_token(200s内都是有效的)
     * @create Nov 28, 2015 8:30:12 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getQQAccessToken(String appid){
        Long expireTime = (Long)tokenMap.get(FLAG_QQ_ACCESSTOKEN_EXPIRETIME + appid);
        if(null!=expireTime && expireTime>=System.currentTimeMillis()){
            return (String)tokenMap.get(FLAG_QQ_ACCESSTOKEN + appid);
        }
        if(qqAccessTokenRefreshing.compareAndSet(false, true)){
            String accessToken = null;
            try {
                accessToken = QQHelper.getQQAccessToken(appid, getQQAppsecret(appid));
                tokenMap.put(FLAG_QQ_ACCESSTOKEN + appid, accessToken);
                tokenMap.put(FLAG_QQ_ACCESSTOKEN_EXPIRETIME + appid, System.currentTimeMillis()+QQ_TOKEN_EXPIRE_TIME_MILLIS);
            } catch (Exception e) {
                LogUtil.getLogger().error("获取QQappid=["+appid+"]的access_token失败", e);
            }
            qqAccessTokenRefreshing.set(false);
        }
        return (String)tokenMap.get(FLAG_QQ_ACCESSTOKEN + appid);
    }


    /**
     * 获取QQjsapi_ticket
     * @see 这里只缓存7000s,详细介绍见<<QQ公众号API文档.pdf>>-20150907版
     * @create Nov 28, 2015 8:31:20 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getQQJSApiTicket(String appid){
        Long expireTime = (Long)tokenMap.get(FLAG_QQ_JSAPI_TICKET_EXPIRETIME + appid);
        if(null!=expireTime && expireTime>=System.currentTimeMillis()){
            return (String)tokenMap.get(FLAG_QQ_JSAPI_TICKET + appid);
        }
        if(qqJSApiTicketRefreshing.compareAndSet(false, true)){
            String jsapiTicket;
            try {
                jsapiTicket = QQHelper.getQQJSApiTicket(getQQAccessToken(appid));
                tokenMap.put(FLAG_QQ_JSAPI_TICKET + appid, jsapiTicket);
                tokenMap.put(FLAG_QQ_JSAPI_TICKET_EXPIRETIME + appid, System.currentTimeMillis()+QQ_TOKEN_EXPIRE_TIME_MILLIS);
            } catch (Exception e) {
                LogUtil.getLogger().error("获取QQappid=["+appid+"]的jsapi_ticket失败", e);
            }
            qqJSApiTicketRefreshing.set(false);
        }
        return (String)tokenMap.get(FLAG_QQ_JSAPI_TICKET + appid);
    }


    /**
     * 通过code换取网页授权access_token
     * @see 这里只缓存7000s,详细介绍见<<QQ公众号API文档.pdf>>-20150907版
     * @param code 换取access_token的有效期为5分钟的票据
     * @return 返回获取到的网页access_token(获取失败时的应答码也在该返回中)
     * @create Nov 28, 2015 8:32:05 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static QQOAuthAccessToken getQQOAuthAccessToken(String appid, String code){
        //Long expireTime = (Long)tokenMap.get(FLAG_QQ_OAUTH_ACCESSTOKEN_EXPIRETIME + appid);
        //if(null!=expireTime && expireTime>=System.currentTimeMillis()){
        //    return (QQOAuthAccessToken)tokenMap.get(FLAG_QQ_OAUTH_ACCESSTOKEN + appid);
        //}
        //if(qqOAuthAccessTokenRefreshing.compareAndSet(false, true)){
        //    QQOAuthAccessToken qqOauthAccessToken = QQHelper.getQQOAuthAccessToken(appid, getQQAppsecret(appid), code);
        //    tokenMap.put(FLAG_QQ_OAUTH_ACCESSTOKEN + appid, qqOauthAccessToken);
        //    tokenMap.put(FLAG_QQ_OAUTH_ACCESSTOKEN_EXPIRETIME + appid, System.currentTimeMillis()+QQ_TOKEN_EXPIRE_TIME_MILLIS);
        //    qqOAuthAccessTokenRefreshing.set(false);
        //}
        //return (QQOAuthAccessToken)tokenMap.get(FLAG_QQ_OAUTH_ACCESSTOKEN + appid);
        return QQHelper.getQQOAuthAccessToken(appid, getQQAppsecret(appid), code);
    }
}