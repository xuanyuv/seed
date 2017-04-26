package com.jadyer.seed.controller;

import com.jadyer.seed.boot.BootProperties;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/7 17:43.
 */
@ApiIgnore
@Controller
//@RestController//它就相当于@Controller和@ResponseBody注解
public class DemoController {
    //若配置文件中未找到该属性，则为其赋默认值为冒号后面的字符串
    @Value("${user.blog:https://jadyer.github.io/}")
    private String blog;
    @Value("${user.weight}")
    private long weight;
    @Value("${user.height}")
    private int height;
    @Value("${user.sex}")
    private int sex;
    @Value("${user.age}")
    private int age;
    @Value("${encrypt.username:Jasypt未启用}")
    private String encryptUsername;
    @Value("${encrypt.password:Jasypt未启用}")
    private String encryptPassword;
    @Resource
    private BootProperties bootProperties;

    ///**
    // * 配置Thymeleaf默认的访问首页
    // */
    //@RequestMapping("/")
    //String index(){
    //    return "login";
    //}


    /**
     * 读取配置文件中的属性
     */
    @ResponseBody
    @GetMapping("/prop")
    public Map<String, Object> prop(){
        Map<String, Object> map = new HashMap<>();
        map.put("weight", this.weight);
        map.put("height", this.height);
        map.put("sex", this.sex);
        map.put("age", this.age);
        map.put("blog", this.blog);
        map.put("hello", "Hello Boot");
        map.put("encryptUsername", this.encryptUsername);
        map.put("encryptPassword", this.encryptPassword);
        map.put("packages", this.bootProperties.getPackages());
        map.put("detailInfo", this.bootProperties.getDetailInfo());
        map.put("addressList", this.bootProperties.getAddressList());
        return map;
    }


    /**
     * Thymeleaf页面点击登录按钮
     */
    @ResponseBody
    @GetMapping("/login/{username}/{password}")
    public CommonResult login(@PathVariable String username, @PathVariable String password, HttpSession session){
        if("jadyer".equals(username) && "xuanyu".equals(password)){
            Map<String, String> fans01 = new HashMap<>();
            fans01.put("headimgurl", "http://wx.qlogo.cn/mmopen/Sa1DhFzJREXnSqZKc2Y2AficBdiaaiauFNBbiakfO7fJkf8Cp3oLgJQhbgkwmlN3co2aJr9iabEKJq5jsZYup3gibaVCHD5W13XRmR/0");
            fans01.put("nickname", "玄玉");
            fans01.put("country", "中国");
            fans01.put("city", "哈尔滨");
            fans01.put("sex", "1");
            fans01.put("subscribe", "1");
            Map<String, String> fans02 = new HashMap<>();
            fans02.put("headimgurl", "http://a.hiphotos.baidu.com/baike/c0=baike80,5,5,80,26/sign=58560b52ea24b899ca31716a0f6f76f0/9a504fc2d562853595b3f37393ef76c6a7ef6314.jpg");
            fans02.put("nickname", "嬴渠梁");
            fans02.put("country", "秦国");
            fans02.put("city", "栎阳");
            fans02.put("sex", "0");
            fans02.put("subscribe", "0");
            List<Map<String, String>> fansList = new ArrayList<>();
            fansList.add(fans01);
            fansList.add(fans02);
            session.setAttribute("fansList", fansList);
            session.setAttribute("currentMenu", "menu_sys");
            session.setAttribute(Constants.WEB_SESSION_USER, "JadyerIsLogging");
            return new CommonResult();
        }
        return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "用户名或密码不正确");
    }


    /**
     * 直接访问页面资源
     * <p>
     *     可以url传参，比如http://127.0.0.1/view?url=user/userInfo&id=3，则参数id=3会被放到request中
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
}