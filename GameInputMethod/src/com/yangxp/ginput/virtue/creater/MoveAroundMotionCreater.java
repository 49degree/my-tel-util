package com.yangxp.ginput.virtue.creater;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.yangxp.ginput.virtue.MotionCreaterFactory;
import com.yangxp.ginput.virtue.bean.KeyMapping;
import com.yangxp.ginput.virtue.bean.MotionBean;
import com.yangxp.ginput.virtue.instrument.IMotionInstrument;

public class MoveAroundMotionCreater implements IMotionCreater{
	AroundHandler mAroundHandler = null;
	IMotionInstrument mMotionInstrument;
	MotionBean mMotionBean = null;

	public MoveAroundMotionCreater(IMotionInstrument motionInstrument){
		mAroundHandler = new AroundHandler(); 
		mMotionInstrument = motionInstrument;
		mMotionBean = new MotionBean();
	}
	
	@Override
	public boolean create(InputEvent event,KeyMapping keyMapping){
		//Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName());
		MotionEvent me = (MotionEvent)event;
		float x = 0;
		float y = 0;
		if(keyMapping.key==220){//左摇杆
			x = me.getX();
			y = me.getY();
		}else{
			x = me.getAxisValue(MotionEvent.AXIS_Z);
			y = me.getAxisValue(MotionEvent.AXIS_RZ);
		}
		
		//MotionEvent motionEvent = null;
		mAroundHandler.resetParams(keyMapping, x, y);
		
		if(keyStatuMap.containsKey(keyMapping.key)){
			mAroundHandler.removeMessages(AroundHandler.MSG_ENQUEUE_AXIS_KEY_REPEAT);
			if(!MotionCreaterFactory.checkMotionEvent(x, y)){
				mMotionBean.action = MotionEvent.ACTION_UP;
				keyStatuMap.remove(keyMapping.key);
			}else{
				calMoveMotionEvent(keyMapping,x,y);
				return true;
			}
		}else{
			if(!MotionCreaterFactory.checkMotionEvent(x, y)){
				return false;
			}
			calStartMotionEvent(keyMapping,x,y);
			Message m = mAroundHandler.obtainMessage(AroundHandler.MSG_ENQUEUE_AXIS_KEY_REPEAT);
			
			Log.e("", "ViewConfiguration.getKeyRepeatDelay():"+ViewConfiguration.getKeyRepeatDelay());
			
			mAroundHandler.sendMessageDelayed(m, mAroundHandler.repeatTime);
			
		}	
		
		if(mMotionBean!=null){
			mMotionInstrument.sendMotion(mMotionBean);
			return true;
		}
		return false;
	}
	
	private MotionEvent calStartMotionEvent(KeyMapping keyMapping,float x,float y){
		float indexX = (float) Math.sin(Math.atan(Math.abs(y/x)))*keyMapping.radius;
		float indexY = (float) Math.cos(Math.atan(Math.abs(y/x)))*keyMapping.radius;
		Log.e(this.getClass().getSimpleName(),"indexX:"+indexX);
		Log.e(this.getClass().getSimpleName(),"indexY:"+indexY);
		if(x>0&&y<0){
			indexX = keyMapping.x-indexX;
			indexY = keyMapping.y+indexY;
		}else if(x>0&&y>0){
			indexX = keyMapping.x-indexX;
			indexY = keyMapping.y-indexY;
		}else if(x<0&&y>0){
			indexX = keyMapping.x+indexX;
			indexY = keyMapping.y-indexY;
		}else if(x<0&&y<0){
			indexX = keyMapping.x+indexX;
			indexY = keyMapping.y+indexY;
		}
		
		MotionEvent motionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_DOWN, indexX, indexY, 0);
		mMotionBean.action = MotionEvent.ACTION_DOWN;
		mMotionBean.x = indexX;
		mMotionBean.y = indexY;
		
		keyStatuMap.put(keyMapping.key, mMotionBean);
		
		return motionEvent;
	}
	
	
	private void calMoveMotionEvent(KeyMapping keyMapping,float x,float y){
		Log.e(this.getClass().getSimpleName(),"calMoveMotionEvent:"+x+":"+y);
		//MotionEvent motionEvent = keyStatuMap.get(keyMapping.key);
		
		if(Math.sqrt(Math.pow(mMotionBean.getX()-keyMapping.x,2)+Math.pow(mMotionBean.getY()-keyMapping.y,2))>=keyMapping.radius){
			mMotionBean.action = MotionEvent.ACTION_UP;
			
			mMotionInstrument.sendMotion(mMotionBean);
			
			calStartMotionEvent(keyMapping,x,y);
			
			mMotionInstrument.sendMotion(mMotionBean);
		}else{
			mMotionBean.action = MotionEvent.ACTION_MOVE;
			mMotionBean.x = mMotionBean.getX()+mAroundHandler.x*10;
			mMotionBean.y = mMotionBean.getY()+mAroundHandler.y*10;
			mMotionInstrument.sendMotion(mMotionBean);
		}
		keyStatuMap.put(keyMapping.key, mMotionBean);
		Message m = mAroundHandler.obtainMessage(AroundHandler.MSG_ENQUEUE_AXIS_KEY_REPEAT);
		mAroundHandler.sendMessageDelayed(m, mAroundHandler.repeatTime);
	}
	
	
    final class AroundHandler extends Handler {
    	private final static int MSG_ENQUEUE_AXIS_KEY_REPEAT = 3;
    	float x = 0;
    	float y = 0;
    	KeyMapping keyMapping = null;
    	float radus = 0;
    	int repeatTime = 50;
    	
    	void resetParams(KeyMapping keyMapping,float x,float y){
    		this.keyMapping = keyMapping;
    		this.x = x;
    		this.y = y;
    		radus = (float) Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    		//repeatTime = (int)(10/radus);
    	}
    	
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ENQUEUE_AXIS_KEY_REPEAT: {
                	calMoveMotionEvent(keyMapping,x,y);
                } break;
            }
        }
    }

}
