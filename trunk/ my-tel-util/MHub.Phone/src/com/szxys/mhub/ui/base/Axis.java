package com.szxys.mhub.ui.base;

import java.text.Format;


import android.graphics.Canvas;

public abstract class Axis {		
	public final static int TYPE_LINEAR = 0;
	public final static int TYPE_TIME = 1;
	protected double graduation;	//刻度
	protected double startValue;
	protected double length;
	protected int skip;			
	protected float x;
	protected float y;	
	protected Format format;	

	public void doDraw(Canvas canvas){};				
	public void doDraw(Canvas canvas, Ranks rank){};
	public void setCoordinate(double startCoordinate, double endCoordinate) {};		
	public  double dataConversion(double value, Ranks rank) {
//		Log.v(TAG, "The value is:" + value + ";and the startValue is:" + startValue + ";and the graduation is:" + graduation * rank.scaleFact + ";and the size is:" + rank.size+";and the offset is:" + rank.offset );
		double returnValue = (value - startValue)/(graduation * rank.scaleFact) * rank.size + rank.offset;		
		return returnValue;
	}	
		
}
