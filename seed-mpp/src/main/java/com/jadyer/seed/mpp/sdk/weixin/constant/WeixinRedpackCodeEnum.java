package com.jadyer.seed.mpp.sdk.weixin.constant;

/**
 * 微信红包--错误码
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/28 11:09.
 */
public enum WeixinRedpackCodeEnum {
    WEIXIN_REDPACK_NO_AUTH             ("NO_AUTH",             "没有权限"),
    WEIXIN_REDPACK_CA_ERROR            ("CA_ERROR",            "CA证书出错"),
    WEIXIN_REDPACK_XML_ERROR           ("XML_ERROR",           "请求的xml格式错误，或者post的数据为空"),
    WEIXIN_REDPACK_FREQ_LIMIT          ("FREQ_LIMIT",          "超过频率限制,请稍后再试"),
    WEIXIN_REDPACK_SIGN_ERROR          ("SIGN_ERROR",          "签名错误"),
    WEIXIN_REDPACK_PARAM_ERROR         ("PARAM_ERROR",         "参数有误"),
    WEIXIN_REDPACK_SYSTEMERROR         ("SYSTEMERROR",         "微信红包系统繁忙，请稍后再试。"),
    WEIXIN_REDPACK_SENDNUM_LIMIT       ("SENDNUM_LIMIT",       "该用户今日领取红包个数超过限制"),
    WEIXIN_REDPACK_ILLEGAL_APPID       ("ILLEGAL_APPID",       "非法appid"),
    WEIXIN_REDPACK_OPENID_ERRO         ("OPENID_ERROR",        "openid和appid不匹配"),
    WEIXIN_REDPACK_NOTENOUGH           ("NOTENOUGH",           "帐号余额不足"),
    WEIXIN_REDPACK_FATAL_ERROR         ("FATAL_ERROR",         "重复请求时，参数与原单不一致"),
    WEIXIN_REDPACK_MONEY_LIMIT         ("MONEY_LIMIT",         "红包金额发放限制"),
    WEIXIN_REDPACK_SEND_FAILED         ("SEND_FAILED",         "红包发放失败,请更换单号再重试"),
    WEIXIN_REDPACK_PROCESSING          ("PROCESSING",          "请求已受理，请稍后使用原单号查询发放结果"),
    //发放平台红包（https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_4&index=3）
    WEIXIN_REDPACK_FATAL_ERROR_99      ("金额和原始单参数不一致", "金额和原始单参数不一致"),
    WEIXIN_REDPACK_MSGAPPID_ERROR      ("MSGAPPID_ERROR",      "触达消息给用户appid有误"),
    WEIXIN_REDPACK_ACCEPTMODE_ERROR    ("ACCEPTMODE_ERROR",    "服务商模式下主商户号与子商户号关系校验失败"),
    //发放裂变红包（https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_5&index=4）,
    WEIXIN_REDPACK_NOT_FOUND           ("NOT_FOUND",           "指定单号数据不存在"),
    //查询红包记录（https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_6&index=5）
    WEIXIN_REDPACK_SECOND_OVER_LIMITED ("SECOND_OVER_LIMITED", "企业红包的按分钟发放受限"),
    WEIXIN_REDPACK_DAY_OVER_LIMITED    ("DAY_OVER_LIMITED",    "企业红包的按天日发放受限");

    private final String code;
    private final String msg;

    WeixinRedpackCodeEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 通过枚举code获取对应的message
     * @return 取不到时返回null
     */
    public static String getMsgByCode(String code){
        for(WeixinRedpackCodeEnum weixinRedpackCodeEnum : values()){
            if(weixinRedpackCodeEnum.getCode().equals(code)){
                return weixinRedpackCodeEnum.getMsg();
            }
        }
        return null;
    }
}