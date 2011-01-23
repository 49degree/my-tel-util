package com.yang.android.tel;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

/**
 * ��ȡ�㲥�¼�,�ܽ�����Ͷ���
 * @author yangkele
 *
 */
public class RefuseReceiver  extends BroadcastReceiver{
	public static String TAG = "RefuseReceiver";
	
	public void onReceive(Context context, Intent intent) {
		TelephonyManager tManager =  
			(TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE); 
		 ITelephony iTelephony = Util.getIPhone(tManager);
		// �����绰   
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
			
		} else { 
			// ����绰   
			 switch (tManager.getCallState()) { 
			 case TelephonyManager.CALL_STATE_RINGING: // ��ǰ������
	        	
	        	 
	             break;
	         case TelephonyManager.CALL_STATE_OFFHOOK://����
	             break;
	         case TelephonyManager.CALL_STATE_IDLE: // �һ�      
	             break;
	         default:
	        	 break;
	     }   
			
		}
	}
}
