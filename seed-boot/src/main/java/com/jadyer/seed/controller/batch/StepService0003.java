package com.jadyer.seed.controller.batch;

import com.alibaba.druid.pool.DruidDataSource;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.controller.batch.model.Person;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * 结算批量任务：读取数据库内容，转换后，再写入文件
 * Created by 玄玉<https://jadyer.cn/> on 2018/8/15 17:02.
 */
@Component
public class StepService0003 {
    @Resource
    private DruidDataSource dataSource;
    @Resource
    private StepBuilderFactory stepBuilderFactory;
    @Resource
    private StepExecutionListener stepExecutionListener;

    @Bean
    public Step step0003(){
        return stepBuilderFactory.get("step0003")
                .listener(stepExecutionListener)
                .<Person, Person>chunk(10) //批处理每次提交10条数据
                .reader(this.reader())
                .processor(this.processor())
                .writer(this.writer())
                .build();
    }


    private JdbcPagingItemReader<Person> reader(){
        JdbcPagingItemReader<Person> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(100);
        reader.setQueryProvider(new MySqlPagingQueryProvider() {{
            setSelectClause("SELECT *");
            setFromClause("FROM t_person");
            setWhereClause("id >= :id");
            setSortKeys(new HashMap<String, Order>() {
                private static final long serialVersionUID = 8259586833721646740L;
                {
                    put("id", Order.ASCENDING);
                }
            });
        }});
        reader.setParameterValues(new HashMap<String, Object>() {
            private static final long serialVersionUID = 9216104005619956148L;
            {
                put("id", 5);
            }
        });
        reader.setRowMapper(new BeanPropertyRowMapper<>(Person.class));
        try {
            reader.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return reader;
    }


    private FlatFileItemWriter<Person> writer(){
        //写到文件
        FlatFileItemWriter<Person> writer = new FlatFileItemWriter<>();
        writer.setEncoding(SeedConstants.DEFAULT_CHARSET);
        writer.setResource(new FileSystemResource("/data/seedboot-batch-result.txt"));
        //创建对象属性聚合字符串（它会根据设置的分隔符以及对象属性对应的字符名称来聚合）
        writer.setLineAggregator(new DelimitedLineAggregator<Person>() {{
            setDelimiter(", ");
            setFieldExtractor(new BeanWrapperFieldExtractor<Person>() {{
                setNames(new String[]{"realName", "age"});
            }});
        }});
        return writer;
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
                item.setRealName(item.getRealName() + "--11");
                item.setAge(item.getAge() - 5);
                LogUtil.getLogger().info("转换后==>{}", ReflectionToStringBuilder.toString(item));
                return item;
            }
        };
    }
}