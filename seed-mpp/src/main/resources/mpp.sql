DROP TABLE IF EXISTS t_user_info;
CREATE TABLE t_user_info(
id         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
pid        INT NOT NULL COMMENT '平台用户所属上一级ID',
username   VARCHAR(16) NOT NULL COMMENT '用户名',
password   VARCHAR(32) NOT NULL COMMENT '登录密码',
uuid       VARCHAR(32) NOT NULL COMMENT '用户唯一标识，用来生成微信或QQ公众平台Token',
mptype     CHAR(1) COMMENT '公众平台类型：0--未知，1--微信，2--QQ',
mpid       VARCHAR(32) COMMENT '微信或QQ公众平台原始ID',
mpno       VARCHAR(32) COMMENT '微信或QQ公众平台号',
mpname     VARCHAR(32) COMMENT '微信或QQ公众平台名称',
appid      VARCHAR(32) COMMENT '微信或QQ公众平台appid',
appsecret  VARCHAR(64) COMMENT '微信或QQ公众平台appsecret',
bindStatus CHAR(1) COMMENT '微信或QQ公众平台绑定状态：0--未绑定，1--已绑定',
bindTime   TIMESTAMP NULL DEFAULT '0000-00-00 00:00:00' COMMENT '微信或QQ公众平台绑定解绑时间',
createTime TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
updateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='mpplus平台用户表';


DROP TABLE IF EXISTS t_fans_info;
CREATE TABLE t_fans_info(
id            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
uid           INT NOT NULL COMMENT '平台用户ID，对应t_user#id',
wxId          VARCHAR(32) COMMENT '微信原始ID',
openid        VARCHAR(64) NOT NULL COMMENT '粉丝的openid',
name          VARCHAR(16) COMMENT '粉丝的真实姓名',
idCard        VARCHAR(18) COMMENT '粉丝的身份证号',
phoneNo       CHAR(11) COMMENT '粉丝的手机号',
subscribe     CHAR(1) NOT NULL COMMENT '关注状态：0--未关注，其它为已关注',
nickname      VARCHAR(32) COMMENT '粉丝的昵称',
sex           CHAR(1) COMMENT '粉丝的性别：0--未知，1--男，2--女',
city          VARCHAR(32) COMMENT '粉丝所在城市',
country       VARCHAR(32) COMMENT '粉丝所在国家',
province      VARCHAR(32) COMMENT '粉丝所在省份',
language      VARCHAR(32) COMMENT '粉丝的语言，简体中文为zh_CN',
headimgurl    VARCHAR(256) COMMENT '粉丝的头像，值为腾讯服务器的图片URL',
subscribeTime VARCHAR(19) COMMENT '粉丝最后一次关注的时间，格式为yyyy-MM-dd HH:mm:ss',
unionid       VARCHAR(64) COMMENT '只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段',
remark        VARCHAR(64) COMMENT '公众号运营者对粉丝的备注',
groupid       VARCHAR(16) COMMENT '粉丝用户所在的分组ID',
createTime    TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
updateTime    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
UNIQUE INDEX unique_index_uid_openid(uid, openid)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='粉丝表';


DROP TABLE IF EXISTS t_reply_info;
CREATE TABLE t_reply_info(
id         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
uid        INT NOT NULL COMMENT '平台用户ID，对应t_user#id',
category   CHAR(1) NOT NULL COMMENT '回复的类别：0--通用的回复，1--关注后回复，2--关键字回复',
type       CHAR(1) NOT NULL COMMENT '回复的类型：0--文本，1--图文，2--图片，3--活动，4--转发到多客服',
keyword    VARCHAR(16) COMMENT '关键字',
content    VARCHAR(2048) COMMENT '回复的内容',
pluginId   INT COMMENT '活动插件ID，对应t_plugin#id',
createTime TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
updateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
UNIQUE INDEX unique_index_uid_keyword(uid, keyword)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='统一回复设置表';


DROP TABLE IF EXISTS t_menu_info;
CREATE TABLE t_menu_info(
id         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
pid        INT COMMENT '上一级菜单的ID，一级菜单情况下为0',
uid        INT NOT NULL COMMENT '平台用户ID，对应t_user#id',
type       CHAR(1) NOT NULL COMMENT '菜单类型：1--CLICK，2--VIEW，3--JSON',
level      CHAR(1) COMMENT '菜单级别：1--一级菜单，2--二级菜单',
name       VARCHAR(16) COMMENT '菜单名称',
viewURL    VARCHAR(256) COMMENT 'type=2时用到',
replyId    INT COMMENT 'type=1时用到，对应t_reply_info#id',
menuJson   VARCHAR(9999) COMMENT '微信或QQ公众号菜单JSON',
createTime TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
updateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='自定义菜单表';


INSERT INTO t_reply_info(uid, category, type) VALUES(2, '0', '4');
INSERT INTO t_user_info(id, pid, username, password, uuid, mptype, bindStatus) VALUES(1, 0, 'admin', '93924bf3b652dfe3e6b965639629d366', REPLACE(UUID(),'-',''), '0', '0');
INSERT INTO t_user_info(id, pid, username, password, uuid, mptype, bindStatus) VALUES(2, 1, 'wx', '71bc07cacd4751e752f8f615dccc6699', REPLACE(UUID(),'-',''), '1', '0');
INSERT INTO t_user_info(id, pid, username, password, uuid, mptype, bindStatus) VALUES(3, 1, 'qq', '3ad08eb4ecb08d3b7aad434ac504922f', REPLACE(UUID(),'-',''), '2', '0');