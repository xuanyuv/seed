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

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2015/08/29 18:04.
 */
public interface ScheduleTaskRepository extends BaseRepository<ScheduleTask, Long> {
    @Query("SELECT st.name as name, st.url as url FROM ScheduleTask st WHERE st.id IN (?1)")
    List<ScheduleSummary> selectByIdList(List<Long> idList);

    /**
     * 通过SQL的IN()函数批量查询定时任务
     * ---------------------------------------------------------------------------
     * 若SQL返回的是st，而不是st的个别字段，则该方法返回值便可写成List<ScheduleTask>
     * ---------------------------------------------------------------------------
     */
    @Query("SELECT st.name, st.url FROM ScheduleTask st WHERE st.id IN (?1)")
    Object[] selectByIds(List<Long> idList);

    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE ScheduleTask SET nextFireTime=?1, previousFireTime=?2 WHERE id=?3")
    int updateTriggerTimeById(Date nextFireTime, Date previousFireTime, long id);

    /**
     * 更新定时任务的状态
     * ----------------------------------------------------------------------------------------------------
     * 1. springdatajpa实现的方法默认都使用了事务，且是只读事务
     * 2. 对于更新或删除操作，如果使用的是 @Query 注解，那么还要结合 @Modifying 来声明这是一个修改操作
     * 3. 但如果没有使用 @Query，而是类似这样void deleteByName(String name)则不需要 @Modifying 注解
     *    注意：调用完.deleteByName()后，还需要调用.flush()，否则若后面再添加相同name的数据，会冲突
     *    注意：也就是说，真正的deleteByName()生效，是在整个事务的最后，除非提前.flush()才会提前生效
     * 4. 对于更新或删除操作，如果Spring在执行时发现它没有声明事务，那么会报告类似下面的异常
     *    jakarta.persistence.TransactionRequiredException: Executing an update/delete query
     *    jakarta.persistence.TransactionRequiredException: No EntityManager with actual transaction
     *    此时需要在Service类或方法上，或者repository方法上，添加@Transactional注解（否则会被当成只读事务去执行）
     * ----------------------------------------------------------------------------------------------------
     * @return UPDATE所影响的记录行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE ScheduleTask SET status=?1 WHERE id=?2")
    int updateStatusById(int status, long id);

    /**
     * 更新定时任务的CronExpression
     * @return UPDATE所影响的记录行数
     */
    @Modifying
    @Transactional
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
     * 7. 返回值为ScheduleTask时，若查询不到数据，则返回的ScheduleTask==null
     * 8. 返回值为List<ScheduleTask>时，若查询不到数据，则返回的List<ScheduleTask>!=null，而是List<ScheduleTask>.size()=0
     */
    //根据 name 来获取对应的 ScheduleTask
    ScheduleTask getByName(String name);
    ScheduleTask getByAppnameAndName(String appname, String name);

    //WHERE name=? AND cron=?
    //如果能确认name和cron联合条件只会查到一条结果，那么可以用一个实体接收
    ScheduleTask findByNameAndCron(String name, String cron);

    //WHERE name LIKE %?%（注意前后的百分号，它会自动加上，不需要传入的时候手动加）
    List<ScheduleTask> findByNameContaining(String name);

    //WHERE name LIKE ?（注意不是“%?%”，前后的百分号需要传参数的时候自己加，比如“%5%”）
    List<ScheduleTask> getByNameLike(String name);

    //WHERE name LIKE %? AND id < ?（注意这里EndingWith实际就是“%?”，不需要像Like那种传参的时候要自己加“%”）
    List<ScheduleTask> getByNameEndingWithAndIdLessThan(String name, Long id);

    //WHERE name LIKE ?% AND id < ?（注意这里StartingWith实际就是“?%”，不需要像Like那种传参的时候要自己加“%”）
    List<ScheduleTask> getByNameStartingWithAndIdLessThan(String name, Long id);

    //WHERE name IN (?, ?, ?) OR id < ?
    List<ScheduleTask> getByNameInOrIdLessThan(List<String> names, Long id);

    //唯一查询
    //也可以写成findDistinctByNameOrStatus，Distinct前后加单词或者表字段名等等，都是没意义的
    //实际查询都是select distinct id,name,url,status... from ... where name=? or status=?
    List<ScheduleTask> findDistinctByNameOrStatus(String name, Integer status);
    List<ScheduleTask> findDistinctCronByNameOrStatus(String name, Integer status);
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

    //限制查询结果（注意这里不能写成findFirstOrderByIdAsc，即First和Top后面要跟上By，哪怕没有查询条件）
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