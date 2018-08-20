package com.jadyer.seed.comm.annotation;

import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class SeedQSSRegProcessor implements BeanPostProcessor, EnvironmentAware {
    private static final String URL_API_QSS = "{qssHost}/qss/reg";
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    /**
     * bean初始化方法调用前被调用
     * 此时bean已被实例化，但还未注入对应的属性（即调用InitializingBean的afterPropertiesSet()方法或bean对应的init-method之前）
     * @param bean     当前状态的bean
     * @param beanName 当前bean的名称
     * @return 需要放入到bean容器中的bean
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    /**
     * bean初始化方法调用后被调用
     * 此时对应的依赖注入已经完成（即在调用InitializingBean的afterPropertiesSet()方法或bean对应init-method方法之后）
     * @param bean     当前状态的bean
     * @param beanName 当前bean的名称
     * @return 需要放入到bean容器中的bean
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass().getSuperclass();
        if(!clazz.isAnnotationPresent(Controller.class) && !clazz.isAnnotationPresent(RestController.class)){
            return bean;
        }
        //迭代本类中的所有public、protected、default、private的方法（不包括父类中的）
        for(Method method : clazz.getDeclaredMethods()){
            if(!method.isAnnotationPresent(SeedQSSReg.class)){
                continue;
            }
            //类名.方法名（不含类的包名），举例：MppController.bind
            String methodInfo = clazz.getSimpleName() + "." + method.getName();
            //校验SeedQSSReg
            SeedQSSReg reg = method.getAnnotation(SeedQSSReg.class);
            String qssHost = this.getPropertyFromEnv(reg.qssHost());
            String appHost = this.getPropertyFromEnv(reg.appHost());
            String appname = this.getPropertyFromEnv(reg.appname());
            String name = this.getPropertyFromEnv(reg.name());
            String cron = this.getPropertyFromEnv(reg.cron());
            if(StringUtils.isBlank(qssHost)){
                LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：空的QSS系统地址", methodInfo);
                continue;
            }
            if(qssHost.endsWith("/")){
                qssHost = qssHost.substring(0, qssHost.length()-1);
            }
            if(StringUtils.isBlank(appHost)){
                LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：空的应用系统地址", methodInfo);
                continue;
            }
            if(StringUtils.isBlank(appname)){
                LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：空的应用名称", methodInfo);
                continue;
            }
            if(StringUtils.isBlank(name)){
                LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：空的任务名称", methodInfo);
                continue;
            }
            if(StringUtils.isBlank(cron)){
                LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：空的CronExpression", methodInfo);
                continue;
            }
            if(!CronExpression.isValidExpression(cron)){
                LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：不正确的CronExpression", methodInfo);
                continue;
            }
            //Validate：方法所在类是否为Controller or RestController，并获取类地址
            //注意：类上面可加可不加RequestMapping，加的话也可以不设置映射路径
            String classURL = null;
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping = (RequestMapping)clazz.getAnnotation(RequestMapping.class);
                String[] urls = requestMapping.value();
                if(ArrayUtils.isEmpty(urls)){
                    urls = requestMapping.path();
                }
                if(ArrayUtils.isNotEmpty(urls)){
                    classURL = urls[0];
                }
            }
            //Validate：请求类型（get or post），并获取方法地址
            //注意：方法上面必须加XxxMapping，且必须设置映射路径
            if(!method.isAnnotationPresent(RequestMapping.class) && !method.isAnnotationPresent(GetMapping.class) && !method.isAnnotationPresent(PostMapping.class)){
                LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：未定义XxxMapping", methodInfo);
                continue;
            }
            String methodURL;
            if(method.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                if(ArrayUtils.isNotEmpty(requestMapping.method()) && !ArrayUtils.contains(requestMapping.method(), RequestMethod.POST)){
                    LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：任务必须支持POST请求", methodInfo);
                    continue;
                }
                String[] urls = requestMapping.value();
                if(ArrayUtils.isEmpty(urls)){
                    urls = requestMapping.path();
                }
                if(ArrayUtils.isEmpty(urls)){
                    LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：RequestMapping注解缺少values or path", methodInfo);
                    continue;
                }
                methodURL = urls[0];
            }else{
                if(!method.isAnnotationPresent(PostMapping.class)){
                    LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：任务必须显式支持POST请求", methodInfo);
                    continue;
                }
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                String[] urls = postMapping.value();
                if(ArrayUtils.isEmpty(urls)){
                    urls = postMapping.path();
                }
                if(ArrayUtils.isEmpty(urls)){
                    LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：PostMapping注解缺少values or path", methodInfo);
                    continue;
                }
                methodURL = urls[0];
            }
            if(StringUtils.isBlank(methodURL)){
                LogUtil.getLogger().error("QSS任务注册-->失败：[{}]：未获取到方法URL", methodInfo);
                continue;
            }
            //构造任务完整URL
            String taskURL = appHost;
            if(taskURL.endsWith("/")){
                taskURL = taskURL.substring(0, taskURL.length()-1);
            }
            if(StringUtils.isNotBlank(classURL)){
                taskURL += classURL.startsWith("/") ? classURL : "/" + classURL;
            }
            if(taskURL.endsWith("/")){
                taskURL = taskURL.substring(0, taskURL.length()-1);
            }
            taskURL += methodURL.startsWith("/") ? methodURL : "/" + methodURL;
            //注册到QSS
            Map<String, String> params = new HashMap<>();
            params.put("dynamicPassword", "https://jadyer.cn/");
            params.put("appname", appname);
            params.put("name", name);
            params.put("cron", cron);
            params.put("url", taskURL);
            HTTPUtil.post(URL_API_QSS.replace("{qssHost}", qssHost), params);
        }
        return bean;
    }


    private String getPropertyFromEnv(String prop){
        if(prop.startsWith("${") && prop.endsWith("}")){
            prop = prop.substring(2, prop.length()-1);
            prop = this.environment.getProperty(prop);
        }
        return prop;
    }
}