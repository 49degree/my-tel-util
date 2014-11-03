package com.yangxp.ginput.virtue.instrument;

import android.view.MotionEvent;

import com.yangxp.ginput.virtue.bean.MotionBean;

public interface IMotionInstrument {
	
	public boolean sendMotion(MotionBean motionBean);
	public boolean sendMotion(MotionEvent event);
}
