package com.guanri.android.dilog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.guanri.android.jpos.common.SharedPreferencesUtils;
import com.guanri.android.lib.utils.DialogUtils;
import com.guanri.android.lib.utils.NetAddressCheck;

/**
 * 系统配置管理
 * 
 * @author Administrator
 * 
 */
public class SystemConfigService {
	private Context context;
	
	/**
	 * @roseuid 4DF8330D031C
	 */
	public SystemConfigService(Context context) {
		this.context = context;
	}

	/**
	 * @roseuid 4DF819C30222
	 */
	public void setServerInfo() {

	}

	/**
	 * @roseuid 4DF819DA00DA
	 */
	public void setPrinterInfo() {
		
	}



	/**
	 * 输入服务器信息数据检查
	 * @param serverip 
	 * @param serverport
	 * @return
	 */
	public boolean serverInfoValuesCheck(String serverip,String serverport) {
		if(serverip.equals("") || serverip.length() <=0){
			//Toast.makeText(ServerInfoSetActivity.this, "必须输入的服务器IP地址", Toast.LENGTH_LONG).show();
			DialogUtils.showMessageAlertDlg(context, "提示", "必须输入的服务器IP地址", null);
			return false;
		}else if(serverport.equals("") || serverport.length() <=0){
			//Toast.makeText(ServerInfoSetActivity.this, "必须输入的服务器端口号", Toast.LENGTH_LONG).show();
			DialogUtils.showMessageAlertDlg(context, "提示", "必须输入的服务器端口号", null);
			return false;
		}else if(!NetAddressCheck.isValidIPAddress(serverip)){
			//Toast.makeText(ServerInfoSetActivity.this, "输入的IP地址不合法，请重新输入", Toast.LENGTH_LONG).show();
			DialogUtils.showMessageAlertDlg(context, "提示", "输入的IP地址不合法，请重新输入", null);
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 保存服务器信息
	 * @param serverip 
	 * @param serverport
	 */
	public void saveServerInfoValues(String serverip,String serverport) {
		// TODO Auto-generated method stub
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.SERVER_INFO, 
				SharedPreferencesUtils.SERVERIP, 
				serverip);
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.SERVER_INFO, 
				SharedPreferencesUtils.SERVERPORT,  
				serverport);
		
	}
	
	public String getServerInfoIp(){
		String ip = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.SERVER_INFO, 
				SharedPreferencesUtils.SERVERIP);
		return ip;
	}
	
	public String getServerInfoPort(){
		String port = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.SERVER_INFO, 
				SharedPreferencesUtils.SERVERPORT);
		return port;
	}
	



}
