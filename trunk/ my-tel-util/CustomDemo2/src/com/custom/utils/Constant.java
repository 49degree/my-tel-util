package com.custom.utils;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;

import com.custom.view.R;

public class Constant {
	public final static String foldPath = "foldPath";
	public static String foldName="";
	public static String foldName_ex = "";
	public static String path = "";
	public final static float zoom = 0.6f;
	public static String pageNumPicPath="";
	public final static String bgPicName = "BACKGROUP";
	public final static String raw_first_name = "INDEX|PLAY";
	public final static String imageIndexFileName="IndexImageIndex.txt";
	public final static String mapFileName="map.txt";
	public final static int fistFoldDepth=0;
	public final static String foldDepth="foldDepth";
	public final static String swfView = "file:///android_asset/swf_view.htm";
	public final static String swfView2 = "swf_view.htm";
	public final static String viewClass="viewClass";
	public final static String secondViewClass="com.custom.view.SecondView";
	public final static String resourceFoldEnd="_raw";
	public final static String resourceFold="raw";
	public final static String backGroundSwfName = "background.swf";//复制文件
	private static String sdPath = null;
	private static String extSdPath = null;
	private static String updateDataPath = null;
	public final static String framePicName1 = "frame1.png";
	public final static String framePicName2 = "frame2.png";
	public final static String update_package = "com.custom.update";
	public final static String preface = "preface.txt";
	public final static String foldTilePic = "title.png";
	public final static String loadSound = "loading.mp3";
	
	public final static String backPicName1 = "back1.png";
	public final static String backPicName2 = "back2.png";
	
	public static boolean noPageNum = false;
	
	public static String root_fold = "custom";
	
	public static String inited_file_fold = "custom/inited";
	public static String inited_file_info_file = "inited_file_info.txt";
	
	public static String copy_file_fold = "custom/copy_file";
	public static String copy_file_info_file = "copy_file_info.txt";
	
	
	public final static  HashMap<String,String> picType= new HashMap<String,String>();;
	public final static  HashMap<String,String> swfType= new HashMap<String,String>();
	public enum BgType{
		pic,swf
	}
	public enum DirType{
		assets,file,sd,extSd
	}
	
	static{
		picType.put("JPG", "");
		picType.put("jpg", "");
		picType.put("GIF", "");
		picType.put("gif", "");
		picType.put("PNG", "");
		picType.put("png", "");
		picType.put("JEPG", "");
		picType.put("jepg", "");
		swfType.put("swf", "");
		swfType.put("SWF", "");
		try{
			noPageNum =  Boolean.parseBoolean(MainApplication.getInstance().getString(R.string.no_page_num));
		}catch(Exception e){
			
		}
		try{
			sdPath = MainApplication.getInstance().getString(R.string.D_ROOT);
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				extSdPath =  MainApplication.getInstance().getString(R.string.TF_ROOT);
				if(!(new File(extSdPath).exists()))
					extSdPath = null;
			}
			/**
			 * 使用本地目录
			Context friendContext = MainApplication.getInstance().createPackageContext(
					update_package,Context.CONTEXT_IGNORE_SECURITY);	
			updateDataPath =  friendContext.getFilesDir().getAbsolutePath();
			*/
			updateDataPath =  MainApplication.getInstance().getFilesDir().getAbsolutePath();
		}catch(Exception e){
			
		}
		String foldName = MainApplication.getInstance().getString(R.string.fold_name);
		Constant.foldName = foldName;
		foldName_ex =MainApplication.getInstance().getString(R.string.fold_name_ex);
		Constant.path = "custom/"+foldName;
		Constant.pageNumPicPath = "custom/"+foldName+"/pagepic";



		
		
		
	}
	public static String getSdPath(){
		return sdPath;
	}
	
	public static String getExtSdPath(){
		return extSdPath;
	}	
	
	public static String getUpdateDataPath(){
		return updateDataPath;
	}
}
