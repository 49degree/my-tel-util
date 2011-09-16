package com.guanri.android.jpos.common;

/**
 * 定义服务器交互命令常量
 */
public class CommandConstant {
	

	//终端信息配置文件 
	public static String COMFIG_POS_ID = SharedPreferencesUtils.getConfigString(
			SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.POSID);//终端ID
	public static String COMFIG_POS_CONTACT = SharedPreferencesUtils.getConfigString(
			SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.POSCONTACT);//商户编号
	
	// 人民币代码
	public final static String RMBCODE = "156";
	// 网络信息代码
	public final static String NETMSGCODE = "001";
	
	
	
	// 交易成功
	public final static String SUCCESS = "00";
	// 失败 请联系发卡行
	public final static String FAILEDCARD = "01";
	// 失败 请联系快钱公司
	public final static String FAILEDCAY = "02";
	// 失败 无效商户
	public final static String FAILEDINALIDCONTACT = "03";
	// 失败 无效终端
	public final static String FAILEDINALIDPOS = "04";
	// 
	
	
}

