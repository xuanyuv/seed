package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.FansService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/fans")
public class FansController{
    @Resource
    private FansService fansService;

    /**
     * 分页查询粉丝信息
     * @param pageNo zero-based page index
     */
    @RequestMapping("/list")
    public String listViaPage(String pageNo, HttpServletRequest request){
        final long uid = ((MppUserInfo)request.getSession().getAttribute(SeedConstants.WEB_SESSION_USER)).getId();
        request.setAttribute("page", fansService.listViaPage(uid, pageNo));
        return "/admin/fans.list";
    }
}