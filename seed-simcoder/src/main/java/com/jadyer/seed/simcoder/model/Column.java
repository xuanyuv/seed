package com.jadyer.seed.simcoder.model;

/**
 * 列信息
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/7 17:23.
 */
public class Column {
    /** 列的名称 */
    private String name;
    /** 列的备注 */
    private String comment;
    /** 列的数据类型 */
    private String type;
    /** 列的最大长度（比如TINYINT(1)类型，其最大值为127，故此时length=3，另：当type=longtext时该值固定为999999999） */
    private int length;
    /** 列是否可空（true为可空，false为不可空） */
    private boolean nullable;
    /** 是否主键 */
    private boolean isPrikey;
    /** 是否自增长 */
    private boolean isAutoIncrement;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isPrikey() {
        return isPrikey;
    }

    public void setPrikey(boolean prikey) {
        isPrikey = prikey;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }
}