package com.swftest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

public class TestActivity extends Activity {

	private WebView mWebView;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main1);

		mWebView = (WebView) findViewById(R.id.webview);

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
				} catch (Exception e) {
				}
			}
		});
		mWebView.loadUrl("file:///android_asset/index.htm");

	}


	/**
	 * 
	 * �������ص�WebView���� <br />
	 * 
	 * ˵����WebView��ȫ�˳�swf�ķ�����ֹͣ�����Ĳ��š�
	 * 
	 * @param name
	 */

	private void callHiddenWebViewMethod(String name) {

		if (mWebView != null) {
			try {
				Log.e("callHiddenWebViewMethod", "callHiddenWebViewMethod");
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView); // ����

			} catch (NoSuchMethodException e) { // û�������ķ���

				Log.i("No such method: " + name, e.toString());

			} catch (IllegalAccessException e) { // �Ƿ�����

				Log.i("Illegal Access: " + name, e.toString());

			} catch (InvocationTargetException e) { // ���õ�Ŀ���쳣

				Log.d("Invocation Target Exception: " + name, e.toString());

			}

		}

	}

	@Override
	protected void onPause() {

		super.onPause();

		mWebView.pauseTimers();

		if (isFinishing()) {

			mWebView.loadUrl("about:blank");

			setContentView(new FrameLayout(this));

		}

		callHiddenWebViewMethod("onPause");

	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.resumeTimers();
		callHiddenWebViewMethod("onResume");
	}
}

