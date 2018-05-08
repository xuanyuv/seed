package com.jadyer.seed.open;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.open.core.OpenMethod;
import com.jadyer.seed.open.model.LoanSubmit;
import com.jadyer.seed.open.model.LoanSubmit1101;
import com.jadyer.seed.open.model.ReqData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RouterService870 extends RouterService100 {
    /**
     * 申请单提交接口
     */
    @OpenMethod(SeedConstants.OPEN_METHOD_boot_loan_submit)
    public CommResult<Map<String, String>> loanSubmit(ReqData reqData) {
        String validMsg;
        LoanSubmit loanSubmit = JSON.parseObject(reqData.getData(), LoanSubmit.class);
        if("1101".equals(loanSubmit.getProductCode())){
            LoanSubmit1101 loanSubmit1101 = JSON.parseObject(reqData.getData(), LoanSubmit1101.class);
            validMsg = ValidatorUtil.validate(loanSubmit1101);
        }else{
            validMsg = ValidatorUtil.validate(loanSubmit);
        }
        if(StringUtils.isNotBlank(validMsg)){
            return CommResult.fail(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), validMsg);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("applyNo", "2016022316140001");
        return CommResult.success(resultMap);
    }


    /**
     * 开放平台接口文档
     */
    @OpenMethod(SeedConstants.OPEN_METHOD_boot_apidoc_h5)
    public String apidocH5(ReqData reqData) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String method = reqMap.get("method");
        System.out.println("当前访问的接口名称-->[" + method + "]");
        return "/apidoc";
    }
}