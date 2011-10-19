package com.guanri.android.insurance.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.activity.dialog.PosinfoSettingDialog;
import com.guanri.android.insurance.activity.dialog.PrinterSettingDialog;
import com.guanri.android.insurance.activity.dialog.SystemSettingDialog;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgSureBtn;
import com.guanri.android.insurance.service.LoginService;
import com.guanri.android.insurance.service.SystemConfigService;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.network.NetWorkTools;
import com.guanri.android.lib.utils.StringUtils;


/**
 * 登录界面
 * @author Administrator
 *
 */
public class LoginAcvitity extends ApsaiActivity{
	Button mLogin_in_btn = null;
	Button mLogin_out_btn = null;
	ImageButton mSetting_printer_btn = null;
	ImageButton mSetting_server_btn = null;
	ImageButton mSetting_post_btn = null;

	EditText mUser_name_edit = null;
	EditText mPassword_edit = null;

	// 登录验证
	LoginService mInitializedDAO = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_main);

		mInitializedDAO = new LoginService(this);

		mLogin_in_btn = (Button) findViewById(R.id.setting_in);
		mLogin_out_btn = (Button) findViewById(R.id.setting_out);
		mSetting_printer_btn = (ImageButton) findViewById(R.id.imgbtn_printer);
		mSetting_server_btn = (ImageButton) findViewById(R.id.imgbtn_server);
		mSetting_post_btn = (ImageButton) findViewById(R.id.imgbtn_posinfo);

		mUser_name_edit = (EditText) findViewById(R.id.user_name_edit);
		mPassword_edit = (EditText) findViewById(R.id.password_edit);
		mUser_name_edit.setText(
				SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO, 
						SharedPreferencesUtils.USERNO));
		mUser_name_edit.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		
		mLogin_in_btn.setOnClickListener(this);
		mLogin_out_btn.setOnClickListener(this);
		mSetting_printer_btn.setOnClickListener(this);
		mSetting_server_btn.setOnClickListener(this);
		mSetting_post_btn.setOnClickListener(this);
		
		try{
			if(!NetWorkTools.checkInternet(this)){//判断网络状态
				DialogUtils.showChoiceAlertDlg(this, "没有可用的网络", "是否开启GPRS或WIFI网络连接", new OnAlertDlgSureBtn(){
					public void OnSureBtn(){
						Intent mIntent = new Intent("/");
						ComponentName comp = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
						mIntent.setComponent(comp);
						mIntent.setAction("android.intent.action.VIEW");
						startActivity(mIntent);
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//mInitializedDAO.dBOperator.release();// 关闭数据库
	}

	@Override
	public void onClick(View v) {
		SystemConfigService systemConfigDAO = new SystemConfigService(LoginAcvitity.this);
		
		switch (v.getId()) {
		case R.id.setting_in: {
			String strUsrName = null;
			String strPassword = null;

			strUsrName = mUser_name_edit.getText().toString();
			strPassword = mPassword_edit.getText().toString();

			if (strUsrName.length() <= 0 || strPassword.length() <= 0) {
				DialogUtils.showErrorAlertDlg(this,
						StringUtils.getStringFromValue(R.string.apsai_insu_manager_user_login),
						StringUtils.getStringFromValue(R.string.apsai_insu_manager_setting_error_server_empty));
				return;
			}

			// 根据输入用户名，密码进入登录验证
			mInitializedDAO.checkOperator(strUsrName, strPassword);

		}
			break;
		case R.id.setting_out:// 退出
			DialogUtils.showChoiceAlertDlg(this,
					StringUtils.getStringFromValue(R.string.apsai_insu_manager_exit),
					StringUtils.getStringFromValue(R.string.apsai_insu_manager_exit_msg),new OnAlertDlgSureBtn(){
				public void OnSureBtn(){
					finish();
				}
			});
			break;
		case R.id.imgbtn_printer:
			new PrinterSettingDialog(LoginAcvitity.this,systemConfigDAO).displayDlg();
			break;
		case R.id.imgbtn_server:
			new SystemSettingDialog(LoginAcvitity.this,systemConfigDAO).displayDlg();
			break;
		case R.id.imgbtn_posinfo:
			new PosinfoSettingDialog(LoginAcvitity.this,systemConfigDAO).displayDlg();
			break;
		default:
			break;

		}

	}

	
	//拦截系统返回键
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
				&& !event.isCanceled()) {

			DialogUtils.showChoiceAlertDlg(this,
					StringUtils.getStringFromValue(R.string.apsai_insu_manager_exit),
					StringUtils.getStringFromValue(R.string.apsai_insu_manager_exit_msg),new OnAlertDlgSureBtn(){
				public void OnSureBtn(){
					
					MainApplication.getInstance().stopNetWorkListen();//停止监听网络情况
					finish();
				}
			});
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}
