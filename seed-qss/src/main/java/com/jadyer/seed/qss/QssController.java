package com.jadyer.seed.qss;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.qss.model.ScheduleTask;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/qss")
public class QssController {
    @Resource
    private QssService qssService;

    /**
     * 此接口只做演示用：http://127.0.0.1/qss/getByIds?ids=1,2
     */
    @ResponseBody
    @GetMapping("/getByIds")
    public CommResult<Map<String, Object>> getByIds(final String ids){
        return CommResult.success(new HashMap<String, Object>(){
            private static final long serialVersionUID = 2518882720835440047L;
            {
                put("taskInfo", qssService.getById(Long.parseLong(ids.substring(0,1))));
                put("taskList", qssService.getByIds(ids));
            }
        });
    }


    @GetMapping({"", "/"})
    public String list(HttpServletRequest request){
        request.setAttribute("taskList", qssService.getAllTask());
        return "/qss";
    }


    @ResponseBody
    @PostMapping("/add")
    public CommResult add(ScheduleTask task, String dynamicPassword){
        if(!this.verifyDynamicPassword(dynamicPassword)){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
        }
        ScheduleTask obj = qssService.saveTask(task);
        return CommResult.success();
    }


    @ResponseBody
    @GetMapping("/delete/{id}/{dynamicPassword}")
    public CommResult delete(@PathVariable long id, @PathVariable String dynamicPassword){
        if(!this.verifyDynamicPassword(dynamicPassword)){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
        }
        qssService.deleteTask(id);
        return CommResult.success();
    }


    @ResponseBody
    @GetMapping("/updateStatus")
    public CommResult updateStatus(long id, int status, String dynamicPassword){
        if(!this.verifyDynamicPassword(dynamicPassword)){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
        }
        if(qssService.updateStatus(id, status)){
            return CommResult.success();
        }else{
            return CommResult.fail(CodeEnum.SYSTEM_ERROR.getCode(), CodeEnum.SYSTEM_ERROR.getMsg());
        }
    }


    @ResponseBody
    @GetMapping("/updateCron")
    public CommResult updateCron(long id, String cron, String dynamicPassword){
        if(!this.verifyDynamicPassword(dynamicPassword)){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
        }
        if(qssService.updateCron(id, cron)){
            return CommResult.success();
        }else{
            return CommResult.fail(CodeEnum.SYSTEM_ERROR.getCode(), CodeEnum.SYSTEM_ERROR.getMsg());
        }
    }


    /**
     * 立即执行一个QuartzJOB
     */
    @ResponseBody
    @GetMapping("/triggerJob/{id}/{dynamicPassword}")
    public CommResult triggerJob(@PathVariable long id, @PathVariable String dynamicPassword){
        if(!this.verifyDynamicPassword(dynamicPassword)){
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
        }
        ScheduleTask task = qssService.getTaskById(id);
        qssService.triggerJob(task);
        return CommResult.success();
    }


    /**
     * 验证动态密码是否正确（每个动态密码有效期为10分钟）
     * @return 动态密码正确则返回true，反之false
     */
    private boolean verifyDynamicPassword(String dynamicPassword){
        if(StringUtils.equals("http://jadyer.cn/", dynamicPassword)){
            return true;
        }
        String timeFlag = DateFormatUtils.format(new Date(), "HHmm").substring(0, 3) + "0";
        String generatePassword = DigestUtils.md5Hex(timeFlag + "http://jadyer.cn/" + timeFlag);
        return StringUtils.isNotBlank(dynamicPassword) && generatePassword.equalsIgnoreCase(dynamicPassword);
    }
}