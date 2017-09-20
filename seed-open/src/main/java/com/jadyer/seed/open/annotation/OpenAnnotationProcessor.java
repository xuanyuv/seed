package com.jadyer.seed.open.annotation;

import com.jadyer.seed.open.model.ReqData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Open注解处理器
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/19 19:20.
 */
public enum OpenAnnotationProcessor {
    INSTANCE;

    public Object process(ReqData reqData, HttpServletRequest request, HttpServletResponse response){
        System.out.println("just do it");
        return null;
    }
}