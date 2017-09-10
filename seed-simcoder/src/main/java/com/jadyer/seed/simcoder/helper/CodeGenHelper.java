package com.jadyer.seed.simcoder.helper;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.simcoder.model.Column;
import com.jadyer.seed.simcoder.model.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/8 23:22.
 */
public class CodeGenHelper {
    private static final String PACKAGE_MODEL = "com.jadyer.seed.mpp.web.model";
    private static final String PACKAGE_REPOSITORY = "com.jadyer.seed.mpp.web.repository";
    private static final String importColumnAnnotation = "\nimport javax.persistence.Column;";
    private static final String importDate = "import java.util.Date;\n";
    private static final String importBigDecimal = "import java.math.BigDecimal;\n";
    private static final String importBigDecimalAndDate = "import java.math.BigDecimal;\nimport java.util.Date;\n";
    private static GroupTemplate groupTemplate = null;
    static{
        try {
            groupTemplate = new GroupTemplate(new ClasspathResourceLoader("templates/"), Configuration.defaultConfiguration());
        } catch (IOException e) {
            System.err.println("加载Beetl模板失败，堆栈轨迹如下：");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 生成整个数据库的
     */
    public static void genAllDatabase(String outdir, String databaseName){
        List<Table> tableList = DBHelper.getTableList(databaseName);
        for(Table table : tableList){
            genAllTable(outdir, table.getName(), table.getComment());
        }
    }

    /**
     * 生成某张表的
     */
    public static void genAllTable(String outdir, String tablename, String tablecomment){
        boolean hasDate = false;
        boolean hasBigDecimal = false;
        boolean hasColumnAnnotation = false;
        StringBuilder fields = new StringBuilder();
        StringBuilder methods = new StringBuilder();
        List<Column> columnList = DBHelper.getColumnList(tablename);
        for(int i=0; i<columnList.size(); i++){
            if(StringUtils.equalsAnyIgnoreCase(columnList.get(i).getName(), "id", "create_time", "update_time")){
                continue;
            }
            /*
             * /** 字段注释 *\/
             */
            if(StringUtils.isNotBlank(columnList.get(i).getComment())){
                fields.append("    /** ").append(columnList.get(i).getComment()).append(" */\n");
            }
            /*
             * @Column(name="bind_status")
             */
            String fieldname = DBHelper.buildFieldnameFromColumnname(columnList.get(i).getName());
            if(!fieldname.equals(columnList.get(i).getName())){
                hasColumnAnnotation = true;
                fields.append("    @Column(name=\"").append(columnList.get(i).getName()).append("\")").append("\n");
            }
            /*
             * private int bindStatus;
             */
            String javaType = DBHelper.buildJavatypeFromDbtype(columnList.get(i).getType());
            if(javaType.equals("Date")){
                hasDate = true;
            }
            if(javaType.equals("BigDecimal")){
                hasBigDecimal = true;
            }
            fields.append("    private ").append(javaType).append(" ").append(fieldname).append(";").append("\n");
            /*
             * public int getBindStatus() {
             *     return bindStatus;
             * }
             *
             * public void setBindStatus(int bindStatus) {
             *     this.bindStatus = bindStatus;
             * }
             */
            methods.append("    public ").append(javaType).append(" get").append(StringUtils.capitalize(fieldname)).append("() {").append("\n");
            methods.append("        return ").append(fieldname).append(";").append("\n");
            methods.append("    }").append("\n");
            methods.append("\n");
            methods.append("    public void set").append(StringUtils.capitalize(fieldname)).append("(").append(javaType).append(" ").append(fieldname).append(") {").append("\n");
            methods.append("        this.").append(fieldname).append(" = ").append(fieldname).append(";").append("\n");
            methods.append("    }");
            //约定的
            if(i+1 != columnList.size()-2){
                methods.append("\n\n");
            }
        }
        /*
         * 用户信息
         * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/9/5 14:40.
         */
        StringBuilder comments = new StringBuilder();
        if(StringUtils.isNotBlank(tablecomment)){
            if(tablecomment.endsWith("表")){
                tablecomment = tablecomment.substring(0, tablecomment.length()-1);
            }
            comments.append(tablecomment).append("\n");
            comments.append(" * ");
        }
        comments.append("Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on ").append(DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm."));
        /*
         * 构造Beetl模板变量
         */
        String classname = DBHelper.buildClassnameFromTablename(tablename);
        Template template = groupTemplate.getTemplate("model.btl");
        template.binding("PACKAGE_MODEL", PACKAGE_MODEL);
        template.binding("CLASS_NAME", classname);
        template.binding("TABLE_NAME", tablename);
        template.binding("fields", fields.toString());
        template.binding("methods", methods.toString());
        template.binding("comments", comments.toString());
        template.binding("serialVersionUID", JadyerUtil.buildSerialVersionUID());
        if(hasColumnAnnotation){
            template.binding("importColumnAnnotation", importColumnAnnotation);
        }
        if(hasDate && hasBigDecimal){
            template.binding("importBigDecimalDate", importBigDecimalAndDate);
        }else if(hasBigDecimal){
            template.binding("importBigDecimalDate", importBigDecimal);
        }else if(hasDate){
            template.binding("importBigDecimalDate", importDate);
        }
        try {
            template.renderTo(new FileWriter(new File(outdir + classname + ".java")));
        } catch (IOException e) {
            System.err.println("生成代码时发生异常，堆栈轨迹如下：");
            e.printStackTrace();
        }
    }
}