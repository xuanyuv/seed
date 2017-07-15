package com.jadyer.seed.mpp.web.service.async;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.qq.helper.QQHelper;
import com.jadyer.seed.mpp.sdk.qq.helper.QQTokenHolder;
import com.jadyer.seed.mpp.sdk.qq.model.QQFansInfo;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinFansInfo;
import com.jadyer.seed.mpp.web.model.MppFansInfo;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.FansService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Async("mppExecutor")
public class FansSaveAsync {
    @Resource
    private FansService fansService;

    public void save(MppUserInfo mppUserInfo, String openid){
        try{
            MppFansInfo mppFansInfo = fansService.getByUidAndOpenid(mppUserInfo.getId(), openid);
            if(null == mppFansInfo){
                mppFansInfo = new MppFansInfo();
            }
            /*
             * 向微信服务器或QQ服务器查询粉丝信息
             */
            if(1 == mppUserInfo.getMptype()){
                WeixinFansInfo weixinFansInfo = WeixinHelper.getWeixinFansInfo(WeixinTokenHolder.getWeixinAccessToken(mppUserInfo.getAppid()), openid);
                mppFansInfo.setUid(mppUserInfo.getId());
                mppFansInfo.setWxid(mppUserInfo.getMpid());
                mppFansInfo.setOpenid(openid);
                mppFansInfo.setSubscribe(String.valueOf(weixinFansInfo.getSubscribe()));
                mppFansInfo.setSubscribeTime(DateFormatUtils.format(new Date(Long.parseLong(weixinFansInfo.getSubscribe_time())*1000), "yyyy-MM-dd HH:mm:ss"));
                mppFansInfo.setNickname(JadyerUtil.escapeEmoji(weixinFansInfo.getNickname()));
                mppFansInfo.setSex(weixinFansInfo.getSex());
                mppFansInfo.setCity(weixinFansInfo.getCity());
                mppFansInfo.setCountry(weixinFansInfo.getCountry());
                mppFansInfo.setProvince(weixinFansInfo.getProvince());
                mppFansInfo.setLanguage(weixinFansInfo.getLanguage());
                mppFansInfo.setHeadimgurl(weixinFansInfo.getHeadimgurl());
                mppFansInfo.setUnionid(weixinFansInfo.getUnionid());
                mppFansInfo.setRemark(weixinFansInfo.getRemark());
                mppFansInfo.setGroupid(weixinFansInfo.getGroupid());
                fansService.upsert(mppFansInfo);
            }else{
                QQFansInfo qqFansInfo = QQHelper.getQQFansInfo(QQTokenHolder.getQQAccessToken(mppUserInfo.getAppid()), openid);
                mppFansInfo.setUid(mppUserInfo.getId());
                mppFansInfo.setWxid(mppUserInfo.getMpid());
                mppFansInfo.setOpenid(openid);
                mppFansInfo.setSubscribe(String.valueOf(qqFansInfo.getSubscribe()));
                mppFansInfo.setSubscribeTime(DateFormatUtils.format(new Date(Long.parseLong(qqFansInfo.getSubscribe_time())*1000), "yyyy-MM-dd HH:mm:ss"));
                mppFansInfo.setNickname(JadyerUtil.escapeEmoji(qqFansInfo.getNickname()));
                mppFansInfo.setSex(qqFansInfo.getSex());
                mppFansInfo.setCity(qqFansInfo.getCity());
                mppFansInfo.setCountry(qqFansInfo.getCountry());
                mppFansInfo.setProvince(qqFansInfo.getProvince());
                mppFansInfo.setLanguage(qqFansInfo.getLanguage());
                mppFansInfo.setHeadimgurl(qqFansInfo.getHeadimgurl());
                mppFansInfo.setUnionid(qqFansInfo.getUnionid());
                mppFansInfo.setRemark(qqFansInfo.getRemark());
                mppFansInfo.setGroupid(qqFansInfo.getGroupid());
                fansService.upsert(mppFansInfo);
            }
        }catch(Exception e){
            LogUtil.getLogger().error("粉丝信息异步保存时发生异常，堆栈轨迹如下：", e);
        }
    }
}