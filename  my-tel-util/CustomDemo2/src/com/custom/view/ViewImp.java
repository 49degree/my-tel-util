package com.custom.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.custom.bean.ResourceBean;
import com.custom.utils.Constant;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.ScanFoldUtils;
import com.custom.utils.Constant.DirType;

public abstract class ViewImp extends FrameLayout{
	private static final Logger logger = Logger.getLogger(IndexView.class);
	protected Context context = null;
	protected BackgroundLinearLayout scrollView = null;
	protected AbsoluteLayout mLayout = null;
	protected WebView mWebView = null;
	protected String foldPath = null;
	protected WindowManager.LayoutParams wmParams =null;
	protected WindowManager wm = null;
	protected int foldDepth = Constant.fistFoldDepth;
	protected ScanFoldUtils scanFoldUtils = null;
	protected int screenHeight = 0;
	protected int screenWidth = 0;
	protected Bitmap bm = null;
	protected ProgressDialog progress = null; 

	public ViewImp(Context context,String foldPath,int foldDepth){
        this(context,null,foldPath,foldDepth);
	}
	
	public ViewImp(Context context, AttributeSet attr,String foldPath,int foldDepth){
        super(context, attr);
        this.context = context;
        this.foldPath = foldPath; 
        this.foldDepth = foldDepth;
        
		WindowManager manage = ((Activity)context).getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
	}

	public ViewImp(Context context, ScanFoldUtils scanFoldUtils){
        super(context);
        this.scanFoldUtils = scanFoldUtils;
        
		WindowManager manage = ((Activity)context).getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();

	}

	/**
	 * 
	 * 调用隐藏的WebView方法 <br />
	 * 
	 * 说明：WebView完全退出swf的方法，停止声音的播放。
	 * 
	 * @param name
	 */

	private void callHiddenWebViewMethod(String name) {

		if (mWebView != null) {

			try {
				Log.e("callHiddenWebViewMethod", "callHiddenWebViewMethod");
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView); // 调用

			} catch (NoSuchMethodException e) { // 没有这样的方法

				Log.i("No such method: " + name, e.toString());

			} catch (IllegalAccessException e) { // 非法访问

				Log.i("Illegal Access: " + name, e.toString());

			} catch (InvocationTargetException e) { // 调用的目标异常

				Log.d("Invocation Target Exception: " + name, e.toString());

			}

		}
	}

	protected boolean isRestart = false;
	public void onRestart(){
		isRestart = true;
	}
	
	public void onStart() {
		logger.error("onStart");
		if(!isRestart){
			progress = ProgressDialog.show(context, "请稍候", "正在加载资源....");
			initBackground();
			new LoadResAsyncTask().execute(scanFoldUtils);	
		}
	}
	
	public void onResume() {
		logger.error("onResume");
		if (mWebView != null) {
			mWebView.resumeTimers();
			callHiddenWebViewMethod("onResume");
		}
		if(isRestart&&scanFoldUtils.bgtype == Constant.BgType.swf&&wm!=null&&mLayout!=null){
			createView(mLayout);
		}
	}
	
	public void onPause() {
		logger.error("onPause");
		if (mWebView != null) {
			mWebView.pauseTimers();
			callHiddenWebViewMethod("onPause");
		}
		if(scanFoldUtils.bgtype == Constant.BgType.swf&&wm!=null&&mLayout!=null){
			wm.removeView(mLayout);
		}

	}
	public void onStop(){
		logger.error("onStop");

	}
	
	public void onDestroy(){
		logger.equals("onDestroy");
		if(bm!=null&&!bm.isRecycled()){
			//logger.error("onDestroy:"+bm.hashCode());
			bm.recycle();
		}
		bm = null;
		if(scanFoldUtils.resourceInfo!=null){
			Iterator it = scanFoldUtils.resourceInfo.keySet().iterator();
			while(it.hasNext()){
				ResourceBean resourceBean = scanFoldUtils.resourceInfo.get(it.next());
				
				if(resourceBean.getBm()!=null&&!resourceBean.getBm().isRecycled()){
					//logger.error("onDestroy resourceBean:"+resourceBean.getBm().hashCode());
					resourceBean.getBm().recycle();
				}
			}
		}
		scanFoldUtils = null;	
	}	
	
    private class LoadResAsyncTask extends AsyncTask<ScanFoldUtils, Integer, ScanFoldUtils>{

    	@Override
    	protected void onPreExecute() {  
    		// 任务启动，可以在这里显示一个对话框，这里简单处
    	}   
        
    	@Override
    	
        protected ScanFoldUtils doInBackground(ScanFoldUtils... scanFoldUtils) {
            // TODO Auto-generated method stub
    		//参数对应<ScanFoldUtils, String, ScanFoldUtils>第1个,返回值对应第3个
            if(scanFoldUtils[0]!=null&&scanFoldUtils[0].resourceInfo==null){
            	scanFoldUtils[0].queryRes();
            }
            return scanFoldUtils[0];
        }
    	
    	@Override
    	protected void onProgressUpdate(Integer... values) {
    		//参数对应<ScanFoldUtils, String, ScanFoldUtils>第二个
    		
    	} 

        @Override
        protected void onPostExecute(ScanFoldUtils scanFoldUtils) {
        	//参数对应doInBackground返回值，也是<ScanFoldUtils, String, ScanFoldUtils>第3个
			initView();
			if(progress!=null)
				progress.dismiss();
        }
        @Override
        protected void onCancelled(){
        	
        }
    }

	/**
	 * 构建界面
	 */
	
	public void initView(){
		try{
			if(scanFoldUtils.resourceInfo==null||scanFoldUtils.resourceInfo.size()<1){
				TextView text = new TextView(context);
				text.setBackgroundColor(Color.RED);
				text.setText("未找到资源文件");
				LinearLayout.LayoutParams tlayout = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
				tlayout.gravity = Gravity.CENTER;
				text.setLayoutParams(tlayout);
				
				this.addView(text);
				return;
			}
			logger.error("createIndexButton");
			this.createIndexButton();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	protected void initBackground(){
		try {
			/**
			 * 查询资源信息
			 */
			if(scanFoldUtils==null){
				scanFoldUtils = new ScanFoldUtils(context,foldPath,foldDepth);
			}
			logger.error("logger.error(scanFoldUtils.bgPic);"+scanFoldUtils.bgPic);
			if(scanFoldUtils.bgPic==null) 
				return ;
			if(scanFoldUtils.bgtype == Constant.BgType.pic){
				// 设置主界面布局
				scrollView = new BackgroundLinearLayout(this.context);
				scrollView.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.FILL_PARENT));
				this.addView(scrollView);
				/**
				 * 背景视图
				 */
				if(bm==null){
					bm = LoadResources.loadBitmap(context, scanFoldUtils.bgPic, scanFoldUtils.bgDirtype);	
				}
				int[] viewXY = calBackGroudView(bm);
				// 设置主布局
				mLayout = new AbsoluteLayout(context);
				LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
						viewXY[0], viewXY[1]);
				mLayout.setLayoutParams(mLayoutParams);
				mLayout.setBackgroundDrawable(new BitmapDrawable(bm));
				
				// 使背景获取焦点，焦点不要默认在输入框
				scrollView.setFocusable(true);
				scrollView.setFocusableInTouchMode(true);
				scrollView.addView(mLayout);
				

			}else if(scanFoldUtils.bgtype == Constant.BgType.swf){
				if(mWebView==null){
					// 设置主界面布局
					mWebView = new WebView(context); //网页
				    mWebView.setHorizontalScrollBarEnabled(false);
				    mWebView.setVerticalScrollBarEnabled(false);
					mWebView.getSettings().setJavaScriptEnabled(true);
					mWebView.getSettings().setPluginsEnabled(true);
					
					
					try{
						//复制文件
						LoadResources.saveToTempFile(context, scanFoldUtils.bgPic, scanFoldUtils.bgDirtype, Constant.backGroundSwfName);
					}catch(Exception e){
						e.printStackTrace();
					}
					mWebView.setWebViewClient(new WebViewClient() {
						public boolean shouldOverrideUrlLoading(WebView view, String url) {
							view.loadUrl(url);
							Log.e("shouldOverrideUrlLoading", "shouldOverrideUrlLoading");
							return true;
						}
						@Override
						public void onPageFinished(final WebView webView, String url) {
							try {
								mWebView.loadUrl("javascript:showgame('"+context.getFilesDir()+File.separator+Constant.backGroundSwfName+"')");
							} catch (Exception e) {
							}
						}
					});
					

					
					mWebView.loadUrl(Constant.swfView);
					this.addView(mWebView);
				}
				// 设置主布局
				mLayout = new AbsoluteLayout(context);
				createView(mLayout);
			}
			logger.error("background end");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void createView(View view) {
		// 获取WindowManager
		wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wmParams =new WindowManager.LayoutParams();
		/**
		 * 以下都是WindowManager.LayoutParams的相关属性 具体用途可参考SDK文档
		 */
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// 设置Window flag
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */
		// wmParams.gravity=Gravity.LEFT|Gravity.TOP; //调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值

//		int[] indexs = MondifyIndexImageIndex.getImageIndexs(resourceBean.getBtnKey());
//		if(indexs==null||indexs.length<2){
//			wmParams.x = 100;
//			wmParams.y = 100;
//		}else{
//			wmParams.x = indexs[0];
//			wmParams.y = indexs[1];
//			logger.error(indexs[0]+":"+indexs[1]);
//		}

		
		// 设置悬浮窗口长宽数据
//		wmParams.width = 300;
//		wmParams.height = 300;
		// 显示myFloatView图像
		wm.addView(view, wmParams);
	}

	protected int[] calBackGroudView(Bitmap bm){
		int[] viewXY = new int[2];
		viewXY[0] = screenWidth>screenHeight?screenWidth:screenHeight;
		viewXY[1] = screenWidth>screenHeight?screenHeight:screenWidth;
		return viewXY;
		
	}
	protected abstract void setXY(ResourceBean resourceBean);
	protected abstract void createIndexButton() ;

}

