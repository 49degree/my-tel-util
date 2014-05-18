package com.skyeyes.base.util;

import java.util.HashMap;


public class VideoClarity {
	static HashMap<Integer,VideoClarity> videoClarityMap = new HashMap<Integer,VideoClarity>();
	//类型编号
	private int id;
	//引导文件
	private String bootFile;
	//画面宽
	private int with;
	//画面高
	private int height;
	private static VideoClarity obj;
	
	public String getBootFile() {
		return bootFile;
	}
	public int getWith() {
		return with;
	}
	public int getHeight() {
		return height;
	}
	public int getId() {
		return id;
	}
	
	
	private  VideoClarity(){}
	
	/**
	 * 获取设置的视频清晰类型
	 * @return
	 */
	
	private static int getVideoClarityId(){
		return -1;//未实现，先固定
	}
	
	public static VideoClarity instance()
	{
		int videoClarityId = getVideoClarityId();
		
		if(videoClarityMap.containsKey(videoClarityId))
			return videoClarityMap.get(videoClarityId);
		obj=new VideoClarity();
		switch(videoClarityId)
		{
			case 0:
				obj.id=0;
				obj.bootFile="head176144.264";
				obj.with=176;
				obj.height=144;
				break;
			
			case 2:
				obj.id=2;
				obj.bootFile="head704576.264";
				obj.with=704;
				obj.height=576;
				break;
				
			default:
				obj.id=1;
				obj.bootFile="head.264";
				obj.with=352;
				obj.height=288;
		}
		return obj;
	}

}
