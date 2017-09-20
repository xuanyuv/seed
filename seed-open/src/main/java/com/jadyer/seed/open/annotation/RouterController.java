package com.jadyer.seed.open.annotation;

import com.jadyer.seed.open.model.ReqData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Controller("AnnotationRouterController")
@RequestMapping("/annotation/open/router")
public class RouterController {
    @Resource
    private RouterService routerService;

    @ResponseBody
    @RequestMapping(value="/rest", method={RequestMethod.GET, RequestMethod.POST})
    public Object rest(ReqData reqData, HttpServletRequest request, HttpServletResponse response){
        return OpenAnnotationProcessor.INSTANCE.process(reqData, request, response);
    }


    @RequestMapping(value="/rest/h5", method={RequestMethod.GET, RequestMethod.POST})
    public Object resth5(ReqData reqData, HttpServletRequest request, HttpServletResponse response){
        return OpenAnnotationProcessor.INSTANCE.process(reqData, request, response);
    }
}