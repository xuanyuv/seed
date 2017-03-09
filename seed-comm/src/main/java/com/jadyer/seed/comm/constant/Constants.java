package com.jadyer.seed.comm.constant;

/**
 * 接口中属性都是public static final的，即public static final int id=1;等价于int id=1;
 * 接口中的方法都是public abstract的，即public abstract void start();等价于void start();
 * Created by 玄玉<https://jadyer.github.io/> on 2015/08/27 20:21.
 */
public interface Constants {
    /**
     * Web应用的会话标志
     */
    String WEB_SESSION_USER = "user";

    /**
     * 下面是seed-open用到的常量
     */
    //seed-open的全局编码
    String OPEN_CHARSET_UTF8 = "UTF-8";
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