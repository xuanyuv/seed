package com.jadyer.seed.comm.util.tmp.poi;

import com.jadyer.seed.comm.util.tmp.poi.converts.ExcelDataConvert;
import com.jadyer.seed.comm.util.tmp.poi.model.ExcelCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExcelCommonUtil{
	public static void writeCell(String fileName, List<ExcelCell> excelCellList) throws Exception {
		if(null==excelCellList || excelCellList.isEmpty()){
			System.err.println("do not write null list");
			return;
		}
		InputStream fis = new FileInputStream(fileName);
		Workbook workbook = WorkbookFactory.create(fis);
		Sheet sheet = workbook.getSheetAt(0);
		for(ExcelCell obj : excelCellList){
			int rownum = obj.getRowNum();
			int cellnum = obj.getCellNum();
			Object value = obj.getValue();
			Cell cell = sheet.getRow(rownum).getCell(cellnum);
			setCellValue(cell, value);
		}
		FileOutputStream fos = new FileOutputStream(fileName);
		workbook.write(fos);
		if(null != fos){
			fos.close();
		}
		if(null != fis){
			fis.close();
		}
	}

	private static void setCellValue(Cell cell, Object data){
		if(null == data){
			return;
		}
		String valType = data.getClass().getName();
		if("boolean".equals(valType) || "java.lang.Boolean".equals(valType)){
			boolean setValue = ExcelDataConvert.convertToBoolean(valType);
			cell.setCellValue(setValue);
		}else if("double".equals(valType) || "java.lang.Double".equals(valType)){
			double setValue = ExcelDataConvert.convertToDouble(data);
			cell.setCellValue(setValue);
		}else if("float".equals(valType) || "java.lang.Float".equals(valType)){
			double setValue = ExcelDataConvert.convertToFloat(data);
			cell.setCellValue(setValue);
		}else if("long".equals(valType) || "java.lang.Long".equals(valType)){
			double setValue = ExcelDataConvert.convertToLong(data);
			cell.setCellValue(setValue);
		}else if("int".equals(valType) || "java.lang.Integer".equals(valType)){
			double setValue = ExcelDataConvert.convertToInteger(data);
			cell.setCellValue(setValue);
		}else if("java.util.Calendar".equals(valType)){
			cell.setCellValue((Calendar)data);
		}else if("java.util.Date".equals(valType)){
			cell.setCellValue((Date)data);
		}else if("org.apache.poi.ss.usermodel.RichTextString".equals(valType)){
			//RichTextString
		}else{
			cell.setCellValue(String.valueOf(data));
		}
	}
}