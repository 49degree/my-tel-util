package com.guanri.android.insurance.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgSureBtn;
import com.guanri.android.insurance.common.NetWorkBlthStateHandler;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.service.InitializeBusinessIdService;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.network.NetWorkTools;
import com.guanri.android.lib.utils.StringUtils;

/**
 * 初始化界面
 */

public class InitBussIdActivity extends ApsaiActivity {
	public static Logger logger = Logger.getLogger(InitBussIdActivity.class);//日志对象
	
	private String mPostID = null;
	private Dialog mDlg = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏系统标题
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView(R.layout.init_ui);
		
		//判断是否已初始化业务ID
		if (isInitBusinessID()) {
			entryLoginActivity();
		} else {
			mDlg = new InitializeRegisterDialog(this);
			((InitializeRegisterDialog) mDlg).displayDlg();
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
		

	}

	public void onRestart(){
		super.onRestart();
	}
	
	public void onStart(){
		super.onStart();
	}	
	
	
	/**
	 * 进入登录界面
	 */
	public void entryLoginActivity() {
		if (mDlg != null) {
			mDlg.dismiss();
		}
		
		Intent i = new Intent(this, LoginAcvitity.class);
		startActivity(i);
		finish();
	}

	/**
	 * 退出应用程序
	 */
	public void exitApp(final Dialog dialog)
	{
		DialogUtils.showChoiceAlertDlg(this,this.getString(R.string.apsai_insu_manager_exit), 
				this.getString(R.string.apsai_insu_manager_exit_msg),new OnAlertDlgSureBtn(){
			public void OnSureBtn(){
				dialog.dismiss();
				finish();
			}
		});
		
	}
	
	
	
	/**
	 * 判断是否已经初始化业务ID
	 * @return
	 */
	private boolean isInitBusinessID() {
		boolean initState = false;
		String strPostCheck = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.COMPSW);
		//终端校验是否已保存
		if (strPostCheck.length() > 0) {
			initState = true;
		}
		return initState;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 初始化注册对话框
	 * @author Administrator
	 *
	 */
	class InitializeRegisterDialog extends Dialog implements OnClickListener {
		private Button mOkBtn = null;
		public InitializeRegisterDialog(Context context) {
			super(context);
		}

		public void displayDlg() {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
			setContentView(R.layout.init_register_dialog);// 设置对话框的布局
			mOkBtn = (Button) findViewById(R.id.init_ok);
			mOkBtn.setOnClickListener(this);
			show();// 显示对话框
		}

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.init_ok) {
				dismiss();
				new InitializeBusinessDialog(InitBussIdActivity.this).displayDlg();
			}
		}
		
		//拦截系统返回键
		public boolean onKeyUp(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
	                && !event.isCanceled()) {
	        	exitApp(this);
	            return true;
	        }
	        return super.onKeyUp(keyCode, event);
	    }
	}

	/**
	 * 初始化业务ID对话框
	 * @author Administrator
	 *
	 */
	class InitializeBusinessDialog extends Dialog implements
			android.view.View.OnClickListener {
		private Button mOkBtn = null;
		private Button mCancel = null;

		private EditText mBusinessIdEdt = null;
		private EditText mReBusinessIdEdt = null;

		public InitializeBusinessDialog(Context context) {
			super(context);
		}

		public void displayDlg() {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

			setContentView(R.layout.init_business_dialog);// 设置对话框的布局
			String strPostId = null;
			mOkBtn = (Button) findViewById(R.id.register_in);
			mCancel = (Button) findViewById(R.id.register_out);
			mBusinessIdEdt = (EditText) findViewById(R.id.business_id);
			mReBusinessIdEdt = (EditText) findViewById(R.id.re_business_id);

			mOkBtn.setOnClickListener(this);
			mCancel.setOnClickListener(this);

			//是否已录入终端ID,如果已经录入则直接显示在编辑框
			strPostId = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.POS_ID);
			if(strPostId != null && strPostId.length() > 0)
			{
				mBusinessIdEdt.setText(strPostId);
				mReBusinessIdEdt.setText(strPostId);
			}
			show();// 显示对话框
		}

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.register_in) {
				String strBusinessID = mBusinessIdEdt.getText().toString();
				String strReBusinessID = mReBusinessIdEdt.getText().toString();
				if(strBusinessID.length() < 8)
				{
					DialogUtils.showErrorAlertDlg(InitBussIdActivity.this,
							StringUtils.getStringFromValue(R.string.apsai_insu_manager_setting_business_id), 
							StringUtils.getStringFromValue(R.string.apsai_insu_manager_error_bussiness_len));
					return;
				}
				
				if (!strBusinessID.equals(strReBusinessID)) {
					DialogUtils.showErrorAlertDlg(InitBussIdActivity.this, 
							StringUtils.getStringFromValue(R.string.apsai_insu_manager_setting_business_id), 
							StringUtils.getStringFromValue(R.string.apsai_insu_manager_error_bussiness_unequal));
					return;
				}

				mPostID = strBusinessID;
				SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO,
						SharedPreferencesUtils.POS_ID, strBusinessID);// 终端ID
				dismiss();
				//进入服务器设置对话框
				new SystemSettingDialog(InitBussIdActivity.this).displayDlg();
			} else if (v.getId() == R.id.register_out) {
				exitApp(this);			
			}
		}
		
		//拦截系统返回键
		public boolean onKeyUp(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()&& !event.isCanceled()) {
	        	exitApp(this);
	            return true;
	        }
	        return super.onKeyUp(keyCode, event);
	    }
	}

	/**
	 *  连接服务器进行业务初始化及密码验证
	 * @author Administrator
	 *
	 */
	class SystemSettingDialog extends Dialog implements
			android.view.View.OnClickListener {
		private Button mOkBtn = null;
		private Button mCancelBtn = null;

		private EditText mServerIpEdt1 = null;
		private EditText mServerIpEdt2 = null;
		private EditText mServerIpEdt3 = null;
		private EditText mServerIpEdt4 = null;
		
		private EditText mServerPortEdt = null;
		private EditText mPostCheckEdt = null;
		private EditText mSimcodeEdt = null;

		public SystemSettingDialog(Context context) {
			super(context);
		}

		public void displayDlg() {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

			setContentView(R.layout.system_setting_dialog);// 设置对话框的布局

			mOkBtn = (Button) findViewById(R.id.setting_in);
			mCancelBtn = (Button) findViewById(R.id.setting_out);

			mServerIpEdt1 = (EditText) findViewById(R.id.edt_serverip1);
			mServerIpEdt2 = (EditText) findViewById(R.id.edt_serverip2);
			mServerIpEdt3 = (EditText) findViewById(R.id.edt_serverip3);
			mServerIpEdt4 = (EditText) findViewById(R.id.edt_serverip4);
			
			mServerPortEdt = (EditText) findViewById(R.id.server_port_id);
			mPostCheckEdt = (EditText) findViewById(R.id.post_check_id);
			mSimcodeEdt = (EditText) findViewById(R.id.sim_card_id);

			mOkBtn.setOnClickListener(this);
			mCancelBtn.setOnClickListener(this);
			show();// 显示对话框
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.setting_in) {
				String strServerIp = null;
				String strServerPort = null;
				String strPostCheck = null;
				String strSimcode = null;

				strServerIp = mServerIpEdt1.getText().toString()+ "." +
				mServerIpEdt2.getText().toString()+"."+
				mServerIpEdt3.getText().toString()+"."+
				mServerIpEdt4.getText().toString();
				
				strServerPort = mServerPortEdt.getText().toString();
				strPostCheck = mPostCheckEdt.getText().toString();
				strSimcode = mSimcodeEdt.getText().toString();
				
				if(strServerIp.length() <= 0 || strServerPort.length() <= 0
						 || strPostCheck.length() <= 0 || strSimcode.length() <= 0)
				{
					DialogUtils.showErrorAlertDlg(InitBussIdActivity.this,StringUtils.getStringFromValue(R.string.apsai_insu_manager_system_setting), 
							StringUtils.getStringFromValue(R.string.apsai_insu_manager_setting_error_server_empty));
					return;
				}
				
				SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.SERVER_INFO,
						SharedPreferencesUtils.SERVERIP, strServerIp);// ip地址
				SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.SERVER_INFO,
						SharedPreferencesUtils.SERVERPORT, strServerPort);// 端口号
				SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.SERVER_INFO,
						SharedPreferencesUtils.CONNTIMEOUT, "120");// 连接超时时间
				InitializeBusinessIdService mInitBusiness = new InitializeBusinessIdService(InitBussIdActivity.this);
				//到服务端进行终端业务ID初始化
				//this.dismiss();
				mInitBusiness.initializeBusinessID(mPostID, strPostCheck,strSimcode,this);

			} else if (v.getId() == R.id.setting_out) {
				exitApp(this);
			}
		}
		
		//拦截系统返回键
		public boolean onKeyUp(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
	                && !event.isCanceled()) {
				dismiss();
				new InitializeBusinessDialog(InitBussIdActivity.this).displayDlg();
	            return true;
	        }
	        return super.onKeyUp(keyCode, event);
	    }
	}
}
