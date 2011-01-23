package com.yang.android.tel;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

/**
 * 收取广播事件,拒接来电和短信
 * @author yangkele
 *
 */
public class RefuseReceiver  extends BroadcastReceiver{
	public static String TAG = "RefuseReceiver";
	
	public void onReceive(Context context, Intent intent) {
		TelephonyManager tManager =  
			(TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE); 
		 ITelephony iTelephony = Util.getIPhone(tManager);
		// 呼出电话   
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
			
		} else { 
			// 呼入电话   
			 switch (tManager.getCallState()) { 
			 case TelephonyManager.CALL_STATE_RINGING: // 当前是来电
	        	
	        	 
	             break;
	         case TelephonyManager.CALL_STATE_OFFHOOK://接听
	             break;
	         case TelephonyManager.CALL_STATE_IDLE: // 挂机      
	             break;
	         default:
	        	 break;
	     }   
			
		}
	}
}
