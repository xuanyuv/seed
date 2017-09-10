package com.jadyer.seed.simcoder;

import com.jadyer.seed.simcoder.helper.CodeGenHelper;

public class SimcoderRun {
    public static void main(String[] args){
        CodeGenHelper.generateFromTable("t_mpp_fans_info", "意见22反馈表");
        CodeGenHelper.generateFromDatabase("mpp");
    }
}