package com.guanri.android.jpos.common;

import com.guanri.android.lib.context.MainApplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 配置文件管理类
 * @author Administrator
 *
 */
public class SharedPreferencesUtils {
	
	// 服务器IP地址
	public final static String SERVERIP = "Server_id";
	// 服务器端口号
	public final static String SERVERPORT = "Server_port";
	// 终端初始化状态
	public final static String SERVERCONNTIMEOUT = "ServerConnTimeOut";
	// 连接超时时间
	public final static String SERVERREADTIMEOUT = "ServerReadTImeOUt";
	
	
	// 终端ID
	public final static String POSID = "Pos_ID";
	// 商户编号
	public final static String POSMERCHANT  = "Pos_Merchant";
	// 商户名称
	public final static String POSMERCHANTNAME = "Pos_MerchantName";
	// 批次号
	public final static String POSBATCHNO = "PosBatchNo";
	
	
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
