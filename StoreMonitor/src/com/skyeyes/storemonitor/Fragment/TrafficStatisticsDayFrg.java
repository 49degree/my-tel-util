package com.skyeyes.storemonitor.Fragment;

import java.util.ArrayList;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.XYChart;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu.CountManuResultBean;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgHourManuByDay;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.process.impl.CountManuCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

@SuppressLint("NewApi")
public class TrafficStatisticsDayFrg extends SuperFragment {
	private View mItemView;
	private TextView averageTime;
	private TextView total;
	
	private LinearLayout layout;
	
	
	
	private GraphicalView mView;
	
	private XYChart chart;
	
	private final int MONTH_VIEW_REFLESH = 1;
	// 测试数据
	private ArrayList<CountManuResultBean> monthResultBeans;
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MONTH_VIEW_REFLESH:
				layout.removeAllViews();
				xyMultipleSeriesRenderer.setYAxisMax(20.0);

				chart = new LineChart(getDemoDataset(monthResultBeans), xyMultipleSeriesRenderer);
				mView = new GraphicalView(getActivity(), chart);
				averageTime.setText(String.valueOf(monthResultBeans.get(0).avgTime));
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
		layout = (LinearLayout)mItemView.findViewById(R.id.my_chart);
		total = (TextView)mItemView.findViewById(R.id.total_values);
		averageTime = (TextView)mItemView.findViewById(R.id.average_time_value);

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
			for (CountManuResultBean countManuResultBean : receiveCmdBean.countManuResultBeans) {
				Log.e("chenlong",
						DateUtil.getTimeStringFormat(countManuResultBean.time,
								DateUtil.TIME_FORMAT_YMDHMS)
								+ ":"
								+ countManuResultBean.dayofWeet
								+ ":"
								+ countManuResultBean.inManu
								+ ":"
								+ countManuResultBean.outManu
								+ ":"
								+ countManuResultBean.avgTime);
			}
			monthResultBeans =  receiveCmdBean.countManuResultBeans;
			Message message = new Message();
			message.what = MONTH_VIEW_REFLESH;
			handler.sendMessage(message);
		}

	}
}
