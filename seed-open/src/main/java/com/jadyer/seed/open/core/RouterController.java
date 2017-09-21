package com.jadyer.seed.open.core;

import com.jadyer.seed.open.model.ReqData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/open/router")
public class RouterController {
    /**
     * 访问Open开放文档
     */
    @GetMapping("/apidoc")
    String apidoc(){
        return "/apidoc";
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