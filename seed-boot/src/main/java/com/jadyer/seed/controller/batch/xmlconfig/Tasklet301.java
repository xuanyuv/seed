package com.jadyer.seed.controller.batch.xmlconfig;

import com.jadyer.seed.controller.batch.model.Person;
import com.jadyer.seed.controller.batch.model.PersonRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/22 17:05.
 */
public class Tasklet301 implements Tasklet {
    @Resource
    private PersonRepository personRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        File dataFile = new File("D:\\data\\seedboot-batch-result.txt");
        for(Person obj : personRepository.findAll()){
            StringBuilder sb = new StringBuilder();
            sb.append(obj.getAge()).append("|").append(obj.getRealName()).append("\n");
            FileUtils.writeStringToFile(dataFile, sb.toString(), StandardCharsets.UTF_8, true);
        }
        return RepeatStatus.FINISHED;
    }
}