package com.yangxp.ginput.virtue.creater;

import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.yangxp.ginput.virtue.bean.KeyMapping;
import com.yangxp.ginput.virtue.bean.MotionBean;
import com.yangxp.ginput.virtue.instrument.IMotionInstrument;

public class ClickMotionCreater implements IMotionCreater{
	IMotionInstrument mMotionInstrument;
	//MotionBean mMotionBean = null;
	public ClickMotionCreater(IMotionInstrument motionInstrument){
		mMotionInstrument = motionInstrument;
		//mMotionBean = new MotionBean();
	}
	
	public boolean create(InputEvent event,KeyMapping keyMapping){
		//Log.e(this.getClass().getName(), this.getClass().getName());
		int keyCode = ((KeyEvent)event).getKeyCode(),
				action = ((KeyEvent)event).getAction();
//		keyMapping.x = 20;
//		keyMapping.y = 581;
		MotionBean mMotionBean = null;
		if(keyStatuMap.containsKey(keyCode)){
			mMotionBean = keyStatuMap.get(keyCode);
		}else if(action == MotionEvent.ACTION_DOWN){
			mMotionBean = new MotionBean();
		}
		if(mMotionBean==null){
			return false;
		}
		if(action == MotionEvent.ACTION_DOWN){
	    	if(keyStatuMap.containsKey(keyCode)){
	    		mMotionBean.action = MotionEvent.ACTION_MOVE; 
	    		mMotionBean.x = keyStatuMap.get(keyMapping.key).x;
	    		mMotionBean.y = keyStatuMap.get(keyMapping.key).y;
	    	}else{
	    		mMotionBean.action = MotionEvent.ACTION_DOWN; 
	    		mMotionBean.x = keyMapping.x;
	    		mMotionBean.y =  keyMapping.y;
	    		keyStatuMap.put(keyMapping.key, mMotionBean);
	    	}
		}else if(action == MotionEvent.ACTION_UP){
			mMotionBean = keyStatuMap.get(keyCode);
    		mMotionBean.action = MotionEvent.ACTION_UP; 
    		mMotionBean.x = keyMapping.x;
    		mMotionBean.y =  keyMapping.y;
			keyStatuMap.remove(keyMapping.key);
		}
		
		if(mMotionBean!=null){
			mMotionInstrument.sendMotion(mMotionBean);
			return true;
		}
		return false;
	}

}
