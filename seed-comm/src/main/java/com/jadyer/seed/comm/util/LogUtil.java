package com.jadyer.seed.comm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * -----------------------------------------------------------------------------------------------------------
 * Log4j2.x比Logback好----http://donlianli.iteye.com/blog/1921735
 * Log4j2架构分析与实战----http://my.oschina.net/xianggao/blog/523020
 * SpringSide使用Logback--https://github.com/springside/springside4/wiki/Log
 * Java日志性能那些事------http://www.infoq.com/cn/articles/things-of-java-log-performance
 * -----------------------------------------------------------------------------------------------------------
 * 邮件发送
 * 1、log4j邮件发送的前提条件：是在log4j.properties中配置了邮件发送的一些参数，参数举例如下
 *    log4j.rootLogger=DEBUG,CONSOLE,EMAILSEND
 *    log4j.appender.EMAILSEND=org.apache.log4j.net.SMTPAppender
 *    log4j.appender.EMAILSEND.SMTPDebug=false
 *    log4j.appender.EMAILSEND.Threshold=ERROR
 *    log4j.appender.EMAILSEND.BufferSize=10
 *    log4j.appender.EMAILSEND.SMTPHost=smtp.yeah.net
 *    log4j.appender.EMAILSEND.SMTPUsername=jadyer
 *    log4j.appender.EMAILSEND.SMTPPassword=jady*****er
 *    log4j.appender.EMAILSEND.From=jadyer@yeah.net
 *    log4j.appender.EMAILSEND.To=log4j001@yeah.net,log4j002@yeah.net,log4j003@yeah.net
 *    log4j.appender.EMAILSEND.Cc=log4j004@yeah.net
 *    log4j.appender.EMAILSEND.Bcc=log4j005@yeah.net
 *    log4j.appender.EMAILSEND.Subject=【提醒】交易前置系统遇到异常
 *    log4j.appender.EMAILSEND.layout=org.apache.log4j.PatternLayout
 *    log4j.appender.EMAILSEND.layout.ConversionPattern=[%d{yyyyMMdd HH:mm:ss}][%t][%C{1}.%M]%m%n
 *    上面的配置中，由于配置日志级别是ERROR，所以执行LogUtil.getLogger().error("....")时，邮件就会被自动发出去
 * 2、这里用到的smtp.yeah.net属于第三方的SMTP服务，我们也可以在本机上装一个SMTP服务，比如：IMail/Postfix
 * 3、网上很多人说发出去的邮件，如果主题和正文有中文的话，对方收到的是乱码，不过我没遇到，可能是因为我所有地方都用的UTF-8
 * 4、log4j-1.2.14之后才支持SMTP认证，否则会报告异常，且需导入mail.jar（可通过Oracle官网下载javamail-1.4.5.zip取得）
 * 5、若报告该异常Exception in thread "main" java.lang.NoClassDefFoundError: com/sun/mail/util/LineInputStream
 *   通常是由于MyEclipse6.5自带的javaee.jar中的mail包，与我们导入的mail.jar冲突
 *   解决办法就是在myeclipse安装目录下找到javaee.jar，用WinRAR打开，删除里面的mail文件夹即可
 *   ..\myeclipse\eclipse\plugins\com.genuitec.eclipse.j2eedt.core_6.5.0.zmyeclipse650200806\data\libraryset\EE_5\javaee.jar
 * 6、Unix和Linux一般都会默认开启SMTP服务，我们也可以通过命令查看
 *   [wzf@bjgg-kfvm-31 ~]$ telnet 127.0.0.1 25
 *   Trying 127.0.0.1...
 *   Connected to localhost.localdomain (127.0.0.1).
 *   Escape character is '^]'.
 *   220 bjgg-kfvm-31.localdomain ESMTP Postfix
 *   这就表示其已经开启了SMTP服务了，不过若看到类似下面的字样，则表示其没有开启SMTP服务
 *   正在连接到127.0.0.1...无法打开到主机的连接 在端口 25 : 连接失败
 * -----------------------------------------------------------------------------------------------------------
 * 另附log4j数据库保存日志的配置
 * log4j.rootLogger=DEBUG,CONSOLE
 * log4j.logger.databaseLogger=DEBUG,DATABASE_LOG
 * log4j.additivity.databaseLogger=true
 * log4j.appender.DATABASE_LOG=org.apache.log4j.jdbc.JDBCAppender
 * log4j.appender.DATABASE_LOG.URL=jdbc:mysql://127.0.0.1:3306/jadyer?characterEncoding=UTF-8
 * log4j.appender.DATABASE_LOG.driver=com.mysql.jdbc.Driver
 * log4j.appender.DATABASE_LOG.user=root
 * log4j.appender.DATABASE_LOG.password=jadyer
 * log4j.appender.DATABASE_LOG.sql=INSERT INTO t_operate_log(message) VALUES('[%d{yyyyMMdd HH:mm:ss}][%t][%C{1}.%M]%m%n')
 * log4j.appender.DATABASE_LOG.layout=org.apache.log4j.PatternLayout
 * log4j.appender.DATABASE_LOG.layout.ConversionPattern=[%d{yyyyMMdd HH:mm:ss}][%t][%C{1}.%M]%m%n
 * -----------------------------------------------------------------------------------------------------------
 * @version v2.3
 * @history v2.3-->修复获取logger方法中，可能会获取到其它线程绑定的logger，导致获取到的不是想要的logger
 * @history v2.2-->优化Log获取为显式指定所要获取的Log,未指定时默认取上一次的Log,没有上一次的则取defaultLog
 * @history v2.1-->新增多线程情景下的日志集中打印功能
 * @history v2.0-->新增日志的数据库保存和邮件发送功能
 * @history v1.0-->通过<code>java.lang.ThreadLocal</code>实现日志记录器
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2012/11/18 18:19.
 */
public final class LogUtil {
    private static final String LOGGER_NAME_DEFAULT = "defaultLogger";
    private static final String LOGGER_NAME_WEB = "webLogger";
    private static final String LOGGER_NAME_WAP = "wapLogger";

    private LogUtil(){}

    //自定义线程范围内共享的对象
    //即它会针对不同线程分别创建独立的对象，此时每个线程得到的将是自己的实例，各线程间得到的实例没有任何关联
    private static ThreadLocal<Logger> currentLoggerMap = new ThreadLocal<>();

    //定义日志记录器
    private static Logger defaultLogger = LoggerFactory.getLogger(LOGGER_NAME_DEFAULT);
    private static Logger webLogger = LoggerFactory.getLogger(LOGGER_NAME_WEB);
    private static Logger wapLogger = LoggerFactory.getLogger(LOGGER_NAME_WAP);

    /**
     * 获取当前线程中的日志记录器
     * <p>
     *     每个线程调用全局的ThreadLocal.set()方法
     *     相当于在其内部的Map中增加一条记录，key是各自的线程，value是各自set()的值
     *     取的时候直接ThreadLocal.get()即可
     * </p>
     */
    public static Logger getLogger() {
        Logger logger = currentLoggerMap.get();
        if(null == logger){
            return defaultLogger;
        }else{
            return logger;
        }
    }


//    /**
//     * 设置日志记录器为当前会话时最初绑定的日志记录器
//     * 该方法适用于整个会话由2个或2个以上的线程处理的情景
//     * 比如Mina2.x中的exceptionCaught(IoSession session, Throwable cause)，其用法如下
//     * LogUtil.setCurrentLogger((Log)session.getAttribute("currentLog"))
//     * LogUtil.getLogger().error("请求被拒绝or请求地址有误，堆栈轨迹如下", cause)
//     * 最后，记得会话开始时，通过session.setAttribute("currentLog", LogUtil.getLogger())标记日志记录器
//     */
//    public static void setCurrentLogger(Log log){
//        currentLoggerMap.set(log);
//    }
//
//    /**
//     * 默认日志记录器
//     * 多线程情境下若想使用defaultLogger则最好在使用前调用一次该方法
//     */
//    public static void setDefaultLogger(){
//        currentLoggerMap.set(defaultLogger);
//    }


    /**
     * 获取Web端日志记录器
     */
    public static Logger getWebLogger() {
        Logger logger = currentLoggerMap.get();
        if(null!=logger && LOGGER_NAME_WEB.equals(logger.getName())){
            return logger;
        }
        currentLoggerMap.set(webLogger);
        return webLogger;
    }


    /**
     * 获取Wap端日志记录器
     */
    public static Logger getWapLogger() {
        Logger logger = currentLoggerMap.get();
        if(null!=logger && LOGGER_NAME_WAP.equals(logger.getName())){
            return logger;
        }
        currentLoggerMap.set(wapLogger);
        return wapLogger;
    }
}