package com.custom.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.custom.MainApplication;


public class BootBroadcastReceiver extends BroadcastReceiver{
	public static boolean start = false;
	@Override
	public void onReceive(Context context, Intent mintent) {
		if(start)
			return;
		start = true;
		MainApplication.newInstance(context);
    	// 启动完成              
    	Intent intent = new Intent(context, StartServiceReceiver.class);               
    	intent.setAction("com.custom.broadcast.StartServiceReceiver");               
    	
    	PendingIntent sender = PendingIntent.getBroadcast(context, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	long firstime = SystemClock.elapsedRealtime(); 
    	int delay = (int) (Math.random()*200);
    	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    	// 随机值是20分钟之内的       
    	am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,delay * 10000+1000, sender); 

//    	PendingIntent sender = PendingIntent.getBroadcast(context, 0,intent, 0);
//    	long firstime = SystemClock.elapsedRealtime();              
//    	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//    	int delay = (int) (Math.random()*20);
//    	am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,10 * 1000, sender);
	}

}
