package com.jadyer.seed.boot.remoting.server;

public class RemoteServiceScannerRegistrar {}

// import org.apache.commons.lang3.StringUtils;
// import org.springframework.beans.factory.BeanDefinitionStoreException;
// import org.springframework.beans.factory.config.BeanDefinitionHolder;
// import org.springframework.beans.factory.config.RuntimeBeanReference;
// import org.springframework.beans.factory.support.BeanDefinitionRegistry;
// import org.springframework.beans.factory.support.GenericBeanDefinition;
// import org.springframework.context.ResourceLoaderAware;
// import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
// import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
// import org.springframework.core.annotation.AnnotationAttributes;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.core.type.AnnotationMetadata;
// import org.springframework.core.type.classreading.MetadataReader;
// import org.springframework.core.type.filter.AnnotationTypeFilter;
// import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
//
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Set;
//
// /**
//  * 通过实现ImportBeanDefinitionRegistrar接口的方式，手动注册Bean
//  * -------------------------------------------------------------------------------------------
//  * Mybatis-Spring中的@MapperScan注解就是用来手动注册Bean的
//  * 我们这里封装的springhttpinvoker服务输出，就参考了它的实现
//  * https://github.com/mybatis/spring/blob/master/src/main/java/org/mybatis/spring/annotation/MapperScan.java
//  * https://github.com/mybatis/spring/blob/master/src/main/java/org/mybatis/spring/mapper/ClassPathMapperScanner.java
//  * https://github.com/mybatis/spring/blob/master/src/main/java/org/mybatis/spring/annotation/MapperScannerRegistrar.java
//  * -------------------------------------------------------------------------------------------
//  * Spring在解析JavaConfig的时候，会判断其是否标注了@Import注解
//  * 比如本工程扫描到com.jadyer.demo.boot.remoting.server.RemotingConfiguration.java时候
//  * 发现它标注了@Import注解，于是就找到了本类，接下来就会在适当的生命周期，执行这里的手工注册Bean
//  * -------------------------------------------------------------------------------------------
//  * Created by 玄玉<https://jadyer.cn/> on 2016/11/21 18:24.
//  */
// public class RemoteServiceScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
//     private ResourceLoader resourceLoader;
//
//     @Override
//     public void setResourceLoader(ResourceLoader resourceLoader) {
//         this.resourceLoader = resourceLoader;
//     }
//
//
//     @Override
//     public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//         //AnnotationAttributes其实是一个Map，这里得到的是@RemoteServiceScan注解的所有属性及其值
//         AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RemoteServiceScan.class.getName()));
//         ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
//         //this check is needed in Spring 3.1
//         if(null != this.resourceLoader) {
//             scanner.setResourceLoader(this.resourceLoader);
//         }
//         scanner.addIncludeFilter(new AnnotationTypeFilter(RemoteService.class));
//         //计算需要扫描的包路径
//         //這里就是得到@RemoteServiceScan注解的属性值，即扫描的包路径
//         List<String> basePackages = new ArrayList<>();
//         for(String pkg : annoAttrs.getStringArray("value")){
//             if(StringUtils.isNotBlank(pkg)){
//                 basePackages.add(pkg.trim());
//             }
//         }
//         for(String pkg : annoAttrs.getStringArray("scanBasePackages")){
//             if(StringUtils.isNotBlank(pkg)){
//                 basePackages.add(pkg.trim());
//             }
//         }
//         //for(Class<?> clazz : annoAttrs.getClassArray("scanBasePackageClasses")){
//         //    basePackages.add(ClassUtils.getPackageName(clazz));
//         //}
//         if(basePackages.isEmpty()){
//             throw new IllegalArgumentException("undefined scan base packages on @RemoteServiceScan annotation");
//         }
//         scanner.doScan(basePackages.toArray(new String[basePackages.size()]));
//     }
//
//
//     private class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
//         ClassPathMapperScanner(BeanDefinitionRegistry registry) {
//             super(registry);
//         }
//         @Override
//         protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
//             //扫描指定的包，得到Bean定义，再处理这些自定义的Bean
//             Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
//             if(beanDefinitions.isEmpty()){
//                 logger.warn("No Remote Service was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
//             }else{
//                 this.processBeanDefinitions(beanDefinitions);
//             }
//             return beanDefinitions;
//         }
//         private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
//             for(BeanDefinitionHolder holder : beanDefinitions){
//                 try {
//                     //获取每个标注了@RemoteService注解的所有属性及其值，得到一个Map
//                     MetadataReader metadataReader = this.getMetadataReaderFactory().getMetadataReader(holder.getBeanDefinition().getBeanClassName());
//                     AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(metadataReader.getAnnotationMetadata().getAnnotationAttributes(RemoteService.class.getName()));
//                     if(null==annoAttrs || annoAttrs.isEmpty()){
//                         continue;
//                     }
//                     //计算serviceInterface
//                     Class<?> serviceInterface = annoAttrs.getClass("value");
//                     if(null == serviceInterface){
//                         serviceInterface = annoAttrs.getClass("serviceInterface");
//                     }
//                     if("java.lang.Class".equals(serviceInterface.getName())){
//                         throw new IllegalArgumentException("undefined service interface on RemoteService class: " + holder.getBeanDefinition().getBeanClassName());
//                     }
//                     //计算服务路径（"/" + path + "/" + name）
//                     String name = annoAttrs.getString("name").trim();
//                     if(StringUtils.isBlank(name)){
//                         name = serviceInterface.getSimpleName();
//                     }
//                     String path = annoAttrs.getString("path").trim();
//                     if(StringUtils.isNotBlank(path)){
//                         if(path.endsWith("/")){
//                             name = path + name;
//                         }else{
//                             name = path + "/" + name;
//                         }
//                     }
//                     if(!name.startsWith("/")){
//                         name = "/" + name;
//                     }
//                     //通过Spring提供的HttpInvokerServiceExporter输出服务，该类可以将普通Bean实例输出成远程服务
//                     //这里是比较关键的：因为我们需要自己设定哪个Bean输出服务，哪个Bean不用输出，所以才自定义注解来实现
//                     //@RemoteServiceScan和@RemoteService的目的就是找到我们希望输出服务的Bean，然后手工注册Bean并输出
//                     GenericBeanDefinition definition = new GenericBeanDefinition();
//                     definition.getPropertyValues().add("service", new RuntimeBeanReference(holder.getBeanName()));
//                     definition.getPropertyValues().add("serviceInterface", serviceInterface);
//                     definition.setInitMethodName("afterPropertiesSet");
//                     definition.setBeanClass(HttpInvokerServiceExporter.class);
//                     this.registerBeanDefinition(new BeanDefinitionHolder(definition, name), this.getRegistry());
//                 }catch(IOException e){
//                     throw new BeanDefinitionStoreException("Failed to read candidate component class: " + holder.getBeanDefinition().getBeanClassName(), e);
//                 }
//             }
//         }
//     }
// }