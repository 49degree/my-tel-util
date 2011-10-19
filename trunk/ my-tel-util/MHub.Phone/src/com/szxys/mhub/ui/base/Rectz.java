package com.szxys.mhub.ui.base;


public class Rectz {
	protected float xStart;
	protected float yStart;
	protected float xEnd;
	protected float yEnd;	
	protected int color;
	public Rectz(float xStart, float yStart, float xEnd, float yEnd, int color) {
		super();
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
		this.color = color;
	}	
	
	public Rectz(float xStart, float yStart, float xEnd, float yEnd) {
		super();
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
		this.color = 0x5500FF00;
	}	
	
}
