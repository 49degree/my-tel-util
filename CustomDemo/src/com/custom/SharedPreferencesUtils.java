package com.custom;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 配置文件管理类
 * @author Administrator
 *
 */
public class SharedPreferencesUtils {
	public final static String NEW_APP_INFO = "NEW_APP_INFO";
	public final static String INSTALLED_APP_INFO = "INSTALLED_APP_INFO";
	public final static String CONFIG_INFO = "CONFIG_INFO";
	
	public final static String INSTALL_TIME = "INSTALL_TIME";
	public final static String WAKEUP_DATE = "WAKEUP_DATE";
	public final static String COMPLETE_DATE = "COMPLETE_DATE";
	public final static String SELF_COMPLETE_TIME= "SELF_COMPLETE_TIME";
	public final static String SELF_COMPLETE_RESULT= "SELF_COMPLETE_RESULT";
	
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
	
	public static Map<String,?> getConfigAll(String contype){
        SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
        return preferences.getAll();
    }
	
	public static void removeConfigAll(String contype){
        SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

	
	public static void setConfigString(String contype,String param,String value){
        SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(param, value);
        editor.commit();
    }
	
}
