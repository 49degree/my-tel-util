package com.custom.update;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.custom.network.HttpRequest;
import com.custom.utils.LoadResources;


public class CustomUtils {
	final static String TAG = "CustomUtils";
	String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "customDemo"+File.separator;
	Context context = null;
	public CustomUtils(Context context){
		this.context = context;
	}
    
    public JSONObject queryInfo(){
        HashMap<String,String> params = new HashMap<String,String>();
        HttpRequest httpRequest = new HttpRequest(Constant.QUERY_URL,params,context);
        JSONObject retJson = httpRequest.getResponsJSON(false);
        Log.i(TAG, "==================="+httpRequest.getResponsString(false));
        try {  
        	//解析数据
        	LoadResources.initInstalledInfo();
			JSONArray list = retJson.getJSONArray("updates");
			for(int i=0;i<list.length();i++){
				JSONObject installed = list.getJSONObject(i);
				
				if(!LoadResources.installedInfo.containsKey(installed.getString("updateId")))
					return installed;
			}
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
    
	/**
	 * 文件下载
	 * @param url
	 * @param fileName
	 */
	public void downFile(JSONObject installed ,Handler handler) throws Exception{
		String fileName = installed.getString("fileName");
		File sdfile = null;
		boolean fileExsit = false;
		long dowonedLength = 0;
		RandomAccessFile oSavedFile = null;
		String filePath = null;
		String fileDirType = "";
		//查询是否已经存在文件
		if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){
			sdfile = new File( Constant.getSdPath()+File.separator+Constant.foldName+File.separator+fileName);
			if(fileExsit = sdfile.exists()){
				filePath = Constant.getSdPath()+File.separator+Constant.foldName+File.separator+fileName;
				fileDirType = "sd";
			}
		}
		if(!fileExsit){
			sdfile = new File( Constant.getDataPath()+File.separator+Constant.foldName+File.separator+fileName);
			if(fileExsit = sdfile.exists()){
				filePath = Constant.getDataPath()+File.separator+Constant.foldName+File.separator+fileName;
				fileDirType = "data";
			}
		}
		if(fileExsit)
			dowonedLength = sdfile.length();
		int readLength = 0;
		int length = 0;
		byte[] buffer = new byte[1024];
		try {
			URL url = new URL(Constant.INSTALLED_URL+"/"+fileName);   
			HttpURLConnection conn =(HttpURLConnection) url.openConnection();   
			conn.setDoInput(true);
			if(fileExsit){
				// 设置 User-Agent 
				conn.setRequestProperty("User-Agent","NetFox"); 
				// 设置断点续传的开始位置 
				conn.setRequestProperty("RANGE","bytes="+dowonedLength+"-"); 
			}
			conn.connect();
			long setAside = 500*1024*1024;//内存要预留50M空间
			if( conn.getResponseCode() == HttpURLConnection.HTTP_OK||conn.getResponseCode()==206){
				length = conn.getContentLength();
				InputStream in = conn.getInputStream(); 
				
				if(fileExsit){
					try{
						oSavedFile = new RandomAccessFile(fileName,"rw");
						oSavedFile.setLength(length+(fileDirType.equals("data")?setAside:0));
						oSavedFile.setLength(dowonedLength);
					}catch(IOException e1){
						//如果存储空间不够
						if(fileDirType.equals("data")){
							filePath = Constant.getSdPath()+File.separator+Constant.foldName+File.separator+fileName;
							fileDirType = "sd";

						}else if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){
							filePath = Constant.getDataPath()+File.separator+Constant.foldName+File.separator+fileName;
							fileDirType = "data";
						}
						RandomAccessFile oldSavefile = oSavedFile;
						oSavedFile = new RandomAccessFile(fileName,"rw");
						try{
							oSavedFile.setLength(length+(fileDirType.equals("data")?setAside:0));
							oSavedFile.setLength(0);
							//复制原有数据
							while((readLength=oldSavefile.read(buffer))>0){
								oSavedFile.write(buffer,0,readLength);
							}
							
						}catch(IOException e2){
							//没有存储空间了
							handler.sendMessage(handler.obtainMessage(1));//没有存储空间了
							return;
						}finally{
							try{
								oldSavefile.close();
								sdfile.delete();//删除文件
							}catch(IOException e3){
								
							}
						}
					}
				}else{
					try{
						filePath = Constant.getSdPath()+File.separator+Constant.foldName+File.separator+fileName;
						fileDirType = "sd";
						oSavedFile = new RandomAccessFile(fileName,"rw");
						oSavedFile.setLength(length+setAside);
						oSavedFile.setLength(0);
					}catch(IOException e1){
						//如果存储空间不够
						if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){
							filePath = Constant.getDataPath()+File.separator+Constant.foldName+File.separator+fileName;
							fileDirType = "data";
						}else{
							//没有存储空间了
							handler.sendMessage(handler.obtainMessage(1));//没有存储空间了
							return;
						}
					}
				}
				
				oSavedFile = new RandomAccessFile(fileName,"rw");
				try{
					oSavedFile.setLength(length+(fileDirType.equals("data")?setAside:0));
					oSavedFile.setLength(0);
					while((readLength=in.read(buffer))>0){
						oSavedFile.write(buffer,0,readLength);
						dowonedLength +=readLength;
						handler.sendMessage(handler.obtainMessage(2, (int)dowonedLength, length));//报告进度
					}
				}catch(IOException e2){
					//没有存储空间了
					handler.sendMessage(handler.obtainMessage(1));
					return ;
				}finally{
					try{
						oSavedFile.close();
					}catch(IOException e3){
						
					}
					try{
						in.close();
					}catch(IOException e3){
						
					}
				}
				installed.put("fileDirType", fileDirType);
				LoadResources.updateInstalledInfo(installed);//保存已经下载完成
				//通知下载完成
				handler.sendMessage(handler.obtainMessage(3));//通知下载完成
				
			}else{//连接失败,发送通知
				handler.sendMessage(handler.obtainMessage(4));//连接失败
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	
}
