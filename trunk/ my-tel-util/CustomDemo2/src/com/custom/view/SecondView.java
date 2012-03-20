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
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

import com.custom.bean.ResourceBean;
import com.custom.utils.Logger;
import com.custom.utils.Constant.BgType;





public class SecondView extends ViewImp{
	private static final Logger logger = Logger.getLogger(SecondView.class);
	ArrayList<Entry<String,ResourceBean>> resourceInfo = null;
	int screenHeight = 0;
	int screenWidth = 0;
	int pageNum = 1;
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

		SecondViewGroup viewGroup = new SecondViewGroup(this.context);
		mLayout.addView(viewGroup);
		viewGroup.createIndexButton(resourceInfo);
		
//		for(int j=0;j<5;j++){
//			AbsoluteLayout pageLayout = new AbsoluteLayout(context);
//			LinearLayout.LayoutParams pageLayoutParams = new LinearLayout.LayoutParams(
//					screenWidth, screenHeight);
//			pageLayout.setLayoutParams(pageLayoutParams);
//			viewGroup.addView(pageLayout);
//			for(i=(pageNum-1)*8;i<(pageNum*8>resourceInfo.size()?resourceInfo.size():pageNum*8);i++){
//				logger.error("getKey:"+resourceInfo.get(i).getKey());
//				ResourceBean resourceBean = resourceInfo.get(i).getValue();
//				IndexImageButtonImp imageView = null;
//				setXY(resourceBean);
//				
//				imageView = new IndexImagePicButton(context,scrollView,resourceBean);
////				if(scanFoldUtils.bgtype==BgType.pic){
////					imageView = new IndexImagePicButton(context,scrollView,resourceBean);
////				}else{
////					imageView = new IndexImageSwfButton(context,mLayout,resourceBean);
////				}
//				pageLayout.addView(imageView);
//			}
//		}

	}
	int i=0;
	@Override
	protected void setXY(ResourceBean resourceBean) {
		//设置图标的位置
		// TODO Auto-generated method stub
		//int[] indexs = MondifyIndexImageIndex.getImageIndexs(resourceBean.getBtnKey());
		resourceBean.setX(i%4*200+50);
		resourceBean.setY(i/4%2*200+150);
	}
	@Override
	protected int[] calBackGroudView(Bitmap bm){
		int with = bm.getWidth();
		int height = bm.getHeight();


		int[] viewXY = new int[2];
		viewXY[0] = screenWidth;
		viewXY[1] = screenHeight;
		return viewXY;
		
	}
	
}
