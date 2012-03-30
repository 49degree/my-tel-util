package com.custom.update;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.custom.network.HttpRequest;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;


public class CustomUtils {
	private static final Logger logger = Logger.getLogger(CustomUtils.class);
	String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "customDemo"+File.separator;
	Context context = null;
	public CustomUtils(Context context){
		this.context = context;
	}
	boolean stop = false;
	public void stop(){
		stop = true;
	}
    
    public JSONObject queryInfo(){
		try {
			LoadResources.initInstalledInfo();// 获取已经下载的文件列表
			Iterator it = LoadResources.installedInfo.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				JSONObject installed = LoadResources.installedInfo.get(key);
				try{
					if (installed!=null&&installed.getString(Constant.fileUnziped) != null)// 有文件为解压
						continue;
				}catch(Exception e){
					return installed;
				}

			}

			HashMap<String, String> params = new HashMap<String, String>();
			HttpRequest httpRequest = new HttpRequest(Constant.QUERY_URL,
					params, context);
			JSONObject retJson = httpRequest.getResponsJSON(false);
			logger.error("===================" 	+ httpRequest.getResponsString(false));
			// 解析数据
			try{
				if(!retJson.getBoolean("success")){
					return retJson;
				}
			}catch(Exception e){
				
			}

			JSONArray list = retJson.getJSONArray("updates");
			for (int i = 0; i < list.length(); i++) {
				JSONObject installed = list.getJSONObject(i);
				if (!LoadResources.installedInfo.containsKey(installed
						.getString("updateId")))
					return installed;
			}
			
		} catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
    
	/**
	 * 文件下载
	 * 文件优先放在SD卡上
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
		Constant.FileDirType fileDirType = null;
		//查询是否已经存在文件
		if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){
			sdfile = new File( Constant.getSdPath()+File.separator+Constant.path+File.separator+fileName);
			if(fileExsit = sdfile.exists()){
				filePath = Constant.getSdPath()+File.separator+Constant.path+File.separator+fileName;
				fileDirType = Constant.FileDirType.sd;
			}
		}
		if(!fileExsit){
			sdfile = new File( Constant.getDataPath()+File.separator+Constant.path+File.separator+fileName);
			if(fileExsit = sdfile.exists()){
				filePath = Constant.getDataPath()+File.separator+Constant.path+File.separator+fileName;
				fileDirType = Constant.FileDirType.data;
			}
		}
		if(fileExsit)
			dowonedLength = sdfile.length();
		int readLength = 0;
		int length = 0;
		byte[] buffer = new byte[1024];
		try {
			URL url = new URL(Constant.INSTALLED_URL+fileName);  
			logger.error("连接:"+url.toString());
			HttpURLConnection conn =(HttpURLConnection) url.openConnection();   
			conn.setDoInput(true);
			if(fileExsit){
				// 设置 User-Agent 
				//conn.setRequestProperty("User-Agent","NetFox"); 
				// 设置断点续传的开始位置 
				conn.setRequestProperty("RANGE","bytes="+dowonedLength+"-"); 
			}
			conn.connect();
			long setAside = 500*1024*1024;//内存要预留50M空间
			logger.error("连接返回:"+conn.getResponseCode()+":dowonedLength:"+dowonedLength);
			if( conn.getResponseCode() == HttpURLConnection.HTTP_OK||conn.getResponseCode()==206){
				length = conn.getContentLength();
				InputStream in = conn.getInputStream(); 
				logger.error("length:"+length);
				
				if(fileExsit){
					try{
						oSavedFile = new RandomAccessFile(filePath,"rw");
						oSavedFile.setLength(length+(fileDirType==Constant.FileDirType.data?setAside:0));
						oSavedFile.setLength(dowonedLength);
					}catch(IOException e1){
						//如果存储空间不够
						if(fileDirType==Constant.FileDirType.data){
							filePath = Constant.getSdPath()+File.separator+Constant.path+File.separator+fileName;
							fileDirType = Constant.FileDirType.sd;

						}else if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){
							filePath = Constant.getDataPath()+File.separator+Constant.path+File.separator+fileName;
							fileDirType = Constant.FileDirType.data;
						}
						RandomAccessFile oldSavefile = oSavedFile;
						oSavedFile = new RandomAccessFile(filePath,"rw");
						try{
							oSavedFile.setLength(length+(fileDirType==Constant.FileDirType.data?setAside:0));
							oSavedFile.setLength(0);
							//复制原有数据
							while(!stop&&(readLength=oldSavefile.read(buffer))>0){
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
						if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){
							filePath = Constant.getSdPath()+File.separator+Constant.path+File.separator+fileName;
							fileDirType = Constant.FileDirType.sd;
						}else{
							filePath = Constant.getDataPath()+File.separator+Constant.path+File.separator+fileName;
							fileDirType = Constant.FileDirType.data;
						}
						oSavedFile = new RandomAccessFile(filePath,"rw");
						oSavedFile.setLength(length+setAside);
						oSavedFile.setLength(0);
					}catch(IOException e1){
						//如果存储空间不够
						if(fileDirType == Constant.FileDirType.sd){
							filePath = Constant.getDataPath()+File.separator+Constant.path+File.separator+fileName;
							fileDirType = Constant.FileDirType.data;
							try{
								oSavedFile = new RandomAccessFile(filePath,"rw");
								oSavedFile.setLength(length);
								oSavedFile.setLength(0);
							}catch(IOException e4){
								//没有存储空间了
								handler.sendMessage(handler.obtainMessage(1));//没有存储空间了
								return;
							}finally{
								try{
									oSavedFile.close();//删除文件
								}catch(IOException e5){}
							}
						}else{
							//没有存储空间了
							handler.sendMessage(handler.obtainMessage(1));//没有存储空间了
							return;
						}
					}
				}
				try{
					logger.error("filePath:"+filePath);
					if(oSavedFile!=null)
						oSavedFile.seek(dowonedLength);
					while(!stop&&(readLength=in.read(buffer))>0){
						oSavedFile.write(buffer,0,readLength);
						dowonedLength +=readLength;
						logger.error("dowonedLength +=readLength:"+dowonedLength);
						handler.sendMessage(handler.obtainMessage(2, (int)dowonedLength, length));//报告进度
					}
					if(stop)
						return ;
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
				installed.put(Constant.fileDirType.toString(), fileDirType);
				installed.put(Constant.filePath, filePath);
				LoadResources.updateInstalledInfo(installed);//保存已经下载完成
				//通知下载完成
				handler.sendMessage(handler.obtainMessage(3,filePath));//通知下载完成
				
			}else if(conn.getResponseCode() == 416&&fileExsit){//已经完成下载
				installed.put(Constant.fileDirType.toString(), fileDirType);
				installed.put(Constant.filePath, filePath);
				LoadResources.updateInstalledInfo(installed);//保存已经下载完成
				handler.sendMessage(handler.obtainMessage(3,installed));//通知下载完成
			}else{//连接失败,发送通知
				handler.sendMessage(handler.obtainMessage(4));//连接失败
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	
}
