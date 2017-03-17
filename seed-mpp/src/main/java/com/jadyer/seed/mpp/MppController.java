package com.jadyer.seed.mpp;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping(value="/mpp")
public class MppController {
    @ResponseBody
    @RequestMapping(value="/login")
    public CommonResult login(String username, String password, String captcha, HttpSession session){
        if(!session.getAttribute("rand").equals(captcha)){
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的验证码");
        }
        LogUtil.getLogger().info("验证码校验-->通过");
        if("jadyer".equals(username) && DigestUtils.md5Hex("xuanyu").equals(password)){
            session.setAttribute(Constants.WEB_SESSION_USER, username);
            return new CommonResult();
        }
        return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "用户名或密码不正确");
    }


    /**
     * 直接访问页面资源
     * <p>
     *     可以url传参，比如http://127.0.0.1/sample/view?url=user/userInfo&id=3，则参数id=3会被放到request中
     * </p>
     */
    @RequestMapping(value="/view", method= RequestMethod.GET)
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