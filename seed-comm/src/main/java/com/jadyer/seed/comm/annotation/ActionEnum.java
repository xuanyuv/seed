package com.jadyer.seed.comm.annotation;

/**
 * 动作类型
 * Created by 玄玉<https://jadyer.github.io/> on 2017/6/20 16:57.
 */
public enum ActionEnum {
    OTHER  (0, "其它"),
    LIST   (1, "列表查看"),
    GET    (2, "详情查看"),
    ADD    (3, "新增"),
    UPDATE (4, "更新"),
    UPSERT (5, "新增or更新"),
    DELETE (6, "删除");

    private final int code;
    private final String msg;

    ActionEnum(int _code, String _msg){
        this.code = _code;
        this.msg = _msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}