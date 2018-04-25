package com.jadyer.seed.open;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.open.core.OpenMethod;
import com.jadyer.seed.open.model.LoanSubmit;
import com.jadyer.seed.open.model.LoanSubmit1101;
import com.jadyer.seed.open.model.ReqData;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RouterService670 extends RouterService100 {
    /**
     * 申请单提交接口
     */
    @OpenMethod(SeedConstants.OPEN_METHOD_boot_loan_submit)
    public CommonResult loanSubmit(ReqData reqData) {
        Map<String, String> validatorMap;
        LoanSubmit loanSubmit = JSON.parseObject(reqData.getData(), LoanSubmit.class);
        if("1101".equals(loanSubmit.getProductCode())){
            LoanSubmit1101 loanSubmit1101 = JSON.parseObject(reqData.getData(), LoanSubmit1101.class);
            validatorMap = ValidatorUtil.validateToMap(loanSubmit1101);
        }else{
            validatorMap = ValidatorUtil.validateToMap(loanSubmit);
        }
        if(null!=validatorMap && !validatorMap.isEmpty()){
            return new CommonResult(CodeEnum.OPEN_FORM_ILLEGAL, validatorMap);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("applyNo", "2016022316140001");
        return new CommonResult(resultMap);
    }
}