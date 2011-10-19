/*
 * @(#)PanelView.java	1.00 11/05/06
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

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.szxys.mhub.R;

/**
 * 子业务ICON面板类
 * @author xak
 * @version 1.0
 */
public class PanelView extends LinearLayout implements OnClickListener{
//	public final static int SUB_COUNT_MAX = 6;
	public final static int SUB_COUNT_MAX = 4;
	public final static int DEFAUT_ID = 0x10001;
//	private final static int[] subId = {R.id.ll_sub1,R.id.ll_sub2,R.id.ll_sub3,R.id.ll_sub4,R.id.ll_sub5,R.id.ll_sub6};
	private final static int[] subId = {R.id.ll_sub1,R.id.ll_sub2,R.id.ll_sub3,R.id.ll_sub4};
	private static final String TAG = "PanelView";
	public static int subCount;
	private MetsMainActivity mContext;
	
	public PanelView(Context context,int num) {
		super(context);
		mContext = (MetsMainActivity)context;
		LinearLayout panel = (LinearLayout) View.inflate(context, R.layout.mets_main_icon_container, null);
		for (int i=0; i<num; i++){
			LinearLayout sub = (LinearLayout)panel.findViewById(subId[i]);	
//			sub.setOnClickListener(this);	
			sub.setId(DEFAUT_ID + subCount);
			((ImageView)sub.findViewById(R.id.img_sub)).setOnClickListener(this);
			subCount++;
		}
		addView(panel,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	@Override
	public void onClick(View v) {
		int id;
		if (v instanceof ImageView) {
			 id= ((View)v.getParent()).getId();				 
		} else {
			id = v.getId();		}
		
		Log.v(TAG, "onClick;The view'id is:" + id);
		mContext.onSubClick(id);
	}

	
}

