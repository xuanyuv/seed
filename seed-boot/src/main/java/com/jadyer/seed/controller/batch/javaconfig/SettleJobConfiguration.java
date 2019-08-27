package com.jadyer.seed.controller.batch.javaconfig;

import com.jadyer.seed.controller.batch.SettleJobListeners;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.annotation.Resource;
import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 结算批量任务
 * ----------------------------------------------------------------------
 * https://spring.io/guides/gs/batch-processing/
 * https://docs.spring.io/spring-batch/4.1.x/reference/html/index.html
 * ----------------------------------------------------------------------
 * 远程分区Step：http://www.kailing.pub/article/index/arcid/196.html
 * ----------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2018/8/15 17:00.
 */
@Configuration
public class SettleJobConfiguration {
    //全局转换器（读文件时使用）
    static final Map<Class<?>, PropertyEditor> customEditors = new HashMap<Class<?>, PropertyEditor>(){
        private static final long serialVersionUID = -8943129541317025696L;
        {
            put(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
        }
    };
    @Resource
    private Step step0001;
    @Resource
    private Step step0002;
    @Resource
    private Step step0003;
    @Resource
    private Step step0004;
    @Resource
    private Step step0005;
    @Resource
    private Step step0006;
    @Resource
    private JobBuilderFactory jobBuilderFactory;
    @Resource
    private SettleJobListeners settleJobListeners;

    @Bean
    public Job settleJob() {
        return jobBuilderFactory.get("settleJob")
                //使每个Job的运行ID都唯一
                .incrementer(new RunIdIncrementer())
                .listener(this.settleJobListeners)
                //.flow(step0001).split(new SimpleAsyncTaskExecutor("springbatch_seedboot")).add(flow04, flow05, flow06)
                //.start(step0001).split(new SimpleAsyncTaskExecutor("springbatch_seedboot")).add(flow04, flow05, flow06)
                //先执行step0001（注意：上面注释的这两种写法都不会step0001先执行然后再执行step0004...，他们都会使得step0001和step0004...同时执行）
                .flow(step0001)
                //然后step0004、step0005、step0006开始执行且三者是并行执行互不影响的
                .next(this.splitFlow())
                //接着等三者全都执行完毕才会去执行step0002
                .next(step0002)
                //最后执行step0003
                .next(step0003)
                .end()
                .build();
    }


    //https://docs.spring.io/spring-batch/4.0.x/reference/html/index-single.html#scalabilityParallelSteps
    private Flow splitFlow(){
        Flow flow04 = new FlowBuilder<SimpleFlow>("flow04").start(step0004).build();
        Flow flow05 = new FlowBuilder<SimpleFlow>("flow05").start(step0005).build();
        Flow flow06 = new FlowBuilder<SimpleFlow>("flow06").start(step0006).build();
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(new SimpleAsyncTaskExecutor("springbatch_seedboot"))
                .add(flow04, flow05, flow06)
                .build();
    }
}