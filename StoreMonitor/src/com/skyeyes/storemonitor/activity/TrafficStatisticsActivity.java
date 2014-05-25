package com.skyeyes.storemonitor.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu.CountManuResultBean;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.Fragment.TrafficStatisticsDayFrg;
import com.skyeyes.storemonitor.Fragment.TrafficStatisticsMonthFrg;

@SuppressLint("NewApi")
/** 人流统计 */
public class TrafficStatisticsActivity extends Activity {
	private TopTitleView topTitleView;
	private Fragment dayFrg;
	private Fragment monthFrg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic_statistics_view);
		topTitleView = (TopTitleView) findViewById(R.id.ts_topView);
		topTitleView.setOnRightButtonClickListener(new OnClickListenerCallback() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						if (monthFrg == null) {
							monthFrg = new TrafficStatisticsMonthFrg();
						}
						setFragment(monthFrg);

					}
				});
		topTitleView.setOnLeftButtonClickListener(new OnClickListenerCallback() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						if (dayFrg == null) {
							dayFrg = new TrafficStatisticsDayFrg();
						}
						setFragment(dayFrg);
					}
				});

		topTitleView.setOnMenuButtonClickListener(new OnClickListenerCallback() {
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

	private ArrayList<CountManuResultBean> getDataByDay(String dateStr) {

		Random r = new Random();
		ArrayList<CountManuResultBean> countManuResultBeans = new ArrayList<CountManuResultBean>();
		long beginTimeDate = DateUtil.pareStringDate(dateStr,
				DateUtil.TIME_FORMAT_YMDHMS).getTime();
		for (int i = 1; i <= 24; i++) {
			CountManuResultBean countManuResultBean = new CountManuResultBean();
			countManuResultBean.time = beginTimeDate + i * 60 * 60 * 1000L - 1L;
			countManuResultBean.inManu = 20 + r.nextInt() % 10;
			// countManuResultBean.outManu = 20 + r.nextInt() % 10;
			countManuResultBeans.add(countManuResultBean);
		}

		return countManuResultBeans;
	}

	private ArrayList<CountManuResultBean> getDataByMonth(String dateStr) {

		Random r = new Random();
		ArrayList<CountManuResultBean> countManuResultBeans = new ArrayList<CountManuResultBean>();
		long beginTimeDate = DateUtil.pareStringDate(dateStr,
				DateUtil.TIME_FORMAT_YMDHMS).getTime();
		int days = DateUtil.getDaysOfMonthByDate(new Date(beginTimeDate));
		for (int i = 1; i <= days; i++) {

			CountManuResultBean countManuResultBean = new CountManuResultBean();
			countManuResultBean.time = beginTimeDate + i * 24 * 60 * 60 * 1000L
					- 1L;
			// System.out.println(DateUtil.getTimeStringFormat(countManuResultBean.time,
			// DateUtil.TIME_FORMAT_YMDHMS)+":"+countManuResultBean.time);
			countManuResultBean.inManu = 20 + r.nextInt() % 10;
			countManuResultBean.outManu = 0;
			countManuResultBean.dayofWeet = DateUtil
					.getDayOfWeekByDate(new Date(countManuResultBean.time));
			countManuResultBeans.add(countManuResultBean);
		}

		return countManuResultBeans;
	}

}
