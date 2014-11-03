package com.yangxp.config.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yangxp.config.MapperService;
import com.yangxp.config.bean.KeyMapping;
import com.yangxp.config.bean.Mappings;
import com.yangxp.config.business.SettingController;
import com.yangxp.config.util.NetWorkUtil;
import com.yangxp.config.view.ConfigFileDownloadTask.ConfigFileDownloadListener;
import com.yangxp.config.view.button.ClickMotionButton;
import com.yangxp.config.view.button.JoystickMotionButton;
import com.yangxp.config.view.button.MapperButton;
import com.yangxp.config.view.button.MapperViewInterface;
import com.yangxp.config.view.button.MapperViewInterface.RemoveSelfListener;
import com.yangxp.config.view.button.MapperViewInterface.ViewTouchListener;
import com.yangxp.config.view.button.MoveMouseMotionButton;
import com.yangxp.config.view.button.MoveTraceMotionButton;
import com.yangxp.config.view.button.SensorMotionButton;
import com.yangxp.config.view.exception.NewButtonException;
import com.yangxp.ginput.R;
//import android.view.SurfaceControl; 

public class ViewMappings extends LinearLayout implements OnTouchListener,OnDragListener,ViewTouchListener,RemoveSelfListener{
	private final static String TAG = "ViewMappings";
	@SuppressWarnings("deprecation")
	private AbsoluteLayout mButtonGroupView = null;
	private Context mContext;
	private Resources mResources;
	
	private FrameLayout rootView = null; 
	private MapperViewInterface activeMapperButton;
	
	private ImageView mBtSensor = null;  //ivSensor
	private ImageView mBtLs;      // ivLeftStick
	private ImageView mBtRs;      // ivRightStick
	private ImageView mBt;        // ivButton
	private ImageView mBtCursor;  //ivCursor
	private ImageView mBtRecord;  // ivRecord
	private ImageView mSave;
	private ImageView mBtRemove;  //ivremove
	private ImageView mBtSearch; //ivsearch
	private ImageView mTitleMenu;
	private ImageView mDel;
	
	private String mCurrentAppPkgName = "";
	private String mCurrentAppName = "";
	private HashMap<Integer,MapperViewInterface> mMapperButtonKeyMap = new HashMap<Integer,MapperViewInterface>();
	
	//private HashMap<Integer,MoveTraceMotionButton> mMoveTraceMotionButtonKeyMap = new HashMap<Integer,MoveTraceMotionButton>();
	
	
	private SettingController mSettingController;
	
	private NetWorkUtil mNetWorkUtil = null;
	
	
	public ViewMappings(Context context){
		this(context,null);
	}

	public ViewMappings(Context context, AttributeSet paramAttributeSet){
		super(context, paramAttributeSet);
		mContext = context;
		mNetWorkUtil = new NetWorkUtil(mContext,null);
		mResources = mContext.getResources();
		rootView = (FrameLayout)LayoutInflater.from(context).inflate(R.layout.mapper_view, this, false);
		
		rootView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				Log.e(TAG, " onKey(View arg0, int arg1, KeyEvent arg2)");
				return false;
			}
		});
		
		takeScreen();
		
		mButtonGroupView = (AbsoluteLayout)rootView.findViewById(R.id.button_group);
		getTopApp();
		addView(rootView);
		btInit();
		initConfigData();

	}


		
	/**
	 * button
	 * */
	public void btInit() {
		
		mBtSensor = (ImageView)findViewById(R.id.ivSensor);
		mBtLs = (ImageView)findViewById(R.id.ivLeftStick);
		mBtRs =(ImageView)findViewById(R.id.ivRightStick);
		mBt = (ImageView)findViewById(R.id.ivButton);
		mBtCursor = (ImageView)findViewById(R.id.ivCursor);
		mBtRecord = (ImageView)findViewById(R.id.ivRecord);
		mDel = (ImageView) findViewById(R.id.ivDelete);
		mBtRemove = (ImageView)findViewById(R.id.ivRemove);
		mSave = (ImageView)findViewById(R.id.ivSave);
		mBtSearch = (ImageView)findViewById(R.id.ivSearch);
		
		mBtSensor.setOnTouchListener(this);
		mBtLs.setOnTouchListener(this);
		mBtRs.setOnTouchListener(this);
		mBt.setOnTouchListener(this);
		mBtCursor.setOnTouchListener(this); 
		
		mBtRecord.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Mappings keyMapping = new Mappings();
				keyMapping.type = KeyMapping.KEY_MAP_TYPE_MOVE_TRACE;
				MoveTraceMotionButton moveTraceMotionButton = new MoveTraceMotionButton(mContext,keyMapping,mMapperButtonKeyMap,rootView,mButtonGroupView);
				setActiveMapperButton(moveTraceMotionButton);
				FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
				moveTraceMotionButton.setRemoveSelfListener(ViewMappings.this);
				rootView.addView(moveTraceMotionButton, flp);
				mSettingController.addMapperBean(moveTraceMotionButton.getKeyMapping());
				moveTraceMotionButton.setViewTouchListener(ViewMappings.this);
			}
			
		});

		mBtRemove.setOnClickListener(new OnClickListener(){
			//\u5220\u9664\u63a7\u4ef6
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(activeMapperButton != null){
					deleteButton(activeMapperButton);
					mMapperButtonKeyMap.remove(activeMapperButton.getKeyMapping().key);
					activeMapperButton = null;
				}
			}
			
		});
		
		
		mDel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Iterator<Map.Entry<Integer,MapperViewInterface>> it = mMapperButtonKeyMap.entrySet().iterator();
				while(it.hasNext()){
	                deleteButton(it.next().getValue());
	                it.remove();//OK   
				}
				if(activeMapperButton!=null){
					 deleteButton(activeMapperButton);
					 if(mMapperButtonKeyMap.containsKey(activeMapperButton.getKeyMapping().key))
						 mMapperButtonKeyMap.remove(activeMapperButton.getKeyMapping().key);
				}
				activeMapperButton = null;
			}		
		});
		
		
		mSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InformationBar mInformationBar = new InformationBar(mContext);
				mInformationBar.informationBarShow();
				if(mMapperButtonKeyMap.size()==0){
					mInformationBar.showInformations(R.string.info_bar_have_no_config);
				}else{
					mSettingController.saveOrUpdate();
					mSettingController.backupConfigFile(mCurrentAppName);
					mInformationBar.showInformations(R.string.info_bar_save_mappings);
				}
			}		
		});
		
		mBtSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mNetWorkUtil.getNetWorkInfo()==NetWorkUtil.NET_TYPE_NONE){
					showInfoByString(R.string.info_down_no_connective);
					return ;
				}
				ConfigFileDownloadTask ConfigFileDownloadTask = new ConfigFileDownloadTask(mContext,mCurrentAppPkgName);
				ConfigFileDownloadTask.setConfigFileDownloadListener(new MyConfigFileDownloadListener());
				ConfigFileDownloadTask.execute(new String[]{});
			}		
		});
	}
	
	
	private void initConfigData(){
		mSettingController = new SettingController(mCurrentAppPkgName);
		try {
			List<Mappings> keyMappings = mSettingController.getMappings();
			Log.i(TAG, "keyMappings:" +keyMappings.size());
			if(keyMappings!=null){
				for(Mappings keyMapping:keyMappings){
					showHistoryMapperButton(keyMapping);  
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void removeButtonView(MapperViewInterface viewButton){
		// TODO Auto-generated method stub
		if(viewButton != null){
			try{
				if(viewButton instanceof MapperButton)
					mButtonGroupView.removeView((MapperButton)viewButton);
				else if(viewButton instanceof MoveTraceMotionButton){
					//\u5220\u9664\u8f68\u8ff9
					rootView.removeView((MoveTraceMotionButton)viewButton);
					mButtonGroupView.removeView(((MoveTraceMotionButton)viewButton).getKeyText());
				}
				
				if(viewButton instanceof MoveMouseMotionButton
						&&((MoveMouseMotionButton)viewButton).getKeyText()!=null
						&&((MoveMouseMotionButton)viewButton).getKeyText().getParent()!=null){
					mButtonGroupView.removeView(((MoveMouseMotionButton)viewButton).getKeyText());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void deleteButton(MapperViewInterface viewButton){

		// TODO Auto-generated method stub
		removeButtonView(viewButton);
		if(viewButton != null){
			try{
				mSettingController.deleteMappings(viewButton.getKeyMapping());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
	}
	
	private void showHistoryMapperButton(final Mappings keyMapping){
		MapperButton mapperButton = null;
		switch (keyMapping.type) {
		case KeyMapping.KEY_MAP_TYPE_SENSOR:
			keyMapping.type = KeyMapping.KEY_MAP_TYPE_SENSOR;
			try {
				mapperButton = new SensorMotionButton(mContext,keyMapping,mMapperButtonKeyMap);
			} catch (NewButtonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case KeyMapping.KEY_MAP_TYPE_JOYSTICK:
		case KeyMapping.KEY_MAP_TYPE_MOVE_AROUND:
			mapperButton = new JoystickMotionButton(mContext,keyMapping,mMapperButtonKeyMap);
			break;
		case  KeyMapping.KEY_MAP_TYPE_CLICK:
			mapperButton = new ClickMotionButton(mContext,keyMapping,mMapperButtonKeyMap);
			break;
		case KeyMapping.KEY_MAP_TYPE_MOVE_MOUSE:
			
			mapperButton = new MoveMouseMotionButton(mContext,keyMapping,mMapperButtonKeyMap,mButtonGroupView);
			break;
		case KeyMapping.KEY_MAP_TYPE_MOVE_TRACE:
			MoveTraceMotionButton moveTraceMotionButton = new MoveTraceMotionButton(mContext,keyMapping,mMapperButtonKeyMap,rootView,mButtonGroupView);
			FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
			rootView.addView(moveTraceMotionButton, flp);
			moveTraceMotionButton.setRemoveSelfListener(this);
			moveTraceMotionButton.setViewTouchListener(this);
			break;
		}
		
		if(mapperButton != null){
			mapperButton.setVisibility(View.VISIBLE);
			mapperButton.setViewTouchListener(ViewMappings.this);
			
			AbsoluteLayout.LayoutParams aL = new AbsoluteLayout.LayoutParams(
					AbsoluteLayout.LayoutParams.WRAP_CONTENT,
					AbsoluteLayout.LayoutParams.WRAP_CONTENT,
					keyMapping.x - keyMapping.radius, keyMapping.y- keyMapping.radius);
			Log.e(TAG, "mapperButton12:" +keyMapping.x +":"+  keyMapping.radius+":"+keyMapping.y  +":"+  keyMapping.radius);
			mButtonGroupView.addView(mapperButton, aL);
		}
	}
	

	public boolean onTouch(View view, MotionEvent motionEvent) {

		
		Log.e(TAG, "onTouch:" + motionEvent.getAction());
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			ClipData data = ClipData.newPlainText("111", "222");
			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
			view.startDrag(data, shadowBuilder, view, 0);
			mButtonGroupView.setOnDragListener(this);
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean onDrag(View v, DragEvent event) {
		int action = event.getAction();
		ImageView view = (ImageView) event.getLocalState();
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:
			Log.e(TAG,"DragEvent.ACTION_DRAG_STARTED:" + (int) event.getX()+ ":" + (int) event.getY());
			if(view.getParent() != mButtonGroupView){
				view.setVisibility(View.VISIBLE);
			}else{
				view.setVisibility(View.INVISIBLE);
			}
			break;
		case DragEvent.ACTION_DRAG_ENTERED:
			Log.e(TAG,
					"DragEvent.ACTION_DRAG_ENTERED:" + (int) event.getX()+ ":" + (int) event.getY());
			break;
		case DragEvent.ACTION_DRAG_EXITED:
			Log.e(TAG, "DragEvent.ACTION_DRAG_EXITED:" + (int) event.getX()+ ":" + (int) event.getY());
			if(view.getParent() == mButtonGroupView){
				view.setVisibility(View.VISIBLE);
			}
			break;
		case DragEvent.ACTION_DROP:
			Log.e(TAG, "DragEvent.ACTION_DROP:" + (int) event.getX() + ":"+ (int) event.getY());
			
			int xIndex =  (int) event.getX();
			int yIndex = (int) event.getY();
			
			@SuppressWarnings("deprecation")
			MapperButton mapperButton = null;
			if(view.getParent() != mButtonGroupView){
	        	//\u65b0\u521b\u5efa\u6309\u94ae
	        	mapperButton = newMapperButton(view.getId(),event);
	        	//yIndex += toolBarHeight;
	        	if(mapperButton!=null){
		            mapperButton.setViewTouchListener(this);
		            mSettingController.addMapperBean(mapperButton.getKeyMapping());
		            mapperButton.showNewButtonNotice();
	        	}
	        }else{
	        	mapperButton = (MapperButton)view;
	        	mButtonGroupView.removeView(view);
	        }
			
	        if(mapperButton != null ){
	        	mapperButton.setPisition(xIndex, yIndex);
	        	mapperButton.setVisibility(View.VISIBLE);
	        	
	        	setActiveMapperButton(mapperButton);
	        	Log.e(TAG, "setActiveMapperButton:" + (int)xIndex+":"+mapperButton.getKeyMapping().radius+ ":" + (int)yIndex);
		        AbsoluteLayout.LayoutParams aL = new AbsoluteLayout.LayoutParams(
		        		AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT, 
		        		(int)xIndex-mapperButton.getKeyMapping().radius, yIndex-mapperButton.getKeyMapping().radius);
		        if(mapperButton instanceof MoveMouseMotionButton){
		        	aL = new AbsoluteLayout.LayoutParams(
			        		AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT, 
			        		mapperButton.getKeyMapping().x, mapperButton.getKeyMapping().y);
		        }
		        mButtonGroupView.addView(mapperButton,aL);
		        
				saveConfigData();
	        }
			break;
		case DragEvent.ACTION_DRAG_ENDED:{
			Log.e(TAG, "DragEvent.ACTION_DRAG_ENDED:" + (int) event.getX()+ ":" + (int) event.getY());
			mButtonGroupView.setOnDragListener(null);
		}
		default:
			break;
		}
		return true;
	}

	public MapperButton newMapperButton(int buttonId,DragEvent event) {
		MapperButton mapperButton = null;
		Mappings keyMapping = new Mappings();
		switch (buttonId) {
		case R.id.ivSensor:
			keyMapping.type = KeyMapping.KEY_MAP_TYPE_SENSOR;
			try {
				mapperButton = new SensorMotionButton(mContext,keyMapping,mMapperButtonKeyMap);
			} catch (NewButtonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.ivLeftStick:
			keyMapping.type = KeyMapping.KEY_MAP_TYPE_JOYSTICK;
			try {
				mapperButton = new JoystickMotionButton(mContext,keyMapping,mMapperButtonKeyMap,JoystickMotionButton.JoysticType.left_joystic);
			} catch (NewButtonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.ivRightStick:
			keyMapping.type = KeyMapping.KEY_MAP_TYPE_JOYSTICK;
			try {
				mapperButton = new JoystickMotionButton(mContext,keyMapping,mMapperButtonKeyMap,JoystickMotionButton.JoysticType.right_joystic);
			} catch (NewButtonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.ivButton:
			keyMapping.type = KeyMapping.KEY_MAP_TYPE_CLICK;
			mapperButton = new ClickMotionButton(mContext,keyMapping,mMapperButtonKeyMap);
			break;
		case R.id.ivCursor:
			keyMapping.type = KeyMapping.KEY_MAP_TYPE_MOVE_MOUSE;
			Log.e(TAG, "KEY_MAP_TYPE_MOVE_MOUSE==null?"+(mButtonGroupView==null) );
			mapperButton = new MoveMouseMotionButton(mContext,keyMapping,mMapperButtonKeyMap,mButtonGroupView);
			break;
		}
		
		keyMapping.x = (int)event.getX();
		keyMapping.y = (int)event.getY();

		return mapperButton;
	}
	
	/**
	 * 获取当前运行应用信息
	 */
	private void getTopApp() {
		ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);

		RunningTaskInfo runningTaskInfo = runningTasks.get(0);
		

		ComponentName topActivity = runningTaskInfo.baseActivity;// runningTaskInfo.topActivity;
		

		mCurrentAppPkgName = topActivity.getPackageName();
		
		mCurrentAppName = getProgramNameByPackageName(mCurrentAppPkgName);
	}
	
	/**
	 * 通过包名获取应用程序的名称。
	 * @param packageName
	 *            包名。
	 * @return 返回包名所对应的应用程序的名称。
	 */
	private String getProgramNameByPackageName(String packageName) {
		PackageManager pm = mContext.getPackageManager();
		String name = "";
		try {
			name = pm.getApplicationLabel(pm.getApplicationInfo(packageName,PackageManager.GET_META_DATA)).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}

	public void setActiveMapperButton(MapperViewInterface mapperButton){
		if(activeMapperButton != null){
			
			if(activeMapperButton!=mapperButton && 
					activeMapperButton.getKeyMapping().key==0){
				deleteButton(activeMapperButton);
			}else{
				activeMapperButton.setIsActiveButton(false);
				if(activeMapperButton instanceof MapperButton)
					((MapperButton)activeMapperButton).setOnTouchListener(null);
			}
		}
		activeMapperButton = mapperButton;
		activeMapperButton.setIsActiveButton(true);

	}
	
	@Override
	public void onRemoveSelf(MapperViewInterface mapperView) {
		// TODO Auto-generated method stub
		if(mapperView == activeMapperButton){
			activeMapperButton = null;
		}
	}
	
	@Override
	public void onViewTouch(MapperViewInterface button){
		// TODO Auto-generated method stub
		setActiveMapperButton(button);
		
		if(button instanceof MapperButton)
			((MapperButton)button).setOnTouchListener(this);
	} 
	


	public boolean dispatchGenericMotionEvent(MotionEvent event){
		int action = event.getAction();
		//Log.i(TAG, "dispatchGenericMotionEvent action="+action );
		
		if(activeMapperButton!=null)
			return activeMapperButton.dispatchGenericMotionEvent(event);
		if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK)) {
			return true;
		}else
			return super.dispatchGenericMotionEvent(event);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		int action = event.getAction();
	    //Log.e(TAG, "dispatchKeyEvent event= "+event.getKeyCode()+":"+action);
		if(event.getKeyCode()==4){
			if(activeMapperButton!=null && activeMapperButton.dispatchKeyEvent(event))
				return true;
			if(event.getAction() == KeyEvent.ACTION_UP){
				saveConfigData();
				Intent ins = new Intent();
				ins.setClass(mContext, MapperService.class);
				ins.putExtra("action", MapperService.MAPPER_BROADCASTRECEIVER_HIDE_ACTION);
				mContext.startService(ins);
			}
			return true;
		}
		if(activeMapperButton!=null)
			return activeMapperButton.dispatchKeyEvent(event);
		
		
		return super.dispatchKeyEvent(event);	
	}

	/**
	 * 保存配置数据
	 */
	
	
	private void saveConfigData(){
		mSettingController.saveOrUpdate();
	}
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {  
		super.onLayout(changed, l, t, r, b);
		//Log.e(TAG,"onLayout===============");
		setBackground();
	}
	
	public void setBackground(){
		if(mScreenBitmap==null)
			return ;
        Rect frame = new Rect();
		rootView.getWindowVisibleDisplayFrame(frame);
		//Log.e(TAG,"frame.top="+frame.top );
		if(frame.top<0||frame.top>mDisplayMetrics.heightPixels)
			frame.top = 0; 
		/*
		try{
			if(mBackgroundBitmap!=null){
				mBackgroundBitmap.recycle();
			}
		}catch(Exception e){
		}
		*/
		mBackgroundBitmap = Bitmap.createBitmap(mScreenBitmap, 0, frame.top, mScreenBitmap.getWidth(), mScreenBitmap.getHeight()-frame.top, null, false);

        rootView.setBackground(new BitmapDrawable(mBackgroundBitmap));
	}

	private class MyConfigFileDownloadListener implements ConfigFileDownloadListener{

		@Override
		public void onSucess() {
			// TODO Auto-generated method stub
			//清理界面
			Iterator<Map.Entry<Integer,MapperViewInterface>> it = mMapperButtonKeyMap.entrySet().iterator();
			while(it.hasNext()){
                removeButtonView(it.next().getValue());
                it.remove();//OK   
			}
			if(activeMapperButton!=null){
				 deleteButton(activeMapperButton);
				 if(mMapperButtonKeyMap.containsKey(activeMapperButton.getKeyMapping().key))
					 mMapperButtonKeyMap.remove(activeMapperButton.getKeyMapping().key);
			}
			activeMapperButton = null;
			//重新加载数据
			initConfigData();
		}
		
	}
	
	protected void showInfoByString(int rid){
		InformationBar informationBar = new InformationBar(mContext);
		informationBar.informationBarShow();
		informationBar.showInformations(rid);
	}
	
    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;
    private Matrix mDisplayMatrix;
    private Bitmap mScreenBitmap;
    private Bitmap mBackgroundBitmap;
	private WindowManager mWindowManager;
	private void takeScreen(){
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mDisplayMetrics = new DisplayMetrics();
        mDisplayMatrix = new Matrix(); 
        
        mDisplay.getRealMetrics(mDisplayMetrics);
        
        float[] dims = {mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
        
        /*
        float degrees = getDegreesForRotation(mDisplay.getRotation());
        boolean requiresRotation = (degrees > 0);
        if (requiresRotation) {
            // Get the dimensions of the device in its native orientation
            mDisplayMatrix.reset();
            mDisplayMatrix.preRotate(-degrees);
            mDisplayMatrix.mapPoints(dims);
            dims[0] = Math.abs(dims[0]);
            dims[1] = Math.abs(dims[1]);
        }
        */
        // Take the screenshot
       //mScreenBitmap = SurfaceControl.screenshot((int) dims[0], (int) dims[1]);
        // If we couldn't take the screenshot, notify the user
        if (mScreenBitmap == null) {
        	Log.e(TAG, "mScreenBitmap == null");
            return;
        }
        
		float degrees = getDegreesForRotation(mDisplay.getRotation());
		//Log.e(TAG,"degrees="+degrees );
        if(degrees == 180){
            // 定义矩阵对象  
        	Matrix matrix = new Matrix(); 
            // 缩放原图  
            matrix.postScale(1f, 1f); 
            // 向左旋转45度，参数为正则向右旋转  
            matrix.postRotate(180); 
            Bitmap temp = Bitmap.createBitmap(mScreenBitmap, 0, 0, mScreenBitmap.getWidth(), mScreenBitmap.getHeight(), matrix, false);
    		/*
            try{
    			mScreenBitmap.recycle();
    		}catch(Exception e){
    		}
    		*/
            mScreenBitmap = temp;
        }
        // Optimizations
        mScreenBitmap.setHasAlpha(false);
        mScreenBitmap.prepareToDraw();
	}

    /**
     * @return the current display rotation in degrees
     */
    private float getDegreesForRotation(int value) {
        switch (value) {
        case Surface.ROTATION_90:
            return 360f - 90f;
        case Surface.ROTATION_180:
            return 360f - 180f;
        case Surface.ROTATION_270:
            return 360f - 270f;
        }
        return 0f;
    }
    
    public int getScreenRotation(){
    	int value = 0;
    	//Log.e(TAG,(mDisplay!=null)+ ":getScreenRotation"+value);
    	if(mDisplay!=null)
    		value  = mDisplay.getRotation();
    	//Log.e(TAG, "getScreenRotation22"+value);
        switch (value) {
        case Surface.ROTATION_90:
        	return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        case Surface.ROTATION_180:
            return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        case Surface.ROTATION_270:
        	 return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }
}
