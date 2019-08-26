package com.jadyer.seed.test;

import com.alibaba.fastjson.JSON;
import com.github.liaochong.myexcel.core.annotation.ExcelColumn;
import com.github.liaochong.myexcel.core.annotation.ExcelTable;
import com.jadyer.seed.comm.util.MyExcelUtil;
import org.junit.Test;

import java.io.File;
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
        dataList.add(new MyExcelWriterUser(2, "方子敬", new Date()));
        dataList.add(new MyExcelWriterUser(6, "宁不凡", new Date()));
        dataList.add(new MyExcelWriterUser(9, "卓凌昭", new Date()));
        MyExcelUtil.write(dataList, MyExcelWriterUser.class, EXCEL_FILE_PATHNAME);
    }


    @Test
    public void reader() {
        //先写
        this.write();
        //再读
        List<MyExcelReaderUser> dataList = MyExcelUtil.read(new File(EXCEL_FILE_PATHNAME), MyExcelReaderUser.class, 0);
        System.out.println(JSON.toJSONString(dataList));
    }


    @ExcelTable(sheetName="测试数据")
    private static class MyExcelWriterUser{
        @ExcelColumn(order=1, title="序号")
        private int id;
        @ExcelColumn(order=2, title="姓名")
        private String username;
        @ExcelColumn(order=3, title="生日", dateFormatPattern="yyyyMMdd HH:mm:ss:SSS")
        private Date birthday;
        MyExcelWriterUser(int id, String username, Date birthday) {
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
        @ExcelColumn(index=2, dateFormatPattern="yyyyMMdd HH:mm:ss:SSS")
        private Date birthday;
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
        public Date getBirthday() {
            return birthday;
        }
        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }
    }
}