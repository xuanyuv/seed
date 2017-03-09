package com.jadyer.seed.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2017/3/9 20:03.
 */
//@Controller
public class IndexController {
    @RequestMapping("/")
    String index(){
        return "login";
    }
}