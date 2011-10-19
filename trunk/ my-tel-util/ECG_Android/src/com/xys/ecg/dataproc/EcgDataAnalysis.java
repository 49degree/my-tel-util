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
 * �ĵ����ݷ�������
 * 
 * �����ɷ���������ȡ����
 * ��Ϊ������������˳��ģ�ֻ����һ���������˲���ɾ����ͷ���ڶ�β����һ��
 * 
 * @author Administrator
 *
 */
public class EcgDataAnalysis{
	Logger logger = Logger.getLogger(EcgDataAnalysis.class);
	
	public final static int beAnalysisLength = 60; //������е���󳤶�
	private static EcgDataAnalysis instance = new EcgDataAnalysis();//���������
	private EcgBusiness ecgBusiness = null;//ҵ�������
	private LinkedList<EcgDataEntity> analysisEdDataQueue = new LinkedList<EcgDataEntity>();//�����Ѿ����������ݰ�����
	private Thread ecgDataAnalysisTask = null;
	public EcgDataEntity ecgDataEntity = null;//�������з�������
	public boolean stopFlag = false;//�Ƿ�ֹͣ����
	private static byte eventType = 0;//�ϴ��¼�����
	private byte[] dataBeginTime = null;//���ݰ���ʼʱ��
	private int dataPackNum = 0;//��ǰ�ǵڼ������ݰ�
	private int uploadIndex = 0;//ǰ����ϴ���������жϻ�������Ƿ��Ѿ�ȫ������ beAnalysisLength = uploadIndex
	
	
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
	 * ֹͣ�����߳�
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
	 * �����߳�
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
				
				//������ݰ���ʱ�������������ݰ�Ϊ��һ����
				if(tempEcgDataEntity.dataPacketTime!=null&&TypeConversion.bytesToLong(tempEcgDataEntity.dataPacketTime, 0)>0){
					dataBeginTime = tempEcgDataEntity.dataPacketTime;//������ݰ���ʱ�������������ݰ�Ϊ��һ����
					dataPackNum = 0;//��ʼ����������
				}
				dataPackNum++;
				// ���ַ���������ֻ��60�����ݰ�
				while (analysisEdDataQueue.size() >= beAnalysisLength) {
					analysisEdDataQueue.poll();// ��ͷ����
				}
				analysisEdDataQueue.add(tempEcgDataEntity);// �ڶ�β��������

				if(tempEcgDataEntity.isFirstData){//����ǵ�һ�����ݰ�������ó�ʼ������
					analysisInit();
				}
				logger.error("analysis");
				CDiagnoseResult cDiagnoseResult = analysis(tempEcgDataEntity.getByteArray(),dataPackNum,300);// �������ݷ�������
				// �ж��Ƿ���Ҫ�ϴ����� 
				if(cDiagnoseResult.m_code>0){
					logger.info("��⵽�ĵ��쳣״̬");
					if(ecgBusiness.getSoonUploadState()==UploadState.NORMAL){
						ecgBusiness.setSoonUploadState(UploadState.UPLOADEVENT);
						EcgDataAnalysis.eventType = cDiagnoseResult.m_code;//��ʾ���������쳣
					}else if(ecgBusiness.getSoonUploadState()==UploadState.UPLOADING){
						ecgBusiness.setSoonUploadState(UploadState.EVENT_IN_ENENT);// �����ϴ�״̬
					}
				}
				// ֪ͨ���̣߳���������ͼ
				ShapeLineEntity shapeLineEntity = new ShapeLineEntity(tempEcgDataEntity, cDiagnoseResult.m_nCurHR);
				Message msg = mainEventHandler.obtainMessage(HandlerWhat.Analysis2shapeline, shapeLineEntity);
				mainEventHandler.sendMessage(msg);

				switch (ecgBusiness.getSoonUploadState()) {
				case UPLOADEVENT:// ��Ҫ���������ϴ�
					logger.debug("UploadEcgInfoTool UPLOADEVENT");
					if(EcgDataAnalysis.eventType==0){//������Ƿ��������쳣�¼�����Ϊ�û���ť�¼�
						EcgDataAnalysis.eventType = CDiagnoseResult.ET_EventButtonDown;//�ð�ť�¼�
					}
					ecgDataList = new ArrayList<EcgDataEntity>(analysisEdDataQueue);
					ecgDataList.get(0).setDataPacketTime(dataBeginTime);
					this.sendMessage("�ϴ��쳣�¼�ǰ�������.......");
					UploadEcgInfoTool.sendEcgData(ecgDataList, getUploadEventEcgDataHeader((byte)0),mainEventHandler);// �ϴ�����
					SendSMS.sendAnalysisEventSMS(mainEventHandler);//�����¼���Ϣ
					ecgBusiness.setSoonUploadState(UploadState.UPLOADING);// ����״̬Ϊ�ϴ���
					break;
				case EVENT_IN_ENENT://�ϴ����ַ����˵���¼�
					UploadEcgInfoTool.sendEcgData(null, getUploadEventEcgDataHeader((byte)0),mainEventHandler);// �ϴ�����
					this.sendMessage("�ϴ��쳣�¼�.......");
					SendSMS.sendAnalysisEventSMS(mainEventHandler);//�����¼���Ϣ
					ecgBusiness.setSoonUploadState(UploadState.UPLOADING);// ����״̬Ϊ�ϴ���
				case UPLOADING:// ���ڴ��������ϴ��¼�
					logger.debug("UploadEcgInfoTool uploading");
					if (++uploadIndex == beAnalysisLength) {
						ecgDataList = new ArrayList<EcgDataEntity>(analysisEdDataQueue);
						ecgDataList.get(0).setDataPacketTime(dataBeginTime);
						this.sendMessage("�ϴ��쳣�¼���������.......");
						UploadEcgInfoTool.sendEcgData(ecgDataList,getUploadEventEcgDataHeader((byte)1),mainEventHandler);// �ϴ�����
						ecgBusiness.setSoonUploadState(UploadState.NORMAL);// ����״̬Ϊ����
						EcgDataAnalysis.eventType = 0;//�ָ�״̬
						uploadIndex = 0;//�ָ�״̬
					}
					break;
				default:
					break; 
				}
				
				//logger.info("ecgBusiness.getSoonUploadState():"+ecgBusiness.getSoonUploadState());
				
				try{
					synchronized(ecgDataAnalysisTask){
						ecgDataAnalysisTask.wait();//��ͣ
					}
				}catch(InterruptedException e){
					
				}
			}
			ecgDataAnalysisTask = null;

		}
		/**
		 * �����ĵ�����ͷ����
		 * @param dataType
		 * @return
		 */
		private UploadEcgDataHeader getUploadEventEcgDataHeader(byte dataType){
			//�����ϴ�����ͷ����
			UploadEventEcgDataHeader uploadEventEcgDataHeader = new UploadEventEcgDataHeader();
			uploadEventEcgDataHeader.setRqSendBusinessCode(TypeConversion.shortToBytes((short)0x0001));//����ҵ�����
			uploadEventEcgDataHeader.setRqSendFunctionCode(TypeConversion.shortToBytes((short)0x0004));//�����ܴ���
			uploadEventEcgDataHeader.setEventType(EcgDataAnalysis.eventType);//�쳣����
			uploadEventEcgDataHeader.setDataType(dataType);//	1byte	ǰ�������ݱ�ʶ��0Ϊǰ������ݣ�1Ϊ��������
			uploadEventEcgDataHeader.setEvent2getDataMoveTime(TypeConversion.intToBytes((dataPackNum-ecgDataList.size())*EcgBusiness.sleepTime));//4bytes	�¼���������¼����ݵ�ʱ��ƫ����(����)
			uploadEventEcgDataHeader.setEvent2eventDataMoveTime(TypeConversion.intToBytes(ecgDataList.size()*EcgBusiness.sleepTime));//	4bytes	�¼���������¼����ݵ�ʱ��ƫ����(����)
			uploadEventEcgDataHeader.setDataBeginTime(dataBeginTime);
			
			return uploadEventEcgDataHeader;
		}
		/**
		 * �����̷߳�����Ϣ
		 * @param msgStr
		 */
		private void sendMessage(String msgStr){
			Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,msgStr);//��ʾ��Ϣ�������߳�
			mainEventHandler.sendMessage(msg);
		}
	}

	
	static{
		System.loadLibrary("EcgAnalyse");
	}
	/*��ʼ�����ط�������*/
	private native static void analysisInit();
	/*���ñ����ĵ��������*/
	private native CDiagnoseResult analysis(byte[] ecgData,int dataPackNum,int dwPacketCount);
	/* ���Է���*/
	public native int getIntCountFromJNI(); 
	
	
}
