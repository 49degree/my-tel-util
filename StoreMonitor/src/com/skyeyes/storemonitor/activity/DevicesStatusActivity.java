package com.skyeyes.storemonitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
/** 设备状态*/
public class DevicesStatusActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TextView view  = new TextView(this);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		view.setTextSize(15);
		view.setText("DevicesStatusActivity");
		setContentView(view);
	}
}
