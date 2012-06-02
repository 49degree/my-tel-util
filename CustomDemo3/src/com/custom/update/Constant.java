package com.custom.update;

import android.os.Environment;

import com.custom.utils.Logger;
import com.custom.utils.MainApplication;

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
//	{"updates":[
//    {"updateId":"1","updateTime":"2012-3-26 18:00:00","fileName":"file1.zip"},
//    {"updateId":"2","updateTime":"2012-3-28 18:00:00","fileName":"file2.zip"},
//   ]}
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

	public enum FileDirType{
		data,sd,extSd
	}
	public enum DirType{
		assets,file,sd,extSd
	}
	static{
		QUERY_URL = MainApplication.getInstance().getString(R.string.QUERY_URL);
		INSTALLED_URL = MainApplication.getInstance().getString(R.string.INSTALLED_URL);
		sdPath = MainApplication.getInstance().getString(R.string.D_ROOT);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			extSdPath =  MainApplication.getInstance().getString(R.string.TF_ROOT);;
		}
		dataPath =  MainApplication.getInstance().getFilesDir().getAbsolutePath();
		check_mac_url = MainApplication.getInstance().getString(R.string.check_mac_url);

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
}
