package com.jadyer.seed.simcoder;

import com.jadyer.seed.simcoder.helper.GenerateHelper;

/**
 * Seed代码生成器
 * ----------------------------------------------------------------------------------------------------------------
 * 1. 目前支持的数据库类型，详见{@link com.jadyer.seed.simcoder.helper.DBHelper#buildJavatypeFromDbtype(String)}
 * 2. 生成的Service中分页方法的条件，暂时只支持Integer、Long、String三种属性（Date和BigDecimal无法判断拿什么作为分页条件）
 * ----------------------------------------------------------------------------------------------------------------
 */
public class SimcoderRun {
    public static final String PACKGET_PREFIX = "com.jadyer.seed.qss";
    public static final String DB_ADDRESS = "127.0.0.1:3306";
    public static final String DB_NAME = "qss";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "xuanyu";

    public static void main(String[] args){
        //GenerateHelper.generate(DB_NAME);
        GenerateHelper.generate(DB_NAME, "t_schedule_log");
    }
}