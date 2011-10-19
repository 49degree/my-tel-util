package com.szxys.mhub.ui.mets.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class PanelScroll extends HorizontalScrollView {
	public PanelScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void fling(int velocityX) {
		return;
	}

	
	
}