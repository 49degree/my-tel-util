package com.guanri.android.ui.jpos.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.guanri.android.jpos.services.AidlRunService;


public class BootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("BootBroadcastReceiver","电脑已经启动。。。。。。。。。。");
		
		// TODO Auto-generated method stub
        Intent i  = new Intent();  
        i.setClass(context, AidlRunService.class); 
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);  
	}

}
