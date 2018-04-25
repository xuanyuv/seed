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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * 阿里云对象存储服务（OSS：Object Storage Service）工具类
 * ----------------------------------------------------------------------------------------------------------------
 * @version v1.1
 * @history v1.1-->获取图片临时URL接口支持自定义x-oss-process参数
 * @history v1.0-->新建
 * ----------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2018/4/9 16:09.
 */
public class OSSUtil {
    private OSSUtil(){}

    /**
     * 获取图片的临时地址
     * 图片处理：https://help.aliyun.com/document_detail/47505.html
     * 异常码描：https://help.aliyun.com/document_detail/32023.html
     * @param bucket   存储空间名称
     * @param endpoint 存储空间所属地域的访问域名
     * @param timeout  有效时长，单位：分钟
     * @param process  图片的x-oss-process参数值（传空则返回原图），举例：image/resize,p_50表示将图按比例缩略到原来的1/2
     * @return 返回图片的完整地址（浏览器可直接访问）
     * Comment by 玄玉<http://jadyer.cn/> on 2018/4/9 17:23.
     */
    public static String getImgURL(String bucket, String endpoint, String key, String accessKeyId, String accessKeySecret, String process, int timeout) {
        LogUtil.getLogger().info("获取图片临时URL，请求ossKey=[{}]，process=[{}], timeout=[{}]min", key, process, timeout);
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, key, HttpMethod.GET);
            req.setExpiration(DateUtils.addMinutes(new Date(), timeout));
            req.setProcess(StringUtils.isNotBlank(process) ? process : "image/resize,p_100");
            String imgURL = ossClient.generatePresignedUrl(req).toString();
            LogUtil.getLogger().info("获取图片临时URL，请求ossKey=[{}]，应答imgUrl=[{}]", key, imgURL);
            return imgURL;
        } catch (OSSException oe) {
            throw new SeedException("获取图片临时URL，OSS服务端异常，RequestID="+oe.getRequestId() + "，HostID="+oe.getHostId() + "，Code="+oe.getErrorCode() + "，Message="+oe.getMessage());
        } catch (ClientException ce) {
            throw new SeedException("获取图片临时URL，OSS客户端异常，RequestID="+ce.getRequestId() + "，Code="+ce.getErrorCode() + "，Message="+ce.getMessage());
        } catch (Throwable e) {
            throw new SeedException("获取图片临时URL，OSS未知异常：" + e.getMessage());
        } finally {
            if(null != ossClient){
                ossClient.shutdown();
            }
        }
    }


    /**
     * 文件上传
     * @param bucket   存储空间名称
     * @param endpoint 存储空间所属地域的访问域名
     * @param key      文件完整名称（建议含后缀）
     * @param is       文件流
     * Comment by 玄玉<http://jadyer.cn/> on 2018/4/9 17:24.
     */
    public static void upload(String bucket, String endpoint, String key, String accessKeyId, String accessKeySecret, InputStream is) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucket, key, is);
        } catch (OSSException oe) {
            throw new SeedException("文件上传，OSS服务端异常，RequestID="+oe.getRequestId() + "，HostID="+oe.getHostId() + "，Code="+oe.getErrorCode() + "，Message="+oe.getMessage());
        } catch (ClientException ce) {
            throw new SeedException("文件上传，OSS客户端异常，RequestID="+ce.getRequestId() + "，Code="+ce.getErrorCode() + "，Message="+ce.getMessage());
        } catch (Throwable e) {
            throw new SeedException("文件上传，OSS未知异常：" + e.getMessage());
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
    }


    /**
     * 文件下载
     * @param bucket   存储空间名称
     * @param endpoint 存储空间所属地域的访问域名
     * @param localURL 保存在本地的包含完整路径和后缀的完整文件名，若传空则默认放到Java临时目录中
     * @return localURL
     * Comment by 玄玉<http://jadyer.cn/> on 2018/4/9 17:24.
     */
    public static String download(String bucket, String endpoint, String key, String accessKeyId, String accessKeySecret, String localURL) {
        if(StringUtils.isBlank(localURL)){
            //若未传localURL，则把下载到的文件放到Java临时目录
            localURL = System.getProperty("java.io.tmpdir") + "/ossutil-download/" + key;
            ////若文件名称不含后缀，那就主动添加后缀
            //if("".equals(FilenameUtils.getExtension(key))){
            //    localURL += ".txt";
            //}
        }
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            if(!ossClient.doesObjectExist(bucket, key)){
                throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "bucket=["+bucket+"]中不存在文件=["+key+"]");
            }
            //ossClient.getObject(new GetObjectRequest(bucket, key), new File(localURL));
            InputStream is = ossClient.getObject(bucket, key).getObjectContent();
            FileUtils.copyInputStreamToFile(is, new File(localURL));
            return localURL;
        } catch (OSSException oe) {
            throw new SeedException("文件下载，OSS服务端异常，RequestID="+oe.getRequestId() + "，HostID="+oe.getHostId() + "，Code="+oe.getErrorCode() + "，Message="+oe.getMessage());
        } catch (ClientException ce) {
            throw new SeedException("文件下载，OSS客户端异常，RequestID="+ce.getRequestId() + "，Code="+ce.getErrorCode() + "，Message="+ce.getMessage());
        } catch (Throwable e) {
            throw new SeedException("文件下载，OSS未知异常：" + e.getMessage());
        } finally {
            if(null != ossClient){
                ossClient.shutdown();
            }
        }
    }
}