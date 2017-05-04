package com.jadyer.seed.controller.swagger;

import com.jadyer.seed.comm.constant.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2016/9/26 14:40.
 */
@RestController
@RequestMapping("/order")
public class SwaggerDemoOrderController {
    @ApiOperation("获取订单列表")
    @GetMapping("/list")
    CommonResult list(){
        return new CommonResult();
    }

    @ApiIgnore
    @PostMapping("/upsert")
    CommonResult upsert(String username){
        return new CommonResult();
    }
}