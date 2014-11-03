package com.yangxp.config.view.button;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ButtonUtil {
	static String TAG = "ButtonUtil";
	public static String getKeyText(int keyCode) {
		String text = "";
		switch (keyCode) {
		case KeyEvent.KEYCODE_BUTTON_A:
			text = "○按键";
			break;
		case KeyEvent.KEYCODE_BUTTON_B:
			text = "×按键";
			break;
		case KeyEvent.KEYCODE_BUTTON_X:
			text = "□按键";
			break;
		case KeyEvent.KEYCODE_BUTTON_Y:
			text = "△按键";
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			text = "右按键";
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			text = "左按键";
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			text = "下按键";
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			text = "上按键";
			break;
		case MapperViewInterface.X_LEFT_KEYCODE:
			text = "左摇杆左摇";
			break;
		case MapperViewInterface.X_RIGHT_KEYCODE:
			text = "左摇杆右摇";
			break;
		case MapperViewInterface.Y_DOWN_KEYCODE:
			text = "左摇杆下摇";
			break;
		case MapperViewInterface.Y_UP_KEYCODE:
			text = "左摇杆上摇";
			break;
		case MapperViewInterface.RX_LEFT_KEYCODE:
			text = "右摇杆左摇";
			break;
		case MapperViewInterface.RX_RIGHT_KEYCODE:
			text = "右摇杆右摇";
			break;
		case MapperViewInterface.RY_DOWN_KEYCODE:
			text = "右摇杆下摇";
			break;
		case MapperViewInterface.RY_UP_KEYCODE:
			text = "右摇杆上摇";
			break;
		case KeyEvent.KEYCODE_BUTTON_L1:
			text = "LT按键";
			break;
		case KeyEvent.KEYCODE_BUTTON_R1:
			text = "RT按键";
			break;
		case KeyEvent.KEYCODE_BUTTON_L2:
			text = "LB按键";
			break;
		case KeyEvent.KEYCODE_BUTTON_R2:
			text = "RB按键";
			break;
		case KeyEvent.KEYCODE_BUTTON_THUMBL:
			text = "左摇杆按键";
			break;
		case KeyEvent.KEYCODE_BUTTON_THUMBR:
			text = "右摇杆按键";
			break;
		case MapperViewInterface.LEFT_JOYSTICK:
			Log.i(TAG, "LEFT_JOYSTICK = " + keyCode);
			text = "左摇杆";
			break;
		case MapperViewInterface.RIGHT_JOYSTICK:
			text = "右摇杆";
			break;
		default:
			text = "";
			break;
		}
		return text;
	}
	
	/**
	 * 计算摇杆的方向键
	 * @param event
	 * @return
	 */
    public static int update(MotionEvent event) {
    	int joystickKeycode = -1;
        int xDirection = joystickAxisValueToDirection(event.getX());
        int yDirection = joystickAxisValueToDirection(event.getY());
        
        //左摇杆
        if(xDirection != 0){
        	joystickKeycode = xDirection > 0 ? MapperViewInterface.X_RIGHT_KEYCODE : MapperViewInterface.X_LEFT_KEYCODE;
        	return joystickKeycode;
        }
        
        if(yDirection != 0){
        	joystickKeycode = yDirection > 0 ? MapperViewInterface.Y_DOWN_KEYCODE : MapperViewInterface.Y_UP_KEYCODE;
        	return joystickKeycode;
        }

        //右摇杆
        xDirection = joystickAxisValueToDirection(event.getAxisValue(MotionEvent.AXIS_Z));
        yDirection = joystickAxisValueToDirection(event.getAxisValue(MotionEvent.AXIS_RZ));
        if(xDirection != 0){
        	joystickKeycode = xDirection > 0 ? MapperViewInterface.RX_RIGHT_KEYCODE : MapperViewInterface.RX_LEFT_KEYCODE;
        	return joystickKeycode;
        }
        
        if(yDirection != 0){
        	joystickKeycode = yDirection > 0 ? MapperViewInterface.RY_DOWN_KEYCODE : MapperViewInterface.RY_UP_KEYCODE;
        	return joystickKeycode;
        }

        
        //DPAD KEY
        xDirection = joystickAxisValueToDirection(event.getAxisValue(MotionEvent.AXIS_HAT_X));
        yDirection = joystickAxisValueToDirection(event.getAxisValue(MotionEvent.AXIS_HAT_Y));
        if(xDirection != 0){
        	joystickKeycode = xDirection > 0 ? KeyEvent.KEYCODE_DPAD_RIGHT : KeyEvent.KEYCODE_DPAD_LEFT;
        	return joystickKeycode;
        }
        
        if(yDirection != 0){
        	joystickKeycode = yDirection > 0 ? KeyEvent.KEYCODE_DPAD_DOWN  : KeyEvent.KEYCODE_DPAD_UP;
        	return joystickKeycode;
        }
        
        /*
        //RT LT KEY
        xDirection = joystickAxisValueToDirection(event.getAxisValue(MotionEvent.AXIS_LTRIGGER));
        yDirection = joystickAxisValueToDirection(event.getAxisValue(MotionEvent.AXIS_RTRIGGER));
        if(xDirection != 0){
        	joystickKeycode = xDirection > 0 ? KeyEvent.KEYCODE_BUTTON_L1 : joystickKeycode;
        	return joystickKeycode;
        }
        if(yDirection != 0){
        	joystickKeycode = yDirection > 0 ? KeyEvent.KEYCODE_BUTTON_R1  : joystickKeycode;
        	return joystickKeycode;
        }
        */
        return joystickKeycode;
    }

    private static int joystickAxisValueToDirection(float value) {
        if (value >= 0.5f) {
            return 1;
        } else if (value <= -0.5f) {
            return -1;
        } else {
            return 0;
        }
    }	
}
