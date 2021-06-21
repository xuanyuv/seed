DROP TABLE IF EXISTS t_schedule_task;
CREATE TABLE t_schedule_task(
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    appname            VARCHAR(32)  NOT NULL             COMMENT '定时任务的应用名称',
    name               VARCHAR(32)  NOT NULL             COMMENT '定时任务名称',
    cron               VARCHAR(32)  NOT NULL             COMMENT '定时任务执行的CronExpression',
    url                VARCHAR(512) NOT NULL             COMMENT '定时任务URL',
    status             TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '定时任务状态：0--停止，1--启动，2--挂起，3--恢复',
    concurrent         TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '定时任务是否允许并行执行：0--不允许，1--允许',
    next_fire_time     DATETIME                          COMMENT '下次触发时间',
    previous_fire_time DATETIME                          COMMENT '上次触发时间',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE INDEX uniq_appname_name(appname, name)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT='定时任务信息表';


DROP TABLE IF EXISTS t_schedule_log;
CREATE TABLE t_schedule_log(
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    task_id   INT          NOT NULL COMMENT '任务ID，对应t_schedule_task.id',
    appname   VARCHAR(32)  NOT NULL COMMENT '定时任务的应用名称',
    name      VARCHAR(32)  NOT NULL COMMENT '定时任务名称',
    url       VARCHAR(512) NOT NULL COMMENT '定时任务URL',
    fire_time DATETIME     NOT NULL COMMENT '定时任务触发时间',
    duration  BIGINT                COMMENT '定时任务所耗时间，单位：ms',
    resp_data MEDIUMTEXT            COMMENT '定时任务返回结果',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    INDEX idx_taskId(task_id)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT='定时任务执行记录表';


INSERT INTO t_schedule_task(appname, name, cron, url) VALUES
('ifs', 'testTask', '15 */1 * * * ?', 'http://127.0.0.1:8008/qss/getByIds?ids=1'),
('mss', 'testTask', '35 */1 * * * ?', 'http://127.0.0.1:8008/qss/getByIds?ids=1%602');