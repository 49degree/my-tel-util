package com.yang.android.tel.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
	private final static String PREFERENCES_NAME="MYTEL_COMFIG";
	
	public static void initConfig(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        //获得SharedPreferences.Editor
        SharedPreferences.Editor editor = preferences.edit();
        //保存组件中的值
        editor.putBoolean("restart_call", false);
        editor.putBoolean("check_num",false);
        editor.putBoolean("shutdown_call", false);
        editor.putBoolean("check_call_state", false);
    }
	
	public static boolean getConfigBoolean(Context context,String param){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        return preferences.getBoolean(param, false);
    }
	
	public static void setConfigBoolean(Context context,String param,boolean value){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(param, value);
        editor.commit();
    }
	
	public static String getConfigString(Context context,String param){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        return preferences.getString(param, "");
    }
	
	public static void setConfigString(Context context,String param,String value){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(param, value);
        editor.commit();
    }
}
