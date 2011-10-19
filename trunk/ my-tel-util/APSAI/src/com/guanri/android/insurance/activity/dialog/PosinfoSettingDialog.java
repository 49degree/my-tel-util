package com.guanri.android.insurance.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.SystemConfigActivity;
import com.guanri.android.insurance.service.SystemConfigService;
import com.guanri.android.lib.utils.StringUtils;

/**
 * 终端设置对话框
 * @author wuxiang
 *
 */
public class PosinfoSettingDialog extends Dialog implements
		android.view.View.OnClickListener {
	
	private Button mOkBtn = null;
	private Button mCancelBtn = null;

	private EditText EdtPosUpdatePWD = null;
	private EditText EdtTimeOutLock = null;
	private EditText EdtSIMCoder = null;

	private SystemConfigService systemConfigDAO;
	private Context context;
	

	public PosinfoSettingDialog(Context context,SystemConfigService systemConfigDAO) {
		super(context);
		this.context = context;
		this.systemConfigDAO = systemConfigDAO;
	}

	public void displayDlg() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.sys_posinfo_setting_dialog);// 设置对话框的布局

		mOkBtn = (Button) findViewById(R.id.setting_in);
		mCancelBtn = (Button) findViewById(R.id.setting_out);

		EdtPosUpdatePWD = (EditText) findViewById(R.id.edt_updatepwd);
		EdtTimeOutLock = (EditText) findViewById(R.id.edt_lucktimeout);
		EdtSIMCoder = (EditText)findViewById(R.id.edt_simcord);
		//初始化值
		EdtPosUpdatePWD.setText(systemConfigDAO.getPosInfoUpdatePWD());
		EdtTimeOutLock.setText(systemConfigDAO.getPosInfoLuckTimeOut());
		EdtSIMCoder.setText(systemConfigDAO.getPosInfoSim());
		
		
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		show();// 显示对话框
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.setting_in) {

			String strUpdatePWD = EdtPosUpdatePWD.getText().toString();
			String strTimeOutLock = EdtTimeOutLock.getText().toString();
			String strSIMCoder = EdtSIMCoder.getText().toString();
			
			systemConfigDAO.savePosInfo(strUpdatePWD,strTimeOutLock,strSIMCoder);
			Msgdialog msgdialog = new Msgdialog(context);
			msgdialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));
			msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_insu_manager_data_success));
			msgdialog.setImageid(R.drawable.dialog_success);
			msgdialog.displayDlg();
			dismiss();
			
			
		} else if (v.getId() == R.id.setting_out) {
			dismiss();
		}
	}

}
