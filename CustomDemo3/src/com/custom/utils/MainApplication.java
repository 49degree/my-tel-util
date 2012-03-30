package com.custom.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * 
 * @author 杨雪平
 *
 */
public class MainApplication extends Application{
	private static Context instance;
	public int screenHeight = 0;
	public int screenWidth = 0;
	
	private boolean isLogin = false;//是否已经登陆
	private Object userInfo = null;//用户信息对象

	public static Context getInstance() {
		return instance;
	}
	
	public static void newInstance(Context mInstance) {
		if(instance==null){
			instance=mInstance;
		}
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
		//System.out.println("Aplication 初始化");
		instance = this;
	}
	
	
	
}
