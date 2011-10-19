/*
 * @(#)LoadingActivity.java	1.00 11/05/06
 *
 * Copyright (c) 2011  New Element Inc. 
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.szxys.mhub.R;
import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.app.PlatformGuardService;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.VirtualSubSystem;
import com.szxys.mhub.ui.virtualui.VirtualSystemUISSCallBack;

/**
 * Loading页面
 * 
 * @author xak
 * @version 1.0
 */
public class LoadingActivity extends Activity {

	public static final int UPATE_STATUS = 0;
	protected static final String STATUS = "status";
	public Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_loading);
		// mTvStatus = (TextView) findViewById(R.id.tv_status);
		initView(); // 初始化加载界面
		initDatas(); // 加载数据
	}

	/** 初始化 */
	private void initView() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPATE_STATUS:
					// mTvStatus.setText(msg.arg1);
					startMainActivity();
					break;
				default:
					break;
				}
			}
		};
	}

	/**
	 * 初始化数据（目前实现仅供演示） modify by hsl
	 */
	private void initDatas() {
		// 保留this值目的为平台系统初始化结束后关闭本窗口
		MhubApplication.loadingActivity = this;

		startVirtualSubSystem(); // 启动虚拟子业务

		startGuardService(); // 启动服务

		// 平台系统初始化
		BusinessManager.getIBusinessManagerEx().init();
		// /*
		// * new Thread() {
		// *
		// * @Override public void run() { MhubMessage msg =
		// MhubMessage.obtain();
		// * msg.what = UPATE_STATUS; msg.arg1 = R.string.status_load_data;
		// * mHandler.sendMessage(msg); try { Thread.sleep(1000); } catch
		// * (InterruptedException e) { e.printStackTrace(); } MhubMessage msg1
		// =
		// * MhubMessage.obtain(); msg1.what = UPATE_STATUS; msg1.arg1 =
		// * R.string.status_starting; mHandler.sendMessage(msg1); try {
		// * Thread.sleep(500); } catch (InterruptedException e) {
		// * e.printStackTrace(); } // hsl startMainActivity();
		// *
		// * } }.start();
		// */
	}

	/**
	 * 启动虚拟子业务, add by hsl
	 */
	private void startVirtualSubSystem() {
		BusinessManager.getIBusinessManager().startSubSystem(
				VirtualSubSystem.VSS_USERID, Platform.SUBBIZ_VIRTUAL,
				VirtualSystemUISSCallBack.getInstance());
	}

	/** 启动主页面 */
	private void startMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, UserChooseActivity.class);
		startActivity(intent);
		this.finish();
	}

	// 启动守护服务
	private void startGuardService() {
		PlatformGuardService.init();
	}
}
