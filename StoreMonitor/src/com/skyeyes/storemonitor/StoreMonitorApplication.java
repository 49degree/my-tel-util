package com.skyeyes.storemonitor;

import com.skyeyes.base.BaseApplication;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;

public class StoreMonitorApplication extends BaseApplication{
	
	private ReceivLogin mReceivLogin;
	
	private static StoreMonitorApplication instance;
	
	
	public static StoreMonitorApplication getInstance() {
		return instance;
	}
	
	public static StoreMonitorApplication getAppContext() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//System.out.println("Aplication 初始化");
		instance = this;
		// 配置全局异常处理
		//Thread.setDefaultUncaughtExceptionHandler(new UEHandler(this));
	}

	public ReceivLogin getReceivLogin() {
		return mReceivLogin;
	}

	public void setReceivLogin(ReceivLogin mReceivLogin) {
		this.mReceivLogin = mReceivLogin;
	}
	
	
}
