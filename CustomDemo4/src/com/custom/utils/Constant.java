package com.custom.utils;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JLabel;



public class Constant {
	static Logger logger = Logger.getLogger(Constant.class);
	public  static String QUERY_URL = "";
	public  static String INSTALLED_URL = "";
	public final static String installedInfo = "installedInfo.txt";
	public final static String installedFold = "DataFoldCount.txt";
	private static String sdPath = null;
	private static String extSdPath = null;
	private static String dataPath = null;
	public final static String path="custom";
	public final static String timeFormate = "yyyy-MM-dd HH:mm-ss";
	
	//升级文件信息
//	{"updates":[ {"updateId":"2","updateTime":"2012-3-26 18:00:00","fileName":"update1.zip","content":[{"name":"语文","value":4},{"name":"数学","value":1}]}, ]} 
	
	public final static String success="success";
	public final static String root="updates";
	public final static String modifyTime="modifyTime";
	public final static String updateId="updateId";
	public final static String updateTime="updateTime";
	public final static String fileName="fileName";
	public final static FileDirType fileDirType=FileDirType.data;
	public final static String filePath="filePath";
	public final static String fileUnziped="fileUnziped";
	public final static String fileContent="content";
	
	public static String root_fold = "custom";
	
	public static String inited_file_fold = "custom/inited";
	public static String inited_file_info_file = "inited_file_info.txt";
	
	public static String copy_file_fold = "custom/copy_file";
	public static String copy_file_info_file = "copy_file_info.txt";
	public static String check_mac_info_file = "check_mac_info.txt";
	public static String check_mac_url = "";
	public static String wifi_manager = "";
	public static String sdFlagFile = "sdFlagFile";
	public static String extSdFlagFile = "extSdFlagFile";
	public static String SERVER_CONFIG = "config.properties";
	public static boolean isConnectPad = false;
	public static String fileEncode = null;
	public static String[] mediaTypes = null;
	public static String[] picTypes = null;
	public static String[] mp3Types = null;
	
	public static String[] bookTypes = null;
	public static String[] docTypes = null;

	public static String mediaDirName = null;
	public static String helpFileName = null;
	public static String leftPic1 = null;
	public static String leftPic2 = null;
	public static String leftPic3 = null;
	public static String leftPic4 = null;
	public static String leftPic5 = null;
	public static String leftPic6 = null;
	
	public final static String userName="userName";
	public final static String userPsd="userPsd";
	public final static String headerPic="headerPic";
	
	
	
	
	public enum FileDirType{
		data,sd,extSd
	}
	public enum DirType{
		assets,file,sd,extSd
	}
	static{
		Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ SERVER_CONFIG));
		}catch(Exception e){
			
		}


		QUERY_URL = properties.getProperty("QUERY_URL");
		INSTALLED_URL = properties.getProperty("INSTALLED_URL");

		check_mac_url = properties.getProperty(check_mac_url);
		wifi_manager = properties.getProperty(wifi_manager);
		
		fileEncode = properties.getProperty("fileEncode");
		
		if(properties.getProperty("mediaType")!=null){
			mediaTypes = properties.getProperty("mediaType").split(":");
		}
		if(properties.getProperty("picType")!=null){
			picTypes = properties.getProperty("picType").split(":");
		}
		if(properties.getProperty("mp3Type")!=null){
			mp3Types = properties.getProperty("mp3Type").split(":");
		}
		
		if(properties.getProperty("bookType")!=null){
			bookTypes = properties.getProperty("bookType").split(":");
		}
		if(properties.getProperty("docType")!=null){
			docTypes = properties.getProperty("docType").split(":");
		}
		
		if(properties.getProperty("leftPic1")!=null){
			leftPic1 = properties.getProperty("leftPic1");
		}
		if(properties.getProperty("leftPic2")!=null){
			leftPic2 = properties.getProperty("leftPic2");
		}
		if(properties.getProperty("leftPic3")!=null){
			leftPic3 = properties.getProperty("leftPic3");
		}
		if(properties.getProperty("leftPic4")!=null){
			leftPic4 = properties.getProperty("leftPic4");
		}
		if(properties.getProperty("leftPic5")!=null){
			leftPic5 = properties.getProperty("leftPic5");
		}
		if(properties.getProperty("leftPic6")!=null){
			leftPic6 = properties.getProperty("leftPic6");
		}
		helpFileName = properties.getProperty("helpFileName");
		mediaDirName = properties.getProperty("mediaDirName");

	}
	public static String getSdPath(){
		return sdPath;
	}
	public static String getExtSdPath(){
		return extSdPath;
	}	
	public static String getDataPath(){
		return dataPath;
	}
	
	/**
	 * 获取SD,EXTSD路径,不存在则返回空
	 * 
	 * @param type 0=SD;1=EXTSD
	 * @return
	 */
	public static String getWinSdPath(int type) {
		//File root = new File(".");
		File[] roots= File.listRoots();
		
		File temp = null;
		for (int i = 0; i < roots.length; i++) {
			//System.out.println(roots[i]);
			if(type==0){
				temp = new File(roots[i]+Constant.path+File.separator+Constant.sdFlagFile);
				sdPath = roots[i].getAbsolutePath().toLowerCase();
			}else if(type==1){
				temp = new File(roots[i]+Constant.path+File.separator+Constant.extSdFlagFile);
				extSdPath = roots[i].getAbsolutePath().toLowerCase();
			}else{
				return null;
			}
			if(temp.exists()){
				isConnectPad = true;
				return temp.getAbsolutePath();
			}
		}
		if(type==0){
			
			isConnectPad = false;
		}
		return null;

	}
	
	

	
	public static void main(String[] args){
		System.out.println(Constant.getWinSdPath(1));
	}
}
