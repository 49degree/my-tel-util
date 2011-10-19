/*
 * @(#)MHubActivity.java	1.00 11/05/06
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
package com.szxys.mhub.ui.base;

import com.szxys.mhub.R;
import com.szxys.mhub.ui.main.TestActivity;
import com.szxys.mhub.ui.virtualui.ErrorMessageActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * Activity基类
 * @author xak
 * @version 1.0
 */
public abstract class MHubActivity extends Activity implements OnClickListener {
	
	private static final int ITEM_INTERCOMMUNION = 1;
	private static final int ITEM_ERROR_MESSAGE = 2;
	private static final int ITEM_SYSTEM_SETTING = 3;
	private LinearLayout mMenu;
	private boolean mIsAlwaysShow;
	private int mDisableMenuItem;
	
	/**
	 * 设置MENU一直可见
	 * @param isAlwaysShow	如果为TRUE，则一直可见；否则有MENU键控制
	 */
	public void setMenuAlwaysShow(boolean isAlwaysShow) {
		mIsAlwaysShow = isAlwaysShow;		
	}
	
	/**
	 * 禁用某一菜单子项
	 * @param index	需禁用的菜单子项INDEX
	 */
	public void setDisableMenu(int index) {
		mDisableMenuItem = index;
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if (!mIsAlwaysShow) {
			mMenu.setVisibility(View.INVISIBLE);
		}	
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();	
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mMenu = (LinearLayout) View.inflate(this, R.layout.pfphone_menu_view, null);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addContentView(mMenu,params );
		if (!mIsAlwaysShow) {
			mMenu.setVisibility(View.INVISIBLE);
		}		
		LinearLayout intercommunion = (LinearLayout)mMenu.findViewById(R.id.ll_intercommunion);
		intercommunion.setOnClickListener(this);
		LinearLayout errorMessage = (LinearLayout)mMenu.findViewById(R.id.ll_error_message);
		errorMessage.setOnClickListener(this);
		LinearLayout systemSetting = (LinearLayout)mMenu.findViewById(R.id.ll_system_setting);
		systemSetting.setOnClickListener(this);
		disableMenuItem();
	}	

	/**禁用某一菜单项的具体实现*/
	private void disableMenuItem() {	
		LinearLayout item = null;
		switch (mDisableMenuItem) {
		case ITEM_INTERCOMMUNION:
			item = (LinearLayout)mMenu.findViewById(R.id.ll_intercommunion);		
			break;
		case ITEM_ERROR_MESSAGE:
			item = (LinearLayout)mMenu.findViewById(R.id.ll_error_message);		
			break;
		case ITEM_SYSTEM_SETTING:
			item = (LinearLayout)mMenu.findViewById(R.id.ll_system_setting);			
			break;
		default:
			return;
		}	
		item.setOnClickListener(null);
		item.setBackgroundColor(0xFFCCCCCC);
	}

	@Override
	public void onClick(View v) {		
		switch (v.getId()) {
		case R.id.ll_intercommunion:
			showIntercommunion();
			break;
		case R.id.ll_error_message:
			 showErrorMessage();
			break;
		case R.id.ll_system_setting:
			 setSystemSetting();
			break;

		default:
			break;
		}		
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {		
		if (keyCode == KeyEvent.KEYCODE_MENU && !mIsAlwaysShow) {
			Animation ani;
			if (mMenu.getVisibility() == View.VISIBLE) {
				ani= AnimationUtils.loadAnimation(this, R.anim.hidden);
				mMenu.startAnimation(ani);
				mMenu.setVisibility(View.INVISIBLE);
			}else {
				ani = AnimationUtils.loadAnimation(this, R.anim.pop_up);
				mMenu.startAnimation(ani);
				mMenu.setVisibility(View.VISIBLE);
			}
			return true;
			
		} else {
			return super.onKeyDown(keyCode, event);
		}
		
	}
	
	/**获取当前页面名称(仅用于测试）*/
	protected abstract String getSysName();
	
	/**
	 * 系统设置，当用户点击"系统设置"菜单项时触发（目前实现仅供演示）
	 */
	protected void setSystemSetting(){
		Intent intent = new Intent();		
	    intent.setClass(this,TestActivity.class);
	    intent.putExtra("name", "系统设置页面");
	    intent.putExtra("sysname", getSysName());
	    intent.putExtra("menupage", ITEM_SYSTEM_SETTING);
	    startActivity(intent);
	}
	
	/**
	 * 异常信息，当用户点击"异常信息"菜单项时触发（目前实现仅供演示）
	 */
	protected void showErrorMessage(){
		Intent intent = new Intent();		
	    intent.setClass(this,ErrorMessageActivity.class);
	    intent.putExtra("name", "异常信息页面");
	    intent.putExtra("sysname", getSysName());
	    intent.putExtra("menupage", ITEM_ERROR_MESSAGE);
	    startActivity(intent);	
	};	
	
	/**
	 * 互动消息，当用户点击"互动消息"菜单项时触发（目前实现仅供演示）
	 */
	protected void showIntercommunion(){
		Intent intent = new Intent();		
	    intent.setClass(this,TestActivity.class);
	    intent.putExtra("name", "互动消息息页面");
	    intent.putExtra("sysname", getSysName());
	    intent.putExtra("menupage", ITEM_INTERCOMMUNION);
	    startActivity(intent);
	}
	
}


