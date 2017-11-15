package com.jadyer.seed.seedoc;

import com.jadyer.seed.comm.util.IDUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/11/15 15:58.
 */
@Component
public class SeedocHelper {
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
        String filePath = this.getFilePath(isWangEditor) + IDUtil.INSTANCE.nextId() + (isWangEditor?"w":"") + "." + fileExtension;
        LogUtil.getLogger().info("originalFilename=[{}]，filePath=[{}]", originalFilename, filePath);
        return filePath;
    }
}