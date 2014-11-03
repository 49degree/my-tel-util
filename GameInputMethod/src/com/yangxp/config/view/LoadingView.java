package com.yangxp.config.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.yangxp.ginput.R;

/**
 * 提示信息栏
 * 1. 更好的引导用户使用
 * 2. 提高用户体验,显示操作提示信息
 * */
public class LoadingView 
{
	private final static String TAG = "LoadingView";
	private LayoutInflater mInflater;
	
	private WindowManager.LayoutParams mParams;
	private RelativeLayout mLayout;
	private Context mContext;
	
	//信息布局是否可视
	private boolean mVisible = true;
	
	private WindowManager mWindowManager;

	public LoadingView(Context context) {
		mContext = context;
		//创建
		mInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWindowManager = (WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE);
        
		//创建WindowManager.LayoutParams()对象去设置悬浮窗的参数
		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, 
				PixelFormat.TRANSLUCENT);
		//悬浮窗口不不响应任何时间
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		
        //先将information_bar布局文件实例化
		mLayout = (RelativeLayout)mInflater.inflate(R.layout.loader_view, null);

	}
	/**
	 * 添加提示窗口
	 * 
	 * */
	public void show() {
		mWindowManager.addView(mLayout, mParams);
	}

	/**
	 * 隐藏提示窗口
	 * 
	 * */
	public void hide() {
		mWindowManager.removeView(mLayout);
	}
}