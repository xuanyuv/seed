package com.jadyer.seed.controller.batch.javaconfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.controller.batch.SettleJobListeners;
import com.jadyer.seed.controller.batch.model.Person;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 结算批量任务：读取文件数据后录库
 * ---------------------------------------------------------------------------
 * 对于Processor的执行：
 * 1、它主要用于数据的转换与处理：比如写数据前，判断其是否已入库等等处理逻辑
 * 2、它的被调用次数与chunk无关：即ItemReader读到了多少条数据，它就会被调用多少次
 * 3、它返回null时，SpringBatch将会忽略该item，不将其发给ItemWriter
 * 4、对于CompositeItemProcessor：如果processor列表中的某个processor返回null
 *    那么它后面的processor都不会被执行，即整个CompositeItemProcessor执行完毕
 * ---------------------------------------------------------------------------
 * 对于ItemWriter的执行：执行次数与chunk有关
 * ---------------------------------------------------------------------------
 * Comment by 玄玉<https://jadyer.cn/> on 2017/11/24 16:55.
 */
@Component
public class StepService0001 {
    @Resource
    private DruidDataSource dataSource;
    @Resource
    private StepBuilderFactory stepBuilderFactory;
    @Resource
    private SettleJobListeners settleJobListeners;

    @Bean
    public Step step0001(){
        //https://my.oschina.net/chkui/blog/3070081
        return stepBuilderFactory.get("step0001")
                .listener(this.settleJobListeners)
                .<Person, Person>chunk(10) //批处理每次提交10条数据
                .reader(this.reader())
                .processor(this.processor())
                .writer(this.writer())
                .build();
    }


    private FlatFileItemReader<Person> reader(){
        //return new FlatFileItemReaderBuilder<Person>()
        //        .name("step0001Reader")
        //        .resource(new FileSystemResource("/data/seedboot-batch.txt"))
        //        .strict(true)
        //        .encoding(SeedConstants.DEFAULT_CHARSET)
        //        .linesToSkip(1)
        //        .delimited().delimiter("|")
        //        .names(new String[]{"realName", "age", "birthDay"})
        //        .targetType(Person.class).customEditors(SettleJobConfiguration.customEditors)
        //        .build();
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        //输入文件
        reader.setResource(new FileSystemResource("/data/seedboot-batch.txt"));
        //严格模式（默认为true：即资源文件不存在会抛出异常，阻断当前job）
        reader.setStrict(true);
        //读取编码（默认为ISO-8859-1）
        reader.setEncoding(SeedConstants.DEFAULT_CHARSET);
        //跳过标题行（这里跳过了前1行）
        reader.setLinesToSkip(1);
        //行映射（将每行映射为一个对象）
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                //注意：如果文件中某个字段为空，假设某行内容为[卢云||2019-08-06 12:13:14]，那么会正常读取且读取到的age是空的字符串（不是null）
                setDelimiter("|");
                setNames("realName", "age", "birthDay");
            }});
            //setFieldSetMapper(new FieldSetMapper<Person>() {
            //    @Override
            //    public Person mapFieldSet(FieldSet fieldSet) throws BindException {
            //        Person person = new Person();
            //        person.setName(fieldSet.readString("name"));
            //        person.setAge(fieldSet.readInt("age"));
            //        return person;
            //    }
            //});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
                setCustomEditors(SettleJobConfiguration.customEditors);
            }});
        }});
        return reader;
    }


    private JdbcBatchItemWriter<Person> writer(){
        //写到数据库
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO t_person(real_name, age, birth_day) VALUES(:realName, :age, :birthDay)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        //这里如果报以下异常，则需要设置一下afterPropertiesSet
        //java.lang.NullPointerException: null
        //at org.springframework.batch.item.database.JdbcBatchItemWriter$1.doInPreparedStatement(JdbcBatchItemWriter.java:189)
        //https://stackoverflow.com/questions/32656581/pb-using-jdbcbatchitemwriter-with-compositeitemwriter-in-java-code-spring-batch
        writer.afterPropertiesSet();
        return writer;
    }


    private ItemProcessor<Person, Person> processor(){
        return new ItemProcessor<Person, Person>() {
            @Override
            public Person process(Person item) {
                String validateResult = ValidatorUtil.validate(item);
                if(StringUtils.isNotEmpty(validateResult)){
                    //return null;
                    throw new SeedException("数据校验未通过-->" + validateResult);
                }
                LogUtil.getLogger().info("读取到-->{}", ReflectionToStringBuilder.toString(item));
                LogUtil.getLogger().info("转换后==>{}", ReflectionToStringBuilder.toString(item));
                return item;
            }
        };
    }
}