package com.guanri.fsk.view;
import java.awt.Color;


public class CureLineBean {
	public int[] pointHeight = null;
	public Color lineColor = Color.black;
	
	
	public CureLineBean(int[] pointHeight, Color lineColor) {
		super();
		this.pointHeight = pointHeight;
		this.lineColor = lineColor;
	}
	
	public int[] getPointHeight() {
		return pointHeight;
	}
	public void setPointHeight(int[] pointHeight) {
		this.pointHeight = pointHeight;
	}
	public Color getLineColor() {
		return lineColor;
	}
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}
	
	
}
