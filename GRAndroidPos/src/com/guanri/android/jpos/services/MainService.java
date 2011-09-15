package com.guanri.android.jpos.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.guanri.android.jpos.R;

public abstract class MainService extends Service{
    //定义个一个Tag标签   
    private static final String TAG = "MainService"; 
    private NotificationManager manager = null;//(NotificationManager) MainService.this.getSystemService( Context.NOTIFICATION_SERVICE);
    private final static int NOTIFY_ID = 20110913;
    private boolean serviceState = false;
    private static boolean posTaskStart = false;
    
    
	public static boolean isPosTaskStart() {
		return posTaskStart;
	}

    @Override  
    public void onCreate() {  
        Log.e(TAG, "start onCreate~~~:"+serviceState);
        serviceState = true; 
        super.onCreate();  
        posTaskStart = true;
        
        //以下是对Notification的各种参数设定
        int icon=R.drawable.icon;
        String tickerText="POS服务启动通知";
        long when=System.currentTimeMillis();
        Notification nfc=new Notification(icon,tickerText,when);
        Context cxt=getApplicationContext();
        String expandedTitle=this.getClass().getSimpleName()+":POS服务已经启动";
        String expandedText="";
        //intent是非常重要的参数,用来启动你实际想做的事情,设为null后点击状态栏上的Notification就没有任何反应了.
        Intent intent=null;
        PendingIntent nfcIntent=PendingIntent.getActivity(cxt,0,intent,0);
        nfc.setLatestEventInfo(cxt,expandedTitle,expandedText,nfcIntent);
        //发送Notification
        NotificationManager nfcManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nfcManager.notify(1,nfc);
        
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {  
        Log.e(TAG, "start onStart~~~");  
        super.onStart(intent, startId); 
//        manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(R.drawable.icon, 
//        		"POS终端服务已经启动",
//        		System.currentTimeMillis()); 
//        manager.notify(NOTIFY_ID, notification);//发起通知; 
        
        


        
    }  
      
    @Override  
    public void onDestroy() {  
        Log.e(TAG, "start onDestroy~~~");  
        super.onDestroy();  
    }  
      
      
    @Override  
    public boolean onUnbind(Intent intent) {  
        Log.e(TAG, "start onUnbind~~~");  
        return super.onUnbind(intent);  
    }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	} 
}
