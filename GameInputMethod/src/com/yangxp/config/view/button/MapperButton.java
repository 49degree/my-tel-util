package com.yangxp.config.view.button;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.yangxp.config.bean.Mappings;
import com.yangxp.config.view.InformationBar;

public abstract class MapperButton extends ImageView implements MapperViewInterface,OnLongClickListener{
	private static String TAG = "MapperButton";
	//private AbsoluteLayout mButtonGroupView = null;
	protected ViewTouchListener mViewTouchListener;
	protected Mappings mKeyMapping;
	protected boolean mIsActive = false;
	protected Context mContext;
	protected HashMap<Integer,MapperViewInterface> mMapperButtonKeyMap;
	protected PopupMenu mPopupMenu;
	
	protected InformationBar mInformationBar;
	
	public MapperButton(Context context,Mappings keyMapping,HashMap<Integer,MapperViewInterface> mapperButtonKeyMap) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mKeyMapping = keyMapping;
		mMapperButtonKeyMap = mapperButtonKeyMap;
		if(mKeyMapping.key!=0){
			mMapperButtonKeyMap.put(mKeyMapping.key, this);
		} else {
			//新建按钮
			mInformationBar = new InformationBar(mContext);
		}
		setImage();
		setOnLongClickListener(this);
	}
	
	public Mappings getKeyMapping() {
		// TODO Auto-generated method stub
		return mKeyMapping;
	}
	
	public void setKeyMapping(Mappings keyMapping) {
		// TODO Auto-generated method stub
		mKeyMapping = keyMapping;
	}
	
	public void setPisition(float x,float y){
		mKeyMapping.x = (int)x;
		mKeyMapping.y = (int)y;
	}
	
	public void setViewTouchListener(ViewTouchListener viewTouchListener){
		mViewTouchListener = viewTouchListener;
	}
	
	public void setRemoveSelfListener(RemoveSelfListener removeSelfListener){
		
	}
	
	public void setIsActiveButton(boolean isActive){
		mIsActive = isActive;
		Log.i(TAG, "mIsActive+++++++++++++"+isActive);
		setImage();
		if(mIsActive){
			requestFocus();
		}
	}
	
	
	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event){
		if (0 == (event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK)) {
			return super.dispatchTouchEvent(event);
		}
		return onJoysticMotion(event);

	}
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		Log.e(TAG, "dispatchKeyEvent event= "+event.getKeyCode()+":"+event.getAction());
		if(event.getKeyCode()==4||event.getKeyCode()==203){
			return false;
		}
		
		return onJoysticKey(event);
	}
	
    

	
	
	protected boolean containKeyButton(int keyCode){
		Log.i(TAG,""+mMapperButtonKeyMap.get(keyCode));
		return mMapperButtonKeyMap.containsKey(keyCode);
	}
    
	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	protected void initPopuWindow(int menuViewID,PopupMenu.OnMenuItemClickListener menuItemClickListener){
		mPopupMenu = new PopupMenu(mContext, this);
		mPopupMenu.getMenuInflater().inflate(menuViewID, mPopupMenu.getMenu());
		mPopupMenu.setOnMenuItemClickListener(menuItemClickListener);
		mPopupMenu.show();
    }
	
	protected void showInfoByString(int rid){
		InformationBar informationBar = new InformationBar(mContext);
		informationBar.informationBarShow();
		informationBar.showInformations(rid);
	}

}
