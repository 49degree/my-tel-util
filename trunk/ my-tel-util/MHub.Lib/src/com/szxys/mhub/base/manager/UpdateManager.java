package com.szxys.mhub.base.manager;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.szxys.mhub.base.communication.webservice.WebUtils;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.interfaces.IPlatFormInterface;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 平台自动升级管理类
 * @author sujinyi
 * */

public class UpdateManager implements ISubSystemCallBack{

	/**
	 * 平台自动升级管理类单例对象
	 * */
	private static UpdateManager updataManager;
	
	/**
	 * 上下文对象，用于执行apk升级
	 * */
	private Context context;
	
	/**
	 * IPlatFormInterface接口实现类，用于执行平台交互功能
	 * */
	private IPlatFormInterface platFormImpl;
	
	/**
	 * 本地设备版本信息列表
	 * */
	@SuppressWarnings("unused")
	private ArrayList<Object> localVersionList=new ArrayList<Object>();
	
	/**
	 * web端最新设备版本信息列表
	 * */
	private ArrayList<Object> newVersionList=new ArrayList<Object>();
	
	/**
	 * 需要升级的设备信息列表
	 * */
	private ArrayList<Object> updateList=new ArrayList<Object>();
	
	private String updateFilePath;
	/**
	 * 私有构造方法
	 * */
	private UpdateManager(IPlatFormInterface platFormImpl,Context context){
		this.platFormImpl=platFormImpl;
		this.context=context;
		this.updateFilePath="\\sdcard\\mhub\\updata\\";
	}
	
	/**
	 * 静态方法，用于对外提供升级管理类对象
	 * */
	public static UpdateManager getUpdateManagerInstance(IPlatFormInterface platFormImpl,Context context){
		if(updataManager==null){
			updataManager=new UpdateManager(platFormImpl,context);
		}
		
		return updataManager;
	}
	
	/**
	 * 执行升级操作
	 * */
	public void doUpdate(){
		
		updateApk();
		this.checkVersion();
		
	}
	
	/**
	 * 对应用程序进行升级
	 */
	private void updateApk(){
		File files[]=checkLocalFile(updateFilePath);
		for(int i=0;i<files.length;i++){
			if(isApk(files[i])){
				installApk(files[i]);
			}
		}
	}
	
	/**
	 * 根据本地和web端版本信息，判断出需要升级的设备
	 * */
	
	private void checkVersion(){
		//reqIdentfying
		@SuppressWarnings("unused")
		byte []local=packaging(getLocalList());
		//this.platFormImpl.send(reqIdentifying, sendIndex, channel, mainCmd, subCmd, this.packaging(this.getLocalList()), length)
	}
	
	/**
	 * 获取本地设备版本列表
	 * @param dir :本地升级数据包存放目录
	 * @return File[] :文件数组
	 * */
	private ArrayList<Object> getLocalList(){
		ArrayList<Object> localList=new ArrayList<Object>();
		ConfigManager configManager=ConfigManager.getInstance();
		configManager.read("version");
		
		return localList;
	}
	
	/**
	 * 获取web端最新设备版本列表
	 * @param dir :本地升级数据包存放目录
	 * @return File[] :文件数组
	 * */
	private ArrayList<Object> getNewVersionList(byte []webList){
		int i=0;
		
		ArrayList<Object> newVersionList=new ArrayList<Object>();
		while(i<webList.length){
			ArrayList<Object> temp=new ArrayList<Object>();
			try{
				//设备类型
				byte device=webList[i++];
				
				//新版本号
				byte newVersionCodeLength=webList[i++];
				byte []newVersionCodeText=new byte[newVersionCodeLength];
				System.arraycopy(webList, i, newVersionCodeText, 0, newVersionCodeLength);
				i=i+newVersionCodeLength;
				String newVersionCode=new String(newVersionCodeText);
				
				//下载URL
				byte URLLength=webList[i++];
				byte []URLText=new byte[URLLength];
				System.arraycopy(webList, i, URLText, 0, URLLength);
				i+=URLLength;
				String URL=new String(URLText);
				
				//数据包大小
				byte dataSize[]=new byte[4];
				System.arraycopy(webList, i, dataSize, 0, 4);
				i+=4;
				int size=ByteBuffer.allocate(4).put(dataSize).getInt(0);
				
				//升级标志 
				byte updateFlag=webList[i];
				
				//发布时间
				byte []versionTimeBytes=new byte[8];
				ByteBuffer bb_versionTime=ByteBuffer.allocate(8);
				bb_versionTime.put(versionTimeBytes);
				i+=8;
				long versionTime=bb_versionTime.getLong(0);
				
				//MD5
				byte MD5Length=webList[i++];
				byte []MD5Text=new byte[MD5Length];
				System.arraycopy(webList,i,MD5Text,0,MD5Length);
				i+=MD5Length;
				String MD5String=new String(MD5Text);
				
				temp.add(device);
				temp.add(newVersionCode);
				temp.add(URL);
				temp.add(size);
				temp.add(updateFlag);
				temp.add(versionTime);
				temp.add(MD5String);
				
				newVersionList.add(temp);
			}
			catch(Exception e){
				e.printStackTrace();
				break;
			}
		}
		
		return newVersionList;
	}
	
	/**
	 * 下载升级文件
	 * */
	private void download(String url,String savepath,String username,String password){
		platFormImpl.download(url, savepath, username, password);
	}
	
	/**
	 * 检测本地升级数据包
	 * @param dir :本地升级数据包存放目录
	 * @return File[] :文件数组
	 * */
	private File[] checkLocalFile(String dir){
		File[] update;
		File localDir=new File(dir);
		
		update=localDir.listFiles();
	
		return update;
	}
	
	/**
	 * 升级应用
	 * @param file :升级包 apk文件
	 * */
	private void installApk(File file)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(file), type);
		context.startActivity(intent);

	}
	
	/**
	 * 判断文件是否为apk文件
	 * @param file :需判断的文件
	 * @return boolean: 是apk 则为true  ，反之为false
	 * */
	public boolean isApk(File file){
    	boolean flag=false;
    	
    	String fileName=file.getName();
    	String end=fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()).toLowerCase();
    	if("apk".equals(end)){
    		flag=true;
    	}
    	return flag;
    }

	public long onReceived(int mainCmd, int subCmd, byte[] data, int length){
		if(mainCmd==200)
		{
			newVersionList=getNewVersionList(WebUtils.bytesReverseOrder(data));
			for(int i=0;i<newVersionList.size();i++)
			{
				@SuppressWarnings("unchecked")
				ArrayList<Object> temp=(ArrayList<Object>) newVersionList.get(i);
				download((String)temp.get(2),"\\sdcard\\mhub\\updata\\","","");
			}
		}
		return 0;
	}
	
	public long onReceived(int mainCmd, int subCmd, Object obj){
		return 0;
	}
	

	/*public long onReceived(int mainCmd, int subCmd, byte[] data, int length) {
		
	}*/


	private byte[] packaging(ArrayList<Object> localinfo){
		int infoCount=localinfo.size()/2;
		ByteBuffer temp=ByteBuffer.allocate(infoCount*9);
		
		for(int i=0;i<infoCount;i++){
			temp.put((Byte)localinfo.get(i*2));
			temp.putLong((Long)localinfo.get(i*2+1));
		}
		
		
		return temp.array();
	}

	public void setUpdateList(ArrayList<Object> updateList) {
		this.updateList = updateList;
	}

	public ArrayList<Object> getUpdateList() {
		return updateList;
	}
	
}
