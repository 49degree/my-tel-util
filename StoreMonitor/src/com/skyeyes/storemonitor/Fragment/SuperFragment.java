package com.skyeyes.storemonitor.Fragment;

import java.util.ArrayList;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu.CountManuResultBean;
import com.skyeyes.base.view.CustomProgressDialog;
@SuppressLint("NewApi")

public abstract class SuperFragment extends Fragment {
	private int SERIES_NR = 1;
	public static final int VIEW_REFLESH = 1;
	public static final int AVG_TIME = 2;
	public static final int ALL_COUNT = 3;
	protected XYMultipleSeriesRenderer xyMultipleSeriesRenderer;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		xyMultipleSeriesRenderer = getDemoRenderer();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	protected XYMultipleSeriesDataset getDemoDataset(ArrayList<CountManuResultBean>  monthResultBeans) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		final int nr = monthResultBeans.size();
		for (int i = 0; i < SERIES_NR; i++) {

			XYSeries series = new XYSeries("Demo series ");

			for (int k = 0; k < nr; k++) {
				series.add(k, monthResultBeans.get(k).inManu);
				series.addWeeks(k, monthResultBeans.get(k).dayofWeet);
			}
			dataset.addSeries(series);
		}
		return dataset;

	}

	protected XYMultipleSeriesRenderer getDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(30);
		renderer.setPanEnabled(true, false); // 设置沿X或Y轴是否可以拖动
		renderer.setLegendTextSize(15);
//		renderer.setPanLimits(new double[] { -1, 31, 0, 80 }); // 限制xy轴的长度
		renderer.setZoomEnabled(false, false);
		renderer.setXLabels(10); // 当设置为10时，x轴单位为1
		renderer.setXAxisMax(8.0);
//		renderer.setYAxisMax(80.0);
		renderer.setLegendHeight(20);
		renderer.setPointSize(5f);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setYAxisMin(0);
		renderer.setYLabelsPadding(40);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(Color.GREEN);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setLabelsColor(Color.GRAY);
		return renderer;
	}
	
	protected CustomProgressDialog mPdDialog;
	private boolean cancell = false;

	protected void showMPdDialog() {
		if (mPdDialog == null) {
			mPdDialog = CustomProgressDialog.createDialog(getActivity());
			mPdDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (KeyEvent.KEYCODE_BACK == keyCode) {
						cancell = true;

					}
					return false;
				}
			});
		}
		if (!mPdDialog.isShowing()) {
			mPdDialog.show();
		}

	}
	
	
	/**
	 * 隐藏提示框
	 */
	protected void dismissMPdDialog() {
		if (mPdDialog != null && mPdDialog.isShowing()) {
			mPdDialog.dismiss();
		}
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}
}
