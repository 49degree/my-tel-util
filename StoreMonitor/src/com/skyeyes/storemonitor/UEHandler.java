package com.skyeyes.storemonitor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.skyeyes.storemonitor.service.DevicesService;

/**
 * 全局异常处理类
 * 
 * @author linyun.zheng
 * 
 */
public class UEHandler implements Thread.UncaughtExceptionHandler {
	private Context context;
	

	public UEHandler(Context app) {
		context = app;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		Log.e("UEHandler",ex.getMessage());

		try{
			context.stopService(new Intent(context,DevicesService.class));
		}catch(Exception e){
			
		}

		//System.exit(0);
	}
}
