package com.custom.view;

import java.util.ArrayList;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.custom.bean.PageNumBean;
import com.custom.bean.ResourceBean;
import com.custom.utils.Constant.DirType;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.ScanFoldUtils;
import com.custom.view.PageNumView.UnitPageOnclick;

public class SecondViewGroup extends LinearLayout {
	private static final Logger logger = Logger.getLogger(SecondViewGroup.class);
	
	private ArrayList<Entry<String,ResourceBean>> resourceInfo = null;
	private PageNumView pageNumView = null;
	private Context context = null;
	private SecondViewPage secondViewPage = null;
	private PageNumBean pageNumBean=null;
	private int currentScreenIndex = 0;
	private boolean onFling = true;
	private int canotFling = 0;
	private ProgressDialog progress;
	
	public SecondViewGroup(Context context,ArrayList<Entry<String,ResourceBean>> resourceInfo,PageNumView pageNumView) {
		super(context);
		this.context = context;
		this.resourceInfo = resourceInfo;
		this.pageNumView = pageNumView;
		this.pageNumBean = pageNumView.getPageNumBean();
		
		this.pageNumView.setUnitPageOnclick(new UnitPageOnclick(){
			public void upUnitOnclick(){
				if(pageNumBean.prePageView()){
					currentScreenIndex = 0;
					initView();
				}
			}
			public void nextUnitOnclick(){
				if(pageNumBean.nextPageView()){
					currentScreenIndex = 0;
					initView();
				}
			}
		});
		
		initView();
	}
	
	private void initView() {
		if(secondViewPage!=null){
			this.removeView(secondViewPage);
			progress = ProgressDialog.show(context, "请稍候", "正在加载资源....");
		}
		new LoadResAsyncTask().execute(0);
		
	}
	
    /**
     * 回调更新界面
     */
    private class LoadResAsyncTask extends AsyncTask<Integer, Integer, Integer>{

    	@Override
    	protected void onPreExecute() {  
    		// 任务启动，可以在这里显示一个对话框，这里简单处
    	}   
        
    	@Override
    	
        protected Integer doInBackground(Integer... values) {
            // TODO Auto-generated method stub

            return values[0];
        }
    	
    	@Override
    	protected void onProgressUpdate(Integer... values) {
    		//参数对应<ScanFoldUtils, String, ScanFoldUtils>第二个
    		
    	} 

        @Override
        protected void onPostExecute(Integer values) {
        	//参数对应doInBackground返回值，也是<ScanFoldUtils, String, ScanFoldUtils>第3个
        	
    		currentScreenIndex = pageNumBean.getCurPageNum()%pageNumBean.getPageNumPerView();
    		pageNumView.initPageNumView();
    		secondViewPage = new SecondViewPage(context,resourceInfo,pageNumView);
    		secondViewPage.post(new Runnable() {
    			@Override
    			public void run() { 
    				final int delta = currentScreenIndex * getWidth();
    				secondViewPage.scrollTo(delta, 0); 
    				onFling = true;
    			}   
    		});
    		addView(secondViewPage);
			if(progress!=null)
				progress.cancel();
        }
        @Override
        protected void onCancelled(){
        	
        }
    }
    
	public class SecondViewPage extends ViewGroup{
		private Scroller scroller;
		
		private GestureDetector gestureDetector;
		private Context context = null;
		int screenHeight = 0;
		int screenWidth = 0;
		// 设置一个标志位，防止底层的onTouch事件重复处理UP事件
		private boolean fling;
		public Scroller getScroller() {
			return scroller;
		}
		public SecondViewPage(Context context,ArrayList<Entry<String,ResourceBean>> resourceInfo,PageNumView pageNumView) {
			super(context);
			this.context = context;
			
			WindowManager manage = ((Activity)context).getWindowManager();
			Display display = manage.getDefaultDisplay();
			screenHeight = display.getHeight();
			screenWidth = display.getWidth();
			initView();
			createIndexButton();
		}

		private void initView() {
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
					logger.error("onScroll:"+distanceX+":"+getScrollX());
					if(distanceX > 0 && currentScreenIndex == getChildCount() - 1){//移动过最后一页
						if(onFling&&pageNumBean.nextPageView()){
							SecondViewGroup.this.initView();
							onFling = false;
							logger.error( "on scroll>>>>>>>>>>>>>>>>>向后移动<<<<<<<<<<<<<<>>>");
						}
						logger.error( "on scroll22>>>>>>>>>>>>>>>>>向后移动<<<<<<<<<<<<<<>>>");
						
					}else if(distanceX < 0 && getScrollX() <= 0){//向第一页之前移动
						logger.error( "on scrol11l>>>>>>>>>>>>>>>>>向前移动<<<<<<<<<<<<<<>>>");

						if(onFling&&pageNumBean.prePageView()){
							logger.error( "on scroll22>>>>>>>>>>>>>>>>>向前移动<<<<<<<<<<<<<<>>>");
							pageNumBean.setCurPageNum(pageNumBean.getEndPageNum());
							SecondViewGroup.this.initView();
							onFling = false;
							//pageNumView.initPageNumView();
							logger.error( "on scroll33>>>>>>>>>>>>>>>>>向前移动<<<<<<<<<<<<<<>>>");
						}
					}else if ((distanceX > 0 && currentScreenIndex < getChildCount() - 1)// 防止移动过最后一页
							|| (distanceX < 0 && getScrollX() > 0)) {// 防止向第一页之前移动
						logger.error( "on scroll11>>>>>>>>>>>>>>>>>防止向第一页之前移动<<<<<<<<<<<<<<>>>++++++++:"+canotFling);
						scrollBy((int) distanceX, 0);
						onFling = true;
					}
					
					return false;
				}

				@Override
				public void onLongPress(MotionEvent e) {
				}

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					logger.error( "min velocity >>>"
							+ ViewConfiguration.get(context)
									.getScaledMinimumFlingVelocity()
							+ " current velocity>>" + velocityX);

					if (Math.abs(velocityX) > ViewConfiguration.get(context)
							.getScaledMinimumFlingVelocity()) {// 判断是否达到最小轻松速度，取绝对值的
						if (velocityX > 0 && currentScreenIndex > 0) {
							logger.error( ">>>>fling to left");
							fling = true;
							scrollToScreen(currentScreenIndex - 1);
						} else if (velocityX < 0
								&& currentScreenIndex < getChildCount() - 1) {
							logger.error( ">>>>fling to right");
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
			
			
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			logger.error( ">>>>>>>>>>>>>>>>>>>>left: " + left + " top: " + top + " right: " + right
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
				//logger.error( ">>>>>>>>>>computeScroll>>>>>"+scroller.getCurrX());

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
				//logger.error( ">>ACTION_UP:>>>>>>>> MotionEvent.ACTION_UP>>>>>");
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
			if(!onFling)
				return;
			if (getFocusedChild() != null && whichScreen != currentScreenIndex
					&& getFocusedChild() == getChildAt(currentScreenIndex)) {
				getFocusedChild().clearFocus();
			}

			final int delta = whichScreen * getWidth() - getScrollX();
			logger.error("scrollToScreen:"+whichScreen+":delta:"+delta);
			if(Math.abs(delta)>screenWidth){
				return;
			}
			scroller.startScroll(getScrollX(), 0, delta, 0, 200);
			invalidate();
			if(currentScreenIndex>whichScreen&&pageNumBean.prePageNum()){
				pageNumView.initPageNumView();
			}else if(currentScreenIndex<whichScreen&&pageNumBean.nextPageNum()){
				pageNumView.initPageNumView();	
			}
			logger.error("scrollToScreen11:"+getScrollX()+":"+delta);
		
			currentScreenIndex = whichScreen;
		}

		/**
		 * 根据当前x坐标位置确定切换到第几屏
		 */
		private void snapToDestination() {
			scrollToScreen((getScrollX() + (getWidth() / 2)) / getWidth());
		}
		protected void createIndexButton() {
			//this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT) );
			for(int pageNum=pageNumBean.getStartPageNum();pageNum<=pageNumBean.getEndPageNum();pageNum++){
				LinearLayout.LayoutParams pageLayoutParams = new LinearLayout.LayoutParams(
						screenWidth, screenHeight);
				AbsoluteLayout pageLayout = new AbsoluteLayout(context);
				pageLayout.setLayoutParams(pageLayoutParams);
				this.addView(pageLayout);
				int[] index = pageNumBean.getButtonIndexbyPageNum(pageNum);
				//logger.error("index:"+Arrays.toString(index)+":"+pageNumBean.getButtonCount());
				
				for(int i=index[0];i<=index[1];i++){
					ResourceBean resourceBean = resourceInfo.get(i).getValue();
					//加载按钮图片
					try{
						if(resourceBean.getBm()==null)
							resourceBean.setBm(LoadResources.loadBitmap(context, resourceBean.getBtnPic(), resourceBean.getDirType()));
					}catch(Exception e){
						
					}

					IndexImageButtonImp imageView = null;
					setXY(resourceBean,i);
					imageView = new IndexImagePicButton(context,null,resourceBean,true);
					pageLayout.addView(imageView);
				}
			}
		}
		protected void setXY(ResourceBean resourceBean,int buttonIndex) {
			//设置图标的位置
			// TODO Auto-generated method stub
			//int[] indexs = MondifyIndexImageIndex.getImageIndexs(resourceBean.getBtnKey());
//			int[] viewXY = new int[2];
//			viewXY[0] = screenWidth>screenHeight?screenWidth:screenHeight;
//			viewXY[1] = screenWidth>screenHeight?screenHeight:screenWidth;
//			int width = viewXY[0]-250;
//			int height = viewXY[1]-100;
			resourceBean.setX(buttonIndex%4*200+50);
			resourceBean.setY(buttonIndex/4%2*200+150);
		}
		
	}
	

}
