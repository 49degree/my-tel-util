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





public class SecondView extends ViewImp{
	private static final Logger logger = Logger.getLogger(IndexView.class);
	public SecondView(Context context,String foldPath,int foldDepth){
		super(context,foldPath,foldDepth);
	}
	
	public SecondView(Context context, AttributeSet attr,String foldPath,int foldDepth){
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

		int[] viewXY = new int[2];
		viewXY[0] = screenWidth;
		viewXY[1] = screenHeight;
		return viewXY;
		
	}
	
}
