package com.guanri.android.insurance.activity;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.OnTabChangeListener;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.activity.dialog.Msgdialog;
import com.guanri.android.insurance.activity.dialog.PosinfoSettingDialog;
import com.guanri.android.insurance.activity.dialog.PrinterSettingDialog;
import com.guanri.android.insurance.activity.dialog.SystemSettingDialog;

import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.service.SystemConfigService;

import com.guanri.android.lib.utils.StringUtils;

/**
 * 系统配置界面
 * 
 * @author Administrator
 * 
 */
public class SystemConfigActivity extends ApsaiActivity{

	private ImageButton imgbtn_server;
	private ImageButton imgbtn_printer;
	private ImageButton imgbtn_posinfo;

	public SystemConfigService systemConfigDAO = new SystemConfigService(SystemConfigActivity.this);

	/**
	 * @roseuid 4DF8330D0399
	 */
	// public SystemConfigActivity()
	// {
	//
	// }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 隐藏系统标题
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		setContentView(R.layout.sys_info_setting);

		init();
	}

	private void init() {
		imgbtn_server = (ImageButton) findViewById(R.id.imgbtn_server);
		imgbtn_printer = (ImageButton) findViewById(R.id.imgbtn_printer);
		imgbtn_posinfo = (ImageButton) findViewById(R.id.imgbtn_posinfo);
		imgbtn_server.setOnClickListener(this);
		imgbtn_printer.setOnClickListener(this);
		imgbtn_posinfo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgbtn_server:{
				new SystemSettingDialog(SystemConfigActivity.this,systemConfigDAO).displayDlg();
			}
			break;
		case R.id.imgbtn_printer:{
				new PrinterSettingDialog(SystemConfigActivity.this,systemConfigDAO).displayDlg();
			}
			break;
		case R.id.imgbtn_posinfo:{
				new PosinfoSettingDialog(SystemConfigActivity.this,systemConfigDAO).displayDlg();
			}
			break;
		default:
			break;
		}
	}

}
