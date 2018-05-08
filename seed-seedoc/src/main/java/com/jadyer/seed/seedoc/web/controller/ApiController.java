package com.jadyer.seed.seedoc.web.controller;

import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.seedoc.web.model.Api;
import com.jadyer.seed.seedoc.web.service.ApiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * API
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/11/15 17:42.
 */
@Controller
@RequestMapping("/api")
public class ApiController {
    @Resource
    private ApiService apiService;

    /**
     * 分页查询
     * @param pageNo 页码，起始值为0，未传此值则默认取0
     */
    @GetMapping("/list")
    public String list(String pageNo, HttpServletRequest request){
        request.setAttribute("page", apiService.list(pageNo));
        return "/admin/api.list";
    }


    @ResponseBody
    @GetMapping("/get/{id}")
    public CommResult<Api> get(@PathVariable long id){
        return CommResult.success(apiService.get(id));
    }


    @ResponseBody
    @PostMapping("delete/{id}")
    public CommResult delete(@PathVariable long id){
        apiService.delete(id);
        return CommResult.success();
    }


    @ResponseBody
    @PostMapping("/upsert")
    public CommResult<Api> upsert(Api api){
        return CommResult.success(apiService.upsert(api));
    }


    @GetMapping("/see/{id}")
    public String see(@PathVariable long id, HttpServletRequest request){
        request.setAttribute("id", id);
        return "/api.see";
    }
}