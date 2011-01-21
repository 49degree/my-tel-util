package com.yang.android.tel;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class MyTelServices extends Service {
	private static final String TAG = "MyService";
	private MediaPlayer player = null;
	private ITelephony iTelephony = null;
	private String telNum = null;
	private int operateCode = 1;
	private static boolean inListener = false;
	
	//���ض���
	public MyServiceBinder binder = new MyServiceBinder();
	public class MyServiceBinder extends Binder {
        public MyTelServices getServices() {
            return MyTelServices.this; 
        }
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return binder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		endCall();
		return super.onUnbind(intent);
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		
		player = MediaPlayer.create(this,R.raw.message);//���������ǣ���Ҫ�滻���ֵ�����
		player.setLooping(false); // Set looping 
		
		getITelephony();//��ȡ�绰����
	}
	  
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		inListener = false;
		player.stop();
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		player.start();
	}
	/**
	 * ��ʼ����
	 */
	public void startPlay() {
		//Toast.makeText(this, "My music play", Toast.LENGTH_LONG).show();
		Log.d(TAG, "play");
		player.start();
	}
	/**
	 * ֹͣ����
	 */
	public void stopPlay() {
		//Toast.makeText(this, "My music stop", Toast.LENGTH_LONG).show();
		Log.d(TAG, "play");
		player.stop();
	}
	
	/**
	 * ��ʼ����
	 */
	public void startCall(String tel) {
		Toast.makeText(this, "start calling", Toast.LENGTH_LONG).show();
		Log.d(TAG, "start calling");
		telNum = tel;
		operateCode = 0;
		if(!inListener){
			inListener = true;
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			tm.listen(new TeleListener(),PhoneStateListener.LISTEN_CALL_STATE);//���ü�������
		}else{
			dialer(tel);
		}

	}
	/**
	 * ֹͣ�绰
	 */
	public void endCall() {
		Toast.makeText(this, "end calling", Toast.LENGTH_LONG).show();
		Log.d(TAG, "end calling");
		operateCode = 1;
		if(iTelephony!=null){
			try{
				iTelephony.endCall();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	
	// ����
	private void dialer(String tel) {
		Log.e(TAG, "dialer");
		try{
			iTelephony.endCall();
			iTelephony.call(tel);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ȡ�绰�ӿ�
	 */
	private void getITelephony() {
		TelephonyManager tManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		// ��ʼ��iTelephony
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",(Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			iTelephony = (ITelephony) getITelephonyMethod.invoke(tManager, (Object[]) null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * ״̬������
	 * @author szluyl
	 *
	 */
	class TeleListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: {
				Log.e(TAG, "CALL_STATE_IDLE");
				if (operateCode == 0) {
					dialer(telNum);
				}
				break;
			}
			case TelephonyManager.CALL_STATE_OFFHOOK: {
				
				Log.e(TAG, "CALL_STATE_OFFHOOK");
				Log.e(TAG, "CALL_STATE_OFFHOOK  iTelephony:"+iTelephony);
//				startPlay();
//				if(iTelephony!=null){
//					Log.e(TAG, "CALL_STATE_OFFHOOK  iTelephony");
//					try{
//						Thread.sleep(5*1000);
//						iTelephony.endCall();
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
				break;
			}
			case TelephonyManager.CALL_STATE_RINGING: {
				Log.e(TAG, "CALL_STATE_RINGING");
				break;
			}
			default:
				break;
			}
		}
	}	
}


