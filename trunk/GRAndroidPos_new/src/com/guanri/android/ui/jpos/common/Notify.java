package com.guanri.android.ui.jpos.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.ui.jpos.MainActivity;
import com.ihandy.xgx.R;

public class Notify {
	/**
	 * 发通知
	 * @param noifyId
	 * @param msg
	 */
	public static void notify(int noifyId,String title,String msg){
        //以下是对Notification的各种参数设定
        int icon=R.drawable.icon;
        String tickerText=title;
        long when=System.currentTimeMillis();
        Notification nfc=new Notification(icon,tickerText,when);
        Context cxt=MainApplication.getInstance();
        String expandedTitle=msg;
        String expandedText="";
        //intent是非常重要的参数,用来启动你实际想做的事情,设为null后点击状态栏上的Notification就没有任何反应了.
        Intent intent=new Intent(cxt,MainActivity.class);
        PendingIntent nfcIntent=PendingIntent.getActivity(cxt,0,intent,0);
        nfc.setLatestEventInfo(cxt,expandedTitle,expandedText,nfcIntent);
        //发送Notification
        NotificationManager nfcManager=(NotificationManager)cxt.getSystemService(Context.NOTIFICATION_SERVICE);
        nfcManager.notify(noifyId,nfc);
	}
	
	/**
	 * 发通知
	 * @param noifyId
	 * @param msg
	 */
	public static void clearNotify(int noifyId){
        NotificationManager nfcManager=(NotificationManager)MainApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        nfcManager.cancel(noifyId);
	}
}
