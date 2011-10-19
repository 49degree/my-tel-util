package com.xys.ecg.dataproc;

import java.util.ArrayList;
import java.util.LinkedList;

import android.media.MediaPlayer;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.xys.ecg.activity.ECGApplication;
import com.xys.ecg.activity.ECG_Android.MainEventHandler;
import com.xys.ecg.activity.R;
import com.xys.ecg.bean.CDiagnoseResult;
import com.xys.ecg.bean.EcgDataEntity;
import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.bean.ShapeLineEntity;
import com.xys.ecg.bean.UploadEcgDataHeader;
import com.xys.ecg.bean.UploadEventEcgDataHeader;
import com.xys.ecg.business.EcgBusiness;
import com.xys.ecg.business.EcgBusiness.UploadState;
import com.xys.ecg.log.Logger;
import com.xys.ecg.upload.SendSMS;
import com.xys.ecg.upload.UploadEcgInfoTool;
import com.xys.ecg.utils.TypeConversion;
/**
 * 心电数据分析管理
 * 
 * 必须由分析对象来取数据
 * 因为分析数据是有顺序的，只有上一个处理完了才能删除队头，在队尾加入一个
 * 
 * @author Administrator
 *
 */
public class EcgDataAnalysis{
	Logger logger = Logger.getLogger(EcgDataAnalysis.class);
	
	public final static int beAnalysisLength = 60; //缓存队列的最大长度
	private static EcgDataAnalysis instance = new EcgDataAnalysis();//类自身对象
	private EcgBusiness ecgBusiness = null;//业务类对象
	private LinkedList<EcgDataEntity> analysisEdDataQueue = new LinkedList<EcgDataEntity>();//缓存已经分析的数据包队列
	private Thread ecgDataAnalysisTask = null;
	public EcgDataEntity ecgDataEntity = null;//立即进行分析对象
	public boolean stopFlag = false;//是否停止分析
	private static byte eventType = 0;//上传事件类型
	private byte[] dataBeginTime = null;//数据包开始时间
	private int dataPackNum = 0;//当前是第几个数据包
	private int uploadIndex = 0;//前半段上传后计数，判断缓存队列是否已经全部更新 beAnalysisLength = uploadIndex
	
	
	private EcgDataAnalysis(){
		ecgBusiness = EcgBusiness.getInstance();
	}

	public static EcgDataAnalysis getInstance(){
		return instance;
	}
	
	public  void  beginAnalysis(EcgDataEntity ecgDataEntity,MainEventHandler mainEventHandler){
		stopFlag = false;
		this.ecgDataEntity = ecgDataEntity;
		ecgBusiness = EcgBusiness.getInstance();
		if(ecgDataAnalysisTask!=null){
			synchronized(ecgDataAnalysisTask){
				ecgDataAnalysisTask.notify();
			}
			
		}else{
			ecgDataAnalysisTask = new Thread(new EcgDataAnalysisTask(mainEventHandler));
			ecgDataAnalysisTask.start();
		}
	}
	
	/**
	 * 停止分析线程
	 */
	public synchronized boolean stopTask(){
		stopFlag = true;
		if(ecgDataAnalysisTask!=null){
			synchronized(ecgDataAnalysisTask){
				ecgDataAnalysisTask.notify();
		    }
			
		}
		return true;
	}
	
	/**
	 * 分析线程
	 * @author yangkele
	 *
	 */
	public class EcgDataAnalysisTask implements Runnable{
		private MainEventHandler mainEventHandler = null;
		ArrayList<EcgDataEntity> ecgDataList = null;
		
		public EcgDataAnalysisTask(MainEventHandler mainEventHandler){
			this.mainEventHandler = mainEventHandler;
		}

		public void run(){
			Looper.prepare();
			while (!stopFlag) {
				EcgDataEntity tempEcgDataEntity = new EcgDataEntity(ecgDataEntity.getPacketHead(),ecgDataEntity.getEcgPacket(),
						ecgDataEntity.getAccPacket(),ecgDataEntity.getIsFirstData(),ecgDataEntity.getDataPacketTime(),ecgDataEntity.getIsHisDate());
				
				//如果数据包有时间变量，则该数据包为第一个包
				if(tempEcgDataEntity.dataPacketTime!=null&&TypeConversion.bytesToLong(tempEcgDataEntity.dataPacketTime, 0)>0){
					dataBeginTime = tempEcgDataEntity.dataPacketTime;//如果数据包有时间变量，则该数据包为第一个包
					dataPackNum = 0;//初始化包基数器
				}
				dataPackNum++;
				// 保持分析队列中只有60个数据包
				while (analysisEdDataQueue.size() >= beAnalysisLength) {
					analysisEdDataQueue.poll();// 对头出列
				}
				analysisEdDataQueue.add(tempEcgDataEntity);// 在队尾插入数据

				if(tempEcgDataEntity.isFirstData){//如果是第一个数据包，则调用初始化方法
					analysisInit();
				}
				logger.error("analysis");
				CDiagnoseResult cDiagnoseResult = analysis(tempEcgDataEntity.getByteArray(),dataPackNum,300);// 进行数据分析代码
				// 判断是否需要上传数据 
				if(cDiagnoseResult.m_code>0){
					logger.info("检测到心电异常状态");
					if(ecgBusiness.getSoonUploadState()==UploadState.NORMAL){
						ecgBusiness.setSoonUploadState(UploadState.UPLOADEVENT);
						EcgDataAnalysis.eventType = cDiagnoseResult.m_code;//表示分析出的异常
					}else if(ecgBusiness.getSoonUploadState()==UploadState.UPLOADING){
						ecgBusiness.setSoonUploadState(UploadState.EVENT_IN_ENENT);// 设置上传状态
					}
				}
				// 通知主线程，更新曲线图
				ShapeLineEntity shapeLineEntity = new ShapeLineEntity(tempEcgDataEntity, cDiagnoseResult.m_nCurHR);
				Message msg = mainEventHandler.obtainMessage(HandlerWhat.Analysis2shapeline, shapeLineEntity);
				mainEventHandler.sendMessage(msg);

				switch (ecgBusiness.getSoonUploadState()) {
				case UPLOADEVENT:// 需要立即进行上传
					logger.debug("UploadEcgInfoTool UPLOADEVENT");
					if(EcgDataAnalysis.eventType==0){//如果不是分析出的异常事件，则为用户按钮事件
						EcgDataAnalysis.eventType = CDiagnoseResult.ET_EventButtonDown;//置按钮事件
					}
					ecgDataList = new ArrayList<EcgDataEntity>(analysisEdDataQueue);
					ecgDataList.get(0).setDataPacketTime(dataBeginTime);
					this.sendMessage("上传异常事件前半段数据.......");
					UploadEcgInfoTool.sendEcgData(ecgDataList, getUploadEventEcgDataHeader((byte)0),mainEventHandler);// 上传数据
					SendSMS.sendAnalysisEventSMS(mainEventHandler);//发送事件消息
					ecgBusiness.setSoonUploadState(UploadState.UPLOADING);// 设置状态为上传中
					break;
				case EVENT_IN_ENENT://上传中又发生了点击事件
					UploadEcgInfoTool.sendEcgData(null, getUploadEventEcgDataHeader((byte)0),mainEventHandler);// 上传数据
					this.sendMessage("上传异常事件.......");
					SendSMS.sendAnalysisEventSMS(mainEventHandler);//发送事件消息
					ecgBusiness.setSoonUploadState(UploadState.UPLOADING);// 设置状态为上传中
				case UPLOADING:// 正在处理立即上传事件
					logger.debug("UploadEcgInfoTool uploading");
					if (++uploadIndex == beAnalysisLength) {
						ecgDataList = new ArrayList<EcgDataEntity>(analysisEdDataQueue);
						ecgDataList.get(0).setDataPacketTime(dataBeginTime);
						this.sendMessage("上传异常事件后半段数据.......");
						UploadEcgInfoTool.sendEcgData(ecgDataList,getUploadEventEcgDataHeader((byte)1),mainEventHandler);// 上传数据
						ecgBusiness.setSoonUploadState(UploadState.NORMAL);// 设置状态为正常
						EcgDataAnalysis.eventType = 0;//恢复状态
						uploadIndex = 0;//恢复状态
					}
					break;
				default:
					break; 
				}
				
				//logger.info("ecgBusiness.getSoonUploadState():"+ecgBusiness.getSoonUploadState());
				
				try{
					synchronized(ecgDataAnalysisTask){
						ecgDataAnalysisTask.wait();//暂停
					}
				}catch(InterruptedException e){
					
				}
			}
			ecgDataAnalysisTask = null;

		}
		/**
		 * 构造心电数据头对象
		 * @param dataType
		 * @return
		 */
		private UploadEcgDataHeader getUploadEventEcgDataHeader(byte dataType){
			//构造上传数据头对象
			UploadEventEcgDataHeader uploadEventEcgDataHeader = new UploadEventEcgDataHeader();
			uploadEventEcgDataHeader.setRqSendBusinessCode(TypeConversion.shortToBytes((short)0x0001));//请求业务代码
			uploadEventEcgDataHeader.setRqSendFunctionCode(TypeConversion.shortToBytes((short)0x0004));//请求功能代码
			uploadEventEcgDataHeader.setEventType(EcgDataAnalysis.eventType);//异常类型
			uploadEventEcgDataHeader.setDataType(dataType);//	1byte	前后半段数据标识，0为前半段数据，1为后半段数据
			uploadEventEcgDataHeader.setEvent2getDataMoveTime(TypeConversion.intToBytes((dataPackNum-ecgDataList.size())*EcgBusiness.sleepTime));//4bytes	事件的相对于事件数据的时间偏移量(毫秒)
			uploadEventEcgDataHeader.setEvent2eventDataMoveTime(TypeConversion.intToBytes(ecgDataList.size()*EcgBusiness.sleepTime));//	4bytes	事件的相对于事件数据的时间偏移量(毫秒)
			uploadEventEcgDataHeader.setDataBeginTime(dataBeginTime);
			
			return uploadEventEcgDataHeader;
		}
		/**
		 * 给主线程发送消息
		 * @param msgStr
		 */
		private void sendMessage(String msgStr){
			Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,msgStr);//提示信息返回主线程
			mainEventHandler.sendMessage(msg);
		}
	}

	
	static{
		System.loadLibrary("EcgAnalyse");
	}
	/*初始化本地分析对象*/
	private native static void analysisInit();
	/*调用本地心电分析方法*/
	private native CDiagnoseResult analysis(byte[] ecgData,int dataPackNum,int dwPacketCount);
	/* 测试方法*/
	public native int getIntCountFromJNI(); 
	
	
}
