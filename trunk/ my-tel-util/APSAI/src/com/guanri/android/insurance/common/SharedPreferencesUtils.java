package com.guanri.android.insurance.common;

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
	public final static String SERVERIP = "server_id";
	// 服务器端口号
	public final static String SERVERPORT = "port";
	// 终端初始化状态
	public final static String INITSTATE = "InitState";
	// 连接超时时间
	public final static String CONNTIMEOUT = "connect_timeout";
	
	// 终端ID
	public final static String POS_ID ="Pos_id";
    // 终端初始化密码
	public final static String INITPSW = "InitPSW";
    // 终端校验码
	public final static String COMPSW = "ComPSW";
	// SIM卡号码
	public final static String SIMCODE = "SIMCode";
	// 分公司代码
	public final static String BRANCHID = "BranchID";
	// 命令序列
	public final static String COM_SEQ = "ComSeq";
	// 当前批次号
	public final static String CHECK_ID = "CheckId";
	// 上次登录的用户名
	public final static String USERNO = "UserNo";
	
	// 本终端所处的站点的名称
	public final static String STATIONAME = "StationName";
	// 后台短信的接入号码，通过这个后台号码发送的信息，终端会解析显示出来
	public final static String SMSID = "SMSID";
	// 升级密码
	public final static String UPDATAPWD = "UpdataPWD";
	// 终端超时锁定时间
	public final static String LUCKTIMEOUT = "LuckTimeOut";

	
	
	//  打印机蓝牙地址
	public final static String PRINTER_ADD = "printer_add";
	// 打印纸宽度（毫米）
	public final static String PAPER_WIDTH = "paper_width";
    // 左边距（毫米）
	public final static String BORDER_LEFT = "BORDER_LEFT";
	// 右边距（毫米）
	public final static String BORDER_RIGHT = "BORDER_RIGHT";
	
	// 终端设置
	public final static String COMFIG_INFO="Config_info";
	// 服务器信息设置
	public final static String SERVER_INFO="Server_info";
	// 打印机设置
	public final static String PRINTER_INFO="Printer_info";
	// 当前登录的用户ID
	public final static String LOGINUSER = "login_user";
	
	
	
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
	
//	public static int getConfigInt(String contype,String param){
//        SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
//        return preferences.getInt(param, 0);
//    }
	
	public static void setConfigString(String contype,String param,String value){
        SharedPreferences preferences = context.getSharedPreferences(contype, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(param, value);
        editor.commit();
    }
	
	
}
