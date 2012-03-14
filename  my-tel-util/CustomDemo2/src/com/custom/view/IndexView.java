package com.custom.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.custom.bean.ResourceBean;
import com.custom.utils.Logger;



public class IndexView extends FrameLayout{
	private static final Logger logger = Logger.getLogger(IndexView.class);
	private Context context = null;
	BackgroundLinearLayout scrollView = null;
	WebView mWebView = null;
	Stack<IndexImageSwfButton> buttonList = null;


	public enum BgType{
		pic,swf
	}
	public IndexView(Context context){
        super(context);
        this.context = context;
	}
	
	public IndexView(Context context, AttributeSet attr){
        super(context, attr);
        this.context = context;
        
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
	
	private void realeaseButton(){
		if(buttonList!=null&&buttonList.size()>0){
	    	//在程序退出(Activity销毁）时销毁悬浮窗口
			int length = buttonList.size();
    		for(int i=0;i<length;i++){
    			buttonList.peek().removeView();
    		}
	    	
		}
		buttonList = null;
	}

	public void onPause() {
		logger.error("onPause");
		if (mWebView != null) {
			mWebView.pauseTimers();
			callHiddenWebViewMethod("onPause");
		}
		if(buttonList!=null){
			realeaseButton();
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
	private void initView(){
		try {
			/**
			 * 查询资源信息
			 */
			queryRes();
			if(bgtype == BgType.pic){
				// 设置主界面布局
//				this.setLayoutParams(new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.FILL_PARENT,
//						LinearLayout.LayoutParams.FILL_PARENT));
//				this.setOrientation(LinearLayout.VERTICAL);

				scrollView = new BackgroundLinearLayout(this.context);
				scrollView.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.FILL_PARENT));
				this.addView(scrollView);
				/**
				 * 背景视图
				 */
				AssetManager assetManager = context.getAssets();
				InputStream in = assetManager.open(path+"bg.jpg");
				// BitmapDrawable backGroundDr = new BitmapDrawable(in);
				Bitmap bm = BitmapFactory.decodeStream(in);
				in.close();
				int with = bm.getWidth();
				int height = bm.getHeight();
				// 设置主布局
				AbsoluteLayout mLayout = new AbsoluteLayout(context);
				LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
						with, height);
				mLayout.setLayoutParams(mLayoutParams);
				mLayout.setBackgroundDrawable(new BitmapDrawable(bm));
				// 使背景获取焦点，焦点不要默认在输入框
				scrollView.setFocusable(true);
				scrollView.setFocusableInTouchMode(true);
				scrollView.addView(mLayout);
				Iterator it = resourceInfo.keySet().iterator();
				while(it.hasNext()){
					ResourceBean resourceBean = resourceInfo.get(it.next());
					IndexImagePicButton imageView = new IndexImagePicButton(context,scrollView,resourceBean);
					mLayout.addView(imageView);
					
				}
			}else if(bgtype == BgType.swf){
				buttonList = new Stack<IndexImageSwfButton>();
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
								// Thread.sleep(5000);
								 //popAwindow(webView);
							} catch (Exception e) {
							}
						}
					});
					mWebView.loadUrl("file:///android_asset/index.htm");
					this.addView(mWebView);
				}
				
				Iterator it = resourceInfo.keySet().iterator();

				while(it.hasNext()){
					ResourceBean resourceBean = resourceInfo.get(it.next());
					IndexImageSwfButton imageView = new IndexImageSwfButton(context,resourceBean);
					buttonList.push(imageView);
					break;
				}
			}


		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	String path = "custom/yuwen/";
	String bgPicName = "bg";
	HashMap<String,String> btnInfo = null;
	HashMap<String,ResourceBean> resourceInfo = null;
	HashMap<String,String> picType= null;
	HashMap<String,String> swfType= null;
	BgType bgtype = BgType.pic;
	
	public void queryRes(){
		btnInfo = new HashMap<String,String>();
		resourceInfo = new HashMap<String,ResourceBean>();
		picType = new HashMap<String,String>();
		swfType = new HashMap<String,String>();
		picType.put("JPG", "");
		picType.put("jpg", "");
		picType.put("GIF", "");
		picType.put("gif", "");
		picType.put("PNG", "");
		picType.put("png", "");
		picType.put("JEPG", "");
		picType.put("jepg", "");
		
		swfType.put("swf", "");
		swfType.put("SWF", "");
		
		
		try{
			AssetManager assetManager = context.getAssets();
			BufferedReader  fin = new BufferedReader(new InputStreamReader(assetManager.open(path+"map.txt")));
			String line = fin.readLine();
			while(line!=null){
				logger.error(line+":"+line.substring(0,line.indexOf("="))+":"+line.substring(line.indexOf("=")+1));
				if(line.indexOf("=")>0){
					btnInfo.put(line.substring(0,line.indexOf("=")), line.substring(line.indexOf("=")+1));
				}
				line = fin.readLine();
			}
			
			String[] lists = assetManager.list("custom/yuwen");
			for(int i=0;i<lists.length;i++){
				logger.error(lists[i]);
				if(lists[i].startsWith(bgPicName)){
					//是背景图片
					if(picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						bgtype = BgType.pic;
					}else if(swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						bgtype = BgType.swf;
					}	
					bgtype = BgType.swf;
				}else{
					ResourceBean res = null;
					
					if(lists[i].indexOf(".")<0){
						if(resourceInfo.containsKey(lists[i])){
							res = resourceInfo.get(lists[i]);
						}else{
							res = new ResourceBean();
						}
						
						res.setResourcePath(path+lists[i]);
						res.setType(ResourceBean.ResourceType.fold);
						resourceInfo.put(lists[i], res);
						continue;
					}
					String btnName = lists[i].substring(0,lists[i].indexOf("."));
					logger.error(btnName);
					if(picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						if(resourceInfo.containsKey(btnName)){
							res = resourceInfo.get(btnName);
						}else{
							res = new ResourceBean();
						}	
						res.setBtnPic(path+lists[i]);
						resourceInfo.put(btnName, res);
					}else if(swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						if(resourceInfo.containsKey(btnName)){
							res = resourceInfo.get(btnName);
						}else{
							res = new ResourceBean();
						}	
						res.setType(ResourceBean.ResourceType.swf);
						res.setResourcePath(path+lists[i]);
						resourceInfo.put(btnName, res);
					}else if("apk".equals(lists[i].substring(lists[i].indexOf(".")+1))){
						if(resourceInfo.containsKey(btnName)){
							res = resourceInfo.get(btnName);
						}else{
							res = new ResourceBean();
						}	
						res.setType(ResourceBean.ResourceType.apk);
						res.setResourcePath(path+lists[i]);
						resourceInfo.put(btnName, res);
					}
					if(resourceInfo.get(btnName)!=null){
						logger.error( btnName);
						resourceInfo.get(btnName).setName(btnInfo.get(btnName));
					}
					
					
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	


}
