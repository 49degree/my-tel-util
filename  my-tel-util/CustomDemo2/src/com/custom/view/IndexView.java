package com.custom.view;

import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.custom.R;
import com.custom.bean.ResourceBean;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.MondifyIndexImageIndex;
import com.custom.utils.SharedPreferencesUtils;
import com.custom.utils.Constant.BgType;
import com.custom.utils.Constant.DirType;



public class IndexView extends ViewImp{
	private static final Logger logger = Logger.getLogger(IndexView.class);
	protected boolean imageCanMove = false;
	
	public IndexView(Context context,String foldPath,int foldDepth){
		this(context, null, foldPath, foldDepth);
	}
	
	public IndexView(Context context, AttributeSet attr,String foldPath,int foldDepth){
        super(context, attr,foldPath,foldDepth);
		try{
			imageCanMove = Boolean.parseBoolean(context.getString(R.string.modify_index));
		}catch(Exception e){}
		MondifyIndexImageIndex.initImageIndexs(context,imageCanMove);//获取按钮位置信息
	}
	
	@Override
	protected void createIndexButton() {
		Iterator it = scanFoldUtils.resourceInfo.keySet().iterator();
		while(it.hasNext()){
			ResourceBean resourceBean = scanFoldUtils.resourceInfo.get(it.next());
			//加载按钮图片
			try{
				if(resourceBean.getBm()==null)
					resourceBean.setBm(LoadResources.loadBitmap(context, resourceBean.getBtnPic(), resourceBean.getDirType()));
			}catch(Exception e){
				
			}
			setXY(resourceBean);
			IndexImageSwfButton imageView = new IndexImageSwfButton(context,mLayout,resourceBean);
			imageView.setImageMove(imageCanMove);
			mLayout.addView(imageView);
			
//			if(scanFoldUtils.bgtype==BgType.pic){
//				IndexImagePicButton imageView = new IndexImagePicButton(context,scrollView,resourceBean);
//				mLayout.addView(imageView);
//			}else{
//				IndexImageSwfButton imageView = new IndexImageSwfButton(context,mLayout,resourceBean);
//				mLayout.addView(imageView);
//			}
		}
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
		return super.calBackGroudView(bm);
		/**
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
		*/
		
	}
	
	@Override
	public void onPause() {
		SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
				SharedPreferencesUtils.CURPAGENUM, String.valueOf(0));
		super.onPause();

	}
}
