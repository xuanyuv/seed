package com.jadyer.seed.qss.helper;

import com.jadyer.seed.qss.model.ScheduleTask;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 有状态的任务工厂
 * ------------------------------------------------------------------------------------------------------
 * 当任务到了下一次触发时，若本次未执行完毕，则该工厂使得其不允许并发执行
 * ------------------------------------------------------------------------------------------------------
 * @DisallowConcurrentExecution
 * 该注解标记在实现了Job类的上面，它可以使任务不允许并发执行
 * 比如某任务执行需耗时5s，配置的执行频率是3s一次，那么到了3s时若没有该注解，Quartz会启用新的线程来执行任务
 * 而有了该注解便可等待当前任务执行完毕后，立即执行下一次任务（亲测不会再隔3s去执行下一次任务，而是立即执行）
 * ------------------------------------------------------------------------------------------------------
 * @PersistJobDataAfterExecution
 * 该注解标记在实现了Job类的上面
 * 它可以在execute()方法成功完成（没有抛出异常）之后，更新JobDetail的JobDataMap的存储拷贝
 * 这样相同的job（JobDetail）在下次执行的时候，会接收到更新过的值，而不是原来存储的值
 * 若使用该注解，你也非常应该考虑使用@DisallowConcurrentExecution，以避免可能的混淆（任务快速运行的场合中）
 * ------------------------------------------------------------------------------------------------------
 * 遗留问题
 * 1.更新定时任务Cron时，这里获取到的时间表达式参数值还是更新前的，即便使用了@PersistJobDataAfterExecution
 * 2.任务运行两次的问题
 *   这也是网上传的最多的问题，网上有人分析可能是由于以下两个原因造成的
 *   2.1.Spring配置文件加载多次，导致Quartz的bean被实例化多次而导致任务多次执行
 *   2.2.Tomcat的webapps目录问题，Tomcat运行时加载了两次配置文件导致任务多次执行
 *   其实这并不是什么大问题，之所以这么说，是因为项目的业务设计要考虑到健壮性
 *   一个设计良好的业务方法，特别是那些供外部调用的接口或方法，应该都支持幂等性
 *   所以在有些定时任务为分布式设计的系统中，为了确保定时任务的执行甚至会故意人为的去调用两次
 * 3.修改定时任务Cron时，可能会让任务长时间处于线程阻塞状态，即BLOCKED状态，即使你的任务只是简单的System.out
 *   要使它恢复也很简单，删除重建即可
 * ------------------------------------------------------------------------------------------------------
 * 幂等性
 * 所谓幂等性即该方法同样的参数至少在一个时间区间内，调用1次和调用10次100次结果都是一样的
 * 支持幂等性最好在进入方法时就判断，发现已经执行过就立即返回而不是再再执行一遍，以节省资源
 * ------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/8/8 20:50.
 */
@DisallowConcurrentExecution
public class JobDisallowConcurrentFactory implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//Quartz有状态的JobDataMap-->http://www.cnblogs.com/interdrp/p/3463583.html
		//ScheduleTask task = (ScheduleTask)context.getJobDetail().getJobDataMap().get(ScheduleTask.JOB_DATAMAP_KEY);
		ScheduleTask task = (ScheduleTask)context.getMergedJobDataMap().get(ScheduleTask.JOB_DATAMAP_KEY);
		JobHelper.invokMethod(task);
	}
}