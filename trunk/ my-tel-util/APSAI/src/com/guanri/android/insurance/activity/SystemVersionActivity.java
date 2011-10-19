package com.guanri.android.insurance.activity;

import java.lang.reflect.Field;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.bean.OperateLogBean;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.insurance.service.SystemConfigService;
import com.guanri.android.lib.utils.StringUtils;

/**
 * 系统版本信息界面
 * 
 * @author Administrator
 * 
 */
public class SystemVersionActivity extends ApsaiActivity{
	private TextView  tv_sonftinfo = null ;
	private TextView  tv_verid = null;
	private ImageButton btn_update = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(StringUtils.getStringFromValue(R.string.apsai_system_version));
		setContentView(R.layout.sys_verinfo);
		
		tv_sonftinfo = (TextView)findViewById(R.id.tv_verinfo);
		//tv_verid = (TextView)findViewById(R.id.tv_verid);
		btn_update = (ImageButton)findViewById(R.id.soft_update_btn);
		
		tv_sonftinfo.setText(getSoftVerName());
		//tv_verid.setText(getSoftVer());
		btn_update.setOnClickListener(this);
	}
	
	private String getSoftVer(){
		// 软件版本号
		String version = null;
		try {
			version = String.valueOf(getPackageManager().getPackageInfo(
					"com.guanri.android.insurance", 0).versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	
	private String getSoftVerName(){
		// 软件版本号
		String version = "";
		try {
			version = getPackageManager().getPackageInfo(
					"com.guanri.android.insurance", 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.soft_update_btn:{
			/// 软件升级
			
			}
			break;

		default:
			break;
		}
	}
}
