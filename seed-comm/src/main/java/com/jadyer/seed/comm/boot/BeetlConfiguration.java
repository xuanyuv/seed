package com.jadyer.seed.comm.boot;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring6.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring6.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * --------------------------------------------------------
 * 关于idea中修改templates之后自动刷新，详见
 * http://bbs.ibeetl.com/bbs/bbs/topic/612-1.html
 * http://bbs.ibeetl.com/bbs/bbs/topic/507-1.html
 * （Run即可，不需debugrun，且按Ctrl+F9就行）
 * --------------------------------------------------------
 * 【注意】：以上方法，还未测试
 * --------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/13 9:38.
 */
@Configuration
public class BeetlConfiguration {
    ///**
    // * 手动注册GroupTemplate，这样其它地方就可以直接使用groupTemplate对象（比如根据合同模板生成合同HTML）
    // * ------------------------------------------------------------------------------------------------
    // * 其它地方使用时，直接这样注入即可：@Resource private GroupTemplate groupTemplate;
    // * 使用举例如下
    // * //设置模板所需的共享变量
    // * groupTemplate.getSharedVars().putAll(JSON.parseObject(contractDataJson, new TypeReference<Map<String, Object>>(){}));
    // * //模板解析成真实的HTML字符串
    // * String html = groupTemplate.getTemplate("contract-001.html").render();
    // * //模板解析成真实的HTML到文件
    // * groupTemplate.getTemplate("contract-001.html").renderTo(FileUtils.openOutputStream(new File("C:\Users\Jadyer\Desktop\001.html")));
    // * ------------------------------------------------------------------------------------------------
    // */
    //@Bean
    //public GroupTemplate groupTemplate() throws IOException {
    //    GroupTemplate groupTemplate = new GroupTemplate(new FileResourceLoader("E:/IFS/Contract/template/"), org.beetl.core.Configuration.defaultConfiguration());
    //    //异常捕获处理：http://bbs.ibeetl.com/bbs/bbs/topic/1107-1.html
    //    groupTemplate.setErrorHandler(new ConsoleErrorHandler(){
    //        @Override
    //        public void processExcption(BeetlException ex, Writer writer) {
    //            ////控制台输出
    //            //super.processExcption(ex, writer);
    //            ErrorInfo error = new ErrorInfo(ex);
    //            StringBuilder sb = new StringBuilder(">>").append(this.getDateTime()).append(":").append(error.getType())
    //                    .append(":").append(error.getErrorTokenText()).append(" 位于").append(error.getErrorTokenLine()).append("行").append(" 资源:")
    //                    .append(ex.resource.getId());
    //            //显示前后三行的内容
    //            int[] range = this.getRange(error.getErrorTokenLine());
    //            String content = null;
    //            try {
    //                content = ex.gt.getResourceLoader().getResource(ex.resource.getId()).getContent(range[0], range[1]);
    //            } catch (IOException e1) {
    //                LogUtil.getLogger().error("解析模板遇到错误，打印前后三行的内容时，发生BeetlIOException，堆栈轨迹如下", e1);
    //            }
    //            if (null != content) {
    //                sb.append("\n");
    //                int lineNumber = range[0];
    //                String[] strs = content.split(ex.cr);
    //                for (String str : strs) {
    //                    sb.append(lineNumber).append("|").append(str.trim()).append("\n");
    //                    lineNumber++;
    //                }
    //            }
    //            LogUtil.getLogger().error(sb.toString());
    //            throw new BeetlException(BeetlException.ERROR, sb.toString());
    //        }
    //    });
    //    return groupTemplate;
    //}


    @Bean(initMethod="init", name="beetlConfig")
    public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        beetlGroupUtilConfiguration.setResourceLoader(new ClasspathResourceLoader(BeetlConfiguration.class.getClassLoader(), "templates/"));
        return beetlGroupUtilConfiguration;
    }

    @Bean(name="beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver(@Qualifier("beetlConfig") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setSuffix(".html");
        beetlSpringViewResolver.setOrder(0);
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
        return beetlSpringViewResolver;
    }
}