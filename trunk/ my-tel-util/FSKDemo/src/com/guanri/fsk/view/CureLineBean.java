package com.guanri.fsk.view;
import java.awt.Color;

import com.guanri.fsk.utils.TypeConversion;


public class CureLineBean {
	public int[] pointHeight = null;
	public Color lineColor = Color.black;
	
	
	public CureLineBean(int[] pointHeight, Color lineColor) {
		super();
		this.pointHeight = pointHeight;
		this.lineColor = lineColor;
	}
	
	public CureLineBean(byte[] pointHeight, Color lineColor) {
		super();
		this.pointHeight = new int[pointHeight.length/2];
		
		for(int i=0;i<pointHeight.length/2;i++){
			this.pointHeight[i]=TypeConversion.bytesToShort(pointHeight, i*2);
		}
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
