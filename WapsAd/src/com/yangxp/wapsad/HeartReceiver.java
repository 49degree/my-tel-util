package com.yangxp.wapsad;

import java.lang.reflect.Field;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import cn.waps.AdInfo;
import cn.waps.AppConnect;

public class HeartReceiver extends BroadcastReceiver{
	public final static int NOTIFICATION_ID = 123456;
	public static AdInfo adInfo = null;
	
	private Context mContext;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		//Log.e(this.getClass().getSimpleName(), "onReceive+++++++++");
		mContext = arg0;
		//Log.e(this.getClass().getSimpleName(), "connectNetwork+++++++++"+ApplicationCenter.connectNetwork);
		//网络未连接
		if(!ApplicationCenter.connectNetwork)
			return;
		//获取一条自定义广告数据
		try{
			adInfo = AppConnect.getInstance(arg0).getAdInfo();
		}catch(Exception e){}
		if(adInfo!=null)
			showNotification();
		else{
			//Log.e(this.getClass().getSimpleName(), "adInfo is null+++++++++");
			try{
				AppConnect.getInstance("7873e6f6d0abb46c1fe0b7ff05c2fe6e", "waps", arg0);
				// 预加载自定义广告内容（仅在使用了自定义广告、抽屉广告或迷你广告的情况，才需要添加）
				AppConnect.getInstance(arg0).initAdInfo();
			}catch(Exception e){
				e.printStackTrace();
			}
			//获取一条自定义广告数据
			try{
				adInfo = AppConnect.getInstance(arg0).getAdInfo();
			}catch(Exception e){}
			if(adInfo!=null)
				showNotification();

		}
		
	}
	
	
	 // 显示一个通知   
	public void showNotification() {
		// 创建一个通知
		Notification mNotification = new Notification();
		// 设置属性值
		mNotification.icon = android.R.drawable.ic_menu_share;
		//mNotification.largeIcon = adInfo.getAdIcon();
		mNotification.tickerText = adInfo.getAdText();
		mNotification.when = System.currentTimeMillis(); // 立即发生此通知

		// 带参数的构造函数,属性值如上
		// Notification mNotification = = new
		// Notification(R.drawable.icon,"NotificationTest",
		// System.currentTimeMillis()));

		// 添加声音效果
		//mNotification.defaults |= Notification.DEFAULT_SOUND;
		//mNotification.sound = Uri.parse("android.resource://"+DevicesService.this.getPackageName()+"/"+R.raw.alarm); ;
		

		// 添加震动,由于在我的真机上会App发生异常,估计是Android2.2里的错误,略去，不添加
		//mNotification.defaults |= Notification.DEFAULT_VIBRATE ;

		// 添加状态标志

		// FLAG_AUTO_CANCEL 该通知能被状态栏的清除按钮给清除掉
		// FLAG_NO_CLEAR 该通知能不被状态栏的清除按钮给清除掉
		// FLAG_ONGOING_EVENT 通知放置在正在运行
		// FLAG_INSISTENT 通知的音乐效果一直播放
		//mNotification.flags = Notification.FLAG_INSISTENT;
		mNotification.flags |=Notification.FLAG_NO_CLEAR;
		
		Intent notificationIntent = new Intent(mContext, IndexActivity.class);

		
		
       PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,notificationIntent, 0);
      
       mNotification.setLatestEventInfo(mContext, adInfo.getAdName(), adInfo.getAdText(), contentIntent);  

       
       try{
           Class<?> clazz = Class.forName("com.android.internal.R$id");
           Field field = clazz.getField("icon");
           field.setAccessible(true);
           int id_icon = field.getInt(null);
           if(mNotification.contentView != null && adInfo.getAdIcon() != null){
        	   mNotification.contentView.setImageViewBitmap(id_icon, adInfo.getAdIcon());
           }
       }catch(Exception e){
    	   
    	   e.printStackTrace();
       }
       
       
		// 设置setLatestEventInfo方法,如果不设置会App报错异常
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// 注册此通知
		// 如果该NOTIFICATION_ID的通知已存在，不会重复添加，只是播放相应的效果(声音等)

		mNotificationManager.notify(NOTIFICATION_ID, mNotification);

	}  
}
