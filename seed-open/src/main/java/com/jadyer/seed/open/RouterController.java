package com.jadyer.seed.open;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.open.model.ReqData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2016/5/8 19:27.
 */
@Controller
@RequestMapping("/open/router")
public class RouterController {
    @Resource
    private RouterService routerService;

    @ResponseBody
    @RequestMapping(value="/rest", method={RequestMethod.GET, RequestMethod.POST})
    public Object rest(ReqData reqData, HttpServletRequest request, HttpServletResponse response){
        if(Constants.OPEN_METHOD_boot_file_upload.equals(reqData.getMethod())) {
            return routerService.fileupload(reqData, request);
        }
        if(Constants.OPEN_METHOD_boot_loan_submit.equals(reqData.getMethod())){
            return routerService.loanSubmit(reqData);
        }
        if(Constants.OPEN_METHOD_boot_loan_get.equals(reqData.getMethod())){
            return routerService.loanGet(reqData);
        }
        if(Constants.OPEN_METHOD_boot_loan_report_download.equals(reqData.getMethod())){
            return routerService.loanReportDownload(reqData, response);
        }
        if(Constants.OPEN_METHOD_boot_loan_agree.equals(reqData.getMethod())){
            return routerService.loanAgree(reqData, response);
        }
        return new CommonResult(CodeEnum.OPEN_UNKNOWN_METHOD.getCode(), CodeEnum.OPEN_UNKNOWN_METHOD.getMsg()+"-->["+reqData.getMethod()+"]");
    }


    @RequestMapping(value="/rest/h5", method={RequestMethod.GET, RequestMethod.POST})
    public Object resth5(ReqData reqData){
        if(Constants.OPEN_METHOD_boot_apidoc_h5.equals(reqData.getMethod())){
            return routerService.apidocH5(reqData);
        }
        throw new SeedException(CodeEnum.OPEN_UNKNOWN_METHOD.getCode(), CodeEnum.OPEN_UNKNOWN_METHOD.getMsg()+"-->["+reqData.getMethod()+"]");
    }
}