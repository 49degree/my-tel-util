package com.xys.ecg.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class NetworkTool {

	/**
	 * 设置使用哪种网络
	 * @param netWorkType 返回使用的网络名称
	 * @return
	 */
	public static String setNetWork(String netWorkType){
		
		return null;
	}
	
	
	/**
	 * 验证网络是否可用
	 * @return
	 */
	public static boolean checkInternet(Context context) {
		//activity = null;//模拟器测试不判断网络状况
		if (context != null) {
			boolean flag = false;
			ConnectivityManager cwjManager = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network = cwjManager.getActiveNetworkInfo();
			if (network != null) {
				flag = network.isAvailable();
			}else{
				NetworkInfo[] info = cwjManager.getAllNetworkInfo(); 
				if (info != null) {
					for (int i = 0; i < info.length; i++) { 
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true; 
						}
					}
				}
			}
			return flag;
		}
		return true;
	}
	/**
	 * 设置网络
	 * @param handler
	 * @param activity
	 */
	public static  void changeNetwork(Handler handler,final Activity activity){
		handler.post(
				new Runnable(){
					public void run(){
						Builder b = new AlertDialog.Builder(activity).setTitle("没有可用的网络").setMessage("请开启GPRS或WIFI网络连接");
						b.setPositiveButton("确定",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int whichButton) {
										Intent mIntent = new Intent("/");
										ComponentName comp = new ComponentName("com.android.settings",
												"com.android.settings.WirelessSettings");
										mIntent.setComponent(comp);
										mIntent.setAction("android.intent.action.VIEW");
										activity.startActivity(mIntent);
										//System.exit(0);//推出程序
									}
								}).setNeutralButton("取消",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int whichButton) {
										dialog.cancel();
										//System.exit(0);//推出程序
									}
						}).create();
						b.show();
					}
				}
		);
	}	
}
