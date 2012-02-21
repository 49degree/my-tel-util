package com.custom.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;



public class UpdateUtil { 
	// 初始化参数信息
	public final static String httpCheckUpdateLink = "http://www.a3650.com/pos_update/TB_FSK_POS/check_update.htm";
	public final static String httpUpdateLink = "http://www.a3650.com/pos_update/TB_FSK_POS/";
	
	
	/**
	 * 判断是否有需要升级
	 * @param activity
	 */
	public static Bundle checkUpdate(Context context,String saveFilePath){
		try{
			HttpRequest httpRequest = new HttpRequest(httpCheckUpdateLink, null,context);
			Log.d("UPdate",httpRequest.getResponsString(true));
			JSONObject jo = httpRequest.getResponsJSON(true);
			
			Log.d("UPdate",jo.getString("success")+"++++++++++++++");	
			if(jo.getString("success").equals("true")){
				String newVersion = jo.getString("version");
				Log.d("UPdate",newVersion+"++++++++++++++");	
				//下载升级包
				String fileName = jo.getString("fileName");
				String updateMsg = "";
				if(!jo.isNull("updateMsg")){
					updateMsg = jo.getString("updateMsg");
				}
				Bundle bundle = new Bundle();
				bundle.putString("updateMsg", updateMsg);
				bundle.putString("fileName", fileName/*.substring(0,fileName.indexOf("."))+".apk"*/);
				bundle.putString("filePath", new StringBuffer(httpUpdateLink).append(fileName).toString());
				bundle.putString("saveFilePath",saveFilePath);
				bundle.putString("version", jo.getString("version"));
				bundle.putString("updateTime", jo.getString("updateTime"));
				UpdateUtil.downFile(context, bundle);//下载文件
				return bundle;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 文件下载
	 * @param url
	 * @param fileName
	 */
	private static void downFile(final Context context,final Bundle bundle) throws Exception{

		Log.e("UPdate",bundle.getString("filePath")+":"+bundle.getString("fileName"));
		//判断文件是否存在
		File file = new File(bundle.getString("saveFilePath"), bundle.getString("fileName"));//如果存在，则退出下载
		if(file.exists())
			file.delete();
		
		try {
			URL url = new URL(bundle.getString("filePath"));   
			HttpURLConnection conn =(HttpURLConnection) url.openConnection();   
			conn.setDoInput(true);   
			conn.connect();   
			Log.d("UPdate conn.getResponseCode()",conn.getResponseCode()+":");
			if( conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				InputStream is = conn.getInputStream(); 
				FileOutputStream fileOutputStream = null;
				if (is != null) {
					file = new File(bundle.getString("saveFilePath"), bundle.getString("fileName"));
					file.createNewFile();
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					int count = 0;
					while ((ch = is.read(buf)) != -1) {
						Log.d("update count",count+"");
						fileOutputStream.write(buf, 0, ch);
						count += ch;
					}
				}
				if (fileOutputStream != null) {
					fileOutputStream.flush();
					fileOutputStream.close();
				}
				if(is!=null){
					is.close();
				}

			}else{//下载失败,发送通知
				throw new Exception("down file failed");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}	
	
	
}
