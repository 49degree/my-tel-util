package com.szxys.mhub.ui.base;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.util.Log;

/**
 * 曲线类
 * @author xak
 */
public class Curve {
	 /**使用左测Y轴坐标系（默认值）*/
	 public final static int TYPE_LEFT = 0;	
	 /**使用右侧Y轴坐标系*/
	 public final static int TYPE_RIGHT = 1;
	 protected double[] data;		//曲线数据储存数组
	 protected int yAxlxType;	    //Y轴坐标类型,可以为TYPE_LEFT或者TYPE_RIGHT
	 private final static int[] DEFAUT_COLOR = {Color.BLACK,Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW};
	private static final String TAG = "Curve";
	 private int id;
	 private String text;		
	 protected LinkedList<float[]> dataQueue;
	 private boolean isVisible;	//是否可见
	 private int color;
	 private static int defaultColorID;
	 protected int showStart;
	 protected int showEnd;
	protected boolean isStartConversion;
	protected boolean isStartConversionAll;
	protected boolean isPreOver;
	protected int maxPoint;
	 
	/**
	 * 构造函数
	 * @param data 曲线数据存储数组；由X值和Y值组成；格式：{xVaule1，yValue1，xVaule2，yValue2...}	 
	 * @param isVisible	 是否可见
	 * @param yAxlx	Y轴坐标类型,可以为{@link #TYPE_LEFT}或{@link #TYPE_RIGHT}
	 * @param color 曲线颜色
	 * @param name 曲线名称
	 */
	public Curve(double[] data,boolean isVisible, int yAxlx, int color,String text) {
		super();
		this.data = data;
		this.isVisible = isVisible;
		this.yAxlxType = yAxlx;
		this.color = color;
		this.text = text;
		dataQueue = new LinkedList<float[]>();
	}
	
	/**
	 * 默认构造函数；默认使用左边Y轴坐标系，默认可见
	 * @param data 曲线数据存储数组；由X值和Y值组成；格式：{xVaule1，yValue1，xVaule2，yValue2...}	 
	 */
	public Curve(double[] data,String text) {
		super();
		this.data = data;
		this.isVisible = true;
		this.yAxlxType = TYPE_LEFT;
		this.color = DEFAUT_COLOR[defaultColorID];
		this.text = text;		
		dataQueue = new LinkedList<float[]>();
	}

	public Curve(String text, int maxPoint) {
		this.isVisible = true;
		this.yAxlxType = TYPE_LEFT;
		this.color = DEFAUT_COLOR[defaultColorID];
		this.text = text;	
		this.maxPoint = maxPoint;
		dataQueue = new LinkedList<float[]>();
	}

	public void doDraw(Canvas canvas, Paint mPaint) {
		Log.v(TAG, "doDraw");
		mPaint.setColor(color);
//		mPaint.setAntiAlias(true);
//		mPaint.setDither(true);
		mPaint.setStrokeWidth(4);
		mPaint.setStrokeCap(Cap.ROUND);
		for (int i=0; i<dataQueue.size(); i++) {
			Log.v(TAG, "The x is:" + dataQueue.get(i)[0] + ";and the y is:" +dataQueue.get(i)[1]);
			canvas.drawPoint(dataQueue.get(i)[0], dataQueue.get(i)[1], mPaint);
		}
		mPaint.setStrokeWidth(0);
		for (int i=0; i<dataQueue.size()-1; i++) {			
			canvas.drawLine((float)dataQueue.get(i)[0], dataQueue.get(i)[1],dataQueue.get(i+1)[0], dataQueue.get(i+1)[1], mPaint);
		}
		
	}

	public void add(long x, int y) {
		if (dataQueue.size() == maxPoint) {
			dataQueue.remove();
		}
		dataQueue.add(new float[] {x,y});		
	}
		 
}