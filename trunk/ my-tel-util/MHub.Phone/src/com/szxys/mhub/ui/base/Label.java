package com.szxys.mhub.ui.base;

import android.graphics.Color;

public class Label {
	private static final int TEXT_SIZE = 24;
	protected String text;
	protected float x;
	protected float y;
	protected int color;
	protected int size;
	public Label(String text, float x, float y, int color) {
		super();
		this.text = text;
		this.x = x;
		this.y = y;
		this.color = color;
		this.size = TEXT_SIZE;
	}
	
	public Label(String text, float x, float y) {		
		this.text = text;
		this.x = x;
		this.y = y;
		this.color = Color.BLACK;
		this.size = TEXT_SIZE;
	}

	public Label(String text, float x, float y, int color, int size) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.color = color;
		this.size = TEXT_SIZE;
	}
}
