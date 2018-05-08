package com.jadyer.seed.seedoc;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.RequestUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

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

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/11/15 15:59.
 */
@Controller
@RequestMapping("/seedoc")
public class SeedocController {
    @Resource
    private SeedocHelper seedocHelper;

    @GetMapping("/logout")
    String logout(HttpSession session){
        session.removeAttribute(SeedConstants.WEB_SESSION_USER);
        return InternalResourceViewResolver.REDIRECT_URL_PREFIX;
    }


    /**
     * 文件下载
     */
    @RequestMapping(value="/file/get")
    public void fileGet(String path, HttpServletRequest request, HttpServletResponse response) throws Exception{
        InputStream is;
        String filename;
        if(path.endsWith("qrcode.jpg")){
            filename = "qrcode.jpg";
            //这里不能使用URI来创建File，因为URI认的是这个协议：file:///D:/qrcode.jpg
            //filedata = new File(URI.create(JadyerUtil.getFullContextPath(request) + "/img/qrcode.jpg"));
            is = new URL(RequestUtil.getFullContextPath(request) + "/img/qrcode.jpg").openStream();
        }else{
            filename = FilenameUtils.getName(path);
            is = FileUtils.openInputStream(new File(seedocHelper.getFilePath(FilenameUtils.getBaseName(path).endsWith("w"))+path));
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
        response.setHeader("Content-disposition", "attachment; filename=" + new String(filename.getBytes(SeedConstants.DEFAULT_CHARSET), "ISO8859-1"));
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        byte[] buff = new byte[1024];
        int len;
        while((len=is.read(buff)) > -1){
            os.write(buff, 0, len);
        }
        os.flush();
        is.close();
        os.close();
    }


    /**
     * 前台页面通过ajaxfileupload.js实现图片上传
     */
    @RequestMapping("/uploadImg")
    void uploadImg(MultipartFile imgData, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        if(null==imgData || 0==imgData.getSize()){
            out.print("1`请选择文件后上传");
            out.flush();
            out.close();
            return;
        }
        String filePath = seedocHelper.buildUploadFilePath(imgData.getOriginalFilename(), false);
        //这样写也可以实现文件的保存：imgData.transferTo(new File(""));
        FileUtils.copyInputStreamToFile(imgData.getInputStream(), new File(filePath));
        out.print("0`" + FilenameUtils.getName(filePath));
        out.flush();
        out.close();
    }
}