package com.skyeyes.storemonitor.activity;

import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.storemonitor.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class AlermRecorderActivity extends Activity {
	private TopTitleView topTitleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alerm_record_view);
		topTitleView = (TopTitleView)findViewById(R.id.ar_topView);

	}
}
