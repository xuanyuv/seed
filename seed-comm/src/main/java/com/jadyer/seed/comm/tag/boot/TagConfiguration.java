package com.jadyer.seed.comm.tag.boot;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroupDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.TaglibDescriptorImpl;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import java.util.Collection;
import java.util.Collections;

/**
 * 經測試：SpringBoot本身以及它打成的war默認都會加載WEB-INF下的tld文件，故此類暫未用到
 * Created by 玄玉<http://jadyer.cn/> on 2016/7/9 19:34.
 */
//@Configuration
//@HandlesTypes(WebApplicationInitializer.class)
public class TagConfiguration implements EmbeddedServletContainerCustomizer {
    @Override
    public void customize(final ConfigurableEmbeddedServletContainer container) {
        if(container instanceof TomcatEmbeddedServletContainerFactory){
            TomcatEmbeddedServletContainerFactory factory = (TomcatEmbeddedServletContainerFactory)container;
            factory.addContextCustomizers(new TomcatContextCustomizer() {
                JspConfigDescriptor jspConfigDescriptor = new JspConfigDescriptor() {
                    @Override
                    public Collection<TaglibDescriptor> getTaglibs() {
                        TaglibDescriptor descriptor = new TaglibDescriptorImpl("/WEB-INF/jadyer.tld", "http://www.jadyer.com/tag/jadyer");
                        //return new ArrayList<>(Arrays.asList(descriptor11,descriptor22));
                        return Collections.singletonList(descriptor);
                    }
                    @Override
                    public Collection<JspPropertyGroupDescriptor> getJspPropertyGroups() {
                        ////双括号法，但对于List初始化，不如Arrays.asList或Collections.singletonList简洁明了
                        //final JspPropertyGroupDescriptor descriptor11 = new JspPropertyGroupDescriptorImpl(new JspPropertyGroup(){{
                        //    addUrlPattern("*.jsp");
                        //    setPageEncoding("UTF-8");
                        //}});
                        //final JspPropertyGroupDescriptor descriptor22 = new JspPropertyGroupDescriptorImpl(new JspPropertyGroup(){{
                        //    addUrlPattern("*.jsp");
                        //    setPageEncoding("UTF-8");
                        //}});
                        ////new ArrayList<JspPropertyGroupDescriptor>(){{
                        ////    add(descriptor11);
                        ////    add(descriptor22);
                        ////}};
                        //return new ArrayList<>(Arrays.asList(descriptor11,descriptor22));
                        JspPropertyGroup group = new JspPropertyGroup();
                        group.addUrlPattern("*.jsp");
                        group.setPageEncoding("UTF-8");
                        JspPropertyGroupDescriptor descriptor = new JspPropertyGroupDescriptorImpl(group);
                        return Collections.singletonList(descriptor);
                    }
                };
                @Override
                public void customize(Context context) {
                    context.setJspConfigDescriptor(jspConfigDescriptor);
                }
            });
        }
    }
}