package com.jadyer.seed.open;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.open.core.OpenMethod;
import com.jadyer.seed.open.core.RouterService;
import com.jadyer.seed.open.model.LoanSubmit;
import com.jadyer.seed.open.model.LoanSubmit1101;
import com.jadyer.seed.open.model.ReqData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RouterService770 extends RouterService {
    /**
     * 申请单提交接口
     */
    @OpenMethod(SeedConstants.OPEN_METHOD_boot_loan_submit)
    public CommResult<Map<String, String>> loanSubmit(/*ReqData reqData*/ LoanSubmit loanSubmit) {
        String validMsg;
        // LoanSubmit loanSubmit = JSON.parseObject(reqData.getData(), LoanSubmit.class);
        if("1101".equals(loanSubmit.getProductCode())){
            LoanSubmit1101 loanSubmit1101 = JSON.parseObject(loanSubmit.getData(), LoanSubmit1101.class);
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
     * 申请单查询接口
     */
    @Override
    public CommResult<Map<String, Object>> loanGet(ReqData reqData) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("loanMoney", 1000000);
        resultMap.put("creditMoney", 800000);
        return CommResult.success(resultMap);
    }
}