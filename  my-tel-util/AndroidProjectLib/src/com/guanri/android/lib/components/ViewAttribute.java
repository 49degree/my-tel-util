package com.guanri.android.lib.components;


public class ViewAttribute {
	//控件在Activity中显示的实际高度和宽度、因为控件在设置时只能设置为 int整型
	private int height;
	private int width;
	
	private int count = -1;
	private int viewId;
	
	//控件的文字大小，当控件为TextViw、EditText、Button时使用
	private float textSize;
	
	//控件经过扩大或缩小后保留有小数位的实际高度和宽度。为了在下次再进行放大时使用该数乘以放大倍数，降低误差
	private float factWidth;
	private float factHeight;
	private float factTextSize;
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void setViewId(int viewId) {
		this.viewId = viewId;
	}
	public int getViewId() {
		return viewId;
	}
	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}
	public float getTextSize() {
		return textSize;
	}
	public float getFactWidth() {
		return factWidth;
	}
	public void setFactWidth(float factWidth) {
		this.factWidth = factWidth;
	}
	public float getFactHeight() {
		return factHeight;
	}
	public void setFactHeight(float factHeight) {
		this.factHeight = factHeight;
	}
	public float getFactTextSize() {
		return factTextSize;
	}
	public void setFactTextSize(float factTextSize) {
		this.factTextSize = factTextSize;
	}
}
