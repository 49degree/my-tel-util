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
package com.szxys.mhub.ui.main;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.app.PlatformGuardService;
import com.szxys.mhub.interfaces.LightUser;
import com.szxys.mhub.ui.base.MHubActivity;

/**
 * 平台主程序
 * 
 * @author xak
 * @version 1.0
 */

public class MHubMainActivity extends MHubActivity implements OnTouchListener {
	private final static int COUNT_PER_PAGE = 4;
	private static final String TAG = "MHubMain";
	private PageMarkView mMarkView;
	private int mPageCount;
	private int mSubCount;
	private LinearLayout mPanelCon;
	private float mDensity;
	private int mWidth;
	// private String mUserName;
	private Resources mRes;
	private String mSubPackage;
	private PanelScroll mPanelScreen;
	protected boolean mIsSroll;
	private ArrayList<String> mSubName;
	private ArrayList<String> mSubActName;
	private ArrayList<Integer> mSubImg;
	// add by hsl //
	private boolean mIsBound;
	private PlatformGuardService mBoundService;
	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((PlatformGuardService.LocalBinder) service)
					.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBoundService = null;
		}
	};
	private String mUserName;
	private int mUserId;

	// 绑定主服务，使用主服务作为本软件的守护进程
	private void bindGuardService() {
		bindService(new Intent(getApplicationContext(),
				PlatformGuardService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	// 解除对主服务的绑定
	private void unbindGuardService() {
		if (true == mIsBound) {
			mIsBound = false;
			unbindService(mConnection);
		}
	}

	// add by hsl end //

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_main);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;
		mWidth = metrics.widthPixels;
		Log.v(TAG, "the density is:" + mDensity + ";and the w is:" + mWidth);
		mUserName = getIntent().getStringExtra("username");	
		mUserId = getIntent().getIntExtra("userid",0);	
		mRes = getResources();
		setMenuAlwaysShow(true);
		initPanel();

		bindGuardService();
	}

	/** 初始化子业务ICON面板 */
	private void initPanel() {
		getSystemConfig();
		LayoutParams paramFillParent = new LayoutParams(mWidth,
				LayoutParams.FILL_PARENT);
		LinearLayout markCon = (LinearLayout) findViewById(R.id.ll_mark_container);
		mMarkView = new PageMarkView(this, mPageCount);
		markCon.addView(mMarkView);

		// mSubName = getSubName();
		mPanelCon = new LinearLayout(this);

		mPanelCon.setOrientation(LinearLayout.HORIZONTAL);
		for (int i = 0; i < mPageCount - 1; i++) {
			PanelView panel = new PanelView(this, PanelView.SUB_COUNT_MAX);
			mPanelCon.addView(panel, paramFillParent);
		}
		PanelView panel = new PanelView(this, mSubCount
				% PanelView.SUB_COUNT_MAX != 0 ? mSubCount
				% PanelView.SUB_COUNT_MAX : PanelView.SUB_COUNT_MAX);
		mPanelCon.addView(panel, paramFillParent);
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

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		// initUser();
	}

	// /**用户初始化，只有超过一个用户的时候才显示用户选择面板*/
	// private void initUser() {
	// final String[] userName = getUserNames();
	// if (userName.length>1) {
	// final LinearLayout userPanel = (LinearLayout) View.inflate(this,
	// R.layout.pfphone_user_choose, null);
	// userPanel.setOnTouchListener(this);
	// ListView lstUser = (ListView)userPanel.findViewById(R.id.lv_user);
	// lstUser.setAdapter(new ArrayAdapter<String>(this,
	// R.layout.pfphone_user_list_item,userName));
	// lstUser.setOnItemClickListener(new OnItemClickListener (){
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id) {
	// userPanel.setVisibility(View.GONE);
	// }
	// });
	//
	// addContentView(userPanel, new LayoutParams(LayoutParams.FILL_PARENT,
	// LayoutParams.FILL_PARENT));
	//
	//
	// }
	// }

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
							.setImageResource(R.drawable.pfphone_mark_page_current);
					addView(markCurrentPage);
				} else {
					ImageView markHiddenPage = new ImageView(context);
					markHiddenPage
							.setImageResource(R.drawable.pfphone_mark_page_hidden);
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
			Class sub = Class.forName(mSubPackage +mSubActName.get(viewId - PanelView.DEFAUT_ID));
			intent.setClass(MHubMainActivity.this, sub);
			intent.putExtra("name", mSubName.get(viewId - PanelView.DEFAUT_ID));
			intent.putExtra("userid", mUserId);	
			intent.putExtra("username", mUserName);	
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getSysName() {
		return "主页面";
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

	private void getSystemConfig() {
		try {
			mSubName = new ArrayList<String>();
			mSubImg = new ArrayList<Integer>();
			mSubActName = new ArrayList<String>();
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			InputStream input = mRes.getAssets().open(
					"subsystem_info.xml");
			xpp.setInput(input, "UTF-8");
			int eventType = xpp.getEventType();
			Class drawClass = R.drawable.class;
			Class strClass = R.string.class;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					Log.v(TAG, "The name is:" + xpp.getName());
					if (xpp.getName().equals("subsystem")) {
						mSubName.add(mRes.getString(strClass.getField(xpp.getAttributeValue(null, "name")).getInt(xpp.getAttributeValue(null, "name"))));
						mSubImg.add(drawClass.getField(xpp.getAttributeValue(null, "img_name")).getInt(xpp.getAttributeValue(null, "img_name")));
						mSubActName.add(xpp.getAttributeValue(null, "activity_name"));
						mSubCount++;
					} else if (xpp.getName().equals("system")) {
						mSubPackage = xpp.getAttributeValue(null, "package");
					}
				}
				mPageCount = (int) Math.ceil(mSubCount *1.0f/COUNT_PER_PAGE);
				eventType = xpp.next();
			}		
	
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				showCloseDiag();
				return true;
			}
			return super.onKeyDown(keyCode, event);

	}

	/** 程序关闭时调用 */
	private void onClose() {
		Log.v(TAG, "onclose");		
		unbindGuardService();
		mBoundService.stopSelf();
		this.finish();
		
		System.exit(0);
	}

	private void showCloseDiag() {
		 DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				if (which == DialogInterface.BUTTON_POSITIVE) {
					onClose();
				} else {
					return;
				}
			}
		 };
		new AlertDialog.Builder(this).setTitle(mRes.getString(R.string.quit))
              .setMessage(mRes.getString(R.string.quit_msg))
              .setPositiveButton(mRes.getString(R.string.ok), onClickListener)	
              .setNegativeButton(mRes.getString(R.string.cancel), onClickListener )
              .show();
		
	}
}