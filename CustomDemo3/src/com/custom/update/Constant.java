package com.custom.update;

import android.os.Environment;

import com.custom.utils.MainApplication;

public class Constant {
	public  static String QUERY_URL = "";
	public  static String INSTALLED_URL = "";
	public final static String installedInfo = "installedInfo.txt";
	private static String sdPath = null;
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

	public enum FileDirType{
		data,sd
	}
	
	static{
		QUERY_URL = MainApplication.getInstance().getString(R.string.QUERY_URL);
		INSTALLED_URL = MainApplication.getInstance().getString(R.string.INSTALLED_URL);
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		dataPath =  MainApplication.getInstance().getFilesDir().getAbsolutePath();
	}
	public static String getSdPath(){
		return sdPath;
	}
	
	public static String getDataPath(){
		return dataPath;
	}
}
