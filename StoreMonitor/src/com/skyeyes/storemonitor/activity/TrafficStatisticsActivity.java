package com.skyeyes.storemonitor.activity;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.process.impl.CountManuCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;
/** 人流统计*/
public class TrafficStatisticsActivity extends Activity {
	private int SERIES_NR = 1;

	private TopTitleView topTitleView;
	private GraphicalView mView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic_statistics_view);
		XYChart chart = new LineChart(getDemoDataset(), getDemoRenderer());
	    mView = new GraphicalView(this, chart);
		topTitleView = (TopTitleView)findViewById(R.id.ts_topView);
		
	    LinearLayout layout = (LinearLayout)findViewById(R.id.my_chart);
	    layout.addView(mView);
		topTitleView.setOnRightButtonClickListener(new OnClickListenerCallback() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
			}
		});
		topTitleView.setOnLeftButtonClickListener(new OnClickListenerCallback() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
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
	
	
	private XYMultipleSeriesDataset getDemoDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		final int nr = 10;
		Random r = new Random();
		for (int i = 0; i < SERIES_NR; i++) {
			XYSeries series = new XYSeries("Demo series " + (i + 1));
			for (int k = 0; k < nr; k++) {
				series.add(k, 20 + r.nextInt() % 10);
				Log.i("chenlong",
						"series :: " + (i + 1) + "  "
								+ (20 + r.nextInt() % 100));
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
		renderer.setPanLimits(new double[] { -1, 24, 0, 50 }); // 限制xy轴的长度
		renderer.setZoomEnabled(false, false);
		renderer.setXLabels(10); // 当设置为10时，x轴单位为1
		renderer.setPointSize(5f);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setYAxisMin(0);
		renderer.setYLabelsPadding(20);
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
	 * @param dayTime 如：2014-05-01 00:00:00
	 */
	private void getManucountByDay(String dayTime) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		Object[] params = new Object[] {dayTime};
		try {
			sendObjectParams.setParams(REQUST.cmdReqAvgHourManuByDay, params);
			System.out.println("getManucount入参数：" + sendObjectParams.toString());
			CountManuOfHourByDay mCountManuCmdProcess = new CountManuOfHourByDay(REQUST.cmdReqAvgHourManuByDay,(String)params[0]);
			
			DevicesService.sendCmd(sendObjectParams,mCountManuCmdProcess);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 按月统计人流
	 * @param dayTime 如：2014-05-01 00:00:00
	 */
	private void getManucountByMonth(String dayTime) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		Object[] params = new Object[] {dayTime};
		try {
			sendObjectParams.setParams(REQUST.cmdReqAvgDayManuByMouse, params);
			System.out.println("getManucount入参数：" + sendObjectParams.toString());
			CountManuOfDayByMonth mCountManuCmdProcess = new CountManuOfDayByMonth(REQUST.cmdReqAvgDayManuByMouse,(String)params[0]);
			
			DevicesService.sendCmd(sendObjectParams,mCountManuCmdProcess);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 按日统计人流
	 * @author Administrator
	 *
	 */
	public class CountManuOfHourByDay extends CountManuCmdProcess{

		public CountManuOfHourByDay(REQUST requst, String beginTime) {
			super(requst, beginTime);
			// TODO Auto-generated constructor stub
		}
		public void onProcess(ReceiveCountManu receiveCmdBean) {
			super.onProcess(receiveCmdBean);
			//receiveCmdBean.countManuResultBeans 这里是数据列表
		}
	}
	
	/**
	 * 按月统计人流
	 * @author Administrator
	 *
	 */
	public class CountManuOfDayByMonth extends CountManuCmdProcess{

		public CountManuOfDayByMonth(REQUST requst, String beginTime) {
			super(requst, beginTime);
			// TODO Auto-generated constructor stub
		}
		public void onProcess(ReceiveCountManu receiveCmdBean) {
			super.onProcess(receiveCmdBean);
			//receiveCmdBean.countManuResultBeans 这里是数据列表
		}
	}
}
