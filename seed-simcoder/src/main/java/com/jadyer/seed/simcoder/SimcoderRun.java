package com.jadyer.seed.simcoder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/5 14:40.
 */
public class SimcoderRun {
    //BaseEntity类的完整包路径
    private static final String package_BaseEntity = "com.jadyer.seed.comm.jpa.BaseEntity";
    //Model实体类的完整包名
    private static final String package_model = "com.jadyer.seed.mpp.web.model";
    //Repository类的完整包名
    private static final String package_Repository = "com.jadyer.seed.mpp.web.repository";

    public static void main(String[] args) {
        //for(Table obj : SimcoderHelper.getTableList("mpp")){
        //    System.out.println(ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE));
        //}
        //System.out.println("================================================");
        //for(Column obj : SimcoderHelper.getColumnList("t_mpp_user_info")){
        //    System.out.println(ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE));
        //}
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<domain>\n" +
                "<class>\n" +
                "/* \n" +
                " * CreateDate: #now#\n" +
                " *\n" +
                " * Email：darkidiot@icloud.com \n" +
                " * School：CUIT \n" +
                " * Copyright For darkidiot\n" +
                " */\n" +
                "package #class.package#.model;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "#imports#\n" +
                "\n" +
                "/**\n" +
                " * #table.desc#\n" +
                " * \n" +
                " * @author #author#\n" +
                " * @version 1.0\n" +
                " */\n" +
                "public class #class.name# implements Serializable {\n" +
                "\tprivate static final long serialVersionUID = #serialVersionUID#L;\n" +
                "\t#fields#\n" +
                "\t#methods#\n" +
                "}\n" +
                "</class>\n" +
                "<import>import #import#;</import>\n" +
                "\t\n" +
                "\t<field>\n" +
                "\t/** #field.name#:#field.desc# */\n" +
                "\tprivate #field.type# #field.name#;\n" +
                "\t</field>\n" +
                "\n" +
                "\t<method>\n" +
                "\t/** 取得#field.desc# */\n" +
                "\tpublic #field.type# #method.get#() {\n" +
                "\t\treturn #field.name#;\n" +
                "\t}\n" +
                "\t\n" +
                "\t/** 设置#field.desc# */\n" +
                "\tpublic void #method.set#(#field.type# #field.name#) {\n" +
                "\t\tthis.#field.name# = #field.name#;\n" +
                "\t}\n" +
                "\t</method>\n" +
                "</domain>";
        String classTemplate = matchs(xml, "<class>([\\w\\W]+?)</class>", 1).get(0);// 匹配模式是非贪婪的。非贪婪模式尽可能少的匹配所搜索的字符串，而默认的贪婪模式则尽可能多的匹配所搜索的字符串。
        String fieldTemplate = matchs(xml, "<field>([\\w\\W]+?)</field>", 1).get(0);
        String methodTemplate = matchs(xml, "<method>([\\w\\W]+?)</method>", 1).get(0);
        String importTemplate = matchs(xml, "<import>([\\w\\W]+?)</import>", 1).get(0);
        System.out.println("classTemplate = " + classTemplate);
        System.out.println("fieldTemplate = " + fieldTemplate);
        System.out.println("methodTemplate = " + methodTemplate);
        System.out.println("importTemplate = " + importTemplate);
    }


    static List<String> matchs(String input, String regex, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(input);
        List<String> matches = new ArrayList<>();
        while (match.find()) {
            matches.add(match.group(group));
        }
        return matches;
    }
}