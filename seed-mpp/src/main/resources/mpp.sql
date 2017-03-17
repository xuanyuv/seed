--集成方式
--
--1.若微信H5客户端和后台管理的服务端是一个应用，那就不需要在web.xml中配置WeixinFilter
--2.若二者分离（即目前流行的前后端分离），则前端需要在web.xml中配置WeixinFilter，后端不需要
--3.上面提到的后端，对应的就是JadyerSDK-demo的Module
--
--更新日志
--
--[v1.1.1] 2015.12.23
--1.优化WeixinFilter静默方式网页授权获取用户信息的流程，使得前端只需一个Filter不需引入SDK
--
--[v1.1.0] 2015.11.26
--1.增加QQ公众平台SDK，实现基本的收发文本消息、自定义菜单监听、关注与取消关注等功能
--
--[v1.0.6] 2015.11.26
--1.为下个版本增加的QQSDK而重命名并区分微信SDK，因为QQ公众平台最后一定会与微信的不同
--
--[v1.0.5] 2015.11.19
--1.修复1.0.4中微信媒体文件下载接口采用ResponseEntity<byte[]>返回流导致图片无法打开的问题
--
--[v1.0.4] 2015.11.09
--1.修复1.0.3版中走了弯路导致的若干BUG
--2.TokenHolder引入java.util.concurrent.atomic.AtomicBoolean，保证更新Token过程中的旧Token可用
--3.微信后台增加防伪标记，只有绑定到平台的公众号才提供服务，避免开发者URL被破译后，盗用服务
--
--[v1.0.3] 2015.11.03
--1.增加集成方式的简单描述
--2.引入jssdk并编写了一个调用手机相机而不调用相册的例子
--3.WeixinFilter增加dataurl参数，用于指定H5前端引入的SDK中TokenHolder数据的获取源
--4.缓存微信access_token和网页授权access_token等生命周期较长的数据信息到TokenHolder中
--5.合并微信相关Filter到一个WeixinFilter中，用于初始化appid和网页授权时回调的URL等预置数据
--
--[v1.0.2] 2015.10.24
--1.demo中增加后台管理模块，用于管理和发布微信功能
--
--[v1.0.1] 2015.10.20
--1.增加名为demo的Moudle用于演示微信SDK的便捷用法
--2.下一个版本准备在demo中增加一个后台管理模块
--3.再下一个版本准备增加QQ公众平台SDK
--4.再再下一个版本准备支持多用户
--
--[v1.0.0] 2015.10.19
--1.花了一个礼拜天和一个周一共2天时间开发完微信SDK，含以下功能：
--2.处理接收到的文本消息、图片消息、地址位置消息、链接消息、关注 / 取消关注事件
--3.处理自定义菜单拉取消息/跳转链接事件、多客服接入会话 / 关闭会话 / 转接会话事件
--4.可自定义回复图片、多图文、纯文本、带表情的文本，或将消息转发给多客服客户端
--5.可主动拉去指定的粉丝信息、推消息给粉丝（48小时内有过交互）、创建自定义菜单
--6.可通过网页授权静默获取粉丝openid（web.xml配置一个Filter即可，不需要其它代码）


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