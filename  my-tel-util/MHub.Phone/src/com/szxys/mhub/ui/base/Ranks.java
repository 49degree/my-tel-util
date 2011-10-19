package com.szxys.mhub.ui.base;



/**网格行列*/
public class Ranks {
	public final static int CELL_SIZE = 16;
	/**默认单行或者单列宽度*/
	public float defaultSize;
	/**行列总体宽度*/
	public int len;
	/**开始行或者列*/
	public int start;
	/**偏移量*/
	public float offset;
	/**当前默认单行或者单列宽度*/
	public float size;
	/**行数或者列数*/
	public int count;
	/**当前缩放比例*/
	public int scale;
	/**当前缩放因子*/
	public float scaleFact;
	
	public Ranks() {
		defaultSize = (int) (CELL_SIZE * GraphView.mDensity);
		size = defaultSize;
		scaleFact = 1;
	}
	public void setScale(int scale) {
		this.scale = scale;
		scaleFact =  (float) Math.pow(2,scale);
	}
	
	public void setDefaultSize(float s) {
		defaultSize = s;
		size = defaultSize;
	}
	
	public void setSize(float s) {	
		this.size = s;
		float maxSize = defaultSize * 2;
		if (size > maxSize) {
			float tmpOffset= size - maxSize;
			size = size % defaultSize + defaultSize;
			setScale((int) (scale - (tmpOffset / defaultSize + 1)));				
			start = start * 2 - (int)(offset/size);
			offset = offset % size;
		} else if (size < defaultSize) {
			float tmpOffset = defaultSize - size;				
			size = maxSize - (tmpOffset%defaultSize);			
			setScale(scale + 1 + (int)(tmpOffset/defaultSize));			
			if (start % 2 !=0) {
				offset = offset + size;
			}
			start = (int) Math.ceil(start *1.0f / 2);	
		}
		count = (int) Math.ceil(len * 1.0f /size);
	}
	
	public float doScale(int sc) {
		float off =0.0f;			
		if (Math.abs(sc) > GraphView.mScaleMin) {
			off = -(sc * GraphView.SCALE_FACT * len /size)/2;				
			setSize( size + sc*GraphView.SCALE_FACT);				
		}
		return off;		
	}
	
	public boolean doScroll(float off) {							
		if (Math.abs(off)>GraphView.mDragMin) {
			offset += off;
			start += -(int)(offset/size);
			offset = offset%size;
			if (offset < 0) {
				offset += size;
				start++; 
			}		
			return true;
		}
		return false;
	}
}
