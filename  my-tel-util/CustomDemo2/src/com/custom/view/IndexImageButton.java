package com.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class IndexImageButton extends LinearLayout  {
	private static final String TAG = "IndexImageView";
	
	private GestureDetector gestureDetector=null;
	private boolean imageCanMove = false;
	private BackgroundLinearLayout scrollView = null;
	private Bitmap bm=null;
	public IndexImageButton(Context context,BackgroundLinearLayout scrollView,Bitmap bm) {
		super(context);
		this.scrollView = scrollView;
		this.bm = bm;
		initView(context);
		// TODO Auto-generated constructor stub
	}
	
	int touchState = 0;
	int touchX = 0;
	int touchY = 0;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		   int action = ev.getAction();
		   
		   switch(action){
		   case MotionEvent.ACTION_DOWN:
		        Log.e(TAG,"onInterceptTouchEvent action:ACTION_DOWN");
		        touchState = 0;
		        break;
		   case MotionEvent.ACTION_MOVE:
		        Log.e(TAG,"onInterceptTouchEvent action:ACTION_MOVE");
		        touchState = 2;
		        break;
		   case MotionEvent.ACTION_UP:
		        Log.e(TAG,"onInterceptTouchEvent action:ACTION_UP");
		        break;
		   case MotionEvent.ACTION_CANCEL:
		        Log.e(TAG,"onInterceptTouchEvent action:ACTION_CANCEL");
		        break;
		   }
		   return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.e(TAG,"action:ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			Log.e(TAG,"action:ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			Log.e(TAG,"action:ACTION_UP");
			break;
		default:
			break;
		}
		
        if (event.getPointerCount() == 2) {
        	
        } else {
        }

		return true;
	}
	
	private void initView(final Context context) {
		 ImageView jpgView = new ImageView(context);
		 jpgView.setImageBitmap(bm);
		 int with = bm.getWidth();
		 int height = bm.getHeight();
		 LayoutParams alayout = new LayoutParams(
				 200, 200);
		 jpgView.setLayoutParams(alayout);
		 this.addView(jpgView);
		 
		this.gestureDetector = new GestureDetector(new OnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {

				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				Log.e("IndexView",scrollView.getBackgroundMove()+":"+IndexImageButton.this.getImageMove());
				scrollView.setBackgroundMove(false);
				IndexImageButton.this.setImageMove(true);

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(imageCanMove){
					float distanceY = -velocityY*Math.abs(velocityY)/5000;
					float distanceX = -velocityX*Math.abs(velocityX)/5000;
					
					AbsoluteLayout.LayoutParams alayout = (AbsoluteLayout.LayoutParams)IndexImageButton.this.getLayoutParams();
					alayout.x=(int)(alayout.x+distanceX);
					alayout.y=(int)(alayout.y+distanceY);
					IndexImageButton.this.setLayoutParams(alayout);
					
				}
				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
	}
	
    public void setImageMove(boolean canMove){
    	imageCanMove = canMove;
    }
    
    public boolean getImageMove(){
    	return imageCanMove;
    }
    
    
}
