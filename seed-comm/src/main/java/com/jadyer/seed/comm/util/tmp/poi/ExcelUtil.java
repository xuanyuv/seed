package com.jadyer.seed.comm.util.tmp.poi;

import com.jadyer.seed.comm.util.tmp.poi.annotation.ExcelHeader;
import com.jadyer.seed.comm.util.tmp.poi.converts.ExcelDataConvert;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExcelUtil {
    /**
     * <标题，字段>
     */
    private Map<String, String> titleFieldMap = new HashMap<String, String>();
    /**
     * <字段，列号>
     */
    private Map<String, Integer> fieldColumnMap = new HashMap<String, Integer>();
    /**
     * <标题，列号>
     */
    private Map<String, Integer> titleColumnMap = new HashMap<String, Integer>();
    /**
     * <标题，字段实体>
     */
    private Map<String,Field> titleFieldobjMap = new  HashMap<String, Field>();
    /**
     * 表头信息是否初始化
     */
    private boolean isInitHeader = false;
    /**
     * 第一个空行行号
     */
    private int emptyRownum = 0;
    private InputStream is;
    private Workbook workbook;
    private String excelFile;
    /**
     * 默认的sheetName
     */
    private String sheetName = "Sheet";
    /**
     * 传入文件路径对应的文件存在时,是否需要创建新文件覆盖原有文件
     * @see true--覆盖,false--不覆盖
     */
    private boolean isCreatNew = false;
    private boolean ignorHeader = false;

    public ExcelUtil(String fileName){
        try {
            this.excelFile = fileName;
            creatExcel(fileName);
            this.is = new FileInputStream(fileName);
            this.workbook = WorkbookFactory.create(is);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(InvalidFormatException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public ExcelUtil(String fileName, ExcelProperty excelProperty){
        try{
            String sheetName = excelProperty.getDefaultSheetName();
            boolean isCreatNew = excelProperty.isCreatNew();
            boolean ignorHeader = excelProperty.isIgnorHeader();
            this.excelFile = fileName;
            this.sheetName = sheetName;
            this.isCreatNew = isCreatNew;
            this.ignorHeader = ignorHeader;
            creatExcel(fileName);
            this.is = new FileInputStream(fileName);
            this.workbook = WorkbookFactory.create(is);
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch(InvalidFormatException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void creatExcel(String fileName) throws IOException {
        File file = new File(fileName);
        if(file.exists() && !this.isCreatNew){
            return;
        }
        fileName=fileName.replaceAll("\\\\", "/");
        int folderflag = fileName.lastIndexOf("/");
        if(folderflag > 0){
            String newLocalFile = fileName.substring(0, folderflag);
            File filePath = new File(newLocalFile);
            if(!filePath.isDirectory()){
                filePath.mkdirs();
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        HSSFWorkbook wb=new HSSFWorkbook();
        wb.createSheet();
        wb.setSheetName(0, this.sheetName);
        wb.write(fos);
        fos.close();
    }


    /**
     * 将单个对象写入Excel
     */
    public void saveToExcel(String sheetName, Object obj) throws IllegalArgumentException, IllegalAccessException, IOException {
        if(StringUtils.isNotBlank(sheetName)){
            this.sheetName = sheetName;
        }
        Sheet sheet = workbook.getSheet(this.sheetName);
        if(null == sheet){
            sheet = workbook.createSheet(this.sheetName);
        }
        //初始化表头
        this.initHeader(obj.getClass());
        //解析对象信息,返回对象数据
        Map<String, Field> data = this.getDataMap(obj);
        if(!this.ignorHeader){
            //写入表头数据
            if(!isInitHeader){
                writeHeader(sheet);
            }
        }
        // 写入数据
        this.writeDataToCell(sheet, obj, data);
        FileOutputStream fos = new FileOutputStream(excelFile);
        workbook.write(fos);
        if(null != fos){
            fos.close();
        }
    }


    /**
     * 将单个对象写入Excel
     */
    public void saveToExcel(String sheetName, Object obj, OutputStream outputStream) throws IllegalArgumentException, IllegalAccessException, IOException {
        if(StringUtils.isNotBlank(sheetName)){
            this.sheetName = sheetName;
        }
        Sheet sheet = workbook.getSheet(this.sheetName);
        if(null == sheet){
            sheet = workbook.createSheet(this.sheetName);
        }
        //初始化表头
        this.initHeader(obj.getClass());
        //解析对象信息,返回对象数据
        Map<String, Field> data = this.getDataMap(obj);
        if(!this.ignorHeader){
            //写入表头数据
            if(!isInitHeader){
                this.writeHeader(sheet);
            }
        }
        //写入数据
        this.writeDataToCell(sheet, obj, data);
        workbook.write(outputStream);
    }


    /**
     * 将多个对象写入Excel
     */
    public <T> void saveListToExcel(String sheetName, List<T> list) throws IllegalArgumentException, IllegalAccessException, IOException {
        if(StringUtils.isNotBlank(sheetName)){
            this.sheetName = sheetName;
        }
        Sheet sheet = workbook.getSheet(this.sheetName);
        if(null == sheet){
            sheet = workbook.createSheet(this.sheetName);
        }
        if(null==list || list.isEmpty()){
            return;
        }
        this.initHeader(list.get(0).getClass());
        for(Object obj : list){
            Map<String, Field> data = this.getDataMap(obj);
            if(!this.ignorHeader){
                //写入表头数据
                if(!isInitHeader){
                    this.writeHeader(sheet);
                }
            }
            //写入数据
            this.writeDataToCell(sheet, obj, data);
        }
        FileOutputStream fos = new FileOutputStream(excelFile);
        workbook.write(fos);
        if(null != fos){
            fos.close();
        }
    }


    /**
     * 将多个对象写入Excel
     */
    public <T> void saveListToExcel(String sheetName, List<T> list, OutputStream outputStream) throws IllegalArgumentException, IllegalAccessException, IOException {
        if(StringUtils.isNotBlank(sheetName)){
            this.sheetName = sheetName;
        }
        Sheet sheet = workbook.getSheet(this.sheetName);
        if(null == sheet){
            sheet = workbook.createSheet(this.sheetName);
        }
        if(null==list || list.isEmpty()){
            return;
        }
        this.initHeader(list.get(0).getClass());
        for(Object obj : list){
            Map<String, Field> data = getDataMap(obj);
            if(!this.ignorHeader){
                //写入表头数据
                if(!isInitHeader){
                    this.writeHeader(sheet);
                }
            }
            //写入数据
            this.writeDataToCell(sheet, obj, data);
        }
        workbook.write(outputStream);
    }


    /**
     * 初始化表头数据
     */
    private void writeHeader(Sheet sheet){
        //写入表头数据
        Row row = sheet.getRow(0);
        if(null == row){
            row = sheet.createRow(0);
        }
        Set<String> titleSet = this.titleColumnMap.keySet();
        for(String obj : titleSet) {
            int column = this.titleColumnMap.get(obj);
            Cell cell = row.createCell(column);
            cell.setCellValue(obj);
            System.out.println("第[" + column + "]列写入表头[" + obj + "]");
        }
        isInitHeader = true;
        emptyRownum++;
    }


    /**
     * 将数据写入excel
     */
    private void writeDataToCell(Sheet sheet, Object obj, Map<String, Field> data){
        Row dataRow = this.getEmptyRow(sheet);
        Set<String> dataTitleSet = data.keySet();
        for(String title : dataTitleSet){
            try{
                int column = this.titleColumnMap.get(title);
                Cell dataCell = dataRow.createCell(column);
                //取字段
                Field field = data.get(title);
                //取字段值对象
                Object fieldValue = field.get(obj);
                //将字段数据写入单元格
                setFieldValue(dataCell, fieldValue, field);
                System.out.println("第[" + column + "]列写入数据[" + field + "]");
            }catch(ParseException e){
                e.printStackTrace();
            }catch(IllegalArgumentException e){
                e.printStackTrace();
            }catch(IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 将字段值写入单元格
     */
    private void setFieldValue(Cell cell, Object data, Field field) throws ParseException {
        String fieldType = field.getType().getName();
        if(null == data){
            return;
        }
        if("boolean".equals(fieldType) || "java.lang.Boolean".equals(fieldType)){
            boolean setValue = ExcelDataConvert.convertToBoolean(data);
            cell.setCellValue(setValue);
        }else if("double".equals(fieldType) || "java.lang.Double".equals(fieldType)){
            double setValue = ExcelDataConvert.convertToDouble(data);
            cell.setCellValue(setValue);
        }else if("java.util.Calendar".equals(fieldType)){
            cell.setCellValue((Calendar)data);
        }else if("java.util.Date".equals(fieldType)){
            cell.setCellValue((Date)data);
        }else if("org.apache.poi.ss.usermodel.RichTextString".equals(fieldType)){
            //RichTextString
        }else{
            String dataStr = String.valueOf(data);
            dataStr = ExcelDataConvert.convertToDefinedType(dataStr, field);
            cell.setCellValue(dataStr);
        }
    }


    /**
     * 获取最近一个可用空行
     */
    private Row getEmptyRow(Sheet sheet){
        Row row = null;
        int lastRownum = sheet.getLastRowNum();
        System.out.println("lastRownum is " + lastRownum);
        System.out.println("emptyRownum is " + emptyRownum);
        if(lastRownum < emptyRownum){
            row = sheet.createRow(emptyRownum);
        }else{
            for(int i=emptyRownum; i<=lastRownum; i++){
                Row tempRow = sheet.getRow(i);
                if(null == tempRow){
                    //空行对象,创建新行后返回
                    row = sheet.createRow(i);
                    emptyRownum = i;
                    break;
                }
                int firstCellnum = tempRow.getFirstCellNum();
                if(firstCellnum < 0){
                    //无单元格,创建新行后返回
                    row = sheet.createRow(i);
                    emptyRownum = i;
                    break;
                }
                boolean isNullRow = true;
                int lastCellnum = tempRow.getLastCellNum();
                for(int j=firstCellnum; j<=lastCellnum; j++){
                    Cell cell = tempRow.getCell(j);
                    String cellValue = cell.getStringCellValue();
                    if(StringUtils.isNotBlank(cellValue)){
                        isNullRow = false;
                        break;
                    }
                }
                if(isNullRow){
                    //空行,返回
                    emptyRownum = i;
                    break;
                }
            }
            if(null == row){
                row = sheet.createRow(lastRownum + 1);
                emptyRownum = lastRownum + 1;
            }
        }
        emptyRownum++;
        System.out.println("returnemptyRownum " + emptyRownum);
        System.out.println("dataRownum is " + row.getRowNum());
        return row;
    }


    private Row getNotEmptyRow(Sheet sheet){
        Row row = null;
        int firstrownum = sheet.getFirstRowNum();
        int lastrownum = sheet.getLastRowNum();
        for(int i=firstrownum; i<=lastrownum; i++){
            Row tempRow = sheet.getRow(i);
            if(null != tempRow){
                int lastCellnum = tempRow.getLastCellNum();
                int firstCellnum = tempRow.getFirstCellNum();
                for(int j=firstCellnum; j<=lastCellnum; j++){
                    Cell cell = tempRow.getCell(j);
                    String cellValue = cell.getStringCellValue();
                    if(StringUtils.isNotBlank(cellValue)){
                        return tempRow;
                    }
                }
            }
        }
        return row;
    }


    /**
     * 读取指定行
     */
    public Map<String, String> readLine(Class<?> clazz, String sheetName, int rownum){
        if(StringUtils.isNotBlank(sheetName)){
            this.sheetName = sheetName;
        }
        Sheet sheet = workbook.getSheet(this.sheetName);
        if(null == sheet){
            sheet = workbook.createSheet(this.sheetName);
        }
        //初始化表头
        this.initHeader(clazz);
        Map<String, String> map = this.readLine(sheet, rownum);
        return map;
    }


    /**
     * 读取指定行数据
     */
    public Map<String,String> readLine(Sheet sheet, int rownum){
        boolean isEmptyRow = true;
        Map<String, String> resultMap = new HashMap<String, String>();
        Row row = sheet.getRow(rownum);
        Set<String> titleSet = this.titleColumnMap.keySet();
        for(String obj : titleSet){
            int column = this.titleColumnMap.get(obj);
            Cell cell = row.getCell(column);
            if(null != cell){
                Field field = this.titleFieldobjMap.get(obj);
                String value = getCellValue(cell);
                if(this.titleColumnMap.containsKey(value) && rownum<1){
                    //表头数据,直接返回
                    return null;
                }
                try{
                    value = ExcelDataConvert.convertToDefinedType(value, field);
                }catch(ParseException e){
                    e.printStackTrace();
                }
                if(StringUtils.isNotBlank(value)){
                    resultMap.put(obj, value);
                    isEmptyRow = false;
                }
            }
        }
        if(isEmptyRow){
            return null;
        }else{
            return resultMap;
        }
    }


    /**
     * 获取单元格内的数据值
     */
    private String getCellValue(Cell cell){
        String cellValue = null;
        switch(cell.getCellType()){
            case Cell.CELL_TYPE_BLANK   : cellValue = ""; break;
            case Cell.CELL_TYPE_STRING  : cellValue = cell.getRichStringCellValue().getString().trim(); break;
            case Cell.CELL_TYPE_NUMERIC : cellValue = new DecimalFormat("0").format(cell.getNumericCellValue()); break; //不使用DecimalFormat会有科学计数法的问题
            case Cell.CELL_TYPE_BOOLEAN : cellValue = String.valueOf(cell.getBooleanCellValue()).trim(); break;
            case Cell.CELL_TYPE_FORMULA : cellValue = cell.getCellFormula(); break;
            default: cellValue = "";
        }
        return cellValue;
    }


    /**
     * 读取第一行有效数据
     */
    public Map<String, String> readFirstLine(Class<?> clazz, String sheetName){
        Map<String, String> resultMap = new HashMap<String, String>();
        Sheet sheet = workbook.getSheet(sheetName);
        if(null == sheet) {
            return resultMap;
        }
        //初始化表头
        this.initHeader(clazz);
        Row row = this.getNotEmptyRow(sheet);
        Set<String> titleSet = this.titleColumnMap.keySet();
        for(String obj : titleSet){
            int column = this.titleColumnMap.get(obj);
            Cell cell = row.getCell(column);
            resultMap.put(obj, cell.getStringCellValue());
        }
        return resultMap;
    }


    /**
     * 读取所有数据
     */
    public List<Map<String, String>> readAllData(Class<?> clazz, String sheetName){
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        Sheet sheet = workbook.getSheet(sheetName);
        if(null == sheet){
            return resultList;
        }
        //初始化表头
        this.initHeader(clazz);
        int lastRowNum = sheet.getLastRowNum();
        int firstRowNum = sheet.getFirstRowNum();
        for(int i=firstRowNum; i<=lastRowNum; i++){
            Map<String,String> map = this.readLine(sheet, i);
            if(null != map){
                resultList.add(map);
            }
        }
        return resultList;
    }


    /**
     * 初始化表头关系
     */
    private void initHeader(Class<?> clazz){
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            String fieldName = field.getName();
            //取表头配置
            ExcelHeader ed = field.getAnnotation(ExcelHeader.class);
            if(null != ed){
                String title = ed.title();
                int column = ed.column();
                String fieldname = this.titleFieldMap.get(title);
                if(StringUtils.isBlank(fieldname)){
                    //初始化表头标题、列号、字段的映射关系
                    this.titleFieldMap.put(title, fieldName);
                    this.fieldColumnMap.put(fieldName, column);
                    this.titleColumnMap.put(title, column);
                    this.titleFieldobjMap.put(title, field);
                }
            }
        }
    }


    /**
     * 初始化字段Map
     */
    private Map<String, Field> getDataMap(Object obj){
        //读取配置注解
        Map<String, Field> titleField = new HashMap<String, Field>();
        Class<? extends Object> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            //取表头配置
            ExcelHeader ed = field.getAnnotation(ExcelHeader.class);
            if(null != ed){
                String title = ed.title();
                //缓存字段,待写入时处理
                titleField.put(title, field);
            }
        }
        return titleField;
    }
}