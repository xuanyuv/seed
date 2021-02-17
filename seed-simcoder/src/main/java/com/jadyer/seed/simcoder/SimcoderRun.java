package com.jadyer.seed.simcoder;

import com.jadyer.seed.simcoder.helper.GeneratorHelper;

/**
 * Seed代码生成器
 * ----------------------------------------------------------------------------------------------------------------
 * 1. 目前支持的数据库类型，详见{@link com.jadyer.seed.simcoder.helper.DBHelper#buildJavatypeFromDbtype(String)}
 * 2. 生成的Service中分页方法的条件，暂时只支持Integer、Long、String三种属性（Date和BigDecimal无法判断拿什么作为分页条件）
 * ----------------------------------------------------------------------------------------------------------------
 */
public class SimcoderRun {
    /**
     * 参数配置
     */
    // 是否生成实体类Builder（Model和DTO）
    public static final Boolean IS_GENERATE_BUILDER = Boolean.FALSE;
    // 公共类
    public static final String IMPORT_COMMRESULT         = "com.jadyer.seed.api.constant.CommResult";
    public static final String IMPORT_CONSTANTS          = "com.jadyer.seed.api.constant.SeedConstants";
    public static final String IMPORT_ENABLEFORMVALID    = "com.jadyer.seed.comm.annotation.log.EnableFormValid";
    public static final String IMPORT_ENABLELOG          = "com.jadyer.seed.comm.annotation.log.EnableLog";
    public static final String IMPORT_JPA_BASEENTITY     = "com.jadyer.seed.comm.jpa.BaseEntity";
    public static final String IMPORT_JPA_BASEREPOSITORY = "com.jadyer.seed.comm.jpa.BaseRepository";
    public static final String IMPORT_JPA_CONDITION      = "com.jadyer.seed.comm.jpa.Condition";
    public static final String IMPORT_BEANUTIL           = "com.jadyer.seed.comm.util.BeanUtil";
    // 包名（基础）
    private static final String PACKAGE_PREFIX    = "com.jadyer.seed.qss";
    // 包名（各模块）
    public static final String PACKAGE_API        = PACKAGE_PREFIX + ".api";
    public static final String PACKAGE_DTO        = PACKAGE_PREFIX + ".api.dto";
    public static final String PACKAGE_REPOSITORY = PACKAGE_PREFIX + ".repository";
    public static final String PACKAGE_MODEL      = PACKAGE_PREFIX + ".repository.model";
    public static final String PACKAGE_SERVICE    = PACKAGE_PREFIX + ".service";
    public static final String PACKAGE_CONTROLLER = PACKAGE_PREFIX + ".web.controller";
    /**
     * 数据库配置
     */
    public static final String DB_ADDRESS  = "127.0.0.1:3306";
    public static final String DB_NAME     = "qss";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "xuanyu";

    public static void main(String[] args){
        // GeneratorHelper.generate(DB_NAME, "t_");
        GeneratorHelper.generate(DB_NAME);
    }
}