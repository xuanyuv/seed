package com.jadyer.seed.mpp.sdk.weixin.model.redpack;

/**
 * 微信红包--接口出参基类
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/28 10:43.
 */
public abstract class WeixinRedpackRespData {
    /** 返回状态码：SUCCESS/FAIL */
    private String return_code;

    /** 返回信息。如非空，则为错误原因 */
    private String return_msg;

    /** 业务结果：SUCCESS/FAIL */
    private String result_code;

    /** 错误码 */
    private String err_code;

    /** 错误码描述 */
    private String err_code_des;

    /** 微信支付分配的商户号 */
    private String mch_id;

    /** 签名，详见签名生成算法：https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=4_3 */
    private String sign;

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}