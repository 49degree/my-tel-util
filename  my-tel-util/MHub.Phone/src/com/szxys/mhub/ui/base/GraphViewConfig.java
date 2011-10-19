package com.szxys.mhub.ui.base;

import android.graphics.Color;

/**
 * 曲线图控件配置类
 * @author xak
 *
 */
public class GraphViewConfig {
	/**时间模式*/
	public final static int MODE_TIME = 0;
	/**线性模式，默认是该模式*/
	public final static int MODE_LINEAR = 1;
	/**字符串模式（目前没有使用）*/
	public final static int MODE_STRING = 2;	
	
	/**是否支持缩放（考虑到大部分子业务数据缩放无意义，目前控件自动关闭缩放功能*/
	public boolean isSupportScale;
	/**是否支持双坐标模式，默认开启*/
	public boolean isSupportDoubleCoordinate;
	/**是否支持网格，默认开启*/
	public boolean isSupportGrid;
	/**是否是实时模式，默认关闭*/
	public boolean isRealTime;
	/**是否支持拖拉，默认开启*/
	public boolean isSupportDrag;
	/**是否圆角模式*/
	public boolean haveRoundCorners;
	
	/**X坐标轴模式，其取值可以为{@link #MODE_TIME}, {@link #MODE_LINEAR}, 或者 {@link #MODE_STRING}*/	
	public int xAxisMode;	
	/**控件背景颜色*/
	public int background;
	
	
	/**
	 * 默认构造函数
	 */
	public GraphViewConfig() {
		isSupportDrag = true;	
		isSupportDoubleCoordinate = true;
		isSupportGrid = true;
		xAxisMode = MODE_LINEAR;
		background = Color.WHITE;
	}
}