package com.jadyer.seed.comm.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * 阿里云对象存储服务（OSS：Object Storage Service）工具类
 * ----------------------------------------------------------------------------------------------------------------
 * @version v1.0
 * @history v1.0-->新建
 * ----------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2018/4/9 16:09.
 */
public class OSSUtil {
    private OSSUtil(){}

    /**
     * 获取图片的临时地址（https://help.aliyun.com/document_detail/47505.html）
     * @param bucket   存储空间名称
     * @param endpoint 存储空间所属地域的访问域名
     * @param timeout  有效时长，单位：毫秒
     * @return 返回图片的完整地址（浏览器可直接访问）
     * Comment by 玄玉<http://jadyer.cn/> on 2018/4/9 17:23.
     */
    public static String getImageTempURL(String bucket, String endpoint, String key, String accessKeyId, String accessKeySecret, long timeout) {
        String imageURL = "http://jadyer.cn/";
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, key, HttpMethod.GET);
        req.setExpiration(new Date(new Date().getTime() + timeout));
        req.setProcess("image/resize,m_fixed,w_100,h_100/rotate,90");
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            imageURL = ossClient.generatePresignedUrl(req).toString();
        } catch (OSSException oe) {
            //异常码描述见https://help.aliyun.com/document_detail/32023.html
            LogUtil.getLogger().error("服务端异常，RequestID={}，HostID={}，Code={}，Message={}", oe.getRequestId(), oe.getHostId(), oe.getErrorCode(), oe.getMessage());
        } catch (ClientException ce) {
            LogUtil.getLogger().error("客户端异常，RequestID={}，Code={}，Message={}", ce.getRequestId(), ce.getErrorCode(), ce.getMessage());
        } catch (Throwable e) {
            LogUtil.getLogger().error("获取获取图片的临时地址时发生异常，堆栈轨迹如下", e);
        } finally {
            if(null != ossClient){
                ossClient.shutdown();
            }
        }
        return imageURL;
    }


    /**
     * 文件上传
     * @param bucket   存储空间名称
     * @param endpoint 存储空间所属地域的访问域名
     * @param key      文件完整名称（建议含后缀）
     * @param is       文件流
     * Comment by 玄玉<http://jadyer.cn/> on 2018/4/9 17:24.
     */
    public static boolean upload(String bucket, String endpoint, String key, String accessKeyId, String accessKeySecret, InputStream is) {
        boolean uploadSuccess = false;
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucket, key, is);
            uploadSuccess = true;
        } catch (OSSException oe) {
            LogUtil.getLogger().error("服务端异常，RequestID={}，HostID={}，Code={}，Message={}", oe.getRequestId(), oe.getHostId(), oe.getErrorCode(), oe.getMessage());
        } catch (ClientException ce) {
            LogUtil.getLogger().error("客户端异常，RequestID={}，Code={}，Message={}", ce.getRequestId(), ce.getErrorCode(), ce.getMessage());
        } catch (Throwable e) {
            LogUtil.getLogger().error("文件上传时发生异常，堆栈轨迹如下", e);
        } finally {
            try {
                if(null != is){
                    is.close();
                }
            } catch (final IOException ioe) {
                // ignore
            }
            if(null != ossClient){
                ossClient.shutdown();
            }
        }
        return uploadSuccess;
    }


    /**
     * 文件下载
     * @param bucket   存储空间名称
     * @param endpoint 存储空间所属地域的访问域名
     * @param localURL 保存在本地的包含完整路径和后缀的完整文件名
     * Comment by 玄玉<http://jadyer.cn/> on 2018/4/9 17:24.
     */
    public static boolean download(String bucket, String endpoint, String key, String accessKeyId, String accessKeySecret, String localURL) {
        boolean downloadSuccess = false;
        if(StringUtils.isBlank(localURL)){
            //若未传localURL，则把下载到的文件放到Java临时目录
            localURL = System.getProperty("java.io.tmpdir") + "/" + key;
            //若文件名称不含后缀，那就主动添加后缀
            if("".equals(FilenameUtils.getExtension(key))){
                localURL += ".txt";
            }
        }
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            if(!ossClient.doesObjectExist(bucket, key)){
                throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "bucket=["+bucket+"]中不存在文件=["+key+"]");
            }
            //ossClient.getObject(new GetObjectRequest(bucket, key), new File(localURL));
            InputStream is = ossClient.getObject(bucket, key).getObjectContent();
            FileUtils.copyInputStreamToFile(is, new File(localURL));
            downloadSuccess = true;
        } catch (OSSException oe) {
            LogUtil.getLogger().error("服务端异常，RequestID={}，HostID={}，Code={}，Message={}", oe.getRequestId(), oe.getHostId(), oe.getErrorCode(), oe.getMessage());
        } catch (ClientException ce) {
            LogUtil.getLogger().error("客户端异常，RequestID={}，Code={}，Message={}", ce.getRequestId(), ce.getErrorCode(), ce.getMessage());
        } catch (Throwable e) {
            LogUtil.getLogger().error("文件下载时发生异常，堆栈轨迹如下", e);
        } finally {
            if(null != ossClient){
                ossClient.shutdown();
            }
        }
        return downloadSuccess;
    }
}