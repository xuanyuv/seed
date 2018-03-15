package com.jadyer.seed.controller.batch;

import com.jadyer.seed.controller.batch.processor.JobExecutionListener;
import com.jadyer.seed.controller.batch.processor.PersonItemProcessor;
import com.jadyer.seed.controller.batch.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/11/20 11:12.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Resource
    private DataSource dataSource;
    @Resource
    private JobBuilderFactory jobBuilderFactory;
    @Resource
    private StepBuilderFactory stepBuilderFactory;
    @Resource
    private PersonItemProcessor personItemProcessor;
    @Resource
    private JobExecutionListener jobExecutionListener;

    @Bean
    public Job importPeopleJob(){
        return jobBuilderFactory.get("importPeopleJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener)
                .flow(this.importPeopleStep())
                .end()
                .build();
    }


    @Bean
    public Step importPeopleStep(){
        return stepBuilderFactory.get("importPeopleStep")
                .<Person, Person>chunk(10) //批处理每次提交10条数据
                .reader(this.reader())
                .processor(personItemProcessor)
                .writer(this.writer())
                .build();
    }


    @Bean
    public FlatFileItemReader<Person> reader(){
        FlatFileItemReader<Person> txtItemReader = new FlatFileItemReader<>();
        //输入文件
        txtItemReader.setResource(new FileSystemResource("/app/data/seed.txt"));
        //严格模式（默认为true：即资源文件不存在会抛出异常，阻断当前job）
        txtItemReader.setStrict(true);
        //读取编码（默认为ISO-8859-1）
        txtItemReader.setEncoding("UTF-8");
        //跳过标题行（这里跳过了前2行）
        txtItemReader.setLinesToSkip(2);
        //行映射（将每行映射为一个对象）
        txtItemReader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setDelimiter("|");
                setNames(new String[]{"name", "age"});
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
            }});
        }});
        return txtItemReader;
    }


    @Bean
    public ItemWriter<Person> writer(){
        //写到数据库
        JdbcBatchItemWriter<Person> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        jdbcBatchItemWriter.setSql("INSERT INTO t_person(name, age) VALUES(:name, :age)");
        jdbcBatchItemWriter.setDataSource(dataSource);
        //输出多个write时这里要设置一下，否则会报异常
        //java.lang.NullPointerException: null
        //at org.springframework.batch.item.database.JdbcBatchItemWriter$1.doInPreparedStatement(JdbcBatchItemWriter.java:189)
        //https://stackoverflow.com/questions/32656581/pb-using-jdbcbatchitemwriter-with-compositeitemwriter-in-java-code-spring-batch
        jdbcBatchItemWriter.afterPropertiesSet();
        //写到文件
        FlatFileItemWriter<Person> flatFileItemWriter = new FlatFileItemWriter<>();
        flatFileItemWriter.setEncoding("UTF-8");
        flatFileItemWriter.setResource(new FileSystemResource("/app/data/seed-result.txt"));
        //创建对象属性聚合字符串（它会根据设置的分隔符以及对象属性对应的字符名称来聚合）
        flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<Person>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<Person>() {{
                setNames(new String[]{"name", "age"});
            }});
        }});
        CompositeItemWriter writer = new CompositeItemWriter();
        writer.setDelegates(Arrays.asList(jdbcBatchItemWriter, flatFileItemWriter));
        return writer;
    }
}