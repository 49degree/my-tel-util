/*
 * @(#)MHubMainActivity.java	1.00 11/05/06
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
package com.szxys.mhub.ui.mets.main;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.bizmanager.IBusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.ui.base.MHubActivity;
import com.szxys.mhub.ui.main.TestActivity;
import com.szxys.mhub.ui.mets.MetsSSCallBack;
import com.szxys.mhub.ui.mets.components.VoiceUtil;

/**
 * 平台主程序
 * 
 * @author xak
 * @version 1.0
 */

public class MetsMainActivity extends MHubActivity implements OnTouchListener {
	private static int userId = -1;//用户ID
	private static String userName = "";//用户名称
	IBusinessManager bizManager = BusinessManager.getIBusinessManager();
	MetsSSCallBack metsSSCallBack = new MetsSSCallBack();
	
	//静态变量
	public class MainStatic{
		public final static String BusinessIdString = "BusinessId";
		public final static int getup = 0;
		public final static int sleep = 1;
		
		public final static int DRINK = 2; //饮水量
		public final static int URINARY_URGENCY = 3; //尿急
		public final static int URINARY_INCONTINENCE = 4; //尿失禁
		public final static int URINA_RECORD = 5; //排尿记录
		public final static int URINE = 6; //尿量输入
		public final static int Questionaire = 7; //系统管理
		public final static int SYSTEM_MANAGEMENT = 8; //系统管理

		//public final static int CONFIG_INFO = 7; //配置信息
	}

//	private final static int COUNT_PER_PAGE = 6;
	private final static int COUNT_PER_PAGE = 4;
	private static final String TAG = "MHubMain";
	private PageMarkView mMarkView;
	private int mPageCount;
	private int mSubCount = 9;
	private LinearLayout mPanelCon;
	private float mDensity;
	private int mWidth;
	// private String mUserName;
	private Resources mRes;
	private String mSubPackage = "com.szxys.mhub.ui.mets.activity.";
	private PanelScroll mPanelScreen;
	protected boolean mIsSroll;
	private ArrayList<String> mSubName;
	private ArrayList<String> mSubActName;
	private ArrayList<Integer> mSubImg;
	private ArrayList<Integer> mVoice;
	

	private String mUserName;
	private int mUserId;
	
	
    /** Called when the activity is first created. */
	Button getupButton = null;
	Button sleepButton = null;
	Button drinkButton = null;
	Button urineButton = null;
	Button urinary_urgency_Button = null;
	Button urinary_incontinence_Button = null;
	Button configinfo_Button = null;
	Button urinaRecordButton = null;
	Button system_management_Button = null;
	Button questionaire_Button = null;
	
	Button mhub_test_Button = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mets_main);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;
		mWidth = metrics.widthPixels;
		Log.v(TAG, "the density is:" + mDensity + ";and the w is:" + mWidth);
		//初始化用户ID和用户名称
		userName = getIntent().getStringExtra("username");	
		userId = getIntent().getIntExtra("userid",0);	
		mRes = getResources();
		initButtonInfo();
		initPanel();
		
		bizManager.startSubSystem(userId, Platform.SUBBIZ_METS, metsSSCallBack);
	}


	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
//		 initUser();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			bizManager.stopSubSystem(userId, Platform.SUBBIZ_METS);
			Log.e(TAG, "my onKeyDown");
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if ((event.getAction() == MotionEvent.ACTION_UP)
				&& (v.getId() == R.id.sv_panel_con)) {
			int toX = (new BigDecimal(mPanelScreen.getScrollX() * 1.0f / mWidth))
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue() * mWidth;
			if ((mMarkView.currentPageNum - 1) * mWidth > toX) {
				mMarkView.pre();
			} else if ((mMarkView.currentPageNum - 1) * mWidth < toX) {
				mMarkView.next();
			}
			mPanelScreen.scrollTo(toX, 0);
		}
		return false;
	}
	
	/** 初始功能ICON面板 */
	private void initPanel() {
		mPageCount = (int) Math.ceil(mSubCount *1.0f/COUNT_PER_PAGE); // Math.ceil(9f/4) = 3

		LayoutParams paramFillParent = new LayoutParams(mWidth,
				LayoutParams.FILL_PARENT);
		LinearLayout markCon = (LinearLayout) findViewById(R.id.ll_mark_container);
		mMarkView = new PageMarkView(this, mPageCount);
		markCon.addView(mMarkView);

		// mSubName = getSubName();
		mPanelCon = new LinearLayout(this);
		mPanelCon.setOrientation(LinearLayout.HORIZONTAL);
		
		PanelView panel = null;
		
		for(int i= 1; i<Math.ceil(mSubCount *1.0f/COUNT_PER_PAGE); i++) {
			panel = new PanelView(this, PanelView.SUB_COUNT_MAX);
			mPanelCon.addView(panel, paramFillParent);
		}

		panel = new PanelView(this, mSubCount%COUNT_PER_PAGE);
		mPanelCon.addView(panel, paramFillParent);
		
		//为imagaview 和 textview 设置对应的资源
		for (int i = 0; i < mSubCount; i++) {
			LinearLayout ll = (LinearLayout) mPanelCon
					.findViewById(PanelView.DEFAUT_ID + i);
			Log.v(TAG, "The i is:" + i);
			((ImageView) ll.findViewById(R.id.img_sub))
					.setBackgroundResource(mSubImg.get(i));
			((TextView) ll.findViewById(R.id.tv_sub)).setText(mSubName.get(i));
		}
		PanelView.subCount = 0;
		mPanelScreen = (PanelScroll) findViewById(R.id.sv_panel_con);
		mPanelScreen.addView(mPanelCon, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		mPanelScreen.setOnTouchListener(this);
	}
	
	
	 /**用户初始化，只有超过一个用户的时候才显示用户选择面板*/
	private void initButtonInfo() {
		//功能名称
		mSubName = new ArrayList<String>(9);
		mSubName.add(getString(R.string.mets_getup_button_text));
		mSubName.add(getString(R.string.mets_sleep_button_text));
		mSubName.add(getString(R.string.mets_drink_button_text));
		mSubName.add(getString(R.string.mets_urinary_urgency_button_text));
		mSubName.add(getString(R.string.mets_urinary_incontinence_button_text));
		mSubName.add(getString(R.string.mets_urina_record_button_text));
		mSubName.add(getString(R.string.mets_urine_button_text));
		mSubName.add(getString(R.string.mets_questionnaire_button_text));
		mSubName.add(getString(R.string.mets_system_management_button_text));
		//各功能对应的类
		mSubActName = new ArrayList<String>(9);
		mSubActName.add("GetupSleepActivity");
		mSubActName.add("GetupSleepActivity");
		mSubActName.add("DrinkUrineActivity");
		mSubActName.add("DrinkUrineActivity");
		mSubActName.add("DrinkUrineActivity");
		mSubActName.add("UrineRecordCountActivity");
		mSubActName.add("DrinkUrineActivity");
		mSubActName.add("QuestionnaireActivity");
		mSubActName.add("SystemManagementVerifyActivity");

		//各功能对应的图标
		mSubImg = new ArrayList<Integer>(9);
		mSubImg.add(R.drawable.mets_style_main_getup);
		mSubImg.add(R.drawable.mets_style_main_sleep);
		mSubImg.add(R.drawable.mets_style_main_drink);
		mSubImg.add(R.drawable.mets_style_main_urinary_urgency);
		mSubImg.add(R.drawable.mets_style_main_urinary_incontinence);
		mSubImg.add(R.drawable.mets_style_main_record);
		mSubImg.add(R.drawable.mets_style_main_urine);
		mSubImg.add(R.drawable.mets_style_main_questionnaire);
		mSubImg.add(R.drawable.icon);

		mVoice = new ArrayList<Integer>(9);
		mVoice.add(R.raw.mets_getup);
		mVoice.add(R.raw.mets_sleep);
		mVoice.add(R.raw.mets_drink);
		mVoice.add(R.raw.mets_urinary_urgency);
		mVoice.add(R.raw.mets_urinary_incontinence);
		mVoice.add(R.raw.mets_sleep);
		mVoice.add(R.raw.mets_urine);
		mVoice.add(R.raw.mets_sleep);
		mVoice.add(R.raw.mets_sleep);
	}

	/** 页面切换标识控件类 */
	private class PageMarkView extends LinearLayout {
		protected int pageCount;
		protected int currentPageNum;
		private final Context context;

		public PageMarkView(Context context, int count) {
			super(context);
			this.context = context;
			pageCount = count;
			// currentPageNum = (int) Math.ceil(count*1.0f/2);
			currentPageNum = 1;
			initView();
		}

		private void initView() {
			removeAllViews();
			for (int i = 1; i <= pageCount; i++) {
				if (i == currentPageNum) {
					ImageView markCurrentPage = new ImageView(context);
					markCurrentPage
							.setImageResource(R.drawable.mets_mark_page_current);
					addView(markCurrentPage);
				} else {
					ImageView markHiddenPage = new ImageView(context);
					markHiddenPage
							.setImageResource(R.drawable.mets_mark_page_hidden);
					addView(markHiddenPage);
				}
			}
		}

		/** 下一页 */
		protected void next() {
			if (currentPageNum < pageCount) {
				currentPageNum++;
			}
			initView();
		}

		/** 上一页 */
		protected void pre() {
			if (currentPageNum > 1) {
				currentPageNum--;
			}
			initView();
		}
	}

	/** 当用户点击子业务ICON是触发 */
	public void onSubClick(int viewId) {
		Intent intent = new Intent();
		try {
			@SuppressWarnings("rawtypes")
			int index = viewId - PanelView.DEFAUT_ID;
			Class sub = Class.forName(mSubPackage +mSubActName.get(index));
			intent.setClass(MetsMainActivity.this, sub);
			VoiceUtil.playVoice(this, mVoice.get(index));
			intent.putExtra(MetsMainActivity.MainStatic.BusinessIdString, index);
			startActivity(intent);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 获取用户ID
	 * @return
	 */
	public static int getUserId(){
		return userId;
	}
	/**
	 * 获取用户名称
	 * @return
	 */
	public static String getUserName(){
		return userName;
	}
	
	protected String getSysName() {
		return "主页面";
	}
	
	/**
	 * 系统设置，覆盖父类方法
	 * 
	 */
	@Override
	protected void setSystemSetting(){
		Log.e(TAG, "my setSystemSetting");
	}
	
}