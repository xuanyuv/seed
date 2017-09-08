package com.jadyer.seed.simcoder;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.simcoder.model.Column;
import com.jadyer.seed.simcoder.model.Table;
import com.jadyer.seed.simcoder.service.SimcoderHelper;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.IOException;

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

    public static void main(String[] args) throws IOException {
        for(Table obj : SimcoderHelper.getTableList("mpp")){
            System.out.println(ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE));
        }
        System.out.println("================================================");
        for(Column obj : SimcoderHelper.getColumnList("t_mpp_user_info")){
            System.out.println(ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE));
        }
        //默认的，Configuration类总是会先加载默认的配置文件（/org/beetl/core/beetl-default.properties）
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(new ClasspathResourceLoader("templates/"), cfg);
        Template template = gt.getTemplate("demo.btl");
        template.binding("name", "beetl");
        ////将渲染结果输出到Writer
        //template.renderTo(Writer)
        ////将渲染结果输出到OutputStream
        //template.renderTo(OutputStream)
        //输出渲染结果
        System.out.println(template.render());
        System.out.println("------>>>" + JadyerUtil.getProjectPath());
    }
}