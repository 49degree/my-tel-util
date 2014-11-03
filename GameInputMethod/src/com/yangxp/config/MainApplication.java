package com.yangxp.config;


import android.app.Application;

/**
 * 
 * @author 杨雪平
 *
 */
public class MainApplication extends Application {
	private static MainApplication instance;
	public static MainApplication getInstance() {
		return instance;
	}


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//System.out.println("Aplication 初始化");
		instance = this;
	}

}
