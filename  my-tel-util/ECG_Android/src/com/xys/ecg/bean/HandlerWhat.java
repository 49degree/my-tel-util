package com.xys.ecg.bean;

public class HandlerWhat {
	public static final int Main2Analysis = 1;
	public static final int Main2SaveData = 2;
	public static final int Main2shapeline = 3;
	
	public static final int Analysis2Main = 4;
	public static final int SaveData2Main = 5;
	public static final int Shapeline2Main = 6;
	
//	public static final int Analysis2SaveData = 7;
	public static final int Analysis2shapeline = 8;
	
	public static final int SaveData2Analysis = 9;
	public static final int SaveData2shapeline = 10;
	
	public static final int Shapeline2Analysis = 11;
	public static final int Shapeline2SaveData = 12;
	
	public static final int Tread2Notify = 13;//子线程通知消息栏
	
	public static final int Business2Analysis = 14;//业务通知数据分析
	public static final int Business2SaveData = 15;//业务通知数据保存
	public static final int Bluetooth2Main = 16;//蓝牙管理工具通知主线程信息
	
	public static final int SoapQueryAdviceTool2Parents = 17;//通知线程获取医生建议信息
	public static final int FileOperate2MainEmpty = 18;//保存线程发给主线程，主要用于电量检测
	public static final int FileOperate2MainLow = 19;
	public static final int FileOperate2MainNomal = 20;
	public static final int FileOperate2MainHight = 21;
}
