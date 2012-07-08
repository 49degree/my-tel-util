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


import com.custom.network.HttpRequest;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;


public class CustomUtils {
	private static final Logger logger = Logger.getLogger(CustomUtils.class);
	String path = Constant.getSdPath()+ File.separator + "customDemo"+File.separator;

	boolean stop = false;
	public void stop(){
		stop = true;
	}
    
    public JSONObject queryInfo(){
    	logger.error("queryInfo");
		try {
			LoadResources.loadUpdateInstalledInfo();// 获取已经下载的文件列表
			HashMap<String, String> params = new HashMap<String, String>();
			HttpRequest httpRequest = new HttpRequest(Constant.QUERY_URL,
					params);
			JSONObject retJson = httpRequest.getResponsJSON(false);
			logger.error("查询返回："+httpRequest.getResponsString(false));
			// 解析数据
			try{
				if(!retJson.getBoolean("success")){
					return retJson;
				}
			}catch(Exception e){}

			JSONArray list = retJson.getJSONArray("updates");
			for (int i = 0; i < list.length(); i++) {
				try{
					JSONObject installed = list.getJSONObject(i);
					logger.error(installed.getString(Constant.updateId)+":"
							+LoadResources.updateInstalledInfo.containsKey(installed.getString(Constant.updateId)));
					if (!LoadResources.updateInstalledInfo.containsKey(installed.getString(Constant.updateId))){
						LoadResources.updateInstalledInfo(installed);
					}
						//return installed;
				}catch(Exception e){
					e.printStackTrace();
				}
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
		logger.error("downFile");
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
			sdfile = new File( Constant.getExtSdPath()+File.separator+Constant.path+File.separator+fileName);
			if(fileExsit = sdfile.exists()){
				filePath = Constant.getExtSdPath()+File.separator+Constant.path+File.separator+fileName;
				fileDirType = Constant.FileDirType.extSd;
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
			//long setAside = 500*1024*1024;//内存要预留50M空间
			logger.error("连接返回:"+conn.getResponseCode()+":dowonedLength:"+dowonedLength);
			if( conn.getResponseCode() == HttpURLConnection.HTTP_OK||conn.getResponseCode()==206){
				length = conn.getContentLength();
				InputStream in = conn.getInputStream(); 
				logger.error("length:"+length);
				long[] sdRealease= LoadResources.readSDCard();
				long[] fileRealease = LoadResources.readExtSDCard();
				if(fileExsit){
					boolean change = false;
					oSavedFile = new RandomAccessFile(filePath,"rw");
					if(fileDirType==Constant.FileDirType.extSd&&fileRealease[1]<length){
						change = true;
						if(sdRealease[1]<length){
							handler.sendMessage(handler.obtainMessage(1));//没有存储空间了
						    return ;
						}
						filePath = Constant.getSdPath()+File.separator+Constant.path+File.separator+fileName;
						fileDirType = Constant.FileDirType.sd;
					}else if(fileDirType==Constant.FileDirType.sd&&sdRealease[1]<length){
						change = true;
						if(fileRealease[1]<length){
							handler.sendMessage(handler.obtainMessage(1));//没有存储空间了
						    return ;
						}
						filePath = Constant.getExtSdPath()+File.separator+Constant.path+File.separator+fileName;
						fileDirType = Constant.FileDirType.extSd;
					}
					if(change){
						RandomAccessFile oldSavefile  = oSavedFile;
						oSavedFile = new RandomAccessFile(filePath,"rw");
						try{
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
							}catch(IOException e3){}
						}
					}
				}else{
					try{
						if(sdRealease[1]>length){
							filePath = Constant.getSdPath()+File.separator+Constant.path+File.separator+fileName;
							fileDirType = Constant.FileDirType.sd;
						}else if(fileRealease[1]>length){
							filePath = Constant.getExtSdPath()+File.separator+Constant.path+File.separator+fileName;
							fileDirType = Constant.FileDirType.extSd;
						}else{
							handler.sendMessage(handler.obtainMessage(1));//没有存储空间了
							return ;
						}
						oSavedFile = new RandomAccessFile(filePath,"rw");
					}catch(IOException e1){}
				}
					
				try{
					logger.error("filePath:"+filePath);
					if(oSavedFile!=null)
						oSavedFile.seek(dowonedLength);
					long allLength = dowonedLength+length;
					FileInfo fileInfo = new FileInfo();
					fileInfo.allLength = allLength;
					while(!stop&&(readLength=in.read(buffer))>0){
						oSavedFile.write(buffer,0,readLength);
						dowonedLength +=readLength;
						fileInfo.downLength = dowonedLength;
						handler.sendMessage(handler.obtainMessage(2, fileInfo));//报告进度
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
				handler.sendMessage(handler.obtainMessage(3,installed));//通知下载完成
				
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
	
	public static class FileInfo{
		public long downLength = 0;
		public long allLength = 0;
	}
}
