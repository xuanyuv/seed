package com.jadyer.seed.comm.util;

import com.github.liaochong.myexcel.core.DefaultExcelBuilder;
import com.github.liaochong.myexcel.core.SaxExcelReader;
import com.github.liaochong.myexcel.utils.FileExportUtil;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Excel工具类
 * -------------------------------------------------------------------------------
 * 封装自：https://github.com/liaochong/myexcel/wiki
 * -------------------------------------------------------------------------------
 * @version v1.2
 * @history v1.2-->简单封装一个写文件的方法
 * @history v1.1-->读取文件失败时，增加逻辑：修改文件后缀名再重读一次
 * @history v1.0-->初建，并添加读取Excel的方法
 * -------------------------------------------------------------------------------
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


    /**
     * --------------------------------------------------------------------------------------
     * myexcel-2.8.5测试发现：参数中的dataList可以传入通过该方式实例化的List：new ArrayList<>()
     * 但不能是通过这两种方式实例化的List：Arrays.asList()、Collections.singletonList()
     * --------------------------------------------------------------------------------------
     * @param dataList   Excel数据
     * @param modelClass 承载Excel数据的实体类
     * @param pathname   Excel文件保存地址（含路径和文件名及后缀的完整地址，目录可以不存在，方法内部会自动判断并创建）
     * Comment by 玄玉<https://jadyer.cn/> on 2019/8/26 12:03.
     */
    public static <T> void write(List<T> dataList, Class<T> modelClass, String pathname){
        try {
            FileUtils.forceMkdirParent(new File(pathname));
        } catch (IOException e) {
            throw new RuntimeException("目录创建失败："+JadyerUtil.extractStackTraceCausedBy(e), e);
        }
        Workbook workbook = DefaultExcelBuilder.of(modelClass).build(dataList);
        try {
            FileExportUtil.export(workbook, new File(pathname));
        } catch (IOException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY, e);
        }
    }
}