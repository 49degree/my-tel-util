package com.yangxp.config.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpFileDownload {
	private String mUrlString = null;
	private int mFilelength = 0;
	private String mFileName = null;
	private String mSavePath = null;
	public HttpFileDownload(String urlString,String savePath){
		mUrlString = urlString;
		mSavePath = savePath;
		mFileName = mUrlString.substring(mUrlString.lastIndexOf('/')+1);;//从路径中获取文件名称
	}
	
    /**
     * 从路径中获取文件名称
     * @param path 下载路径
     * @return
     */
    public String getFilename(){
        return mFileName;
    }
    
    /**
     * 文件保存路径
     * @return
     */
    public String getSavePath(){
        return mSavePath;
    }
    
	public int getFileSize(){
		if(mFilelength!=0)
			return mFilelength;
		try{
			URL url = new URL(mUrlString);
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setConnectTimeout(5 * 1000);
	        mFilelength = conn.getContentLength();//获取要下载的文件的长度
		}catch(Exception e){
			
		}
		return mFilelength;
	}
	
	public boolean download(){
		boolean reslult = false;
		InputStream inStream = null;
		RandomAccessFile accessFile = null;
		 try {
			 File filedir = new File(mSavePath);
			 if(!filedir.exists()||!filedir.isDirectory())
				 filedir.mkdirs();
			 
             accessFile = new RandomAccessFile(mSavePath+File.separator+mFileName, "rwd");
             accessFile.seek(0);//设置从什么位置开始写入数据
             URL url = new URL(mUrlString);
             HttpURLConnection conn = (HttpURLConnection)url.openConnection();
             conn.setRequestMethod("GET");
             conn.setConnectTimeout(5 * 1000);
             //conn.setRequestProperty("Range", "bytes="+ 0+ "-"+ mFilelength);
             inStream = conn.getInputStream();
             byte[] buffer = new byte[1024];
             int len = 0;
             while( (len=inStream.read(buffer)) != -1 ){
                 accessFile.write(buffer, 0, len);
             }
             reslult = true;
         } catch (Exception e) {
             e.printStackTrace();
         }finally{
        	 if(inStream!=null)
        		 try{
        			 inStream.close();
        		 }catch(Exception e){
        			 
        		 }
        	 if(accessFile!=null)
        		 try{
        			 accessFile.close();
        		 }catch(Exception e){
        			 
        		 }
         }
		 return reslult;
	}
	
}
