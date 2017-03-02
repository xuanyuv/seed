package com.jadyer.seed.qss.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * <p>完整版见https://github.com/jadyer/JadyerEngine/blob/master/JadyerEngine-common/src/main/java/com/jadyer/engine/common/util/LogUtil.java</p>
 * @version v2.3
 * @history v2.3-->修复获取logger方法中，可能会获取到其它线程绑定的logger，导致获取到的不是想要的logger
 * @history v2.2-->优化Log获取为显式指定所要获取的Log,未指定时默认取上一次的Log,没有上一次的则取defaultLog
 * @history v2.1-->新增多线程情景下的日志集中打印功能
 * @history v2.0-->新增日志的数据库保存和邮件发送功能
 * @history v1.0-->通过<code>java.lang.ThreadLocal</code>实现日志记录器
 * @update Aug 26, 2015 3:29:21 PM
 * @create Dec 18, 2012 6:19:31 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public final class LogUtil {
	private static final String LOGGER_NAME_APP = "appLogger";

	private LogUtil(){}

	private static ThreadLocal<Logger> currentLoggerMap = new ThreadLocal<>();

	private static Logger appLogger = LoggerFactory.getLogger(LOGGER_NAME_APP);

	public static Logger getAppLogger() {
		Logger logger = currentLoggerMap.get();
		if(null!=logger && LOGGER_NAME_APP.equals(logger.getName())){
			return logger;
		}
		currentLoggerMap.set(appLogger);
		return appLogger;
	}
}