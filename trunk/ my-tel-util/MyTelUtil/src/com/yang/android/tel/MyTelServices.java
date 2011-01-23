package com.yang.android.tel;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

public class MyTelServices extends Service implements KeyEvent.Callback{

    public boolean onKeyDown(int keyCode, KeyEvent event){
    	Log.e(TAG,"onKeyDown keyCode:"+keyCode);
    	return true;
    }


    public boolean onKeyLongPress(int keyCode, KeyEvent event){
    	Log.e(TAG,"onKeyLongPress keyCode:"+keyCode);
    	return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event){
    	Log.e(TAG,"onKeyUp keyCode:"+keyCode);
    return true;	
    }


    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event){
    	Log.e(TAG," onKeyMultiple keyCode:"+keyCode);
    	return true;
    }
	
	
	private static final String TAG = "MyService";
	private MediaPlayer player = null;
	private ITelephony iTelephony = null;
	private String telNum = null;
	private int operateCode = 1;
	private static boolean inListener = false;
	
	//返回对象
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
		
		player = MediaPlayer.create(this,R.raw.message);//运行例子是，需要替换音乐的名称
		player.setLooping(false); // Set looping 
		
		getITelephony();//获取电话对象
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
	 * 开始播放
	 */
	public void startPlay() {
		//Toast.makeText(this, "My music play", Toast.LENGTH_LONG).show();
		Log.d(TAG, "play");
		player.start();
	}
	/**
	 * 停止播放
	 */
	public void stopPlay() {
		//Toast.makeText(this, "My music stop", Toast.LENGTH_LONG).show();
		Log.d(TAG, "play");
		player.stop();
	}
	
	/**
	 * 开始拨打
	 */
	public void startCall(String tel) {
		Toast.makeText(this, "start calling", Toast.LENGTH_LONG).show();
		Log.d(TAG, "start calling");
		telNum = tel;
		operateCode = 0;
		if(!inListener){
			inListener = true;
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			tm.listen(new TeleListener(),PhoneStateListener.LISTEN_CALL_STATE);//设置监听对象
		}else{
			dialer(tel);
		}

	}
	/**
	 * 停止电话
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
	
	
	// 拨号
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
	 * 获取电话接口
	 */
	private void getITelephony() {
		TelephonyManager tManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		iTelephony = Util.getIPhone(tManager);
	}
	/**
	 * 状态监听器
	 * @author szluyl
	 *
	 */
	class TeleListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			try{

				Log.e(TAG, "CALL_STATE:"+iTelephony.getCallState()+":"+state);
			}catch(Exception e){
				
			}
				
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
				startPlay();
				
				
				
				
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
	
	
//	public class IncomingCallListener extends PhoneStateListener {
//		private CallStateObserver mCallStateListener = null;
//		private ITelephony mPhone;
//	}
}


