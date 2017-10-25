package com.jadyer.seed.mpp.sdk.weixin.model.pay;

/**
 * 微信支付--公众号支付--关闭订单接口出参
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/10 19:48.
 */
public class WeixinPayCloseorderRespData extends WeixinPayRespData {
    /** 业务结果描述（对于业务执行的详细描述） */
    private String result_msg;

    public String getResult_msg() {
        return result_msg;
    }

    public void setResult_msg(String result_msg) {
        this.result_msg = result_msg;
    }
}