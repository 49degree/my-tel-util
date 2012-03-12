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
		// ��XML�е�ListView����ΪItem������
		//ListView list = (ListView) v.findViewById(R.id.MyListView);
		// ���ɶ�̬���飬����ת������
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 30; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemTitle", "This is Title.....");
			map.put("ItemText", "This is text.....");
			mylist.add(map);
		}

		// ����������������===��ListItem
		SimpleAdapter mSchedule = new SimpleAdapter(this, // ûʲô����
				mylist,// ������Դ
				R.layout.my_listitem,// ListItem��XMLʵ��
				// ��̬������ListItem��Ӧ������
				new String[] { "ItemTitle", "ItemText" },
				// ListItem��XML�ļ����������TextView ID
				new int[] { R.id.ItemTitle, R.id.ItemText });

		// ��Ӳ�����ʾ
		list.setAdapter(mSchedule);
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

	PopupWindow window;

	private void popAwindow(View parent) {
		// ȡ�õ�ǰ���л�������Ļ��С
		Object obj = getSystemService(Context.WINDOW_SERVICE);
		WindowManager wm = (WindowManager) obj;
		int envWidth = wm.getDefaultDisplay().getWidth();
		int envHeight = wm.getDefaultDisplay().getHeight();

		if (window == null) {
			LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = lay.inflate(R.layout.popupwindow, null);
			v.setBackgroundDrawable(null);// ʹ����͸��
			window = new PopupWindow(v);
			window.setWidth(LayoutParams.FILL_PARENT);
			window.setHeight(LayoutParams.FILL_PARENT);
			
			ListView list = (ListView) v.findViewById(R.id.MyListView);
			initListView(list);
		}
		


		// ��������popupwindow����ʽ��
		// window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));
		// ʹ��������Ŀռ���ʾ����Ӧ��Ч�����Ƚϵ��buttonʱ������ɫ�ı䡣
		// ���Ϊfalse�����صĿռ������û�з�Ӧ�����¼��ǿ��Լ������ġ�
		// listview�Ļ���û�������á�
		window.setFocusable(true);
		window.update();
		window.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
		
		

	}
}
