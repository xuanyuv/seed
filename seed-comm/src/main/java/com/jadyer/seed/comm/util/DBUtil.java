package com.jadyer.seed.comm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用于获取数据库连接的工具类
 * @version v1.1
 * @history v1.1-->getConnection()支持手动传入数据库URL和用户密码
 * @history v1.0-->通过枚举实现的单例，创建工具类
 * Created by 玄玉<https://jadyer.cn/> on 2013/07/09 13:56.
 */
public enum DBUtil {
    INSTANCE;

    DBUtil(){
        try {
            // Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("数据库驱动载入失败", e);
        }
    }


    public Connection getConnection(String url, String user, String password){
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接创建失败", e);
        }
    }


    public void close(Connection conn){
        if(null != conn){
            try {
                conn.close();
                if(conn.isClosed()){
                    System.out.println("此数据库连接已关闭-->" + conn);
                }else{
                    System.err.println("此数据库连接关闭失败-->" + conn);
                }
            } catch (SQLException e) {
                System.err.println("数据库连接关闭失败，堆栈轨迹如下：");
                e.printStackTrace();
            }
        }
    }


    public void closeAll(ResultSet rs, PreparedStatement pstmt, Connection conn){
        if(null != rs){
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("数据库操作的ResultSet关闭失败，堆栈轨迹如下：");
                e.printStackTrace();
            }
        }
        if(null != pstmt){
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.err.println("数据库操作的PreparedStatement关闭失败，堆栈轨迹如下：");
                e.printStackTrace();
            }
        }
        this.close(conn);
    }
}