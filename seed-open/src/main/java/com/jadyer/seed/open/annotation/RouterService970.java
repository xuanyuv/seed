package com.jadyer.seed.open.annotation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.open.model.ReqData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@OpenService
class RouterService970 {
    /**
     * 申请单查询接口
     */
    @OpenMethod(Constants.OPEN_METHOD_boot_loan_get)
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
}