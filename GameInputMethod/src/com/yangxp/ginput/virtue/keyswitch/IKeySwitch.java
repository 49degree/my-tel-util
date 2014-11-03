package com.yangxp.ginput.virtue.keyswitch;

import android.view.InputEvent;

import com.yangxp.ginput.virtue.creater.IMotionCreater;

public interface IKeySwitch {

	public boolean changeEvent(InputEvent keyCode);
	public boolean containsKeyCode(int keyCode);
	public boolean containsType(int type);
	public void setStatusBarHeight(int statusBarHeight);
	public int getStatusBarHeight();
}
