package com.custom.update;

import java.util.HashMap;

import android.os.Environment;

import com.custom.utils.MainApplication;

public class Constant {
	public final static String QUERY_URL = "http://www.nnetonline.com/mobile/softad";
	public final static String INSTALLED_URL = "http://www.nnetonline.com/mobile/SoftRpt";
	public final static String installedInfo = "installedInfo.txt";
	private static String sdPath = null;
	private static String dataPath = null;

	
	static{

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
