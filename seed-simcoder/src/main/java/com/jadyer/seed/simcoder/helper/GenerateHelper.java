package com.jadyer.seed.simcoder.helper;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.simcoder.SimcoderRun;
import com.jadyer.seed.simcoder.model.Column;
import com.jadyer.seed.simcoder.model.Table;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/8 23:22.
 */
public class GenerateHelper {
    private static final String PACKAGE_MODEL = SimcoderRun.PACKGET_PREFIX + ".web.model";
    private static final String PACKAGE_SERVICE = SimcoderRun.PACKGET_PREFIX + ".web.service";
    private static final String PACKAGE_REPOSITORY = SimcoderRun.PACKGET_PREFIX + ".web.repository";
    private static final String PACKAGE_CONTROLLER = SimcoderRun.PACKGET_PREFIX + ".web.controller";
    private static final String importColumnAnnotation = "\nimport javax.persistence.Column;";
    private static final String importDate = "import java.util.Date;\n";
    private static final String importBigDecimal = "import java.math.BigDecimal;\n";
    private static final String importBigDecimalAndDate = "import java.math.BigDecimal;\nimport java.util.Date;\n";
    private static GroupTemplate groupTemplate = null;
    static {
        try {
            groupTemplate = new GroupTemplate(new ClasspathResourceLoader("templates/"), Configuration.defaultConfiguration());
            ////v2.8.1暂不支持：https://github.com/javamonkey/beetl2.0/issues/351
            //groupTemplate.setSharedVars(new HashMap<String, Object>(){
            //    private static final long serialVersionUID = -7774932094711543319L;
            //    {
            //        put("serialVersionUID", JadyerUtil.buildSerialVersionUID());
            //        put("PACKAGE_MODEL", SimcoderRun.PACKGET_PREFIX + ".web.model");
            //        put("PACKAGE_SERVICE", SimcoderRun.PACKGET_PREFIX + ".web.service");
            //        put("PACKAGE_CONTROLLER", SimcoderRun.PACKGET_PREFIX + ".web.controller");
            //        put("PACKAGE_REPOSITORY", SimcoderRun.PACKGET_PREFIX + ".web.repository");
            //    }
            //});
        } catch (IOException e) {
            System.err.println("加载Beetl模板失败，堆栈轨迹如下：");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 判断给定的表名是否存在于包含列表
     */
    private static boolean isInclude(String tablename, String... includeTablename){
        if(null==includeTablename || includeTablename.length==0){
            return true;
        }
        for(String obj : includeTablename){
            if(obj.equals(tablename)){
                return true;
            }
        }
        return false;
    }


    /**
     * 生成整个数据库的
     * @param databaseName     数据库名
     * @param includeTablename 包含列表，有此值时则以此值为准，即此时只为数据库中的这些表生成代码
     */
    public static void generate(String databaseName, String... includeTablename){
        List<Table> tableList = DBHelper.getTableList(databaseName);
        for(Table table : tableList){
            if(isInclude(table.getName(), includeTablename)){
                generateFromTable(table.getName(), table.getComment());
            }
        }
    }


    /**
     * 生成某张表的
     */
    private static void generateFromTable(String tablename, String tablecomment){
        boolean hasDate = false;
        boolean hasBigDecimal = false;
        boolean hasColumnAnnotation = false;
        StringBuilder fields = new StringBuilder();
        StringBuilder methods = new StringBuilder();
        /*
         * /** 微信或QQ公众平台绑定状态：0--未绑定，1--已绑定 *\/
         * @Column(name="bind_status")
         * private int bindStatus;
         *
         * public int getBindStatus() {
         *     return bindStatus;
         * }
         *
         * public void setBindStatus(int bindStatus) {
         *     this.bindStatus = bindStatus;
         * }
         */
        List<Column> columnList = DBHelper.getColumnList(tablename);
        for(int i=0; i<columnList.size(); i++){
            if(StringUtils.equalsAnyIgnoreCase(columnList.get(i).getName(), "id", "create_time", "update_time")){
                continue;
            }
            ///** 字段注释 */
            if(StringUtils.isNotBlank(columnList.get(i).getComment())){
                fields.append("    /** ").append(columnList.get(i).getComment()).append(" */").append("\n");
            }
            //@Column(name="bind_status")
            String fieldname = DBHelper.buildFieldnameFromColumnname(columnList.get(i).getName());
            if(!fieldname.equals(columnList.get(i).getName())){
                hasColumnAnnotation = true;
                fields.append("    @Column(name=\"").append(columnList.get(i).getName()).append("\")").append("\n");
            }
            //private int bindStatus;
            String javaType = DBHelper.buildJavatypeFromDbtype(columnList.get(i).getType());
            if("Date".equals(javaType)){
                hasDate = true;
            }
            if("BigDecimal".equals(javaType)){
                hasBigDecimal = true;
            }
            fields.append("    private ").append(javaType).append(" ").append(fieldname).append(";").append("\n");
            //getter and setter
            methods.append("    public ").append(javaType).append(" get").append(StringUtils.capitalize(fieldname)).append("() {").append("\n");
            methods.append("        return ").append(fieldname).append(";").append("\n");
            methods.append("    }").append("\n");
            methods.append("\n");
            methods.append("    public void set").append(StringUtils.capitalize(fieldname)).append("(").append(javaType).append(" ").append(fieldname).append(") {").append("\n");
            methods.append("        this.").append(fieldname).append(" = ").append(fieldname).append(";").append("\n");
            methods.append("    }");
            //方法与方法直接都空一行，并且最后一个setter之后就不用换行了
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
         * 设置Beetl共享变量（目前2.8.1版本：共享变量只能set一次，第二次set时会冲掉之前所有的，因为源码里是直接改变对象引用的）
         */
        String classname = DBHelper.buildClassnameFromTablename(tablename);
        Map<String, Object> sharedVars = new HashMap<>();
        sharedVars.put("serialVersionUID", JadyerUtil.buildSerialVersionUID());
        sharedVars.put("PACKAGE_MODEL", PACKAGE_MODEL);
        sharedVars.put("PACKAGE_SERVICE", PACKAGE_SERVICE);
        sharedVars.put("PACKAGE_CONTROLLER", PACKAGE_CONTROLLER);
        sharedVars.put("PACKAGE_REPOSITORY", PACKAGE_REPOSITORY);
        sharedVars.put("CLASS_NAME", classname);
        sharedVars.put("CLASS_NAME_uncapitalize", StringUtils.uncapitalize(classname));
        sharedVars.put("TABLE_NAME", tablename);
        sharedVars.put("TABLE_NAME_nounderline", (tablename.startsWith("t_") ? tablename.substring(2) : tablename).replaceAll("_", ""));
        sharedVars.put("TABLE_NAME_convertpoint", (tablename.startsWith("t_") ? tablename.substring(2) : tablename).replaceAll("_", "."));
        sharedVars.put("fields", fields.toString());
        sharedVars.put("methods", methods.toString());
        sharedVars.put("comments", comments.toString());
        if(hasColumnAnnotation){
            sharedVars.put("importColumnAnnotation", importColumnAnnotation);
        }
        if(hasDate && hasBigDecimal){
            sharedVars.put("importBigDecimalDate", importBigDecimalAndDate);
        }else if(hasBigDecimal){
            sharedVars.put("importBigDecimalDate", importBigDecimal);
        }else if(hasDate){
            sharedVars.put("importBigDecimalDate", importDate);
        }
        groupTemplate.setSharedVars(sharedVars);
        /*
         * 解析Beetl模板
         */
        try {
            String outBaseDir = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + System.getProperty("file.separator");
            groupTemplate.getTemplate("model.btl").renderTo(FileUtils.openOutputStream(new File(outBaseDir + "model" + System.getProperty("file.separator") + classname + ".java")));
            groupTemplate.getTemplate("service.btl").renderTo(FileUtils.openOutputStream(new File(outBaseDir + "service" + System.getProperty("file.separator") + classname + "Service.java")));
            groupTemplate.getTemplate("controller.btl").renderTo(FileUtils.openOutputStream(new File(outBaseDir + "controller" + System.getProperty("file.separator") + classname + "Controller.java")));
            groupTemplate.getTemplate("repository.btl").renderTo(FileUtils.openOutputStream(new File(outBaseDir + "repository" + System.getProperty("file.separator") + classname + "Repository.java")));
        } catch (IOException e) {
            System.err.println("生成代码时发生异常，堆栈轨迹如下：");
            e.printStackTrace();
        }
    }
}