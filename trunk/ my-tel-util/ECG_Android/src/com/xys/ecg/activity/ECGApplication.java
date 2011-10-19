package com.xys.ecg.activity;

import android.app.Activity;
import android.app.Application;
import android.view.Display;
import android.view.WindowManager;

public class ECGApplication extends Application {
	private static ECGApplication instance;
	public int screenHeight = 0;
	public int screenWidth = 0;


	public static ECGApplication getInstance() {
		return instance;
	}
	/**
	 * 设置屏幕的高度和宽度
	 */
	public void setScreenW2H(Activity activity){
		WindowManager manage = activity.getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
	}
	/**
	 * 获取屏幕高度
	 * @return
	 */
	public int getScreenHeight(){
		return screenHeight;
	}
	/**
	 * 获取屏幕宽度
	 * @return
	 */
	public int getScreenWidth(){
		return screenWidth;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
	}
}
