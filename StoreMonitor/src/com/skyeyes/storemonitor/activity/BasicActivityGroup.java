package com.skyeyes.storemonitor.activity;

import android.app.ActivityGroup;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

/**
 * ActivityGroup模板类，用于统一管理和消息分发。
 */
@SuppressWarnings("deprecation")
public abstract class BasicActivityGroup extends ActivityGroup {
	private Handler mainHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 默认无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉顶部阴影效果
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        
		mainHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				BasicActivityGroup.this.handleMessage(msg);
				return true;
			}
		});
	}

	protected void handleMessage(Message msg) {

	}

	public Message obtainMessage() {
		return mainHandler.obtainMessage();
	}

	public Message obtainMessage(int what) {
		return mainHandler.obtainMessage(what);
	}

	public Message obtainMessage(int what, Object obj) {
		return mainHandler.obtainMessage(what, obj);
	}

	public Message obtainMessage(int what, int arg1, int arg2) {
		return mainHandler.obtainMessage(what, arg1, arg2);
	}

	public void postDelayed(Runnable r, int delay) {
		mainHandler.postDelayed(r, delay);
	}
}