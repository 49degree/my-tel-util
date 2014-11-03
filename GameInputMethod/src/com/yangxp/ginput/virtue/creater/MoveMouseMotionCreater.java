package com.yangxp.ginput.virtue.creater;

import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yangxp.ginput.R;
import com.yangxp.ginput.virtue.MotionCreaterFactory;
import com.yangxp.ginput.virtue.bean.KeyMapping;
import com.yangxp.ginput.virtue.instrument.IMotionInstrument;

public class MoveMouseMotionCreater implements IMotionCreater{
	static final String TAG = "MoveMouseMotionCreater";
	static HashMap<Integer,MotionEvent> keyStatuMap = new HashMap<Integer,MotionEvent>();
	
	static final int HIDE_MOUSE_FLAG = 1;
	static final int MOVE_MOUSE_FLAG = 2;
	static final int CHECK_TOP_APP_FLAG = 3;
	
	//\u70b9\u51fb\u4f4d\u7f6e\u4e0e\u624b\u578b\u56fe\u6807\u5de6\u4e0a\u89d2\u7684\u504f\u79fb\u50cf\u7d20
	static final int mouseMoveX = 7;
	static final int mouseMoveY = 38;
	Context mContext = null;
	String myAppPkgName = null;
	IMotionInstrument mMotionInstrument;
	DisplayMetrics mDisplayMetrics = null; 
	final int toolBarHeight = 0;
	
	TN tn = null;
	private Thread service = null;
	private Looper mLooper = null;
	private Handler mHandler = null;  
	MotionEvent clickDown = null;
	MovePoint movePoint = new MovePoint();
	int repeatTime = 50;
	
	
	
	public MoveMouseMotionCreater(Context context ,IMotionInstrument motionInstrument){
		mMotionInstrument = motionInstrument;
		mContext = context;
		mDisplayMetrics = mContext.getResources().getDisplayMetrics();
		service = new Thread(){
			public void run(){
				Looper.prepare();
				mLooper = Looper.myLooper();
				
				mHandler = new Handler(mLooper){
			        @Override
					public void handleMessage(Message msg){
						switch(msg.what){
						case HIDE_MOUSE_FLAG:
							Log.e(this.getClass().getSimpleName(), "HIDE_MOUSE_FLAG++");
							if(tn!=null)
								tn.hide();
							keyStatuMap.remove(msg.arg1);
							break;
						case MOVE_MOUSE_FLAG:
							if(tn!=null&&tn.showing){
								tn.move(movePoint.x,movePoint.y);
								createClick(MotionEvent.ACTION_MOVE);
							}
							sendMessageDelayed(obtainMessage(MOVE_MOUSE_FLAG), repeatTime);
							break;
						case CHECK_TOP_APP_FLAG:
							sendEmptyMessageDelayed(CHECK_TOP_APP_FLAG,500);
							try{
								checkTopApp();
							}catch(Exception e){
								
							}
							break;					
						}
					}
				};
				
				Looper.loop();
			} 
		};
		service.start();
	}
	
	
	

	@Override
	public boolean create(InputEvent event,KeyMapping keyMapping){
		//Log.e(this.getClass().getSimpleName(), "create+++++++++++++++++++++++++");
		
		if(event instanceof MotionEvent){
			MotionEvent motionEvent = (MotionEvent)event;
			float x = 0;
			float y = 0;
			if(keyMapping.key==220){//\u5de6\u6447\u6746
				x = motionEvent.getX();
				y = motionEvent.getY();
			}else{
				x = motionEvent.getAxisValue(MotionEvent.AXIS_Z);
				y = motionEvent.getAxisValue(MotionEvent.AXIS_RZ);
			}
			
			Log.e(this.getClass().getSimpleName(), "MotionCreaterFactory.checkMotionEvent(x, y):"+MotionCreaterFactory.checkMotionEvent(x, y));
			
			if(keyMapping.radius==1){//\u76f8\u5bf9\u79fb\u4f4d
				if(tn!=null)
					tn.moveSelf = true;
				movePoint.x = x;
				movePoint.y = y;
				resetMoveSelfParams(x,y);
		    	if(keyStatuMap.containsKey(keyMapping.key)){
		    		mHandler.removeMessages(MOVE_MOUSE_FLAG);
		    		if(!MotionCreaterFactory.checkMotionEvent(x, y)){
		    			if(clickDown!=null)
		    				createClick(MotionEvent.ACTION_UP);
						Message msg = mHandler.obtainMessage(HIDE_MOUSE_FLAG);
						msg.arg1 = keyMapping.key;
		    			mHandler.sendMessageDelayed(msg,2000);
		    			Log.e(this.getClass().getSimpleName(), "keyMapping.radius==1 & MotionCreaterFactory.checkMotionEvent(x, y):"+MotionCreaterFactory.checkMotionEvent(x, y));
					}else{
						Message msg = mHandler.obtainMessage(MOVE_MOUSE_FLAG);
						mHandler.sendMessage(msg);
			    		keyStatuMap.put(keyMapping.key, motionEvent);
					}
		    	}else{
					if(!MotionCreaterFactory.checkMotionEvent(x, y)){
						return false;
					}
					if(tn==null){
						tn = new TN();
						tn.moveSelf = true;
					}
					if(!tn.showing){
						tn.show();
					}
					Message msg = mHandler.obtainMessage(MOVE_MOUSE_FLAG);
					msg.obj = movePoint;
					mHandler.sendMessage(msg);
					keyStatuMap.put(keyMapping.key, motionEvent);
		    	}
			}else{//\u7edd\u5bf9\u79fb\u4f4d
				if(tn!=null)
					tn.moveSelf = false;
		    	if(keyStatuMap.containsKey(keyMapping.key)){
		    		if(!MotionCreaterFactory.checkMotionEvent(x, y)){
		    			Log.e(this.getClass().getSimpleName(), "11keyMapping.radius==0 & MotionCreaterFactory.checkMotionEvent(x, y):"+MotionCreaterFactory.checkMotionEvent(x, y));
		    			if(clickDown!=null)
		    				createClick(MotionEvent.ACTION_UP);
		    			x=0;
			    		y=0;
						Message msg = mHandler.obtainMessage(HIDE_MOUSE_FLAG);
						msg.arg1 = keyMapping.key;
		    			mHandler.sendMessageDelayed(msg,2000);
					}else{
			    		keyStatuMap.put(keyMapping.key, motionEvent);
			    		if(tn!=null){
			    			mHandler.removeMessages(HIDE_MOUSE_FLAG);
			    		}
					}
		    		if(tn!=null){
		    			tn.move(x,y);
		    		}
		    	}else{
					if(!MotionCreaterFactory.checkMotionEvent(x, y)){
						return false;
					}
					if(tn==null){
						tn = new TN();
						tn.moveSelf = false;
					}
					if(!tn.showing){
						tn.show();
					}
					keyStatuMap.put(keyMapping.key, motionEvent);
					
		    		if(tn!=null){
		    			mHandler.removeMessages(HIDE_MOUSE_FLAG);
		    			tn.move(x,y);
		    		}
		    	}
			}
			return true;
		}else if(event instanceof KeyEvent){
			if(tn==null||!tn.showing)
				return false;
			
			int action = ((KeyEvent)event).getAction();
			return createClick(action);
		}
		return false;
	}
	
	void resetMoveSelfParams(float x,float y){
		float radus = (float) Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
		repeatTime = (int)(10/radus);
	}
	
	private boolean createClick(int action){
		try{
			if(action == MotionEvent.ACTION_DOWN){
				int x = tn.mParams.x+mouseMoveX;
				int y = tn.mParams.y+toolBarHeight+mouseMoveY;
				if(y>tn.endPoint.y)
					y  = tn.endPoint.y;
		    	if(clickDown!=null){
		    		clickDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_MOVE, 
							x,  y, 0);
		    	}else{
		    		clickDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_DOWN, x, y, 0);
		    	}
			}else if(action == MotionEvent.ACTION_UP){
				if(clickDown!=null)
					clickDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_UP, clickDown.getX(),  clickDown.getY(), 0);
			}
			if(clickDown!=null){
				mMotionInstrument.sendMotion(clickDown);
				if(clickDown.getAction()==MotionEvent.ACTION_UP)
					clickDown = null;
				return true;
			}
		}catch(Exception e){
			
		}

		
		return false;
	}
	
	
	private class MovePoint{
		float x;
		float y;
	}
	


	
	
	private void checkTopApp() {
		Log.e(TAG, "start:"+SystemClock.elapsedRealtime());
		ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
		if(runningTasks.size()<1){
			return ;
		}
		RunningTaskInfo runningTaskInfo = runningTasks.get(0);

		ComponentName topActivity = runningTaskInfo.topActivity;
		Log.e(TAG, "end:"+SystemClock.elapsedRealtime());
		
		if(!myAppPkgName.equals(topActivity.getPackageName())){
			if(tn!=null&&tn.showing)
				tn.hide();
			keyStatuMap.clear();
		}
	}
	
    private class TN {
        final Runnable mMove = new Runnable() {
            @Override
            public void run() {
            	if(Looper.myLooper()==null)
            		Looper.prepare();
                handleMove();
            }
        };
    	
        final Runnable mShow = new Runnable() {
            @Override
            public void run() {
            	if(Looper.myLooper()==null)
            		Looper.prepare();
                handleShow();
                showing = true;
            }
        };

        final Runnable mHide = new Runnable() {
            @Override
            public void run() {
             	if(Looper.myLooper()==null)
            		Looper.prepare();
                handleHide();
                showing = false;
                // Don't do this in handleHide() because it is also invoked by handleShow()
                //mView = null;
            }
        };
        String TAG = "TN";
        boolean localLOGV = false;
        private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        
        ImageView mView;
        Bitmap mouseBitMap = null;
        int mGravity;
        int mX, mY;
        float mHorizontalMargin;
        float mVerticalMargin;
        boolean showing = false;
        boolean moveSelf = false;

        float motionX = 0;
        float motionY = 0;
        int mViewWidth = 0;
        int mViewHeight = 0;
        Point centerPoint = null;
        Point startPoint = null;
        Point endPoint = null;

        WindowManager mWM;

        TN() {
        	
            // XXX This should be changed to use a Dialog, with a Theme.Toast
            mView = new ImageView(mContext);
            
           mouseBitMap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dragged_pointer_100p_normal);
            
            mView.setImageBitmap(mouseBitMap);
            //mView.setBackgroundColor(Color.RED);
            
            mViewWidth = mouseBitMap.getWidth();
            mViewHeight = mouseBitMap.getHeight();
            
            Log.e(TAG, "mViewWidth="+mViewWidth+":mViewHeight:"+mViewHeight);
            
    		centerPoint = new Point();
    		centerPoint.x = mDisplayMetrics.widthPixels/2;
    		centerPoint.y = (mDisplayMetrics.heightPixels-toolBarHeight)/2;
    		startPoint = new Point();
    		startPoint.x = 0;
    		startPoint.y = 0;
    		endPoint = new Point();
    		endPoint.x = mDisplayMetrics.widthPixels-mViewWidth;
    		endPoint.y = centerPoint.y*2-mViewHeight;
    		
    		mParams.x = (int)centerPoint.x;
            mParams.y = (int)centerPoint.y;
            
            final WindowManager.LayoutParams params = mParams;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;
            //params.windowAnimations = com.android.internal.R.style.Animation_Toast;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }


        public void handleMove() {
           Log.e(TAG, "HANDLE MOVE:motionX="+motionX+ "motiony="+motionY);
           if(moveSelf){
               mX = (int)(mParams.x+motionX*20);
               mY=(int)(mParams.y+motionY*20);
           }else{
               mX = (int)(centerPoint.x+centerPoint.x*motionX*1.3);
               mY=(int)(centerPoint.y+(centerPoint.y)*motionY*1.3);
           }

           if(mX<startPoint.x){
           	mX = startPoint.x;
           }else if(mX>endPoint.x){
           	mX = endPoint.x;
           }
           
           if(mY<startPoint.y){
           	mY = startPoint.y;
           }else if(mY>endPoint.y){
           	mY = endPoint.y;
           }
           
           Log.e(TAG, "HANDLE MOVE:centerPoint.x:mX="+mX+ ":mY=" + mY);
           
           mParams.x = mX;
           mParams.y = mY;
           synchronized (this) {
        	   if (mView.getParent() != null) 
        		   mWM.updateViewLayout(mView, mParams);
           }
          	
        }
        
        public void handleShow() {
            if (localLOGV) Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView);
            Context context = mView.getContext().getApplicationContext();
            if (context == null) {
                context = mView.getContext();
            }
            mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            final int gravity = Gravity.LEFT | Gravity.TOP;
            mParams.gravity = gravity;
            
            if(!moveSelf){
                mParams.x = (int)centerPoint.x;
                mParams.y = (int)centerPoint.y;
            }
            
            mParams.verticalMargin = mVerticalMargin;
            mParams.horizontalMargin = mHorizontalMargin;
            synchronized (this) {
                if (mView.getParent() == null) {
                	mWM.addView(mView, mParams);
                }
			}

            trySendAccessibilityEvent();
           
        	mHandler.sendEmptyMessage(CHECK_TOP_APP_FLAG);
        }

        private void trySendAccessibilityEvent() {
//            AccessibilityManager accessibilityManager =
//                    AccessibilityManager.getInstance(mView.getContext());
//            if (!accessibilityManager.isEnabled()) {
//                return;
//            }
//            // treat toasts as notifications since they are used to
//            // announce a transient piece of information to the user
//            AccessibilityEvent event = AccessibilityEvent.obtain(
//                    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
//            event.setClassName(getClass().getName());
//            event.setPackageName(mView.getContext().getPackageName());
//            mView.dispatchPopulateAccessibilityEvent(event);
//            accessibilityManager.sendAccessibilityEvent(event);
        }        

        public void handleHide() {
            if (localLOGV) Log.v(TAG, "HANDLE HIDE: " + this + " mView=" + mView);
            if (mView != null) {
            	synchronized (this) {
                    if (mView.getParent() != null) {
                        if (localLOGV) Log.v(TAG, "REMOVE! " + mView + " in " + this);
                        mWM.removeView(mView);
                    }
            	}
            }
            
            mHandler.removeMessages(CHECK_TOP_APP_FLAG);
           
            //mView = null;
        }
        
        public void move(float x,float y) {
            if (localLOGV) Log.v(TAG, "move: " + this);
            motionX = x;
            motionY = y;
            mHandler.post(mMove);
        }

        public void show() {
            if (localLOGV) Log.v(TAG, "SHOW: " + this);
            mHandler.post(mShow);
        }


        public void hide() {
            if (localLOGV) Log.v(TAG, "HIDE: " + this);
            mHandler.post(mHide);
        }
    }

}
