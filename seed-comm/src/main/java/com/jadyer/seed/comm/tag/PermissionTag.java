package com.jadyer.seed.comm.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 自定义标签之权限标签处理类
 * ----------------------------------------------------------------------------------------------------
 * 目前该标签仅实现了根据是否含登录用户来决定JSP页面内容的显示与否
 * 这里采用了Java模板方法模式中的HookMethod实现,事实上Apache Mina和Shiro都是这么用的
 * 详见https://jadyer.github.io/2013/05/13/mina-hook-method/
 * ----------------------------------------------------------------------------------------------------
 * 为了更好的在其它Web项目中重复使用定制标记库,可以把相关的标记处理类/简单标记处理类/Tag/TLD等文件打包成一个Jar
 * 当需要在其它Web应用中使用该定制标记库时,直接把该Jar复制到/WEB-INF/lib目录下即可
 * 然后在JSP页面中使用如下taglig指令来标识该标记库,进而调用该标记库中的标记和Tag文件
 * <%@ taglib prefix="util" uri="http://v512.com/taglib/util"%>
 * 这里的util和http://v512.com/taglib/util都是在TLD文件中设置好的
 * ----------------------------------------------------------------------------------------------------
 * 打包步骤
 * 1)新建目录-->在硬盘上新建一个目录,如D:\mytaglib\
 * 2)准备文件-->在mytaglib中新建META-INF目录,用于存放TLD文件
 *             在META-INF中新建tags目录,用于存放Tag文件
 *             把源文件中\WebRoot\WEB-INF\classes下的标记处理类(包括类所在包名对应的文件夹)复制到mytaglib中
 * 3)打包文件-->在命令提示符窗口中切换到mytaglib目录,执行[D:\mytaglib>jar -cvf Jadyer-tag-1.0.jar *]命令打包
 * 4)补充一下-->打包前的树形目录(这里把源码也打进去了)
 * [Jadyer@localhost mytaglib]# tree
 * .
 * ├── com
 * │   └── jadyer
 * │       └── engine
 * │           └── common
 * │               └── tag
 * │                   ├── HasPermissionTag.class
 * │                   ├── LacksPermissionTag.class
 * │                   └── PermissionTag.class
 * ├── META-INF
 * │   ├── jadyer.tld
 * │   └── tags
 * └── src
 *     └── com
 *         └── jadyer
 *             └── engine
 *                 └── common
 *                     └── tag
 *                         ├── bak
 *                         │   ├── HasPermissionTag.java
 *                         │   ├── LacksPermissionTag.java
 *                         │   ├── PermissionTag.java
 *                         │   └── wth.tld
 *                         ├── HasPermissionTag.java
 *                         ├── jadyer.tld
 *                         ├── LacksPermissionTag.java
 *                         └── PermissionTag.java
 * 
 * 14 directories, 12 files
 * [Jadyer@localhost mytaglib]#
 * ----------------------------------------------------------------------------------------------------
 * 说明一下
 * 1)打包命令中的[Jadyer-tag-1.0]是所要生成的JAR文件的文件名,可随便定义
 * 2)打包命令中的*星号表示对该目录下的所有文件都执行打包操作,说白了就是对mytaglib目录打包
 * 3)mytaglib目录中的标记处理类均是编译后的class文件
 * 4)TLD文件必须位于Jar的META-INF目录中
 * 5)每个要打包的Tag文件都需要在TLD文件中进行配置(不打包Tag文件则无需在TLD中配置)
 *   <tag-file>
 *       <name>dateTime</name>
 *       <path>/META-INF/tags/dateTime.tag</path>
 *   </tag-file>
 *   <name>元素是定制标记的名字,通常与文件名相同(不过也可以指定其它的名字)
 *   <path>元素中设定Jar文件到此标记文件的路径,它必须是以/META-INF/tags/开头
 * ----------------------------------------------------------------------------------------------------
 * 发布本地Maven仓库
 * 对于这种自定义的jar,又不想发布到中央仓库,又没有私服,那就可以发布到本地仓库来使用
 * C:\Users\Jadyer>mvn install:install-file -DgroupId=com.jadyer -DartifactId=Jadyer-tag -Dversion=1.0 -Dpackaging=jar -Dfile=D:/mytaglib/Jadyer-tag-1.0.jar
 * [INFO] Scanning for projects...
 * [INFO]
 * [INFO] ------------------------------------------------------------------------
 * [INFO] Building Maven Stub Project (No POM) 1
 * [INFO] ------------------------------------------------------------------------
 * [INFO]
 * [INFO] --- maven-install-plugin:2.4:install-file (default-cli) @ standalone-pom---
 * [INFO] Installing D:\mytaglib\Jadyer-tag-1.0.jar to D:\Develop\Code\MavenRepository\com\jadyer\Jadyer-tag\1.0\Jadyer-tag-1.0.jar
 * [INFO] Installing C:\Users\Jadyer\AppData\Local\Temp\mvninstall809509404519266315.pom to D:\Develop\Code\MavenRepository\com\jadyer\Jadyer-tag\1.0\Jadyer-tag-1.0.pom
 * [INFO] ------------------------------------------------------------------------
 * [INFO] BUILD SUCCESS
 * [INFO] ------------------------------------------------------------------------
 * [INFO] Total time: 1.404 s
 * [INFO] Finished at: 2015-06-13T14:54:04+08:00
 * [INFO] Final Memory: 6M/72M
 * [INFO] ------------------------------------------------------------------------
 * C:\Users\Jadyer>
 * ----------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/3/4 10:47.
 */
public abstract class PermissionTag extends TagSupport {
	private static final long serialVersionUID = 4677922552292876458L;

	@Override
	public int doStartTag() throws JspException {
		if(this.showTagBody()){
			return TagSupport.EVAL_BODY_INCLUDE;
		}else{
			return TagSupport.SKIP_BODY;
		}
	}
	
	boolean isPermitted(){
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		//return null != request.getSession().getAttribute(Constants.USER);
		return null != request.getSession().getAttribute("user");
	}
	
	protected abstract boolean showTagBody();
}