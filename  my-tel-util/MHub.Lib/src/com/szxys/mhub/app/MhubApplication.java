package com.szxys.mhub.app;

import android.app.Activity;
import android.app.Application;

/**
 * Application
 * 
 * @author 黄仕龙
 * 
 */
public class MhubApplication extends Application {
	public static final int MAINCMD_LOADING_END = 1; // 系统初始化完成
	public static final int MAINCMD_LOADING_USERINFO = 2; // 初始化用户数据

	private static MhubApplication instance;

	// 保存初始化页面对象，用来在初始化结束后关闭初始化页面
	public static Activity loadingActivity = null;

	// 研发阶段测试用数据
	// public String testEGGBthMac = "00:19:5D:24:CC:90"; // 心电采集器蓝牙地址
	public String testEGGBthMac = "00:19:5D:24:CC:2A"; // 心电采集器蓝牙地址
	public String testBOXYBthMac = ""; // 血氧采集器蓝牙地址
	public String testNetSvrAddr = "http://172.18.17.59:8888/Services/RpcService.ashx";// 网络服务器地址

	public static MhubApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
}
