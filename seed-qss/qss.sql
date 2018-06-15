DROP TABLE IF EXISTS t_schedule_task;
CREATE TABLE t_schedule_task(
id                 INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
appname            VARCHAR(32)  NOT NULL COMMENT '定时任务的应用名称',
name               VARCHAR(32)  NOT NULL COMMENT '定时任务名称',
cron               VARCHAR(32)  NOT NULL COMMENT '定时任务执行的CronExpression',
url                VARCHAR(512) NOT NULL COMMENT '定时任务URL',
status             TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '定时任务状态：0--停止，1--启动，2--挂起，3--恢复',
concurrent         TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '定时任务是否允许并行执行：0--不允许，1--允许',
next_fire_time     DATETIME     COMMENT '下次触发时间',
previous_fire_time DATETIME     COMMENT '上次触发时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
UNIQUE INDEX uniq_url(url),
UNIQUE INDEX uniq_appname_name(appname, name)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='定时任务信息表';


INSERT INTO t_schedule_task(appname, name, cron, url) VALUES
('ifs', 'testTask', '0 */1 * * * ?', 'http://127.0.0.1/getJson/2'),
('mss', 'testTask', '0 */2 * * * ?', 'http://127.0.0.1/getJson/3');