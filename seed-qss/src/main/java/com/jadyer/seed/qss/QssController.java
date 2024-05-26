package com.jadyer.seed.qss;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.qss.helper.JobComponent;
import com.jadyer.seed.qss.model.ScheduleSummary;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskDaoJdbc;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/qss")
public class QssController {
    @Resource
    private QssService qssService;
    @Resource
    private JobComponent jobComponent;
    @Resource
    private ScheduleTaskDaoJdbc scheduleTaskDaoJdbc;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    /**
     * 此接口只做演示用：http://127.0.0.1:8008/qss/getByIds?ids=1`2
     */
    @RequestMapping("/getByIds")
    CommResult<Map<String, Object>> getByIds(final String ids){
        // 组装过滤条件
        List<Long> idList = new ArrayList<>();
        String[] idstr = ids.split("`");
        for(String obj : idstr){
            idList.add(Long.parseLong(obj));
        }
        // 使用@Query查询的方式
        List<ScheduleTask> taskList = new ArrayList<>();
        Object[] objs = scheduleTaskRepository.selectByIds(idList);
        for(Object obj : objs){
            ScheduleTask task = new ScheduleTask();
            task.setName(((Object[])obj)[0].toString());
            task.setUrl(((Object[])obj)[1].toString());
            taskList.add(task);
        }
        taskList.forEach(obj -> System.out.println("11--查询到name=[" + obj.getName() + "]，url=[" + obj.getUrl() +"]"));
        // 使用接口作为返回值实现多表查询
        List<ScheduleSummary> scheduleSummaryList = scheduleTaskRepository.selectByIdList(idList);
        scheduleSummaryList.forEach(obj -> System.out.println("22--查询到name=[" + obj.getName() + "]，url=[" + obj.getUrl() +"]"));
        // 返回
        return CommResult.success(new HashMap<String, Object>(){
            private static final long serialVersionUID = 2518882720835440047L;
            {
                put("allJob", jobComponent.getAllJob());
                put("allRunningJob", jobComponent.getAllRunningJob());
                put("nextFireTimes", jobComponent.getNextFireTimes("0 */1 * * * ?", 5));
            }
        });
    }


    @PostMapping("/add")
    public CommResult<Long> add(ScheduleTask task, String dynamicPassword){
        this.verifyDynamicPassword(dynamicPassword);
        ScheduleTask obj = qssService.saveTask(task);
        return CommResult.success(obj.getId());
    }


    @GetMapping("/delete/{id}/{dynamicPassword}")
    public CommResult<Void> delete(@PathVariable long id, @PathVariable String dynamicPassword){
        this.verifyDynamicPassword(dynamicPassword);
        qssService.deleteTask(id);
        return CommResult.success();
    }


    @GetMapping("/updateCron")
    public CommResult<Void> updateCron(long id, String cron, String dynamicPassword){
        this.verifyDynamicPassword(dynamicPassword);
        qssService.updateCron(id, cron);
        return CommResult.success();
    }


    @GetMapping("/updateStatus")
    public CommResult<Void> updateStatus(long id, int status, String dynamicPassword){
        this.verifyDynamicPassword(dynamicPassword);
        qssService.updateStatus(id, status);
        return CommResult.success();
    }


    /**
     * 立即执行一个QuartzJOB
     */
    @GetMapping("/triggerJob/{id}/{dynamicPassword}")
    public CommResult<Void> triggerJob(@PathVariable long id, @PathVariable String dynamicPassword){
        this.verifyDynamicPassword(dynamicPassword);
        jobComponent.triggerJob(qssService.get(id));
        return CommResult.success();
    }


    /**
     * 验证动态密码是否正确（每个动态密码有效期为10分钟）
     */
    private void verifyDynamicPassword(String dynamicPassword){
        if(StringUtils.isBlank(dynamicPassword)){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不能为空");
        }
        if(!dynamicPassword.contains("jadyer")){
            String timeFlag = DateFormatUtils.format(new Date(), "HHmm").substring(0, 3) + "0";
            String generatePassword = DigestUtils.md5Hex(timeFlag + "https://jadyer.cn/" + timeFlag);
            if(!generatePassword.equals(dynamicPassword)){
                throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
            }
        }
    }
}