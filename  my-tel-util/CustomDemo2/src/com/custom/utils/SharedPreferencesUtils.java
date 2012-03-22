package com.custom.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 配置文件管理类
 * @author Administrator
 *
 */
public class SharedPreferencesUtils {
	
	//当前第几页
	public final static String CURPAGENUM = "curPageNum";
	
	
	
	//文件名称
	// 终端设置
	public final static String COMFIG_INFO="Config_info";
	// 服务器信息设置
	public final static String SERVER_INFO="Server_info";
	
	// 上下文
	private static Context context = MainApplication.getInstance();

	
	public static boolean getConfigBoolean(String contype,String param){
        SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
        return preferences.getBoolean(param, false);
    }
	
	public static void setConfigBoolean(String contype,String param,boolean value){
        
		SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(param, value);
        editor.commit();
    }
	
	public static String getConfigString(String contype,String param){
        SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
        return preferences.getString(param, "");
    }
	
	public static void setConfigString(String contype,String param,String value){
        SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(param, value);
        editor.commit();
    }
	
}
