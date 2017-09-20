package com.jadyer.seed.open;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.open.core.annotation.OpenMethod;
import com.jadyer.seed.open.core.annotation.OpenService;
import com.jadyer.seed.open.model.ReqData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有业务实现的父类
 * ------------------------------------------------------------------------
 * 其提供了一些公共的实现，若某个appid有个性化实现，继承这里的方法后覆写即可
 * ------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/20 17:57.
 */
@OpenService
class RouterService100 {
    /**
     * 申请单查询接口
     */
    @OpenMethod(methodName=Constants.OPEN_METHOD_boot_loan_get)
    CommonResult loanGet(ReqData reqData) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String applyNo = reqMap.get("applyNo");
        String partnerApplyNo = reqMap.get("partnerApplyNo");
        if(StringUtils.isBlank(applyNo) && StringUtils.isBlank(partnerApplyNo)){
            return new CommonResult(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), "applyNo and partnerApplyNo is blank");
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
     * 申请单报表下载
     */
    @OpenMethod(methodName=Constants.OPEN_METHOD_boot_loan_report_download)
    Object loanReportDownload(ReqData reqData, HttpServletResponse response) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String reportType = reqMap.get("reportType");
        String reportSignType = reqMap.get("reportSignType");
        if(!"1".equals(reportType)){
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "暂时只能下载昨天放款成功的报表文件");
        }
        if(!"0".equals(reportSignType) && !Constants.OPEN_SIGN_TYPE_md5.equals(reportSignType) && !Constants.OPEN_SIGN_TYPE_hmac.equals(reportSignType)){
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "未知的报表文件内容签名类型");
        }
        response.setCharacterEncoding(Constants.OPEN_CHARSET_UTF8);
        response.setContentType("text/plain; charset=" + Constants.OPEN_CHARSET_UTF8);
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