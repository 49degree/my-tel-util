package com.yangxp.ginput.virtue;

import java.util.HashMap;

import android.content.Context;

import com.yangxp.ginput.virtue.bean.KeyMapping;
import com.yangxp.ginput.virtue.creater.ClickMotionCreater;
import com.yangxp.ginput.virtue.creater.IMotionCreater;
import com.yangxp.ginput.virtue.creater.JoystickMotionCreater;
import com.yangxp.ginput.virtue.creater.MoveAroundMotionCreater;
import com.yangxp.ginput.virtue.creater.MoveMouseMotionCreater;
import com.yangxp.ginput.virtue.creater.MoveTraceMotionCreater;
import com.yangxp.ginput.virtue.creater.SensorMotionCreater;
import com.yangxp.ginput.virtue.instrument.IMotionInstrument;

public class MotionCreaterFactory {

	
	private static HashMap<Integer,IMotionCreater> instances = new HashMap<Integer,IMotionCreater>();

	
	
	public static IMotionCreater getMotionCreater(int type){
		return instances.get(type);
	}
	
	public static void init(Context context,IMotionInstrument motionInstrument){
		instances.put(KeyMapping.KEY_MAP_TYPE_JOYSTICK, new JoystickMotionCreater(motionInstrument));
		instances.put(KeyMapping.KEY_MAP_TYPE_CLICK, new ClickMotionCreater(motionInstrument));
		instances.put(KeyMapping.KEY_MAP_TYPE_MOVE_AROUND, new MoveAroundMotionCreater(motionInstrument));
		instances.put(KeyMapping.KEY_MAP_TYPE_MOVE_MOUSE, new MoveMouseMotionCreater(context,motionInstrument));
		instances.put(KeyMapping.KEY_MAP_TYPE_MOVE_TRACE, new MoveTraceMotionCreater(motionInstrument));
		instances.put(KeyMapping.KEY_MAP_TYPE_SENSOR, new SensorMotionCreater());
	}
	
	public static boolean checkMotionEvent(float x,float y){
		if(Math.abs(x)<0.1&&Math.abs(y)<0.1){
			return false;
		}
		return true;
	}


}
