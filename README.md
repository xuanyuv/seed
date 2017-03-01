# seed [![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/jadyer/seed/blob/master/LICENSE)
玄玉的个人总结<br/><br/>

## 功能列表

据不完全统计，本实践至少包含以下功能

* 整合Fastjson
* 整合Druid
* 集成JPA
* 集成RestTemplate
* 集成并封装SpringHttpInvoker
* 使用JSP自定义标签
* 停用JSP
* 啟用thymeleaf
* 啟用Jasypt
* 集成H2
* 啟用H2-Console
* 集成Swagger
* 集成RedisCluster（這裡用的是Redis3.0）
* 啟用会话共享到RedisCluster（這裡用的是Redis3.0）
* Redis3.0实现的分布式锁
* Redis3.0**+**Lua結合TokenBucket算法实現的限流

## 版本迭代
> ### version 2.6.2.RELEASE
>> * 新增自定义启动Banner示例
>> * 重新梳理com.jadyer.demo.boot包结构
>> * 升级到fastjson-1.2.23、druid-1.0.27、spring-boot-1.4.3.RELEASE
>
> ### version 2.6.1.RELEASE
>> * 新增网站建设中页面
>> * 新增wangEditor的使用例子
>> * 新增封装SpringHttpInvoker的注解
>
> ### version 2.6.0.RELEASE
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
> ### version 2.5.2.RELEASE
>> * SpringBoot升級至1.3.6
>> * 增加自定義404和500頁面
>> * 增加JSP自定義標籤以及配置
>> * 增加Redis實現的分佈式鎖工具類
>
> ### version 2.5.1.RELEASE
>> * 通过参照SpringSide实现的Condition.java优化JPA分页条件查询的实现方式
>
> ### version 2.5.0.RELEASE
>> * 增加会话共享到RedisCluster以及简洁的管理系统权限校验
>
> ### version 2.4.1.RELEASE
>> * 开放平台增加RSA加解密报文的支持
>
> ### version 2.4.0.RELEASE
>> * 增加H2用法並修復部分細節
>
> ### version 2.3.1.RELEASE
>> * 增加Jasypt實現配置文件屬性加解密讀取
>> * 升級SprinBoot至最新版1.3.5.RELEASE
>
> ### version 2.3.0.RELEASE
>> * 增加开放平台接口/router/rest及接口文档
>
> ### version 2.2.0.RELEASE
>> * tag for publish first