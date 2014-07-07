package com.yangxp.wapsad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootedReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.e(this.getClass().getSimpleName(), "onCreate+++++++++"+hashCode()+":"+arg1.getAction());
		Intent intent = new Intent(arg0,ApplicationCenter.class);
		arg0.startService(intent);
	}

}
