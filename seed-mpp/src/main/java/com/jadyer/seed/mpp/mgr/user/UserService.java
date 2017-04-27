package com.jadyer.seed.mpp.mgr.user;

import com.jadyer.seed.mpp.mgr.user.model.MenuInfo;
import com.jadyer.seed.mpp.mgr.user.model.UserInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {
    @Resource
    private UserInfoRepository userInfoRepository;
    @Resource
    private MenuInfoRepository menuInfoRepository;

    private String buildEncryptPassword(String password){
        return DigestUtils.md5Hex(password + "https://jadyer.github.io/");
    }

    UserInfo findByUsernameAndPassword(String username, String password){
        return userInfoRepository.findByUsernameAndPassword(username, buildEncryptPassword(password));
    }

    UserInfo findOne(long id){
        return userInfoRepository.findOne(id);
    }

    List<UserInfo> findAll(){
        return userInfoRepository.findAll();
    }

    public UserInfo findByWxid(String mpid){
        return userInfoRepository.findByWxid(mpid);
    }

    public UserInfo findByQqid(String mpid){
        return userInfoRepository.findByQqid(mpid);
    }

    @Transactional(rollbackFor=Exception.class)
    public UserInfo save(UserInfo userInfo){
        return userInfoRepository.saveAndFlush(userInfo);
    }

    /**
     * 修改密码
     * @param userInfo    HttpSession中的当前登录用户信息
     * @param oldPassword 用户输入的旧密码
     * @param newPassword 用户输入的新密码
     */
    @Transactional(rollbackFor=Exception.class)
    UserInfo passwordUpdate(UserInfo userInfo, String oldPassword, String newPassword){
        if(!userInfo.getPassword().equals(buildEncryptPassword(oldPassword))){
            return null;
        }
        userInfo.setPassword(buildEncryptPassword(newPassword));
        return userInfoRepository.saveAndFlush(userInfo);
    }

    /**
     * 查询当前登录用户关联的公众平台自定义菜单JSON信息
     */
    String getMenuJson(long uid){
        List<MenuInfo> menuList = menuInfoRepository.findMenuListByUID(uid);
        for(MenuInfo obj : menuList){
            if(3 == obj.getType()){
                return obj.getMenuJson();
            }
        }
        return "";
    }

    /**
     * 存储微信或QQ公众号自定义菜单的JSON字符串
     * @param menuJson 微信或QQ公众号自定义菜单数据的JSON字符串
     * @return 返回本次存储在数据库的自定义菜单内容
     */
    @Transactional(rollbackFor=Exception.class)
    boolean menuJsonUpsert(long uid, String menuJson){
        MenuInfo menu = null;
        List<MenuInfo> menuList = menuInfoRepository.findMenuListByUID(uid);
        for(MenuInfo obj : menuList){
            if(3 == obj.getType()){
                menu = obj;
                break;
            }
        }
        if(null == menu){
            menu = new MenuInfo();
            menu.setUid(uid);
            menu.setType(3);
            menu.setName("json");
            menu.setMenuJson(menuJson);
        }
        menu.setMenuJson(menuJson);
        return null!= menuInfoRepository.saveAndFlush(menu);
    }
}