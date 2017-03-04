package com.jadyer.seed.controller;

import com.jadyer.seed.boot.BootProperties;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/7 17:43.
 */
@Controller
@RequestMapping(value="/demo")
//它就相当于@Controller和@ResponseBody注解
//@RestController
public class DemoController {
    //初始化登录用户
    private static final Map<String, String> userInfoMap = Collections.singletonMap("jadyer", DigestUtils.md5Hex("xuanyu"));
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
    @Value("${encrypt.username:jasypt is disable}")
    private String encryptUsername;
    @Value("${encrypt.password:jasypt is disable}")
    private String encryptPassword;
    @Resource
    private BootProperties bootProperties;

    /**
     * 读取配置文件中的属性
     */
    @ResponseBody
    @RequestMapping(value="/properties")
    public Map<String, Object> properties(){
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
     * 測試HTTP-404頁面
     */
    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="no-reason")
    @RequestMapping(value="/notfound")
    void notfound(){}


    /**
     * 測試HTTP-500服務異常頁面，并驗證頁面源碼中是否會打印堆棧軌跡
     */
    @RequestMapping(value="/servererror")
    void servererror(){
        throw new RuntimeException("這是玄玉自定義的異常提示信息");
    }


    /**
     * JSP页面点击登录按钮
     */
    @ResponseBody
    @RequestMapping(value="/jspLogin")
    public CommonResult jspLogin(String username, String password, String captcha, HttpSession session){
        if(!session.getAttribute("rand").equals(captcha)){
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的验证码");
        }
        LogUtil.getAppLogger().info("验证码校验-->通过");
        for(Map.Entry<String, String> entry : userInfoMap.entrySet()){
            if(entry.getKey().equals(username) && entry.getValue().equals(password)){
                session.setAttribute("userinfo", "伪用户");
                return new CommonResult();
            }
        }
        return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "用户名或密码不正确");
    }


    /**
     * Thymeleaf页面点击登录按钮
     */
    @ResponseBody
    @RequestMapping(value="/thymeleafLogin/{username}/{password}")
    public CommonResult thymeleafLogin(@PathVariable String username, @PathVariable String password, HttpSession session){
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
            session.setAttribute("uid", "https://jadyer.github.io/");
            return new CommonResult();
        }
        return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "用户名或密码不正确");
    }


    /**
     * 前台页面通过ajaxfileupload.js实现图片上传
     */
    @RequestMapping("/uploadImg/{userId}")
    void uploadImg(@PathVariable int userId, MultipartFile imgData, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        if(null==imgData || 0==imgData.getSize()){
            out.print("1`请选择文件后上传");
            out.flush();
            return;
        }
        //以下两种方式都能实现文件的保存
        //imgData.transferTo(new File(""));
        //FileUtils.copyInputStreamToFile(imgData.getInputStream(), new File(""));
        String fileExtension = FilenameUtils.getExtension(imgData.getOriginalFilename());
        String newFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + RandomStringUtils.randomNumeric(6);
        String ftpPath = "/img/" + userId + "/" + newFileName + "." + fileExtension;
        out.print("0`" + ftpPath);
        out.flush();
    }


    /**
     * 前台页面通过wangEditor上传图片
     */
    @RequestMapping("/wangEditor/uploadImg")
    void wangEditorUploadImg(String username, MultipartFile minefile, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        if(null==minefile || 0==minefile.getSize()){
            //此时前台wangEditor会自动alert('未上传文件');
            out.print("error|未上传文件");
            out.flush();
            return;
        }
        //文件的名字会被wangEditor重命名为一个长度为16的数字，所以getOriginalFilename得到的并非真实原文件名
        System.out.println("收到username=["+username+"]上传的图片=["+minefile.getOriginalFilename()+"]");
        out.print("http://ww2.sinaimg.cn/small/723dadf5gw1f8vnviyg1zj20cb0bq74o.jpg");
        out.flush();
    }


    /**
     * 前台页面通过wangEditor提交内容
     */
    @ResponseBody
    @RequestMapping("/wangEditor/submit")
    CommonResult wangEditorSubmit(String mpno, String mpname) {
        System.out.println("收到wangEditor的内容，mpno=["+mpno+"]，mpname=["+mpname+"]");
        return new CommonResult();
    }


    /**
     * 直接访问页面资源
     * @see 可以在URL上传参
     * @see 比如http://127.0.0.1/demo/view?url=user/userInfo&id=3,则参数id=3会被放到request中
     * @create Nov 3, 2015 5:50:54 PM
     * @author 玄玉<https://jadyer.github.io/>
     */
    @RequestMapping(value="/view", method= RequestMethod.GET)
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