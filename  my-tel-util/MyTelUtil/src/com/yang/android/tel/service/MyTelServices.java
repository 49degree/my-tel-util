package com.yang.android.tel.service;


import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.yang.android.tel.activity.MyApplication;
import com.yang.android.tel.activity.MyTelUtilActivity;
import com.yang.android.tel.log.Logger;
import com.yang.android.tel.network.SocketClient;
import com.yang.android.tel.receiver.RefuseReceiver;
import com.yang.android.tel.utils.SharedPreferencesUtils;
import com.yang.android.tel.utils.Utils;

public class MyTelServices extends Service{
	private static final String TAG = "MyService";
	public Logger logger = Logger.getLogger(MyTelServices.class);
	private MediaPlayer player = null;
	private ITelephony iTelephony = null;
	private String telNum = null;
	private int operateCode = 1;
	public static boolean inListener = false;
	
	public static boolean isReCall = false;//�Ƿ��ز�
	

	
	//���ض���
	public MyServiceBinder binder = new MyServiceBinder();
	public class MyServiceBinder extends Binder {
        public MyTelServices getServices() {
            return MyTelServices.this; 
        }
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		logger.error("onBind");
		return binder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		logger.error("onUnbind");
		endCall();
		return super.onUnbind(intent);
	}
	
	@Override
	public void onCreate() {
		//Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		logger.error("onCreate");
		inListener = true;
		//player = MediaPlayer.create(this,R.raw.message);//���������ǣ���Ҫ�滻���ֵ�����
		//player.setLooping(false); // Set looping 
		
		getITelephony();//��ȡ�绰����
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		tm.listen(new TeleListener(),PhoneStateListener.LISTEN_CALL_STATE);//���ü�������
	}
	  
	@Override
	public void onDestroy() {
		logger.error("onDestroy");
		inListener = false;
		//player.stop();
		//Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		//Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		logger.error("onStart");
		//player.start();
	}
	/**
	 * ��ʼ����
	 */
	public void startPlay() {
		//Toast.makeText(this, "My music play", Toast.LENGTH_LONG).show();
		Log.d(TAG, "play");
		//player.start();
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
		//Toast.makeText(this, "start calling", Toast.LENGTH_LONG).show();
		Log.d(TAG, "start calling");
		telNum = tel;
		operateCode = 0;
		dialer(tel);

	}
	/**
	 * ֹͣ�绰
	 */
	public void endCall() {
		//Toast.makeText(this, "end calling", Toast.LENGTH_LONG).show();
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
		logger.error("dialer");
		try{
			iTelephony.endCall();
			iTelephony.call(tel);
			sendBroadcast(new Intent(MyTelUtilActivity.inAction2));
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
		iTelephony = Utils.getIPhone(tManager);
	}
	/**
	 * ״̬������
	 * @author szluyl
	 *
	 */
	int ringerMode = -999;
	int vibratorMode = -999;
	class TeleListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			if(!inListener){
				return;
			}
			
			AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);//����
			Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);//��
			
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: {
				logger.error("CALL_STATE_IDLE"+ringerMode);
				if (operateCode == 0&&isReCall) {
					dialer(telNum);
				}
				
				if(MyApplication.getInstance().getActivity("IncomingCallActivity")!=null){
					MyApplication.getInstance().getActivity("IncomingCallActivity").finish();
				}
				
				if(MyApplication.getInstance().getActivity("ImcomingBlackActivity")!=null){
					MyApplication.getInstance().getActivity("ImcomingBlackActivity").finish();
				}
				
	            if (audioManager != null&&ringerMode!=-999) { 
	            	audioManager.setRingerMode(ringerMode);               
	            	audioManager.getStreamVolume(AudioManager.STREAM_RING);
	            } 
				break;
			}
			case TelephonyManager.CALL_STATE_OFFHOOK: {
				break;
			}
			case TelephonyManager.CALL_STATE_RINGING: {
				if(!RefuseReceiver.isCheckTel){//�Ƿ����
					return ;
				}
				logger.error("CALL_STATE_RINGING:"+incomingNumber);
				try {
					if (MyTelUtilActivity.telMap!=null&&MyTelUtilActivity.telMap.containsKey(incomingNumber)) {    
			            if (audioManager != null) { 
			            	if(audioManager.getRingerMode()>0){
			            		ringerMode = audioManager.getRingerMode();
			            	}
			            	audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);               
			            	audioManager.getStreamVolume(AudioManager.STREAM_RING);
			            } 
			            if(vibrator!=null){
			            	try{
			            		vibrator.cancel();
			            	}catch(Exception e){
			            	}
			            }
						try {
							if (RefuseReceiver.shoutDown) {// �Ƿ������Ҷ�
								iTelephony.endCall();
								// �����﷢����Ϣ
								String ip = SharedPreferencesUtils.getConfigString(MyTelServices.this,"socket_ip");
								String port = SharedPreferencesUtils.getConfigString(MyTelServices.this,"socket_port");
								SocketClient socket = new SocketClient(ip, port);
								StringBuffer msg = new StringBuffer();
								msg.append("name:").append(MyTelUtilActivity.telMap.get(incomingNumber));
								msg.append(":tel:").append(incomingNumber);
								boolean sendRst = socket.sendMessage(msg.toString());
								logger.error("send message:" + sendRst);

							}
						}catch(Exception e){
		            	}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			}
			default:
				break;
			}
		}
	}
	
}


