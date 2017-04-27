package com.jadyer.seed.mpp.sdk.qq.constant;

public interface QQConstants {
    /**
     * 如果希望QQ服务器对我们回复的消息不做任何处理，并且不会发起重试，可采用两种回复方式告之QQ服务器
     * 1)直接回复success（个人推荐此方式，且已验证通过）
     * 2)直接回复空串（指字节长度为0的空字符串，而不是XML结构体中content字段的内容为空）
     */
    String NOT_NEED_REPLY_FLAG = "success";

    /**
     * 网页授权获取用户信息的方式
     */
    String QQ_OAUTH_SCOPE_SNSAPI_BASE     = "snsapi_base";
    String QQ_OAUTH_SCOPE_SNSAPI_USERINFO = "snsapi_userinfo";

    /**
     * URL属性中的占位符
     */
    String URL_PLACEHOLDER_APPID        = "{appid}";
    String URL_PLACEHOLDER_APPSECRET    = "{appsecret}";
    String URL_PLACEHOLDER_ACCESSTOKEN  = "{accesstoken}";
    String URL_PLACEHOLDER_OPENID       = "{openid}";
    String URL_PLACEHOLDER_REDIRECT_URI = "{redirecturi}";
    String URL_PLACEHOLDER_SCOPE        = "{scope}";
    String URL_PLACEHOLDER_STATE        = "{state}";
    String URL_PLACEHOLDER_CODE         = "{code}";
    String URL_PLACEHOLDER_MEDIAID      = "{mediaid}";

    /**
     * QQURL
     */
    //获取access token
    String URL_QQ_GET_ACCESSTOKEN        = "https://api.mp.qq.com/cgi-bin/token?appid=" + URL_PLACEHOLDER_APPID + "&secret=" + URL_PLACEHOLDER_APPSECRET;
    //获取用户基本信息
    String URL_QQ_GET_FANSINFO           = "https://api.mp.qq.com/cgi-bin/user/info?lang=zh_CN&openid=" + URL_PLACEHOLDER_OPENID + "&access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //自定义菜单之创建
    String URL_QQ_GET_CREATE_MENU        = "https://api.mp.qq.com/cgi-bin/menu/create?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //单发主动推消息
    String URL_QQ_CUSTOM_PUSH_MESSAGE    = "https://api.mp.qq.com/cgi-bin/message/custom/send?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //单发主动推模板消息
    String URL_QQ_TEMPLATE_PUSH_MESSAGE  = "https://api.mp.qq.com/cgi-bin/message/template/send?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //网页授权获取用户信息的Code地址
    String URL_QQ_OAUTH2_GET_CODE        = "https://open.mp.qq.com/connect/oauth2/authorize?appid=" + URL_PLACEHOLDER_APPID + "&redirect_uri=" + URL_PLACEHOLDER_REDIRECT_URI + "&response_type=code&scope=" + URL_PLACEHOLDER_SCOPE + "&state=" + URL_PLACEHOLDER_STATE + "#qq_redirect";
    //通过code换取网页授权access_token
    String URL_QQ_OAUTH2_GET_ACCESSTOKEN = "https://api.mp.qq.com/sns/oauth2/access_token?appid=" + URL_PLACEHOLDER_APPID + "&secret=" + URL_PLACEHOLDER_APPSECRET +"&code=" + URL_PLACEHOLDER_CODE + "&grant_type=authorization_code";
    //获取QQjsapi_ticket
    String URL_QQ_GET_JSAPI_TICKET       = "https://api.mp.qq.com/cgi-bin/ticket/getticket?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN + "&type=jsapi";
    //获取QQ临时素材
    String URL_QQ_GET_TEMP_MEDIA_FILE    = "https://api.mp.qq.com/cgi-bin/media/get?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN + "&media_id=" + URL_PLACEHOLDER_MEDIAID;
}