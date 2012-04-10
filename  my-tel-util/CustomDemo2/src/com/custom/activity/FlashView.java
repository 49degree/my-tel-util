package com.custom.activity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.custom.utils.Constant;
import com.custom.utils.Constant.DirType;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.view.R;

public class FlashView extends Activity {
	private static final Logger logger = Logger.getLogger(FlashView.class);
	private WebView mWebView = null;
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

//		if(!f.exists()){
			LoadResources.saveToTempFile(this, Constant.swfView2, DirType.assets,Constant.swfView2,false);
//		}else{
//			logger.error("f.exists()");
//		}
		mWebView.loadUrl("file://"+this.getFilesDir().getAbsolutePath()+File.separator+Constant.swfView2);
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
		super.onDestroy();
	}	

}
