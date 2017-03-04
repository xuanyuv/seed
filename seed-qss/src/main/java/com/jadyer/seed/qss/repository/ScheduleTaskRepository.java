package com.jadyer.seed.qss.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.qss.module.ScheduleTask;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * -----------------------------------------------------------------------------------------------------------
 * 1.Spring-Data-JPA实现的方法默认都使用了事务
 *   针对查询类的方法，其等价于@Transactional(readOnly=true)
 *   针对增删改类型的方法，等价于@Transactional
 * 2.有人说：也可以在接口上使用@Transactional显式指定事务属性，它将覆盖Spring-Data-JPA提供的默认值
 *   但经过我的试验，发现save()/delete()方法还是使用了@Transactional，而非@Transactional(readOnly=true)
 * -----------------------------------------------------------------------------------------------------------
 * 关于通过名称来定义查询，可参考http://cw1057.blogspot.com/2013/12/more-in-spring-data-jpa-150-m1.html
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/08/29 18:04.
 */
public interface ScheduleTaskRepository extends BaseRepository<ScheduleTask, Integer> {
	/**
	 * 通过SQL的IN()函数批量查询定时任务
	 * <p>
	 *     若SQL返回的是st，而不是st的个别字段，则该方法返回值便可写成List<ScheduleTask>
	 * </p>
	 */
	@Query("SELECT st.name, st.url FROM ScheduleTask st WHERE st.id IN (?1)")
	Object[] getByIds(List<Integer> idList);
	
	/**
	 * 更新定时任务的状态
	 * <p>
	 *     对于这类操作，Spring在执行时如果发现它没有声明事务，那么会报告下面的异常
	 *     javax.persistence.TransactionRequiredException: Executing an update/delete query
	 * </p>
	 * @return UPDATE所影响的记录行数
	 */
	@Modifying
	@Transactional(timeout=10)
	@Query("UPDATE ScheduleTask SET status=?1 WHERE id=?2")
	int updateStatusById(int status, int id);
	
	/**
	 * 更新定时任务的CronExpression
	 * @return UPDATE所影响的记录行数
	 */
	@Modifying
	@Transactional(timeout=10)
	@Query(value="UPDATE t_schedule_task SET cron=?1 WHERE id=?2", nativeQuery=true)
	int updateCronById(String cron, int id);
}