package com.yang.android.tel.receiver;

import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.yang.android.tel.activity.ImcomingBlackActivity;
import com.yang.android.tel.activity.IncomingCallActivity;
import com.yang.android.tel.activity.MyTelUtilActivity;
import com.yang.android.tel.log.Logger;

/**
 * ��ȡ�㲥�¼�,�ܽ�����Ͷ���
 * @author yangkele
 *
 */
public class RefuseReceiver  extends BroadcastReceiver{
	public static String TAG = "RefuseReceiver";
	public Logger logger = Logger.getLogger(RefuseReceiver.class);
	
	public static boolean isCheckTel = false;
	public static boolean shoutDown = false;
	public static boolean refuseMessage = false;
	public void onReceive(Context context, Intent intent) {
		try{
			TelephonyManager tManager =  
				(TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE); 
			 final Context fcontext = context;
			if(intent.getAction().equals(MyTelUtilActivity.SMS_RECEIVE)){//�յ�����
		        // ��һ������ȡ���ŵ����ݺͷ�����  
		        StringBuilder body = new StringBuilder();// ��������  
		        StringBuilder number = new StringBuilder();// ���ŷ�����  
		        Bundle bundle = intent.getExtras();  
		        if (bundle != null) {  
		            Object[] _pdus = (Object[]) bundle.get("pdus");  
		            SmsMessage[] message = new SmsMessage[_pdus.length];  
		            for (int i = 0; i < _pdus.length; i++) {  
		                message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);  
		            }  
		            for (SmsMessage currentMessage : message) {  
		                body.append(currentMessage.getDisplayMessageBody());  
		                number.append(currentMessage.getDisplayOriginatingAddress());  
		            }  
		            String smsBody = body.toString();  
		            String smsNumber = number.toString();  
		            if (smsNumber.contains("+86")) {  
		                smsNumber = smsNumber.substring(3);  
		            }  
		            // �ڶ���:ȷ�ϸö��������Ƿ������������  
		            boolean flags_filter = false;  
		            if (smsNumber.equals("10086")) {// ����10086�����Ķ���  
	
		            }  
		            
					if (MyTelUtilActivity.telMap!=null&&MyTelUtilActivity.telMap.containsKey(smsNumber)) {
						if(refuseMessage){//�Ƿ������Ҷ�
			                flags_filter = true;  
			                logger.error("sms_number:"+smsNumber);  
						}
					}
		            
		            // ������:ȡ��  
		            if (flags_filter) {  
		                this.abortBroadcast();  
		            } 
		        }
			}else if (intent.getAction().equals(MyTelUtilActivity.inAction2)){// �����绰   
				logger.error("ACTION_NEW_OUTGOING_CALL");
				new Thread() {
					public void run() {
						try {
							sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent myTelUtilActivity = new Intent(fcontext,
								MyTelUtilActivity.class);
						fcontext.startActivity(myTelUtilActivity); 
					}
				}.start();
			}else{ 
				logger.error("tManager.getCallState():"+isCheckTel);
				if(!isCheckTel){//�Ƿ����
					return ;
				}
				// ����绰   
				switch (tManager.getCallState()) {
				case TelephonyManager.CALL_STATE_RINGING: // ��ǰ������
					
					logger.error("CALL_STATE_RINGING");
					final String phoneNum = intent.getStringExtra("incoming_number");
					logger.error(phoneNum);
					try {
						logger.error(":"+shoutDown);
						if (MyTelUtilActivity.telMap!=null&&MyTelUtilActivity.telMap.containsKey(phoneNum)) {
							if(shoutDown){//�Ƿ������Ҷ�

								new Thread() {
									public void run() {
										try {
											sleep(100);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										Intent incomingCall = new Intent(fcontext,
												ImcomingBlackActivity.class);
										incomingCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										fcontext.startActivity(incomingCall);
									}
								}.start();
							}else{
								new Thread() {
									public void run() {
										try {
											sleep(100);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										Intent incomingCall = new Intent(fcontext,
												IncomingCallActivity.class);
										incomingCall.putExtra("callName",MyTelUtilActivity.telMap.containsKey(phoneNum));
										incomingCall.putExtra("callNumber",phoneNum);
										incomingCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										fcontext.startActivity(incomingCall);
									}
								}.start();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:// ����
					break;
				case TelephonyManager.CALL_STATE_IDLE: // �һ�
					break;
				default:
					break;
				}   
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
