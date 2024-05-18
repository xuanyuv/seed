package com.jadyer.seed.test;

import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.qss.boot.QssRun;
import com.jadyer.seed.qss.model.ScheduleLog;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleLogRepository;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/4/27 18:29.
 */
@Profile("local")
@SpringBootTest(classes=QssRun.class)
public class JPATest {
    @Resource
    private ScheduleLogRepository scheduleLogRepository;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    /**
     * mysql> select id,name,status,url from t_schedule_task;
     * +----+--------+--------+--------------------------------------+
     * | id | name   | status | url                                  |
     * +----+--------+--------+--------------------------------------+
     * |  1 | test22 |      0 | http://127.0.0.1/seed/user/getJson/2 |
     * |  2 | test33 |      0 | http://127.0.0.1/seed/user/getJson/3 |
     * |  5 | test44 |      0 | http://127.0.0.1/seed/user/getJson/4 |
     * |  6 | test55 |      0 | http://127.0.0.1/seed/user/getJson/6 |
     * +----+--------+--------+--------------------------------------+
     */
    @Test
    public void selectTest() throws InterruptedException, ExecutionException, TimeoutException {
        //Assertions.assertEquals("test55", scheduleTaskRepository.getByName("test55").getName());
        Condition<ScheduleTask> spec = Condition.<ScheduleTask>and().like("name", "3");
        Assertions.assertEquals("test33", scheduleTaskRepository.findAll(spec).get(0).getName());
        Assertions.assertEquals("test33", scheduleTaskRepository.getByNameLike("%3%").get(0).getName());
        //Assertions.assertEquals("test44", scheduleTaskRepository.findByNameAndCron("test44", "0/15 * * * * ?").getName());
        //Assertions.assertEquals("test55", scheduleTaskRepository.getByNameEndingWithAndIdLessThan("55", 9L).get(0).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.getByNameStartingWithAndIdLessThan("test", 9L).get(0).getName());
        //Assertions.assertEquals("test33", scheduleTaskRepository.getByNameInOrIdLessThan(Arrays.asList("test44", "test22", "jadyer"), 3L).get(1).getName());
        //Assertions.assertEquals("test55", scheduleTaskRepository.findDistinctTaskByNameOrStatus("test55", 1).get(0).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.findTaskDistinctByNameOrStatus("test33", 0).get(0).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.findByUrlIgnoreCase("http://127.0.0.1/seed/user/getJson/2").get(0).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.findByNameAndUrlAllIgnoreCase("test22", "http://127.0.0.1/seed/user/getJson/2").get(0).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.findByNameOrderByIdAsc("test22").get(0).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.findByNameOrderByUrlDesc("test22").get(0).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.findFirstByOrderByIdAsc().getName());
        //Assertions.assertEquals("test55", scheduleTaskRepository.findTopByOrderByNameDesc().getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.findTop3ByStatus(0, PageRequest.of(0, 10, new Sort(Sort.Direction.ASC, "id"))).getContent().get(0).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.queryFirst10ByStatus(0, PageRequest.of(0, 10, new Sort(Sort.Direction.ASC, "id"))).getContent().get(0).getName());
        //Assertions.assertEquals("test44", scheduleTaskRepository.findTop10ByStatus(0, PageRequest.of(0, 10, new Sort(Sort.Direction.DESC, "id"))).get(1).getName());
        //Assertions.assertEquals("test44", scheduleTaskRepository.findFirst10ByStatus(0, new Sort(Sort.Direction.DESC, "id")).get(1).getName());
        //Assertions.assertEquals("test55", scheduleTaskRepository.findByName("test55").get(60, TimeUnit.SECONDS).getName());
        //Assertions.assertEquals("test22", scheduleTaskRepository.findOneByUrl("http://127.0.0.1/seed/user/getJson/2").get(60, TimeUnit.SECONDS).getName());
        //Assertions.assertEquals("test33", scheduleTaskRepository.findOneByName("test33").get(60, TimeUnit.SECONDS).getName());
        for(ScheduleTask obj : scheduleTaskRepository.findDistinctByNameOrStatus("1", 0)){
            System.out.println("----" + ReflectionToStringBuilder.toString(obj));
        }
        for(ScheduleTask obj : scheduleTaskRepository.findDistinctCronByNameOrStatus("1", 0)){
            System.out.println("===" + ReflectionToStringBuilder.toString(obj));
        }
        for(ScheduleTask obj : scheduleTaskRepository.findTaskDistinctByNameOrStatus("1", 0)){
            System.out.println(">>>" + ReflectionToStringBuilder.toString(obj));
        }
    }


    @Test
    public void betweenTest() throws ParseException {
        Condition<ScheduleLog> spec = Condition.<ScheduleLog>and().between("duration", 700, 1000);
        for(ScheduleLog log : scheduleLogRepository.findAll(spec)){
            System.out.println("查询到：id=" + log.getId());
        }
        System.out.println("--------------------------------------------------");
        Date startDate = DateUtils.parseDate("2018-07-20 11:19:20", "yyyy-MM-dd HH:mm:ss");
        Date endDate = DateUtils.parseDate("2018-07-20 11:20:20", "yyyy-MM-dd HH:mm:ss");
        spec = Condition.<ScheduleLog>and().between("fireTime", startDate, endDate);
        for(ScheduleLog log : scheduleLogRepository.findAll(spec)){
            System.out.println("再次查询到：id=" + log.getId());
        }
    }
}