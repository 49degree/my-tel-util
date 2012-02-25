package com.custom.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.custom.MainApplication;


public class BootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent mintent) {
		MainApplication.newInstance(context);
        if (Intent.ACTION_BOOT_COMPLETED.equals(mintent.getAction())) {
        	// 启动完成              
        	Intent intent = new Intent(context, StartServiceReceiver.class);               
        	intent.setAction("com.custom.broadcast.StartServiceReceiver");               
        	PendingIntent sender = PendingIntent.getBroadcast(context, 0,intent, 0);
        	long firstime = SystemClock.elapsedRealtime();              
        	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        	// 10秒一个周期，不停的发送广播             
        	am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,10 * 1000, sender);//6小时执行一次
        	
        	
        }
	}

}
