package com.jadyer.seed.qss;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.qss.helper.JobDisallowConcurrentFactory;
import com.jadyer.seed.qss.helper.JobFactory;
import com.jadyer.seed.qss.model.ScheduleSummary;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskDaoJdbc;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class QssService {
	/**
	 * 注入Spring管理的Scheduler
	 * ----------------------------------------------------------------------------------------------------
	 * 1.原本应该注入applicationContext.xml配置的org.springframework.scheduling.quartz.SchedulerFactoryBean
	 * 2.由于SchedulerFactoryBean是一个工厂Bean，得到的不是它本身，而是它负责创建的org.quartz.impl.StdScheduler
	 *   所以就要注意：在使用注解注入SchedulerFactoryBean的时候，要通过类型来注入，否则会报告类似下面的异常
	 *   Bean named 'schedulerFactoryBean' must be of type [org.springframework.scheduling.quartz.SchedulerFactoryBean], but was actually of type [org.quartz.impl.StdScheduler]
	 * 3.在查看SchedulerFactoryBean源码后发现，它的getObject()方法是返回的Scheduler对象
	 *   既然如此，我们就不必注入SchedulerFactoryBean再调用getScheduler()这么麻烦了，可以直接声明Scheduler对象
	 *   这里涉及到getBean("bean")和getBean("&bean")的区别
	 * ----------------------------------------------------------------------------------------------------
	 * FactoryBean源代码分析
	 * 如果bean实现了FactoryBean接口，那么BeanFactory将把它作为一个bean工厂，而不是直接作为普通bean
	 * 正常情况下，BeanFactory的getBean("bean")返回FactoryBean生产的bean实例，也就是getObject()里面的东西
	 * 如果要返回FactoryBean本身的实例，需调用getBean("&bean")
	 * ----------------------------------------------------------------------------------------------------
	 */
	@Resource
	private Scheduler scheduler;

	@Resource
	private ScheduleTaskRepository scheduleTaskRepository;

	@Resource
	private ScheduleTaskDaoJdbc scheduleTaskDaoJdbc;


	@PostConstruct
	public void init() throws Exception {
		List<ScheduleTask> taskList = this.getAllTask();
		for(ScheduleTask task : taskList){
			this.addJob(task);
		}
	}


	ScheduleTask getById(long id){
		return scheduleTaskDaoJdbc.getById(id);
	}


	List<ScheduleTask> getByIds(String ids) {
		List<String> idstr = Arrays.asList(ids.split(","));
		List<Long> idList = new ArrayList<>();
		for(String obj : idstr){
			idList.add(Long.parseLong(obj));
		}
		//使用@Query查询的方式
		List<ScheduleTask> taskList = new ArrayList<>();
		Object[] objs = scheduleTaskRepository.getByIds(idList);
		for(Object obj : objs){
			ScheduleTask task = new ScheduleTask();
			task.setName(((Object[])obj)[0].toString());
			task.setUrl(((Object[])obj)[1].toString());
			taskList.add(task);
		}
		for(ScheduleTask obj : taskList){
			System.out.println("11--查询到name=[" + obj.getName() + "]，url=[" + obj.getUrl() +"]");
		}
		//使用接口作为返回值实现多表查询
		List<ScheduleSummary> scheduleSummaryList = scheduleTaskRepository.findByIds(idList);
		for(ScheduleSummary obj : scheduleSummaryList){
			System.out.println("22--查询到name=[" + obj.getName() + "]，url=[" + obj.getUrl() +"]");
		}
		//使用统一组件查询的方式
		Condition<ScheduleTask> query = Condition.<ScheduleTask>and().in("id", idList);
		return scheduleTaskRepository.findAll(query);
	}


	/**
	 * 获取数据库中的所有任务
	 */
	List<ScheduleTask> getAllTask(){
		//return scheduleTaskDao.findAll();
		List<ScheduleTask> jobList = this.getAllJob();
		List<ScheduleTask> taskList = scheduleTaskRepository.findAll();
		for(ScheduleTask task : taskList){
			for(ScheduleTask job : jobList){
				if(job.getName().equals(task.getName())){
					task.setNextFireTime(job.getNextFireTime());
					task.setPreviousFireTime(job.getPreviousFireTime());
					break;
				}
			}
		}
		return taskList;
	}


	/**
	 * 新增任务到数据库
	 */
	ScheduleTask saveTask(ScheduleTask task){
		if(!CronExpression.isValidExpression(task.getCron())){
			throw new IllegalArgumentException("CronExpression不正确");
		}
		return scheduleTaskRepository.save(task);
	}
	
	
	/**
	 * 移除QuartzJob和数据库中的任务
	 */
	void deleteTask(long taskId){
		ScheduleTask task = this.getTaskById(taskId);
		this.deleteJob(task);
		scheduleTaskRepository.delete(taskId);
	}


	/**
	 * 从数据库中查询指定的任务信息
	 */
	ScheduleTask getTaskById(long taskId) {
		return scheduleTaskRepository.findOne(taskId);
	}


	/**
	 * 更改QuartzJob和数据库任务状态
	 */
	boolean updateStatus(long taskId, int status){
		ScheduleTask task = this.getTaskById(taskId);
		task.setStatus(status);
		if(ScheduleTask.STATUS_NOT_RUNNING == status){
			this.deleteJob(task);
		}else if(ScheduleTask.STATUS_RUNNING == status){
			this.addJob(task);
		}else if(ScheduleTask.STATUS_PAUSE == status){
			this.pauseJob(task);
		}else if(ScheduleTask.STATUS_RESUME == status){
			this.resumeJob(task);
		}
		return 1==scheduleTaskRepository.updateStatusById(status, taskId);
	}


	/**
	 * 更新CronExpression
	 */
	boolean updateCron(long taskId, String cron){
		if(!CronExpression.isValidExpression(cron)){
			throw new IllegalArgumentException("CronExpression不正确");
		}
		ScheduleTask task = this.getTaskById(taskId);
		task.setCron(cron);
		if(ScheduleTask.STATUS_RUNNING == task.getStatus()){
			this.updateJobCron(task);
		}
		return 1==scheduleTaskRepository.updateCronById(cron, taskId);
	}
	
	
	/**
	 * 获取所有计划中的QuartzJob列表
	 * Trigger各状态说明
	 * None------Trigger已经完成,且不会再执行,或者找不到该触发器,或者Trigger已被删除
	 * NORMAL----正常状态
	 * PAUSED----暂停状态
	 * COMPLETE--触发器完成,但任务可能还正在执行中
	 * BLOCKED---线程阻塞状态
	 * ERROR-----出现错误
	 */
	private List<ScheduleTask> getAllJob(){
		try{
			List<ScheduleTask> taskList = new ArrayList<>();
			Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
			for(JobKey jobKey : jobKeys){
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
				for(Trigger trigger : triggers){
					ScheduleTask task = new ScheduleTask();
					task.setName(jobKey.getName());
					//task.setGroup(jobKey.getGroup());
					//task.setDesc("触发器[" + trigger.getKey() + "]");
					//task.setStartTime(trigger.getStartTime());             //开始时间
					//task.setEndTime(trigger.getEndTime());                 //结束时间
					task.setNextFireTime(trigger.getNextFireTime());         //下次触发时间
					task.setPreviousFireTime(trigger.getPreviousFireTime()); //上次触发时间
					Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
					task.setStatus("N".equals(triggerState.name()) ? 0 : 1);
					if(trigger instanceof CronTrigger){
						task.setCron(((CronTrigger)trigger).getCronExpression());
					}
					taskList.add(task);
				}
			}
			return taskList;
		}catch(SchedulerException e){
			throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "获取所有计划中的QuartzJob列表失败", e);
		}
	}


//	/**
//	 * 获取所有正在运行的QuartzJob
//	 */
//	public List<ScheduleTask> getAllRunningJob(){
//		try{
//			List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
//			List<ScheduleTask> taskList = new ArrayList<ScheduleTask>(executingJobs.size());
//			for(JobExecutionContext obj : executingJobs){
//				Trigger trigger = obj.getTrigger();
//				JobKey jobKey = obj.getJobDetail().getKey();
//				ScheduleTask task = new ScheduleTask();
//				task.setName(jobKey.getName());
//				//task.setGroup(jobKey.getGroup());
//				//task.setDesc("触发器[" + trigger.getKey() + "]");
//				//task.setStartTime(trigger.getStartTime());             //开始时间
//				//task.setEndTime(trigger.getEndTime());                 //结束时间
//				task.setNextFireTime(trigger.getNextFireTime());         //下次触发时间
//				task.setPreviousFireTime(trigger.getPreviousFireTime()); //上次触发时间
//				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
//				task.setStatus(triggerState.name());
//				if(trigger instanceof CronTrigger){
//					task.setCron(((CronTrigger)trigger).getCronExpression());
//				}
//				taskList.add(task);
//			}
//			return taskList;
//		}catch(SchedulerException e){
//			throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "获取所有正在运行的QuartzJob失败", e);
//		}
//	}
	

	/**
	 * 添加QuartzJob
	 */
	private void addJob(ScheduleTask task){
		if(null==task || (ScheduleTask.STATUS_RUNNING != task.getStatus() && ScheduleTask.STATUS_RESUME != task.getStatus())){
			return;
		}
		TriggerKey triggerKey = TriggerKey.triggerKey(task.getName());
		try{
			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
			if(null == trigger){
				//Trigger不存在就创建一个
				Class<? extends Job> clazz = ScheduleTask.CONCURRENT_YES == task.getConcurrent() ? JobFactory.class : JobDisallowConcurrentFactory.class;
				JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(task.getName()).build();
				jobDetail.getJobDataMap().put(ScheduleTask.JOB_DATAMAP_KEY, task);
				//表达式调度构建器
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron());
				//按新的cronExpression表达式构建一个新的Trigger
				trigger = TriggerBuilder.newTrigger().withIdentity(task.getName()).withSchedule(scheduleBuilder).build();
				scheduler.scheduleJob(jobDetail, trigger);
			}else{
				//Trigger已存在则更新相应的定时设置
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron());
				//按新的cronExpression表达式重新构建Trigger
				trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
				//按新的Trigger重新设置Job执行
				scheduler.rescheduleJob(triggerKey, trigger);
			}
		}catch(SchedulerException e){
			throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "添加QuartzJob失败", e);
		}
	}


	/**
	 * 更新QuartzJobCronExpression
	 * @return <code>null</code> if a <code>Trigger</code> with the given
     *         name & group was not found and removed from the store (and the 
     *         new trigger is therefore not stored), otherwise
     *         the first fire time of the newly scheduled trigger is returned.
	 */
	private Date updateJobCron(ScheduleTask task){
		TriggerKey triggerKey = TriggerKey.triggerKey(task.getName());
		try{
			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron());
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
			return scheduler.rescheduleJob(triggerKey, trigger);
		}catch(SchedulerException e){
			throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "更新QuartzJobCronExpression失败", e);
		}
	}


	/**
	 * 立即执行一个QuartzJob
	 * 这里的立即运行,只会运行一次
	 * Quartz是通过临时生成一个Trigger(Trigger的key是随机生成的)的方式实现的
	 * 这个临时的Trigger将在本次任务运行完成之后自动删除
	 */
	void triggerJob(ScheduleTask task){
		JobKey jobKey = JobKey.jobKey(task.getName());
		try{
			scheduler.triggerJob(jobKey);
		}catch(SchedulerException e){
			throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "立即执行QuartzJob失败", e);
		}
	}


	/**
	 * 删除一个QuartzJob
	 * 删除任务后,所对应的Trigger也将被删除
	 * @return rue if the Job was found and deleted.
	 */
	private boolean deleteJob(ScheduleTask task){
		JobKey jobKey = JobKey.jobKey(task.getName());
		try{
			return scheduler.deleteJob(jobKey);
		}catch(SchedulerException e){
			throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "删除QuartzJob失败", e);
		}
	}


	/**
	 * 暂停一个QuartzJob
	 */
	private void pauseJob(ScheduleTask task){
		JobKey jobKey = JobKey.jobKey(task.getName());
		try{
			scheduler.pauseJob(jobKey);
		}catch(SchedulerException e){
			throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "暂停QuartzJob失败", e);
		}
	}


	/**
	 * 恢复一个QuartzJob
	 */
	private void resumeJob(ScheduleTask task){
		JobKey jobKey = JobKey.jobKey(task.getName());
		try{
			scheduler.resumeJob(jobKey);
		}catch(SchedulerException e){
			throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "恢复QuartzJob失败", e);
		}
	}
}