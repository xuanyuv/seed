package com.jadyer.seed.mpp.web;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.RequestUtil;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.MppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/24 15:40.
 */
@Controller
public class ViewController {
    @Resource
    private MppUserService mppUserService;

    @GetMapping({"", "/"})
    String index(){
        return "/login";
    }


    @ResponseBody
    @PostMapping("/login")
    public CommResult login(String username, String password, String captcha, HttpSession session){
        if(!StringUtils.equals(captcha, (String)session.getAttribute("captcha"))){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "无效的验证码");
        }
        MppUserInfo mppUserInfo = mppUserService.findByUsernameAndPassword(username, password);
        if(null == mppUserInfo){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "无效的用户名或密码");
        }
        session.setAttribute(SeedConstants.WEB_SESSION_USER, mppUserInfo);
        session.setAttribute(SeedConstants.WEB_CURRENT_MENU, "menu_mpp");
        return CommResult.success();
    }


    @GetMapping("/logout")
    String logout(HttpSession session){
        session.removeAttribute(SeedConstants.WEB_SESSION_USER);
        return InternalResourceViewResolver.REDIRECT_URL_PREFIX;
    }


    @GetMapping("/captcha")
    void captcha(HttpServletResponse response, HttpSession session){
        session.setAttribute("captcha", RequestUtil.captcha(1, response));
    }


    /**
     * 直接访问页面资源
     * <p>
     *     可以url传参，比如http://127.0.0.1/view?url=/info/get&id=3，则参数id=3会被放到request中
     * </p>
     */
    @GetMapping("/view")
    String view(String url, HttpServletRequest request){
        Map<String, String[]> paramMap = request.getParameterMap();
        for(Map.Entry<String,String[]> entry : paramMap.entrySet()){
            if(!"url".equals(entry.getKey())){
                request.setAttribute(entry.getKey(), entry.getValue()[0]);
            }
        }
        return url;
    }
}