package com.jadyer.seed.comm.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Jsoup帮助类
 * Created by 玄玉<https://jadyer.cn/> on 2017/1/7 14:20.
 */
public final class JsoupHelper {
    private JsoupHelper(){}

    /**
     * 抓取天涯论坛帖子内容
     */
    public static void getTianyaBBSTxt(String bbsURL){
        try {
            getTianyaBBSTxt(bbsURL, 999999);
        } catch (IOException e) {
            System.out.println("抓取失败，堆栈轨迹如下：");
            e.printStackTrace();
        }
    }

    /**
     * 抓取天涯论坛帖子内容
     * 目前只抓取楼主发言部分，且内容会存储到用户桌面的文章URL同名txt文件中
     * @param bbsURL      帖子地址（支持传入首页地址或本帖其它任意页面的地址）
     * @param finalPageNo 帖子的最大的页码（如传入页码超出实际最大页码，这里在抓取完最大页码内容后，会自动停止作业）
     */
    private static void getTianyaBBSTxt(String bbsURL, int finalPageNo) throws IOException {
        String txt;
        String author;
        String publishTime;
        Element atlInfo;
        Elements elements;
        Document document;
        //去掉URL中的参数
        bbsURL = bbsURL.endsWith("shtml") ? bbsURL : bbsURL.substring(0, bbsURL.indexOf(".shtml")+6);
        //计算待写入的txt文件，并预先清空里面的内容（如果已存在）
        String filePath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        String fileName = bbsURL.substring(bbsURL.indexOf("post")).replace(".shtml", ".txt");
        File bbsFile = new File(filePath, fileName);
        FileUtils.writeStringToFile(bbsFile, "", StandardCharsets.UTF_8);
        //获取帖子的起始页码
        int pageNo = Integer.parseInt(bbsURL.substring(bbsURL.lastIndexOf("-")+1, bbsURL.lastIndexOf(".")));
        //开始处理所有页面的所有楼层
        for(int i=pageNo; i<finalPageNo; i++){
            if(i == 1){
                /*
                 * 单独处理首层楼（首层楼只存在于首页）
                 */
                document = Jsoup.connect(bbsURL).get();
                //读取作者和发布时间
                atlInfo = document.getElementById("post_head").select("div.atl-info").first();
                author = atlInfo.select("span").eq(0).select("a").first().text();
                publishTime = atlInfo.select("span").eq(1).text();
                //获取楼层内容：每一个<div class="atl-item"></div>都代表一个楼层，首层也不例外
                elements = document.getElementsByClass("atl-item");
                //楼层具体内容都是在<div class="bbs-content"></div>里面包着的
                txt = elements.first().select("div.bbs-content").html().replaceAll("<br>", "");
                //写入txt
                FileUtils.writeStringToFile(bbsFile, "楼主："+author+"，"+publishTime+"\r\n", StandardCharsets.UTF_8, true);
                FileUtils.writeStringToFile(bbsFile, txt+"\r\n\r\n", StandardCharsets.UTF_8, true);
                //需要移除已处理过的首层楼
                elements.remove(0);
            }else{
                /*
                 * 对于非首页的帖子，每次都需重新计算URL，并重新抓取内容
                 */
                bbsURL = bbsURL.replace("-"+(i-1)+".shtml", "-"+i+".shtml");
                document = Jsoup.connect(bbsURL).get();
                //超出帖子最终页码的访问，会被天涯重定向到最终页码页
                if(!bbsURL.equals(document.location())){
                    System.out.println("帖子抓取完毕");
                    return;
                }
                //得到本页需要抓取的elements
                elements = document.getElementsByClass("atl-item");
            }
            /*
             * 上面两种条件，最终都是计算好本页需要迭代处理的elements
             */
            for(Element obj: elements){
                atlInfo = obj.select("div.atl-info").first();
                String authorType = atlInfo.select("strong.host").text();
                //作者类型为空就说明，该楼层非楼主发言，暂时不写入txt
                if(StringUtils.isNotBlank(authorType)){
                    txt = obj.select("div.bbs-content").html().replaceAll("<br>", "");
                    author = atlInfo.select("span").eq(0).select("a").first().text();
                    publishTime = atlInfo.select("span").eq(1).text();
                    FileUtils.writeStringToFile(bbsFile, authorType+"："+author+"，"+publishTime+"\r\n", StandardCharsets.UTF_8, true);
                    FileUtils.writeStringToFile(bbsFile, txt+"\r\n\r\n", StandardCharsets.UTF_8, true);
                }
            }
        }
    }
}