package com.jadyer.seed.mpp.sdk.weixin.constant;

/**
 * 微信支付--公众号支付--错误码
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/8 13:52.
 */
public enum WeixinPayCodeEnum {
    WEIXIN_PAY_SYSTEMERROR                        ("SYSTEMERROR",           "系统错误或超时，请重新调用"),
    WEIXIN_PAY_APPID_NOT_EXIST                    ("APPID_NOT_EXIST",       "缺少或无效的APPID"),
    WEIXIN_PAY_MCHID_NOT_EXIST                    ("MCHID_NOT_EXIST",       "缺少或无效的MCHID"),
    WEIXIN_PAY_REQUIRE_POST_METHOD                ("REQUIRE_POST_METHOD",   "请通过post提交"),
    WEIXIN_PAY_SIGNERROR                          ("SIGNERROR",             "参数签名结果不正确"),
    WEIXIN_PAY_XML_FORMAT_ERROR                   ("XML_FORMAT_ERROR",      "XML格式错误"),
    WEIXIN_PAY_NOTENOUGH                          ("NOTENOUGH",             "统一下单时表示：用户余额不足，退款时表示：商户可用退款余额不足"),
    WEIXIN_PAY_PARAM_ERROR                        ("PARAM_ERROR",           "请求参数错误"),
    WEIXIN_PAY_INVALID_TRANSACTIONID              ("INVALID_TRANSACTIONID", "请求参数错误，请检查原交易号是否存在或发起支付交易接口返回失败"),
    //统一下单（https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1）
    WEIXIN_PAY_UNIFIEDORDER_NOAUTH                ("NOAUTH",                "商户无此接口权限"),
    WEIXIN_PAY_UNIFIEDORDER_ORDERPAID             ("ORDERPAID",             "商户订单已支付，无需重复操作"),
    WEIXIN_PAY_UNIFIEDORDER_ORDERCLOSED           ("ORDERCLOSED",           "订单已关闭，无法支付"),
    WEIXIN_PAY_UNIFIEDORDER_APPID_MCHID_NOT_MATCH ("APPID_MCHID_NOT_MATCH", "appid和mch_id不匹配"),
    WEIXIN_PAY_UNIFIEDORDER_LACK_PARAMS           ("LACK_PARAMS",           "缺少必要的请求参数"),
    WEIXIN_PAY_UNIFIEDORDER_OUT_TRADE_NO_USED     ("OUT_TRADE_NO_USED",     "重复的商户订单号，请不要多次提交同一笔交易"),
    WEIXIN_PAY_UNIFIEDORDER_POST_DATA_EMPTY       ("POST_DATA_EMPTY",       "post数据不能为空"),
    WEIXIN_PAY_UNIFIEDORDER_NOT_UTF8              ("NOT_UTF8",              "请使用UTF-8编码格式"),
    //查询订单（https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2）
    WEIXIN_PAY_ORDERQUERY_ORDERNOTEXIST           ("ORDERNOTEXIST",         "此交易订单号不存在"),
    //关闭订单（https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_3）
    WEIXIN_PAY_CLOSEORDER_ORDERPAID               ("ORDERPAID",             "订单已支付，不能发起关单"),
    WEIXIN_PAY_CLOSEORDER_ORDERCLOSED             ("ORDERCLOSED",           "订单已关闭，无法重复关闭"),
    //申请退款（https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_4）
    WEIXIN_PAY_REFUND_BIZERR_NEED_RETRY           ("BIZERR_NEED_RETRY",     "退款业务流程错误（比如并发情况下，业务会被拒绝），需要商户触发重试来解决"),
    WEIXIN_PAY_REFUND_TRADE_OVERDUE               ("TRADE_OVERDUE",         "订单已经超过可退款的最大期限（支付后一年内可退款）"),
    WEIXIN_PAY_REFUND_ERROR                       ("ERROR",                 "申请退款业务发生错误，请根据实际返回的具体错误原因做相应处理"),
    WEIXIN_PAY_REFUND_USER_ACCOUNT_ABNORMAL       ("USER_ACCOUNT_ABNORMAL", "退款请求失败，可能原因是用户帐号已注销，此时商户可自行处理退款"),
    WEIXIN_PAY_REFUND_INVALID_REQ_TOO_MUCH        ("INVALID_REQ_TOO_MUCH",  "连续错误请求数过多被系统短暂屏蔽，请休息一分钟后重试"),
    WEIXIN_PAY_REFUND_FREQUENCY_LIMITED           ("FREQUENCY_LIMITED",     "2个月之前的订单申请退款有频率限制，故该笔退款未受理，请降低频率后重试"),
    //查询退款（https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_5）
    WEIXIN_PAY_REFUNDQUERY_REFUNDNOTEXIST         ("REFUNDNOTEXIST",        "退款订单查询失败：订单号错误或订单状态（未支付、已支付未退款等）不正确"),
    //下载对账单（https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_6）
    WEIXIN_PAY_DOWNLOADBILL_SIGN_ERROR            ("SIGN ERROR",            "参数错误"),
    WEIXIN_PAY_DOWNLOADBILL_invalid_bill_type     ("invalid bill_type",     "参数错误"),
    WEIXIN_PAY_DOWNLOADBILL_data_format_error     ("data format error",     "参数错误"),
    WEIXIN_PAY_DOWNLOADBILL_missing_parameter     ("missing parameter",     "参数错误"),
    WEIXIN_PAY_DOWNLOADBILL_NO_Bill_Exist         ("NO Bill Exist",         "账单不存在（当前商户号没有已成交的订单，不生成对账单）"),
    WEIXIN_PAY_DOWNLOADBILL_Bill_Creating         ("Bill Creating",         "账单未生成（当前商户号没有已成交的订单或对账单尚未生成，若有订单则请10点以后再下载）"),
    WEIXIN_PAY_DOWNLOADBILL_CompressGZip_Error    ("CompressGZip Error",    "账单压缩失败，请稍后重试"),
    WEIXIN_PAY_DOWNLOADBILL_UnCompressGZip_Error  ("CompressGZip Error",    "账单解压失败，请稍后重试");

    private final String code;
    private final String message;

    WeixinPayCodeEnum(String _code, String _message){
        this.code = _code;
        this.message = _message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 通过枚举code获取对应的message
     * @return 取不到时返回null
     */
    public static String getMessageByCode(String code){
        for(WeixinPayCodeEnum _enum : values()){
            if(_enum.getCode().equals(code)){
                return _enum.getMessage();
            }
        }
        return null;
    }
}