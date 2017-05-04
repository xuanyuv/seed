package com.jadyer.seed.comm.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 操作码
 * Created by 玄玉<http://jadyer.cn/> on 2015/6/3 21:26.
 */
public enum CodeEnum {
    SUCCESS              (0,    "成功"),
    SYSTEM_OK            (1000, "保留码"),
    SYSTEM_BUSY          (1001, "系统繁忙"),
    SYSTEM_ERROR         (1002, "系统错误"),
    FILE_NOT_FOUND       (1003, "文件未找到"),
    FILE_TRANSFER_FAIL   (1004, "文件传输失败"),
    OPEN_UNKNOWN_APPID   (2001, "未知的合作方"),
    OPEN_UNKNOWN_VERSION (2002, "未知的协议版本"),
    OPEN_UNKNOWN_SIGN    (2003, "未知的签名算法"),
    OPEN_UNKNOWN_METHOD  (2004, "未知的接口请求"),
    OPEN_TIMESTAMP_ERROR (2005, "时间戳异常"),
    OPEN_SIGN_ERROR      (2006, "验签拒绝"),
    OPEN_DECRYPT_FAIL    (2007, "解密失败"),
    OPEN_FORM_ILLEGAL    (2008, "表单验证未通过"),
    OPEN_UNGRANT_API     (2009, "未授权的接口"),
    OPEN_UNGRANT_DATA    (2010, "未授权的数据访问"),
    OPEN_PROCESSING      (2011, "请求处理中");

    private final int code;
    private final String msg;

    CodeEnum(int _code, String _msg){
        this.code = _code;
        this.msg = _msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 通过枚举code获取对应的msg
     * @return 取不到时返回null
     */
    public static String getMsgByCode(int code){
        for(CodeEnum _enum : values()){
            if(_enum.getCode() == code){
                return _enum.getMsg();
            }
        }
        return null;
    }

    /**
     * 通过枚举code获取枚举对象
     * @return 取不到时返回null
     */
    public static CodeEnum getByCode(int code){
        for(CodeEnum _enum : values()){
            if(_enum.getCode() == code){
                return _enum;
            }
        }
        return null;
    }

    /**
     * 获取全部枚举
     * @return 取不到时返回空List=new ArrayList<>()
     */
    public List<CodeEnum> getAllEnum(){
        List<CodeEnum> list = new ArrayList<>();
        Collections.addAll(list, values());
        return list;
    }

    /**
     * 获取全部枚举code
     * @return 取不到时返回空List=new ArrayList<Integer>()
     */
    public List<Integer> getAllEnumCode(){
        List<Integer> list = new ArrayList<>();
        for(CodeEnum _enum : values()){
            list.add(_enum.getCode());
        }
        return list;
    }
}