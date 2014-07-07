package com.yangxp.wapsad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.yangxp.wapsad.util.NetWorkUtil;

public class ApplicationCenter extends Service{
	private static PendingIntent sendHeartReceiverSender =null;//用于定时发送心跳数据
	public static NetWorkUtil mNetWorkUtil;
	public static boolean connectNetwork;
	
	final static int splitTime = 5*60*1000;//30min
	@Override 
	public void onCreate() {
		super.onCreate();
		// TODO Auto-generated method stub
		Log.e(this.getClass().getSimpleName(), (sendHeartReceiverSender!=null)+"onCreate+++++++++"+hashCode());

		sendHeartReceiverSender= PendingIntent.getBroadcast(this, 0, new Intent(this, HeartReceiver.class), 0);

		/*
		if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			
		}else if(arg1.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			
		}
		*/
		try{
			mNetWorkUtil = new NetWorkUtil(this.getApplicationContext(), new Handler(){
				public void handleMessage(Message msg){
					boolean connect = mNetWorkUtil.getNetWorkInfo() != NetWorkUtil.NET_TYPE_NONE;
					//Log.e(this.getClass().getSimpleName(), "connect+++++++++"+connect);
					connectNetwork = connect;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			if(mNetWorkUtil.getNetWorkInfo() == NetWorkUtil.NET_TYPE_NONE){
				connectNetwork = false;
			}else{
				connectNetwork = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	    AlarmManager alarm=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
	    //设定一个30分钟后的时间定时发送心跳
	    alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+1000, splitTime, sendHeartReceiverSender);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
