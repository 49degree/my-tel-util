package com.guanri.android.insurance.activity;


import com.guanri.android.insurance.R;
import com.guanri.android.lib.utils.StringUtils;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabWidget;

public class InsuQueryActivity extends TabActivity{
	private TabWidget mTabWidget;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(StringUtils.getStringFromValue(R.string.apsai_inse_query));
		setContentView(R.layout.insu_query_main);
		
		//得到TabHost对象，正对TabActivity的操作通常都有这个对象完成
		


		TabHost tabHost = getTabHost();
		
		

		//生成一个Intent对象，该对象指向一个Activity
		Intent saleIntent = new Intent();
		saleIntent.setClass(this, SaleQueryAcvitity.class);
		//生成一个TabSpec对象，这个对象代表了一个页
		TabHost.TabSpec saleSpec = tabHost.newTabSpec(StringUtils.getStringFromValue(R.string.apsai_sale_query));
		Resources res = getResources();
		//设置该页的indicator
		saleSpec.setIndicator(StringUtils.getStringFromValue(R.string.apsai_sale_query));
		//设置该页的内容
		saleSpec.setContent(saleIntent);
		//将设置好的TabSpec对象添加到TabHost当中
		tabHost.addTab(saleSpec);
		
		
		Intent backIntent = new Intent();
		backIntent.setClass(this, InsuBackQueryAcvitity.class);
		TabHost.TabSpec backSpec = tabHost.newTabSpec(StringUtils.getStringFromValue(R.string.apsai_sale_back_query));
		backSpec.setIndicator(StringUtils.getStringFromValue(R.string.apsai_sale_back_query));
		backSpec.setContent(backIntent);
		tabHost.addTab(backSpec);
		
		Intent uselessIntent = new Intent();
		uselessIntent.setClass(this, InsuUselessQueryAcvitity.class);
		TabHost.TabSpec uselessSpec = tabHost.newTabSpec(StringUtils.getStringFromValue(R.string.apsai_sale_useless_query));
		uselessSpec.setIndicator(StringUtils.getStringFromValue(R.string.apsai_sale_useless_query));
		uselessSpec.setContent(uselessIntent);
		tabHost.addTab(uselessSpec);
		
		mTabWidget = tabHost.getTabWidget();
		for (int i =0; i < mTabWidget.getChildCount(); i++) {  
			mTabWidget.getChildAt(i).getLayoutParams().height = 50;  
		}

		 

	}
}
