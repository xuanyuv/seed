package com.jadyer.seed.test;

import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.qss.boot.QssRun;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/4/27 18:29.
 */
@Profile("local")
@RunWith(SpringRunner.class)
@SpringBootTest(classes=QssRun.class)
public class JPATest {
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
        //Assert.assertEquals("test55", scheduleTaskRepository.getByName("test55").getName());
        Condition<ScheduleTask> spec = Condition.<ScheduleTask>and().like("name", "3");
        Assert.assertEquals("test33", scheduleTaskRepository.findAll(spec).get(0).getName());
        Assert.assertEquals("test33", scheduleTaskRepository.getByNameLike("%3%").get(0).getName());
        //Assert.assertEquals("test44", scheduleTaskRepository.findByNameAndCron("test44", "0/15 * * * * ?").getName());
        //Assert.assertEquals("test55", scheduleTaskRepository.getByNameEndingWithAndIdLessThan("55", 9L).get(0).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.getByNameStartingWithAndIdLessThan("test", 9L).get(0).getName());
        //Assert.assertEquals("test33", scheduleTaskRepository.getByNameInOrIdLessThan(Arrays.asList("test44", "test22", "jadyer"), 3L).get(1).getName());
        //Assert.assertEquals("test55", scheduleTaskRepository.findDistinctTaskByNameOrStatus("test55", 1).get(0).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.findTaskDistinctByNameOrStatus("test33", 0).get(0).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.findByUrlIgnoreCase("http://127.0.0.1/seed/user/getJson/2").get(0).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.findByNameAndUrlAllIgnoreCase("test22", "http://127.0.0.1/seed/user/getJson/2").get(0).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.findByNameOrderByIdAsc("test22").get(0).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.findByNameOrderByUrlDesc("test22").get(0).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.findFirstByOrderByIdAsc().getName());
        //Assert.assertEquals("test55", scheduleTaskRepository.findTopByOrderByNameDesc().getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.findTop3ByStatus(0, new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "id"))).getContent().get(0).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.queryFirst10ByStatus(0, new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "id"))).getContent().get(0).getName());
        //Assert.assertEquals("test44", scheduleTaskRepository.findTop10ByStatus(0, new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id"))).get(1).getName());
        //Assert.assertEquals("test44", scheduleTaskRepository.findFirst10ByStatus(0, new Sort(Sort.Direction.DESC, "id")).get(1).getName());
        //Assert.assertEquals("test55", scheduleTaskRepository.findByName("test55").get(60, TimeUnit.SECONDS).getName());
        //Assert.assertEquals("test22", scheduleTaskRepository.findOneByUrl("http://127.0.0.1/seed/user/getJson/2").get(60, TimeUnit.SECONDS).getName());
        //Assert.assertEquals("test33", scheduleTaskRepository.findOneByName("test33").get(60, TimeUnit.SECONDS).getName());
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
}