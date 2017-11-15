package com.jadyer.seed.seedoc.web.controller;

import com.jadyer.seed.comm.constant.CommonResult;
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
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/11/15 15:45.
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
    public CommonResult get(@PathVariable long id){
        return new CommonResult(apiService.get(id));
    }


    @ResponseBody
    @PostMapping("delete/{id}")
    public CommonResult delete(@PathVariable long id){
        apiService.delete(id);
        return new CommonResult();
    }


    @ResponseBody
    @PostMapping("/upsert")
    public CommonResult upsert(Api api){
        return new CommonResult(apiService.upsert(api));
    }
}