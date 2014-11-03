package com.yangxp.ginput.virtue.creater;

import java.nio.ByteBuffer;

import android.os.SystemClock;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

import com.yangxp.ginput.virtue.bean.KeyMapping;
import com.yangxp.ginput.virtue.instrument.IMotionInstrument;

public class MoveTraceMotionCreater implements IMotionCreater{
	int pointDataLen = 12;
	int packHeaderLen = 48;//不包含长度字段4byte
	IMotionInstrument mMotionInstrument;
	int mStatusBarHeight = 0;
	public MoveTraceMotionCreater(IMotionInstrument motionInstrument){
		mMotionInstrument = motionInstrument;
	}
	
	public void setStatusBarHeight(int statusBarHeight){
		mStatusBarHeight = statusBarHeight;
	}
	
	@Override
	public boolean create(InputEvent event,KeyMapping keyMapping){
		//Log.e(this.getClass().getName(), this.getClass().getName());
		if(event instanceof KeyEvent){
			int action = ((KeyEvent)event).getAction();
			if(action == MotionEvent.ACTION_DOWN){
		    	if(keyStatuMap.containsKey(keyMapping.key)){
					return true;
		    	}else{
					ByteBuffer buffer = ByteBuffer.wrap(keyMapping.record);
					if(buffer!=null){
						try{
							createMotionEvent(buffer);
							keyStatuMap.put(keyMapping.key, null);
							return true;
						}catch(Exception e){
							e.printStackTrace();
						}
					}
		    	}
		    	keyStatuMap.put(keyMapping.key, null);
			}else if(action == MotionEvent.ACTION_UP){
				if(keyStatuMap.containsKey(keyMapping.key)){
					keyStatuMap.remove(keyMapping.key);
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	private void createMotionEvent(ByteBuffer buffer){ 
		buffer.rewind();
		long srcStartTime = 0;
		long myStartTime = 0;
		
		long srcEventTime = 0;
		long myEventTime = 0;
		int statusBarHeight = 0 ;
		try{
			statusBarHeight = mStatusBarHeight;
		}catch(Exception e){}
		
		while(buffer.limit()>buffer.position()+4){
			/*
			buffer.putInt(event.getPointerCount()*pointDataLen+packHeaderLen);//长度
			buffer.putInt(event.getActionMasked());//动作类型
			buffer.putLong(event.getDownTime());//按下时间
			buffer.putLong(event.getEventTime());
			buffer.putInt(event.getMetaState());
			buffer.putFloat(event.getXPrecision());
			buffer.putFloat(event.getYPrecision());
			buffer.putInt(event.getDeviceId());
			buffer.putInt(event.getEdgeFlags());
			buffer.putInt(event.getSource());
			buffer.putInt(event.getFlags());
			*/
			//拆包
			int packLen = buffer.getInt();//长度
			ByteBuffer motionPack = ByteBuffer.allocate(packLen);
			buffer.get(motionPack.array());//获取包数据
			int pointerCount = (packLen-packHeaderLen)/pointDataLen;//触摸点数
			int action = motionPack.getInt();//动作类型
			long downTime =  motionPack.getLong();//按下时间
			long eventTime = motionPack.getLong();
			if(srcStartTime==0){
				srcStartTime = downTime;
				myStartTime = downTime = SystemClock.uptimeMillis();
				
				srcEventTime = eventTime;
				myEventTime = eventTime = SystemClock.uptimeMillis();
			}else{
				downTime = myStartTime+(downTime-srcStartTime);
				eventTime = myEventTime+(eventTime-srcEventTime);
			}
			
			
			int metaState = motionPack.getInt();
			float xPrecision = motionPack.getFloat();
			float yPrecision = motionPack.getFloat();
			int deviceId = motionPack.getInt();
			deviceId = 123456;
			int edgeFlags = motionPack.getInt();
			int source = motionPack.getInt();
			int flags = motionPack.getInt();
			
				
			
			PointerCoords[] pointerCoords = new PointerCoords[pointerCount];
			int[] pointerIds = new int[pointerCount];
			for(int i=0;i<pointerCount;i++){
				/*
				buffer.putInt(pointId);
				buffer.putFloat(x);
				buffer.putFloat(y);
				*/
				pointerIds[i] = motionPack.getInt();
				pointerCoords[i] = new PointerCoords();
				pointerCoords[i].x = motionPack.getFloat();
				pointerCoords[i].y = motionPack.getFloat()+statusBarHeight;
				
				//Log.e("createMotionEvent", "pointid:"+":"+pointerCoords[i].x+":"+pointerCoords[i].y+":"+pointerIds[i]);
				
			}
			
			@SuppressWarnings("deprecation")
			MotionEvent motionEvent = MotionEvent.obtain(
					downTime, 
					eventTime, 
					action, 
					pointerCount, 
					pointerIds, 
					pointerCoords, 
					metaState, 
					xPrecision, 
					yPrecision, 
					deviceId, 
					edgeFlags, 
					source, 
					flags);
			mMotionInstrument.sendMotion(motionEvent);
			//Log.e("createMotionEvent", "pointid========================");
		}

		
	}
}
