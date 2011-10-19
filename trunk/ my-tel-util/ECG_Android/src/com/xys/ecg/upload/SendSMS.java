package com.xys.ecg.upload;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;

import com.xys.ecg.activity.ECGApplication;
import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.sqlite.ContactDB;

/**
 * ���Ͷ��Ź�����
 * @author Administrator
 *
 */
public class SendSMS {
	public static void sendAnalysisEventSMS(Handler mainEventHandler){
		new SendSMS().new SendAnalysisEventSMSThread(mainEventHandler).start();
	}
	
	/**
	 * �����¼�����
	 * @author Administrator
	 *
	 */
	private class SendAnalysisEventSMSThread extends Thread{
		Handler mainEventHandler = null;
		public SendAnalysisEventSMSThread(Handler mainEventHandler){
			this.mainEventHandler = mainEventHandler;
		}
		
		public void run(){
			ContactDB contactDB = null;
			Cursor cursor = null;
			
			try{
				HashMap<String,String> sendSMSMap = new HashMap<String,String>();
				contactDB = new ContactDB(ECGApplication.getInstance());
				cursor = contactDB.getAllContact();
				while(!cursor.isAfterLast()){
					if(cursor.getInt(cursor.getColumnIndex(("SMS")))==0){
						sendSMSMap.put(cursor.getString(cursor.getColumnIndex("PhoneNum")), cursor.getString(cursor.getColumnIndex("MSMContent")));
					}
				}
				
				SmsManager sms = SmsManager.getDefault();
				// �������û�г������Ƴ��ȣ��򷵻�һ�����ȵ�List��
				Iterator<String> it = sendSMSMap.keySet().iterator();
				while(it.hasNext()){
					String phoneNum = it.next();
					List<String> texts = sms.divideMessage(sendSMSMap.get(phoneNum));
					for (String text : texts) {
						sms.sendTextMessage(phoneNum, null, text, null, null);
					}
				}
				
				if(mainEventHandler!=null){
					Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,"�����¼���Ϣ�ɹ���");//��ʾ��Ϣ�������߳�
					mainEventHandler.sendMessage(msg);
				}
			}catch(Exception e){
				if(mainEventHandler!=null){
					Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,"�����¼���Ϣʧ�ܣ�");//��ʾ��Ϣ�������߳�
					mainEventHandler.sendMessage(msg);
				}
			}finally{
				if(cursor!=null){
					cursor.close();
				}
				if(contactDB!=null){
					contactDB.close();
				}
				
			}

			
		}
		
	}

}
