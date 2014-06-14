package com.skyeyes.base.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.skyeyes.base.BaseApplication;
import com.skyeyes.storemonitor.R;

/**
 * 通用视图帮助类
 */
public class ViewUtils {
	
    /**
	 * 持续时间为Toast.LENGTH_SHORT
	 * 
	 * @param context
	 * @param charSequence
	 */
	public static void show(Context context, CharSequence charSequence) {
		show(context, charSequence, Toast.LENGTH_SHORT);
	}

	/**
	 * 持续时间为Toast.LENGTH_LONG
	 * 
	 * @param context
	 * @param charSequence
	 */
	public static void showLong(Context context, CharSequence charSequence) {
		show(context, charSequence, Toast.LENGTH_LONG);
	}

	private static void show(Context context, CharSequence charSequence,
			int duration) {
		android.widget.Toast toast = new android.widget.Toast(context);
		View view = View.inflate(context, R.layout.toast, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_toast);
		tv.setText(charSequence);
		toast.setView(view);
		toast.setDuration(duration);
		toast.setGravity(Gravity.BOTTOM, 0, 100);
		toast.show();
	}
	
	public static void showErrorInfo(String errorInfo){
		show(BaseApplication.getInstance(),errorInfo);
	}
    
	public static void showWrongInfo(String wrongInfo){
		show(BaseApplication.getInstance(),wrongInfo);
	}
	
	public static void showNoticeInfo(String noticeInfo){
		show(BaseApplication.getInstance(),noticeInfo);
	} 
}
