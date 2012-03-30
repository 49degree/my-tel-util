package com.custom.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.custom.update.Constant;
import com.custom.update.Update;

public class LoadResources {
	private static final Logger logger = Logger.getLogger(LoadResources.class);
	static boolean secrete = true;

	public static HashMap<String,JSONObject> installedInfo = new HashMap<String,JSONObject>();
	private static JSONObject json = null;
	public static void initInstalledInfo(){
		try{
			String filePath = Constant.getDataPath()+File.separator+Constant.installedInfo;
			byte[] buf = LoadResources.loadFile(filePath);
			if(buf==null){
				return ;
			}
			String info = new String(buf,"GBK");
			logger.error(info);
			json = new JSONObject(info);
			JSONArray list = null;
			try{
				list = json.getJSONArray(Constant.root);
			}catch(Exception e){
				list = new JSONArray();
				json.put(Constant.root, list);
				
			}
			
			for(int i=0;i<list.length();i++){
				JSONObject installed = list.getJSONObject(i);
				installedInfo.put(installed.getString(Constant.updateId), installed);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void updateInstalledInfo(JSONObject installed){
		String filePath = Constant.getDataPath()+File.separator+Constant.installedInfo;
		try{
			if(json!=null){
				json.getJSONArray(Constant.root).put(installed);
			}else{
				json = new JSONObject();
				json.put(Constant.root, new JSONArray());
				logger.error("new json:"+json.toString());
			}
			
			if(installedInfo.containsKey(installed.getString(Constant.updateId))){
				JSONArray list = json.getJSONArray(Constant.root);
				for(int i=0;i<list.length();i++){//如果已经存在，则替换
					JSONObject temp = list.getJSONObject(i);
					if(temp.getString(Constant.updateId).equals(Constant.updateId)){
						list.put(i, installedInfo);
						break;
					}
				}
			}
			installedInfo.put(installed.getString(Constant.updateId), installed);
			
			logger.error("update json:"+json.toString());
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
	 * 查询已经下载了多少业务
	 */
	public static HashMap<String,String> queryDownedFold(Context context){
		//读取数据
		FileInputStream in = null;
		HashMap<String,String> folds = new HashMap<String,String>();
		try{
			in = context.openFileInput("DataFoldCount.txt");
			BufferedReader fin = new BufferedReader(new InputStreamReader(in));
			String line = fin.readLine();
			int count = 0;
			while(line!=null){
				line = line.substring(line.indexOf('=')+1);
				if(line.indexOf("=")>0){
					folds.put(line.substring(0,line.indexOf("=")), line.substring(line.indexOf("=")+1).trim());
				}
				line = fin.readLine();
			}
		}catch(Exception e){
			
		}finally{
			try{
				if(in!=null)
					in.close();
			}catch(Exception e){
			}
		}
		FileOutputStream out = null;
        try{
    		out = context.openFileOutput("DataFoldCount.txt",Context.MODE_WORLD_READABLE);
			//查询
			Iterator it = folds.keySet().iterator();
			while(it.hasNext()){
				String name = (String)it.next();
				String value = folds.get(name);
	    		out.write(("="+name+"="+value+"\n").getBytes("GBK"));
	    		out.flush();
			}
        }catch(Exception e){
        	
        }finally{
        	try{
        		if(out!=null)
        			out.close();
            }catch(Exception e){
            	
            }
        	
        }
		
		
		return folds;
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
