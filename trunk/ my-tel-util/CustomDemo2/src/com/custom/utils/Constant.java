package com.custom.utils;

import java.util.HashMap;

public class Constant {
	public final static String foldPath = "foldPath";
	public final static String path = "custom/yuwen";
	public final static String bgPicName = "bg";
	public final static String imageIndexFileName="IndexImageIndex.txt";
	public final static String mapFileName="map.txt";
	public final static int fistFoldDepth=0;
	public final static String foldDepth="foldDepth";
	public final static String swfView = "file:///android_asset/swf_view.htm";
	public final static String viewClass="viewClass";
	public final static String secondViewClass="com.custom.view.SecondView";
	public final static String resourceFoldEnd="_raw";
	public final static String resourceFold="raw";
	
	public final static  HashMap<String,String> picType= new HashMap<String,String>();;
	public final static  HashMap<String,String> swfType= new HashMap<String,String>();
	public enum BgType{
		pic,swf
	}
	public enum DirType{
		assets,file,sd
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
	}
	
}
