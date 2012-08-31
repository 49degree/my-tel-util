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
    	PendingIntent sender = PendingIntent.getBroadcast(context, 0,intent, 0);
    	long firstime = SystemClock.elapsedRealtime();              
    	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    	// 10秒一个周期，不停的发送广播             
    	am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,10 * 1000, sender);
	}

}
