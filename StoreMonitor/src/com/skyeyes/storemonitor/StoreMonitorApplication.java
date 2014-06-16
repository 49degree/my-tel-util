package com.skyeyes.storemonitor;

import com.ffmpeg.lib.h264.NativeH264Decoder;
import com.skyeyes.base.BaseApplication;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceRegisterInfo;
import com.skyeyes.base.cmd.bean.impl.ReceiveStatusChange;
import com.skyeyes.base.cmd.bean.impl.ReceiveUserInfo;

public class StoreMonitorApplication extends BaseApplication{
	public static final int PIC_WIDTH = 322,PIC_HEIGHT=288;
	private ReceivLogin mReceivLogin;
	private ReceiveDeviceRegisterInfo mReceiveDeviceRegisterInfo;
	private ReceiveUserInfo mReceiveUserInfo;
	private int deviceStatus = -1;
	
	
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
		Thread.setDefaultUncaughtExceptionHandler(new UEHandler(this));
	}

	public ReceivLogin getReceivLogin() {
		return mReceivLogin;
	}

	public void setReceivLogin(ReceivLogin mReceivLogin) {
		this.mReceivLogin = mReceivLogin;
	}
	public ReceiveDeviceRegisterInfo getReceiveDeviceRegisterInfo() {
		return mReceiveDeviceRegisterInfo;
	}

	public void setReceiveDeviceRegisterInfo(ReceiveDeviceRegisterInfo mReceiveDeviceRegisterInfo) {
		this.mReceiveDeviceRegisterInfo = mReceiveDeviceRegisterInfo;
	}

	public ReceiveUserInfo getReceiveUserInfo() {
		return mReceiveUserInfo;
	}

	public void setReceiveUserInfo(ReceiveUserInfo mReceiveUserInfo) {
		this.mReceiveUserInfo = mReceiveUserInfo;
	}

	public int getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(int deviceStatus) {
		this.deviceStatus = deviceStatus;
	}


	
	
	
}
