package com.jadyer.seed.open.core;

import com.jadyer.seed.open.model.ReqData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/open/router")
public class RouterController {
    @GetMapping({"", "/", "/api", "/openapi"})
    String index(){
        return "/index";
    }

    /**
     * 访问Open开放文档
     */
    @GetMapping("/apidoc")
    String apidoc(){
        return "/apidoc/index";
    }


    @GetMapping("/apidoc/view/{url}")
    String apidocView(@PathVariable String url){
        return "/apidoc/" + url;
    }


    @RequestMapping(value="/rest/h5", method={RequestMethod.GET, RequestMethod.POST})
    public Object resth5(ReqData reqData, HttpServletRequest request, HttpServletResponse response){
        return OpenAnnotationProcessor.INSTANCE.process(reqData, request, response);
    }


    @ResponseBody
    @RequestMapping(value="/rest", method={RequestMethod.GET, RequestMethod.POST})
    public Object rest(ReqData reqData, HttpServletRequest request, HttpServletResponse response){
        return OpenAnnotationProcessor.INSTANCE.process(reqData, request, response);
    }
}