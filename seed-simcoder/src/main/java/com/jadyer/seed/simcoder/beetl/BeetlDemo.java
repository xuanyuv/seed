package com.jadyer.seed.simcoder.beetl;

import com.jadyer.seed.comm.util.JadyerUtil;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.IOException;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/7 20:24.
 */
public class BeetlDemo {
    public static void main(String[] args) throws IOException {
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