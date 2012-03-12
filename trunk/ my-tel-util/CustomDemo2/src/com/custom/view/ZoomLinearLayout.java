package com.custom.view;

import java.util.Stack;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;





public class ZoomLinearLayout extends LinearLayout{
	private static final String TAG = "ZoomLinearLayout";
    private boolean backgroundCanMove = true;
	private boolean isFirstLayout = true;
	//视图的宽度及高度
	private int contentHeight = 0;
	private int contentWidth = 0;
	private int left, top, right,bottom;//视图左右边距离屏幕左边的距离和上下边距离屏幕顶部的距离（像素）
	
	private Scroller scroller;
	public View child = null;
	private int currentScreenIndex;

	private GestureDetector gestureDetector;

	// 设置一个标志位，防止底层的onTouch事件重复处理UP事件
	private boolean fling;

	//用于记录是否是第一次创建
	public boolean isFirstCreate = true;
	public int firstCreateHeight ;
	
	public Scroller getScroller() {
		return scroller;
	}

    public ZoomLinearLayout(Context context) {
        this(context, null);
    }

    public ZoomLinearLayout(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	initView(context);
    }
    
	private void initView(final Context context) {
		this.scroller = new Scroller(context);
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
				if(backgroundCanMove){
					scrollToIndex(distanceX, distanceY);
				}
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
//				if(backgroundCanMove){
//					if (Math.abs(velocityX) > ViewConfiguration.get(context)
//							.getScaledMinimumFlingVelocity()) {// 判断是否达到最小轻松速度，取绝对值的
//						if (velocityX > 0 && currentScreenIndex > 0) {
//							fling = true;
//							//scrollToScreen(currentScreenIndex - 1);
//						} else if (velocityX < 0
//								&& currentScreenIndex < getChildCount() - 1) {
//							fling = true;
//							//scrollToScreen(currentScreenIndex + 1);
//						}
//					}
//					
//					float distanceY = -velocityY*Math.abs(velocityY)/5000;
//					float distanceX = -velocityX*Math.abs(velocityX)/5000;
//					
//					scrollToIndex(distanceX, distanceY);
//				}
				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
	}
	
	private void scrollToIndex(float distanceX, float distanceY){
		//防止滑倒图像外面
		int endX = (int)(getScrollX()+distanceX);
		int endY = (int)(getScrollY()+distanceY);
		int width = child.getWidth()<contentWidth?contentWidth:child.getWidth();
		int height = child.getHeight()<contentHeight?contentHeight:child.getHeight();
		if(endX<0){
			distanceX = 0-getScrollX();
		}else if(endX+contentWidth>width){
			distanceX = width-(getScrollX()+contentWidth);
		}
		if(endY<0){
			distanceY = 0-getScrollY();
		}else if(endY+contentHeight>height){
			distanceY = height-(getScrollY()+contentHeight);
		}
		scrollBy((int) distanceX, (int)distanceY);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() > 1) {
            throw new IllegalStateException("ZoomLinearLayout can host only one direct child");
        }

		if(isFirstLayout){
			 contentWidth = right-left;
			 contentHeight = bottom-top;
			 this.left = left;
			 this.top = top;
			 this.right = right;
			 this.bottom = bottom;
			 isFirstLayout = false;
		}
		child = getChildAt(0);
		
		//Log.e(TAG,"view:"+this.getWidth()+"view:"+this.getHeight()+"left:"+left+"left:"+left+"top:"+top+"right:"+right+"bottom:"+bottom+"childgetWidth:"+child.getWidth()+"childgetHeight:"+child.getHeight());
		
        ViewGroup.LayoutParams mLayoutParams =  child.getLayoutParams();
        //Log.e(TAG,"view:"+mLayoutParams.width+":"+mLayoutParams.height);
        if(mLayoutParams.width<contentWidth||contentHeight>mLayoutParams.height){
        	
        	int height = 0;
        	int width = 0;
        	if(mLayoutParams.width*1.0f/contentWidth>mLayoutParams.height*1.0f/contentHeight){
        		height = contentHeight;
        		width = (int)(mLayoutParams.width*(contentHeight*1.0f/mLayoutParams.height));
        	}else{
        		width = contentWidth;
        		height = (int)(mLayoutParams.height*(contentWidth*1.0f/mLayoutParams.width));
        	}
			mLayoutParams = new LinearLayout.LayoutParams(width,height);
			child.setLayoutParams(mLayoutParams);
        }
	}
	
    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ZoomLinearLayout can host only one direct child");
        }

        super.addView(child);
    }
	
	
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {

			scrollTo(scroller.getCurrX(), 0);
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if (!fling) {
				//snapToDestination();
			}
			fling = false;
			break;
		default:
			break;
		}
		
        if (event.getPointerCount() == 2) {
        	
        } else {
        }

		return true;
	}
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
    	this.onTouchEvent(event);
    	return super.dispatchTouchEvent(event);
    }

    public void setBackgroundMove(boolean canMove){
    	backgroundCanMove = canMove;
    }

    public boolean getBackgroundMove(){
    	return backgroundCanMove;
    }
}














