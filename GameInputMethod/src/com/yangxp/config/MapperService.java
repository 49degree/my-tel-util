package com.yangxp.config;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.yangxp.config.view.ViewMappings;

/**
 * 启动形式的Service，主线程中
 * @author Administrator
 *
 */

public class MapperService extends Service {
	public final static String MAPPER_BROADCASTRECEIVER_SHOW_ACTION = "MapperBroadcastReceiverShow";
	public final static String MAPPER_BROADCASTRECEIVER_HIDE_ACTION = "MapperBroadcastReceiverHide";
	
	public static WindowManager mWindowsManager;
	private static ViewMappings mViewMappings;
	private static  WindowManager.LayoutParams wmParams;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public int onStartCommand(Intent intent, int flags, int startId){
		String action = "";
		try{
			/**
			 * 得到键为“action”的值
			 */
			action = intent.getExtras().getString("action"); 
		}catch(Exception e){
			
		}
		Log.i(this.getClass().getSimpleName(), action+":"+hashCode());
		synchronized (this) {
			if(action.equals(MAPPER_BROADCASTRECEIVER_SHOW_ACTION)){
				if(mWindowsManager==null){
					mWindowsManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
					mViewMappings = new ViewMappings(this);
					wmParams = new WindowManager.LayoutParams(
							WindowManager.LayoutParams.TYPE_PHONE,
							WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, 
							PixelFormat.TRANSLUCENT);
					wmParams.screenOrientation = mViewMappings.getScreenRotation();
					mViewMappings.setBackgroundColor(Color.argb(80, 1, 1, 1)); //背景透明度
					mWindowsManager.addView(mViewMappings, wmParams);
				}
			}else if(action.equals(MAPPER_BROADCASTRECEIVER_HIDE_ACTION)){
				if(mWindowsManager!=null){
					mWindowsManager.removeView(mViewMappings);
					wmParams = null;
					mViewMappings = null;
					mWindowsManager = null;
					
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
}