package com.yangxp.config.view.button;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.yangxp.config.bean.Mappings;

public interface MapperViewInterface {
    public final static int Y_UP_KEYCODE = 222;
    public final static int Y_DOWN_KEYCODE = 223;
    public final static int X_LEFT_KEYCODE = 224;
    public final static int X_RIGHT_KEYCODE = 225;

    public final static int RY_UP_KEYCODE = 226;
    public final static int RY_DOWN_KEYCODE = 227;
    public final static int RX_LEFT_KEYCODE = 228;
    public final static int RX_RIGHT_KEYCODE = 229;

//    public final static int LTRIGGER_KEYCODE = 230;
//    public final static int RTRIGGER_KEYCODE = 231;
    
    public final static int LEFT_JOYSTICK = 220;
    public final static int RIGHT_JOYSTICK = 221;
    
    
	public Mappings getKeyMapping();
	
	public void setKeyMapping(Mappings keyMapping);
	
	public void setPisition(float x,float y);
	
	public void setViewTouchListener(ViewTouchListener viewTouchListener);
	
	public void setRemoveSelfListener(RemoveSelfListener removeSelfListener);
	
	public void setIsActiveButton(boolean isActive);
	
	
	public boolean onJoysticMotion(MotionEvent event);
	public boolean onJoysticKey(KeyEvent event);
	
	public boolean dispatchKeyEvent(KeyEvent event);
	public boolean dispatchTouchEvent(MotionEvent event);
	public boolean dispatchGenericMotionEvent(MotionEvent event);
	
	public void setImage();
	
	public void showNewButtonNotice();
	
	public interface ViewTouchListener{
		public void onViewTouch(MapperViewInterface mapperButton);
	}
	public interface RemoveSelfListener{
		public void onRemoveSelf(MapperViewInterface mapperView);
	}
	
}
