package com.skyeyes.storemonitor.Fragment;

import java.util.ArrayList;
import java.util.Calendar;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.XYChart;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu.CountManuResultBean;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgHourManuByDay;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.Log;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.process.impl.CountManuCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

@SuppressLint("NewApi")
public class TrafficStatisticsDayFrg extends SuperFragment implements
		OnClickListener {
	private View mItemView;
	private TextView averageTime;
	private TextView total;
	private LinearLayout layout;
	private GraphicalView mView;
	private Button timeBtn;
	private Button lastDayIbtn;
	private Button nextDayIbtn;
	private XYChart chart;
	public Calendar mCurCalendar = Calendar.getInstance(); // 当前的时间

	private final int MONTH_VIEW_REFLESH = 1;
	// 测试数据
	private ArrayList<CountManuResultBean> monthResultBeans;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MONTH_VIEW_REFLESH:
				layout.removeAllViews();
				xyMultipleSeriesRenderer.setYAxisMax(20.0);
				xyMultipleSeriesRenderer.setPanLimits(new double[] { -1,
						monthResultBeans.size(), 0, 80 }); // 限制xy轴的长度

				chart = new LineChart(getDemoDataset(monthResultBeans),
						xyMultipleSeriesRenderer);
				mView = new GraphicalView(getActivity(), chart);
				averageTime
						.setText(String.valueOf(monthResultBeans.get(0).avgTime));
				total.setText(String.valueOf(monthResultBeans.get(0).inManu));
				layout.addView(mView);
				break;

			default:
				break;
			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		mItemView = inflater.inflate(R.layout.traffic_statistivs_day_frg_view,
				container, false);
		layout = (LinearLayout) mItemView.findViewById(R.id.my_chart);
		total = (TextView) mItemView.findViewById(R.id.total_values);
		averageTime = (TextView) mItemView
				.findViewById(R.id.average_time_value);
		lastDayIbtn = (Button) mItemView.findViewById(R.id.last_day_ibtn);
		nextDayIbtn = (Button) mItemView.findViewById(R.id.next_day_ibtn);
		timeBtn = (Button) mItemView.findViewById(R.id.search_date_bt);
		timeBtn.setText(DateUtil
				.getTimeStringFormat(mCurCalendar, "yyyy-MM-dd"));

		lastDayIbtn.setOnClickListener(this);
		nextDayIbtn.setOnClickListener(this);
		timeBtn.setOnClickListener(this);
		return mItemView;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getManucountByDay("2014-05-17 00:00:00");
	}

	/**
	 * 按日统计人流
	 * 
	 * @param dayTime
	 *            如：2014-05-01 00:00:00
	 */
	private void getManucountByDay(String dayTime) {
		SendObjectParams sendObjectParams = new SendObjectParams();

		Object[] params = new Object[] { dayTime };
		try {
			sendObjectParams.setParams(REQUST.cmdReqAvgHourManuByDay, params);

			CountManuOfHourByDay mCountManuCmdProcess = new CountManuOfHourByDay(
					REQUST.cmdReqAvgHourManuByDay, (String) params[0]);

			DevicesService.sendCmd(sendObjectParams, mCountManuCmdProcess);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 按日统计人流
	 * 
	 * @author Administrator
	 * 
	 */
	public class CountManuOfHourByDay extends
			CountManuCmdProcess<ReceiveAvgHourManuByDay> {

		public CountManuOfHourByDay(REQUST requst, String beginTime) {
			super(requst, beginTime);
			// TODO Auto-generated constructor stub
		}

		public void onProcess(ReceiveAvgHourManuByDay receiveCmdBean) {
			super.onProcess(receiveCmdBean);
			// for (CountManuResultBean countManuResultBean :
			// receiveCmdBean.countManuResultBeans) {
			// Log.e("chenlong",
			// DateUtil.getTimeStringFormat(countManuResultBean.time,
			// DateUtil.TIME_FORMAT_YMDHMS)
			// + ":"
			// + countManuResultBean.dayofWeet
			// + ":"
			// + countManuResultBean.inManu
			// + ":"
			// + countManuResultBean.outManu
			// + ":"
			// + countManuResultBean.avgTime);
			// }
			monthResultBeans = receiveCmdBean.countManuResultBeans;
			Message message = new Message();
			message.what = MONTH_VIEW_REFLESH;
			handler.sendMessage(message);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.last_day_ibtn:
			mCurCalendar = DateUtil.getCalendarByNumDay(mCurCalendar, -1);
			timeBtn.setText(DateUtil.getTimeStringFormat(mCurCalendar,
					"yyyy-MM-dd"));
			getManucountByDay(DateUtil.getTimeStringFormat(mCurCalendar,
					"yyyy-MM-dd HH:mm:ss"));

			break;
		case R.id.next_day_ibtn:
			mCurCalendar = DateUtil.getCalendarByNumDay(mCurCalendar, 1);
			timeBtn.setText(DateUtil.getTimeStringFormat(mCurCalendar,
					"yyyy-MM-dd"));
			getManucountByDay(DateUtil.getTimeStringFormat(mCurCalendar,
					"yyyy-MM-dd HH:mm:ss"));
			break;
		case R.id.search_date_bt:
//			new DatePickerDialog(getActivity(), listener,
//					mCurCalendar.get(Calendar.YEAR),
//					mCurCalendar.get(Calendar.MONTH),
//					mCurCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		default:
			break;
		}

	}

//	private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() { //
//
//		@Override
//		public void onDateSet(DatePicker arg0, int y, int m, int d) {
//			Log.i("chenlong", "time "+y+"-"+m+"-"+d);
//			timeBtn.setText(y+"-"+m+"-"+d);
//		}
//
//	};
//	

}