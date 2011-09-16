package com.guanri.android.jpos.common;


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
	public final static String POSID = "server_id";
	// 商户编号
	public final static String POSCONTACT = "port";
	// 流水号
	public final static String POSBILLNO = "PosBIllNo";
	// 批次号
	public final static String POSBATCHNO = "PosBatchNo";
	
	
	// 终端设置
	public final static String COMFIG_INFO="Config_info";
	// 服务器信息设置
	public final static String SERVER_INFO="Server_info";
	
	// 上下文
	
	public static boolean getConfigBoolean(String contype,String param){
		return false;
    }
	
	public static void setConfigBoolean(String contype,String param,boolean value){

    }
	
	public static String getConfigString(String contype,String param){
		return null;
    }
	
	public static void setConfigString(String contype,String param,String value){

    }
	
	
}
