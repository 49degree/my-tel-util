package com.yangxp.wapsad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.yangxp.wapsad.util.NetWorkUtil;

public class ApplicationCenter extends BroadcastReceiver{
	private static PendingIntent sendHeartReceiverSender =null;//用于定时发送心跳数据
	public static NetWorkUtil mNetWorkUtil;
	public static boolean connectNetwork;
	
	final static int splitTime = 30*60*1000;//30min
	@Override 
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		//Log.e(this.getClass().getSimpleName(), (sendHeartReceiverSender!=null)+"onReceive+++++++++"+hashCode()+":"+arg1.getAction());
		
		if(sendHeartReceiverSender!=null)
			return;

		/*
		if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			
		}else if(arg1.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			
		}
		*/
		
		try{
			if(mNetWorkUtil!=null)
				mNetWorkUtil.cancelMmonitor();
		}catch(Exception e){}
		try{
			mNetWorkUtil = new NetWorkUtil(arg0.getApplicationContext(), new Handler(){
				public void handleMessage(Message msg){
					boolean connect = mNetWorkUtil.getNetWorkInfo() != NetWorkUtil.NET_TYPE_NONE;
					//Log.e(this.getClass().getSimpleName(), "connect+++++++++"+connect);
					if(connectNetwork!=connect){
						connectNetwork = connect;
					}
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

		
		sendHeartReceiverSender= PendingIntent.getBroadcast(arg0, 0, new Intent(arg0, HeartReceiver.class), 0);
	    
	    AlarmManager alarm=(AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
	    
	    alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), sendHeartReceiverSender);
	    //设定一个30分钟后的时间定时发送心跳
	    alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+splitTime, splitTime, sendHeartReceiverSender);

		

	}

}
