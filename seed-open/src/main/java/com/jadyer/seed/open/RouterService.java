package com.jadyer.seed.open;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.open.constant.OpenCodeEnum;
import com.jadyer.seed.open.constant.OpenConstant;
import com.jadyer.seed.open.model.LoanSubmit;
import com.jadyer.seed.open.model.LoanSubmit1101;
import com.jadyer.seed.open.model.ReqData;
import com.jadyer.seed.open.util.ValidatorUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/10 17:59.
 */
@Service
class RouterService {
    /**
     * 文件上传接口
     */
    CommonResult fileupload(ReqData reqData, HttpServletRequest request) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String partnerApplyNo = reqMap.get("partnerApplyNo");
        if(StringUtils.isBlank(partnerApplyNo)){
            return new CommonResult(OpenCodeEnum.FORM_ILLEGAL.getCode(), "partnerApplyNo is blank");
        }
        //接收并处理上传过来的文件
        MultipartFile fileData = null;
        CommonsMultipartResolver mutilpartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(mutilpartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartFile = (MultipartHttpServletRequest) request;
            fileData = multipartFile.getFile("fileData");
        }
        if(null==fileData || fileData.getSize()==0){
            return new CommonResult(OpenCodeEnum.FORM_ILLEGAL.getCode(), "未传输文件流");
        }
        InputStream is;
        try {
            is = fileData.getInputStream();
        } catch (IOException e) {
            return new CommonResult(OpenCodeEnum.SYSTEM_BUSY.getCode(), "文件流获取失败-->"+e.getMessage());
        }
        LogUtil.getAppLogger().info("文档类型：" + fileData.getContentType());
        LogUtil.getAppLogger().info("文件大小：" + fileData.getSize()); // 2667993=2.54MB=2,667,993字节
        LogUtil.getAppLogger().info("文件原名：" + fileData.getOriginalFilename());
        try {
            String desktop = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + System.getProperty("file.separator");
            String separator = System.getProperty("file.separator");
            FileUtils.copyInputStreamToFile(is, new File(desktop + separator +fileData.getOriginalFilename()));
        } catch (IOException e) {
            throw new SeedException(OpenCodeEnum.SYSTEM_BUSY.getCode(), "文件流保存失败-->"+e.getMessage(), e);
        }
        return new CommonResult(new HashMap<String, Integer>(){
            private static final long serialVersionUID = 8673982917114045418L;
            {
                put("fileId", 33);
            }
        });
    }


    /**
     * 申请单提交接口
     */
    CommonResult loanSubmit(ReqData reqData) {
        Map<String, String> validatorMap;
        LoanSubmit loanSubmit = JSON.parseObject(reqData.getData(), LoanSubmit.class);
        if("1101".equals(loanSubmit.getProductCode())){
            LoanSubmit1101 loanSubmit1101 = JSON.parseObject(reqData.getData(), LoanSubmit1101.class);
            validatorMap = ValidatorUtil.validateToMap(loanSubmit1101);
        }else{
            validatorMap = ValidatorUtil.validateToMap(loanSubmit);
        }
        if(null!=validatorMap && !validatorMap.isEmpty()){
            return new CommonResult(OpenCodeEnum.FORM_ILLEGAL.getCode(), OpenCodeEnum.FORM_ILLEGAL.getMsg(), validatorMap);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("applyNo", "2016022316140001");
        return new CommonResult(resultMap);
    }


    /**
     * 申请单查询接口
     */
    CommonResult loanGet(ReqData reqData) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String applyNo = reqMap.get("applyNo");
        String partnerApplyNo = reqMap.get("partnerApplyNo");
        if(StringUtils.isBlank(applyNo) && StringUtils.isBlank(partnerApplyNo)){
            return new CommonResult(OpenCodeEnum.FORM_ILLEGAL.getCode(), "applyNo and partnerApplyNo is blank");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("partnerApplyNo", RandomStringUtils.randomNumeric(32));
        resultMap.put("applyNo", RandomStringUtils.randomNumeric(32));
        resultMap.put("applyStatus", "A");
        resultMap.put("applyTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        resultMap.put("approveTime", "");
        resultMap.put("approveRemark", "");
        resultMap.put("loanTerm", 12);
        resultMap.put("loanMoney", 1000000);
        resultMap.put("creditMoney", 800000);
        resultMap.put("creditTerm", 12);
        resultMap.put("userName", "玄玉");
        resultMap.put("userPhone", "13600000000");
        return new CommonResult(resultMap);
    }


    /**
     * 申请单协议接口
     */
    Object loanAgree(ReqData reqData, HttpServletResponse response) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String type = reqMap.get("type");
        String applyNo = reqMap.get("applyNo");
        if(StringUtils.isBlank(applyNo) || StringUtils.isBlank(type)){
            return new CommonResult(OpenCodeEnum.FORM_ILLEGAL.getCode(), "applyNo or type is blank");
        }
        if(!"1".equals(type) && !"2".equals(type) && !"3".equals(type)){
            return new CommonResult(OpenCodeEnum.FORM_ILLEGAL.getCode(), "type shoule be 1 or 2 or 3");
        }
        response.setCharacterEncoding(OpenConstant.CHARSET_UTF8);
        response.setContentType("text/plain; charset=" + OpenConstant.CHARSET_UTF8);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new SeedException(OpenCodeEnum.SYSTEM_BUSY.getCode(), "返回字符串时出错-->"+e.getMessage(), e);
        }
        out.write("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>个人循环信用额度贷款合同</title></head><body><b style=\"line-height:1.5;\">这是个人循环信用额度贷款合同的正文</b></body></html>");
        out.flush();
        out.close();
        return null;
    }


    /**
     * 申请单报表下载
     */
    Object loanReportDownload(ReqData reqData, HttpServletResponse response) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String reportType = reqMap.get("reportType");
        String reportSignType = reqMap.get("reportSignType");
        if(!"1".equals(reportType)){
            return new CommonResult(OpenCodeEnum.SYSTEM_BUSY.getCode(), "暂时只能下载昨天放款成功的报表文件");
        }
        if(!"0".equals(reportSignType) && !OpenConstant.SIGN_TYPE_md5.equals(reportSignType) && !OpenConstant.SIGN_TYPE_hmac.equals(reportSignType)){
            return new CommonResult(OpenCodeEnum.SYSTEM_BUSY.getCode(), "未知的报表文件内容签名类型");
        }
        response.setCharacterEncoding(OpenConstant.CHARSET_UTF8);
        response.setContentType("text/plain; charset=" + OpenConstant.CHARSET_UTF8);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new SeedException(OpenCodeEnum.SYSTEM_BUSY.getCode(), "返回字符串时出错-->"+e.getMessage(), e);
        }
        out.write("20151210111055`玄玉`2321262015121011113636`800000`12`A`20151209232425`600000`12`A`20151210102030");
        out.flush();
        out.close();
        return null;
    }


    /**
     * 开放平台接口文档
     */
    String apidocH5(ReqData reqData) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String method = reqMap.get("method");
        System.out.println("当前访问的接口名称-->[" + method + "]");
        //由于启用了JSP，这时会跳转到/WEB-INF/jsp/user/info.jsp
        //return "user/info";
        return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "/apidoc/index.jsp";
    }
}