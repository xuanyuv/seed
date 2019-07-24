package com.jadyer.seed.comm.tmp;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * 这里要用到poi-3.9-20121203.jar和poi-ooxml-3.9-20121203.jar
 * Created by 玄玉<https://jadyer.cn/> on 2013/07/09 19:54.
 */
public class POIDemo {
    public static void writeExcel() throws IOException {
        //创建一个Excel(or new XSSFWorkbook())
        Workbook wb = new HSSFWorkbook();
        //创建表格
        Sheet sheet = wb.createSheet("测试Sheet_01");
        //创建行
        Row row = sheet.createRow(0);
        //设置行高
        row.setHeightInPoints(30);
        //创建样式
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(CellStyle.ALIGN_CENTER);
        cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cs.setBorderBottom(CellStyle.BORDER_DOTTED);
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);
        cs.setBorderTop(CellStyle.BORDER_THIN);
        //创建单元格
        Cell cell = row.createCell(0);
        //设置单元格样式
        cell.setCellStyle(cs);
        //设置单元格的值
        cell.setCellValue("序号");
        cell = row.createCell(1);
        cell.setCellStyle(cs);
        cell.setCellValue("用户");
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("1");
        cell = row.createCell(1);
        cell.setCellValue("张起灵");
        FileOutputStream fos = new FileOutputStream("D:/测试的Excel.xls");
        wb.write(fos);
        fos.close();
    }

    public static void readExcel() throws InvalidFormatException, IOException {
        long startTime = System.currentTimeMillis();
        int count = 0;
        //老版本POI是使用这种方式创建Workbook的,新版本中可以使用WorkbookFactory,它能自动根据文档的类型打开一个Excel
        //Workbook wb = new HSSFWorkbook(new FileInputStream("D:/5月业务定制对账文件汇总.xls"));
        Workbook wb = WorkbookFactory.create(new File("D:/5月业务定制对账文件汇总.xls"));
        //获取Excel中的某一个数据表..也可以通过Sheet名称来获取,即Workbook.getSheet("定制对账文件")
        Sheet sheet = wb.getSheetAt(0);
        Row row;
        Cell cell = null;
        //获取Excel的总行数:Sheet.getLastRowNum()+1(需要+1)
        for(/*int i=0*/ int i=sheet.getFirstRowNum(); i<sheet.getLastRowNum()+1; i++){
            //获取数据表里面的某一行
            row = sheet.getRow(i);
            //获取Excel的总列数:Row.getLastCellNum()(不用+1)
            for(/*int j=0*/ int j=row.getFirstCellNum(); j<row.getLastCellNum(); j++){
                //获取一行中的一个单元格
                String cellData = getCellValue(row.getCell(j)).trim();
                System.out.print(j == 0 ? count + 1 + "----" + cellData + "----" : cellData + "----");
            }
            count++;
            //打印完一行的数据之后,再输入一个空行
            System.out.println();
        }
        long endTime = System.currentTimeMillis();
        long useTime = endTime - startTime;
        System.out.println("导入文件完毕,导入数据[" + count + "]条,耗时" + useTime + "ms");
        String suffix = String.valueOf(useTime % 1000);
        while(suffix.endsWith("0")){
            suffix = suffix.substring(0, suffix.length()-1);
        }
        System.out.println("导入文件完毕,导入数据[" + count + "]条,耗时" + (useTime/1000) + "." + suffix + "秒");
    }

    /**
     * 使用for-each循环来读取Excel
     */
    public static void readExcelUseForeach() throws InvalidFormatException, IOException {
        for(Row row : WorkbookFactory.create(new File("D:/5月业务定制对账文件汇总.xls")).getSheetAt(0)){
            for(Cell cell : row){
                System.out.print(getCellValue(cell) + "----");
            }
            System.out.println();
        }
    }

    /**
     * 获取单元格内的数据值
     */
    private static String getCellValue(Cell cell){
        String str;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                str = ""; break;
            case Cell.CELL_TYPE_BOOLEAN:
                str = String.valueOf(cell.getBooleanCellValue()); break;
            case Cell.CELL_TYPE_FORMULA:
                str = String.valueOf(cell.getCellFormula()); break;
            case Cell.CELL_TYPE_NUMERIC:
                //str = String.valueOf(cell.getNumericCellValue()); break;                      //不要科学计数法
                str = new DecimalFormat("0").format(cell.getNumericCellValue()); break; //处理科学计数法
            case Cell.CELL_TYPE_STRING:
                str = cell.getStringCellValue(); break;
            default:
                str = null;
                break;
        }
        return str;
    }
}