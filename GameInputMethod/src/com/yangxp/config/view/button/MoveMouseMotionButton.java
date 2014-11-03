package com.yangxp.config.view.button;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.TextView;

import com.yangxp.config.bean.Mappings;
import com.yangxp.ginput.R;

public class MoveMouseMotionButton extends MapperButton{
	private static String TAG = "MoveMouseMotionButton";
	private AbsoluteLayout mButtonGroupView = null;
	TextView keyText = null;
	public MoveMouseMotionButton(Context context,Mappings keyMapping,HashMap<Integer,MapperViewInterface> mapperButtonKeyMap,AbsoluteLayout buttonGroupView){
		super(context,keyMapping,mapperButtonKeyMap);
		mButtonGroupView = buttonGroupView;
		setImage();
		Log.e(TAG, "mButtonGroupView==null?"+(mButtonGroupView==null) );
			
	// TODO Auto-generated constructor stub
	}
	
	public TextView getKeyText(){
		return keyText;
	}
	
	@Override
	public void setImage() {
		// TODO Auto-generated method stub
		if(mIsActive){
			setImageResource(R.drawable.dragged_pointer_100p_active) ;
		}else{
			setImageResource(R.drawable.dragged_pointer_100p_normal) ;
		}	
		
		
		
		String joyName = ButtonUtil.getKeyText(mKeyMapping.key);
		String keyName = ButtonUtil.getKeyText(mKeyMapping.keyClick);
		
		Log.i(TAG, mKeyMapping.key+":"+mKeyMapping.keyClick+":mKeyMapping.key="+joyName+":"+keyName);
		if(mButtonGroupView!=null){
			if(keyText!=null&&keyText.getParent()!=null)
				mButtonGroupView.removeView(keyText);
				
			keyText = new TextView(mContext);
			
			joyName = ButtonUtil.getKeyText(mKeyMapping.key);
			keyName = ButtonUtil.getKeyText(mKeyMapping.keyClick);
			Log.i(TAG, mKeyMapping.key+":"+mKeyMapping.keyClick+":mKeyMapping.key="+joyName+":"+keyName );
			keyText.setText(joyName+":"+keyName);
			keyText.setTextColor(Color.WHITE);
			keyText.setTextSize(20);
			if(mIsActive){
				keyText.setBackgroundColor(Color.BLUE);
			}else{
				keyText.setBackgroundColor(0);
			}
			keyText.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mViewTouchListener.onViewTouch(MoveMouseMotionButton.this);
				}
				
			});
			try{
				Bitmap resouseBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dragged_pointer_100p_active);
				AbsoluteLayout.LayoutParams aL = new AbsoluteLayout.LayoutParams(
						AbsoluteLayout.LayoutParams.WRAP_CONTENT,
						AbsoluteLayout.LayoutParams.WRAP_CONTENT,
						mKeyMapping.x+40, mKeyMapping.y+40);
				mButtonGroupView.addView(keyText,aL);
				resouseBitmap.recycle();
			}catch(Exception e){
				e.printStackTrace();
			}

		}
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
		
		if(mKeyMapping.key  == joyKey){
			mInformationBar.showInformations(R.string.info_bar_cursor_click_unassigned_help);		    
			return true;
	    }
		Log.i(TAG, "containKeyButton(X_LEFT_KEYCODE)||"+
				"containKeyButton(X_RIGHT_KEYCODE)||"+
				"containKeyButton(Y_DOWN_KEYCODE)||"+
				"containKeyButton(Y_UP_KEYCODE)||"+
				"containKeyButton(LEFT_JOYSTICK):"+(containKeyButton(X_LEFT_KEYCODE)||
						containKeyButton(X_RIGHT_KEYCODE)||
						containKeyButton(Y_DOWN_KEYCODE)||
						containKeyButton(Y_UP_KEYCODE)||
						containKeyButton(LEFT_JOYSTICK)));
						
		
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
		}else{
			if(mKeyMapping.keyClick == 0){
				showInfoByString(R.string.info_bar_cursor_click_unassigned_help);
			}
		}
		return true;
		
	}
	


	@Override
	public boolean onJoysticKey(KeyEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		
		
		mKeyMapping.keyClick = event.getKeyCode();
		
		Log.i(TAG, "onJoysticKey action="+action+":"+event.getKeyCode()+":"+mKeyMapping.keyClick+":"+mMapperButtonKeyMap.get(event.getKeyCode()));
		
		if(mMapperButtonKeyMap.containsKey(mKeyMapping.keyClick)){
			mKeyMapping.keyClick = 0;
			
		}
		setImage();
		if(mKeyMapping.keyClick == 0){
			showInfoByString(R.string.info_bar_key_code_beused_help);
		}
		return true;
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
				case R.id.menu_absolute_cursor:
					Log.i(TAG, "menu menu_absolute_cursor item click");
					mKeyMapping.radius=0;
					break;
				case R.id.menu_accelerated_cursor:
					Log.i(TAG, "menu menu_accelerated_cursor item click");
					mKeyMapping.radius=1;
					break;
				case R.id.menu_move:
					Log.i(TAG, "menu move item click");
					mViewTouchListener.onViewTouch(MoveMouseMotionButton.this);
					break;					
				default :
					break;
				}
				setImage();
				return true;
			}
		};
		initPopuWindow(R.menu.cursor_menu,listener);
		
		if(mPopupMenu!=null){
			if(mKeyMapping.radius==0){
				((MenuItem)(mPopupMenu.getMenu().findItem(R.id.menu_absolute_cursor))).setChecked(true);
			}else{
				((MenuItem)(mPopupMenu.getMenu().findItem(R.id.menu_accelerated_cursor))).setChecked(true);
			}
		}
		
		return true;
	}
	*/
	@Override
	public void showNewButtonNotice() {
		// TODO Auto-generated method stub
		mInformationBar.informationBarShow();
		mInformationBar.showInformations(R.string.info_bar_cursor_move_unassigned_help);
	}
}
