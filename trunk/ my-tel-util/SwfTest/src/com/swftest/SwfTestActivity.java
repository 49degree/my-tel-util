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

public class SwfTestActivity extends Activity {

	private WebView mWebView;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

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
					// Thread.sleep(5000);
					 //popAwindow(webView);
				} catch (Exception e) {
				}
			}
		});
		mWebView.loadUrl("file:///android_asset/index.htm");
		
		ListView list = (ListView) this.findViewById(R.id.MyListView);
		initListView(list);
	}
	
	
	public void initListView(ListView list){
		// 绑定XML中的ListView，作为Item的容器
		//ListView list = (ListView) v.findViewById(R.id.MyListView);
		// 生成动态数组，并且转载数据
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 30; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemTitle", "This is Title.....");
			map.put("ItemText", "This is text.....");
			mylist.add(map);
		}

		// 生成适配器，数组===》ListItem
		SimpleAdapter mSchedule = new SimpleAdapter(this, // 没什么解释
				mylist,// 数据来源
				R.layout.my_listitem,// ListItem的XML实现
				// 动态数组与ListItem对应的子项
				new String[] { "ItemTitle", "ItemText" },
				// ListItem的XML文件里面的两个TextView ID
				new int[] { R.id.ItemTitle, R.id.ItemText });

		// 添加并且显示
		list.setAdapter(mSchedule);
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

	PopupWindow window;

	private void popAwindow(View parent) {
		// 取得当前运行环境的屏幕大小
		Object obj = getSystemService(Context.WINDOW_SERVICE);
		WindowManager wm = (WindowManager) obj;
		int envWidth = wm.getDefaultDisplay().getWidth();
		int envHeight = wm.getDefaultDisplay().getHeight();

		if (window == null) {
			LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = lay.inflate(R.layout.popupwindow, null);
			v.setBackgroundDrawable(null);// 使背景透明
			window = new PopupWindow(v);
			window.setWidth(LayoutParams.FILL_PARENT);
			window.setHeight(LayoutParams.FILL_PARENT);
			
			ListView list = (ListView) v.findViewById(R.id.MyListView);
			initListView(list);
		}
		


		// 设置整个popupwindow的样式。
		// window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));
		// 使窗口里面的空间显示其相应的效果，比较点击button时背景颜色改变。
		// 如果为false点击相关的空间表面上没有反应，但事件是可以监听到的。
		// listview的话就没有了作用。
		window.setFocusable(true);
		window.update();
		window.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
		
		

	}
}
