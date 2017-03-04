package com.jadyer.seed.controller.swagger;

import com.jadyer.seed.comm.constant.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/9/26 14:40.
 */
@RestController
@RequestMapping("/order")
public class SwaggerDemoOrderController {
    @ApiOperation("获取订单列表")
    @RequestMapping(value="/list", method= RequestMethod.GET)
    CommonResult list(){
        return new CommonResult();
    }

    @ApiIgnore
    @RequestMapping(value="/upsert", method=RequestMethod.POST)
    CommonResult upsert(String username){
        return new CommonResult();
    }
}