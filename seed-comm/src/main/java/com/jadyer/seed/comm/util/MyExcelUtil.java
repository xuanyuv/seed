package com.jadyer.seed.comm.util;

import com.github.liaochong.myexcel.core.SaxExcelReader;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import java.io.File;
import java.util.List;

/**
 * 封装MyExcel：https://github.com/liaochong/myexcel/wiki/Excel-Csv导入
 * -------------------------------------------------------------------------------
 * @version v1.1
 * @history v1.1-->读取文件失败时，增加逻辑：修改文件后缀名再重读一次
 * @history v1.0-->初建，并添加读取Excel的方法
 *  * -------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2019/8/15 19:31.
 */
public class MyExcelUtil {
    private MyExcelUtil(){}

    /**
     * @param excelFile  Excel文件
     * @param modelClass 承载Excel数据的实体类
     * @param skipRows   指定跳过的行数：从0开始，传-1表示不跳过
     * Comment by 玄玉<https://jadyer.cn/> on 2019/8/15 19:35.
     */
    public static <T> List<T> read(File excelFile, Class<T> modelClass, int skipRows){
        List<T> dataList;
        //初始化SaxExcelReader
        SaxExcelReader<T> saxExcelReader = SaxExcelReader.of(modelClass);
        //判断是否需要跳过行
        if(-1 < skipRows){
            saxExcelReader = saxExcelReader.rowFilter(row -> row.getRowNum() > skipRows);
        }
        try{
            //读文件
            dataList = saxExcelReader.read(excelFile);
        }catch (OfficeXmlFileException e){
            //若读取xls时报告格式错误，那就试试重命名为xlsx再读（有的excel文件头是2007版的，但文件名却是.xls结尾）
            String newFilePath = "unkonwnFile";
            if(excelFile.getName().endsWith(".xls")){
                newFilePath = excelFile.getPath() + "x";
            }
            if(excelFile.getName().endsWith(".xlsx")){
                newFilePath = excelFile.getPath().substring(0, excelFile.getPath().length()-1);
            }
            LogUtil.getLogger().warn("文件读取失败，异常信息为：{}。现尝试修改文件后缀名再重新读取一次，新文件名为：{}", e.getMessage(), newFilePath);
            File newFile = new File(newFilePath);
            excelFile.renameTo(newFile);
            dataList = saxExcelReader.read(newFile);
        }
        return dataList;
    }
}