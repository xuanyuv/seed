package com.jadyer.seed.test;

import com.alibaba.fastjson.JSON;
import com.github.liaochong.myexcel.core.annotation.ExcelColumn;
import com.github.liaochong.myexcel.core.annotation.ExcelModel;
import com.jadyer.seed.comm.util.MyExcelUtil;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/8/26 12:08.
 */
public class MyExcelUtilTest {
    private static final String EXCEL_FILE_PATHNAME = "C:\\Users\\Jadyer\\Desktop\\myexcel_test.xlsx";

    @Test
    public void write() {
        List<MyExcelWriterUser> dataList = new ArrayList<>();
        dataList.add(new MyExcelWriterUser(2, "方子敬", new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        dataList.add(new MyExcelWriterUser(6, "宁不凡", new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        dataList.add(new MyExcelWriterUser(9, "卓凌昭", new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        MyExcelUtil.writeToFile(dataList, MyExcelWriterUser.class, EXCEL_FILE_PATHNAME);
    }


    @Test
    public void reader() {
        //先写
        this.write();
        //再读
        List<MyExcelReaderUser> dataList = MyExcelUtil.read(new File(EXCEL_FILE_PATHNAME), MyExcelReaderUser.class, 0);
        System.out.println(JSON.toJSONString(dataList));
    }


    @ExcelModel(sheetName="测试数据", style={"text-align:left", "title->text-align:center; vertical-align:center; color:red; font-weight:bold", "odd->background-color:#DDEBF7; border-style:dotted"})
    private static class MyExcelWriterUser{
        @ExcelColumn(order=1, title="序号", convertToString=true)
        private int id;
        @ExcelColumn(order=2, title="姓名", width=8)
        private String username;
        @ExcelColumn(order=3, title="生日", format="yyyy-MM-dd HH:mm:ss", width=11)
        private LocalDateTime birthday;
        MyExcelWriterUser(int id, String username, LocalDateTime birthday) {
            this.id = id;
            this.username = username;
            this.birthday = birthday;
        }
    }


    public static class MyExcelReaderUser{
        @ExcelColumn(index=0)
        private String id;
        @ExcelColumn(index=1)
        private String username;
        @ExcelColumn(index=2, format="yyyyMMdd HH:mm:ss:SSS")
        private LocalDateTime birthday;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public LocalDateTime getBirthday() {
            return birthday;
        }
        public void setBirthday(LocalDateTime birthday) {
            this.birthday = birthday;
        }
    }
}