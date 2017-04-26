package com.jadyer.seed.qss;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
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

@Controller
@RequestMapping("/qss")
public class QssController {
	@Resource
	private QssService qssService;

	/**
	 * 这里只做演示用：http://127.0.0.1/qss/getByIds?ids=1,2
	 */
	@ResponseBody
	@GetMapping("/getByIds")
	public CommonResult getByIds(final String ids){
		return new CommonResult(new HashMap<String, Object>(){
			private static final long serialVersionUID = 2518882720835440047L;
			{
				put("taskInfo", qssService.getById(Long.parseLong(ids.substring(0,1))));
				put("taskList", qssService.getByIds(ids));
			}
		});
	}

	
	@GetMapping("/list")
	public String list(HttpServletRequest request){
		request.setAttribute("taskList", qssService.getAllTask());
		return "qss";
	}


	@ResponseBody
	@PostMapping("/add")
	public CommonResult add(ScheduleTask task, String dynamicPassword){
		if(!this.verifyDynamicPassword(dynamicPassword)){
			return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
		}
		ScheduleTask obj = qssService.saveTask(task);
		return new CommonResult(CodeEnum.SUCCESS.getCode(), String.valueOf(obj.getId()));
	}


	@ResponseBody
	@GetMapping("/delete/{id}/{dynamicPassword}")
	public CommonResult delete(@PathVariable long id, @PathVariable String dynamicPassword){
		if(!this.verifyDynamicPassword(dynamicPassword)){
			return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
		}
		qssService.deleteTask(id);
		return new CommonResult();
	}


	@ResponseBody
	@GetMapping("/updateStatus")
	public CommonResult updateStatus(long id, int status, String dynamicPassword){
		if(!this.verifyDynamicPassword(dynamicPassword)){
			return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
		}
		if(qssService.updateStatus(id, status)){
			return new CommonResult();
		}else{
			return new CommonResult(CodeEnum.SYSTEM_ERROR);
		}
	}


	@ResponseBody
	@GetMapping("/updateCron")
	public CommonResult updateCron(long id, String cron, String dynamicPassword){
		if(!this.verifyDynamicPassword(dynamicPassword)){
			return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
		}
		if(qssService.updateCron(id, cron)){
			return new CommonResult();
		}else{
			return new CommonResult(CodeEnum.SYSTEM_ERROR);
		}
	}


	/**
	 * 立即执行一个QuartzJOB
	 */
	@ResponseBody
	@GetMapping("/triggerJob/{id}/{dynamicPassword}")
	public CommonResult triggerJob(@PathVariable long id, @PathVariable String dynamicPassword){
		if(!this.verifyDynamicPassword(dynamicPassword)){
			return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "动态密码不正确");
		}
		ScheduleTask task = qssService.getTaskById(id);
		qssService.triggerJob(task);
		return new CommonResult();
	}


	/**
	 * 验证动态密码是否正确（每个动态密码有效期为10分钟）
	 * @return 动态密码正确则返回true，反之false
	 */
	private boolean verifyDynamicPassword(String dynamicPassword){
		String timeFlag = DateFormatUtils.format(new Date(), "HHmm").substring(0, 3) + "0";
		String generatePassword = DigestUtils.md5Hex(timeFlag + "https://jadyer.github.io/" + timeFlag);
		return StringUtils.isNotBlank(dynamicPassword) && generatePassword.equalsIgnoreCase(dynamicPassword);
	}
}