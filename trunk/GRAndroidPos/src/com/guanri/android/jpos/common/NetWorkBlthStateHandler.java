package com.guanri.android.jpos.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;

import com.guanri.android.jpos.R;
import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.context.MainApplication;

public class NetWorkBlthStateHandler extends Handler {
	public final int NETWORK_NOTIFY_ID = 123456;
	private int connectTimes = 0;
	
	public NetWorkBlthStateHandler() {
		super();
	}

	public void handleMessage(Message msg) {
		NotificationManager manager = (NotificationManager) MainApplication.getInstance().getSystemService( Context.NOTIFICATION_SERVICE);
        //构建一个通知对象
		if (msg.what == HandlerWhat.BLUE_THOOTH_CONNECT_RESULE) {
		} else {
			Notification notification = new Notification(R.drawable.icon, "", System.currentTimeMillis()); 
			if ((Boolean)msg.obj) {
				Toast.makeText(MainApplication.getInstance(), "网络已经连接", Toast.LENGTH_LONG).show();
				manager.cancel(NETWORK_NOTIFY_ID);//清楚消息提示
				connectTimes = 0;
			} else {
				if(++connectTimes>5){
					MainApplication.getInstance().stopNetWorkListen();
				}else{
					Toast.makeText(MainApplication.getInstance(), "网络连接失败", Toast.LENGTH_LONG).show();
					MainApplication.getInstance().createConnection();//连接网络
			        
					//增加消息提示
					PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getInstance(),
			        		0, new Intent(Settings.ACTION_WIRELESS_SETTINGS),0 );
			        notification.setLatestEventInfo(MainApplication.getInstance(),"网络连接失败", "网络已经断开，请点击设置网络!",pendingIntent);
			        notification.flags|= Notification.FLAG_INSISTENT; //自动终止
			        notification.flags |= Notification.FLAG_AUTO_CANCEL;
			        //notification.defaults |= Notification.DEFAULT_SOUND; //默认声音
			        manager.notify(NETWORK_NOTIFY_ID, notification);//发起通知
				}
			}
		}

	}
}
