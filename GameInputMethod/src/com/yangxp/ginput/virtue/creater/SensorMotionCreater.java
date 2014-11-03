package com.yangxp.ginput.virtue.creater;

import android.content.res.Configuration;
import android.view.InputEvent;
import android.view.MotionEvent;

import com.yangxp.ginput.virtue.MotionCreaterFactory;
import com.yangxp.ginput.virtue.bean.KeyMapping;

public class SensorMotionCreater implements IMotionCreater{
	static final float max = 10f;
	float sensorX = 0;
	float sensorY = 0;
	float sensorZ = max;
	
	@Override
	public boolean create(InputEvent event,KeyMapping keyMapping){
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
		/*
		//land or PORTRAIT 
		try{
			int orientationType = SystemProperties.getInt("user.orientation", Configuration.ORIENTATION_LANDSCAPE);
			if (orientationType == Configuration.ORIENTATION_PORTRAIT) {
				float temp = x;
	  			x = -y;
	  			y = temp;
	  	    }
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		
		if(!MotionCreaterFactory.checkMotionEvent(x, y)){
			sensorX = 0;
			sensorY = 0;
			sensorZ = 0;
			return false;
		}
		
		sensorX = -max*x;
		sensorY = max*y;
		sensorZ = max - (float)Math.abs(Math.atan(y/x)/(Math.PI/2));
		return true;
	}
	
	public boolean setSensorValues(float[] values){
		if(values.length>=3){
			values[0] = sensorX;
			values[1] = sensorY;
			values[2] = sensorZ;
			return true;
		}else
			return false;
	}

}
