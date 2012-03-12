package com.custom.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

public class IndexImageView extends ImageView  {
	private static final String TAG = "ZoomLinearLayout";
	
	private GestureDetector gestureDetector=null;
	private boolean imageCanMove = false;
	private ZoomLinearLayout scrollView = null;
	public IndexImageView(Context context,ZoomLinearLayout scrollView) {
		super(context);
		initView(context);
		this.scrollView = scrollView;
		// TODO Auto-generated constructor stub
	}
	
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		   int action = ev.getAction();
		   switch(action){
		   case MotionEvent.ACTION_DOWN:
		        Log.e(TAG,"onInterceptTouchEvent action:ACTION_DOWN");
//		      return true;
		        break;
		   case MotionEvent.ACTION_MOVE:
		        Log.e(TAG,"onInterceptTouchEvent action:ACTION_MOVE");
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
		return false;
	}
	
	private void initView(final Context context) {
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
				Log.e("IndexView",scrollView.getBackgroundMove()+":"+IndexImageView.this.getImageMove());
				scrollView.setBackgroundMove(false);
				IndexImageView.this.setImageMove(true);

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(imageCanMove){
					float distanceY = -velocityY*Math.abs(velocityY)/5000;
					float distanceX = -velocityX*Math.abs(velocityX)/5000;
					
					AbsoluteLayout.LayoutParams alayout = (AbsoluteLayout.LayoutParams)IndexImageView.this.getLayoutParams();
					alayout.x=(int)(alayout.x+distanceX);
					alayout.y=(int)(alayout.y+distanceY);
					IndexImageView.this.setLayoutParams(alayout);
					
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
