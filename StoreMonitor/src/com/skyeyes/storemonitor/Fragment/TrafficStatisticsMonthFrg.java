package com.skyeyes.storemonitor.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.XYChart;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgDayManuByMouse;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.process.impl.CountManuCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

@SuppressLint("NewApi")
public class TrafficStatisticsMonthFrg extends SuperFragment implements
		OnClickListener {

	private View mItemView;
	private TextView averageTime;
	private TextView total;

	private LinearLayout layout;

	private Button timeBtn;
	private Button lastDayIbtn;
	private Button nextDayIbtn;
	public Calendar mCurCalendar = Calendar.getInstance(); // 当前的时间

	private GraphicalView mView;

	private XYChart chart;

	private final int MONTH_VIEW_REFLESH = 1;

	private CustomerDatePickerDialog mDialog;
	private int mYear;
	private int mMonth;
	// 测试数据
	private ArrayList<CountManuResultBean> monthResultBeans;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MONTH_VIEW_REFLESH:
				layout.removeAllViews();
				xyMultipleSeriesRenderer.setYAxisMax(80.0);
				xyMultipleSeriesRenderer.setPanLimits(new double[] { -1,
						monthResultBeans.size(), 0, 80 }); // 限制xy轴的长度

				chart = new LineChart(getDemoDataset(monthResultBeans),
						xyMultipleSeriesRenderer);
				mView = new GraphicalView(getActivity(), chart);
				averageTime
						.setText(String.valueOf(monthResultBeans.get(0).avgTime));
				total.setText(String.valueOf(monthResultBeans.get(0).inManu));
				layout.addView(mView);
				dismissMPdDialog();
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
		mItemView = inflater.inflate(
				R.layout.traffic_statistivs_month_frg_view, container, false);
		layout = (LinearLayout) mItemView.findViewById(R.id.my_chart);
		total = (TextView) mItemView.findViewById(R.id.total_values);
		averageTime = (TextView) mItemView
				.findViewById(R.id.average_time_value);
		lastDayIbtn = (Button) mItemView.findViewById(R.id.last_day_ibtn);
		nextDayIbtn = (Button) mItemView.findViewById(R.id.next_day_ibtn);
		timeBtn = (Button) mItemView.findViewById(R.id.search_date_bt);
		timeBtn.setText(getMonthDate(mCurCalendar, 0));

		lastDayIbtn.setOnClickListener(this);
		nextDayIbtn.setOnClickListener(this);
		timeBtn.setOnClickListener(this);
		return mItemView;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getManucountByMonth("2014-05-01 00:00:00");
	}

	private void getManucountByMonth(String dayTime) {
		showMPdDialog();

		SendObjectParams sendObjectParams = new SendObjectParams();

		Object[] params = new Object[] { dayTime };
		try {
			sendObjectParams.setParams(REQUST.cmdReqAvgDayManuByMouse, params);
			System.out
					.println("getManucount入参数：" + sendObjectParams.toString());
			CountManuOfDayByMonth mCountManuCmdProcess = new CountManuOfDayByMonth(
					REQUST.cmdReqAvgDayManuByMouse, (String) params[0]);

			DevicesService.sendCmd(sendObjectParams, mCountManuCmdProcess);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 按月统计人流
	 * 
	 * @author Administrator
	 * 
	 */
	public class CountManuOfDayByMonth extends
			CountManuCmdProcess<ReceiveAvgDayManuByMouse> {

		public CountManuOfDayByMonth(REQUST requst, String beginTime) {
			super(requst, beginTime);
			// TODO Auto-generated constructor stub
		}

		public void onProcess(ReceiveAvgDayManuByMouse receiveCmdBean) {
			super.onProcess(receiveCmdBean);
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
			getViewData(mCurCalendar,-1);
			break;
			
		case R.id.next_day_ibtn:
			getViewData(mCurCalendar,1);
			break;
			
		case R.id.search_date_bt:
			mDialog = new CustomerDatePickerDialog(getActivity(),
					new DatePickerDialog.OnDateSetListener() {

						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							mYear = year;
							mMonth = monthOfYear;
							updateDisplay();
						}
					}, mCurCalendar.get(Calendar.YEAR),
					mCurCalendar.get(Calendar.MONTH),
					mCurCalendar.get(Calendar.DAY_OF_MONTH));
			mDialog.setTitle(mCurCalendar.get(Calendar.YEAR) + "年"
					+ (mCurCalendar.get(Calendar.MONTH) + 1) + "月");
			mDialog.show();

			DatePicker dp = findDatePicker((ViewGroup) mDialog.getWindow()
					.getDecorView());
			if (dp != null) {
				((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
						.getChildAt(2).setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}



	private void updateDisplay() {
		mCurCalendar.set(Calendar.YEAR, mYear);
		mCurCalendar.set(Calendar.MONTH, mMonth);

		String month = String.valueOf(mMonth + 1);
		if (mMonth < 9) {
			month = "0" + (mMonth + 1);
		}
		timeBtn.setText(new StringBuilder()
		// Month is 0 based so add 1
				.append(mYear).append("-").append(month));
		
		getViewData(mCurCalendar,0);
		
	}

	/**
	 * 将时间转换为参数需要的格式
	 * @param curCalendar
	 * @param index 1,为上一个月，-1为下一个月，0为当前
	 */
	private void getViewData(Calendar curCalendar,int index){
		StringBuffer buffer1 = new StringBuffer();
		buffer1.append(getMonthDate(curCalendar, index));
		timeBtn.setText(buffer1.toString());

		buffer1.append("-01 00:00:00");

		getManucountByMonth(buffer1.toString());
		buffer1 = null;
	}
	
	
	/**
	 * 
	 * @param cc
	 * @param month 1,为上一个月，-1为下一个月，0为当前
	 * @return
	 */
	private String getMonthDate(Calendar cc, int month) {
		cc.add(Calendar.MONTH, month);
		return new SimpleDateFormat("yyyy-MM").format(cc.getTime());
	}

	//只显示年月的datepickerdialog
	class CustomerDatePickerDialog extends DatePickerDialog {
		public CustomerDatePickerDialog(Context context,
				OnDateSetListener callBack, int year, int monthOfYear,
				int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);
			mDialog.setTitle(year + "年" + (month + 1) + "月");
		}
	}

	private DatePicker findDatePicker(ViewGroup group) {
		if (group != null) {
			for (int i = 0, j = group.getChildCount(); i < j; i++) {
				View child = group.getChildAt(i);
				if (child instanceof DatePicker) {
					return (DatePicker) child;
				} else if (child instanceof ViewGroup) {
					DatePicker result = findDatePicker((ViewGroup) child);
					if (result != null)
						return result;
				}
			}
		}
		return null;
	}
}
