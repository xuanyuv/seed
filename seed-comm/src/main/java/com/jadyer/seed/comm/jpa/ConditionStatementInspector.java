package com.jadyer.seed.comm.jpa;

import com.jadyer.seed.comm.util.LogUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA-SQL拦截器
 * ------------------------------------------------------------------------------------------
 * 条件：只会拦截数据库表中，含有appid字段名，的表
 * ------------------------------------------------------------------------------------------
 * 注意：需要在application.yml中增加以下配置，方可生效
 * 注意：spring.jpa.properties.hibernate.session_factory.statement_inspector=com.jadyer.seed.comm.jpa.ConditionStatementInspector
 * ------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2021/7/6 11:09.
 */
@Component
public class ConditionStatementInspector implements StatementInspector {
    private static final long serialVersionUID = 4825640415026989942L;
    private static List<String> excludeTableList = new ArrayList<>();
    private static ThreadLocal<Long> appidMap = new ThreadLocal<>();

    @Resource
    private DataSource dataSource;

    @PostConstruct
    public void initExcludeTable() {
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs_column = null;
        try {
            // 获取数据库连接
            conn = dataSource.getConnection();
            String catalog = conn.getCatalog();
            DatabaseMetaData metaData = conn.getMetaData();
            // 获取表信息
            rs = metaData.getTables(catalog, null, "t_%", new String[]{"TABLE"});
            while(rs.next()){
                // 表名
                String tableName = rs.getString(3);
                // 获取该表的列信息
                rs_column = metaData.getColumns(catalog, null, tableName, "appid");
                // while(rs_column.next()){
                //     // 列名
                //     String columnName = rs_column.getString("COLUMN_NAME");
                //     if(StringUtils.equals(columnName, "appid")){
                //         includeTableList.add(tableName);
                //     }
                // }
                if(!rs_column.next()){
                    excludeTableList.add(tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            // DBUtil.INSTANCE.closeAll(rs_column, null, null);
            // DBUtil.INSTANCE.closeAll(rs, null, conn);
            if(null != rs_column){
                try {
                    rs_column.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != rs){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != conn){
                try {
                    conn.close();
                    if(conn.isClosed()){
                        System.out.println("SQL拦截器：此数据库连接已关闭-->" + conn);
                    }else{
                        System.err.println("SQL拦截器：此数据库连接关闭失败-->" + conn);
                    }
                } catch (SQLException e) {
                    System.err.println("SQL拦截器：数据库连接关闭失败，堆栈轨迹如下：");
                    e.printStackTrace();
                }
            }
        }
    }


    public static void removeAppid() {
        appidMap.remove();
    }


    public static void setAppid(long appid){
        appidMap.set(appid);
    }


    public static Long getAppid(){
        return appidMap.get();
    }


    @Override
    public String inspect(String sql) {
        return this.buildSQL(getAppid(), sql);
    }


    private String buildSQL(Long appid, String sql){
        LogUtil.getLogger().debug("SQL拦截器：appid=[{}]，SQL=[{}]", appid, sql);
        sql = sql.toLowerCase();
        if(null==appid || !StringUtils.startsWithAny(sql, "insert", "delete", "update", "select")){
            LogUtil.getLogger().debug("SQL拦截器：无需拦截，appid为空或当前非CRUD-SQL");
            return null;
        }
        for (String obj : excludeTableList) {
            if(sql.contains(obj)){
                LogUtil.getLogger().debug("SQL拦截器：无需拦截，当前表=[{}]中无appid字段", obj);
                return null;
            }
        }
        if(StringUtils.startsWithAny(sql, "insert")){
            String[] sqls = sql.split("\\)");
            sql = sqls[0] + ", appid)" + sqls[1] + ", " + appid + ")";
        }else{
            // SELECT * FROM t_demo_person where name='jadyer' order by id desc limit ?
            if(sql.contains("where")){
                String[] sqls = sql.split("where");
                sql = sqls[0] + "WHERE appid=" + appid;
                if(StringUtils.startsWithAny(sqls[1].trim(), "order by", "limit")){
                    sql = sql + sqls[1];
                }else{
                    sql = sql + " AND" + sqls[1];
                }
            }else{
                if(sql.contains("order by")){
                    sql = sql.substring(0, sql.indexOf("order by")) + "WHERE appid=" + appid + " " + sql.substring(sql.indexOf("order by"));
                }else if(sql.contains("limit")){
                    sql = sql.substring(0, sql.indexOf("limit")) + "WHERE appid=" + appid + " " + sql.substring(sql.indexOf("limit"));
                }else{
                    sql = sql + " WHERE appid=" + appid;
                }
            }
        }
        LogUtil.getLogger().debug("SQL拦截器：appid=[{}]，newSQL=[{}]", appid, sql);
        return sql;
    }
}