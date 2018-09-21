package com.jadyer.seed.qss;

import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/24 15:40.
 */
@Controller
public class ViewController {
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    @GetMapping({"", "/"})
    String index(HttpServletRequest request){
        request.setAttribute("taskList", scheduleTaskRepository.findAll());
        return "/qss";
    }
}