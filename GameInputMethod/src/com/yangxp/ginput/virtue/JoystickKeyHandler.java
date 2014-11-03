package com.yangxp.ginput.virtue;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.InputEvent;

import com.yangxp.ginput.virtue.instrument.IMotionInstrument;
import com.yangxp.ginput.virtue.instrument.NormalMotionInstrument;
import com.yangxp.ginput.virtue.keyswitch.IKeySwitch;
import com.yangxp.ginput.virtue.keyswitch.NormalKeySwitch;


public class JoystickKeyHandler {
	private String TAG = "JoystickKeyHandler";
	private static HashMap<String,IKeySwitch> instances = new HashMap<String,IKeySwitch>();
	Context mContext = null;
	IMotionInstrument mMotionInstrument = null;
	MappinggsModifyBroadcast mMappinggsModifyBroadcast = null;
	
	static JoystickKeyHandler mJoystickKeyHandler = null;
	
	public static JoystickKeyHandler getInstance(){
		return mJoystickKeyHandler;
	}
	
	public static synchronized void init(Context context){
		if(mJoystickKeyHandler!=null){
			mJoystickKeyHandler.release();
		}
		mJoystickKeyHandler = new JoystickKeyHandler(context);
	}
	
	private JoystickKeyHandler(Context context){
		mContext = context;
		mMotionInstrument = new NormalMotionInstrument();
		MotionCreaterFactory.init(context,mMotionInstrument);
		if(mMappinggsModifyBroadcast==null&&mContext.getApplicationContext()!=null){
			mMappinggsModifyBroadcast = new MappinggsModifyBroadcast();
			IntentFilter mMappinggsModifyBroadcastIntent = new IntentFilter();
			mMappinggsModifyBroadcastIntent.addAction("MappinggsModifyBroadcast");
			mContext.getApplicationContext().registerReceiver(mMappinggsModifyBroadcast, mMappinggsModifyBroadcastIntent);
		}
	}
	
	private void release(){
		try{
			synchronized(this){
				if(mMappinggsModifyBroadcast!=null){
					mContext.unregisterReceiver(mMappinggsModifyBroadcast);
					mMappinggsModifyBroadcast = null;
				}
			}
		}catch(Exception e){
		}
	}
	
	public void finalize(){
		release();

		try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public int getStatusBarHeight(String packageName) {
		return getKeySwitch(packageName,0).getStatusBarHeight();
	}

	public boolean changeEvent(String packageName,InputEvent event,int statusBarHeight){
		if(statusBarHeight<0)
			statusBarHeight = 0;
		return getKeySwitch(packageName,statusBarHeight).changeEvent(event);
	}
	
	public boolean containsKeyCode(String packageName,int keyCode){
		return getKeySwitch(packageName,0).containsKeyCode(keyCode);
	}
	
	
	public boolean containsType(String packageName,int type){
		return getKeySwitch(packageName,0).containsType(type);
	}
	
	private  IKeySwitch getKeySwitch(String packageName,int statusBarHeight){
		IKeySwitch iKeySwitch = instances.get(packageName);
		if(iKeySwitch == null){
			iKeySwitch = new NormalKeySwitch(mContext,packageName);
			instances.put(packageName, iKeySwitch);
		}
		if(iKeySwitch.getStatusBarHeight()!=statusBarHeight)
			iKeySwitch.setStatusBarHeight(statusBarHeight);
		return instances.get(packageName);
	}
	

	
	private class MappinggsModifyBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			Log.i(TAG,"JoystickKeyHandler onReceive:");
			String packageName = intent.getExtras().getString("packageName");
			if(instances.containsKey(packageName)){
				((NormalKeySwitch)instances.get(packageName)).parseConfig();
			}
		}
	}
	
}
