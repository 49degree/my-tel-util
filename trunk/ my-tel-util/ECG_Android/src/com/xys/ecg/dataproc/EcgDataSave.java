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
	 * ��ʼ�������ݰ�
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
	 * ֹͣ�������ݰ��߳�
	 */
	public synchronized boolean stopTask(){	
		stopFlag = true;
		if(ecgDataSaveTask!=null){
			synchronized(ecgDataSaveTask){
				ecgDataSaveTask.notify();
		    }
			
			//�˳�ǰ�ϲ���ɾ���ĸ���ʱ�ļ�
			ecgDataFileOperate.combineFile(ecgDataFileOperate.gFilePathEcg, 
					ecgDataFileOperate.gFilePathAcc_X, ecgDataFileOperate.gFilePathAcc_Y, 
					ecgDataFileOperate.gFilePathAcc_Z, ecgDataFileOperate.PATH_SD + ecgDataFileOperate.gCurrentTime+".dat");//�ϲ��ĸ���ʱ�ļ�
			
			ecgDataFileOperate.delete(ecgDataFileOperate.gFilePathEcg, ecgDataFileOperate.gFilePathAcc_X, 
					ecgDataFileOperate.gFilePathAcc_Y, ecgDataFileOperate.gFilePathAcc_Z);//ɾ����ʱ�ļ�
			
			ecgDataFileOperate.combineFile(ecgDataFileOperate.gFilePathEcgEx, 
					ecgDataFileOperate.gFilePathAcc_XEx, ecgDataFileOperate.gFilePathAcc_YEx, 
					ecgDataFileOperate.gFilePathAcc_ZEx, ecgDataFileOperate.PATH_SD + ecgDataFileOperate.gCurrentTimeEx+".dat");//�ϲ��ĸ���ʱ�ļ�
			
			ecgDataFileOperate.delete(ecgDataFileOperate.gFilePathEcgEx, ecgDataFileOperate.gFilePathAcc_XEx, 
					ecgDataFileOperate.gFilePathAcc_YEx, ecgDataFileOperate.gFilePathAcc_ZEx);//ɾ����ʱ�ļ�
		}
		return true;
	}
	
	/**
	 * ���������߳�
	 * @author yangkele
	 *
	 */
	private class EcgDataSaveTask implements Runnable{
		private MainEventHandler mainHandler = null;
		public EcgDataSaveTask(MainEventHandler mainHandler){
			this.mainHandler = mainHandler;
			ecgDataFileOperate = new EcgDataFileOperate(this.mainHandler);//��ʼ���ļ��������
		}
		public void run(){
			while (!stopFlag){
				logger.debug("save the ecg data to file!");
				
				//���������߳�
				boolean saveResult = true;
				if(ecgDataEntity instanceof EcgDataEntity){
					saveResult = ecgDataFileOperate.saveEcgData((EcgDataEntity)ecgDataEntity);
				}else if(ecgDataEntity instanceof List){
					saveResult = ecgDataFileOperate.seveEcgDataList((LinkedList)ecgDataEntity);
				}
				
				//�����������ʧ�ܣ�������Ϣ�����߳̽�����ʾ
				if(!saveResult){
					Message msg = mainHandler.obtainMessage(HandlerWhat.Tread2Notify,"��������ʧ����");
					mainHandler.sendMessage(msg);
				}
				try{
					synchronized(ecgDataSaveTask){
						ecgDataSaveTask.wait();//��ͣ
					}
				}catch(InterruptedException e){
					
				}
			}
			ecgDataSaveTask = null;

		}
	}
}
