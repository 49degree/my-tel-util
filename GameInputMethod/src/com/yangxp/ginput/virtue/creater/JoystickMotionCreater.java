package com.yangxp.ginput.virtue.creater;

import android.view.InputEvent;
import android.view.MotionEvent;

import com.yangxp.ginput.virtue.MotionCreaterFactory;
import com.yangxp.ginput.virtue.bean.KeyMapping;
import com.yangxp.ginput.virtue.bean.MotionBean;
import com.yangxp.ginput.virtue.instrument.IMotionInstrument;

public class JoystickMotionCreater implements IMotionCreater{
	IMotionInstrument mMotionInstrument;
	//MotionBean mMotionBean = null;
	public JoystickMotionCreater(IMotionInstrument motionInstrument){
		mMotionInstrument = motionInstrument;
		//mMotionBean = new MotionBean();
	}
	
	@Override
	public boolean create(InputEvent event,KeyMapping keyMapping){
		//Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName());
		MotionEvent motionEvent = (MotionEvent)event;
		float x = 0;
		float y = 0;
		if(keyMapping.key==220){//左摇杆
			x = motionEvent.getX();
			y = motionEvent.getY();
		}else{
			x = motionEvent.getAxisValue(MotionEvent.AXIS_Z);
			y = motionEvent.getAxisValue(MotionEvent.AXIS_RZ);
		}
		
		MotionBean mMotionBean = null;
		if(keyStatuMap.containsKey(keyMapping.key)){
			mMotionBean = keyStatuMap.get(keyMapping.key);
		}else{
			mMotionBean = new MotionBean();
		}

    	if(keyStatuMap.containsKey(keyMapping.key)){
    		float indexX = keyStatuMap.get(keyMapping.key).x;
    		float indexY = keyStatuMap.get(keyMapping.key).y;
    		indexX=keyMapping.x+x*keyMapping.radius;
    		indexY=keyMapping.y+y*keyMapping.radius;
    		if(!MotionCreaterFactory.checkMotionEvent(x, y)){
				keyStatuMap.remove(keyMapping.key);
				mMotionBean.action = MotionEvent.ACTION_UP; 
			}
			else{
				mMotionBean.action = MotionEvent.ACTION_MOVE; 
			}
    		
    		mMotionBean.x = indexX;
    		mMotionBean.y = indexY;
    	}else{
			if(!MotionCreaterFactory.checkMotionEvent(x, y)){
				return false;
			}
			
			mMotionBean.action = MotionEvent.ACTION_DOWN;
    		mMotionBean.x = keyMapping.x;
    		mMotionBean.y = keyMapping.y;
    		
			keyStatuMap.put(keyMapping.key, mMotionBean);
    	}	
    	
		if(mMotionBean!=null){
			mMotionInstrument.sendMotion(mMotionBean);
			return true;
		}
		return false;
	}

}
