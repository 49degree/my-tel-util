package com.skyeyes.storemonitor.activity;

import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;

import android.app.Activity;
import android.os.Bundle;

public class DoorRecordActivity extends Activity {

	private TopTitleView topTitleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.door_record_view);
		topTitleView = (TopTitleView)findViewById(R.id.dr_topView);
		topTitleView
		.setOnMenuButtonClickListener(new OnClickListenerCallback() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				HomeActivity.getInstance().toggleMenu();

			}
		});
	}


}
