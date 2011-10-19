package com.xys.ecg.dataproc;

import java.util.LinkedList;
import java.util.List;

import android.os.Message;

import com.xys.ecg.activity.ECG_Android.MainEventHandler;
import com.xys.ecg.bean.EcgDataEntity;
import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.file.EcgDataFileOperate;
import com.xys.ecg.log.Logger;

public class EcgDataSave {
	public static Logger logger = Logger.getLogger(EcgDataSave.class);
	private static EcgDataSave instance = new EcgDataSave(); 
	private EcgDataFileOperate ecgDataFileOperate = null;
	public  Thread ecgDataSaveTask = null;
	public Object ecgDataEntity = null;
	public boolean stopFlag = false;
	
	private EcgDataSave(){
		
	}
	
	public static EcgDataSave getInstance(){
		return instance;
	}
	
	/**
	 * 开始保存数据包
	 * @param ecgDataEntity
	 * @param mainHandler
	 */
	public void  beginEcgDataSave(Object ecgDataEntity,MainEventHandler mainHandler){
		stopFlag = false;
		this.ecgDataEntity = ecgDataEntity;
		if(ecgDataSaveTask!=null){
			synchronized(ecgDataSaveTask){
				ecgDataSaveTask.notifyAll();
			}
		}else{
			ecgDataSaveTask = new Thread(new EcgDataSaveTask(mainHandler));
			ecgDataSaveTask.start();
		}
	}
	
	/**
	 * 停止保存数据包线程
	 */
	public synchronized boolean stopTask(){	
		stopFlag = true;
		if(ecgDataSaveTask!=null){
			synchronized(ecgDataSaveTask){
				ecgDataSaveTask.notify();
		    }
			
			//退出前合并并删除四个临时文件
			ecgDataFileOperate.combineFile(ecgDataFileOperate.gFilePathEcg, 
					ecgDataFileOperate.gFilePathAcc_X, ecgDataFileOperate.gFilePathAcc_Y, 
					ecgDataFileOperate.gFilePathAcc_Z, ecgDataFileOperate.PATH_SD + ecgDataFileOperate.gCurrentTime+".dat");//合并四个临时文件
			
			ecgDataFileOperate.delete(ecgDataFileOperate.gFilePathEcg, ecgDataFileOperate.gFilePathAcc_X, 
					ecgDataFileOperate.gFilePathAcc_Y, ecgDataFileOperate.gFilePathAcc_Z);//删除临时文件
			
			ecgDataFileOperate.combineFile(ecgDataFileOperate.gFilePathEcgEx, 
					ecgDataFileOperate.gFilePathAcc_XEx, ecgDataFileOperate.gFilePathAcc_YEx, 
					ecgDataFileOperate.gFilePathAcc_ZEx, ecgDataFileOperate.PATH_SD + ecgDataFileOperate.gCurrentTimeEx+".dat");//合并四个临时文件
			
			ecgDataFileOperate.delete(ecgDataFileOperate.gFilePathEcgEx, ecgDataFileOperate.gFilePathAcc_XEx, 
					ecgDataFileOperate.gFilePathAcc_YEx, ecgDataFileOperate.gFilePathAcc_ZEx);//删除临时文件
		}
		return true;
	}
	
	/**
	 * 保存数据线程
	 * @author yangkele
	 *
	 */
	private class EcgDataSaveTask implements Runnable{
		private MainEventHandler mainHandler = null;
		public EcgDataSaveTask(MainEventHandler mainHandler){
			this.mainHandler = mainHandler;
			ecgDataFileOperate = new EcgDataFileOperate(this.mainHandler);//初始化文件保存对象
		}
		public void run(){
			while (!stopFlag){
				logger.debug("save the ecg data to file!");
				
				//保存数据线程
				boolean saveResult = true;
				if(ecgDataEntity instanceof EcgDataEntity){
					saveResult = ecgDataFileOperate.saveEcgData((EcgDataEntity)ecgDataEntity);
				}else if(ecgDataEntity instanceof List){
					saveResult = ecgDataFileOperate.seveEcgDataList((LinkedList)ecgDataEntity);
				}
				
				//如果保存数据失败，返回消息到主线程进行提示
				if(!saveResult){
					Message msg = mainHandler.obtainMessage(HandlerWhat.Tread2Notify,"保存数据失败了");
					mainHandler.sendMessage(msg);
				}
				try{
					synchronized(ecgDataSaveTask){
						ecgDataSaveTask.wait();//暂停
					}
				}catch(InterruptedException e){
					
				}
			}
			ecgDataSaveTask = null;

		}
	}
}
