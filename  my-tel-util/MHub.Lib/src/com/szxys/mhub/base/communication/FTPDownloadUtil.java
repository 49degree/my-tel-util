package com.szxys.mhub.base.communication;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.IOException;

/**
 * FTP下载工具类
 * @author sujinyi
 */

public class FTPDownloadUtil {

	/**
	 * FTP地址
	 */
	private String url;
	
	
	/**
	 * FTP用户名
	 */
	private String username;
	
	/**
	 * FTP密码
	 */
	private String password;
	
	
	/**
	 * FTP下载目录
	 */
	private String directory;

	
	/**
	 *构造方法
	 * @param url:FTP服务器地址
	 * @param username:用户名
	 * @param password:密码
	 */
	public FTPDownloadUtil(String url,String username,String password){
		this.url=url;
		this.username=username;
		this.password=password;
		
	}
	
	
	
	/**
	 * 分解一个整个FTP下载地址的方法，把完整的地址分解成 服务器地址、目录、文件名三段
	 * @param str :一个完整的FTP下载地址 ,例如"ftp://192.168.0.1/test/test/test.rar"
	 * @return 
	 */
	public static String[] checkUrl(String str){
		String temp[]=str.split("/");
		String strs[]=new String[3];
		if(temp.length>3){
			StringBuffer dir=new StringBuffer("");
			for(int i=3;i<temp.length-1;i++){
				StringBuffer sb=new StringBuffer("//"+temp[i]);
				dir.append(sb);
			}
			strs[1]=new String(dir);
		}
		strs[0]=temp[2];
		strs[2]=temp[temp.length-1];
		
		return strs;
	}
	/**
	 * 执行下载操作
	 * @param url:下载完整路径，例如"ftp://test/test1.rar"
	 * @param localdir:本地保存地址
	 * @return:下载成功返回true，反之false
	 */
	public boolean downloadFile(String url,String localdir){
		boolean result=false;
		
		String temp[]=checkUrl(url);
		if(temp.length!=3)
			return result;
		url=temp[0];
		
		result=downloadFile(temp[1],temp[2],localdir);
		
		return result;
	}
	
	/**
	 * 执行下载操作
	 * @param webdir:下载目录，例如"test//test1"
	 * @param fileName:文件名
	 * @param localdir:本地保存地址
	 * @return:下载成功返回true，反之false
	 */
	public boolean downloadFile(String webdir,String fileName,String localdir){
		boolean result=false;
		
		checkDownLoadDirectory(localdir);
		
		FTPClient ftp=new FTPClient();
		
		try {
			ftp.connect(url);
			ftp.login(username, password);
			
			if(!"".equals(webdir.trim()))
				ftp.changeDirectory(webdir);
			
			ftp.download(fileName, new File(localdir+"//"+fileName));
			result=true;
			ftp.disconnect(true);
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			
			e.printStackTrace();
		} catch (FTPException e) {
			
			e.printStackTrace();
		} catch (FTPDataTransferException e) {
			
			e.printStackTrace();
		} catch (FTPAbortedException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 检测文件夹是否存在，如果不存在则创建一个
	 * @param url:要检查的文件夹地址
	 */
	private void checkDownLoadDirectory(String url){
		File file=new File(url);
		
		if(!file.isDirectory()){
			file.mkdir();
		}
	}
	
	/**
	 * 以下为Geter和Setter
	 */
	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getDirectory() {
		return directory;
	}


	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	
}
