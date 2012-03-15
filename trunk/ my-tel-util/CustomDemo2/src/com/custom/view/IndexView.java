package com.custom.view;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.custom.bean.ResourceBean;
import com.custom.utils.Constant;
import com.custom.utils.Logger;
import com.custom.utils.MondifyIndexImageIndex;
import com.custom.utils.ScanFoldUtils;



public class IndexView extends FrameLayout{
	private static final Logger logger = Logger.getLogger(IndexView.class);
	private Context context = null;
	BackgroundLinearLayout scrollView = null;
	AbsoluteLayout mLayout = null;
	WebView mWebView = null;
	String foldPath = null;
	private WindowManager.LayoutParams wmParams =null;
	private WindowManager wm = null;
	int foldDepth = Constant.fistFoldDepth;

	public IndexView(Context context,String foldPath,int foldDepth){
        super(context);
        this.context = context;
        this.foldPath = foldPath; 
        this.foldDepth = foldDepth;
	}
	
	public IndexView(Context context, AttributeSet attr,String foldPath,int foldDepth){
        super(context, attr);
        this.context = context;
        this.foldPath = foldPath; 
        this.foldDepth = foldDepth;
        
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

	public void onResume() {
		logger.error("onResume");
		if (mWebView != null) {
			mWebView.resumeTimers();
			callHiddenWebViewMethod("onResume");
		}
		initView();
	}

	
	/**
	 * 构建界面
	 */
	ScanFoldUtils scanFoldUtils = null;
	private void initView(){
		try {
			/**
			 * 查询资源信息
			 */
			scanFoldUtils = new ScanFoldUtils(context,foldPath,foldDepth);
			MondifyIndexImageIndex.initImageIndexs(context);//获取按钮位置信息
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
				AssetManager assetManager = context.getAssets();
				logger.error(scanFoldUtils.bgPic);
				InputStream in = assetManager.open(scanFoldUtils.bgPic);
				// BitmapDrawable backGroundDr = new BitmapDrawable(in);
				Bitmap bm = BitmapFactory.decodeStream(in);
				in.close();
				int with = bm.getWidth();
				int height = bm.getHeight();
				
				WindowManager manage = ((Activity)context).getWindowManager();
				Display display = manage.getDefaultDisplay();
				int screenHeight = display.getHeight();
				int screenWidth = display.getWidth();
				
				logger.error("with:"+with+":height:"+height+":screenHeight:"+screenHeight+":screenWidth:"+screenWidth);
				if(with<screenWidth){
					if(height<screenHeight&&with*1.0f/screenWidth>height*1.0f/screenHeight){
						height = screenHeight;
						with = (int)(with*height*1.0f/screenWidth);
					}else{
						with = screenWidth;
						height = (int)(with*height*1.0f/screenHeight);
					}
				}else if(height<screenHeight){
					if(with<screenWidth&&with*1.0f/screenWidth>height*1.0f/screenHeight){
						height = screenHeight;
						with = (int)(with*height*1.0f/screenWidth);
					}else{
						with = screenWidth;
						height = (int)(with*height*1.0f/screenHeight);
					}
				}
				
				
				// 设置主布局
				mLayout = new AbsoluteLayout(context);
				LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
						with, height);
				mLayout.setLayoutParams(mLayoutParams);
				mLayout.setBackgroundDrawable(new BitmapDrawable(bm));
				// 使背景获取焦点，焦点不要默认在输入框
				scrollView.setFocusable(true);
				scrollView.setFocusableInTouchMode(true);
				scrollView.addView(mLayout);
				Iterator it = scanFoldUtils.resourceInfo.keySet().iterator();
				while(it.hasNext()){
					ResourceBean resourceBean = scanFoldUtils.resourceInfo.get(it.next());
					IndexImagePicButton imageView = new IndexImagePicButton(context,scrollView,resourceBean);
					mLayout.addView(imageView);
					
				}
			}else if(scanFoldUtils.bgtype == Constant.BgType.swf){
				if(mWebView==null){
					// 设置主界面布局
					mWebView = new WebView(context); //网页
				    mWebView.setHorizontalScrollBarEnabled(false);
				    mWebView.setVerticalScrollBarEnabled(false);
					mWebView.getSettings().setJavaScriptEnabled(true);
					mWebView.getSettings().setPluginsEnabled(true);
					
					mWebView.setWebViewClient(new WebViewClient() {
						public boolean shouldOverrideUrlLoading(WebView view, String url) {
							view.loadUrl(url);
							Log.e("shouldOverrideUrlLoading", "shouldOverrideUrlLoading");
							return true;
						}
						@Override
						public void onPageFinished(final WebView webView, String url) {
							try {
								mWebView.loadUrl("javascript:showgame('"+scanFoldUtils.bgPic+"')");
							} catch (Exception e) {
							}
						}
					});
					mWebView.loadUrl(Constant.swfView);
					this.addView(mWebView);
				}
				
				
				// 设置主布局
				mLayout = new AbsoluteLayout(context);
				//mLayout.setBackgroundColor(Color.RED);
				createView(mLayout);
				
				Iterator it = scanFoldUtils.resourceInfo.keySet().iterator();
				while(it.hasNext()){
					ResourceBean resourceBean = scanFoldUtils.resourceInfo.get(it.next());
					IndexImageSwfButton imageView = new IndexImageSwfButton(context,mLayout,resourceBean);
					mLayout.addView(imageView);
					
				}
			}


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


	


}
