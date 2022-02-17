package com.jadyer.seed.controller;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.boot.BootProperties;
import com.jadyer.seed.comm.annotation.ActionEnum;
import com.jadyer.seed.comm.annotation.SeedLog;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.RequestUtil;
import com.jadyer.seed.controller.rabbitmq.UserMsg;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2016/5/7 17:43.
 */
@RestController
public class DemoController {
    //若配置文件中未找到该属性，则为其赋默认值为冒号后面的字符串
    @Value("${user.blog:https://jadyer.cn/}")
    private String blog;
    @Value("${user.weight}")
    private Long weight;
    @Value("${user.height}")
    private Integer height;
    @Value("${user.sex}")
    private Integer sex;
    @Value("${user.age}")
    private Integer age;
    @Value("${encrypt.username:Jasypt未启用}")
    private String encryptUsername;
    @Value("${encrypt.password:Jasypt未启用}")
    private String encryptPassword;
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private BootProperties bootProperties;

    public void propLockFail(Integer id, UserMsg userMsg){
        System.out.println("加锁失败，收到回调-->>id=" + id + ", userMsg=" + ReflectionToStringBuilder.toString(userMsg));
    }


    /**
     * 读取配置文件中的属性
     */
    @GetMapping("/prop")
    //@SeedLock(key="#userMsg.name", appname="seedboot", fallbackMethod="propLockFail")
    @SeedLog(action= ActionEnum.LIST, value="读取配置文件中的属性")
    public CommResult<Map<String, Object>> prop(Integer id, UserMsg userMsg){
        Map<String, Object> map = new HashMap<>(13);
        map.put("weight", this.weight);
        map.put("height", this.height);
        map.put("sex", this.sex);
        map.put("age", this.age);
        map.put("blog", this.blog);
        map.put("hello", "Hello Boot");
        map.put("PID", RequestUtil.getPID());
        map.put("encryptUsername", this.encryptUsername);
        map.put("encryptPassword", this.encryptPassword);
        map.put("packages", this.bootProperties.getPackages());
        map.put("secretName", this.bootProperties.getSecretName());
        map.put("detailInfo", this.bootProperties.getDetailInfo());
        map.put("addressList", this.bootProperties.getAddressList());
        return CommResult.success(map);
    }


    /**
     * 动态修改日志级别
     * @param name  日志端点，可传logback-boot.xml里面配置的<logger name=""/>、或者com.jadyer、或者com.jadyer.seed
     * @param level 修改后的日志级别，可传info、inFO、debug、error、ErrOR等等
     */
    @GetMapping("/loglevel/{name}/{level}")
    public CommResult loglevel(@PathVariable String name, @PathVariable String level){
        LogUtil.getLogger().info("这是info级别的日志");
        LogUtil.getLogger().debug("这是debug级别的日志");
        LogUtil.getLogger().error("这是error级别的日志");
        String reqData = JSON.toJSONString(new HashMap<String, String>(){
            private static final long serialVersionUID = 2268259870612640575L;
            {
                put("configuredLevel", level);
            }
        });
        HTTPUtil.post("http://127.0.0.1/boot/loggers/"+name, reqData, "application/json; charset=UTF-8");
        LogUtil.getLogger().info("这是info级别的日志");
        LogUtil.getLogger().debug("这是debug级别的日志");
        LogUtil.getLogger().error("这是error级别的日志");
        return CommResult.success();
    }


    @GetMapping("/mail")
    public CommResult mail(){
        //发送一封简单的邮件
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(this.mailFrom);
        simpleMailMessage.setTo("jadyer@yeah.net");
        simpleMailMessage.setSubject("下午开会准时参加");
        simpleMailMessage.setText("15点26楼会议室");
        this.javaMailSender.send(simpleMailMessage);
        //发送一封复杂的邮件
        //注意addInline()里面的"huiyi"要与"cid:huiyi"一致
        //注意addAttachment()方法用于添加附件
        try{
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(this.mailFrom);
            helper.setTo("jadyer@yeah.net");
            helper.setSubject("请查收会议纪要");
            helper.setText("<html><body><span style='color:#F00'>会议截图如下，完整图片见附件。</span><br><img src=\\\"cid:huiyi\\\" ></body></html>", true);
            helper.addInline("huiyi", new FileSystemResource(new File("E:\\Jadyer\\Stripes.jpg")));
            helper.addAttachment("会议纪要完整图片.jpg", new FileSystemResource(new File("E:\\Jadyer\\Fedora13.jpg")));
            this.javaMailSender.send(mimeMessage);
        }catch(MessagingException e){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "邮件发送失败，堆栈轨迹如下："+ JadyerUtil.extractStackTrace(e));
        }
        return CommResult.success();
    }
}