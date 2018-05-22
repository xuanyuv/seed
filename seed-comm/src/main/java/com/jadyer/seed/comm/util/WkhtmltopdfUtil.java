package com.jadyer.seed.comm.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * wkhtmltopdf工具类
 * ------------------------------------------------------------------------------------------------------------
 * Linux下安装wkhtmltopdf：http://jadyer.cn/2013/09/07/centos-config-develop/#安装wkhtmltopdf
 * C:\Users\Jadyer\Desktop>wkhtmltopdf --footer-center 第[page]页／共[topage]页 --margin-top 30mm --margin-bottom 20mm --footer-spacing 5 --header-spacing 5 --header-html header.html 11.html 11.pdf
 * C:\Users\Jadyer\Desktop>wkhtmltopdf --footer-center 第[page]页／共[topage]页 --margin-top 30mm --margin-bottom 20mm --footer-spacing 5 --header-spacing 5 --header-html http://127.0.0.1:8000/headerpdf 11.html 11.pdf
 * ------------------------------------------------------------------------------------------------------------
 * https://wkhtmltopdf.org/
 * https://github.com/wkhtmltopdf/wkhtmltopdf
 * https://wkhtmltopdf.org/usage/wkhtmltopdf.txt
 * Input表单或JavaScript脚本支持：--enable-forms，下面这些是网友整理的参数说明
 * wkhtmltopdf [OPTIONS]... <input file> [More input files] <output file>
 * 常规选项
 * --allow <path>                  允许加载从指定的文件夹中的文件或文件（可重复）
 * --book*                         设置一会打印一本书的时候，通常设置的选项
 * --collate                       打印多份副本时整理
 * --cookie <name> <value>         设置一个额外的cookie（可重复）
 * --cookie-jar <path>             读取和写入的Cookie，并在提供的cookie jar文件
 * --copies <number>               复印打印成pdf文件数（默认为1）
 * --cover* <url>                  使用HTML文件作为封面。它会带页眉和页脚的TOC之前插入
 * --custom-header <name> <value>  设置一个附加的HTTP头（可重复）
 * --debug-javascript              显示的javascript调试输出
 * --default-header*               添加一个缺省的头部，与页面的左边的名称，页面数到右边，例如： --header-left '[webpage]' --header-right '[page]/[toPage]'  --header-line
 * --disable-external-links*       禁止生成链接到远程网页
 * --disable-internal-links*       禁止使用本地链接
 * --disable-javascript            禁止让网页执行JavaScript
 * --disable-pdf-compression*      禁止在PDF对象使用无损压缩
 * --disable-smart-shrinking*      禁止使用WebKit的智能战略收缩，使像素/ DPI比没有不变
 * --disallow-local-file-access    禁止允许转换的本地文件读取其他本地文件，除非explecitily允许用 --allow
 * --dpi <dpi>                     显式更改DPI（这对基于X11的系统没有任何影响）
 * --enable-plugins                启用已安装的插件（如Flash
 * --encoding <encoding>           设置默认的文字编码
 * --extended-help                 显示更广泛的帮助，详细介绍了不常见的命令开关
 * --forms*                        打开HTML表单字段转换为PDF表单域
 * --grayscale                     PDF格式将在灰阶产生
 * --help                          Display help
 * --htmldoc                       输出程序HTML帮助
 * --ignore-load-errors            忽略claimes加载过程中已经遇到了一个错误页面
 * --lowquality                    产生低品质的PDF/ PS。有用缩小结果文档的空间
 * --manpage                       输出程序手册页
 * --margin-bottom <unitreal>      设置页面下边距 (default 10mm)
 * --margin-left <unitreal>        将左边页边距 (default 10mm)
 * --margin-right <unitreal>       设置页面右边距 (default 10mm)
 * --margin-top <unitreal>         设置页面上边距 (default 10mm)
 * --minimum-font-size             <)
 * --no-background                 不打印背景
 * --orientation <orientation>     设置方向为横向或纵向
 * --page-height <unitreal>        页面高度 (default unit millimeter)
 * --page-offset* <offset>         设置起始页码 (default )
 * --page-size <size>              设置纸张大小: A4, Letter, etc.
 * --page-width <unitreal>         页面宽度 (default unit millimeter)
 * --password <password>           HTTP验证密码
 * --post <name> <value>           Add an additional post field (repeatable)
 * --post-file <name> <path>       Post an aditional file (repeatable)
 * --print-media-type*             使用的打印介质类型，而不是屏幕
 * --proxy <proxy>                 使用代理
 * --quiet                         Be less verbose
 * --read-args-from-stdin          读取标准输入的命令行参数
 * --readme                        输出程序自述
 * --redirect-delay <msec>         等待几毫秒为JS-重定向(default )
 * --replace* <name> <value>       替换名称，值的页眉和页脚（可重复）
 * --stop-slow-scripts             停止运行缓慢的JavaScripts
 * --title <text>                  生成的PDF文件的标题（第一个文档的标题使用，如果没有指定）
 * --toc*                          插入的内容的表中的文件的开头
 * --use-xserver*                  使用X服务器（一些插件和其他的东西没有X11可能无法正常工作）
 * --user-style-sheet <url>        指定用户的样式表，加载在每一页中
 * --username <username>           HTTP认证的用户名
 * --version                       输出版本信息退出
 * --zoom                          <)
 *
 * 页眉和页脚选项
 * --header-center*    <text>  (设置在中心位置的页眉内容)
 * --header-font-name* <name>  (default Arial)(设置页眉的字体名称)
 * --header-font-size* <size>  (设置页眉的字体大小)
 * --header-html*      <url>   (添加一个HTML页眉，后面是网址)
 * --header-left*      <text>  (左对齐的页眉文本)
 * --header-line*              (显示一条线在页眉下)
 * --header-right*     <text>  (右对齐页眉文本)
 * --header-spacing*   <real>  (设置页眉和内容的距离，默认0)
 * --footer-center*    <text>  (设置在中心位置的页脚内容)
 * --footer-font-name* <name>  (设置页脚的字体名称)
 * --footer-font-size* <size>  (设置页脚的字体大小default )
 * --footer-html*      <url>   (添加一个HTML页脚，后面是网址)
 * --footer-left*      <text>  (左对齐的页脚文本)
 * --footer-line*              显示一条线在页脚内容上)
 * --footer-right*     <text>  (右对齐页脚文本)
 * --footer-spacing*   <real>  (设置页脚和内容的距离)
 *
 * 页脚和页眉
 * [page]       由当前正在打印的页的数目代替
 * [frompage]   由要打印的第一页的数量取代
 * [topage]     由最后一页要打印的数量取代
 * [webpage]    通过正在打印的页面的URL替换
 * [section]    由当前节的名称替换
 * [subsection] 由当前小节的名称替换
 * [date]       由当前日期系统的本地格式取代
 * [time]       由当前时间，系统的本地格式取代
 *
 * 轮廓选项
 * --dump-outline  <file>  转储目录到一个文件
 * --outline               显示目录（文章中h1，h2来定）
 * --outline-depth <level> 设置目录的深度（默认为4）
 *
 * 表内容选项中
 *  --toc-depth*              <level>  Set the depth of the toc (default)
 *  --toc-disable-back-links*          Do not link from section header to toc
 *  --toc-disable-links*               Do not link from toc to sections
 *  --toc-font-name*          <name>   Set the font used for the toc (default Arial)
 *  --toc-header-font-name*   <name>   The font of the toc header (if unset use --toc-font-name)
 *  --toc-header-font-size*   <size>   The font size of the toc header (default)
 *  --toc-header-text*        <text>   The header text of the toc (default Table Of Contents)
 *  --toc-l1-font-size*       <size>   Set the font size on level of the toc (default)
 *  --toc-l1-indentation*     <num>    Set indentation on level of the toc (default)
 *  --toc-l2-font-size*       <size>   Set the font size on level of the toc (default)
 *  --toc-l2-indentation*     <num>    Set indentation on level of the toc (default)
 *  --toc-l3-font-size*       <size>   Set the font size on level of the toc (default)
 *  --toc-l3-indentation*     <num>    Set indentation on level of the toc (default)
 *  --toc-l4-font-size*       <size>   Set the font size on level of the toc (default)
 *  --toc-l4-indentation*     <num>    Set indentation on level of the toc (default)
 *  --toc-l5-font-size*       <size>   Set the font size on level of the toc (default)
 *  --toc-l5-indentation*     <num>    Set indentation on level of the toc (default)
 *  --toc-l6-font-size*       <size>   Set the font size on level of the toc (default)
 *  --toc-l6-indentation*     <num>    Set indentation on level of the toc (default)
 *  --toc-l7-font-size*       <size>   Set the font size on level of the toc (default)
 *  --toc-l7-indentation*     <num>    Set indentation on level of the toc (default)
 *  --toc-no-dots*                     Do not use dots, in the toc
 * ------------------------------------------------------------------------------------------------------------
 * @version v1.2
 * @version v1.2-->支持自定义wkhtmltopdf命令目录
 * @history v1.1-->适配Linux：注意要设置工作目录，以及命令要写其所在的完整目录
 * @history v1.0-->新建
 * ------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2018/5/17 10:26.
 */
public final class WkhtmltopdfUtil {
    private WkhtmltopdfUtil() {}

    public static boolean convert(String headerHtmlPath, String htmlPath, String pdfPath){
        String commandPath;
        if(System.getProperty("os.name").contains("Windows")){
            commandPath = "wkhtmltopdf";
        }else{
            commandPath = "/opt/wkhtmltox/bin/wkhtmltopdf";
        }
        return convert(commandPath, headerHtmlPath, htmlPath, pdfPath);
    }


    /**
     * HTML文件转为PDF文件
     * -------------------------------------------------------------------------------------------------------
     * Linux环境下，即使环境变量已加入wkhtmltopdf命令，也要传该命令完整目录，否则出现下面的提示
     * java.io.IOException: Cannot run program "wkhtmltopdf": error=2, 没有那个文件或目录
     * java.io.IOException: Cannot run program "wkhtmltopdf" (in directory "/app/ifs/tmp/20180521"): error=2, 没有那个文件或目录
     * 所以Linux下既要传完整命令目录，不过Windows下直接传"wkhtmltopdf"就行（前提是已经加到环境变量）
     * -------------------------------------------------------------------------------------------------------
     * @param commandPath    wkhtmltopdf命令目录
     * @param headerHtmlPath 页眉html文件（其源码必须是以＜!DOCTYPE html>打头的html字符串）
     * @param htmlPath       html文件路径（比如：/app/ifs/contract.html，可以是本地或网络完整路径，本地文件则需含文件名和后缀）
     * @param pdfPath        pdf存储路径（比如：/app/ifs/contract.pdf，包含文件名和后缀的完整路径）
     * @return 转换成功或失败
     */
    public static boolean convert(String commandPath, String headerHtmlPath, String htmlPath, String pdfPath){
        //PDF存储目录不存在，则新建
        File parent = new File(pdfPath).getParentFile();
        if(!parent.exists()){
            parent.mkdirs();
        }
        //组装命令
        StringBuilder cmd = new StringBuilder();
        cmd.append(commandPath);
        cmd.append(" --footer-center 第[page]页／共[topage]页");
        cmd.append(" --margin-top 30mm");
        cmd.append(" --margin-bottom 20mm");
        cmd.append(" --footer-spacing 5");
        cmd.append(" --header-spacing 5");
        cmd.append(" --header-html ").append(headerHtmlPath);
        Process process;
        try{
            //执行命令（根据源HTML为网络路径或本地路径，来决定是否设置工作目录）
            String workPath = FilenameUtils.getFullPath(htmlPath);
            if(!htmlPath.startsWith("/") || StringUtils.isBlank(workPath)){
                cmd.append(" ").append(htmlPath);
                cmd.append(" ").append(pdfPath);
                process = Runtime.getRuntime().exec(cmd.toString());
            }else{
                cmd.append(" ").append(FilenameUtils.getName(htmlPath));
                cmd.append(" ").append(pdfPath);
                process = Runtime.getRuntime().exec(cmd.toString(), null, new File(workPath));
            }
            ExecutorService threadPool = Executors.newCachedThreadPool();
            threadPool.execute(new ClearBufferThread(process.getErrorStream()));
            threadPool.execute(new ClearBufferThread(process.getInputStream()));
            process.waitFor();
            threadPool.shutdown();
        }catch(Exception e){
            LogUtil.getLogger().error("HTML转PDF异常", e);
            return false;
        }
        return true;
    }


    /**
     * 清理输入流缓存的线程
     * ------------------------------------------------------------------------------
     * jdk-1.6：process.waitFor()不能正常执行，会阻塞，1.7无此问题
     * 所以1.6在接收Process的输入和错误信息时，需要创建另外的线程，否则当前线程会一直等待
     * ------------------------------------------------------------------------------
     * Created by kagome on 2016/8/9.
     */
    static class ClearBufferThread implements Runnable {
        private InputStream is;
        ClearBufferThread(InputStream is){
            this.is = is;
        }
        public void run() {
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                for(String line; (line=br.readLine())!=null;){
                    LogUtil.getLogger().info(line);
                }
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}