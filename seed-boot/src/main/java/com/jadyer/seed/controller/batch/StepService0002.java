package com.jadyer.seed.controller.batch;

import com.alibaba.druid.pool.DruidDataSource;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.controller.batch.model.Person;
import com.jadyer.seed.controller.batch.model.PersonRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 结算批量任务：读取数据库内容，转换后，再写入数据库
 * Created by 玄玉<https://jadyer.cn/> on 2018/8/15 17:02.
 */
@Component
public class StepService0002 {
    @Resource
    private DruidDataSource dataSource;
    @Resource
    private PersonRepository personRepository;
    @Resource
    private StepBuilderFactory stepBuilderFactory;
    @Resource
    private StepExecutionListener stepExecutionListener;

    @Bean
    @StepScope
    public Step step0002(){
        return stepBuilderFactory.get("step0002")
                .listener(stepExecutionListener)
                .<Person, Person>chunk(10) //批处理每次提交10条数据
                .reader(this.reader())
                .processor(this.processor())
                .writer(this.writer())
                .build();
    }


    private JdbcCursorItemReader<Person> reader(){
        JdbcCursorItemReader<Person> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM t_person WHERE id > ?");
        reader.setPreparedStatementSetter(new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, 3);
            }
        });
        reader.setRowMapper(new BeanPropertyRowMapper<>(Person.class));
        return reader;
    }


    private ItemWriter<Person> writer(){
        //写到数据库
        return new ItemWriter<Person>() {
            @Override
            public void write(List<? extends Person> items) {
                for(Person obj : items){
                    LogUtil.getLogger().info("写入DB：" + ReflectionToStringBuilder.toString(personRepository.saveAndFlush(obj)));
                }
            }
        };
    }


    private ItemProcessor<Person, Person> processor(){
        return new ItemProcessor<Person, Person>() {
            @Override
            public Person process(Person item) {
                String validateResult = ValidatorUtil.validate(item);
                if(StringUtils.isNotEmpty(validateResult)){
                    throw new SeedException("数据校验未通过-->" + validateResult);
                }
                LogUtil.getLogger().info("读取到-->{}", ReflectionToStringBuilder.toString(item));
                item.setRealName(item.getRealName() + "--00");
                item.setAge(item.getAge() + 10);
                item.setId(null);
                item.setUpdateTime(null);
                item.setCreateTime(new Date());
                LogUtil.getLogger().info("转换后==>{}", ReflectionToStringBuilder.toString(item));
                return item;
            }
        };
    }
}