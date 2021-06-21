package com.jadyer.seed.simcoder.helper;

import java.util.Random;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2021/6/21 13:37.
 */
public final class CoderHelper {
    private CoderHelper(){}

    /**
     * 构建serialVersionUID
     */
    public static long buildSerialVersionUID(){
        // long serialVersionUID = new Random().nextLong();
        // return serialVersionUID>0 ? serialVersionUID : -serialVersionUID;
        return new Random().nextLong();
    }
}