package com.custom.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;

import com.custom.bean.ResourceBean;
import com.custom.utils.Logger;
import com.custom.utils.MondifyIndexImageIndex;



public class IndexView extends ViewImp{
	private static final Logger logger = Logger.getLogger(IndexView.class);
	public IndexView(Context context,String foldPath,int foldDepth){
		super(context,foldPath,foldDepth);
	}
	
	public IndexView(Context context, AttributeSet attr,String foldPath,int foldDepth){
        super(context, attr,foldPath,foldDepth);
	}

	@Override
	protected void setXY(ResourceBean resourceBean) {
		//设置图标的位置
		// TODO Auto-generated method stub
		int[] indexs = MondifyIndexImageIndex.getImageIndexs(resourceBean.getBtnKey());
				
		if (indexs == null || indexs.length < 2) {
			indexs = new int[2];
			resourceBean.setX(100);
			resourceBean.setY(100);
		} else {
			resourceBean.setX(indexs[0]);
			resourceBean.setY(indexs[1]);
			if (indexs[0] < 0) {
				resourceBean.setX(100);
			}
			if (indexs[1] < 0) {
				resourceBean.setY(100);
			}
		}
	}
	@Override
	protected int[] calBackGroudView(Bitmap bm){
		int with = bm.getWidth();
		int height = bm.getHeight();
		WindowManager manage = ((Activity)context).getWindowManager();
		Display display = manage.getDefaultDisplay();
		int screenHeight = display.getHeight();
		int screenWidth = display.getWidth();
		
		logger.error("with:"+with+":height:"+height+":screenHeight:"+screenHeight+":screenWidth:"+screenWidth);
		if(with<screenWidth){
			if(height<screenHeight&&with*1.0f/screenWidth>height*1.0f/screenHeight){
				with = (int)(with*screenHeight/height*1.0f);
				height = screenHeight;
				
			}else{
				height = (int)(height*screenWidth/with*1.0f);
				with = screenWidth;
				
			}
		}else if(height<screenHeight){
			if(with<screenWidth&&with*1.0f/screenWidth>height*1.0f/screenHeight){
				with = (int)(with*screenHeight/height*1.0f);
				height = screenHeight;
				
			}else{
				height = (int)(height*screenWidth/with*1.0f);
				with = screenWidth;
			}
		}
		int[] viewXY = new int[2];
		viewXY[0] = with;
		viewXY[1] = height;
		return viewXY;
		
	}
	
}
