# seed [![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/jadyer/seed/blob/master/LICENSE)
玄玉的个人总结<br/><br/>


## 模块列表

* [x] seed-admin：SpringBootAdmin服务端
* [x] seed-boot：SpringBoot个人实践
* [x] seed-comm：工具包
* [x] seed-mpp：公众平台
* [x] seed-open：开放平台
* [x] seed-qss：定时系统（Quartz Scheduler System）
* [x] seed-server：Mina实现的服务器
* [x] seed-simcoder：简版的代码生成器
* [x] seed-simulator：Swing实现的模拟器

## 依赖关系

* .
* ├── seed-admin
* ├── seed-boot
* │   └── seed-comm
* ├── seed-mpp
* │   └── seed-comm
* ├── seed-open
* │   └── seed-comm
* ├── seed-qss
* │   └── seed-comm
* ├── seed-server
* │   └── seed-comm
* ├── seed-simcoder
* │   └── seed-comm
* └── seed-simulator
*  　　└── seed-comm

## 版本迭代

> ### version 2.1.RELEASE
>> * upgrade to beetl-2.9.7
>> * upgrade to fastjson-1.2.54
>> * upgrade to fredisson-3.10.0
>> * upgrade to fspringboot-2.1.2
>> * upgrade to fspringbootadmin-2.1.2
>> * upgrade to fcommons-fileupload-1.4
>> * 删除seed-scs（Sample Code System）脚手架模块
>
> ### version 1.4.RELEASE
>> * 修复前一版中走了弯路导致的若干BUG
>> * 增加后台管理模块，用于管理和发布微信功能
>> * 引入jssdk并编写了一个调用手机相机而不调用相册的例子
>> * 优化WeixinFilter静默方式网页授权获取用户信息的流程，使得前端只需一个Filter不需引入SDK
>> * 微信后台增加防伪标记，只有绑定到平台的公众号才提供服务，避免开发者URL被破译后盗用服务
>> * 缓存微信和网页授权access_token等生命周期较长的数据信息，到Weixin和QQ各自的TokenHolder中
>> * TokenHolder引入java.util.concurrent.atomic.AtomicBoolean，保证更新Token过程中，旧的Token可用
>> * 增加QQ公众平台SDK（2015.11.26），实现基本的收发文本消息、自定义菜单监听、关注与取关等功能
>
> ### version 1.3.RELEASE
>> * 花了周日和周一共2天时间开发完微信SDK，含以下功能（2015.10.19）：
>> * 处理接收到的文本消息、图片消息、地址位置消息、链接消息、关注/取消关注事件
>> * 处理自定义菜单拉取消息/跳转链接事件、多客服接入会话/关闭会话/转接会话事件
>> * 可自定义回复图片、多图文、纯文本、带表情的文本，或将消息转发给多客服客户端
>> * 可主动拉去指定的粉丝信息、推消息给粉丝（48小时内有过交互）、创建自定义菜单
>> * 可通过网页授权静默获取粉丝openid（web.xml配置一个Filter即可，不需要其它代码）
>
> ### version 1.2.RELEASE
>> * 打包工具增加打包后重命名文件并自动上传FTP的功能（deploy.bat）
>> * 升级依赖包版本至spring-boot-1.3.8、jedis-2.9.0、druid-1.0.26、fastjson-1.2.21、commons-lang3-3.5
>> * 添加**md5.js**和**ajaxfileupload.js**实现前端加密字符串以及异步文件上传
>> * Druid集成时增加自定义数据库连接池参数值的配置
>> * logback.xml中增加通过环境变量进行条件判断的用法
>> * 增加Swagger集成Spring-boot方法，及其api-doc写法的demo
>> * 增加TokenBucket算法实现的限流帮助类
>> * Redis3.0实现的分布式锁增加另一种用法（即**V2**帮助类）
>> * 优化用于缓存Open接口应答内容的RedisFilter实现细节
>> * 修复500页面显示时，由于无堆栈异常导致打印报错的问题
>> * 修复从RedisCluster中读取会话数据时，由于无会话导致反序列化失败的问题
>> * 修复JPA条件查询工具Condition.java在处理**NE**条件后，没有**break**导致查询失败的问题
>
> ### version 1.1.RELEASE
>> * 通过参照SpringSide实现的Condition.java优化JPA分页条件查询的实现方式
>
> ### version 1.0.RELEASE
>> * tag for publish first