package com.custom.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.custom.bean.PageNumBean;
import com.custom.bean.ResourceBean;
import com.custom.utils.Constant;
import com.custom.utils.Logger;
import com.custom.utils.SharedPreferencesUtils;
import com.custom.view.PageNumView.UnitPageOnclick;





public class SecondView extends ViewImp{
	private static final Logger logger = Logger.getLogger(SecondView.class);
	ArrayList<Entry<String,ResourceBean>> resourceInfo = null;
	int screenHeight = 0;
	int screenWidth = 0;
	int curPageNum = 0;
	PageNumBean pageNumBean = null;
	public SecondView(Context context,String foldPath,int foldDepth){
		super(context,foldPath,foldDepth);
		WindowManager manage = ((Activity)context).getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
	}
	
	public SecondView(Context context, AttributeSet attr,String foldPath,int foldDepth){
        super(context, attr,foldPath,foldDepth);
		WindowManager manage = ((Activity)context).getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
	}

	@Override
	protected void createIndexButton() {
		//对资源进行排序
		resourceInfo = new ArrayList<Entry<String,ResourceBean>>(scanFoldUtils.resourceInfo.entrySet());     
		Collections.sort(resourceInfo,new Comparator<Map.Entry<String, ResourceBean>>() {
					@Override
					public int compare(Entry<String, ResourceBean> object1,
							Entry<String, ResourceBean> object2) {
						// TODO Auto-generated method stub
						return object1.getKey().compareTo(object2.getKey());
					}
				}); 
//		Entry<String,ResourceBean> data=  resourceInfo.get(0);
//		Entry<String,ResourceBean> data2=  resourceInfo.get(1);
//		resourceInfo.removeAll(resourceInfo);
//		for(int i=0;i<32;i++){
//			resourceInfo.add(data);
//		}
//		
//		for(int i=0;i<12;i++){
//			resourceInfo.add(data2);
//		}
		
		pageNumBean = new PageNumBean(resourceInfo.size());
		pageNumBean.setCurPageNum(curPageNum);
		
		FrameLayout frameLayout = new FrameLayout(this.context);
		LinearLayout.LayoutParams frameLayoutParams = new LinearLayout.LayoutParams(
				screenWidth, screenHeight);
		frameLayout.setLayoutParams(frameLayoutParams);
		mLayout.addView(frameLayout);
		
		PageNumView pageNumView = new PageNumView(this.context,pageNumBean);
		pageNumView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT) );
		
		SecondViewGroup viewGroup = new SecondViewGroup(this.context,resourceInfo,pageNumView);
		frameLayout.addView(viewGroup);
		frameLayout.addView(pageNumView);
		pageNumView.initView();
		
		

	}
	@Override
	protected void setXY(ResourceBean resourceBean) {
	}
	@Override
	protected int[] calBackGroudView(Bitmap bm){
		int[] viewXY = new int[2];
		viewXY[0] = screenWidth;
		viewXY[1] = screenHeight;
		return viewXY;
		
	}
	
	@Override
	public void onPause() {
		SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
				SharedPreferencesUtils.CURPAGENUM, String.valueOf(this.pageNumBean.getCurPageNum()));
		super.onPause();

	}
	@Override
	public void onResume() {
		try{
			curPageNum = Integer.parseInt(SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO, 
					SharedPreferencesUtils.CURPAGENUM));
		}catch(Exception e){
			curPageNum = 0;
		}

		super.onResume();
	}
	
}
