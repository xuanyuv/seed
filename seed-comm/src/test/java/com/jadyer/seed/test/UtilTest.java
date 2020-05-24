package com.jadyer.seed.test;

import com.jadyer.seed.comm.util.CodecUtil;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.FTPUtil;
import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.IDUtil;
import com.jadyer.seed.comm.util.ImageUtil;
import com.jadyer.seed.comm.util.JsoupHelper;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.comm.util.OSSUtil;
import com.jadyer.seed.comm.util.PasswordUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UtilTest {
    @Test
    public void jsoupHelperTest(){
        JsoupHelper.getTianyaBBSTxt("http://bbs.tianya.cn/post-no05-284609-1.shtml?aa=bb");
    }


    @Test
    public void passwordUtilTest() {
        String password = "xuanyu";
        String passwordHash = PasswordUtil.createHash(password, "jadyer");
        System.out.println("password hash = [" + passwordHash + "]");
        System.out.println("password verify = [" + PasswordUtil.verifyPassword(password, "jadyer", passwordHash) +"]");
        System.out.println("--------------------------------------");
        passwordHash = PasswordUtil.createHash(password);
        System.out.println("new password hash = [" + passwordHash + "]");
        System.out.println("password verify = [" + PasswordUtil.verifyPassword(password, passwordHash) +"]");
    }


    /**
     * 生成AES密钥
     */
    @Test
    public void initAESKey(){
        for(int i=0; i<6; i++){
            System.out.println(CodecUtil.initKey(CodecUtil.ALGORITHM_AES, true));
        }
    }


    /**
     * 生成RSA公私钥
     */
    @Test
    public void initRSAKey(){
        Map<String, String> keyMap = CodecUtil.initRSAKey(2048);
        System.out.println("public11-->[" + keyMap.get("publicKey") + "]");
        System.out.println("private11-->[" + keyMap.get("privateKey") + "]");
        keyMap = CodecUtil.initRSAKey(2048);
        System.out.println("public22-->[" + keyMap.get("publicKey") + "]");
        System.out.println("private22-->[" + keyMap.get("privateKey") + "]");
    }


    /**
     * 加解密工具类之RSA算法测试用例
     */
    @Test
    public void codecUtilForRSATest(){
        String data = "玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉玄玉";
        //String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApaHTWseE87o9ypJ9nDFabPSe60ZODPdcMngQ4KC2kHMC0uznzmABEdmpw/Zc86JhsMer5Z4BAzu1N22gCoB4uoPr1S0SQwTEInPEuKKRnZYqqj8Yt18sOJQr7hIAYzLo9lAwJE35w84Wi9Tub3WJd5FaMWAsuNyfYoQnWj/a7cA1+sEFYNug8DVgvtJZncOWwMAohcEkjJjQSaClItTGVpsy18pt83/jWpRofy7DzDWR+svEpiUaWrB0naGoJJDqL0pYOu3z0qkxnvUrJAZAhdbpAlXImMgc8Pu3ubqRa2VtcB0V/eAfikXTl0kbwhzwaaH9BerYFckGuhu54JcvhQIDAQAB";
        //String privateKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQClodNax4Tzuj3Kkn2cMVps9J7rRk4M91wyeBDgoLaQcwLS7OfOYAER2anD9lzzomGwx6vlngEDO7U3baAKgHi6g+vVLRJDBMQic8S4opGdliqqPxi3Xyw4lCvuEgBjMuj2UDAkTfnDzhaL1O5vdYl3kVoxYCy43J9ihCdaP9rtwDX6wQVg26DwNWC+0lmdw5bAwCiFwSSMmNBJoKUi1MZWmzLXym3zf+NalGh/LsPMNZH6y8SmJRpasHSdoagkkOovSlg67fPSqTGe9SskBkCF1ukCVciYyBzw+7e5upFrZW1wHRX94B+KRdOXSRvCHPBpof0F6tgVyQa6G7ngly+FAgMBAAECggEAYuvcVBpXUE1J/EMzW+ap7+rFQxTxJpNRcB7/TXkOsATQifLNmxcBbIzH9G6eIzA3EiKeinusdSbi2yDQ9ZS3BgYmAmJxxq8BCOaFdhQ5zZlTD+yxMUtZGM190yjTLGvKcNmBdx1O71+VXZwlb1IOcOfRqy2aaCnz0x3EdwUuFtHAArnnVyeR2g2Xgl1mnEk4KQa7usMfxc6xP+8iD89S4Es7wJqUYM/ZsIiHUXv1u96JgFppd3THGPR7Ci2nrzl3UfhmVQXYqHhthVYwlDBk5Q5YVSGENSs1rbD5QWNdfjeno65eeQKRREriAorIyf41JBEIs6BCOUbsQHA4AupHHQKBgQDWPzsmJ0W9VwHsA+dKUdWz6HFAgJ4bn90OlAwH9ARHXtPc//q+Qoy7G/wNF4bsBmZl8CCG++pYZ0teKVlKEpFN0DTi4NYOdeTaNVA58Az6aMyoqUdxHxCAocIOtbkNhsBLeD9CfLxXKe5OUDNOkfA2y4+T2p5Of3ngEYLF1vhKJwKBgQDF6TO5MRpmDo2ykFwrqiObQ7xlKXNPFaxznmK0LEYzOASvO4SzOHJststSPdw8rhJ6Oh0rlocvjra+zsxNbPZQMssu2X5kvgKI4t+hWR39z/I0wvkg+CeT4SUkpcRX1TO6iQZ8uSOO/BhnOmDe5rSuP5SvZW/2J7w6sjjRlo8gcwKBgDAuFA0mc8Z6lJIQ5qiN8rL8qMtKoUOxFbM7k+EN/RBXwOlIH4k9ygwh5PLEwbC+V7TA5W+1oyOyRv6r6cqAlnCbS+lhioHB/W8c4ifFVgXSH4QmXUyRIdLrjYplT3I9RW0zY3Z+OpSXd2HhI0ieBRkteeJUHZmljTDYf0Iib7+tAoGATbQQ1b4cskM2iQw60/35+uTuW/2ZQ0ysJ7zg3gKgEU4GMNK6eC9KZbqjO4gEJ2Lk6E5W051HOHnc8C97cU69qqE2uw8zm9QqZJpG2S+HIfb6DpMag0JLL7lu/uOCokWYCL3x6Rg7iNEbt7PpArsr51oZQ4AdJFVXhuggfNGTIlcCgYEAtdRuV5P8x8U3l5Yi52ajnjrySaqIQOolI+4gp4nIK6KbwkJi092VoOC02qIQ87uq+98Zcp/X8BnfaoH3/NfsF9eUwYzYqRyvdgE6OrDmg6gvPpxzxPNpPZ58AIdO0uxmS4zGqi3i532piuLo5fBFC/gFfuDBLiAhgHyGkHfUJ2c=";
        Map<String, String> keyMap = CodecUtil.initRSAKey(2048);
        String publicKeyStr = keyMap.get("publicKey");
        String privateKeyStr = keyMap.get("privateKey");
        System.out.println("public-->[" + publicKeyStr + "]");
        System.out.println("private-->[" + privateKeyStr + "]");
        System.out.println("明文-->[" + data + "]");
        System.out.println();
        String data22 = CodecUtil.buildRSAEncryptByPrivateKey(data, privateKeyStr);
        System.out.println("私钥加密-->[" + data22 + "]");
        System.out.println("公钥解密-->[" + CodecUtil.buildRSADecryptByPublicKey(data22, publicKeyStr) +"]");
        System.out.println();
        String data33 = CodecUtil.buildRSAEncryptByPublicKey(data, publicKeyStr);
        System.out.println("公钥加密-->[" + data33 + "]");
        System.out.println("私钥解密-->[" + CodecUtil.buildRSADecryptByPrivateKey(data33, privateKeyStr) +"]");
        System.out.println();
        String data44 = CodecUtil.buildRSASignByPrivateKey(data, privateKeyStr);
        System.out.println("私钥签名-->[" + data44 +"]");
        System.out.println("公钥验签-->[" + CodecUtil.buildRSAverifyByPublicKey(data, publicKeyStr, data44) +"]");
    }


    /**
     * 图片压缩测试
     */
    @Test
    public void imageUtilTest(){
        ImageUtil.resize("C:/Users/Jadyer/Desktop/IMG_1007.JPG", "C:/Users/Jadyer/Desktop/image2233.jpg", 100);
    }


    /**
     * OSS文件上传测试
     */
    @Test
    public void ossUtilForUploadTest() throws IOException {
        String bucket = "";
        String endpoint = "";
        String accessKeyId = "";
        String accessKeySecret = "";
        String filename = "https://jadyer.cn/img/2015/2015-11-14-childhood-haerbin-07.png";
        InputStream is = new URL(filename).openStream();
        String key = "ifs/test/" + IDUtil.INSTANCE.nextId() + "." + FilenameUtils.getExtension(filename);
        OSSUtil.upload(bucket, endpoint, key, accessKeyId, accessKeySecret, is);
        System.out.println("本次上传的ossKey=" + key);
    }


    /**
     * 文件上传测试
     */
    @Test
    public void httpUtilForUploadTest() throws FileNotFoundException {
        String reqURL = "http://127.0.0.1:8080/engine/file/upload";
        String filename = "菱纱.jpg";
        InputStream is = new FileInputStream("E:\\Wallpaper\\菱纱.jpg");
        String fileBodyName = "fileData";
        Map<String, String> params = new HashMap<>();
        params.put("serialNo", UUID.randomUUID().toString().replaceAll("-", ""));
        String respData = HTTPUtil.upload(reqURL, filename, is, fileBodyName, params);
        System.out.println("文件上传完毕，收到应答报文" + respData);
    }


    /**
     * 文件下载测试
     */
    @Test
    public void httpUtilForDownloadTest() {
        String reqURL = "http://127.0.0.1:8080/engine/file/download";
        Map<String, String> params = new HashMap<>();
        params.put("sysCode", "33");
        Map<String, String> resultMap = HTTPUtil.download(reqURL, params);
        if("yes".equals(resultMap.get("isSuccess"))){
            System.out.println("文件下载成功，保存路径为" + resultMap.get("fullPath"));
        }else{
            System.out.println("文件下载失败，失败原因为" + resultMap.get("failReason"));
        }
    }


    /**
     * FTP文件读取测试
     */
    @Test
    public void FTPUtilForReadFileDataTest(){
        //String bizDate = "20191008";
        String bizDate = "20170115";
        String username = "yangguang01";
        String password = "yangguang01@#$";
        String host = "ftp.jadyer.test";
        String filename = "/vc_cash/yyyyMMdd/CashTransList_005103.data";
        List<String> withdrawDataList = FTPUtil.readFileData(bizDate, filename, host, username, password);
        for(String obj : withdrawDataList){
            System.out.println("读取到-->[" + obj + "]");
        }
    }


    /**
     * FTP上传测试
     */
    @Test
    public void FTPUtilForUploadTest() throws IOException {
        //InputStream is = FileUtils.openInputStream(new File("E:\\Wallpaper\\三大名迹.jpg"));
        //String remoteURL = "/mytest/02/03/" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".jpg";
        //Assert.assertTrue(FTPUtil.upload("192.168.2.60", "ftpupload", "HUvueMGWg92y8SSN", remoteURL, is));
        //is = FileUtils.openInputStream(new File("E:\\Wallpaper\\Wentworth.Miller.jpg"));
        //remoteURL = "/mytest/02/03/" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss_2") + ".jpg";
        //Assert.assertTrue(FTPUtil.upload("192.168.2.60", "ftpupload", "HUvueMGWg92y8SSN", remoteURL, is));
        //FTPUtil.logout();
        InputStream is = FileUtils.openInputStream(new File("F:\\Tool\\Enterprise_Architect_8.0.858.zip"));
        String remoteURL = "/mytest/02/03/" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".jpg";
        Assert.assertTrue(FTPUtil.uploadAndLogout("192.168.2.60", "ftpupload", "HUvueMGWg92y8SSN", remoteURL, is));
    }


    /**
     * FTP下载测试
     */
    @Test
    public void FTPUtilForDownloadTest() throws IOException {
        String host = "ftp.jadyer.test";
        String username = "yangguang01";
        String password = "yangguang01@#$";
        String remoteURL = "/vc_cash/20170115/CashTransList_005103.data";
        String localURL = "D:\\home\\bb.txt";
        FTPUtil.downloadAndLogout(host, username, password, remoteURL, localURL);
    }


    /**
     * FTP删除测试
     */
    @Test
    public void FTPUtilForDeleteFileTest(){
        String remoteURL = "/mytest/02/03/20151006151054_test.jpg";
        Assert.assertTrue("文件不存在", FTPUtil.deleteFileAndLogout("192.168.2.60", "ftpupload", "HUvueMGWg92y8SSN", remoteURL));
    }


    /**
     * SFTP上传测试
     */
    @Test
    public void FTPUtilForUploadViaSFTPTest() throws IOException{
        InputStream is = FileUtils.openInputStream(new File("F:\\Tool\\Wireshark-win32-1.4.9中文版.exe"));
        String remoteURL = "/upload/test/sf/" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".exe";
        Assert.assertTrue(FTPUtil.uploadAndLogoutViaSFTP("192.168.2.41", 22, "yizhifu", "YMQwcUZh2LvhmR87d7tjmqoRbj6ST1", remoteURL, is));
    }


    /**
     * SFTP下载测试
     */
    @Test
    public void FTPUtilForDownloadViaSFTPTest() throws IOException {
        String remoteURL = "/upload/test/sf/20151022151736.exe";
        String localURL = "C:\\Users\\Jadyer.JADYER-PC.000\\Desktop\\aa.exe";
        FTPUtil.downloadAndLogoutViaSFTP("192.168.2.41", 22, "yizhifu", "YMQwcUZh2LvhmR87d7tjmqoRbj6ST1", remoteURL, localURL);
    }


    /**
     * SFTP删除测试
     */
    @Test
    public void FTPUtilForDeleteFileViaSFTPTest(){
        String remoteURL = "/upload/test/sf/20151022151451.exe";
        Assert.assertTrue("文件不存在", FTPUtil.deleteFileAndLogoutViaSFTP("192.168.2.41", 22, "yizhifu", "YMQwcUZh2LvhmR87d7tjmqoRbj6ST1", remoteURL));
    }


    @Test
    public void dateUtilTest() throws ParseException {
        Date begin = DateUtils.parseDate("20170808000800", "yyyyMMddHHmmss");
        Date end = DateUtils.parseDate("20170818000800", "yyyyMMddHHmmss");
        System.out.println(DateUtil.getDistanceTime(begin, end));
        System.out.println(DateUtil.getDistanceDay(begin, end));
    }


    @Test
    public void moneyUtilTest(){
        System.out.println(MoneyUtil.toChinese("0"));
        System.out.println(MoneyUtil.toChinese("00"));
        System.out.println(MoneyUtil.toChinese("000"));
        System.out.println(MoneyUtil.toChinese("0.0"));
        System.out.println(MoneyUtil.toChinese("0.00"));
        System.out.println(MoneyUtil.toChinese("0.000"));
        System.out.println(MoneyUtil.toChinese("0.001"));
        System.out.println(MoneyUtil.toChinese("1.01"));
        System.out.println(MoneyUtil.toChinese("1.00"));
        System.out.println(MoneyUtil.toChinese("987654321.00"));
        Assert.assertEquals("玖仟玖佰玖拾玖万伍仟陆佰柒拾捌亿玖仟零壹拾贰万叁仟肆佰伍拾陆元柒角捌分玖厘", MoneyUtil.toChinese("9999567890123456.7899"));
    }


    /**
     * hibernate.validator测试
     */
    @Test
    public void validatorUtilTest(){
        ValidateUser user = new ValidateUser();
        //user.setName("铁面生");
        String validateMsg = ValidatorUtil.validate(user, "id");
        System.out.print("User验证结果为[" + validateMsg + "]-->");
        if(StringUtils.isBlank(validateMsg)){
            System.out.println("验证通过");
        }else{
            System.out.println("验证未通过");
        }
        System.out.println("-------------------------------");
        ValidateUserDetail userDetail = new ValidateUserDetail();
        userDetail.setId(2);
        //userDetail.setSex("M");
        validateMsg = ValidatorUtil.validate(userDetail);
        System.out.print("UserDetail验证[" + validateMsg + "]-->");
        if(StringUtils.isBlank(validateMsg)){
            System.out.println("验证通过");
        }else{
            System.out.println("验证未通过");
        }
    }
    class ValidateUser{
        @Min(1)
        private int id;
        @NotBlank
        private String name;
        public int getId() {
            return id;
        }
        void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        void setName(String name) {
            this.name = name;
        }
    }
    public class ValidateUserDetail extends ValidateUser {
        @NotBlank
        @Pattern(regexp="^M|F$", message="性别只能传M或F")
        private String sex;
        public String getSex() {
            return sex;
        }
        void setSex(String sex) {
            this.sex = sex;
        }
    }
}