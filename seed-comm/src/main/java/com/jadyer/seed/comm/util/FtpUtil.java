package com.jadyer.seed.comm.util;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * FTP工具类
 * -----------------------------------------------------------------------------------------------------------
 * 1.登出时要注意ftpClient.disconnect()的时机,ftpClient.logout()也会抛异常
 *   所要注意避免FTPClient对象退出异常,连接没有释放,最后积少成多直至阻塞FTP服务器的连接,进而引发连接异常
 * 2.FTP response 421 received.  Server closed connection.
 *   这个错误的原因就是FTP服务器端连接数满了
 * 3.Connection closed without indication.
 *   这个错误的原因就是FTP服务器端发生故障或者网络出现问题
 * -----------------------------------------------------------------------------------------------------------
 * @version v2.3
 * @history v2.3-->重構并優化FTP文件下載接口：解決大文件下載時假死的情況
 * @history v2.2-->校验文件是否存在时，传入的文件名编码由ISO-8859-1改为系统默认编码，以解决明明有文件却取到空数组的问题
 * @history v2.1-->FTP文件下载后增加传输成功与否的校验,否则会导致同一FTP连接下载第二个文件时找不到文件
 * @history v2.0-->增加JSch实现的SFTP上传和下载等静态方法
 * @history v1.3-->增加FTP传输进度显示[    0%   101890  33KB/s  58351458   3s]
 * @history v1.2-->增加防止重复登录FTP的判定以及上传和下载文件时支持断点续传的备用注释代码
 * @history v1.1-->增加<code>deleteFileAndLogout(String, String, String, String)<code>删除FTP文件的方法
 * @history v1.0-->新建并提供了上传和下载文件的方法,以及操作完成后自动logout并释放连接
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2015/06/22 11:22.
 */
public final class FtpUtil {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int DEFAULT_DEFAULT_TIMEOUT = 0;
    private static final int DEFAULT_CONNECT_TIMEOUT = 1000;
    private static final int DEFAULT_DATA_TIMEOUT = 0;
    private static final int DEFAULT_SFTP_TIMEOUT = 0;
    private static ThreadLocal<FTPClient> ftpClientMap = new ThreadLocal<>();
    private static ThreadLocal<ChannelSftp> channelSftpMap = new ThreadLocal<>();
    private FtpUtil(){}

    /**
     * 连接并登录FTP服务器
     * @param hostname FTP地址
     * @param username FTP登录用户
     * @param password FTP登录密码
     * @return True if successfully completed, false if not.
     */
    private static boolean login(String hostname, String username, String password, int defaultTimeout, int connectTimeout, int dataTimeout){
        FTPClient ftpClient = ftpClientMap.get();
        if(null == ftpClient){
            ftpClientMap.remove();
            ftpClient = new FTPClient();
        }
        if(ftpClient.isAvailable() && ftpClient.isConnected()){
            return true;
        }
        ftpClient.setDefaultTimeout(0==defaultTimeout ? DEFAULT_DEFAULT_TIMEOUT : defaultTimeout);
        ftpClient.setConnectTimeout(0==connectTimeout ? DEFAULT_CONNECT_TIMEOUT : connectTimeout);
        ftpClient.setDataTimeout(0==dataTimeout ? DEFAULT_DATA_TIMEOUT : dataTimeout);
        //防止读取文件名乱码
        ftpClient.setControlEncoding(DEFAULT_CHARSET);
        //如果FTP传输速度特别慢，设置一下该参数就会大大提高传输速度（它默认的好像是1024）
        //ftpClient.setBufferSize(102400);
        //输出FTP交互过程中使用到的命令到控制台
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        try {
            ftpClient.connect(hostname, FTP.DEFAULT_PORT);
        } catch (Exception e) {
            LogUtil.getLogger().error("FTP服务器["+hostname+"]无法连接,堆栈轨迹如下", e);
            return false;
        }
        //FTP服务器连接应答码-->2开头表示连接成功
        if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
            LogUtil.getLogger().error("FTP服务器["+hostname+"]连接失败,FTP连接应答码为" + ftpClient.getReplyCode());
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                // ignore
            }
            return false;
        }
        LogUtil.getLogger().debug("FTP服务器["+hostname+"]连接成功...");
        boolean isLoginSuccess;
        try {
            isLoginSuccess = ftpClient.login(username, password);
        } catch (IOException e) {
            LogUtil.getLogger().error("FTP服务器["+hostname+"]登录失败,堆栈轨迹如下", e);
            try {
                ftpClient.disconnect();
            } catch (IOException ioe) {
                // ignore
            }
            return false;
        }
        try {
            if(isLoginSuccess){
                LogUtil.getLogger().debug("FTP服务器["+hostname+"]登录成功...当前所在目录为" + ftpClient.printWorkingDirectory());
            }else{
                LogUtil.getLogger().error("FTP服务器["+hostname+"]登录失败...");
                return false;
            }
            //设置文件类型和傳輸模式
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            /*
             * ----------------------------------------------------------------------------------------------------
             * FTP协议的两种工作方式，即PORT（主动式）和PASV（被动式）
             * PORT
             * PORT（主动式）的连接过程是：客户端向服务器的FTP端口（默认是21）发送连接请求，服务器接受连接，建立一条命令链路
             * 当需要传送数据时，客户端在命令链路上用PORT命令告诉服务器：“我打开了xxx端口，你过来连接我”
             * 于是服务器从20端口向客户端的xxx端口发送连接请求，建立一条数据链路来传送数据
             * PASV
             * PASV（被动式）的连接过程是：客户端向服务器的FTP端口（默认是21）发送连接请求，服务器接受连接，建立一条命令链路
             * 当需要传送数据时，服务器在命令链路上用PASV命令告诉客户端：“我打开了xxx端口，你过来连接我”
             * 于是客户端向服务器的xxx端口发送连接请求，建立一条数据链路来传送数据
             * ----------------------------------------------------------------------------------------------------
             * 服务端是两种模式的，使用哪种模式取决于客户端
             * 同时关键点在于网络环境适合用哪种模式，比如客户端在防火墙内，则最好选择被动模式
             * ----------------------------------------------------------------------------------------------------
             * 有时执行到FTPClient.listFiles()或者FTPClient.retrieveFile()就停住了，什么反应都没有，呈现假死状态
             * 这时通过enterLocalPassiveMode()就可以在每次数据连接之前，ftpClient告诉ftpServer开通一个端口来传输数据
             * 主要因为ftpServer可能每次开启不同的端口来传输数据，但linux上由于安全限制，可能某些端口没开启，所以出现阻塞
             * ----------------------------------------------------------------------------------------------------
             */
            ////配置為本地主动模式
            //ftpClient.enterLocalActiveMode();
            //配置為本地被动模式
            ftpClient.enterLocalPassiveMode();
            ftpClientMap.set(ftpClient);
            return true;
        } catch (IOException e) {
            // ignore
        }
        return false;
    }


    /**
     * 登出FTP服务器
     * <p>
     *     由于本工具类会自动维护FTPClient连接，故调用该方法便可直接登出FTP
     * </p>
     */
    public static void logout(){
        FTPClient ftpClient = ftpClientMap.get();
        ftpClientMap.remove();
        if(null != ftpClient){
            String ftpRemoteAddress = ftpClient.getRemoteAddress().toString();
            try{
                ftpClient.logout();
                LogUtil.getLogger().debug("FTP服务器[" + ftpRemoteAddress + "]登出成功...");
            }catch (IOException e){
                LogUtil.getLogger().warn("FTP服务器[" + ftpRemoteAddress + "]登出时发生异常，堆栈轨迹如下", e);
            }finally{
                if(ftpClient.isConnected()){
                    try {
                        ftpClient.disconnect();
                        LogUtil.getLogger().debug("FTP服务器[" + ftpRemoteAddress + "]连接释放完毕...");
                    } catch (IOException ioe) {
                        LogUtil.getLogger().warn("FTP服务器[" + ftpRemoteAddress + "]连接释放时发生异常，堆栈轨迹如下", ioe);
                    }
                }
            }
        }
    }


    /**
     * 创建远程目录
     * @param remotePath 不含文件名的远程路径(格式为/a/b/c)
     */
    private static void createRemoteFolder(FTPClient ftpClient, String remotePath) throws IOException{
        String[] folders = remotePath.split("/");
        String remoteTempPath = "";
        for(String folder : folders){
            if(StringUtils.isNotBlank(folder)){
                remoteTempPath += "/" + folder;
                boolean flag = ftpClient.changeWorkingDirectory(remoteTempPath);
                LogUtil.getLogger().info("change working directory : " + remoteTempPath + "-->" + (flag?"SUCCESS":"FAIL"));
                if(!flag){
                    flag = ftpClient.makeDirectory(remoteTempPath);
                    LogUtil.getLogger().info("make directory : " + remoteTempPath + "-->" + (flag?"SUCCESS":"FAIL"));
                }
            }
        }
    }


    /**
     * 上传文件
     * <p>
     *     该方法与{@link #uploadAndLogout(String, String, String, String, InputStream)}的区别是：
     *     上传完文件后没有登出服务器及释放连接，但会关闭输入流；
     *     之所以提供该方法是用于同时上传多个文件的情况下，使之能够共用一个FTP连接
     * </p>
     * @param hostname  目标主机地址
     * @param username  FTP登录用户
     * @param password  FTP登录密码
     * @param remoteURL 保存在FTP上的含完整路径和后缀的完整文件名
     * @param is        文件输入流
     * @return True if successfully completed, false if not.
     */
    public static boolean upload(String hostname, String username, String password, String remoteURL, InputStream is){
        if(!login(hostname, username, password, DEFAULT_DEFAULT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT, DEFAULT_DATA_TIMEOUT)){
            return false;
        }
        FTPClient ftpClient = ftpClientMap.get();
        try{
            remoteURL = FilenameUtils.separatorsToUnix(remoteURL);
            if(!ftpClient.changeWorkingDirectory(FilenameUtils.getFullPathNoEndSeparator(remoteURL))){
                createRemoteFolder(ftpClient, FilenameUtils.getFullPathNoEndSeparator(remoteURL));
                ftpClient.changeWorkingDirectory(FilenameUtils.getFullPathNoEndSeparator(remoteURL));
            }
            String remoteFile = new String(FilenameUtils.getName(remoteURL).getBytes(DEFAULT_CHARSET), "ISO-8859-1");
            ftpClient.setCopyStreamListener(new FTPProcess(is.available(), System.currentTimeMillis()));
            return ftpClient.storeFile(remoteFile, is);
        }catch(IOException e){
            LogUtil.getLogger().error("文件["+remoteURL+"]上传到FTP服务器["+hostname+"]失败,堆栈轨迹如下", e);
            return false;
        }finally{
            IOUtils.closeQuietly(is);
        }
    }


    /**
     * 上传文件
     * <p>
     *     该方法会在上传完文件后，自动登出服务器，并释放FTP连接，同时关闭输入流
     * </p>
     * @param hostname  目标主机地址
     * @param username  FTP登录用户
     * @param password  FTP登录密码
     * @param remoteURL 保存在FTP上的含完整路径和后缀的完整文件名
     * @param is        文件输入流
     * @return True if successfully completed, false if not.
     */
    public static boolean uploadAndLogout(String hostname, String username, String password, String remoteURL, InputStream is){
        try{
            return upload(hostname, username, password, remoteURL, is);
        }finally{
            logout();
        }
    }


    /**
     * 文件下载
     * <p>
     *     文件下载失败时，该方法会自动登出服务器并释放FTP连接，然后抛出RuntimeException
     *     读取文件流后，一定要调用completePendingCommand()告诉FTP传输完毕，否则会导致该FTP连接在下一次读取不到文件
     * </p>
     * @param hostname  目标主机地址
     * @param username  FTP登录用户
     * @param password  FTP登录密码
     * @param remoteURL 保存在FTP上的含完整路径和后缀的完整文件名
     */
    public static InputStream download(String hostname, String username, String password, String remoteURL){
        if(!login(hostname, username, password, DEFAULT_DEFAULT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT, DEFAULT_DATA_TIMEOUT)){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "FTP服务器登录失败");
        }
        FTPClient ftpClient = ftpClientMap.get();
        try{
            //注意这里没有写成new String(remoteURL.getBytes(DEFAULT_CHARSET), "ISO-8859-1")
            //这是因为有时会因为编码的问题导致明明ftp上面有文件，但读到的是空数组，所以我们就使用默认编码
            FTPFile[] files = ftpClient.listFiles(new String(remoteURL.getBytes(DEFAULT_CHARSET)));
            if(1 != files.length){
                logout();
                throw new SeedException(CodeEnum.FILE_NOT_FOUND.getCode(), "远程文件["+remoteURL+"]不存在");
            }
            InputStream is = ftpClient.retrieveFileStream(remoteURL);
            //拷貝InputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len;
            while((len=is.read(buff)) > -1){
                baos.write(buff, 0, len);
            }
            baos.flush();
            //事实上就像JDK的API所述：Closing a ByteArrayOutputStream has no effect
            //查询ByteArrayOutputStream.close()的源码会发现，它没有做任何事情,所以其close()与否是无所谓的
            baos.close();
            IOUtils.closeQuietly(is);
            //completePendingCommand()會一直等待FTPServer返回[226 Transfer complete]
            //但是FTPServer需要在InputStream.close()執行之後才會返回，所以要先執行InputStream.close()
            //201704121637測試發現：對於小文件，沒有調用close()，直接completePendingCommand()也會返回true
            //但大文件就會卡在completePendingCommand()位置，所以上面做了一步InputStream拷貝
            if(!ftpClient.completePendingCommand()){
                logout();
                throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "File transfer failed.");
            }
            return new ByteArrayInputStream(baos.toByteArray());
        }catch(IOException e){
            logout();
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "从FTP服务器["+hostname+"]下载文件["+remoteURL+"]失败，堆棧軌跡如下：", e);
        }
    }


    /**
     * 文件下载
     * <p>
     *     该方法会在下载完文件后，自动登出服务器，并释放FTP连接，同时关闭输入流
     * </p>
     * @param hostname  目标主机地址
     * @param username  FTP登录用户
     * @param password  FTP登录密码
     * @param remoteURL 保存在FTP上的含完整路径和后缀的完整文件名
     * @param localURL  保存在本地的包含完整路径和后缀的完整文件名
     */
    public static void downloadAndLogout(String hostname, String username, String password, String remoteURL, String localURL){
        try{
            InputStream is = download(hostname, username, password, remoteURL);
            FileUtils.copyInputStreamToFile(is, new File(localURL));
        } catch (IOException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "保存FTP服务器["+hostname+"]下载到的文件流至["+localURL+"]失败，堆棧軌跡如下：", e);
        } finally{
            logout();
        }
    }


    /**
     * 文件删除
     * <p>
     *     该方法会在删除完文件后，自动登出服务器，并释放FTP连接
     * </p>
     * @param hostname  目标主机地址
     * @param username  FTP登录用户
     * @param password  FTP登录密码
     * @param remoteURL 保存在FTP上的含完整路径和后缀的完整文件名
     * @return True if successfully completed, false if not.
     */
    public static boolean deleteFileAndLogout(String hostname, String username, String password, String remoteURL){
        if(!login(hostname, username, password, DEFAULT_DEFAULT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT, DEFAULT_DATA_TIMEOUT)){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "FTP服务器登录失败");
        }
        try{
            //ftpClient.rename(from, to)
            //ftpClient.removeDirectory(pathname)
            //如果待删除文件不存在,ftpClient.deleteFile()会返回false
            return ftpClientMap.get().deleteFile(remoteURL);
        }catch(IOException e){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "从FTP服务器["+hostname+"]删除文件["+remoteURL+"]失败", e);
        }finally{
            logout();
        }
    }


    /**
     * 连接并登录SFTP服务器
     * @param hostname FTP地址
     * @param username FTP登录用户
     * @param password FTP登录密码
     * @param timeout  超时时间,单位ms,it use java.net.Socket.setSoTimeout(timeout)
     * @return True if successfully completed, false if not.
     */
    private static boolean loginViaSFTP(String hostname, int port, String username, String password, int timeout){
        ChannelSftp channelSftp = channelSftpMap.get();
        if(null!=channelSftp && channelSftp.isConnected()){
            return true;
        }
        channelSftpMap.remove();
        JSch jsch = new JSch();
        Session session;
        Channel channel;
        try {
            session = jsch.getSession(username, hostname, port);
        } catch (JSchException e) {
            LogUtil.getLogger().warn("SFTP Server[" + hostname + "] Session created failed,堆栈轨迹如下", e);
            return false;
        }
        session.setPassword(password);
        //Security.addProvider(new com.sun.crypto.provider.SunJCE());
        //Setup Strict HostKeyChecking to no so we dont get the unknown host key exception
        session.setConfig("StrictHostKeyChecking", "no");
        try {
            session.setTimeout(timeout);
            session.connect();
        } catch (Exception e) {
            LogUtil.getLogger().warn("SFTP Server[" + hostname + "] Session connected failed,堆栈轨迹如下", e);
            return false;
        }
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftpMap.set(channelSftp);
            LogUtil.getLogger().warn("SFTP Server[" + hostname + "] connected success...当前所在目录为" + channelSftp.pwd());
            return true;
        } catch (Exception e) {
            LogUtil.getLogger().warn("SFTP Server[" + hostname + "] Opening FTP Channel failed,堆栈轨迹如下", e);
            return false;
        }
    }


    /**
     * 登出SFTP服务器
     * <p>
     *     由于本工具类会自动维护ChannelSftp，故调用该方法便可直接登出SFTP
     * </p>
     */
    public static void logoutViaSFTP(){
        ChannelSftp channelSftp = channelSftpMap.get();
        channelSftpMap.remove();
        if(null != channelSftp){
            String hostname = null;
            try{
                hostname = channelSftp.getHome();
                channelSftp.quit();
                LogUtil.getLogger().debug("SFTP服务器[" + hostname + "]登出成功...");
            }catch (Exception e){
                LogUtil.getLogger().warn("SFTP服务器[" + hostname + "]登出时发生异常，堆栈轨迹如下", e);
            }finally{
                try{
                    Session session = channelSftp.getSession();
                    if(null!=session && session.isConnected()){
                        session.disconnect();
                        LogUtil.getLogger().debug("SFTP服务器[" + hostname + "]连接释放完毕...");
                    }
                }catch(Exception e){
                    LogUtil.getLogger().warn("SFTP服务器[" + hostname + "]连接释放时发生异常，堆栈轨迹如下", e);
                }
            }
        }
    }


    /**
     * 创建远程目录
     * @param remotePath 不含文件名的远程路径(格式为/a/b/c)
     */
    private static void createRemoteFolderViaSFTP(ChannelSftp channelSftp, String remotePath){
        String[] folders = remotePath.split("/");
        String remoteTempPath = "";
        for(String folder : folders){
            if(StringUtils.isNotBlank(folder)){
                remoteTempPath += "/" + folder;
                boolean flag = true;
                try{
                    channelSftp.cd(remoteTempPath);
                }catch(SftpException e){
                    flag = false;
                }
                LogUtil.getLogger().info("change working directory : " + remoteTempPath + "-->" + (flag?"SUCCESS":"FAIL"));
                if(!flag){
                    try{
                        channelSftp.mkdir(remoteTempPath);
                        flag = true;
                    }catch(SftpException ignored){}
                    LogUtil.getLogger().info("make directory : " + remoteTempPath + "-->" + (flag?"SUCCESS":"FAIL"));
                }
            }
        }
    }


    /**
     * upload Via SFTP without auto logout
     * <p>
     *     1.写文件到不存在的目录会报告[2: No such file]
     *     2.写文件到未授权的目录会报告[3: Permission denied]
     * </p>
     * @param hostname  SFTP地址
     * @param port      SFTP端口(通常为22)
     * @param username  SFTP登录用户
     * @param password  SFTP登录密码
     * @param remoteURL 保存在SFTP上的含完整路径和后缀的完整文件名
     * @param is        文件输入流
     * @return True if successfully upload completed, false if not.
     */
    public static boolean uploadViaSFTP(String hostname, int port, String username, String password, String remoteURL, InputStream is){
        if(!loginViaSFTP(hostname, port, username, password, DEFAULT_SFTP_TIMEOUT)){
            return false;
        }
        ChannelSftp channelSftp = channelSftpMap.get();
        remoteURL = FilenameUtils.separatorsToUnix(remoteURL);
        String remoteDirectory = FilenameUtils.getFullPathNoEndSeparator(remoteURL);
        try{
            channelSftp.cd(remoteDirectory);
        }catch(SftpException e){
            createRemoteFolderViaSFTP(channelSftp, remoteDirectory);
            try{
                channelSftp.cd(remoteDirectory);
            }catch(SftpException e1){
                //nothing to do
            }
        }
        try{
            String filename = new String(FilenameUtils.getName(remoteURL).getBytes(DEFAULT_CHARSET), "ISO-8859-1");
            //channelSftp.put(is, filename);
            channelSftp.put(is, filename, new SFTPProcess(is.available(), System.currentTimeMillis()));
            return true;
        }catch(Exception e){
            LogUtil.getLogger().error("文件["+remoteURL+"]上传到FTP服务器["+hostname+"]失败,堆栈轨迹如下", e);
            return false;
        }finally{
            IOUtils.closeQuietly(is);
        }
    }


    /**
     * upload Via SFTP and auto logout
     * <p>
     *     该方法会在上传完文件后，自动登出服务器，并释放FTP连接，同时关闭输入流
     * </p>
     * @param hostname  SFTP地址
     * @param port      SFTP端口(通常为22)
     * @param username  SFTP登录用户
     * @param password  SFTP登录密码
     * @param remoteURL 保存在SFTP上的含完整路径和后缀的完整文件名
     * @param is        文件输入流
     * @return True if successfully upload completed, false if not.
     */
    public static boolean uploadAndLogoutViaSFTP(String hostname, int port, String username, String password, String remoteURL, InputStream is){
        try{
            return uploadViaSFTP(hostname, port, username, password, remoteURL, is);
        }finally{
            logoutViaSFTP();
        }
    }


    /**
     * download Via SFTP
     * <p>
     *     文件下载失败时，该方法会自动登出服务器并释放SFTP连接，然后抛出RuntimeException
     * </p>
     * @param hostname  SFTP地址
     * @param port      SFTP端口(通常为22)
     * @param username  SFTP登录用户
     * @param password  SFTP登录密码
     * @param remoteURL 保存在SFTP上的含完整路径和后缀的完整文件名
     */
    public static InputStream downloadViaSFTP(String hostname, int port, String username, String password, String remoteURL){
        if(!loginViaSFTP(hostname, port, username, password, DEFAULT_SFTP_TIMEOUT)){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "SFTP服务器登录失败");
        }
        try{
            return channelSftpMap.get().get(FilenameUtils.separatorsToUnix(remoteURL));
        }catch(SftpException e){
            logoutViaSFTP();
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "从SFTP服务器["+hostname+"]下载文件["+remoteURL+"]失败", e);
        }
    }


    /**
     * download Via SFTP and auto logout
     * <p>
     *     该方法会在下载完文件后，自动登出服务器，并释放SFTP连接，同时关闭输入流
     * </p>
     * @param hostname  SFTP地址
     * @param port      SFTP端口(通常为22)
     * @param username  SFTP登录用户
     * @param password  SFTP登录密码
     * @param remoteURL 保存在SFTP上的含完整路径和后缀的完整文件名
     * @param localURL  保存在本地的包含完整路径和后缀的完整文件名
     */
    public static void downloadAndLogoutViaSFTP(String hostname, int port, String username, String password, String remoteURL, String localURL){
        if(!loginViaSFTP(hostname, port, username, password, DEFAULT_SFTP_TIMEOUT)){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "SFTP服务器登录失败");
        }
        try{
            //channelSftpMap.get().get(remoteURL, new FileOutputStream(new File(localURL)), new SFTPProcess(is.available(), System.currentTimeMillis()));
            channelSftpMap.get().get(remoteURL, new FileOutputStream(new File(localURL)));
        }catch(Exception e){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "从SFTP服务器["+hostname+"]下载文件["+remoteURL+"]失败", e);
        }finally{
            logoutViaSFTP();
        }
    }


    /**
     * delete file Via SFTP and auto logout
     * <p>
     *     该方法会在删除完文件后，自动登出服务器，并释放FTP连接
     * </p>
     * @param hostname  SFTP地址
     * @param port      SFTP端口(通常为22)
     * @param username  SFTP登录用户
     * @param password  SFTP登录密码
     * @param remoteURL 保存在FTP上的含完整路径和后缀的完整文件名
     * @return True if successfully completed, false if not.
     */
    public static boolean deleteFileAndLogoutViaSFTP(String hostname, int port, String username, String password, String remoteURL){
        if(!loginViaSFTP(hostname, port, username, password, DEFAULT_SFTP_TIMEOUT)){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "SFTP服务器登录失败");
        }
        try{
            //channelSftpMap.get().rename(oldpath, newpath);
            //channelSftpMap.get().rmdir(path)
            channelSftpMap.get().rm(remoteURL);
            return true;
        }catch(Exception e){
            LogUtil.getLogger().error("从SFTP服务器["+hostname+"]删除文件["+remoteURL+"]失败", e);
            return false;
        }finally{
            logout();
        }
    }


    /**
     * 该方法仅用来读取FTP文件交换过程中的文件数据
     * <ul>
     *     文件交换的约定如下
     *     <li>payBatch-yyyyMMdd.ctrl：文件交换的数据文件属性文件，一般存储data文件交易记录数和data文件大小，并以[|]分隔<（data文件大小单位为字节，即java.io.File.length()）/li>
     *     <li>payBatch-yyyyMMdd.data：文件交换的数据内容，内容格式根据不同业务有所不同</li>
     *     <li>payBatch-yyyyMMdd.succ：当成功读取并业务处理了data和ctrl文件后，才会生成succ文件，以示处理过了</li>
     *     <li>无论是否有交易数据，都要在约定时间生成data和ctrl文件，哪怕是空的</li>
     * </ul>
     * <ul>
     *     下面举例说明ctrl和data文件
     *     /vc_cash/20191008/CashTransList_005103.ctrl内容为：13|656
     *     /vc_cash/20191008/CashTransList_005103.data内容为：
     *     510319042300000005|100.00|20190423001027|20190423
     *     510319042300000005|1000.00|20190423001046|20190423
     *     510319042300000005|0.01|20190525002257|20190525
     *     510319042300000005|1000.00|20190526002903|20190526
     *     510319102500000008|1000.00|20190527000139|20190527
     *     510319042300000005|0.01|20190628000638|20190628
     *     510319092800000012|3000.00|20190928023045|20190928
     *     510319102500000008|1000.00|20190828001155|20190828
     *     510319102500000008|1000.00|20190829000812|20190829
     *     510319083000000004|1000.00|20190830230020|20190830
     *     510319102300000002|1000.00|20190409000132|20190409
     *     510319041000000002|3000.00|20190410001648|20190410
     *     510319041000000003|3000.00|20190410031408|20190410
     * </ul>
     * @param bizDate  业务日期，格式为yyyyMMdd
     * @param filename FTP交换的文件完整名称，例如：/payBatch/payBatch-yyyyMMdd.data
     * @param hostname FTP地址
     * @param username FTP登录用户
     * @param password FTP登录密码
     * @return data文件内容
     */
    public static List<String> readFileData(String bizDate, String filename, String hostname, String username, String password){
        /*
        if(StringUtils.isBlank(bizDate)){
            bizDate = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyyMMdd");
        }else{
            bizDate = bizDate.replaceAll("-", "");
            try {
                DateUtils.parseDate(bizDate, "yyyyMMdd");
            } catch (ParseException e) {
                throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "无效的入参时间-->[" + bizDate + "]");
            }
        }
        */
        String dataFile = filename.replace("yyyyMMdd", bizDate);
        String ctrlFile = dataFile.replace(".data", ".ctrl");
        String succFile = dataFile.replace(".data", ".succ");
        /*
         * 下载succ文件并判断是否需要下载报表
         */
        InputStream is = null;
        try {
            is = download(hostname, username, password, succFile);
            if(null != is){
                IOUtils.closeQuietly(is);
                throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "文件["+succFile+"]已存在，故不再扫描Ctrl文件，请求终止");
            }
            LogUtil.getLogger().info("文件[{}]不存在，准备扫描Ctrl文件", succFile);
        } catch (SeedException e) {
            if(e.getCode() == CodeEnum.FILE_NOT_FOUND.getCode()){
                LogUtil.getLogger().info("文件[{}]不存在，开始扫描Ctrl文件", succFile);
            }else{
                IOUtils.closeQuietly(is);
                throw e;
            }
        }
        /*
         * 下载ctrl文件并判断是否下载成功
         */
        try {
            is = download(hostname, username, password, ctrlFile);
        } catch (SeedException e) {
            IOUtils.closeQuietly(is);
            throw e;
        }
        /*
         * 读取ctrl文件内容('交易记录数|文件大小')
         */
        String ctrlFileText;
        try{
            ctrlFileText = IOUtils.toString(is, StandardCharsets.UTF_8);
            LogUtil.getLogger().info("文件[{}]读取到的内容为[{}]", ctrlFile, ctrlFileText);
        }catch(IOException e){
            LogUtil.getLogger().error("文件[{}]读取内容失败", ctrlFile);
            IOUtils.closeQuietly(is);
            logout();
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "文件["+ctrlFile+"]内容读取失败", e);
        }
        int ctrlFileTextCount = Integer.parseInt(ctrlFileText.split("\\|", -1)[0]);
        if(ctrlFileTextCount <= 0){
            IOUtils.closeQuietly(is);
            logout();
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "文件["+ctrlFile+"]内容为["+ctrlFileTextCount+"]条，故不需处理");
        }
        /*
         * 下载data文件并判断是否下载成功
         */
        try {
            is = download(hostname, username, password, dataFile);
        } catch (SeedException e) {
            IOUtils.closeQuietly(is);
            throw e;
        }
        /*
         * 读取data文件内容
         */
        String dataFileText;
        try{
            dataFileText = IOUtils.toString(is, StandardCharsets.UTF_8);
            LogUtil.getLogger().info("文件[{}]读取到的内容为[{}]", dataFile, dataFileText);
        }catch(IOException e){
            LogUtil.getLogger().error("文件[{}]读取内容失败", dataFile);
            logout();
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "文件["+dataFile+"]内容读取失败", e);
        }finally{
            IOUtils.closeQuietly(is);
        }
        /*
         * 校验data文件记录数
         */
        String[] dataFileTexts = dataFileText.split("\n");
        if(ctrlFileTextCount != dataFileTexts.length){
            logout();
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "文件["+dataFile+"]记录数["+dataFileTexts.length+"]与Ctrl标记的["+ctrlFileTextCount+"]不符");
        }
        ///*
        // * 回写succ文件
        // */
        //uploadAndLogout(hostname, username, password, succFile, IOUtils.toInputStream(String.valueOf(ctrlFileTextCount), StandardCharsets.UTF_8));
        /*
         * 返回data内容
         */
        logout();
        return Arrays.asList(dataFileTexts);
    }


    /**
     * 回写succ文件
     * <p>
     *     该方法只有在处理完毕readFileData()文件后，再执行以示这一日的FTP交换文件已处理过
     * </p>
     * @param bizDate  业务日期，传空则默认读取前一天的文件
     * @param filename FTP交换的文件完整名称，例如：/payBatch/payBatch-yyyyMMdd.data
     * @param hostname FTP地址
     * @param username FTP登录用户
     * @param password FTP登录密码
     * @return
     */
    public static boolean writeSuccFile(String bizDate, String filename, String hostname, String username, String password){
        String succFile = filename.replace("yyyyMMdd", bizDate).replace(".data", ".succ");
        return uploadAndLogout(hostname, username, password, succFile, IOUtils.toInputStream(filename, StandardCharsets.UTF_8));
    }
}


/**
 * FTP传输进度显示
 *     0%   101890  33KB/s  58351458   3s
 *     0%   101891  33KB/s  58351458   3s
 *     0%   101892  33KB/s  58351458   3s
 *     0%   101893  33KB/s  58351458   3s
 *     0%   101894  33KB/s  58351458   3s
 *     0%   101895  33KB/s  58351458   3s
 *     0%   101896  33KB/s  58351458   3s
 * Created by 玄玉<http://jadyer.cn/> on 2015/10/22 09:42.
 */
class FTPProcess implements CopyStreamListener {
    private long fileSize;
    private long startTime;
    /**
     * @param fileSize  文件的大小,单位字节
     * @param startTime 开始的时间,可通过System.currentTimeMillis()获取
     */
    FTPProcess(long fileSize, long startTime){
        this.fileSize = fileSize;
        this.startTime = startTime;
    }
    @Override
    public void bytesTransferred(CopyStreamEvent copyStreamEvent){}
    /**
     * 本次传输了多少字节
     * @param totalBytesTransferred 到目前为止已经传输的字节数
     * @param bytesTransferred      本次传输的字节数
     * @param streamSize            The number of bytes in the stream being copied
     */
    @Override
    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize){
        long end_time = System.currentTimeMillis();
        long time = (end_time - startTime) / 1000; //耗时
        long speed;                                //速度
        if(0 == time){
            speed = 0;
        }else{
            speed = totalBytesTransferred/1024/time;
        }
        System.out.printf("\r    %d%%   %d  %dKB/s  %d   %ds", totalBytesTransferred*100/fileSize, totalBytesTransferred, speed, fileSize, time);
    }
}


/**
 * SFTP传输进度显示
 * 每次传输count字节时，就会调用new SFTPProcess(fileSize, startTime)对象的count()方法
 *     92%   18311601  17882KB/s  19903865   1s
 *     92%   18344242  17914KB/s  19903865   1s
 *     92%   18376883  17946KB/s  19903865   1s
 *     92%   18409524  17978KB/s  19903865   1s
 *     92%   18442165  18009KB/s  19903865   1s
 * Created by 玄玉<http://jadyer.cn/> on 2015/10/22 10:46.
 */
class SFTPProcess implements SftpProgressMonitor {
    private long fileSize;
    private long startTime;
    private long totalBytesTransferred = 0;
    /**
     * @param fileSize  文件的大小,单位字节
     * @param startTime 开始的时间,可通过System.currentTimeMillis()获取
     */
    SFTPProcess(long fileSize, long startTime){
        this.fileSize = fileSize;
        this.startTime = startTime;
    }
    @Override
    public void init(int op, String src, String dest, long max){}
    @Override
    public void end(){}
    /**
     * 本次传输了多少字节
     * @param count 本次传输了多少字节
     * @return true--继续传输,false--取消传输
     */
    @Override
    public boolean count(long count){
        totalBytesTransferred += count;
        long end_time = System.currentTimeMillis();
        long time = (end_time - startTime) / 1000; //耗时
        long speed;                                //速度
        if(0 == time){
            speed = 0;
        }else{
            speed = totalBytesTransferred/1024/time;
        }
        System.out.printf("\r    %d%%   %d  %dKB/s  %d   %ds", totalBytesTransferred*100/fileSize, totalBytesTransferred, speed, fileSize, time);
        return true;
    }
}
//http://www.iteye.com/problems/13329
//http://www.oschina.net/code/snippet_2667725_54609
//http://blog.csdn.net/daizhonghai1314/article/details/7738487
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.io.RandomAccessFile;
//
//import org.apache.commons.net.PrintCommandListener;
//import org.apache.commons.net.ftp.FTP;
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPFile;
//import org.apache.commons.net.ftp.FTPReply;
//
///**
// * 支持断点续传的FTP实用类
// * @version 0.1 实现基本断点上传下载
// * @version 0.2 实现上传下载进度汇报
// * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持
// */
//public class ContinueFTP {
//    public FTPClient ftpClient = new FTPClient();
//
//    public ContinueFTP() {
//        // 设置将过程中使用到的命令输出到控制台
//        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
//    }
//
//    /**
//     * 连接到FTP服务器
//     * @param hostname 主机名
//     * @param port     端口
//     * @param username 用户名
//     * @param password 密码
//     * @return 是否连接成功
//     */
//    public boolean connect(String hostname, int port, String username, String password) throws IOException {
//        ftpClient.connect(hostname, port);
//        ftpClient.setControlEncoding("GBK");
//        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
//            if (ftpClient.login(username, password)) {
//                return true;
//            }
//        }
//        disconnect();
//        return false;
//    }
//
//
//    /**
//     * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
//     * @param remote 远程文件路径
//     * @param local  本地文件路径
//     * @return 上传的状态
//     */
//    public DownloadStatus download(String remote, String local) throws IOException {
//        // 设置被动模式
//        ftpClient.enterLocalPassiveMode();
//        // 设置以二进制方式传输
//        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//        DownloadStatus result;
//        // 检查远程文件是否存在
//        FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"), "iso-8859-1"));
//        if (files.length != 1) {
//            System.out.println("远程文件不存在");
//            return DownloadStatus.Remote_File_Noexist;
//        }
//        long lRemoteSize = files[0].getSize();
//        File f = new File(local);
//        // 本地存在文件，进行断点下载
//        if (f.exists()) {
//            long localSize = f.length();
//            // 判断本地文件大小是否大于远程文件大小
//            if (localSize >= lRemoteSize) {
//                System.out.println("本地文件大于远程文件，下载中止");
//                return DownloadStatus.Local_Bigger_Remote;
//            }
//            // 进行断点续传，并记录状态
//            FileOutputStream out = new FileOutputStream(f, true);
//            ftpClient.setRestartOffset(localSize);
//            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
//            byte[] bytes = new byte[1024];
//            long step = lRemoteSize / 100;
//            long process = localSize / step;
//            int c;
//            while ((c = in.read(bytes)) != -1) {
//                out.write(bytes, 0, c);
//                localSize += c;
//                long nowProcess = localSize / step;
//                if (nowProcess > process) {
//                    process = nowProcess;
//                    if (process % 10 == 0)
//                        System.out.println("下载进度：" + process);
//                    // 更新文件下载进度,值存放在process变量中
//                }
//            }
//            in.close();
//            out.close();
//            boolean isDo = ftpClient.completePendingCommand();
//            if (isDo) {
//                result = DownloadStatus.Download_From_Break_Success;
//            } else {
//                result = DownloadStatus.Download_From_Break_Failed;
//            }
//        } else {
//            OutputStream out = new FileOutputStream(f);
//            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
//            byte[] bytes = new byte[1024];
//            long step = lRemoteSize / 100;
//            long process = 0;
//            long localSize = 0L;
//            int c;
//            while ((c = in.read(bytes)) != -1) {
//                out.write(bytes, 0, c);
//                localSize += c;
//                long nowProcess = localSize / step;
//                if (nowProcess > process) {
//                    process = nowProcess;
//                    if (process % 10 == 0)
//                        System.out.println("下载进度：" + process);
//                    // 更新文件下载进度,值存放在process变量中
//                }
//            }
//            in.close();
//            out.close();
//            boolean upNewStatus = ftpClient.completePendingCommand();
//            if (upNewStatus) {
//                result = DownloadStatus.Download_New_Success;
//            } else {
//                result = DownloadStatus.Download_New_Failed;
//            }
//        }
//        return result;
//    }
//
//
//    /**
//     * 上传文件到FTP服务器，支持断点续传
//     * @param local 本地文件名称，绝对路径
//     * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext
//     *               按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
//     * @return 上传结果
//     */
//    public UploadStatus upload(String local, String remote) throws IOException {
//        // 设置PassiveMode传输
//        ftpClient.enterLocalPassiveMode();
//        // 设置以二进制流的方式传输
//        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//        ftpClient.setControlEncoding("GBK");
//        UploadStatus result;
//        // 对远程目录的处理
//        String remoteFileName = remote;
//        if (remote.contains("/")) {
//            remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
//            // 创建服务器远程目录结构，创建失败直接返回
//            if (CreateDirecroty(remote, ftpClient) == UploadStatus.Create_Directory_Fail) {
//                return UploadStatus.Create_Directory_Fail;
//            }
//        }
//        // 检查远程是否存在文件
//        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"), "iso-8859-1"));
//        if (files.length == 1) {
//            long remoteSize = files[0].getSize();
//            File f = new File(local);
//            long localSize = f.length();
//            if (remoteSize == localSize) {
//                return UploadStatus.File_Exits;
//            } else if (remoteSize > localSize) {
//                return UploadStatus.Remote_Bigger_Local;
//            }
//            // 尝试移动文件内读取指针,实现断点续传
//            result = uploadFile(remoteFileName, f, ftpClient, remoteSize);
//            // 如果断点续传没有成功，则删除服务器上文件，重新上传
//            if (result == UploadStatus.Upload_From_Break_Failed) {
//                if (!ftpClient.deleteFile(remoteFileName)) {
//                    return UploadStatus.Delete_Remote_Faild;
//                }
//                result = uploadFile(remoteFileName, f, ftpClient, 0);
//            }
//        } else {
//            result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
//        }
//        return result;
//    }
//
//
//    /**
//     * 断开与远程服务器的连接
//     */
//    public void disconnect() throws IOException {
//        if (ftpClient.isConnected()) {
//            ftpClient.disconnect();
//        }
//    }
//
//
//    /**
//     * 递归创建远程服务器目录
//     * @param remote 远程服务器文件绝对路径
//     * @param ftpClient FTPClient对象
//     * @return 目录创建是否成功
//     */
//    public UploadStatus CreateDirecroty(String remote, FTPClient ftpClient) throws IOException {
//        UploadStatus status = UploadStatus.Create_Directory_Success;
//        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
//        if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"), "iso-8859-1"))) {
//            // 如果远程目录不存在，则递归创建远程服务器目录
//            int start = 0;
//            int end = 0;
//            if (directory.startsWith("/")) {
//                start = 1;
//            } else {
//                start = 0;
//            }
//            end = directory.indexOf("/", start);
//            while (true) {
//                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
//                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
//                    if (ftpClient.makeDirectory(subDirectory)) {
//                        ftpClient.changeWorkingDirectory(subDirectory);
//                    } else {
//                        System.out.println("创建目录失败");
//                        return UploadStatus.Create_Directory_Fail;
//                    }
//                }
//                start = end + 1;
//                end = directory.indexOf("/", start);
//                // 检查所有目录是否创建完毕
//                if (end <= start) {
//                    break;
//                }
//            }
//        }
//        return status;
//    }
//
//
//    /**
//     * 上传文件到服务器,新上传和断点续传
//     * @param remoteFile  远程文件名，在上传之前已经将服务器工作目录做了改变
//     * @param localFile   本地文件File句柄，绝对路径
//     * @param processStep 需要显示的处理进度步进值
//     * @param ftpClient   FTPClient引用
//     */
//    public UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize) throws IOException {
//        UploadStatus status;
//        // 显示进度的上传
//        long step = localFile.length() / 100;
//        long process = 0;
//        long localreadbytes = 0L;
//        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
//        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"), "iso-8859-1"));
//        // 断点续传
//        if (remoteSize > 0) {
//            ftpClient.setRestartOffset(remoteSize);
//            process = remoteSize / step;
//            raf.seek(remoteSize);
//            localreadbytes = remoteSize;
//        }
//        byte[] bytes = new byte[1024];
//        int c;
//        while ((c = raf.read(bytes)) != -1) {
//            out.write(bytes, 0, c);
//            localreadbytes += c;
//            if (localreadbytes / step != process) {
//                process = localreadbytes / step;
//                System.out.println("上传进度:" + process);
//                // 汇报上传状态
//            }
//        }
//        out.flush();
//        raf.close();
//        out.close();
//        boolean result = ftpClient.completePendingCommand();
//        if (remoteSize > 0) {
//            status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
//        } else {
//            status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
//        }
//        return status;
//    }
//
//
//    public static void main(String[] args) {
//        ContinueFTP myFtp = new ContinueFTP();
//        try {
//            myFtp.connect("192.168.21.181", 21, "nid", "123");
//            // myFtp.ftpClient.makeDirectory(new
//            // String("电视剧".getBytes("GBK"),"iso-8859-1"));
//            // myFtp.ftpClient.changeWorkingDirectory(new
//            // String("电视剧".getBytes("GBK"),"iso-8859-1"));
//            // myFtp.ftpClient.makeDirectory(new
//            // String("走西口".getBytes("GBK"),"iso-8859-1"));
//            // System.out.println(myFtp.upload("E:\\yw.flv", "/yw.flv",5));
//            // System.out.println(myFtp.upload("E:\\走西口24.mp4","/央视走西口/新浪网/走西口24.mp4"));
//            System.out.println(myFtp.download("/央视走西口/新浪网/走西口24.mp4", "E:\\走西口242.mp4"));
//            myFtp.disconnect();
//        } catch (IOException e) {
//            System.out.println("连接FTP出错：" + e.getMessage());
//        }
//    }
//}
//
//
//enum UploadStatus {
//    Create_Directory_Fail, // 远程服务器相应目录创建失败
//    Create_Directory_Success, // 远程服务器闯将目录成功
//    Upload_New_File_Success, // 上传新文件成功
//    Upload_New_File_Failed, // 上传新文件失败
//    File_Exits, // 文件已经存在
//    Remote_Bigger_Local, // 远程文件大于本地文件
//    Upload_From_Break_Success, // 断点续传成功
//    Upload_From_Break_Failed, // 断点续传失败
//    Delete_Remote_Faild; // 删除远程文件失败
//}
//
//
//enum DownloadStatus {
//    Remote_File_Noexist, // 远程文件不存在
//    Local_Bigger_Remote, // 本地文件大于远程文件
//    Download_From_Break_Success, // 断点下载文件成功
//    Download_From_Break_Failed, // 断点下载文件失败
//    Download_New_Success, // 全新下载文件成功
//    Download_New_Failed; // 全新下载文件失败
//}