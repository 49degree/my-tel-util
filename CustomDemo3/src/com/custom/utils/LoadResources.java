package com.custom.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.custom.update.Constant;

public class LoadResources {
	private static final Logger logger = Logger.getLogger(LoadResources.class);
	static boolean secrete = true;

	private static HashMap<String,JSONObject> installedInfo = new HashMap<String,JSONObject>();
	private static JSONObject json = null;
	public static void initInstalledInfo(){
		try{
			String filePath = Constant.getDataPath()+File.separator+Constant.installedInfo;
			byte[] buf = LoadResources.loadFile(filePath);
			if(buf==null){
				return ;
			}
			String info = new String(buf,"GBK");
			json = new JSONObject(info);
			JSONArray list = json.getJSONArray("updates");
			for(int i=0;i<list.length();i++){
				JSONObject installed = list.getJSONObject(i);
				installedInfo.put(installed.getString("updateId"), installed);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void updateInstalledInfo(JSONObject installed){
		String filePath = Constant.getDataPath()+File.separator+Constant.installedInfo;
		try{
			json.getJSONArray("updates").put(installed);
			installedInfo.put(installed.getString("updateId"), installed);
			
			writeFile(filePath,json.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	/**
	 * 根据路径和路径类型读取文件
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 */
	public static byte[] loadFile(String filePath){
		InputStream in= null;
		try{
			in= new FileInputStream(filePath);
			byte[] buf = new byte[in.available()];
			in.read(buf,0,buf.length);
			return buf;
		}catch(Exception e){
			return null;
		}finally{
			if(in!=null){
				try{
					in.close();
				}catch(Exception e){
					
				}
				
			}
		}
	}	
	
	/**
	 * 根据路径和路径类型读取文件
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 */
	public static void writeFile(String filePath,String value){
		FileOutputStream in= null;
		try{
			in= new FileOutputStream(filePath);
			byte[] buff = value.getBytes("GBK");
			in.write(buff);
			in.flush();
		}catch(Exception e){
		}finally{
			if(in!=null){
				try{
					in.close();
				}catch(Exception e){
					
				}
				
			}
		}
	}
}
