package com.guanri.android.jpos.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.guanri.android.jpos.services.AutoRunService;

public class BootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("BootBroadcastReceiver","电脑已经启动。。。。。。。。。。");
		
		// TODO Auto-generated method stub
//        Intent i  = new Intent();  
//        i.setClass(context, AutoRunService.class); 
//        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startService(i);  
	}

}
