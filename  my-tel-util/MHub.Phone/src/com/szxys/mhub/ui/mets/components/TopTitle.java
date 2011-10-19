package com.szxys.mhub.ui.mets.components;

import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szxys.mhub.R;

public class TopTitle extends LinearLayout{

	private Context mContext;
	private View view;
	
	public TopTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public TopTitle(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	private void init()
	{
		LayoutInflater inflater  = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.mets_top_title, null);
		this.addView(view);
	}
	
	public void setTitleText(String text)
	{
		TextView tv = (TextView)view.findViewById(R.id.mets_toptitle_tvTopTitle);
		tv.setText(text);
	}
	
}
