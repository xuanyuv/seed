package com.jadyer.seed.open;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.open.core.OpenMethod;
import com.jadyer.seed.open.model.ReqData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

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
 * 所有业务实现的父类
 * ------------------------------------------------------------------------
 * 其提供了一些公共的实现，若某个appid有个性化实现，继承这里的方法后覆写即可
 * ------------------------------------------------------------------------
 * 具体的业务实现类只有一个约定：类名为“RouterService + {appid}”即可
 * ------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/20 17:57.
 */
@Service
public class RouterService100 {
    /**
     * 文件上传接口
     */
    @OpenMethod(SeedConstants.OPEN_METHOD_boot_file_upload)
    public CommResult<Map<String, Integer>> fileupload(ReqData reqData, HttpServletRequest request) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String partnerApplyNo = reqMap.get("partnerApplyNo");
        if(StringUtils.isBlank(partnerApplyNo)){
            return CommResult.fail(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), "partnerApplyNo is blank");
        }
        //接收并处理上传过来的文件
        MultipartFile fileData = null;
        CommonsMultipartResolver mutilpartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(mutilpartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartFile = (MultipartHttpServletRequest) request;
            fileData = multipartFile.getFile("fileData");
        }
        if(null==fileData || fileData.getSize()==0){
            return CommResult.fail(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), "未传输文件流");
        }
        InputStream is;
        try {
            is = fileData.getInputStream();
        } catch (IOException e) {
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "文件流获取失败-->"+e.getMessage());
        }
        LogUtil.getLogger().info("文档类型：" + fileData.getContentType());
        LogUtil.getLogger().info("文件大小：" + fileData.getSize()); // 2667993=2.54MB=2,667,993字节
        LogUtil.getLogger().info("文件原名：" + fileData.getOriginalFilename());
        try {
            String desktop = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + System.getProperty("file.separator");
            String separator = System.getProperty("file.separator");
            FileUtils.copyInputStreamToFile(is, new File(desktop + separator +fileData.getOriginalFilename()));
        } catch (IOException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "文件流保存失败-->"+e.getMessage(), e);
        }
        return CommResult.success(new HashMap<String, Integer>(){
            private static final long serialVersionUID = 8673982917114045418L;
            {
                put("fileId", 33);
            }
        });
    }


    /**
     * 申请单查询接口
     */
    @OpenMethod(methodName=SeedConstants.OPEN_METHOD_boot_loan_get)
    public CommResult<Map<String, Object>> loanGet(ReqData reqData) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String applyNo = reqMap.get("applyNo");
        String partnerApplyNo = reqMap.get("partnerApplyNo");
        if(StringUtils.isBlank(applyNo) && StringUtils.isBlank(partnerApplyNo)){
            return CommResult.fail(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), "applyNo and partnerApplyNo is blank");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("partnerApplyNo", JadyerUtil.randomNumeric(32));
        resultMap.put("applyNo", JadyerUtil.randomNumeric(32));
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
        return CommResult.success(resultMap);
    }


    /**
     * 申请单协议接口
     */
    @OpenMethod(SeedConstants.OPEN_METHOD_boot_loan_agree)
    public CommResult loanAgree(ReqData reqData, HttpServletResponse response) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String type = reqMap.get("type");
        String applyNo = reqMap.get("applyNo");
        if(StringUtils.isBlank(applyNo) || StringUtils.isBlank(type)){
            return CommResult.fail(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), "applyNo or type is blank");
        }
        if(!"1".equals(type) && !"2".equals(type) && !"3".equals(type)){
            return CommResult.fail(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), "type shoule be 1 or 2 or 3");
        }
        response.setCharacterEncoding(SeedConstants.DEFAULT_CHARSET);
        response.setContentType("text/plain; charset=" + SeedConstants.DEFAULT_CHARSET);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "返回字符串时出错-->"+e.getMessage(), e);
        }
        out.write("<!DOCTYPE html><html><head><meta charset=\"" + SeedConstants.DEFAULT_CHARSET + "\"><title>个人循环信用额度贷款合同</title></head><body><b style=\"line-height:1.5;\">这是个人循环信用额度贷款合同的正文</b></body></html>");
        out.flush();
        out.close();
        return null;
    }


    /**
     * 申请单报表下载
     */
    @OpenMethod(methodName=SeedConstants.OPEN_METHOD_boot_loan_report_download)
    public CommResult loanReportDownload(ReqData reqData, HttpServletResponse response) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String reportType = reqMap.get("reportType");
        String reportSignType = reqMap.get("reportSignType");
        if(!"1".equals(reportType)){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "暂时只能下载昨天放款成功的报表文件");
        }
        if(!"0".equals(reportSignType) && !SeedConstants.OPEN_SIGN_TYPE_md5.equals(reportSignType) && !SeedConstants.OPEN_SIGN_TYPE_hmac.equals(reportSignType)){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "未知的报表文件内容签名类型");
        }
        response.setCharacterEncoding(SeedConstants.DEFAULT_CHARSET);
        response.setContentType("text/plain; charset=" + SeedConstants.DEFAULT_CHARSET);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "返回字符串时出错-->"+e.getMessage(), e);
        }
        out.write("20151210111055`玄玉`2321262015121011113636`800000`12`A`20151209232425`600000`12`A`20151210102030");
        out.flush();
        out.close();
        return null;
    }
}