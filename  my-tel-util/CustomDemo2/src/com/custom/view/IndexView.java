package com.custom.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

import com.custom.bean.ResourceBean;



public class IndexView extends LinearLayout{
	private static final String TAG = "IndexView";
	private Context context = null;


	public IndexView(Context context){
        super(context);
        this.context = context;
        initView();
	}
	
	public IndexView(Context context, AttributeSet attr){
        super(context, attr);
        this.context = context;
        initView();
	}


	
	/**
	 * 构建界面
	 */
	private void initView(){
		try {
			// 设置主界面布局
			this.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));
			this.setOrientation(LinearLayout.VERTICAL);
			BackgroundLinearLayout scrollView = new BackgroundLinearLayout(this.context);
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

			/**
			 * 图片按钮
			 */
			queryRes();
			Iterator it = resourceInfo.keySet().iterator();
			while(it.hasNext()){
				ResourceBean resourceBean = resourceInfo.get(it.next());
				IndexImageButton imageView = new IndexImageButton(context,scrollView,resourceBean);
				mLayout.addView(imageView);
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
	HashMap<String,String> moveType= null;
	
	public void queryRes(){
		btnInfo = new HashMap<String,String>();
		resourceInfo = new HashMap<String,ResourceBean>();
		picType = new HashMap<String,String>();
		moveType = new HashMap<String,String>();
		picType.put("JPG", "");
		picType.put("jpg", "");
		picType.put("GIF", "");
		picType.put("gif", "");
		picType.put("PNG", "");
		picType.put("png", "");
		picType.put("JEPG", "");
		picType.put("jepg", "");
		
		moveType.put("swf", "");
		moveType.put("SWF", "");
		
		
		try{
			AssetManager assetManager = context.getAssets();
			BufferedReader  fin = new BufferedReader(new InputStreamReader(assetManager.open(path+"map.txt")));
			String line = fin.readLine();
			while(line!=null){
				Log.e(TAG, line+":"+line.substring(0,line.indexOf("="))+":"+line.substring(line.indexOf("=")+1));
				if(line.indexOf("=")>0){
					btnInfo.put(line.substring(0,line.indexOf("=")), line.substring(line.indexOf("=")+1));
				}
				line = fin.readLine();
			}
			
			String[] lists = assetManager.list("custom/yuwen");
			for(int i=0;i<lists.length;i++){
				Log.e(TAG, lists[i]);
				if(lists[i].startsWith(bgPicName)&&picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					//是背景图片
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
					String btnName = lists[i].substring(0,lists[i].indexOf(".")+1);
					Log.e(TAG, btnName);
					if(picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						if(resourceInfo.containsKey(btnName)){
							res = resourceInfo.get(btnName);
						}else{
							res = new ResourceBean();
						}	
						res.setBtnPic(path+lists[i]);
						resourceInfo.put(btnName, res);
					}else if(moveType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
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
					resourceInfo.get(btnName).setName(btnInfo.get(btnName));
					
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	


}
