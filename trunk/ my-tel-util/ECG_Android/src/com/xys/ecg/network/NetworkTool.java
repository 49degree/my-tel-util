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
	 * ����ʹ����������
	 * @param netWorkType ����ʹ�õ���������
	 * @return
	 */
	public static String setNetWork(String netWorkType){
		
		return null;
	}
	
	
	/**
	 * ��֤�����Ƿ����
	 * @return
	 */
	public static boolean checkInternet(Context context) {
		//activity = null;//ģ�������Բ��ж�����״��
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
	 * ��������
	 * @param handler
	 * @param activity
	 */
	public static  void changeNetwork(Handler handler,final Activity activity){
		handler.post(
				new Runnable(){
					public void run(){
						Builder b = new AlertDialog.Builder(activity).setTitle("û�п��õ�����").setMessage("�뿪��GPRS��WIFI��������");
						b.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int whichButton) {
										Intent mIntent = new Intent("/");
										ComponentName comp = new ComponentName("com.android.settings",
												"com.android.settings.WirelessSettings");
										mIntent.setComponent(comp);
										mIntent.setAction("android.intent.action.VIEW");
										activity.startActivity(mIntent);
										//System.exit(0);//�Ƴ�����
									}
								}).setNeutralButton("ȡ��",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int whichButton) {
										dialog.cancel();
										//System.exit(0);//�Ƴ�����
									}
						}).create();
						b.show();
					}
				}
		);
	}	
}
