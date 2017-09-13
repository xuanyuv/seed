package com.jadyer.seed.seedoc;

import com.jadyer.seed.comm.constant.CommonResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/13 10:13.
 */
@Controller
@RequestMapping("/seedoc")
public class SeedocController {
    @GetMapping("/list")
    public String list(HttpSession session){
        session.setAttribute("username", "玄玉");
        return "/list";
    }


    @GetMapping("/list99")
    public String list99(HttpSession session){
        session.setAttribute("username", "玄玉99 ");
        return "/list";
    }

    @GetMapping("/get")
    public String get(HttpServletRequest request){
        request.setAttribute("uname", "玄玉归来");
        return "/info/get";
    }

    @ResponseBody
    @GetMapping("/getjson")
    public CommonResult getjson(){
        return new CommonResult("这是返回的jsondata测试文本");
    }
}