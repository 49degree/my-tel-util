package com.guanri.android.lib.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;





public class ZoomLinearLayout extends LinearLayout{
	private static final String TAG = "ZoomLinearLayout";
	private Stack<Node> stack = new Stack<Node>(); 
	private boolean isRootLayout = false;
	
    private boolean startTwoTouchPoint = false;
    private double startDistance;
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
				scrollToIndex(distanceX, distanceY);
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (Math.abs(velocityX) > ViewConfiguration.get(context)
						.getScaledMinimumFlingVelocity()) {// 判断是否达到最小轻松速度，取绝对值的
					if (velocityX > 0 && currentScreenIndex > 0) {
						fling = true;
						//scrollToScreen(currentScreenIndex - 1);
					} else if (velocityX < 0
							&& currentScreenIndex < getChildCount() - 1) {
						fling = true;
						//scrollToScreen(currentScreenIndex + 1);
					}
				}
				
				float distanceY = -velocityY* Math.abs(velocityY)/5000;
				float distanceX = -velocityX/5000 *Math.abs(velocityX);
				
				scrollToIndex(distanceX, distanceY);
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
		int height = child.getHeight()<contentWidth?contentHeight:child.getHeight();
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
            if (startTwoTouchPoint == false) {
                startDistance = getDistance(event.getX(0), event
                        .getY(0), event.getX(1), event.getY(1));
                startTwoTouchPoint = true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                double distance = getDistance(event.getX(0), event
                        .getY(0), event.getX(1), event.getY(1));
                float rate = getZoomRate(distance-startDistance);
                isRootLayout = true;
              //判断用户是放大操作
                if( rate > 1 ){
                	if(child.getWidth()<contentWidth*2||child.getHeight()<contentHeight*2){
                    	Node node = new Node();
                    	Node resultNode = null;
                    	if(! stack.empty()){
                    		resultNode = stack.peek();
                    	}
                    	zoomInAndOut(ZoomLinearLayout.this,rate,checkIsFormer(rate), node, resultNode);
                    	stack.push(node);
                	}

                } else {
                	if(! stack.isEmpty()){
                		zoomInAndOuts(ZoomLinearLayout.this,rate,checkIsFormer(rate), stack.pop());
                	}
                }
            }
           // return false;
        } else {
            startTwoTouchPoint = false;
        }

		return true;
	}
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
    	this.onTouchEvent(event);
    	return super.dispatchTouchEvent(event);
    }

    
    private static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private float getZoomRate(double distance){
		int width = child.getWidth()<contentWidth?contentWidth:child.getWidth();
		int height = child.getHeight()<contentWidth?contentHeight:child.getHeight();
    	float rate = 1.1f;
    	if(distance<0){
    		rate = 0.9f;
    	}
    	if(child.getWidth()*rate+0.5<contentWidth){
    		return new Float(contentWidth)/width;
    	}
    	if(child.getHeight()*rate+0.5<contentHeight){
    		return new Float(contentHeight)/height;
    	}
    	
    	if(child.getWidth()*rate+0.5>=contentWidth*2){
    		return new Float(contentWidth*2)/width;
    	}
    	if(child.getHeight()*rate+0.5>=contentHeight*2){
    		return new Float(contentHeight*2)/height;
    	}
    	return rate;
    }
    private boolean checkIsFormer(float rate){
    	if(child.getWidth()*rate+0.5<contentWidth){
    		return true;
    	}
    	if(child.getHeight()*rate+0.5<contentHeight){
    		return true;
    	}
    	return false;
    }
    
  //处理用户放大事件
    public void zoomInAndOut(View parentView,float rate,boolean isFormer,Node node, Node resultNode){
		if(parentView instanceof ViewGroup&&((ViewGroup) parentView).getChildCount()>0){
	    	int childCount = ((ViewGroup)parentView).getChildCount();
	    	List<Node> childNodeList = new ArrayList<Node>();
	    	
	    	for(int i=0;i<childCount;i++){
	    		View childView= ((ViewGroup)parentView).getChildAt(i);
	    		
	        	Node childNode = new Node();
	        	
	        	//当发生放大时，先将放大之前控件的大小记录下来，用于在控件缩小时恢复控件大小
	    		ViewAttribute viewAttribute = new ViewAttribute();
	    		viewAttribute.setCount(i);
	    		viewAttribute.setWidth(childView.getWidth());
	    		viewAttribute.setHeight(childView.getHeight());
	    		viewAttribute.setViewId(childView.getId());
	    		
	    		ViewGroup.LayoutParams childViewParams = childView.getLayoutParams();
	    		
	    		Node childResultNode = null;
	    		if(resultNode != null){
	    			//处理用户非第一次放大，不是第一次时ViewAttribute保存了控件放大保留小数位的大小，为了减少误差
	    			childResultNode = resultNode.getChildNodes().get(i);
	    			childViewParams.width = (int)Math.round(childResultNode.getViewAttribute().getFactWidth()*rate);
	    			childViewParams.height = (int)Math.round(childResultNode.getViewAttribute().getFactHeight()*rate);
	    			viewAttribute.setFactHeight(Math.round(childResultNode.getViewAttribute().getFactHeight()*rate));
		        	viewAttribute.setFactWidth(Math.round(childResultNode.getViewAttribute().getFactWidth()*rate));
	    		} else {
	    			//处理用户时第一次进行放大
	    			childViewParams.width = (int)Math.round(childView.getWidth()*rate);
	    			childViewParams.height = (int)Math.round(childView.getHeight()*rate);
	    			viewAttribute.setFactHeight(Math.round(childView.getHeight()*rate));
		        	viewAttribute.setFactWidth(Math.round(childView.getWidth()*rate));
	    		}
	        	
	        	if(childView.equals(child)&&isFormer){
	        		childViewParams.height = this.contentHeight;
	        		childViewParams.width = this.contentWidth;	
	        	}
	        	childView.setLayoutParams(childViewParams);
	        	
	        	//当控件为TextView的子类时处理textSize
	        	if(childView instanceof TextView){
	        		float textSize = ((TextView) childView).getTextSize();
	        		viewAttribute.setTextSize(textSize);
	        		((TextView) childView).setTextSize(TypedValue.COMPLEX_UNIT_PX,Math.round(textSize*rate-0.1));
	        		
	        	}
	        	childNode.setViewAttribute(viewAttribute);
	    		if(childView instanceof ViewGroup&&((ViewGroup) childView).getChildCount()>0){
	    			zoomInAndOut(childView,rate,isFormer, childNode, childResultNode);
	    		}
	    		childNodeList.add(childNode);
	    	}
	    	node.setChildNodes(childNodeList);
		}
    }
    
  //处理缩小事件
    public void zoomInAndOuts(View parentView,float rate,boolean isFormer,Node node){
		if(parentView instanceof ViewGroup&&((ViewGroup) parentView).getChildCount()>0){
	    	int childCount = ((ViewGroup)parentView).getChildCount();
	    	
	    	
	    	for(int i=0;i<childCount;i++){
	    		View childView= ((ViewGroup)parentView).getChildAt(i);
	    		
	    		//用户缩小界面时，从放大的记录中取出保存的大小恢复控件
	    		Node childNode = node.getChildNodes().get(i);
	    		ViewAttribute viewAttribute = childNode.getViewAttribute();
	    		
	    		ViewGroup.LayoutParams childViewParams = childView.getLayoutParams();
	        	childViewParams.width = viewAttribute.getWidth();
	        	childViewParams.height = viewAttribute.getHeight();
	        	
//	        	if(childView.equals(child)&&isFormer){
//	        		childViewParams.height = this.contentHeight;
//	        		childViewParams.width = this.contentWidth;	
//	        	}
	        	
	        	childView.setLayoutParams(childViewParams);
	        	if(childView instanceof TextView){
	        		float textSize = ((TextView) childView).getTextSize();
	        		((TextView) childView).setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttribute.getTextSize());
	        		
	        	}
	    		if(childView instanceof ViewGroup&&((ViewGroup) childView).getChildCount()>0){
	    			zoomInAndOuts(childView,rate,isFormer, childNode);
	    		}
	    	}
		}
    }
    
  //处理当ZoomLinearLayout控件大小发生改变
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	onResizeListener.onResize(w, h, oldw, oldh);
    }
    
    public void setOnResizeListener(OnResizeListener onResizeListener) {
		this.onResizeListener = onResizeListener;
	}

	public OnResizeListener getOnResizeListener() {
		return onResizeListener;
	}

	private OnResizeListener onResizeListener;
    
	/**
	 * 当该控件大小重新设置时回调接口
	 */
    public interface OnResizeListener{
    	void onResize(int w, int h, int oldw, int oldh);
    }
}














