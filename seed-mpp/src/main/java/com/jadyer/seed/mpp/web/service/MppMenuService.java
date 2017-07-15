package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.mpp.web.model.MppMenuInfo;
import com.jadyer.seed.mpp.web.repository.MppMenuInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/15 12:54.
 */
@Service
public class MppMenuService {
    @Resource
    private MppMenuInfoRepository mppMenuInfoRepository;

    /**
     * 查询当前登录用户关联的公众平台自定义菜单JSON信息
     */
    public String getMenuJson(long uid){
        List<MppMenuInfo> menuList = mppMenuInfoRepository.findByUid(uid);
        for(MppMenuInfo obj : menuList){
            if(3 == obj.getType()){
                return obj.getMenuJson();
            }
        }
        return "";
    }


    /**
     * 存储公众号自定义菜单的json字符串
     * @param menuJson 微信或QQ公众号自定义菜单数据的json字符串
     * @return 返回本次存储在数据库的自定义菜单内容
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean menuJsonUpsert(long uid, String menuJson){
        MppMenuInfo menu = null;
        List<MppMenuInfo> menuList = mppMenuInfoRepository.findByUid(uid);
        for(MppMenuInfo obj : menuList){
            if(3 == obj.getType()){
                menu = obj;
                break;
            }
        }
        if(null == menu){
            menu = new MppMenuInfo();
            menu.setUid(uid);
            menu.setType(3);
            menu.setName("json");
            menu.setMenuJson(menuJson);
        }
        menu.setMenuJson(menuJson);
        return null!= mppMenuInfoRepository.saveAndFlush(menu);
    }
}