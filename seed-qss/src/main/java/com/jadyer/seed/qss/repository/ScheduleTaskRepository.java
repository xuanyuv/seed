package com.jadyer.seed.qss.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.qss.model.ScheduleSummary;
import com.jadyer.seed.qss.model.ScheduleTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * -----------------------------------------------------------------------------------------------------------
 * 1.Spring-Data-JPA实现的方法默认都使用了事务
 *   针对查询类的方法，其等价于@Transactional(readOnly=true)
 *   针对增删改类型的方法，等价于@Transactional
 * 2.有人说：也可以在接口上使用@Transactional显式指定事务属性，它将覆盖Spring-Data-JPA提供的默认值
 *   但经过我的试验，发现save()/delete()方法还是使用了@Transactional，而非@Transactional(readOnly=true)
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/08/29 18:04.
 */
public interface ScheduleTaskRepository extends BaseRepository<ScheduleTask, Long> {
    @Query("SELECT st.name as name, st.url as url FROM ScheduleTask st WHERE st.id IN (?1)")
    List<ScheduleSummary> findByIds(List<Long> idList);

    /**
     * 通过SQL的IN()函数批量查询定时任务
     * <p>
     *     若SQL返回的是st，而不是st的个别字段，则该方法返回值便可写成List<ScheduleTask>
     * </p>
     */
    @Query("SELECT st.name, st.url FROM ScheduleTask st WHERE st.id IN (?1)")
    Object[] getByIds(List<Long> idList);

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
    int updateStatusById(int status, long id);

    /**
     * 更新定时任务的CronExpression
     * @return UPDATE所影响的记录行数
     */
    @Modifying
    @Transactional(timeout=10)
    @Query(value="UPDATE t_schedule_task SET cron=?1 WHERE id=?2", nativeQuery=true)
    int updateCronById(String cron, long id);


    ////////////////【以下为JPA的自定义简单查询】////////////////
    /*
     * 自定义的简单查询就是根据方法名来自动生成SQL，主要的语法是：findXXBy、readXXBy、getXXBy、queryXXBy、countXXBy
     * 基本上SQL体系中的关键词都可以使用，例如：LIKE、IgnoreCase、OrderBy
     * http://docs.spring.io/spring-data/jpa/docs/1.11.3.RELEASE/reference/html/#repository-query-keywords
     * http://docs.spring.io/spring-data/jpa/docs/1.11.3.RELEASE/reference/html/#jpa.query-methods.query-creation
     * 需要注意的几个点
     * 1. 查询方法以 find、read、get 开头
     * 2. 方法名以find..By、read..By、query..By、count..By、get..By做开头时，By之前可以添加Distinct表示查找不重复数据，By之后才是真正的查询条件
     * 3. 可以查询某个属性，也可以使用条件进行比较复杂的查询，例如Between、LessThan、GreaterThan、Like、And、Or
     * 4. 字符串属性后面可以跟IgnoreCase表示不区分大小写，也可以后跟AllIgnoreCase表示所有属性都不区分大小写
     * 5. 可以使用OrderBy对结果进行升序或降序排序
     * 6. 可以查询属性的属性，直接将几个属性连着写即可，如果可能出现歧义属性，可以使用下划线分隔多个属性
     */
    //根据 name 来获取对应的 ScheduleTask
    ScheduleTask getByName(String name);

    //WHERE name LIKE %?%
    List<ScheduleTask> getByNameLike(String name);

    //WHERE name LIKE %? AND id < ?
    List<ScheduleTask> getByNameEndingWithAndIdLessThan(String name, Long id);

    //WHERE name LIKE ?% AND id < ?
    List<ScheduleTask> getByNameStartingWithAndIdLessThan(String name, Long id);

    //WHERE name IN (?, ?, ?) OR id < ?
    List<ScheduleTask> getByNameInOrIdLessThan(List<String> names, Long id);

    //唯一查询
    List<ScheduleTask> findDistinctTaskByNameOrStatus(String name, Integer status);
    List<ScheduleTask> findTaskDistinctByNameOrStatus(String name, Integer status);

    //对某一属性不区分大小写
    List<ScheduleTask> findByUrlIgnoreCase(String url);

    //所有属性不区分大小写
    List<ScheduleTask> findByNameAndUrlAllIgnoreCase(String name, String url);

    //启用静态排序
    List<ScheduleTask> findByNameOrderByIdAsc(String name);
    List<ScheduleTask> findByNameOrderByUrlDesc(String name);

    //查询Person.Address.ZipCode
    //List<Person> findByAddressZipCode(ZipCode zipCode);
    //避免歧义可以这样
    //List<Person> findByAddress_ZipCode(ZipCode zipCode);

    //限制查询结果
    ScheduleTask findFirstByOrderByIdAsc();
    ScheduleTask findTopByOrderByNameDesc();
    Slice<ScheduleTask> findTop3ByStatus(Integer status, Pageable pageable);
    Page<ScheduleTask> queryFirst10ByStatus(Integer status, Pageable pageable);
    List<ScheduleTask> findTop10ByStatus(Integer status, Pageable pageable);
    List<ScheduleTask> findFirst10ByStatus(Integer status, Sort sort);

    @Async
    Future<ScheduleTask> findByName(String name);
    @Async
    ListenableFuture<ScheduleTask> findOneByUrl(String url);
    @Async
    CompletableFuture<ScheduleTask> findOneByName(String name);
}