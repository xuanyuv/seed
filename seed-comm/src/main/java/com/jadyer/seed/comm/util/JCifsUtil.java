package com.jadyer.seed.comm.util;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * 使用JCIFS获取远程共享文件
 * -----------------------------------------------------------------------------------------------------------
 * 1)JCIFS官网为http://jcifs.samba.org/
 * 2)关于jcifs的介绍，网上有一大片，这里谈到的远程文件指的是网络共享文件
 * 3)用法如JCifsUtil.getRemoteFile("wzf", "unicomroot", "192.168.8.57/南天-case/", "D:/mylocal/")
 * 4)据网络所说：JCIFS比较适用于单域环境，多域环境就会很麻烦(本人尚未验证)，详见http://jusescn.iteye.com/blog/757475
 * -----------------------------------------------------------------------------------------------------------
 * @version v1.1
 * @history v1.1-->抽取拷贝远程文件公共方法,使之共通用
 * @history v1.0-->初建
 * Created by 玄玉<https://jadyer.github.io/> on 2013/4/22 23:48.
 */
public final class JCifsUtil {
	private JCifsUtil(){}
	
	/**
	 * 拷贝远程文件到本地目录
	 * @param smbFile        远程SmbFile
	 * @param localDirectory 本地存储目录，本地目录不存在时会自动创建，本地目录存在时可自行选择是否清空该目录下的文件
	 * @return 拷贝结果，true--成功，false--失败
	 */
	private static boolean copyRemoteFile(SmbFile smbFile, String localDirectory) {
		SmbFileInputStream in = null;
		FileOutputStream out = null;
		try {
			File[] localFiles = new File(localDirectory).listFiles();
			if(null == localFiles){
				//目录不存在的话，就创建目录
				//new File("D:/aa/bb.et").mkdirs()会在aa文件夹下创建一个名为bb.et的文件夹
				new File(localDirectory).mkdirs();
			}else if(localFiles.length > 0){
				for(File file : localFiles){
					//清空本地目录下的所有文件
					//new File("D:/aa/bb.et").delete()会删除bb.et文件，但aa文件夹还存在
					file.delete();
				}
			}
			in = new SmbFileInputStream(smbFile);
			out = new FileOutputStream(localDirectory + smbFile.getName());
			byte[] buffer = new byte[1024];
			int len;
			while((len=in.read(buffer)) > -1){
				out.write(buffer, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			//IOUtils.closeQuietly(in);
			//IOUtils.closeQuietly(out);
			if(null != out){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	
	/**
	 * 获取远程文件
	 * @param remoteUsername 远程目录访问用户名
	 * @param remotePassword 远程目录访问密码
	 * @param remoteFilepath 远程文件地址，该参数需以IP打头，如[192.168.8.2/aa/bb.java]或者[192.168.8.2/aa/]，但是[192.168.8.2/aa]是不对的
	 * @param localDirectory 本地存储目录，该参数需以'/'结尾，如[D:/]或者[D:/mylocal/]
	 * @return 获取结果，true--成功，false--失败
	 */
	public static boolean getRemoteFile(String remoteUsername, String remotePassword, String remoteFilepath, String localDirectory) {
		if(remoteFilepath.startsWith("/") || remoteFilepath.startsWith("\\")){
			return false;
		}
		if(!(localDirectory.endsWith("/") || localDirectory.endsWith("\\"))){
			return false;
		}
		boolean isSuccess = false;
		try {
			SmbFile smbFile = new SmbFile("smb://" + remoteUsername + ":" + remotePassword + "@" + remoteFilepath);
			if(smbFile.isDirectory()){
				for(SmbFile file : smbFile.listFiles()){
					isSuccess = copyRemoteFile(file, localDirectory);
				}
			}else if(smbFile.isFile()){
				isSuccess = copyRemoteFile(smbFile, localDirectory);
			}
		} catch (SmbException e) {
			if(e.getMessage().startsWith("The network name cannot be found")){
				System.err.println("未找到网络共享");
			}else if(e.getMessage().startsWith("Failed to connect")){
				System.err.println("远程连接失败（如需VPN，请开启VPN）");
			}
		} catch (MalformedURLException e) {
			System.err.println("系统异常，堆栈轨迹如下");
			e.printStackTrace();
		}
		return isSuccess;
	}
}