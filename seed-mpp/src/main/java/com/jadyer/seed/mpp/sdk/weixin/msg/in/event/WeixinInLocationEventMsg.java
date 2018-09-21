package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

/**
 * 上報地理位置事件
 * -----------------------------------------------------------------------------------------------------------
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140454&token=&lang=zh_CN
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140841&token=&lang=zh_CN
 * http://mp.weixin.qq.com/wiki/4/0bd25e04332eccf83bc2e71df9d3e860.html
 * http://mp.weixin.qq.com/wiki/7/9f89d962eba4c5924ed95b513ba69d9b.html#.E4.B8.8A.E6.8A.A5.E5.9C.B0.E7.90.86.E4.BD.8D.E7.BD.AE.E4.BA.8B.E4.BB.B6
 * -----------------------------------------------------------------------------------------------------------
 * <xml>
 *     <ToUserName><![CDATA[gh_4769d11d72e0]]></ToUserName>
 *     <FromUserName><![CDATA[o3SHot22_IqkUI7DpahNv-KBiFIs]]></FromUserName>
 *     <CreateTime>1464262900</CreateTime>
 *     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[LOCATION]]></Event>
 *     <Latitude>29.619682</Latitude>
 *     <Longitude>106.497185</Longitude>
 *     <Precision>65.000000</Precision>
 * </xml>
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2016/5/26 19:17.
 */
public class WeixinInLocationEventMsg extends WeixinInEventMsg {
    /**
     * 上報地理位置事件
     */
    public static final String EVENT_INLOCATION_LOCATION = "LOCATION";

    /**
     * 地理位置纬度
     */
    private String latitude;

    /**
     * 地理位置经度
     */
    private String longitude;

    /**
     * 地理位置精度
     */
    private String precision;

    public WeixinInLocationEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }
}
