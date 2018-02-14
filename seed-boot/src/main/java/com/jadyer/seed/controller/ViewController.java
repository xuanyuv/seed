package com.jadyer.seed.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/23 12:09.
 */
@Controller
public class ViewController {
    @GetMapping({"", "/"})
    String index(){
        return "/index";
    }

    @GetMapping("/viewlog")
    String viewlog(){
        return "/viewlog";
    }


    @ResponseBody
    @PostMapping("/login")
    public CommonResult login(String username, String password, String captcha, HttpSession session){
        if(!StringUtils.equals(captcha, (String)session.getAttribute("captcha"))){
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的验证码");
        }
        session.setAttribute(Constants.WEB_SESSION_USER, "玄玉登录成功");
        return new CommonResult();
    }


    @GetMapping("/logout")
    String logout(HttpSession session){
        session.removeAttribute(Constants.WEB_SESSION_USER);
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