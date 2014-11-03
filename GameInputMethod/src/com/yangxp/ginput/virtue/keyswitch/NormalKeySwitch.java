package com.yangxp.ginput.virtue.keyswitch;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.yangxp.ginput.virtue.MotionCreaterFactory;
import com.yangxp.ginput.virtue.bean.KeyMapping;
import com.yangxp.ginput.virtue.creater.IMotionCreater;
import com.yangxp.ginput.virtue.db.JoystickDbHelper;
import com.yangxp.ginput.virtue.instrument.IMotionInstrument;

public class NormalKeySwitch implements IKeySwitch{
	private String TAG = "NormalKeySwitch";
	
	private HashMap<Integer,KeyMapping> keyMap = null;
	private HashMap<Integer,Object> typeMap = null;
	
	private String packageName = null;
	private GameMappingJoystickHandler mGameMappingJoystickHandler = null;
	private Context mContext;
	private int mStatusBarHeight = 0;
	
	static NormalKeySwitch instance = null;
	
	
	public NormalKeySwitch(Context context,String packageName){
		this.packageName = packageName;
		mContext = context;
		mGameMappingJoystickHandler = new GameMappingJoystickHandler();
		keyMap = new HashMap<Integer,KeyMapping>();
		typeMap = new HashMap<Integer,Object>();
		parseConfig();
	}
	
	public void parseConfig(){
		keyMap.clear();
		typeMap.clear();
		try{
			PackageInfo info = mContext.getPackageManager().getPackageInfo(packageName, 0);  
			
			List<KeyMapping> keyMappings = JoystickDbHelper.queryMappings(packageName,info.versionCode);
			if(keyMappings!=null){
				for(KeyMapping keyMapping:keyMappings){
					keyMapping.y +=mStatusBarHeight;//add the satus bar heitht
					keyMap.put(keyMapping.key, keyMapping);
					typeMap.put(keyMapping.type, null);
					Log.i(TAG, keyMapping.toString());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public boolean changeEvent(InputEvent event){
		//Log.i(TAG,"NormalKeySwitch changeEvent");
		if("com.qucii.gameconfig".equals(this.packageName)){
			return false;
		}
		
		KeyMapping keyMapping = null;
		if(event instanceof KeyEvent){
			
			keyMapping = keyMap.get(((KeyEvent)event).getKeyCode());
			//判断是否在mouse clickKey中
			if(keyMapping==null){
				if(keyMap.containsKey(GameMappingJoystickHandler.LEFT_JOYSTICK)&&
						keyMap.get(GameMappingJoystickHandler.LEFT_JOYSTICK).type==KeyMapping.KEY_MAP_TYPE_MOVE_MOUSE&&
						keyMap.get(GameMappingJoystickHandler.LEFT_JOYSTICK).keyClick==((KeyEvent)event).getKeyCode()){
					keyMapping = keyMap.get(GameMappingJoystickHandler.LEFT_JOYSTICK);
				}else if(keyMap.containsKey(GameMappingJoystickHandler.RIGHT_JOYSTICK)&&
						keyMap.get(GameMappingJoystickHandler.RIGHT_JOYSTICK).type==KeyMapping.KEY_MAP_TYPE_MOVE_MOUSE&&
						keyMap.get(GameMappingJoystickHandler.RIGHT_JOYSTICK).keyClick==((KeyEvent)event).getKeyCode()){
					keyMapping = keyMap.get(GameMappingJoystickHandler.RIGHT_JOYSTICK);
				}
			}
			
			//Log.i(TAG, "keyMapping is "+(keyMapping!=null?keyMapping.toString():"")+"null");
			
			if(keyMapping!=null&&
					MotionCreaterFactory.getMotionCreater(keyMapping.type).create(event, keyMapping)){
				return true;
			}
		}else if(event instanceof MotionEvent){
			//Log.i(TAG, "is MotionEvent");
			MotionEvent motionEvent = (MotionEvent)event;
			return mGameMappingJoystickHandler.update(motionEvent, true);
		}
		return false;
	}
    @Override
	public boolean containsKeyCode(int keyCode){
		return keyMap.containsKey(keyCode) ;
	}
	
	@Override
	public boolean containsType(int type) {
		// TODO Auto-generated method stub
		
		return typeMap.containsKey(type);
	}
	
	public void setStatusBarHeight(int statusBarHeight){
		mStatusBarHeight = statusBarHeight;
		parseConfig();
	}
	
	public int getStatusBarHeight(){
		return mStatusBarHeight;
	}
	
    private boolean changeMotionEvent(MotionEvent event,int keyCode){
    	//Log.i(TAG, "changeMotionEvent keyCode:"+keyCode);
    	KeyMapping keyMapping = keyMap.get(keyCode);
		if(keyMapping!=null&&
				MotionCreaterFactory.getMotionCreater(keyMapping.type).create(event, keyMapping)){
			return true;
		}
		return false;
    }
	
    /**
     * Creates dpad events from unhandled joystick movements.
     */
    final class GameMappingJoystickHandler extends Handler {
        private final static int MSG_ENQUEUE_AXIS_KEY_REPEAT = 3;
        
        private final static int Y_UP_KEYCODE = 222;
        private final static int Y_DOWN_KEYCODE = 223;
        private final static int X_LEFI_KEYCODE = 224;
        private final static int X_RIGHT_KEYCODE = 225;

        private final static int RY_UP_KEYCODE = 226;
        private final static int RY_DOWN_KEYCODE = 227;
        private final static int RX_LEFI_KEYCODE = 228;
        private final static int RX_RIGHT_KEYCODE = 229;

        private final static int LTRIGGER_KEYCODE = 230;
        private final static int RTRIGGER_KEYCODE = 231;
        
        private final static int LEFT_JOYSTICK = 220;
        private final static int RIGHT_JOYSTICK = 221;

        private int mLastXDirection;
        private int mLastYDirection;
        private int mLastXKeyCode;
        private int mLastYKeyCode;
        
        private int mLastZDirection;
        private int mLastRZDirection;
        private int mLastZKeyCode;
        private int mLastRZKeyCode;
        
        private int mLastHAT_XDirection;
        private int mLastHAT_YDirection;
        private int mLastHAT_XKeyCode;
        private int mLastHAT_YKeyCode;
        
        private int LTRIGGERDirection;
        private int RTRIGGERDirection;
        private int LTRIGGERKeyCode;
        private int RTRIGGERKeyCode;
        
        

        public GameMappingJoystickHandler() {
            //super(true);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ENQUEUE_AXIS_KEY_REPEAT: {
                   /*目前无需重复发送事件
                	KeyEvent oldEvent = (KeyEvent)msg.obj;
                    KeyEvent e = KeyEvent.changeTimeRepeat(oldEvent,
                            SystemClock.uptimeMillis(),
                            oldEvent.getRepeatCount() + 1);
                    
                  	changeEvent(e);
                    Message m = obtainMessage(msg.what, e);
                    //m.setAsynchronous(true);
                    sendMessageDelayed(m, ViewConfiguration.getKeyRepeatDelay());
                    */
                } break;
            }
        }

        public boolean process(MotionEvent event) {
            return update(event, true);
        }

        public void cancel(MotionEvent event) {
            update(event, false);
        }

        private boolean sendKeyEvent(MotionEvent event,int keyCode,int action){
        	Log.i(TAG, "sendKeyEvent keyCode:"+keyCode+":action:"+action);
            final long time = event.getEventTime();
            final int metaState = event.getMetaState();
            final int deviceId = event.getDeviceId();
            final int source = event.getSource();
            
        	if(action == KeyEvent.ACTION_DOWN){
                final KeyEvent e = new KeyEvent(time, time,
                        KeyEvent.ACTION_DOWN, keyCode, 0, metaState,
                        deviceId, 0, KeyEvent.FLAG_FALLBACK, source);
            	changeEvent(e);
                Message m = obtainMessage(MSG_ENQUEUE_AXIS_KEY_REPEAT, e);
                sendMessageDelayed(m, ViewConfiguration.getKeyRepeatTimeout());
        	}else{
                removeMessages(MSG_ENQUEUE_AXIS_KEY_REPEAT);
                KeyEvent e = new KeyEvent(time, time,
                        KeyEvent.ACTION_UP, keyCode, 0, metaState,
                        deviceId, 0, KeyEvent.FLAG_FALLBACK, source);
                changeEvent(e);
        	}
        	return true;
        }

        private boolean update(MotionEvent event, boolean synthesizeNewKeys) {

        	boolean result = false;
            int xDirection = joystickAxisValueToDirection(event.getX());
            int yDirection = joystickAxisValueToDirection(event.getY());
            //左摇杆
            if (xDirection != mLastXDirection && mLastXKeyCode != 0 && containsKeyCode(mLastXKeyCode)) {
            	sendKeyEvent(event,mLastXKeyCode,KeyEvent.ACTION_UP);
                mLastXKeyCode = 0;
                result = true;
            }else if (xDirection != 0 && synthesizeNewKeys) {
            	mLastXDirection = xDirection;
                mLastXKeyCode = xDirection > 0
                        ? X_RIGHT_KEYCODE : X_LEFI_KEYCODE;
                if(containsKeyCode(mLastXKeyCode)){
                	sendKeyEvent(event,mLastXKeyCode,KeyEvent.ACTION_DOWN);
                    result = true;
                }else{
                	mLastXKeyCode = 0;
            	}
            }

            if (yDirection != mLastYDirection && mLastYKeyCode != 0 && containsKeyCode(mLastYKeyCode)) {
                sendKeyEvent(event,mLastYKeyCode,KeyEvent.ACTION_UP);;
                mLastYKeyCode = 0;
                result = true;
            }else if (yDirection != 0 && synthesizeNewKeys) {
            	mLastYDirection = yDirection;
                mLastYKeyCode = yDirection > 0
                        ? Y_DOWN_KEYCODE : Y_UP_KEYCODE;
                if(containsKeyCode(mLastYKeyCode)){
                    sendKeyEvent(event,mLastYKeyCode,KeyEvent.ACTION_DOWN);
                    result = true;
                }else{
                	mLastYKeyCode = 0;
            	}
            }   


            //右摇杆
            xDirection = joystickAxisValueToDirection(
                    event.getAxisValue(MotionEvent.AXIS_Z));
            yDirection = joystickAxisValueToDirection(
                    event.getAxisValue(MotionEvent.AXIS_RZ));
            if (xDirection != mLastZDirection && mLastZKeyCode != 0 && containsKeyCode(mLastZKeyCode)) {
                sendKeyEvent(event,mLastZKeyCode,KeyEvent.ACTION_UP);
                mLastZKeyCode = 0;
            }else if (xDirection != 0 && synthesizeNewKeys) {
            	mLastZDirection = xDirection;
            	mLastZKeyCode = xDirection > 0
                        ? RX_RIGHT_KEYCODE : RX_LEFI_KEYCODE;
            	if(containsKeyCode(mLastZKeyCode)){
                    sendKeyEvent(event,mLastZKeyCode,KeyEvent.ACTION_DOWN);
                    result = true;
            	}else{
            		mLastZKeyCode = 0;
            	}
            }
            if (yDirection != mLastRZDirection && mLastRZKeyCode != 0 && containsKeyCode(mLastRZKeyCode)) {
                sendKeyEvent(event,mLastRZKeyCode,KeyEvent.ACTION_UP);
                mLastRZKeyCode = 0;
                result = true;
            }else if (yDirection != 0 && synthesizeNewKeys) {
            	mLastRZDirection = yDirection;
            	mLastRZKeyCode = yDirection > 0
                        ? RY_DOWN_KEYCODE : RY_UP_KEYCODE;
            	if(containsKeyCode(mLastRZKeyCode)){
                    sendKeyEvent(event,mLastRZKeyCode,KeyEvent.ACTION_DOWN);
                    result = true;
            	}else{
            		mLastRZKeyCode = 0;
            	}
            }
            
            //左右摇杆只要设置了一种方向键，则改摇杆数据不再发送到应用程序
            if(mLastXKeyCode != 0 && mLastYKeyCode != 0 && 
            		(containsKeyCode(X_RIGHT_KEYCODE) 
            				|| containsKeyCode( X_LEFI_KEYCODE) 
            				|| containsKeyCode(  Y_DOWN_KEYCODE ) 
            				|| containsKeyCode( Y_UP_KEYCODE))){
            	result =true;
            }

            if(mLastZKeyCode != 0 && mLastRZKeyCode != 0 && 
            		(containsKeyCode(RX_RIGHT_KEYCODE) 
            				|| containsKeyCode( RX_LEFI_KEYCODE) 
            				|| containsKeyCode(  RY_DOWN_KEYCODE ) 
            				|| containsKeyCode( RY_UP_KEYCODE))){
            	result =true;
            }

            if(mLastXKeyCode == 0 && mLastYKeyCode == 0 && containsKeyCode(LEFT_JOYSTICK)){
            	result = changeMotionEvent(event,LEFT_JOYSTICK);
            }

            if(mLastZKeyCode == 0 && mLastRZKeyCode == 0 && containsKeyCode(RIGHT_JOYSTICK)){
            	if(changeMotionEvent(event,RIGHT_JOYSTICK)){
            		result = true;
            	}
            }
            
            //DPAD KEY
            xDirection = joystickAxisValueToDirection(
                    event.getAxisValue(MotionEvent.AXIS_HAT_X));
            yDirection = joystickAxisValueToDirection(
                    event.getAxisValue(MotionEvent.AXIS_HAT_Y));
            if (xDirection != mLastHAT_XDirection && mLastHAT_XKeyCode != 0 && containsKeyCode(mLastHAT_XKeyCode)) {
                sendKeyEvent(event,mLastHAT_XKeyCode,KeyEvent.ACTION_UP);
                mLastHAT_XKeyCode = 0;
                result = true;
            }else if (xDirection != 0 && synthesizeNewKeys) {
            	mLastHAT_XDirection = xDirection;
            	mLastHAT_XKeyCode = xDirection > 0
                        ? KeyEvent.KEYCODE_DPAD_RIGHT : KeyEvent.KEYCODE_DPAD_LEFT;
            	if(containsKeyCode(mLastHAT_XKeyCode)){
                    sendKeyEvent(event,mLastHAT_XKeyCode,KeyEvent.ACTION_DOWN);
                    result = true;
            	}else{
            		mLastHAT_XKeyCode = 0;
            	}
            }
            
            if (yDirection != mLastHAT_YDirection && mLastHAT_YKeyCode != 0 && containsKeyCode(mLastHAT_YKeyCode)) {
                sendKeyEvent(event,mLastHAT_YKeyCode,KeyEvent.ACTION_UP);
                mLastHAT_YKeyCode = 0;
                result = true;
            }else if (yDirection != 0 && synthesizeNewKeys) {
            	mLastHAT_YDirection = yDirection;
            	mLastHAT_YKeyCode = yDirection > 0
                        ? KeyEvent.KEYCODE_DPAD_DOWN : KeyEvent.KEYCODE_DPAD_UP;
            	if(containsKeyCode(mLastHAT_YKeyCode)){
            		sendKeyEvent(event,mLastHAT_YKeyCode,KeyEvent.ACTION_DOWN);
                    result = true;
            	}else{
            		mLastHAT_YKeyCode = 0;
            	}
            }

            
            //RT LT KEY
            xDirection = joystickAxisValueToDirection(
                    event.getAxisValue(MotionEvent.AXIS_LTRIGGER));
            yDirection = joystickAxisValueToDirection(
                    event.getAxisValue(MotionEvent.AXIS_RTRIGGER));
            
            if (xDirection != LTRIGGERDirection && LTRIGGERKeyCode != 0 && containsKeyCode(LTRIGGERKeyCode)) {
            	sendKeyEvent(event,LTRIGGERKeyCode,KeyEvent.ACTION_UP);;
                LTRIGGERKeyCode = 0;
                result = true;
            }else if (xDirection != 0 && synthesizeNewKeys) {
            	LTRIGGERDirection = xDirection;
            	LTRIGGERKeyCode = xDirection > 0
                        ? LTRIGGER_KEYCODE : 0;
            	if(containsKeyCode(LTRIGGERKeyCode)){
            		sendKeyEvent(event,LTRIGGERKeyCode,KeyEvent.ACTION_DOWN);
                    result = true;
            	}else{
            		LTRIGGERKeyCode = 0;
            	}
            }
            if (yDirection != RTRIGGERDirection && RTRIGGERKeyCode != 0 && containsKeyCode(RTRIGGERKeyCode)) {
            	sendKeyEvent(event,RTRIGGERKeyCode,KeyEvent.ACTION_UP);
                RTRIGGERKeyCode = 0;
                result = true;
            }else if (yDirection != 0 && synthesizeNewKeys) {
            	RTRIGGERDirection = yDirection;
            	RTRIGGERKeyCode = yDirection > 0
                        ? RTRIGGER_KEYCODE : 0;
            	if(containsKeyCode(RTRIGGERKeyCode)){
            		sendKeyEvent(event,RTRIGGERKeyCode,KeyEvent.ACTION_DOWN);
                    result = true;
            	}else{
            		RTRIGGERKeyCode = 0;
            	}
            }
            
            return result;
        }

        private int joystickAxisValueToDirection(float value) {
            if (value >= 0.2f) {
                return 1;
            } else if (value <= -0.2f) {
                return -1;
            } else {
                return 0;
            }
        }
    }



}
