package com.skyeyes.base.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.skyeyes.base.BaseApplication;

public class ResourceUtil {

	/**
	 * 获取资源文件中的字符
	 * 
	 * @param id
	 * @return
	 */
	public static String getStringFromResource(int id) {
		return getStringFromResource(BaseApplication.getInstance(), id);
	}

	/**
	 * 获取资源文件中的字符
	 * 
	 * @param id
	 * @return
	 */
	public static String getStringFromResource(Context context, int id) {
		if (context.getResources().getString(id) == null)
			return "";
		else
			return context.getResources().getString(id);
	}

	/**
	 * 获取资源文件中的字符
	 * 
	 * @param id
	 * @return
	 */
	public static int getColorFromResource(Context context, int id) {
		return context.getResources().getColor(id);
	}

	public static int getColorFromResource(int id) {
		return BaseApplication.getInstance().getResources().getColor(id);
	}

	/**
	 * 获取DIMEN
	 * 
	 * @param id
	 * @return
	 */
	public static float getDimenFromResource(int id) {
		return BaseApplication.getInstance().getResources().getDimension(id);
	}

	/**
	 * 获取资源文件中的图片
	 * 
	 * @param id
	 * @return
	 */
	public static Drawable getDrawableFromResource(int id) {
		return BaseApplication.getInstance().getResources().getDrawable(id);
	}

}
