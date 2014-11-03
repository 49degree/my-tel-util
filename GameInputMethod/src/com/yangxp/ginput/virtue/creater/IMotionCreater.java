package com.yangxp.ginput.virtue.creater;

import java.util.HashMap;

import android.view.InputEvent;

import com.yangxp.ginput.virtue.bean.KeyMapping;
import com.yangxp.ginput.virtue.bean.MotionBean;

public interface IMotionCreater {
	static HashMap<Integer,MotionBean> keyStatuMap = new HashMap<Integer,MotionBean>();
	public final static int MOVE_STEP_PIX = 1;
	public boolean create(InputEvent event,KeyMapping keyMapping);
}
