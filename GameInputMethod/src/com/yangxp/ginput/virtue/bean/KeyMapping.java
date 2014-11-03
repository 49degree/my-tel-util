package com.yangxp.ginput.virtue.bean;

import java.util.HashMap;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.util.Log;

public class KeyMapping {
	public final static int KEY_MAP_TYPE_JOYSTICK = 0;//摇杆左右上下移动（围绕中心点在一定范围移动）
	public final static int KEY_MAP_TYPE_CLICK = 1;//点击
	public final static int KEY_MAP_TYPE_MOVE_TRACE = 2;//移动（轨迹数据待定义）
	public final static int KEY_MAP_TYPE_MOVE_MOUSE = 3;//模拟鼠标及点击
	public final static int KEY_MAP_TYPE_SENSOR = 4;//前后视角切换
	public final static int KEY_MAP_TYPE_MOVE_AROUND = 5;//晃动（围绕中心点在一定范围晃动）
	
	

	
	public KeyMapping(){
	}
	

	
	public int id;
	public int pageId;
	public int toPage;
	public int type;
	public int key;
	public int keyDrag;
	public int keyClick;
	public int x;
	public int y;
	public int radius;
	public byte[] record;
	
	

	public KeyMapping(String value) throws Exception{
		if(value==null||"".equals(value.trim()))
			throw new Exception("value is null");
		
		
		StringTokenizer strToke=new StringTokenizer(value,"|");//默认不打印分隔符
		String[] params = new String[11];
		int i = 0;
		while(strToke.hasMoreElements())
	    {
			params[i++] = strToke.nextToken();
			Log.e(this.getClass().toString(),params[i-1]);
	     }

		id = string2Int(params[0]);
		pageId = string2Int(params[1]);
		toPage = string2Int(params[2]);
		type = string2Int(params[3]);
		key = string2Int(params[4]);
		keyDrag = string2Int(params[5]);
		keyClick = string2Int(params[6]);
		x = string2Int(params[7]);
		y = string2Int(params[8]);
		radius = string2Int(params[9]);
		//record = params[10];
	}
	
	private int string2Int(String value){
		try{
			return Integer.parseInt(value);
		}catch(Exception e){
			return 0;
		}
	}
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		str.append("id:").append(id).append(";");
		str.append("pageId:").append(pageId).append(";");
		str.append("toPage:").append(toPage).append(";");
		str.append("type:").append(type).append(";");
		str.append("key:").append(key).append(";");
		str.append("keyDrag:").append(keyDrag).append(";");
		str.append("keyClick:").append(keyClick).append(";");
		str.append("x:").append(x).append(";");
		str.append("y:").append(y).append(";");
		str.append("radius:").append(radius).append(";");
		return str.toString();
	}
	
	
}
