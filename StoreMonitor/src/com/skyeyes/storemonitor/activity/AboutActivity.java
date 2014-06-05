package com.skyeyes.storemonitor.activity;

import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class AboutActivity extends Activity {
	private TopTitleView topTitleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_view);
		topTitleView = (TopTitleView) findViewById(R.id.about_topView);
		topTitleView
				.setOnMenuButtonClickListener(new OnClickListenerCallback() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						((HomeActivity) getParent()).backToPreView();
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			((HomeActivity) getParent()).backToPreView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
