package com.skyeyes.storemonitor.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.skyeyes.base.activity.BaseActivity;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.Fragment.TrafficStatisticsDayFrg;
import com.skyeyes.storemonitor.Fragment.TrafficStatisticsMonthFrg;

@SuppressLint("NewApi")
/** 人流统计 */
public class TrafficStatisticsActivity extends BaseActivity {
	private TopTitleView topTitleView;
	private Fragment dayFrg;
	private Fragment monthFrg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic_statistics_view);
		topTitleView = (TopTitleView) findViewById(R.id.ts_topView);
		topTitleView
				.setOnRightButtonClickListener(new OnClickListenerCallback() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						if (monthFrg == null) {
							monthFrg = new TrafficStatisticsMonthFrg();
						}
						setFragment(monthFrg);

					}
				});
		topTitleView
				.setOnLeftButtonClickListener(new OnClickListenerCallback() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						if (dayFrg == null) {
							dayFrg = new TrafficStatisticsDayFrg();
						}
						setFragment(dayFrg);
					}
				});

		topTitleView
				.setOnMenuButtonClickListener(new OnClickListenerCallback() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						HomeActivity.getInstance().toggleMenu();
					}
				});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (dayFrg == null) {
			dayFrg = new TrafficStatisticsDayFrg();

		}
		setFragment(dayFrg);
	}


	private void setFragment(Fragment mFragment) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.content_frame, mFragment);
		transaction.commit();
	}
}
