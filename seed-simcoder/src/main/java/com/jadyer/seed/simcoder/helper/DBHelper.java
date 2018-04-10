package com.jadyer.seed.simcoder.helper;

import com.jadyer.seed.comm.util.DBUtil;
import com.jadyer.seed.simcoder.SimcoderRun;
import com.jadyer.seed.simcoder.model.Column;
import com.jadyer.seed.simcoder.model.Table;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/7 17:18.
 */
class DBHelper {
    private static final String DB_URL = "jdbc:mysql://" + SimcoderRun.DB_ADDRESS + "/" + SimcoderRun.DB_NAME + "?useUnicode=true&characterEncoding=UTF8&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull";
    private static final String DB_USERNAME = SimcoderRun.DB_USERNAME;
    private static final String DB_PASSWORD = SimcoderRun.DB_PASSWORD;
    private static final String SQL_GET_TABLE = "SELECT TABLE_NAME, TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA=?;";
    private static final String SQL_GET_COLUMN = "SELECT COLUMN_NAME as name, COLUMN_COMMENT as comment, DATA_TYPE as type, ifnull(CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION) as length, if(IS_NULLABLE='yes', true, false) as nullable, if(COLUMN_KEY='pri', true, false) as isPrikey, if(EXTRA='auto_increment', true, false) as isAutoIncrement FROM information_schema.COLUMNS WHERE TABLE_NAME=? ORDER BY ORDINAL_POSITION;";

    /**
     * 获取数据库中的所有表信息
     */
    static List<Table> getTableList(String databaseName){
        List<Table> tableList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = DBUtil.INSTANCE.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            pstmt = conn.prepareStatement(SQL_GET_TABLE);
            pstmt.setString(1, databaseName);
            rs = pstmt.executeQuery();
            while(rs.next()){
                Table table = new Table();
                table.setName(rs.getString("table_name"));
                table.setComment(rs.getString("table_comment"));
                tableList.add(table);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            DBUtil.INSTANCE.closeAll(rs, pstmt, conn);
        }
        return tableList;
    }


    /**
     * 获取某张表的所有列信息
     */
    static List<Column> getColumnList(String tableName){
        List<Column> columnList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = DBUtil.INSTANCE.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            pstmt = conn.prepareStatement(SQL_GET_COLUMN);
            pstmt.setString(1, tableName);
            rs = pstmt.executeQuery();
            while(rs.next()){
                Column column = new Column();
                column.setName(rs.getString("name"));
                column.setComment(rs.getString("comment"));
                column.setNullable(rs.getBoolean("nullable"));
                column.setPrikey(rs.getBoolean("isPrikey"));
                column.setAutoIncrement(rs.getBoolean("isAutoIncrement"));
                column.setType(rs.getString("type"));
                String length = rs.getString("length");
                column.setLength(StringUtils.isBlank(length) ? 0 : StringUtils.equals("longtext", column.getType()) ? 999999999 : Integer.parseInt(length));
                columnList.add(column);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            DBUtil.INSTANCE.closeAll(rs, pstmt, conn);
        }
        return columnList;
    }


    /**
     * 通过表名构建类名
     */
    static String buildClassnameFromTablename(String tablename){
        if(StringUtils.isBlank(tablename)){
            throw new RuntimeException("表名不能为空");
        }
        if(tablename.startsWith("t_")){
            tablename = tablename.substring(2);
        }
        StringBuilder sb = new StringBuilder();
        for(String obj : tablename.split("_")){
            sb.append(StringUtils.capitalize(obj));
        }
        return sb.toString();
    }


    /**
     * 通过列名构建属性名
     */
    static String buildFieldnameFromColumnname(String columnname){
        if(StringUtils.isBlank(columnname)){
            throw new RuntimeException("列名不能为空");
        }
        String[] columnnames = columnname.split("_");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<columnnames.length; i++){
            if(i == 0){
                sb.append(columnnames[0]);
            }else{
                sb.append(StringUtils.capitalize(columnnames[i]));
            }
        }
        return sb.toString();
    }


    /**
     * 通过数据库列类型构建Java中的属性类型
     * <ul>
     *     <li>MySQL中的datetime和timestamp区别如下</li>
     *     <li>timestamp使用4字节的存储空间，datetime则使用8字节</li>
     *     <li>timestamp的范围为1970--2037年，datetime则为0001--9999年</li>
     *     <li>timestamp不允许空（空的时候它会拿当前时间填充），datetime允许空</li>
     *     <li>timestamp的值受时区的影响，datetime则不受。比如20170908174242，若修改时区为东9区（mysql> set time_zone='+9:00';），则timestamp会增加一个小时变成20170908184242，而datetime则不变</li>、
     *     <li>https://stackoverflow.com/questions/409286/should-i-use-field-datetime-or-timestamp：Timestamps in MySQL generally used to track changes to records, and are often updated every time the record is changed. If you want to store a specific value you should use a datetime field.</li>
     * </ul>
     */
    static String buildJavatypeFromDbtype(String dbtype){
        if(StringUtils.isBlank(dbtype)){
            throw new RuntimeException("数据库列类型不能为空");
        }
        if(StringUtils.equals(dbtype, "int")){
            return "long";
        }
        if(StringUtils.equals(dbtype, "tinyint")){
            return "int";
        }
        if(StringUtils.equals(dbtype, "bigint")){
            return "BigInteger";
        }
        if(StringUtils.equals(dbtype, "decimal")){
            return "BigDecimal";
        }
        if(StringUtils.equalsAnyIgnoreCase(dbtype, "datetime", "timestamp")){
            return "Date";
        }
        if(StringUtils.equalsAnyIgnoreCase(dbtype, "char", "varchar", "tinytext", "text", "mediumtext")){
            return "String";
        }
        throw new RuntimeException("不支持的类型[" + dbtype + "]");
    }
}