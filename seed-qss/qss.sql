DROP TABLE IF EXISTS t_schedule_task;
CREATE TABLE t_schedule_task(
id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
name        VARCHAR(32)  NOT NULL COMMENT '定时任务名称',
cron        VARCHAR(32)  NOT NULL COMMENT '定时任务执行的CronExpression',
status      TINYINT(1)   NOT NULL COMMENT '定时任务状态：0--停止，1--启动，2--挂起，3--恢复',
concurrent  TINYINT(1)   NOT NULL COMMENT '定时任务是否允许并行执行：0--不允许，1--允许',
url         VARCHAR(256) NOT NULL COMMENT '定时任务URL',
comment     VARCHAR(256) COMMENT '定时任务描述',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='定时任务信息表';


INSERT INTO t_schedule_task(name, cron, status, concurrent, url, create_time) VALUES
('test22', '0/10 * * * * ?', 0, 0, 'http://127.0.0.1/seed/user/getJson/2', now()),
('test33', '0/15 * * * * ?', 0, 0, 'http://127.0.0.1/seed/user/getJson/3', now());