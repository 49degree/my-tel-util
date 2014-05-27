package com.skyeyes.base.util;

/**
 * 配置文件管理类
 * @author Administrator
 *
 */

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.skyeyes.base.BaseApplication;

public class PreferenceUtil {
	public final static String ACCOUNT_IFNO = "account_info_preferences";   //账户Preference
	public final static String SYSCONFIG = "sysconfig_preferences";              //系统配置Preference
	public final static String DEVICE_INFO = "device_info_preferences";           
	
	
	public final static String account_login_id = "login_id";
	public final static String account_login_name = "login_name";
	public final static String account_login_psd = "login_psd";
	
	public final static String sysconfig_server_ip = "server_ip";
	public final static String sysconfig_server_port = "server_port";
	
	public final static String device_count = "device_count";
	public final static String device_code_list = "device_code_list";
	public final static String device_current_code = "device_current_code";
	public final static String device_door_query_last_time = "device_door_query_last_time";
	public final static String device_alarm_query_last_time = "device_alarm_query_last_time";
	
	
	public static Context context = BaseApplication.getInstance();
	
	/**
	 * 保存配置信息(单条记录)
	 * @param confType
	 * @param paramType
	 * @param paramName
	 * @param paramValue
	 */
	public static void setSingleConfigInfo(String confType, String paramName, Object paramValue) {
		setSingleConfigInfo(confType, paramName, paramValue, Activity.MODE_PRIVATE);
	}
	
	/**
	 * 保存配置信息(单条记录)
	 * @param confType
	 * @param paramType
	 * @param paramName
	 * @param paramValue
	 * @param accessType
	 */
	public static void setSingleConfigInfo(String confType, String paramName, Object paramValue, int accessType) {
		SharedPreferences preferences = context.getSharedPreferences(confType, accessType);
		SharedPreferences.Editor editor = preferences.edit();
		if (paramValue instanceof Boolean) {
			 editor.putBoolean(paramName, (Boolean)paramValue);
		} else if (paramValue instanceof String) {
			editor.putString(paramName, (String)paramValue);
		} else if (paramValue instanceof Long) {
			editor.putLong(paramName, (Long)paramValue);
		} else if (paramValue instanceof Float) {
			editor.putFloat(paramName, (Float)paramValue);
		} else if (paramValue instanceof Set){
		    editor.putStringSet(paramName, (Set<String>)paramValue);
		}else {
			editor.putInt(paramName, (Integer)paramValue);
		} 
		
		editor.commit();
	}
	
	/**
	 * 保存配置信息(多条记录)
	 * @param confType
	 * @param paramType
	 * @param paramName
	 * @param paramValue
	 */
	public static boolean setMultiConfigInfo(String confType, Map<String, Object> params) {
		return setMultiConfigInfo(confType, params, Activity.MODE_PRIVATE);
	}
	
	/**
	 * 保存配置信息(多条记录)
	 * @param confType
	 * @param params
	 * @param accessType
	 * @return
	 */
	public static boolean setMultiConfigInfo(String confType, Map<String, Object> params, int accessType) {
		SharedPreferences preferences = context.getSharedPreferences(confType, accessType);
		SharedPreferences.Editor editor = preferences.edit();
		Iterator<String> keys = params.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Object value = params.get(key);
			if (value instanceof Boolean) {
				 editor.putBoolean(key, (Boolean)value);
			} else if (value instanceof String) {
				editor.putString(key, (String)value);
			} else if (value instanceof Long) {
				editor.putLong(key, (Long)value);
			} else if (value instanceof Float) {
				editor.putFloat(key, (Float)value);
			} else {
				editor.putInt(key, (Integer)value);
			}
		}
		return editor.commit();
	}
	
	/**
	 * 获取BOOLEAN类型数据
	 * @param confType
	 * @param paramName
	 * @return
	 */
	public static boolean getConfigBoolean(String confType,String paramName){
        return getConfigBoolean(confType, paramName, Activity.MODE_PRIVATE);
    }
	
	public static boolean getConfigBoolean(String confType,String paramName, int accessType){
        SharedPreferences preferences = context.getSharedPreferences(confType, accessType);
        return preferences.getBoolean(paramName, false);
    }
	
	/**
	 * 获取String类型数据
	 * @param confType
	 * @param paramName
	 * @return
	 */
	public static String getConfigString(String confType,String paramName){
        return getConfigString(confType, paramName, Activity.MODE_PRIVATE);
    }
	
	public static String getConfigString(String confType,String paramName, int accessType){
        SharedPreferences preferences = context.getSharedPreferences(confType, accessType);
        return preferences.getString(paramName, "");
    }
	
	/**
	 * 获取Long类型数据
	 * @param confType
	 * @param paramName
	 * @return
	 */
	public static Long getConfigLong(String confType,String paramName){
        return getConfigLong(confType, paramName, Activity.MODE_PRIVATE);
    }
	
	public static Long getConfigLong(String confType,String paramName, int accessType){
        SharedPreferences preferences = context.getSharedPreferences(confType, accessType);
        return preferences.getLong(paramName, 0);
    }
	
	/**
	 * 获取Int类型数据
	 * @param confType
	 * @param paramName
	 * @return
	 */
	public static int getConfigInt(String confType,String paramName){
        return getConfigInt(confType, paramName, Activity.MODE_PRIVATE);
    }
	
	public static int getConfigInt(String confType,String paramName, int accessType){
        SharedPreferences preferences = context.getSharedPreferences(confType, accessType);
        return preferences.getInt(paramName, 0);
    }
	
	/**
	 * 获取Float类型数据
	 * @param confType
	 * @param paramName
	 * @return
	 */
	public static Float getConfigFloat(String confType,String paramName){
        return getConfigFloat(confType, paramName, Activity.MODE_PRIVATE);
    }
	
	public static Float getConfigFloat(String confType,String paramName, int accessType){
        SharedPreferences preferences = context.getSharedPreferences(confType, accessType);
        return preferences.getFloat(paramName, 0);
    }
	
	/**
	 * 删除某一项
	 * @param confType
	 * @param key
	 * @return
	 */
	public static boolean deleteConfig(String confType,String key) {
    	return deleteConfig(confType, key, Activity.MODE_PRIVATE);
	}
	
	/**
	 * 删除某一项
	 * @param confType
	 * @param key
	 * @param accessType
	 * @return
	 */
	public static boolean deleteConfig(String confType,String key, int accessType) {
		SharedPreferences preferences = getSharedPreferences(confType, accessType);
		Editor edit = preferences.edit();
		edit.remove(key);
		return edit.commit();
	}
	
	/**
	 * 获取SharedPreferences 
	 * @param contype
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(String contype){
		return context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
	}
	
	public static SharedPreferences getSharedPreferences(String contype, int accessType){
		return context.getSharedPreferences(contype, accessType);
	}
}
