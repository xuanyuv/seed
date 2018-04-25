package com.jadyer.seed.scs;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/23 12:09.
 */
@Controller
public class ViewController {
    @GetMapping({"", "/"})
    String index(){
        return "/portal/index";
    }

    @GetMapping("/404")
    String notfound(){
        return "/404";
    }

    @GetMapping("/500")
    String err(){
        return "/500";
    }

    @GetMapping("/img")
    String img(String path, HttpServletRequest request){
        request.setAttribute("path", path);
        return "/comm/img";
    }

    @GetMapping("/tologin")
    String tologin(){
        return "/login";
    }


    @GetMapping("/building")
    String building(){
        return "/building";
    }


    @ResponseBody
    @PostMapping("/login")
    public CommonResult login(String username, String password, String captcha, HttpSession session){
        if(!StringUtils.equals(captcha, (String)session.getAttribute("captcha"))){
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的验证码");
        }
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("appid", "134545");
        dataMap.put("mpid", "g_dfuer");
        dataMap.put("mpno", "xuanyu");
        dataMap.put("mpname", "半步多社区");
        dataMap.put("appsecret", "vydntyg");
        dataMap.put("bindStatus", "1");
        dataMap.put("mptype", "微信公众号");
        dataMap.put("username", "玄玉");
        session.setAttribute(SeedConstants.WEB_SESSION_USER, dataMap);
        session.setAttribute(SeedConstants.WEB_CURRENT_MENU, "menu_module");
        session.setAttribute("mpurl", "http://jadyer.cn/");
        session.setAttribute("token", "13248558648888942");
        return new CommonResult();
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