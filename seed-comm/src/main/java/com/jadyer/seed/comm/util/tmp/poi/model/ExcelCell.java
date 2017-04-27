package com.jadyer.seed.comm.util.tmp.poi.model;

public class ExcelCell {
    private int rowNum;
    private int cellNum;
    private Object value;
    
    public int getRowNum() {
        return rowNum;
    }
    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }
    public int getCellNum() {
        return cellNum;
    }
    public void setCellNum(int cellNum) {
        this.cellNum = cellNum;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
}