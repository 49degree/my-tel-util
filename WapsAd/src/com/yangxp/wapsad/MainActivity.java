package com.yangxp.wapsad;

import cn.waps.AppConnect;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		this.findViewById(R.id.test).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PendingIntent sendHeartReceiverSender= PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(MainActivity.this, BootedReceiver.class), 0);
			    
			    AlarmManager alarm=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
			    
			    alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), sendHeartReceiverSender);

			}
		});
	}


}
