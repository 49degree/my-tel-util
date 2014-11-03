package com.yangxp.config.view.button;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yangxp.config.bean.Mappings;
import com.yangxp.config.view.InformationBar;
import com.yangxp.ginput.R;

public class MoveTraceMotionButton extends SurfaceView implements MapperViewInterface,SurfaceHolder.Callback, Runnable {
	private static String TAG = "MoveTraceMotionButton";
	
	private final static int addLen = 1024;
	private final static int pointDataLen = 12;
	private final static int packHeaderLen = 48;//不包含长度字段4byte
	
	protected ViewTouchListener mViewTouchListener;
	protected RemoveSelfListener mRemoveSelfListener;
	protected Mappings mKeyMapping;
	protected boolean mIsActive = false;
	protected Context mContext;
	protected HashMap<Integer,MapperViewInterface> mMapperButtonKeyMap;
	
	public static final int TIME_IN_FRAME = 50;
	private Paint mPaint = null;
	private Paint mTextPaint = null;
	private SurfaceHolder mSurfaceHolder = null;
	private boolean mRunning = false;
	private Canvas mCanvas = null;
	private float mPosX, mPosY;
	private final int len = 16; 
	private PointFloat[] points = new PointFloat[len];
	private Path[] mPath = new Path[len];
	
	private Bitmap linsBegingBitmap;
	private Bitmap linsEndBitmap;
	
	private List<PointFloat> linsBeging = new ArrayList<PointFloat>();
	private List<PointFloat> linsEnding = new ArrayList<PointFloat>();
	
	/*
	缓冲区基本属性 
	这几个属性是每个缓冲区都有的并且是常用的操作。 
	a. 容量(capacity),缓冲区大小 
	b. 限制(limit),第一个不应被读取或写入的字节的索引，总是小于容量。 
	c. 位置(position)，下一个被读取或写入的字节的索引，总是小于限制。 
	d. clear()方法：设置limit为capacity，position为0。 
	e. filp()方法：设置limit为当前position，然后设置position为0。 
	f. rewind()方法：保持limit不变，设置position为0。
	*/

	private ByteBuffer buffer = null;//ByteBuffer.allocate(addLen);
	private int keycode = -1;
	
	private FrameLayout mRootView;
	private AbsoluteLayout mButtonGroupView = null;
	private TextView keyText = null;
	protected InformationBar mInformationBar;
	private int statusBarHeight = 0;
	
	public MoveTraceMotionButton(Context context,Mappings keyMapping,HashMap<Integer,MapperViewInterface> mapperButtonKeyMap,FrameLayout rootView,AbsoluteLayout buttonGroupView) {
		super(context);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
		mCanvas = new Canvas();
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(5);
		mPath = new Path[len];
		for(int i=0;i<len;i++){
			mPath[i] = new Path();
			points[i] = new PointFloat();
		}
		mTextPaint = new Paint();
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setStrokeWidth(5);
		mTextPaint.setTextSize(20);
		
		
		mContext = context;
		mKeyMapping = keyMapping;
		mMapperButtonKeyMap = mapperButtonKeyMap;

		mRootView = rootView;
		mButtonGroupView = buttonGroupView;
		
		if(mKeyMapping.record==null){
			buffer = ByteBuffer.allocate(addLen);
		}else{
			Log.i(TAG, "mKeyMapping.record:"+mKeyMapping.record.length);
			buffer = ByteBuffer.wrap(mKeyMapping.record);
			createPathPoint();
		}
		
		setZOrderOnTop(true);//设置画布  背景透明
	    getHolder().setFormat(PixelFormat.TRANSPARENT);
		
		linsBegingBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.gesture_line_startpoint);
		linsEndBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.gesture_line_endpoint);
		
		if(mKeyMapping.key!=0){
			mMapperButtonKeyMap.put(mKeyMapping.key, this);
		}else{
			//新建按钮
			//新建按钮
			mInformationBar = new InformationBar(mContext);
			showNewButtonNotice();
		}
		setImage();
		
		Rect frame = new Rect();
		mButtonGroupView.getWindowVisibleDisplayFrame(frame);
		statusBarHeight = frame.top;
	}
	

	public void setIsActiveButton(boolean isActive){
		mIsActive = isActive;
		setImage();
		if(mIsActive){
			requestFocus();
		}
	}
	
	public Mappings getKeyMapping() {
		// TODO Auto-generated method stub
		return mKeyMapping;
	}
	
	public void setKeyMapping(Mappings keyMapping) {
		// TODO Auto-generated method stub
		mKeyMapping = keyMapping;
	}

	public TextView getKeyText(){
		return keyText;
	}

	@Override
	public void setPisition(float x, float y) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setViewTouchListener(ViewTouchListener viewTouchListener){
		mViewTouchListener = viewTouchListener;
	}
	
	public void setRemoveSelfListener(RemoveSelfListener removeSelfListener){
		mRemoveSelfListener = removeSelfListener;
	}


	@Override
	public void setImage() {
		// TODO Auto-generated method stub
		if(mIsActive && mKeyMapping.key == 0){
			setBackgroundColor(Color.argb(120, 1, 1, 1)); //背景透明度
		}else{
			setBackgroundColor(Color.argb(0, 1, 1, 1)); //背景透明度
		}
		
		if(!mIsActive||(mIsActive&&mKeyMapping.key!=0)){
			if(keyText!=null&&keyText.getParent()!=null)
				mButtonGroupView.removeView(keyText);
				
			keyText = new TextView(mContext);
			
			//String joyName = ButtonUtil.getKeyText(mKeyMapping.key);
			String keyName = ButtonUtil.getKeyText(mKeyMapping.key);
			Log.i(TAG, mKeyMapping.key+":"+mKeyMapping.keyClick+":mKeyMapping.key="+keyName);
			keyText.setText(keyName);
			keyText.setTextColor(Color.WHITE);
			keyText.setTextSize(20);
			if(mIsActive){
				keyText.setBackgroundColor(Color.BLUE);
			}else{
				keyText.setBackgroundColor(0);
			}
			
			AbsoluteLayout.LayoutParams aL = new AbsoluteLayout.LayoutParams(
					AbsoluteLayout.LayoutParams.WRAP_CONTENT,
					AbsoluteLayout.LayoutParams.WRAP_CONTENT,
					mKeyMapping.x, mKeyMapping.y);
			mButtonGroupView.addView(keyText,aL);
			
			keyText.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mViewTouchListener.onViewTouch(MoveTraceMotionButton.this);
				}
				
			});
		}else if(mIsActive && mButtonGroupView!=null){
			if(keyText!=null&&keyText.getParent()!=null)
				mButtonGroupView.removeView(keyText);
		}
		//this.invalidate();
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
		Log.i(TAG, "onJoysticMotion action="+action+":"+event.getKeyCode() );
		
		Iterator<Map.Entry<Integer,MapperViewInterface>> it = mMapperButtonKeyMap.entrySet().iterator();
		int keyCount = 0;
		while(it.hasNext()){
			if(it.next().getValue().getKeyMapping().keyClick == event.getKeyCode()){
				keyCount=1;
				break;
			}
		}
		if(mMapperButtonKeyMap==null )
			return false;
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
	public boolean dispatchGenericMotionEvent(MotionEvent event){
		if (0 == (event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK)) {
			return super.dispatchTouchEvent(event);
		}
		return onJoysticMotion(event);

	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		//Log.e(TAG, "dispatchKeyEvent event= "+event.getKeyCode()+":"+event.getAction()+":"+mKeyMapping.key);
		if(event.getKeyCode()==4){
			if(mKeyMapping.key==0){
				//关闭界面
				mRootView.removeView(this);
				if(mRemoveSelfListener!=null &&  event.getAction() == KeyEvent.ACTION_UP)
					mRemoveSelfListener.onRemoveSelf(this);
				return true;
			}else{
				setImage();
				return false;
			}
		}
		return onJoysticKey(event);
	}
	

	private void createPathPoint(){ 
		//Log.e("createPathPoint", "allocate:"+buffer.position()+":"+buffer.capacity()+":"+buffer.limit());
		buffer.rewind();
		long srcStartTime = 0;
		long myStartTime = 0;
		long srcEventTime = 0;
		long myEventTime = 0;
		while(buffer.limit()>buffer.position()+4){
			/*
			buffer.putInt(event.getPointerCount()*pointDataLen+packHeaderLen);//长度
			buffer.putInt(event.getActionMasked());//动作类型
			buffer.putLong(event.getDownTime());//按下时间
			buffer.putLong(event.getEventTime());
			buffer.putInt(event.getMetaState());
			buffer.putFloat(event.getXPrecision());
			buffer.putFloat(event.getYPrecision());
			buffer.putInt(event.getDeviceId());
			buffer.putInt(event.getEdgeFlags());
			buffer.putInt(event.getSource());
			buffer.putInt(event.getFlags());
			*/
			//拆包
			int packLen = buffer.getInt();//长度
			if(packLen<=0)
				continue;
			ByteBuffer motionPack = ByteBuffer.allocate(packLen);
			buffer.get(motionPack.array());//获取包数据
			int pointerCount = (packLen-packHeaderLen)/pointDataLen;//触摸点数
			//Log.e("motionPack", "motionPack:"+motionPack.position()+":"+motionPack.capacity()+":"+motionPack.limit());
			int action = motionPack.getInt();//动作类型
//			motionPack.getLong();//按下时间
//			motionPack.getLong();
//			motionPack.getInt();
//			motionPack.getFloat();
//			motionPack.getFloat();
//			motionPack.getInt();
//			motionPack.getInt();
//			motionPack.getInt();
//			motionPack.getInt();
			
			motionPack.position(packHeaderLen);
			
			for(int i=0;i<pointerCount;i++){
				int pointId = motionPack.getInt();
				float x = motionPack.getFloat();
				float y = motionPack.getFloat();

				switch (action) {
				case MotionEvent.ACTION_POINTER_DOWN:
					linsBeging.add(new PointFloat(x,y));
				case MotionEvent.ACTION_DOWN:
					mPath[pointId].moveTo(x, y);
					if(mKeyMapping.x==0){
						mKeyMapping.x = (int)x;
						mKeyMapping.y = (int)y;
					}
					linsBeging.add(new PointFloat(x,y));
					break;
				case MotionEvent.ACTION_MOVE:
					mPath[pointId].quadTo(points[pointId].x, points[pointId].y, x, y);
					break;
				case MotionEvent.ACTION_POINTER_UP:
					linsEnding.add(new PointFloat(x,y));
					break;
				case MotionEvent.ACTION_UP:
					linsEnding.add(new PointFloat(x,y));
					mPath[i].moveTo(x, y);
					mKeyMapping.record = buffer.array();
					break;
				}
				// 记录当前触摸点得当前得坐标
				points[pointId].x = x;
				points[pointId].y = y;
			}
		}
	}
	
	
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		if(!mIsActive||(mIsActive&&mKeyMapping.key!=0))
			return false;
		
		//Log.e("onTouchEvent", "onTouchEvent:"+event.getPointerCount()+":"+event.getActionMasked()+":"+event.getActionIndex());

		int eventPackLen = event.getPointerCount()*pointDataLen+packHeaderLen+4;
		int bufferPosition =  buffer.position();
		if((buffer.position()+eventPackLen)>=buffer.capacity()){
			byte[] temp = buffer.array();
			buffer = ByteBuffer.allocate(buffer.capacity()+addLen); //.allocate(temp.length+1024);
			buffer.put(temp,0,bufferPosition);
			//Log.e("onTouchEvent", "allocate:"+buffer.position()+":"+buffer.capacity()+":"+buffer.limit());
		}
		buffer.limit(buffer.position()+eventPackLen);
		buffer.putInt(eventPackLen-4);//长度，不包括本长度字段
		buffer.putInt(event.getAction());//动作类型
		buffer.putLong(event.getDownTime());//按下时间
		buffer.putLong(event.getEventTime());
		buffer.putInt(event.getMetaState());
		buffer.putFloat(event.getXPrecision());
		buffer.putFloat(event.getYPrecision());
		buffer.putInt(event.getDeviceId());
		buffer.putInt(event.getEdgeFlags());
		buffer.putInt(event.getSource());
		buffer.putInt(event.getFlags());
	
		
		float topX = event.getRawX() - event.getX();
		float topY = event.getRawY() - event.getY();
		//Log.e("onTouchEvent", "onTouchEvent topX:"+topX+":topY:"+topY);
		for(int i=0;i<event.getPointerCount();i++){
			int pointId = event.getPointerId(i);
			float x = event.getX(i);//+topX;
			float y = event.getY(i);//+topY;
			//Log.e("onTouchEvent", "onTouchEvent pointid:"+pointId+":"+x+":"+y+":"+action);
			if(event.getDeviceId()!=123456){ 
				buffer.putInt(pointId);
				buffer.putFloat(x); 
				buffer.putFloat(y);
			}
			switch (action) {
			case MotionEvent.ACTION_POINTER_DOWN:
				linsBeging.add(new PointFloat(x,y));
			case MotionEvent.ACTION_DOWN:
				mPath[pointId].moveTo(x, y);
				if(mKeyMapping.x==0){
					mKeyMapping.x = (int)x;
					mKeyMapping.y = (int)y;
				}
				linsBeging.add(new PointFloat(x,y));
				break;
			case MotionEvent.ACTION_MOVE:
				mPath[pointId].quadTo(points[pointId].x, points[pointId].y, x, y);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				linsEnding.add(new PointFloat(x,y));
				break;
			case MotionEvent.ACTION_UP:
				linsEnding.add(new PointFloat(x,y));
				mPath[i].moveTo(x, y);
				mKeyMapping.record = buffer.array();
				break;
			}
			// 记录当前触摸点得当前得坐标
			points[pointId].x = x;
			points[pointId].y = y;
		}
		synchronized (mSurfaceHolder) {
			try{
				mSurfaceHolder.notify();
			}catch(Exception e){
				
			}
		}
		return true;
	}

	
	private void onDraw() {
		if(mCanvas==null)
			return;
		
		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		if(mIsActive && mKeyMapping.key==0){
			String keyName = ButtonUtil.getKeyText(mKeyMapping.key);
			mCanvas.drawText(keyName,mKeyMapping.x, mKeyMapping.y, mTextPaint);
		}
		
		
		// 绘制曲线
		for(int i=0;i<len;i++){
			mCanvas.drawPath(mPath[i], mPaint);
			//Log.d("drawPath", "drawPath:"+mPath[i]);
			//mCanvas.drawPoint(points[i].x, points[i].y,mPaint);
		}
		
		for(PointFloat point:linsBeging){
			mCanvas.drawBitmap(linsBegingBitmap, point.x-10, point.y-10, null);
		}
		
		for(PointFloat point:linsEnding){
			mCanvas.drawBitmap(linsEndBitmap, point.x-10, point.y-10, null);
		}
		
//		mCanvas.drawText("当前触笔X：" + mPosX, 0, 20, mTextPaint);
//		mCanvas.drawText("当前触笔Y:" + mPosY, 0, 40, mTextPaint);
//		mCanvas.drawText("keycode:" + keycode,0, 60, mTextPaint);
	}

	public void run() {
		// TODO Auto-generated method stub
		while (mRunning) {
			try{
				long startTime = System.currentTimeMillis();
				synchronized (mSurfaceHolder) {
					try{
						mCanvas = mSurfaceHolder.lockCanvas();
						onDraw();
					}catch(Exception e){
						
					}
					try{
						mSurfaceHolder.unlockCanvasAndPost(mCanvas);
					}catch(Exception e){
						
					}
					try{
						mSurfaceHolder.wait();
					}catch(Exception e){
						
					}
					
				}
//				long endTime = System.currentTimeMillis();
//				int diffTime = (int) (endTime - startTime);
//				while (diffTime <= TIME_IN_FRAME) {
//					diffTime = (int) (System.currentTimeMillis() - startTime);
//					Thread.yield();
//				}
			}catch(Exception e){
				
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		synchronized (mSurfaceHolder) {
			try{
				mSurfaceHolder.notify();
			}catch(Exception e){
				
			}
			
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mRunning = true;
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub\
		
		Log.e(TAG, "surfaceDestroyed====================");
		mRunning = false;
	}


    public class PointFloat {
    	public PointFloat(){
    		
    	}
    	public PointFloat(float x,float y){
    		this.x = x;
    		this.y = y;
    	}
    	float x;
    	float y;
    }


	@Override
	public void showNewButtonNotice() {
		// TODO Auto-generated method stub
         mInformationBar.informationBarShow();
		mInformationBar.showInformations(R.string.info_bar_record_entered_help);
	}
	protected void showInfoByString(int rid){
		InformationBar informationBar = new InformationBar(mContext);
		informationBar.informationBarShow();
		informationBar.showInformations(rid);
	}	
}
