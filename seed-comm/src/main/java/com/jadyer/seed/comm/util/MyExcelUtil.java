package com.jadyer.seed.comm.util;

import com.github.liaochong.myexcel.core.DefaultExcelBuilder;
import com.github.liaochong.myexcel.core.DefaultStreamExcelBuilder;
import com.github.liaochong.myexcel.core.SaxExcelReader;
import com.github.liaochong.myexcel.utils.AttachmentV2ExportUtil;
import com.github.liaochong.myexcel.utils.FileExportUtil;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
 * @version v1.5
 * @history v1.5-->读取文件时，支持指定文件编码（通常用于csv文件）
 * @history v1.4-->增加支持前端下载文件的流式导出的方法
 * @history v1.3-->写文件的方法增加对文件后缀名.xls的判断
 * @history v1.2-->简单封装一个写文件的方法
 * @history v1.1-->读取文件失败时，增加逻辑：修改文件后缀名再重读一次
 * @history v1.0-->初建，并添加读取Excel的方法
 * -------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2019/8/15 19:31.
 */
public class MyExcelUtil {
    private MyExcelUtil(){}

    public static <T> List<T> read(File excelFile, Class<T> modelClass, int skipRows){
        return read(excelFile, modelClass, skipRows, null);
    }

    /**
     * @param excelFile  Excel文件
     * @param modelClass 承载Excel数据的实体类
     * @param skipRows   指定跳过的行数：从0开始，传-1表示不跳过
     * @param charset    Excel文件编码，默认UTF-8（注：csv时，若读取到乱码，可显式传GBK试试）
     * Comment by 玄玉<https://jadyer.cn/> on 2019/8/15 19:35.
     */
    public static <T> List<T> read(File excelFile, Class<T> modelClass, int skipRows, String charset){
        List<T> dataList;
        //初始化SaxExcelReader
        SaxExcelReader<T> saxExcelReader = SaxExcelReader.of(modelClass);
        //是否指定编码
        if(!StringUtils.isBlank(charset)){
            saxExcelReader = saxExcelReader.charset(charset);
        }
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
     * 文件导出
     * --------------------------------------------------------------------------------------
     * myexcel-2.8.5测试发现：参数中的dataList可以传入通过该方式实例化的List：new ArrayList<>()
     * 但不能是通过这两种方式实例化的List：Arrays.asList()、Collections.singletonList()
     * --------------------------------------------------------------------------------------
     * 补充：HSSFWorkbook 和 XSSFWorkbook 的Sheet导出条数上限，如下所示
     *      <=2003版是65535行、256列
     *      >=2007版是1048576行、16384列
     *      若数据量超过此上限，则可以使用SXSSFWorkbook来做导出（其实上千条数据就可以用它了）
     * --------------------------------------------------------------------------------------
     * @param dataList   Excel数据
     * @param modelClass 承载Excel数据的实体类
     * @param pathname   Excel文件保存地址（含路径和文件名及后缀的完整地址，目录可以不存在，方法内部会自动判断并创建）
     * Comment by 玄玉<https://jadyer.cn/> on 2019/8/26 12:03.
     */
    public static <T> void writeToFile(List<T> dataList, Class<T> modelClass, String pathname){
        if(CollectionUtils.isEmpty(dataList)){
            throw new RuntimeException("空数据，无法创建Excel");
        }
        try {
            FileUtils.forceMkdirParent(new File(pathname));
        } catch (IOException e) {
            throw new RuntimeException("目录创建失败："+JadyerUtil.extractStackTraceCausedBy(e), e);
        }
        //创建工作对象
        Workbook workbook;
        if(pathname.endsWith(".xls")){
            workbook = DefaultExcelBuilder.of(modelClass, new HSSFWorkbook()).build(dataList);
        }else{
            workbook = DefaultExcelBuilder.of(modelClass).build(dataList);
        }
        try {
            FileExportUtil.export(workbook, new File(pathname));
        } catch (IOException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY, e);
        }
    }


    /**
     * 流式导出
     * --------------------------------------------------------------------------------------
     * https://github.com/liaochong/myexcel/wiki/Excel流式导出
     * --------------------------------------------------------------------------------------
     * @param dataList   Excel数据
     * @param modelClass 承载Excel数据的实体类
     * @param fileName   流数据保存到本地的文件名
     */
    public static <T> void writeToStream(List<T> dataList, Class<T> modelClass, String fileName, HttpServletResponse response){
        try(DefaultStreamExcelBuilder<T> streamExcelBuilder = DefaultStreamExcelBuilder.of(modelClass).start()){
            streamExcelBuilder.append(dataList);
            Workbook workbook = streamExcelBuilder.build();
            AttachmentV2ExportUtil.export(workbook, fileName, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}