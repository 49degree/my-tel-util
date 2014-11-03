package com.yangxp.config.view.button;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.yangxp.config.bean.Mappings;
import com.yangxp.ginput.R;

public class ClickMotionButton extends MapperButton{
	private static String TAG = "ClickMotionButton";
	public ClickMotionButton(Context context,Mappings keyMapping,HashMap<Integer,MapperViewInterface> mapperButtonKeyMap){
		super(context,keyMapping,mapperButtonKeyMap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setImage() {
		// TODO Auto-generated method stub
		int resId = 0;
		if(mIsActive){
			switch(mKeyMapping.key){
			case KeyEvent.KEYCODE_BUTTON_A:
				resId = R.drawable.dragged_button_a_100p_active;
				break;
			case KeyEvent.KEYCODE_BUTTON_B:
				resId = R.drawable.dragged_button_b_100p_active;
				break;
			case KeyEvent.KEYCODE_BUTTON_X:
				resId = R.drawable.dragged_button_x_100p_active;
				break;
			case KeyEvent.KEYCODE_BUTTON_Y:
				resId = R.drawable.dragged_button_y_100p_active;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				resId = R.drawable.dragged_button_dpad_right_100p_active;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				resId = R.drawable.dragged_button_dpad_left_100p_active;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				resId = R.drawable.dragged_button_dpad_down_100p_active;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				resId = R.drawable.dragged_button_dpad_up_100p_active;
				break;
			case MapperButton.X_LEFT_KEYCODE:
				resId = R.drawable.dragged_button_ls_left_100p_active;
				break;
			case MapperButton.X_RIGHT_KEYCODE:
				resId = R.drawable.dragged_button_ls_right_100p_active;
				break;
			case MapperButton.Y_DOWN_KEYCODE:
				resId = R.drawable.dragged_button_ls_down_100p_active;
				break;
			case MapperButton.Y_UP_KEYCODE:
				resId = R.drawable.dragged_button_ls_up_100p_active;
				break;
			case MapperButton.RX_LEFT_KEYCODE:
				resId = R.drawable.dragged_button_rs_left_100p_active;
				break;
			case MapperButton.RX_RIGHT_KEYCODE:
				resId = R.drawable.dragged_button_rs_right_100p_active;
				break;
			case MapperButton.RY_DOWN_KEYCODE:
				resId = R.drawable.dragged_button_rs_down_100p_active;
				break;
			case MapperButton.RY_UP_KEYCODE:
				resId = R.drawable.dragged_button_rs_up_100p_active;
				break;
			case KeyEvent.KEYCODE_BUTTON_L1:
				resId = R.drawable.dragged_button_lb_100p_active;
				break;
			case KeyEvent.KEYCODE_BUTTON_R1:
				resId = R.drawable.dragged_button_rb_100p_active;
				break;
			case KeyEvent.KEYCODE_BUTTON_L2:
				resId = R.drawable.dragged_lt_focus;
				break;
			case KeyEvent.KEYCODE_BUTTON_R2:
				resId = R.drawable.dragged_rt_focus;
				break;
			case KeyEvent.KEYCODE_BUTTON_THUMBL:
				resId = R.drawable.dragged_button_ls_100p_active;
				break;
			case KeyEvent.KEYCODE_BUTTON_THUMBR:
				resId = R.drawable.dragged_button_rs_100p_active;
				break;
			default:
				resId = R.drawable.dragged_button_unassigned_100p_active;
				break;
	        }
			
		}else{
			switch(mKeyMapping.key){
			case KeyEvent.KEYCODE_BUTTON_A:
				resId = R.drawable.dragged_button_a_100p_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_B:
				resId = R.drawable.dragged_button_b_100p_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_X:
				resId = R.drawable.dragged_button_x_100p_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_Y:
				resId = R.drawable.dragged_button_y_100p_normal;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				resId = R.drawable.dragged_button_dpad_right_100p_normal;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				resId = R.drawable.dragged_button_dpad_left_100p_normal;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				resId = R.drawable.dragged_button_dpad_down_100p_normal;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				resId = R.drawable.dragged_button_dpad_up_100p_normal;
				break;
			case MapperButton.X_LEFT_KEYCODE:
				resId = R.drawable.dragged_button_ls_left_100p_normal;
				break;
			case MapperButton.X_RIGHT_KEYCODE:
				resId = R.drawable.dragged_button_ls_right_100p_normal;
				break;
			case MapperButton.Y_DOWN_KEYCODE:
				resId = R.drawable.dragged_button_ls_down_100p_normal;
				break;
			case MapperButton.Y_UP_KEYCODE:
				resId = R.drawable.dragged_button_ls_up_100p_normal;
				break;
			case MapperButton.RX_LEFT_KEYCODE:
				resId = R.drawable.dragged_button_rs_left_100p_normal;
				break;
			case MapperButton.RX_RIGHT_KEYCODE:
				resId = R.drawable.dragged_button_rs_right_100p_normal;
				break;
			case MapperButton.RY_DOWN_KEYCODE:
				resId = R.drawable.dragged_button_rs_down_100p_normal;
				break;
			case MapperButton.RY_UP_KEYCODE: 
				resId = R.drawable.dragged_button_rs_up_100p_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_L1:
				resId = R.drawable.dragged_button_lb_100p_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_R1:
				resId = R.drawable.dragged_button_rb_100p_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_L2:
				resId = R.drawable.dragged_lt_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_R2:
				resId = R.drawable.dragged_rt_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_THUMBL:
				resId = R.drawable.dragged_button_ls_100p_normal;
				break;
			case KeyEvent.KEYCODE_BUTTON_THUMBR:
				resId = R.drawable.dragged_button_rs_100p_normal;
				break;				
			default:
				resId = R.drawable.dragged_button_unassigned_100p_normal;
				break;
	        }
		}

		setImageResource(resId) ;
		
		try{
			Bitmap resouseBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
			if(resouseBitmap.getWidth()>0)
				mKeyMapping.radius = resouseBitmap.getWidth()/2;
			resouseBitmap.recycle();
		}catch(Exception e){
			
		}
		

	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event){
		mViewTouchListener.onViewTouch(this);
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onJoysticMotion(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		int joyKey = ButtonUtil.update(event);
		
		Log.i(TAG, "onJoysticMotion action="+action+":"+joyKey );
		if(joyKey==-1 || mMapperButtonKeyMap==null )
			return true;
		
		if(joyKey == X_RIGHT_KEYCODE||
				joyKey == X_LEFT_KEYCODE||
				joyKey == Y_DOWN_KEYCODE||
				joyKey == Y_UP_KEYCODE){
			Log.i(TAG, "onJoysticMotion left joystick"+action );
			if(mMapperButtonKeyMap.containsKey(MapperButton.LEFT_JOYSTICK)){
				showInfoByString(R.string.info_bar_joystick_code_beused_help);
				return true;
			}
		}else if(joyKey == RX_RIGHT_KEYCODE||
				joyKey == RX_LEFT_KEYCODE||
				joyKey == RY_DOWN_KEYCODE||
				joyKey == RY_UP_KEYCODE){
			Log.i(TAG, "onJoysticMotion right joystick"+action );
			if(mMapperButtonKeyMap.containsKey(MapperButton.RIGHT_JOYSTICK)){
				showInfoByString(R.string.info_bar_joystick_code_beused_help);
				return true;
			}
				
		}
		
		if(mMapperButtonKeyMap.containsKey(joyKey)&&
				mMapperButtonKeyMap.get(joyKey)!=this){
			showInfoByString(R.string.info_bar_joystick_code_beused_help);
			return true;
		}else{
			if(mMapperButtonKeyMap.containsKey(mKeyMapping.key))
				mMapperButtonKeyMap.remove(mKeyMapping.key);
			mKeyMapping.key = joyKey;
			mMapperButtonKeyMap.put(mKeyMapping.key, this);
			Log.i(TAG, "onJoysticMotion action="+action+":"+joyKey );
			setImage();
			return true;
		}
	}

	@Override
	public boolean onJoysticKey(KeyEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		//Log.i(TAG, "onJoysticMotion action="+action+":"+event.getKeyCode() );
		
		if(mMapperButtonKeyMap==null )
			return false;
		
		Iterator<Map.Entry<Integer,MapperViewInterface>> it = mMapperButtonKeyMap.entrySet().iterator();
		int keyCount = 0;
		while(it.hasNext()){
			if(it.next().getValue().getKeyMapping().keyClick == event.getKeyCode()){
				keyCount=1;
				break;
			}
		}
		
		if(keyCount>0||
				(mMapperButtonKeyMap.containsKey(event.getKeyCode())&&
						mMapperButtonKeyMap.get(event.getKeyCode())!=this)){
			showInfoByString(R.string.info_bar_key_code_beused_help);
			return true;
		}else{
			
			if(mMapperButtonKeyMap.containsKey(mKeyMapping.key))
				mMapperButtonKeyMap.remove(mKeyMapping.key);			
			mKeyMapping.key = event.getKeyCode();
			mMapperButtonKeyMap.put(mKeyMapping.key, this);
			setImage();
			return true;
		}
	}

	@Override
	public void showNewButtonNotice() {
		// TODO Auto-generated method stub
		mInformationBar.informationBarShow();
		mInformationBar.showInformations(R.string.info_bar_button_unassigned_help);
	}

	
}
