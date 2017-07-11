package com.jadyer.seed.mpp.mgr.user;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.mgr.user.model.UserInfo;
import com.jadyer.seed.mpp.sdk.qq.helper.QQTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/7 19:46.
 */
@Component
@Async
class AppidSignupAsync {
    @Resource
    private UserService userService;

    void signup(){
        try {
            List<UserInfo> userinfoList = userService.findAll();
            if(userinfoList.isEmpty()){
                LogUtil.getLogger().info("未查询到需要登记的appid");
            }
            for(UserInfo obj : userinfoList){
                if(1 == obj.getBindStatus()){
                    if(1 == obj.getMptype()){
                        WeixinTokenHolder.setWeixinAppidAppsecret(obj.getAppid(), obj.getAppsecret());
                        LogUtil.getLogger().info("登记微信appid=[{}]，appsecret=[{}]完毕", obj.getAppid(), WeixinTokenHolder.getWeixinAppsecret(obj.getAppid()));
                        if(StringUtils.isNotBlank(obj.getMchid())){
                            WeixinTokenHolder.setWeixinAppidMch(obj.getAppid(), obj.getMchid(), obj.getMchkey());
                            LogUtil.getLogger().info("登记微信appid=[{}]，mchid=[{}]完毕", obj.getAppid(), WeixinTokenHolder.getWeixinMchid(obj.getAppid()));
                        }
                    }
                    if(2 == obj.getMptype()){
                        QQTokenHolder.setQQAppidAppsecret(obj.getAppid(), obj.getAppsecret());
                        LogUtil.getLogger().info("登记QQappid=[{}]，appsecret=[{}]完毕", obj.getAppid(), QQTokenHolder.getQQAppsecret(obj.getAppid()));
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.getLogger().error("登记appid时发生异常，堆栈轨迹如下", e);
        }
    }
}
