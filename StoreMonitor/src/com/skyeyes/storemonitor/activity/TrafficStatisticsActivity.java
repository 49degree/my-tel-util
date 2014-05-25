package com.skyeyes.storemonitor.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu.CountManuResultBean;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgDayManuByMouse;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgHourManuByDay;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.process.impl.CountManuCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

/** 人流统计 */
public class TrafficStatisticsActivity extends Activity {
	private int SERIES_NR = 1;
	private TopTitleView topTitleView;
	private GraphicalView mView;
	
	private XYChart chart;
	private LinearLayout layout;
	
	private final int MONTH_VIEW_REFLESH = 1;
	// 测试数据
	private ArrayList<CountManuResultBean> monthResultBeans;

	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MONTH_VIEW_REFLESH:
				chart = new LineChart(getDemoDataset(monthResultBeans), getDemoRenderer());
				mView = new GraphicalView(TrafficStatisticsActivity.this, chart);
				layout.addView(mView);
				break;

			default:
				break;
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic_statistics_view);
//		monthResultBeans = getDataByMonth("2014-05-01 00:00:00");

		
		topTitleView = (TopTitleView) findViewById(R.id.ts_topView);
		 layout = (LinearLayout) findViewById(R.id.my_chart);
		
		topTitleView
				.setOnRightButtonClickListener(new OnClickListenerCallback() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
					}
				});
		topTitleView
				.setOnLeftButtonClickListener(new OnClickListenerCallback() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
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
		 getManucountByDay("2014-05-17 00:00:00");
	}

	private XYMultipleSeriesDataset getDemoDataset(ArrayList<CountManuResultBean>  monthResultBeans) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		final int nr = monthResultBeans.size();
		for (int i = 0; i < SERIES_NR; i++) {

			XYSeries series = new XYSeries("Demo series ");

			for (int k = 0; k < nr; k++) {
				series.add(k, monthResultBeans.get(k).inManu);
				series.addWeeks(k, monthResultBeans.get(k).dayofWeet);
				Log.e("chenlong", " monthResultBeans.get(k).dayofWeet :::::::  " +  monthResultBeans.get(k).dayofWeet);

			}
			dataset.addSeries(series);
		}
		return dataset;

	}

	private XYMultipleSeriesRenderer getDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(30);
		renderer.setPanEnabled(true, true); // 设置沿X或Y轴是否可以拖动
		renderer.setLegendTextSize(15);
		renderer.setPanLimits(new double[] { -1, 31, 0, 50 }); // 限制xy轴的长度
		renderer.setZoomEnabled(false, false);
		renderer.setXLabels(10); // 当设置为10时，x轴单位为1
		renderer.setXAxisMax(8.0);
		renderer.setPointSize(5f);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setYAxisMin(0);
		renderer.setYLabelsPadding(40);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		// r.setColor(Color.BLUE);
		// r.setPointStyle(PointStyle.SQUARE);
		// r.setFillBelowLine(true);
		// r.setFillBelowLineColor(Color.WHITE);
		// r.setFillPoints(true);
		// renderer.addSeriesRenderer(r);
		// r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(Color.GREEN);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setLabelsColor(Color.GRAY);
		return renderer;
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
	 * 按月统计人流
	 * 
	 * @param dayTime
	 *            如：2014-05-01 00:00:00
	 */
	private void getManucountByMonth(String dayTime) {
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
//			for (CountManuResultBean countManuResultBean : receiveCmdBean.countManuResultBeans) {
//				Log.e("chenlong",
//						DateUtil.getTimeStringFormat(countManuResultBean.time,
//								DateUtil.TIME_FORMAT_YMDHMS)
//								+ ":"
//								+ countManuResultBean.dayofWeet
//								+ ":"
//								+ countManuResultBean.inManu
//								+ ":"
//								+ countManuResultBean.outManu
//								+ ":"
//								+ countManuResultBean.avgTime);
//			}
			monthResultBeans =  receiveCmdBean.countManuResultBeans;
			Message message = new Message();
			message.what = MONTH_VIEW_REFLESH;
			handler.sendMessage(message);
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
			// receiveCmdBean.countManuResultBeans 这里是数据列表
		}
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
