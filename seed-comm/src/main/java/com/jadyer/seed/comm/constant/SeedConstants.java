package com.jadyer.seed.comm.constant;

/**
 * 接口中属性都是public static final的，即public static final int id=1;等价于int id=1;
 * 接口中的方法都是public abstract的，即public abstract void start();等价于void start();
 * Created by 玄玉<https://jadyer.cn/> on 2015/08/27 20:21.
 */
public interface SeedConstants {
    String BOOT_ACTIVE_NAME = "spring.profiles.active";
    String BOOT_ACTIVE_DEFAULT_VALUE = "local";

    /**
     * Web应用的会话标志，及后台的焦点菜单
     */
    String WEB_SESSION_USER = "user";
    String WEB_CURRENT_MENU = "currentMenu";
    String WEB_CURRENT_SUB_MENU = "currentSubMenu";

    /**
     * 微信或QQ公众平台绑定时发送的文本指令
     */
    String MPP_BIND_TEXT = "我是玄玉";

    /**
     * seed-qss用到的常量
     */
    int QSS_STATUS_STOP        = 0; //停止
    int QSS_STATUS_RUNNING     = 1; //启动
    //int QSS_STATUS_PAUSE     = 2; //暂停
    //int QSS_STATUS_RESUME    = 3; //暂停后恢复
    int QSS_CONCURRENT_NO      = 0; //禁止并发执行
    int QSS_CONCURRENT_YES     = 1; //允许并发执行
    String QSS_JOB_DATAMAP_KEY = "qss"; //存放在Quartz测JobDataMap中的key
    String CHANNEL_SUBSCRIBER  = "qss_jedis_pubsub_channel";

    /**
     * seed-open用到的常量
     */
    //seed-open的API协议版本
    String OPEN_VERSION_21 = "2.1";
    String OPEN_VERSION_22 = "2.2";
    //seed-open的签名算法
    String OPEN_SIGN_TYPE_md5 = "md5";
    String OPEN_SIGN_TYPE_hmac = "hmac";
    //seed-open的服务名称
    String OPEN_METHOD_boot_apidoc_h5 = "boot.apidoc.h5";
    String OPEN_METHOD_boot_file_upload = "boot.file.upload";
    String OPEN_METHOD_boot_loan_agree = "boot.loan.agree";
    String OPEN_METHOD_boot_loan_report_download = "boot.loan.report.download";
    String OPEN_METHOD_boot_loan_submit = "boot.loan.submit";
    String OPEN_METHOD_boot_loan_get = "boot.loan.get";
    String OPEN_METHOD_boot_loan_sign = "boot.loan.sign";
    String OPEN_METHOD_boot_contract_get = "boot.contract.get";
    String OPEN_METHOD_boot_product_list = "boot.product.list";
    String OPEN_METHOD_boot_user_bindcard = "boot.user.bindcard";
    String OPEN_METHOD_boot_user_blacklist_get = "boot.user.blacklist.get";
}