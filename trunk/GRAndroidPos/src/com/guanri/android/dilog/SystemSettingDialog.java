package com.guanri.android.dilog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.jpos.R;
import com.guanri.android.lib.utils.DialogUtils;

/**
 * 服务器设置对话框
 * @author wuxiang
 *
 */
public class SystemSettingDialog extends Dialog 
		implements android.view.View.OnClickListener  {

	private Button mOkBtn = null;
	private Button mCancelBtn = null;

	private EditText mServerIpEdt1 = null;
	private EditText mServerIpEdt2 = null;
	private EditText mServerIpEdt3 = null;
	private EditText mServerIpEdt4 = null;
	private EditText mServerPortEdt = null;
	
	private SystemConfigService systemConfig;
	private Context context;
	
	private String serverip = "";
	public SystemSettingDialog(Context context) {
		super(context);
		this.context = context;
		this.systemConfig = new SystemConfigService(context);
	}

	public void displayDlg() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.sys_server_setting_dialog);// 设置对话框的布局

		mOkBtn = (Button) findViewById(R.id.setting_in);
		mCancelBtn = (Button) findViewById(R.id.setting_out);

		mServerIpEdt1 = (EditText) findViewById(R.id.edt_serverip1);
		mServerIpEdt2 = (EditText) findViewById(R.id.edt_serverip2);
		mServerIpEdt3 = (EditText) findViewById(R.id.edt_serverip3);
		mServerIpEdt4 = (EditText) findViewById(R.id.edt_serverip4);
		
		mServerPortEdt = (EditText) findViewById(R.id.edt_serverport);
		// 初始化值
		serverip = systemConfig.getServerInfoIp();
		
		int i = serverip.indexOf(".");
		String tmpstr = serverip.substring(0, i);
		mServerIpEdt1.setText(tmpstr);
		
		serverip = serverip.substring(i + 1);
		i = serverip.indexOf(".");
		tmpstr = serverip.substring(0, i);
		mServerIpEdt2.setText(tmpstr);
		
		serverip = serverip.substring(i + 1);
		i = serverip.indexOf(".");
		tmpstr = serverip.substring(0, i);
		mServerIpEdt3.setText(tmpstr);
		
		serverip = serverip.substring(i + 1);
		mServerIpEdt4.setText(serverip);
		
		mServerPortEdt.setText(systemConfig.getServerInfoPort());
		
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		show();// 显示对话框
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.setting_in) {
			String strServerIp = mServerIpEdt1.getText().toString()+ "."+
			mServerIpEdt2.getText().toString()+"."+
			mServerIpEdt3.getText().toString()+"."+
			mServerIpEdt4.getText().toString();
			String strServerPort = mServerPortEdt.getText().toString();
			//合法性检查
			if(systemConfig.serverInfoValuesCheck(strServerIp, strServerPort)){
				systemConfig.saveServerInfoValues(strServerIp, strServerPort);
				dismiss();
			}		
			DialogUtils.showMessageAlertDlg(context, "提示", "服务器信息修改成功", null);
		} else if (v.getId() == R.id.setting_out) {
			dismiss();
		}
	}
}
