/**
 * Created by 玄玉<https://jadyer.cn/> on 2022/2/16 11:28.
 */
package com.jadyer.seed.comm.annotation.qss;

/*
@RestController
@RequestMapping("/qss")
public class QssController {
    @Resource
    private JedisPool jedisPool;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    //通过注解注册任务
    @PostMapping("/reg")
    public CommResult<Boolean> reg(ScheduleTask task, String dynamicPassword){
        this.verifyDynamicPassword(dynamicPassword);
        LogUtil.getLogger().info("收到任务注册请求：{}", ReflectionToStringBuilder.toString(task));
        if(null == scheduleTaskRepository.getByAppnameAndName(task.getAppname(), task.getName())){
            //注册过来的任务自动启动
            task.setStatus(SeedConstants.QSS_STATUS_RUNNING);
            task = scheduleTaskRepository.saveAndFlush(task);
            //通过Redis发布订阅来同步到所有QSS节点里面
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(SeedConstants.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
            }
            return CommResult.success();
        }else{
            LogUtil.getLogger().info("收到任务注册请求：任务已存在：{}#{}，自动忽略...", task.getAppname(), task.getName());
            return CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "任务已存在："+task.getAppname()+"#"+task.getName()+"，自动忽略...");
        }
    }
*/