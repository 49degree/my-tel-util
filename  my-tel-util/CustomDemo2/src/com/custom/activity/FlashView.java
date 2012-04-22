package com.custom.activity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.custom.utils.Constant;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.Constant.DirType;
import com.custom.view.BackButton;
import com.custom.view.R;

public class FlashView extends Activity {
	private static final Logger logger = Logger.getLogger(FlashView.class);
	private WebView mWebView = null;
	protected WindowManager.LayoutParams wmParams =null;
	protected WindowManager wm = null;
	BackButton backButton = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏标题栏
		
		setContentView(R.layout.web_view);
		// 设置主界面布局
		mWebView = (WebView)this.findViewById(R.id.WebView01); //网页
		
	    mWebView.setHorizontalScrollBarEnabled(false);
	    mWebView.setVerticalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginsEnabled(true);
		
		Bundle bundle = this.getIntent().getExtras();
		final String foldPath = bundle.getString(Constant.foldPath);

		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				Log.e("shouldOverrideUrlLoading", "shouldOverrideUrlLoading");
				return true;
			}
			@Override
			public void onPageFinished(final WebView webView, String url) {
				try {
					mWebView.loadUrl("javascript:showgame('"+foldPath+"')");
				} catch (Exception e) {
				}
			}
		});
		
		File f = new File(this.getFilesDir().getAbsolutePath()+File.separator+Constant.swfView2);

		if(!f.exists()){
			LoadResources.saveToTempFile(this, Constant.swfView2, DirType.assets,Constant.swfView2,false);
		}else{
			logger.error("f.exists()");
		}
		mWebView.loadUrl("file://"+this.getFilesDir().getAbsolutePath()+File.separator+Constant.swfView2);
		
		backButton = new BackButton(this);
		backButton.setZoom(Constant.zoom);
		backButton.initView(this);

		createView(backButton);
		
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
	protected void createView(View view) {
		// 获取WindowManager
		wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		wmParams =new WindowManager.LayoutParams();
		/**
		 * 以下都是WindowManager.LayoutParams的相关属性 具体用途可参考SDK文档
		 */
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// 设置Window flag

		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。*/
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		 
		wmParams.gravity=Gravity.RIGHT|Gravity.BOTTOM; //调整悬浮窗口至左上角
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
		wmParams.width = 100;
		wmParams.height = 100;

		wm.addView(view, wmParams);
	}

	public void onResume() {
		super.onResume();
		logger.error("onResume");
		if (mWebView != null) {
			mWebView.resumeTimers();
			callHiddenWebViewMethod("onResume");
		}
	}
	
	public void onPause() {
		logger.error("onPause");
		if (mWebView != null) {
			mWebView.pauseTimers();
			callHiddenWebViewMethod("onPause");
		}
		super.onPause();
	}	
	
	public void onDestroy() {
		logger.error("onPause");
		if (mWebView != null) {
			mWebView.pauseTimers();
			callHiddenWebViewMethod("onDestroy");
		}
		wm.removeView(backButton);
		super.onDestroy();
	}	

}
