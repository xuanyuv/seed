package com.jadyer.seed.open.constant;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/8 21:13.
 */
public interface OpenConstant {
    /**
     * 全局编码
     */
    String CHARSET_UTF8 = "UTF-8";
    /**
     * API协议版本
     */
    String VERSION_21 = "2.1";
    String VERSION_22 = "2.2";
    /**
     * 签名算法
     */
    String SIGN_TYPE_md5 = "md5";
    String SIGN_TYPE_hmac = "hmac";
    /**
     * 服务名称
     */
    String METHOD_boot_apidoc_h5 = "boot.apidoc.h5";
    String METHOD_boot_file_upload = "boot.file.upload";
    String METHOD_boot_loan_agree = "boot.loan.agree";
    String METHOD_boot_loan_report_download = "boot.loan.report.download";
    String METHOD_boot_loan_submit = "boot.loan.submit";
    String METHOD_boot_loan_get = "boot.loan.get";
    String METHOD_boot_loan_sign = "boot.loan.sign";
    String METHOD_boot_contract_get = "boot.contract.get";
    String METHOD_boot_product_list = "boot.product.list";
    String METHOD_boot_user_bindcard = "boot.user.bindcard";
    String METHOD_boot_user_blacklist_get = "boot.user.blacklist.get";
}