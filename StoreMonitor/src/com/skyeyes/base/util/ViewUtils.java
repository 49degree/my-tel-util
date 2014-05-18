/**
* Copyright(C)2012-2013 深圳市掌星立意科技有限公司版权所有
* 创 建 人:	Gofeel
* 修 改 人:
* 创 建日期:	2013-7-22
* 描	   述:	视图帮助类
* 版 本 号:	1.0
*/ 
package com.skyeyes.base.util;

import com.skyeyes.storemonitor.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

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
		show(context, charSequence, 0);
	}

	/**
	 * 持续时间为Toast.LENGTH_LONG
	 * 
	 * @param context
	 * @param charSequence
	 */
	public static void showLong(Context context, CharSequence charSequence) {
		show(context, charSequence, 1);
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
	
	
    
   
}
