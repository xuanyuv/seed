package com.jadyer.seed.mpp.sdk.weixin.model.pay;

/**
 * 微信支付--公众号支付--统一下单接口入参
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/10 10:16.
 */
public class WeixinPayUnifiedorderReqData extends WeixinPayReqData {
    /** 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" */
    private String device_info;

    /** 商品简单描述，该字段请按照规范传递，具体请见参数规定：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String body;

    /** 商品详细描述，对于使用单品优惠的商户，改字段必须按照规范上传，详见“单品优惠参数说明”：https://pay.weixin.qq.com/wiki/doc/api/danpin.php?chapter=9_102&index=2 */
    private String detail;

    /** 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用 */
    private String attach;

    /** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。详见商户订单号：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String out_trade_no;

    /** 标价币种。符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String fee_type;

    /** 标价金额。即订单总金额，单位为分，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String total_fee;

    /** 终端IP。APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP */
    private String spbill_create_ip;

    /** 交易起始时间。即订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String time_start;

    /**
     * 交易结束时间。即订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2
     * <p>
     *     注意：最短失效时间间隔必须大于5分钟
     * </p>
     */
    private String time_expire;

    /** 订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠：https://pay.weixin.qq.com/wiki/doc/api/tools/sp_coupon.php?chapter=12_1 */
    private String goods_tag;

    /** 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数 */
    private String notify_url;

    /**
     * 交易类型
     * JSAPI---公众号支付
     * NATIVE--原生扫码支付
     * APP-----app支付
     * 说明详见参数规定：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2
     */
    private String trade_type;

    /** 商品ID。trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义 */
    private String product_id;

    /** 指定支付方式。上传此参数no_credit--可限制用户不能使用信用卡支付 */
    private String limit_pay;

    /** 用户标识。trade_type=JSAPI时（即公众号支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识 */
    private String openid;

    /**
     * 场景信息。该字段用于统一下单时上报场景信息，目前支持上报实际门店信息
     * <p>
     *     {
     *         "store_info":{
     *             "id": "SZTX001",
     *             "name": "腾大餐厅",
     *             "area_code": "440305",
     *             "address": "科技园中一路腾讯大厦"
     *         }
     *     }
     * </p>
     */
    private SceneInfo scene_info = null;

    /**
     * 场景信息
     */
    public static class SceneInfo{
        /** 门店id */
        private String id;

        /** 门店名称 */
        private String name;

        /** 门店行政区划码，即门店所在地行政区划码，详细见《最新县及县以上行政区划代码》 */
        private String area_code;

        /** 门店详细地址 */
        private String address;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArea_code() {
            return area_code;
        }

        public void setArea_code(String area_code) {
            this.area_code = area_code;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getLimit_pay() {
        return limit_pay;
    }

    public void setLimit_pay(String limit_pay) {
        this.limit_pay = limit_pay;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public SceneInfo getScene_info() {
        return scene_info;
    }

    public void setScene_info(SceneInfo scene_info) {
        this.scene_info = scene_info;
    }
}