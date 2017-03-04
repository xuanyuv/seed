package com.jadyer.seed.controller.swagger;

import com.jadyer.seed.comm.constant.CommonResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/9/26 13:06.
 */
@RestController
@RequestMapping("/user")
public class SwaggerDemoUserController {
    private static Map<Long, SwaggerDemoUser> USER_MAP = Collections.synchronizedMap(new HashMap<Long, SwaggerDemoUser>());

    @ApiOperation("查看用户")
    @ApiImplicitParam(name="id", value="用户ID", required=true, dataType="Long", paramType="path")
    @RequestMapping(value="/get/{id}", method=RequestMethod.GET)
    CommonResult get(@PathVariable long id){
        return new CommonResult(USER_MAP.get(id));
    }


    @ApiOperation("删除用户")
    @ApiImplicitParam(name="id", value="用户ID", required=true, dataType="Long", paramType="path")
    @RequestMapping(value="/delete/{id}", method=RequestMethod.POST)
    CommonResult delete(@PathVariable long id){
        return new CommonResult(USER_MAP.remove(id));
    }


    @ApiOperation("创建用户")
    @ApiImplicitParam(name="user", value="用户详细信息的实体User", required=true)
    @RequestMapping(value="/add", method=RequestMethod.POST)
    CommonResult add(SwaggerDemoUser user){
        USER_MAP.put(user.getId(), user);
        return new CommonResult(user);
    }


    @ApiOperation("更新用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="用户ID", required=true, dataType="Long", paramType="path"),
            @ApiImplicitParam(name="user", value="更新后的User对象信息", required=true)
    })
    @RequestMapping(value="/update/{id}", method=RequestMethod.POST)
    CommonResult update(@PathVariable long id, SwaggerDemoUser user){
        SwaggerDemoUser u = USER_MAP.get(id);
        u.setUsername(user.getUsername());
        u.setPosition(user.getPosition());
        USER_MAP.put(id, u);
        return new CommonResult(u);
    }
}