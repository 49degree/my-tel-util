package com.xys.ecg.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import android.media.MediaPlayer;

import com.xys.ecg.activity.ECGApplication;
import com.xys.ecg.activity.R;
import com.xys.ecg.activity.ECG_Android.MainEventHandler;
import com.xys.ecg.bean.EcgDataEntity;
import com.xys.ecg.dataproc.EcgDataAnalysis;
import com.xys.ecg.dataproc.EcgDataSave;
import com.xys.ecg.file.EcgDataFileOperate;
import com.xys.ecg.log.Logger;
import com.xys.ecg.utils.TypeConversion;

/**
 * 心电数据管理
 * @author Administrator
 *
 */
public class EcgBusiness {
	
	public static Logger logger = Logger.getLogger(EcgBusiness.class);
	
	private static EcgBusiness instance = new EcgBusiness(); //心电业务对象
	private LinkedList<EcgDataEntity> ecgBusinessDataQueue = new LinkedList<EcgDataEntity>();//心电业务缓存数据
	private static UploadState soonUploadState = UploadState.NORMAL;//是否立即发送数据
	private EcgDataAnalysis ecgDataAnalysis = EcgDataAnalysis.getInstance();
	private EcgDataSave ecgDataSave = EcgDataSave.getInstance();
	
	
	public Thread ecgBusinessTask = null;//数据处理线程
	public boolean stopFlag = false;
	public static int sleepTime = 960; //发送数据间隔时间（毫秒）
	//异常状态控制
	public static enum UploadState{
		NORMAL,     //正常情况
		UPLOADEVENT,//发生立即上传事件
		UPLOADING ,  //正常上传中
		EVENT_IN_ENENT//上传中又发生了点击事件
	}
	
	private EcgBusiness(){
	}
	
	public static EcgBusiness getInstance(){
		return instance;
	}

	public boolean insertEcgData(EcgDataEntity ecgDataEntity){
		try {
			if(1 == ecgDataEntity.getIsEventData()){
				// 当有事件按钮按下标志时，置异常标志
				if(soonUploadState==UploadState.NORMAL){
					soonUploadState = UploadState.UPLOADEVENT;
				}else if(soonUploadState==UploadState.UPLOADING){
					soonUploadState = UploadState.EVENT_IN_ENENT;
				}
				
				
				MediaPlayer play = MediaPlayer.create(ECGApplication.getInstance(), R.raw.exceptionevent);
				play.start();
			}
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
		}
		synchronized(ecgBusinessDataQueue){
			logger.info("inserEcgData ");
			return ecgBusinessDataQueue.add(ecgDataEntity);
		}
		
	}

	/**
	 * 获取缓存队列的长度
	 * @return
	 */
	public int getEcgBusinessDataQueueLength(){
		if(ecgBusinessDataQueue!=null){
			return ecgBusinessDataQueue.size();
		}else{
			return 0;
		}
	}
	
	public EcgDataEntity getEcgBusinessDataQueue() {
		synchronized(ecgBusinessDataQueue){
			return ecgBusinessDataQueue.poll();
		}
	}

	private void clearEcgBusinessDataQueue() {
		synchronized(ecgBusinessDataQueue){
			 ecgBusinessDataQueue.clear();
		}
	}
	
	public UploadState getSoonUploadState(){
		return this.soonUploadState;
	}
	
	public void setSoonUploadState(UploadState soonUploadState){
		this.soonUploadState = soonUploadState;
	}
	/**
	 * 开始心电业务线程
	 * @param mainEventHandler 主线程消息处理对象
	 */
	public synchronized void startTask(MainEventHandler mainEventHandler){
		stopFlag = false;
		if(ecgBusinessTask!=null){
			ecgBusinessTask.notify();
		}else{
			ecgBusinessTask = new Thread(new EcgBusinessTask(mainEventHandler));
			ecgBusinessTask.start();
		}
	}
	/**
	 * 停止心电业务线程
	 */
	public synchronized boolean stopTask(MainEventHandler mainEventHandler){
		stopFlag = true;
		if(ecgBusinessTask!=null){
			ecgDataSave.beginEcgDataSave(new LinkedList(ecgBusinessDataQueue), mainEventHandler);//保存缓存数据
			clearEcgBusinessDataQueue();//清空队列
			
			synchronized(ecgBusinessTask){
				ecgBusinessTask.notify();
		    }
			ecgBusinessTask = null;
		}
		ecgDataSave.stopTask();//停止保存线程
		ecgDataAnalysis.stopTask();//停止分析线程
		return true;
	}
	
	/**
	 * 停止采集数据
	 * @return
	 */
	public synchronized boolean suspendTask(){
		if(ecgBusinessTask!=null){
			try{
				synchronized(ecgBusinessTask){
					ecgBusinessTask.wait();//暂停
			    }
			}catch(InterruptedException e){
			}
		}
		return true;
	}
	
	/**
	 * 定时器，发送数据给分析线程和保存线程，并通知进行相应的操作
	 * @author Administrator
	 *
	 */
	private class EcgBusinessTask implements Runnable{
		private MainEventHandler mainEventHandler = null;
		public EcgBusinessTask(MainEventHandler mainEventHandler){
			this.mainEventHandler = mainEventHandler;
		}
		public void run(){
//			while(i++<30){
//				insertEcgData(getEcgDataEntity(i));
//				logger.debug("insert the data:"+i);
//			}
			while(mainEventHandler!=null&&!stopFlag){
//				insertEcgData(getEcgDataEntity(i));
//				logger.debug("insert the data:"+i);
//				i++;
				logger.info("ecgBusinessDataQueue:"+ecgBusinessDataQueue.size());
				//根据缓存中的数据包数量调整循环间隔时间
				if(ecgBusinessDataQueue.size()>180){//如果缓存数据包大于180
					EcgBusiness.sleepTime = 800;//等待时间改成800毫秒
				}
				if(ecgBusinessDataQueue.size()<=120&&EcgBusiness.sleepTime == 800){//如果缓存数据包小于120且当前等待时间为800毫秒，则恢复为960
					EcgBusiness.sleepTime = 960;
				}
				if(ecgBusinessDataQueue.size()>0){
					EcgDataEntity ecgDataEntity = getEcgBusinessDataQueue();
					ecgDataSave.beginEcgDataSave(ecgDataEntity, mainEventHandler);//保存数据
					//判断是否历史数据，如果是则不需要进行分析
					if(!ecgDataEntity.getIsHisDate()){//如果不是历史数据，则进行分析
						ecgDataAnalysis.beginAnalysis(ecgDataEntity, mainEventHandler);
					}
				}
				try{
					Thread.sleep(EcgBusiness.sleepTime);//暂停
				}catch(InterruptedException e){
					
				}
			}
		}
	}
	
	/**
	 * 创建模拟的数据包
	 * @param i
	 * @return
	 */
	public static EcgDataEntity getEcgDataEntity(int i){
		//构造包头
		byte[] packetHead = new byte[10];
		packetHead[0] = (short)0x06;
		System.arraycopy(TypeConversion.shortToBytes((short)10), 0, packetHead, 1, 2);
		System.arraycopy(TypeConversion.intToBytes(i), 0, packetHead, 3, 4);
		byte status1 = 56;
		status1 = (byte)(status1|(30<<4));
		status1 = (byte)(status1|(1<<7));
		
		packetHead[7]  = status1;
		
		byte status2 = 1;//触摸式
		if(i%70==0){
			status2= 0;//导连式
		}
		if(i%50==0){
			status2 = (byte)(status2|(1<<1));//为历史数据
		}else{
			status2 = (byte)(status2|(0<<1));//为即时数据
		}
		if(i%100==0){
			status2 = (byte)(status2|(1<<2));//结束
		}else{
			status2 = (byte)(status2|(0<<2));
		}
		packetHead[8]  = status2;
		
		//构造ECG数据
		byte[] ecgPackage = new byte[296];
		System.arraycopy(TypeConversion.shortToBytes((short)300), 0, ecgPackage, 0, 2);
		ecgPackage[2] = ((byte)1)<<4;
		ecgPackage[3] = (byte)0;
		ecgPackage[4] = (byte)0;
		System.arraycopy(TypeConversion.shortToBytes((short)296), 0, ecgPackage, 5, 2);
		ecgPackage[7] = (byte)0;
		System.arraycopy(TypeConversion.longToBytes(2960322321341321L), 0, ecgPackage, 8, 8);
		System.arraycopy(TypeConversion.longToBytes(23425234254324523L), 0, ecgPackage, 16, 8);

		//构造ACC数据
		byte[] accPackage = new byte[222];
		System.arraycopy(TypeConversion.shortToBytes((short)75), 0, accPackage, 0, 2);
		accPackage[2] = (byte)7;
		System.arraycopy(TypeConversion.shortToBytes((short)222), 0, accPackage, 3, 2);
		accPackage[5] = (byte)0;
		System.arraycopy(TypeConversion.longToBytes(46345343535232323L), 0, accPackage, 6, 8);
		System.arraycopy(TypeConversion.longToBytes(46345343535232323L), 0, accPackage, 78, 8);
		System.arraycopy(TypeConversion.longToBytes(46345343535232323L), 0, accPackage, 150, 8);
		
		//构造整个数据包
		byte[] ecgDataEntitybt = new byte[528];
		System.arraycopy(packetHead, 0, ecgDataEntitybt, 0, 10);
		System.arraycopy(ecgPackage, 0, ecgDataEntitybt, 10, 296);
		System.arraycopy(accPackage, 0, ecgDataEntitybt, 306, 222);
		
		boolean isfirst = false;
		boolean isHisData = false;
		if(i%100==0){
			isfirst = true;
		}
		if(i%2!=0){
			isHisData = true;
		}
		
		byte[] nowTime = new byte[8];
		System.arraycopy(TypeConversion.longToBytes(new Date().getTime()/1000), 0, nowTime, 0, 8);//采集器给过来的时间是秒数，所以new Date().getTime()/1000获取时间
		EcgDataEntity ecgDataEntity = new EcgDataEntity(ecgDataEntitybt,isfirst,nowTime,isHisData);
		return ecgDataEntity;
	}
}
