package com.skyeyes.base;


import android.app.Application;

/**
 * 
 * @author 杨雪平
 *
 */
public class BaseApplication extends Application {
	private boolean writeLog = false; // 是否打印Log
	private boolean outputLog = true; // 是否输出Log
	
	private static BaseApplication instance;
	public static BaseApplication getInstance() {
		return instance;
	}
	
	public static BaseApplication getAppContext() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//System.out.println("Aplication 初始化");
		instance = this;
	}

	public boolean isWriteLog() {
		return writeLog;
	}

	public void setWriteLog(boolean writeLog) {
		this.writeLog = writeLog;
	}
	public boolean isOutputLog() {
		return outputLog;
	}

	public void setOutputLog(boolean outputLog) {
		this.outputLog = outputLog;
	}
}
