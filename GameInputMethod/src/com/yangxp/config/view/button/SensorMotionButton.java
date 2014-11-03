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

import com.yangxp.config.bean.KeyMapping;
import com.yangxp.config.bean.Mappings;
import com.yangxp.config.view.InformationBar;
import com.yangxp.config.view.exception.NewButtonException;
import com.yangxp.ginput.R;

public class SensorMotionButton extends MapperButton{
	private static String TAG = "SensorMotionButton";
	public SensorMotionButton(Context context,Mappings keyMapping,HashMap<Integer,MapperViewInterface> mapperButtonKeyMap) throws NewButtonException{
		super(context,keyMapping,mapperButtonKeyMap);
		// TODO Auto-generated constructor stub
		Iterator<Map.Entry<Integer,MapperViewInterface>> it = mMapperButtonKeyMap.entrySet().iterator();
		int sensorCount = 0;
		while(it.hasNext()){
			if(it.next().getValue().getKeyMapping().type == KeyMapping.KEY_MAP_TYPE_SENSOR){
				sensorCount++;
				continue;
			}
		}
		
		if(mKeyMapping.key!=0){
			mMapperButtonKeyMap.put(mKeyMapping.key, this);
		} else {
			//新建按钮
			mInformationBar = new InformationBar(mContext);
		}
		
		if(sensorCount==2&&mKeyMapping.key==0)
			throw new NewButtonException("button is on");
	}
	@Override
	public void setImage() {
		// TODO Auto-generated method stub
		int resId = 0;
		if(mIsActive){
			if(mKeyMapping.key == RIGHT_JOYSTICK){
				resId = R.drawable.dragged_sensor_rs_active ;
			}else if(mKeyMapping.key == LEFT_JOYSTICK){
				resId = R.drawable.dragged_sensor_ls_active ;
			}else{
				resId = R.drawable.dragged_sensor_active ;
			}
		}else{
			if(mKeyMapping.key == RIGHT_JOYSTICK){
				resId = R.drawable.dragged_sensor_rs_normal ;
			}else if(mKeyMapping.key == LEFT_JOYSTICK){
				resId = R.drawable.dragged_sensor_ls_normal ;
			}else{
				resId = R.drawable.dragged_sensor_normal ;
			}
			
		}
		
		setImageResource(resId) ;
		
		try{
			Bitmap resouseBitmap = BitmapFactory.decodeResource(mContext.getResources(),resId);
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
		Log.i(TAG, "onJoysticMotion action="+action );
		int joyKey = ButtonUtil.update(event);
		if(joyKey == X_RIGHT_KEYCODE||
				joyKey == X_LEFT_KEYCODE||
				joyKey == Y_DOWN_KEYCODE||
				joyKey == Y_UP_KEYCODE){
			Log.i(TAG, "onJoysticMotion left joystick"+action );
			joyKey = LEFT_JOYSTICK;
		}else if(joyKey == RX_RIGHT_KEYCODE||
				joyKey == RX_LEFT_KEYCODE||
				joyKey == RY_DOWN_KEYCODE||
				joyKey == RY_UP_KEYCODE){
			Log.i(TAG, "onJoysticMotion right joystick"+action );
			joyKey = RIGHT_JOYSTICK;
		}else{
			return true;
		}
		
		if(mKeyMapping.key  == joyKey)
			return true;
		
		if(joyKey == LEFT_JOYSTICK && 
				(containKeyButton(X_LEFT_KEYCODE)||
				containKeyButton(X_RIGHT_KEYCODE)||
				containKeyButton(Y_DOWN_KEYCODE)||
				containKeyButton(Y_UP_KEYCODE)||
				containKeyButton(LEFT_JOYSTICK))){
			mMapperButtonKeyMap.remove(mKeyMapping.key);
			mKeyMapping.key = 0;
		}else if(joyKey == RIGHT_JOYSTICK &&
				(containKeyButton(RX_LEFT_KEYCODE) ||
				containKeyButton(RX_RIGHT_KEYCODE) ||
				containKeyButton(RY_DOWN_KEYCODE) ||
				containKeyButton(RY_UP_KEYCODE)||
				containKeyButton(RIGHT_JOYSTICK))){
			mMapperButtonKeyMap.remove(mKeyMapping.key);
			mKeyMapping.key = 0;
		}else{
			if(mMapperButtonKeyMap.containsKey(mKeyMapping.key))
				mMapperButtonKeyMap.remove(mKeyMapping.key);
			mKeyMapping.key = joyKey;
			mMapperButtonKeyMap.put(mKeyMapping.key, this);
		}
		setImage();
		
		if(mKeyMapping.key == 0){
			showInfoByString(R.string.info_bar_joystick_code_beused_help);
		}
		
		return true;
		
	}

	@Override
	public boolean onJoysticKey(KeyEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		Log.i(TAG, "onJoysticKey action="+action+":"+event.getKeyCode());
		return false;
	}
	
	@Override
	public void showNewButtonNotice() {
		// TODO Auto-generated method stub
		mInformationBar.informationBarShow();
		mInformationBar.showInformations(R.string.info_bar_sensor_unassigned_help);
	}
}
