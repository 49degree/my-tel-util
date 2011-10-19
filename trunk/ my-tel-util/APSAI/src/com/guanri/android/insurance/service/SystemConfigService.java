package com.guanri.android.insurance.service;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.SystemConfigActivity;
import com.guanri.android.insurance.activity.dialog.Msgdialog;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.lib.utils.NetAddressCheck;
import com.guanri.android.lib.utils.StringUtils;

import android.R.raw;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

	public String getPosInfoSim() {
		String sim = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.COMFIG_INFO,
				SharedPreferencesUtils.SIMCODE);
		return sim;
	}

	public String getPosInfoPosId() {
		String posid = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.COMFIG_INFO,
				SharedPreferencesUtils.POS_ID);
		return posid;
	}

	public String getPosInfoUpdatePWD() {
		String updatepws = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.COMFIG_INFO,
				SharedPreferencesUtils.UPDATAPWD);
		return updatepws;
	}
	public String getPosInfoLuckTimeOut(){
		String LuckTimeOut = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.COMFIG_INFO,
				SharedPreferencesUtils.LUCKTIMEOUT);
		return LuckTimeOut;
	}

	/**
	 * 打印信息参数检查
	 * @param blthadd

	 * @return
	 */
	public boolean PrinterInfoValuesCheck(String blthadd) {
		boolean result = false;
		if (blthadd.length() > 0 && (!blthadd.equals(""))) {
			if (NetAddressCheck.isValidMACAddress(blthadd)) {
				result = true;
			}
		}
		return result;
	}
	/**
	 * 保存打印参数信息
	 * @param blthadd
	 *            蓝牙打印机地址
	 * @param printwidth
	 *            打印纸宽度
	 * @param left
	 *            左边距
	 * @param right
	 *            右边距
	 */
	public void savePrinterInfoValues(String blthadd, String printwidth,
			String left, String right) {
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.PRINTER_INFO,
				SharedPreferencesUtils.PRINTER_ADD, blthadd);
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.PRINTER_INFO,
				SharedPreferencesUtils.PAPER_WIDTH, printwidth);
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.PRINTER_INFO,
				SharedPreferencesUtils.BORDER_LEFT, left);
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.PRINTER_INFO,
				SharedPreferencesUtils.BORDER_RIGHT, right);
	}
	/**
	 * 获得打印机蓝牙地址
	 * @return
	 */
	public String getPrinterInfoBlthAdd(){
		String blthadd = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.PRINTER_INFO,
				SharedPreferencesUtils.PRINTER_ADD);
		return blthadd;
	}
	/**
	 * 获得打印机纸张宽度
	 * @return
	 */
	public String getPrinterInfoWidth(){
		String width = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.PRINTER_INFO,
				SharedPreferencesUtils.PAPER_WIDTH);
		return width;
	}
	/**
	 * 获得打印机左边距
	 * @return
	 */
	public String getPrinterInfoLeft(){
		String left = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.PRINTER_INFO,
				SharedPreferencesUtils.BORDER_LEFT);
		return left;
	}
	/**
	 * 获得打印机右边距
	 * @return
	 */
	public String getPrinterinfoRight(){
		String right = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.PRINTER_INFO,
				SharedPreferencesUtils.BORDER_RIGHT);
		return right;
	}

	/**
	 * 输入服务器信息数据检查
	 * @param serverip 
	 * @param serverport
	 * @return
	 */
	public boolean serverInfoValuesCheck(String serverip,String serverport,String timeout) {
		if(serverip.equals("") || serverip.length() <=0){
			//Toast.makeText(ServerInfoSetActivity.this, "必须输入的服务器IP地址", Toast.LENGTH_LONG).show();
			Msgdialog msgdialog = new Msgdialog(context);
			msgdialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));
			msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_systeminfo_ipaddress_error));
			msgdialog.setImageid(R.drawable.dialog_failure);
			msgdialog.displayDlg();
			return false;
		}else if(serverport.equals("") || serverport.length() <=0){
			//Toast.makeText(ServerInfoSetActivity.this, "必须输入的服务器端口号", Toast.LENGTH_LONG).show();
			Msgdialog msgdialog = new Msgdialog(context);
			msgdialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));
			msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_systeminfo_port_error));
			msgdialog.setImageid(R.drawable.dialog_failure);
			msgdialog.displayDlg();
			return false;
		}else if(!NetAddressCheck.isValidIPAddress(serverip)){
			//Toast.makeText(ServerInfoSetActivity.this, "输入的IP地址不合法，请重新输入", Toast.LENGTH_LONG).show();
			Msgdialog msgdialog = new Msgdialog(context);
			msgdialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));
			msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_systeminfo_ipaddress_error));
			msgdialog.setImageid(R.drawable.dialog_failure);
			msgdialog.displayDlg();
			return false;
		}else if(timeout =="" || timeout.length() <=0 || Integer.valueOf(timeout) <= 0){
			//Toast.makeText(ServerInfoSetActivity.this, "输入的超时时间不合法，请重新输入", Toast.LENGTH_LONG).show();
			Msgdialog msgdialog = new Msgdialog(context);
			msgdialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));
			msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_systeminfo_timeout_error));
			msgdialog.setImageid(R.drawable.dialog_failure);
			msgdialog.displayDlg();
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
	public void saveServerInfoValues(String serverip,String serverport,String conntimeout) {
		// TODO Auto-generated method stub
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.SERVER_INFO, 
				SharedPreferencesUtils.SERVERIP, 
				serverip);
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.SERVER_INFO, 
				SharedPreferencesUtils.SERVERPORT,  
				serverport);
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.SERVER_INFO, 
				SharedPreferencesUtils.CONNTIMEOUT,  
				conntimeout);
		
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
	
	public String getServerTimtOut(){
		String timeout = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.SERVER_INFO, 
				SharedPreferencesUtils.CONNTIMEOUT);
		return timeout;
	}
	/**
	 * 保存终端信息
	 * @param SIMCord SIM卡
	 * @param updatePWD 升级密码
	 * @param lucktime 
	 */
	public void savePosInfo(String SIMCord,String updatePWD,String lucktime){
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.COMFIG_INFO, 
				SharedPreferencesUtils.SIMCODE, 
				SIMCord);
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.COMFIG_INFO, 
				SharedPreferencesUtils.UPDATAPWD,  
				updatePWD);
		SharedPreferencesUtils.setConfigString(
				SharedPreferencesUtils.COMFIG_INFO, 
				SharedPreferencesUtils.LUCKTIMEOUT,  
				lucktime);
	}

}
