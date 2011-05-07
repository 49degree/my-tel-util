package com.yang.android.tel.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.view.Display;
import android.view.WindowManager;

public class MyApplication extends Application {
	private static MyApplication instance;
	private static Map<String,Activity> activityMap = new HashMap<String,Activity>();
	public int screenHeight = 0;
	public int screenWidth = 0;

	public static MyApplication getInstance() {
		return instance;
	}

	/**
	 * 设置屏幕的高度和宽度
	 */
	public void setScreenW2H(Activity activity) {
		WindowManager manage = activity.getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @return
	 */
	public int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		return screenWidth;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
	}

	public void setActivity(String activityName,Activity instance) {
		activityMap.put(activityName, instance);
	}
	
	public Activity removeActivity(String activityName) {
		return activityMap.remove(activityName);
	}

	public Activity getActivity(String activityName) {
		return activityMap.get(activityName);
	}

}
