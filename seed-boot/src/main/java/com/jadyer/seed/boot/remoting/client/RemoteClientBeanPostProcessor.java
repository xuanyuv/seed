package com.jadyer.seed.boot.remoting.client;

public class RemoteClientBeanPostProcessor {}

// import org.apache.commons.lang3.StringUtils;
// import org.springframework.beans.BeansException;
// import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
// import org.springframework.core.Ordered;
// import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;
// import org.springframework.util.ReflectionUtils;
//
// import javax.annotation.Resource;
// import java.lang.reflect.Field;
// import java.lang.reflect.Modifier;
//
// /**
//  * RemoteClient注解的处理实现类（客户端在调用服务端之前，通过该类来实例化请求服务）
//  * <ul>
//  *     <li>这里通过继承InstantiationAwareBeanPostProcessorAdapter实现：Bean初始化和实例化的前后的个性处理</li>
//  *     <li>关于SpringBean的一些常用扩展接口，可参考http://www.cnblogs.com/xrq730/p/5721366.html</li>
//  *     <li>
//  *         此时客户端的写法如下（下面postProcessAfterInstantiation()方法中的注释就是针对它来描述的）
//  *         @RemoteClient("${host.ifs}")
//  *         private IFSService ifsService;
//  *     </li>
//  * </ul>
//  * Created by 玄玉<https://jadyer.cn/> on 2016/11/21 18:41.
//  */
// @Component
// @Order(Ordered.LOWEST_PRECEDENCE - 1)
// public class RemoteClientBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
//     @Resource
//     private RemoteClientBuilder remoteClientBuilder;
//
//     /**
//      * Bean实例化之后被调用（通常用来"梳妆打扮"已经实例化的对象）
//      */
//     @Override
//     public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
//         //getFields()能获取到父类和子类中所有public的属性
//         //getDeclaredFields()能获取到类的所有属性（不受访问权限控制，也不包括父类）
//         for(Field field : bean.getClass().getDeclaredFields()){
//             //这里得到的field就表示bean里面的所有属性，其中也就包括ifsService;
//             //然后判断当前属性是否标注了@RemoteClient注解，若获取到的client不是null就说明标注了
//             RemoteClient client = field.getAnnotation(RemoteClient.class);
//             if(null != client){
//                 //@RemoteClient注解不能被标注在static的属性上
//                 if(Modifier.isStatic(field.getModifiers())){
//                     throw new IllegalStateException("@RemoteClient annotation is not supported on static fields!");
//                 }
//                 //获取服务端接口地址
//                 String serviceUrl = StringUtils.isNotBlank(client.value()) ? client.value() : client.serviceUrl();
//                 //生成@RemoteClient的实例（这里field代表ifsService，getType()之后就得到了IFSService.class）
//                 Object value = this.remoteClientBuilder.build(field.getType(), serviceUrl);
//                 //使变量域可用
//                 ReflectionUtils.makeAccessible(field);
//                 try{
//                     //设定对象的属性值为指定的值（即设置当前Bean中的'ifsService'属性的值为获取到的RemoteClient实例）
//                     field.set(bean, value);
//                 }catch(IllegalAccessException e){
//                     throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + e);
//                 }
//             }
//         }
//         return true;
//     }
// }