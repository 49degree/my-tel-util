package com.yangxp.ginput.virtue.instrument;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

import com.yangxp.ginput.virtue.bean.MotionBean;

public class NormalMotionInstrument implements IMotionInstrument{


	static String TAG = "NormalMotionInstrument";
	private ExecutorService service = null;
	private Instrumentation instrumentation = null;
	
	private TreeSet<Integer> emptyPointIndex = new TreeSet<Integer>();
	private HashMap<Integer,MotionBean> pointMap = new HashMap<Integer,MotionBean>();
	
	private final static int MAX_LEN = 16;
	
	private PointerCoords[] pointerCoords = new PointerCoords[MAX_LEN];
	
	//private int pointerCount = 0;//´¥ÃþµãÊý
	private int action = MotionEvent.ACTION_DOWN;
	
	private long downTime = 0;
	private long eventTime = 0;
	
	public NormalMotionInstrument(){
		service = Executors.newSingleThreadExecutor();
		instrumentation = new Instrumentation();

		for(int i=0;i<MAX_LEN;i++){
			pointerCoords[i] = new PointerCoords();
			emptyPointIndex.add(i);
		}
	}
	

	@Override
	public synchronized boolean sendMotion(MotionBean motionBean) {
		// TODO Auto-generated method stub
		try{
			if(motionBean.pointId == -1){
				if(emptyPointIndex.size()==0)
					return false;
				motionBean.pointId = emptyPointIndex.pollFirst();
			}
				
			pointMap.put(motionBean.pointId, motionBean);
			
			action = motionBean.action;
			
			if(pointMap.size()>1){
				if(action == MotionEvent.ACTION_DOWN)
					action = MotionEvent.ACTION_POINTER_DOWN;
				else if(action == MotionEvent.ACTION_UP)
					action = MotionEvent.ACTION_POINTER_UP;
			}else{
				//init event down time
				if(action == MotionEvent.ACTION_DOWN)
					downTime = SystemClock.uptimeMillis();
			}
			//Log.e("createMotionEvent", "pointid=======pointMap.size()="+pointMap.size()+";action="+action+";motionBean.pointId="+motionBean.pointId);
			
			//init event time
			eventTime = SystemClock.uptimeMillis();
			

			Iterator<Integer> keys = pointMap.keySet().iterator();
			int i = 0;
			while(keys.hasNext()){
				if(keys.next()==motionBean.pointId){
					action |= (i<<MotionEvent.ACTION_POINTER_INDEX_SHIFT);
					break;
				}
				i++;
			}
			
			sendMotion(createMotionEvent());
			
			if(motionBean.action==MotionEvent.ACTION_UP){
				pointMap.remove(motionBean.pointId);
				emptyPointIndex.add(motionBean.pointId);
				motionBean.pointId = -1;
				//clear down time
				if(pointMap.size()==0)
					downTime = 0;
			}
		}catch(Exception e){
			
		}

		
		return true;
	}

	
	@Override
	public boolean sendMotion(final MotionEvent event) {
		// TODO Auto-generated method stub
    	if(event!=null){
    		service.execute(new Runnable(){
    			public void run(){
    				try{
    					//Log.e(TAG, event.toString());
    					instrumentation.sendPointerSync(event);
    				}catch(Exception e){
    					e.printStackTrace();
    				}
    			}
    		});
    		return true; 
    		
    	}
		return false;
		
	}
	
	
	private MotionEvent createMotionEvent(){
		
		int metaState = 0;
		int buttonState = 0;
		float xPrecision = 1.0f;
		float yPrecision = 1.0f;
		int deviceId = 0;
		int edgeFlags = 0;
		int source = InputDevice.SOURCE_TOUCHSCREEN|InputDevice.SOURCE_CLASS_POINTER;
		int flags = 0;
		//int32_t source = AINPUT_SOURCE_TOUCHSCREEN|AINPUT_SOURCE_CLASS_POINTER;
		
		int tempPointCount = pointMap.size();
		
		PointerProperties[] pointerProperties = new PointerProperties[tempPointCount];
		
		int[] pointerIds = new int[tempPointCount];
		PointerCoords[] sendPointerCoords = new PointerCoords[tempPointCount];
		Iterator<Integer> keys = pointMap.keySet().iterator();
		int i = 0;
		while(keys.hasNext()){
			pointerIds[i] = keys.next();
			
			PointerProperties ppi = new PointerProperties();
			ppi.id = pointerIds[i] ;
			ppi.toolType = MotionEvent.TOOL_TYPE_FINGER;
			pointerProperties[i] = ppi;
			
			sendPointerCoords[i] = pointerCoords[pointerIds[i]];
			sendPointerCoords[i].x = pointMap.get(pointerIds[i]).x;
			sendPointerCoords[i].y = pointMap.get(pointerIds[i]).y;
			i++;
		}

		/*
		@SuppressWarnings("deprecation")
		MotionEvent motionEvent = MotionEvent.obtain(
				downTime, 
				eventTime, 
				action, 
				tempPointCount, 
				pointerIds, 
				sendPointerCoords, 
				metaState, 
				xPrecision, 
				yPrecision, 
				deviceId, 
				edgeFlags, 
				source, 
				flags);
		*/
		MotionEvent motionEvent = MotionEvent.obtain(
				downTime, 
				eventTime, 
				action, 
				tempPointCount, 
				pointerProperties, 
				sendPointerCoords,
				metaState,
				buttonState, 
				xPrecision, 
				yPrecision, 
				deviceId, 
				edgeFlags, 
				source, 
				flags);
		
		//Log.e("createMotionEvent", "pointid========================"+motionEvent.toString());
		
		
		return motionEvent;
	}




}
