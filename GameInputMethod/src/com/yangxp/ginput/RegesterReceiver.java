package com.yangxp.ginput;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputDevice;
import android.widget.Toast;

public class RegesterReceiver extends BroadcastReceiver{
	static InputManager mIm = null;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.e(this.getClass().getSimpleName(), "arg1:"+arg1);
		Log.e(this.getClass().getSimpleName(), "========================="+this.hashCode());
		
		/*
		Iterator<String> it = arg1.getExtras().keySet().iterator();
		while(it.hasNext()){
			String key  = it.next();
			Log.e(this.getClass().getSimpleName(), key+"="+arg1.getExtras().get(key));
		}
		
		Toast.makeText(arg0, Arrays.toString(arg1.getExtras().keySet().toArray()), Toast.LENGTH_SHORT).show();
		*/
		Toast.makeText(arg0,arg1.getAction(), Toast.LENGTH_SHORT).show();
		
		if(GameInputManagerService.getInstance()==null){
			Intent intent = new Intent(arg0,GameInputManagerService.class);
			arg0.startService(intent);
		}
	}

}
