package com.jadyer.seed.scs;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

/**
 * --------------------------------------------------------------------
 * 复制脚手架时修改点如下
 * 1、pom.xml 中的 artifactId 修改为工程名
 * 2、logback.xml 中的 PROJECT_NAME 修改为 工程名
 * 3、放置业务代码的包名由 com.jadyer.seed 修改为 com.jadyer.工程名
 * --------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2017/3/10 6:00.
 */
@Controller
@RequestMapping(value="/sample")
public class SampleController {
    @Resource
    private ScsHelper scsHelper;

    /**
     * JSP页面点击登录按钮
     */
    @ResponseBody
    @RequestMapping(value="/login")
    public CommonResult jspLogin(String username, String password, String captcha, HttpSession session){
        if(!session.getAttribute("rand").equals(captcha)){
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的验证码");
        }
        LogUtil.getLogger().info("验证码校验-->通过");
        if("jadyer".equals(username) && "xuanyu".equals(password)){
            session.setAttribute(Constants.WEB_SESSION_USER, username);
            return new CommonResult();
        }
        return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "用户名或密码不正确");
    }


    /**
     * 文件下载
     */
    @RequestMapping(value="/file/get")
    public void fileGet(String filePath, HttpServletRequest request, HttpServletResponse response) throws Exception{
        InputStream is;
        String filename;
        if(filePath.endsWith("qrcode.png")){
            filename = "qrcode.png";
            StringBuilder sb = new StringBuilder();
            sb.append(request.getScheme()).append("://").append(request.getServerName());
            if(80!=request.getServerPort() && 443!=request.getServerPort()){
                sb.append(":").append(request.getServerPort());
            }
            sb.append(request.getContextPath());
            sb.append("/img/qrcode.png");
            //这里不能使用URI来创建File，因为URI认的是这个协议：file:///D:/qrcode.jpg
            //filedata = new File(URI.create(sb.toString()));
            is = new URL(sb.toString()).openStream();
        }else{
            filename = FilenameUtils.getName(filePath);
            is = FileUtils.openInputStream(new File(scsHelper.getFilePath(FilenameUtils.getBaseName(filePath).endsWith("w"))+filePath));
        }
        /*
        //也有人像下面这样返回文件流，但大文件会内存溢出
        HttpStatus status = HttpStatus.CREATED;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", FilenameUtils.getName(filePath));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(new File("")), headers, status);
        */
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + new String(filename.getBytes("UTF-8"), "ISO8859-1"));
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        byte[] buff = new byte[1024];
        int len;
        while((len=is.read(buff)) != -1){
            os.write(buff, 0, len);
        }
        os.flush();
        is.close();
        os.close();
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
            out.close();
            return;
        }
        String filePath = scsHelper.buildUploadFilePath(imgData.getOriginalFilename(), false);
        //这样写也可以实现文件的保存：imgData.transferTo(new File(""));
        FileUtils.copyInputStreamToFile(imgData.getInputStream(), new File(filePath));
        out.print("0`" + FilenameUtils.getName(filePath));
        out.flush();
        out.close();
    }


    /**
     * 前台页面通过wangEditor上传图片
     */
    @RequestMapping("/wangEditor/uploadImg")
    void wangEditorUploadImg(String username, MultipartFile minefile, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        if(null==minefile || 0==minefile.getSize()){
            //此时前台wangEditor会自动alert('未上传文件');
            out.print("error|未上传文件");
            out.flush();
            out.close();
            return;
        }
        //文件的名字会被wangEditor重命名为一个长度为16的数字，所以getOriginalFilename得到的并非真实原文件名
        String filePath = scsHelper.buildUploadFilePath(minefile.getOriginalFilename(), true);
        FileUtils.copyInputStreamToFile(minefile.getInputStream(), new File(filePath));
        out.write(request.getContextPath() + "/sample/file/get?filePath=" + FilenameUtils.getName(filePath));
        out.flush();
        out.close();
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
     * <p>
     *     可以url传参，比如http://127.0.0.1/sample/view?url=user/userInfo&id=3，则参数id=3会被放到request中
     * </p>
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