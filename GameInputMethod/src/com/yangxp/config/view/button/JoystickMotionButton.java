package com.yangxp.config.view.button;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.AbsoluteLayout;

import com.yangxp.config.bean.KeyMapping;
import com.yangxp.config.bean.Mappings;
import com.yangxp.config.view.exception.NewButtonException;
import com.yangxp.ginput.R;

public class JoystickMotionButton extends MapperButton{
	private static String TAG = "JoystickMotionButton";
	
    private final static int LEFT_JOYSTICK = 220;
    private final static int RIGHT_JOYSTICK = 221;
	
	private JoysticType mJoysticType = null;
	private float zoomX = 0f;
	private float zoomY = 0f;
	private int res = -1;
	private Bitmap resouseBitmap = null;
	private Bitmap viewBitmap = null;
	

	
	public JoystickMotionButton(Context context,Mappings keyMapping,HashMap<Integer,MapperViewInterface> mapperButtonKeyMap){
		super(context,keyMapping,mapperButtonKeyMap);
		if (keyMapping.key == LEFT_JOYSTICK)
			mJoysticType = JoysticType.left_joystic;
		else
			mJoysticType = JoysticType.right_joystic;
	}

	public JoystickMotionButton(Context context,Mappings keyMapping,HashMap<Integer,MapperViewInterface> mapperButtonKeyMap,JoysticType joysticType) throws NewButtonException{
		super(context,keyMapping,mapperButtonKeyMap);
		// TODO Auto-generated constructor stub
		mJoysticType = joysticType;
		if(mJoysticType == JoysticType.left_joystic)
			mKeyMapping.key = LEFT_JOYSTICK;
		else 
			mKeyMapping.key = RIGHT_JOYSTICK;

		
		Log.i(TAG, mKeyMapping.key+":mapperButtonKeyMap.containsKey(mKeyMapping.key)="+mapperButtonKeyMap.containsKey(mKeyMapping.key) );
		if(mapperButtonKeyMap.containsKey(mKeyMapping.key)){
			showInfoByString(R.string.info_bar_joystick_code_beused_help);
			throw new NewButtonException("button is on");
		}
		if(mKeyMapping.key == LEFT_JOYSTICK){
			if(mapperButtonKeyMap.containsKey(X_LEFT_KEYCODE)||
				mapperButtonKeyMap.containsKey(X_RIGHT_KEYCODE)||
				mapperButtonKeyMap.containsKey(Y_DOWN_KEYCODE)||
				mapperButtonKeyMap.containsKey(Y_UP_KEYCODE)){
				showInfoByString(R.string.info_bar_joystick_code_beused_help);
				throw new NewButtonException("button is on");
			}
		}
		if(mKeyMapping.key == RIGHT_JOYSTICK){
			if(mapperButtonKeyMap.containsKey(RX_LEFT_KEYCODE)||
				mapperButtonKeyMap.containsKey(RX_RIGHT_KEYCODE)||
				mapperButtonKeyMap.containsKey(RY_DOWN_KEYCODE)||
				mapperButtonKeyMap.containsKey(RY_UP_KEYCODE)){
				showInfoByString(R.string.info_bar_joystick_code_beused_help);
				throw new NewButtonException("button is on");
			}
		}
		mMapperButtonKeyMap.put(keyMapping.key, this);
		
		/*
		if(mapperButtonKeyMap.containsKey(mKeyMapping.key) && 
				(mapperButtonKeyMap.get(mKeyMapping.key).getKeyMapping().type==KeyMapping.KEY_MAP_TYPE_JOYSTICK||
				mapperButtonKeyMap.get(mKeyMapping.key).getKeyMapping().type==KeyMapping.KEY_MAP_TYPE_MOVE_AROUND))
			throw new NewButtonException("button is on");
		if(mKeyMapping.key == LEFT_JOYSTICK){
			removeKeyButton(X_LEFT_KEYCODE);
			removeKeyButton(X_RIGHT_KEYCODE);
			removeKeyButton(Y_DOWN_KEYCODE);
			removeKeyButton(Y_UP_KEYCODE);
			removeKeyButton(LEFT_JOYSTICK);
		}
		
		if(mKeyMapping.key == RIGHT_JOYSTICK){
			removeKeyButton(RX_LEFT_KEYCODE);
			removeKeyButton(RX_RIGHT_KEYCODE);
			removeKeyButton(RY_DOWN_KEYCODE);
			removeKeyButton(RY_UP_KEYCODE);
			removeKeyButton(RIGHT_JOYSTICK);
		}
		mMapperButtonKeyMap.put(keyMapping.key, this);
		*/
		
	}
	
	private void removeKeyButton(int keyCode){
		if(mMapperButtonKeyMap.containsKey(keyCode)){
			Log.e(TAG, "removeKeyButton:"+keyCode);
			mMapperButtonKeyMap.get(keyCode).getKeyMapping().key = 0;
			mMapperButtonKeyMap.remove(keyCode).setImage();
		}
	}
	@Override
	public void setImage() {
		// TODO Auto-generated method stub
		int tempRes = -1;
		if(mIsActive){
			if(mKeyMapping.key == LEFT_JOYSTICK){
				if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_JOYSTICK){
					tempRes = R.drawable.dragged_ls_100p_active;
                }else if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_MOVE_AROUND){
                	tempRes = R.drawable.dragged_ls_fps_swipe_100p_active;
				}
			}else{ 
				if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_JOYSTICK){
					tempRes = R.drawable.dragged_rs_100p_active;
                }else if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_MOVE_AROUND){
                	tempRes = R.drawable.dragged_rs_fps_swipe_100p_active;
				}
			}
		}else{
			if(mKeyMapping.key == LEFT_JOYSTICK){
				if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_JOYSTICK){
					tempRes = R.drawable.dragged_ls_100p_normal;
	            }else if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_MOVE_AROUND){
	            	tempRes = R.drawable.dragged_ls_fps_swipe_100p_normal;
				}
			}else{
				if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_JOYSTICK){
					tempRes = R.drawable.dragged_rs_100p_normal;
	            }else if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_MOVE_AROUND){
	            	tempRes = R.drawable.dragged_rs_fps_swipe_100p_normal;
				}
			}
		}
		if(tempRes!=res){
			res = tempRes;
			if(resouseBitmap != null){
				resouseBitmap.recycle();
			}
			
			resouseBitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
		}
		
		if(mKeyMapping.radius==0){
			//新建第一次进入界面
			mKeyMapping.radius = resouseBitmap.getWidth()/2;
			zoomX = zoomY = 1.0f;
		}else{
			if(zoomX == 0f){
				//历史记录第一次加入界面
				zoomX = zoomY= mKeyMapping.radius/(resouseBitmap.getWidth()/2.0f);
			}
		}
		
		AbsoluteLayout.LayoutParams lp = null;
		int centerIndexX = 0;
		int centerIndexY = 0;
		try{
			lp = (AbsoluteLayout.LayoutParams)getLayoutParams();
			if(lp!=null){
				centerIndexX = lp.x + mKeyMapping.radius;
				centerIndexY = lp.y + mKeyMapping.radius;
			}
		}catch(Exception e){
			
		}

		
		Matrix matrix = new Matrix(); 
		matrix.postScale(zoomX, zoomY);  
		//Log.e(TAG, "matrix matrix= " + matrix);
		Bitmap map = Bitmap.createBitmap(resouseBitmap, 0, 0, resouseBitmap.getWidth(), resouseBitmap.getHeight(), matrix, true);
		
		setImageBitmap(map);
//		if(viewBitmap!=null)
//			viewBitmap.recycle();
		viewBitmap = map;
		mKeyMapping.radius = map.getWidth()/2;

		try{
			if(lp!=null){
				lp.x = centerIndexX - mKeyMapping.radius;
				lp.y = centerIndexY - mKeyMapping.radius;
			}
		}catch(Exception e){
			
		}

	}
	
	public enum JoysticType{
		left_joystic,
		right_joystic;
	}

	
	@Override
	public boolean onJoysticMotion(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onJoysticKey(KeyEvent event) {
		// TODO Auto-generated method stub
		
		boolean result = false;
		switch(event.getKeyCode()) {
		case KeyEvent.KEYCODE_BUTTON_Y:		
			zoomX = zoomY = (mKeyMapping.radius+2)/(resouseBitmap.getWidth()/2.0f);;
			result = true;
			break;
		case KeyEvent.KEYCODE_BUTTON_A:
			zoomX = zoomY = (mKeyMapping.radius-2)/(resouseBitmap.getWidth()/2.0f);;
			result = true;
			break;
		default:
			break;
		}
		if(result)
			setImage();
		return result;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event){
		int action = event.getAction();
		Log.i(TAG, "dispatchTouchEvent and action = " + action);
		mViewTouchListener.onViewTouch(this);
		return super.dispatchTouchEvent(event);
	}
	
	/*
	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {	
//		        <item android:id="@id/menu_normal_stick" android:title="@string/normal_stick" />
//		        <item android:id="@id/menu_look_stick" android:title="@string/look_stick" />
				case R.id.menu_normal_stick:
					Log.i(TAG, "menu menu_normal_stick item click");
					mKeyMapping.type = KeyMapping.KEY_MAP_TYPE_JOYSTICK;
					break;
				case R.id.menu_look_stick:
					Log.i(TAG, "menu menu_look_stick item click");
					mKeyMapping.type = KeyMapping.KEY_MAP_TYPE_MOVE_AROUND;
					break;
				case R.id.menu_move:
					Log.i(TAG, "menu move item click");
					mViewTouchListener.onViewTouch(JoystickMotionButton.this);
					break;
				default :
					break;
				}
				setImage();
				return true;
			}
		};
		initPopuWindow(R.menu.stick_menu,listener);
		
		if(mPopupMenu!=null){
			if(mKeyMapping.type == KeyMapping.KEY_MAP_TYPE_JOYSTICK){
				((MenuItem)(mPopupMenu.getMenu().findItem(R.id.menu_normal_stick))).setChecked(true);
			}else{
				((MenuItem)(mPopupMenu.getMenu().findItem(R.id.menu_look_stick))).setChecked(true);
			}
		}
		return false;
	}
	*/

	@Override
	public void showNewButtonNotice() {
		// TODO Auto-generated method stub
		mInformationBar.informationBarShow();
		mInformationBar.showInformations(R.string.info_bar_stick_resize_help);
	}
}
