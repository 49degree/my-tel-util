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
 * 发送短信工具类
 * @author Administrator
 *
 */
public class SendSMS {
	public static void sendAnalysisEventSMS(Handler mainEventHandler){
		new SendSMS().new SendAnalysisEventSMSThread(mainEventHandler).start();
	}
	
	/**
	 * 发送事件短信
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
				// 如果短信没有超过限制长度，则返回一个长度的List。
				Iterator<String> it = sendSMSMap.keySet().iterator();
				while(it.hasNext()){
					String phoneNum = it.next();
					List<String> texts = sms.divideMessage(sendSMSMap.get(phoneNum));
					for (String text : texts) {
						sms.sendTextMessage(phoneNum, null, text, null, null);
					}
				}
				
				if(mainEventHandler!=null){
					Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,"发送事件信息成功！");//提示信息返回主线程
					mainEventHandler.sendMessage(msg);
				}
			}catch(Exception e){
				if(mainEventHandler!=null){
					Message msg = mainEventHandler.obtainMessage(HandlerWhat.Tread2Notify,"发送事件信息失败！");//提示信息返回主线程
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
