package com.jadyer.seed.comm.util.tmp.poi;

public class ExcelProperty {
    /**
     * 指定工作空间名
     */
    private String defaultSheetName;
    /**
     * 文件存在时是否需要覆盖
     */
    private boolean isCreatNew;
    /**
     * 是否跳过首行
     */
    private boolean ignorHeader;

    public String getDefaultSheetName() {
        return defaultSheetName;
    }
    public void setDefaultSheetName(String defaultSheetName) {
        this.defaultSheetName = defaultSheetName;
    }
    public boolean isCreatNew() {
        return isCreatNew;
    }
    public void setCreatNew(boolean isCreatNew) {
        this.isCreatNew = isCreatNew;
    }
    public boolean isIgnorHeader() {
        return ignorHeader;
    }
    public void setIgnorHeader(boolean ignorHeader) {
        this.ignorHeader = ignorHeader;
    }
}