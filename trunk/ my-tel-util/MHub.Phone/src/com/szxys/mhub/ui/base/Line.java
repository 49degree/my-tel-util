package com.szxys.mhub.ui.base;

import android.graphics.Color;

public class Line {
	protected float xStart;
	protected float yStart;
	protected float xEnd;
	protected float yEnd;	
	protected int color;
	public Line(float xStart, float yStart, float xEnd, float yEnd, int color) {
		super();
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
		this.color = color;
	}	
	
	public Line(float xStart, float yStart, float xEnd, float yEnd) {
		super();
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
		this.color = Color.BLACK;
	}	
	
}
