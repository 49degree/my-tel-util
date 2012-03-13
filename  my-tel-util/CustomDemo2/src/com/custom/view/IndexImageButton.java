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
	private boolean imageCanMove = true;
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
	int startTouchX = 0;
	int startTouchY = 0;
	int endTouchX = 0;
	int endTouchY = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(gestureDetector!=null){
			gestureDetector.onTouchEvent(event);
			return true;
		}else{
			return false;
		}

	}
	
	private void initView(final Context context) {
		ImageView jpgView = new ImageView(context);
		jpgView.setImageBitmap(bm);
		int with = bm.getWidth();
		int height = bm.getHeight();
		LayoutParams alayout = new LayoutParams(200, 200);
		jpgView.setLayoutParams(alayout);
		this.addView(jpgView);

		//如果可以移动
		if(imageCanMove){
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
					moveImage(distanceX, distanceY);
					return true;
				}
				@Override
				public void onLongPress(MotionEvent e) {
				}
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					return true;
				}
				@Override
				public boolean onDown(MotionEvent e) {
					return false;
				}
			});
		}
	}
	
	private void moveImage(float distanceX, float distanceY){
		
		AbsoluteLayout.LayoutParams alayout = (AbsoluteLayout.LayoutParams)this.getLayoutParams();
		alayout.x=(int)(alayout.x+distanceX);
		alayout.y=(int)(alayout.y+distanceY);
		if(alayout.x<0){
			alayout.x=0;
		}else if(alayout.x>this.scrollView.getWidth()-alayout.width){
			alayout.x=this.scrollView.getWidth()-alayout.width;
		}
		
		if(alayout.y<0){
			alayout.y=0;
		}else if(alayout.y>this.scrollView.getHeight()-alayout.height){
			alayout.x=this.scrollView.getHeight()-alayout.height;
		}
		IndexImageButton.this.setLayoutParams(alayout);
		
	}
	
    public void setImageMove(boolean canMove){
    	imageCanMove = canMove;
    }
    
    public boolean getImageMove(){
    	return imageCanMove;
    }
    
    
}
