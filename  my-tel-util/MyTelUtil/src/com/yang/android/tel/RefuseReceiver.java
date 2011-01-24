package com.yang.android.tel;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.yang.android.tel.db.DBBean;
import com.yang.android.tel.db.DBOperator;

/**
 * 收取广播事件,拒接来电和短信
 * @author yangkele
 *
 */
public class RefuseReceiver  extends BroadcastReceiver{
	public static String TAG = "RefuseReceiver";
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	public void onReceive(Context context, Intent intent) {
		DBOperator dbOperator = new DBOperator(context,"RefuseTelDB.db",null,1);
		SQLiteDatabase sqlDb = dbOperator.getReadableDatabase();
		
		TelephonyManager tManager =  
			(TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE); 
		 ITelephony iTelephony = Util.getIPhone(tManager);
		// 呼出电话   
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER); 
			Log.e(TAG, phoneNumber);
		}else if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
	           SmsMessage[] messages = getMessagesFromIntent(intent);
	           for (SmsMessage message : messages){
	              Log.i(TAG, message.getOriginatingAddress() + " : " + 
	                  message.getDisplayOriginatingAddress() + " : " + 
	                  message.getDisplayMessageBody() + " : " + 
	                  message.getTimestampMillis());
	           }
	           
	   }else { 
			// 呼入电话   
			 switch (tManager.getCallState()) { 
			 case TelephonyManager.CALL_STATE_RINGING: // 当前是来电
				 String phoneNum = intent.getStringExtra("incoming_number");
				 Log.e(TAG, phoneNum);
				 try{
					 Map<String,String> params = new HashMap<String,String>(1);
					 params.put(DBBean.RefuseTel.REFUSE_TEL_NUM, phoneNum);
					 params.put(DBBean.RefuseTel.REFUSE_CALL, "1");
					 if(DBOperator.queryRowNum(DBBean.needInitTables.get("RefuseTel"), sqlDb, params)>0){
						 iTelephony.endCall();
					 }
				 }catch(Exception e){
					 e.printStackTrace();
				 }
	             break;
	         case TelephonyManager.CALL_STATE_OFFHOOK://接听
	             break;
	         case TelephonyManager.CALL_STATE_IDLE: // 挂机   
	             break;
	         default:
	        	 break;
	     }   
			
		}
		sqlDb.close();
	}
	
    public final SmsMessage[] getMessagesFromIntent(Intent intent){
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++){
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++)
        {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }

}
