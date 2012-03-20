package com.custom.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.custom.bean.ResourceBean;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SecondViewGroup extends ViewGroup {

	private static final String TAG = "scroller";

	private Scroller scroller;

	private int currentScreenIndex;

	private GestureDetector gestureDetector;
	
	private Context context = null;

	// 设置一个标志位，防止底层的onTouch事件重复处理UP事件
	private boolean fling;


	public Scroller getScroller() {
		return scroller;
	}

	public SecondViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SecondViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public SecondViewGroup(Context context) {
		super(context);
		//this.resourceInfo = resourceInfo;
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
				Log.d(TAG, "on scroll>>>>>>>>>>>>>>>>>distanceX<<<<<<<<<<<<<<>>>"+distanceX);
				if ((distanceX > 0 && currentScreenIndex < getChildCount() - 1)// 防止移动过最后一页
						|| (distanceX < 0 && getScrollX() > 0)) {// 防止向第一页之前移动
					scrollBy((int) distanceX, 0);
					Log.d(TAG, "on scroll>>>>>>>>>>>>>>>>>防止向第一页之前移动<<<<<<<<<<<<<<>>>");
				}
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				Log.d(TAG, "min velocity >>>"
						+ ViewConfiguration.get(context)
								.getScaledMinimumFlingVelocity()
						+ " current velocity>>" + velocityX);
				if (Math.abs(velocityX) > ViewConfiguration.get(context)
						.getScaledMinimumFlingVelocity()) {// 判断是否达到最小轻松速度，取绝对值的
					if (velocityX > 0 && currentScreenIndex > 0) {
						Log.d(TAG, ">>>>fling to left");
						fling = true;
						scrollToScreen(currentScreenIndex - 1);
					} else if (velocityX < 0
							&& currentScreenIndex < getChildCount() - 1) {
						Log.d(TAG, ">>>>fling to right");
						fling = true;
						scrollToScreen(currentScreenIndex + 1);
					}
				}

				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
		
		//createIndexButton();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>left: " + left + " top: " + top + " right: " + right
				+ " bottom:" + bottom);

		/**
		 * 设置布局，将子视图顺序横屏排列
		 */
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setVisibility(View.VISIBLE);
			child.measure(right - left, bottom - top);
			child.layout(0 + i * getWidth(), 0, getWidth() + i * getWidth(),
					getHeight());
		}
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			Log.d(TAG, ">>>>>>>>>>computeScroll>>>>>"+scroller.getCurrX());

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
			Log.d(TAG, ">>ACTION_UP:>>>>>>>> MotionEvent.ACTION_UP>>>>>");
			if (!fling) {
				snapToDestination();
			}
			fling = false;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 切换到指定屏
	 * 
	 * @param whichScreen
	 */
	public void scrollToScreen(int whichScreen) {
		if (getFocusedChild() != null && whichScreen != currentScreenIndex
				&& getFocusedChild() == getChildAt(currentScreenIndex)) {
			getFocusedChild().clearFocus();
		}

		final int delta = whichScreen * getWidth() - getScrollX();
		scroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		invalidate();

		currentScreenIndex = whichScreen;
	}

	/**
	 * 根据当前x坐标位置确定切换到第几屏
	 */
	private void snapToDestination() {
		scrollToScreen((getScrollX() + (getWidth() / 2)) / getWidth());
	}
	
	ArrayList<Entry<String,ResourceBean>> resourceInfo = null;
	int screenHeight = 0;
	int screenWidth = 0;
	int pageNum = 1;
	protected void createIndexButton(ArrayList<Entry<String,ResourceBean>> resourceInfo) {		
		this.resourceInfo = resourceInfo;
		WindowManager manage = ((Activity)context).getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		
		for(int j=0;j<5;j++){
			AbsoluteLayout pageLayout = new AbsoluteLayout(context);
			LinearLayout.LayoutParams pageLayoutParams = new LinearLayout.LayoutParams(
					screenWidth, screenHeight);
			pageLayout.setLayoutParams(pageLayoutParams);
			this.addView(pageLayout);
			for(i=(pageNum-1)*8;i<(pageNum*8>resourceInfo.size()?resourceInfo.size():pageNum*8);i++){
				ResourceBean resourceBean = resourceInfo.get(i).getValue();
				IndexImageButtonImp imageView = null;
				setXY(resourceBean);
				imageView = new IndexImagePicButton(context,null,resourceBean);
				pageLayout.addView(imageView);
			}
		}

	}
	int i=0;
	protected void setXY(ResourceBean resourceBean) {
		//设置图标的位置
		// TODO Auto-generated method stub
		//int[] indexs = MondifyIndexImageIndex.getImageIndexs(resourceBean.getBtnKey());
		resourceBean.setX(i%4*200+50);
		resourceBean.setY(i/4%2*200+150);
	}

}
