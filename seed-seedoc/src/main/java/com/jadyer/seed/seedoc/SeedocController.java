package com.jadyer.seed.seedoc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "/demo/list";
    }


    @GetMapping("/get")
    public String get(HttpServletRequest request){
        request.setAttribute("uname", "玄玉归来");
        return "/demo/get";
    }
}