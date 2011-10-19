/*
 * @(#)TestActivity.java	1.00 11/05/06
 *
 * Copyright (c) 2011-2013  New Element Inc. 
 * 9/10f, Building 2, Financial Base, No.6 Keyuan Road, 
 * Nanshan District, Shenzhen 518057
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * New Element Medical Equipment Technology Development CO., Ltd 
 * ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with New Element.
 */
package com.szxys.mhub.ui.main;

import java.util.List;

import com.szxys.mhub.R;
import com.szxys.mhub.interfaces.LightUser;
import com.szxys.mhub.ui.base.MHubActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * 相关页面模拟类（仅用于演示）
 * @author xak
 * @version 1.0
 */
public class TestActivity  extends MHubActivity{
	private static final String TAG = "TestActivity";
	private String mParentname;
	private int mPage;
	private String mUserName;
	private int mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_test);
		TextView sysName = (TextView)findViewById(R.id.tv_name);
		String name = getIntent().getStringExtra("name");
		sysName.setText(name);
		String sys =getIntent().getStringExtra("sysname");
		mUserName = getIntent().getStringExtra("username");	
		mUserId = getIntent().getIntExtra("userid",0);	
		Log.v(TAG,"The userName is:" + mUserName + ";and the id is:" + mUserId);
		if (name.startsWith("子") || name.startsWith("主")) {
			mParentname = name;
		} else {
			mParentname = sys;
		}
		if (sys != null) {				
			sysName.append("\n属于" + sys);
		}
		mPage = getIntent().getIntExtra("menupage", -1);
		if(mPage!=-1) {
			setDisableMenu(mPage);
		}
	}
	
	@Override
	protected String getSysName() {
		return mParentname;
	}
	@Override
	protected void onStop() {
		super.onStop();
		if(mPage!=-1) {
			this.finish();
		}
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (!super.onKeyDown(keyCode, event)) {
//			if (keyCode == KeyEvent.KEYCODE_BACK) {			
//				this.finish();
//				return true;
//			} else {
//				return false;
//			}			
//		}else {
//			return false;
//			}
//	}
	
	


	

}
