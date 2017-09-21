package com.jadyer.seed.scs;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/10 5:35.
 */
@Component
class ScsHelper {
    @Value("${app.upload}")
    private String appUpload;

    String getFilePath(boolean isWangEditor){
        return (appUpload.endsWith("/") ? appUpload : appUpload+"/") + (isWangEditor ? "wangEditor/" : "");
    }

    /**
     * 构建上传文件的存储路径
     */
    String buildUploadFilePath(String originalFilename, boolean isWangEditor){
        String fileExtension = FilenameUtils.getExtension(originalFilename);
        String fileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + JadyerUtil.randomNumeric(6);
        String filePath = this.getFilePath(isWangEditor) + fileName + (isWangEditor?"w":"") + "." + fileExtension;
        LogUtil.getLogger().info("originalFilename=[{}]，filePath=[{}]", originalFilename, filePath);
        return filePath;
    }
}