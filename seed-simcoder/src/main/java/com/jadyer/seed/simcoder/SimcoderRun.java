package com.jadyer.seed.simcoder;

import com.jadyer.seed.simcoder.helper.GenerateHelper;

/**
 * TODO：遗留问题：若某表主键为BIGINT，那么需要手工将生成的代码中id改为BigInteger（目前都是Long）
 */
public class SimcoderRun {
    public static final String PACKGET_PREFIX = "com.jadyer.seed.mpp";
    public static final String DB_ADDRESS = "127.0.0.1:3306";
    public static final String DB_NAME = "mpp";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "xuanyu";

    public static void main(String[] args){
        //GenerateHelper.generate(DB_NAME, "t_mpp_user_info", "t_mpp_fans_info");
        GenerateHelper.generate(DB_NAME);
    }
}