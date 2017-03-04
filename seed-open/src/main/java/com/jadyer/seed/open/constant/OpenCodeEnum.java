package com.jadyer.seed.open.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 接口应答码
 * <p>1打头表示公共应答码,2打头表示内部业务逻辑码</p>
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/8 20:38.
 */
public enum OpenCodeEnum {
    SUCCESS            (0, "操作成功"),
    UNUSED             (100, "暂未使用的保留域"),
    SYSTEM_BUSY        (101, "系统繁忙"),
    UNKNOWN_APPID      (102, "未知的合作方"),
    UNKNOWN_VERSION    (103, "未知的协议版本"),
    UNKNOWN_SIGN       (104, "未知的签名算法"),
    UNKNOWN_METHOD     (105, "未知的接口请求"),
    TIMESTAMP_ERROR    (106, "时间戳异常"),
    SIGN_ERROR         (107, "验签拒绝"),
    DECRYPT_FAIL       (108, "解密失败"),
    FORM_ILLEGAL       (109, "表单验证未通过"),
    UNGRANT_API        (110, "未授权的接口"),
    UNGRANT_DATA       (111, "未授权的数据访问"),
    PROCESSING         (112, "请求处理中"),
    FILE_NOT_FOUND     (201, "文件不存在"),
    FILE_TRANSFER_FAIL (202, "文件传输失败");

    private final int code;

    private final String msg;

    OpenCodeEnum(int _code, String _msg){
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
     * @create 2015-6-8 下午3:41:47
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static String getMsgByCode(int code){
        for(OpenCodeEnum _enum : values()){
            if(_enum.getCode() == code){
                return _enum.getMsg();
            }
        }
        return null;
    }


    /**
     * 通过枚举code获取枚举对象
     * @return 取不到时返回null
     * @create 2015-6-3 下午9:32:51
     * @author 玄玉<https://jadyer.github.io/>
     */
    public static OpenCodeEnum getByCode(int code){
        for(OpenCodeEnum _enum : values()){
            if(_enum.getCode() == code){
                return _enum;
            }
        }
        return null;
    }


    /**
     * 获取全部枚举
     * @return 取不到时返回空List
     * @create 2015-6-3 下午9:35:17
     * @author 玄玉<https://jadyer.github.io/>
     */
    public List<OpenCodeEnum> getAllEnum(){
        List<OpenCodeEnum> list = new ArrayList<>();
        Collections.addAll(list, values());
        return list;
    }


    /**
     * 获取全部枚举code
     * @return 取不到时返回空List,即new ArrayList<Integer>()
     * @create 2015-6-3 下午9:57:28
     * @author 玄玉<https://jadyer.github.io/>
     */
    public List<Integer> getAllEnumCode(){
        List<Integer> list = new ArrayList<>();
        for(OpenCodeEnum _enum : values()){
            list.add(_enum.getCode());
        }
        return list;
    }
}
