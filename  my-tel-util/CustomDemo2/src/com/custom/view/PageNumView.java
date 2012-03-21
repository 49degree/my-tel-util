package com.custom.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import com.custom.bean.PageNumBean;
import com.custom.utils.Constant;
import com.custom.utils.Constant.DirType;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;

public class PageNumView extends AbsoluteLayout{
	private static final Logger logger = Logger.getLogger(PageNumView.class);
	private int screenHeight = 0;
	private int screenWidth = 0;
	private PageNumBean pageNumBean=null;
	Context context = null;
	private ArrayList<View> pageNumViews = new ArrayList<View>();
	public PageNumView(Context context,PageNumBean pageNumBean) {
		super(context);
		this.context = context;
		this.pageNumBean = pageNumBean;
		
		WindowManager manage = ((Activity)context).getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		initView();
	}
	/**
	 * 构建界面
	 */
	
	int beginX = 0;
	int beginY = 0;
	
	private void initView(){
		try{
			Bitmap bm = LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/tree.png", DirType.assets);
			ImageView imageView = new ImageView(context);
			imageView.setImageBitmap(bm);
			beginX = screenWidth-bm.getWidth();
			beginY = 20;
			AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(
					bm.getWidth(), bm.getHeight(),beginX, beginY);
			imageView.setLayoutParams(layout);
			this.addView(imageView);
			
			initPageNumView();
			
		}catch(Exception e){
			
		}
	}
	
	
	public void initPageNumView(){
		try{
			while(pageNumViews.size()>0){
				try{
					this.removeView(pageNumViews.remove(0));
				}catch(Exception e){
					
				}
				
			}
			
			for(int i=pageNumBean.getStartPageNum();i<=pageNumBean.getEndPageNum();i++){
				logger.error("beginX:"+i+":"+pageNumBean.getCurPageNum());
				AbsoluteLayout.LayoutParams layout = null;
				ImageView imageView = new ImageView(context);
				if(i==pageNumBean.getCurPageNum()){
					Bitmap pageNum2 = LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/pageNum2.png", DirType.assets);
					imageView.setImageBitmap(pageNum2);
					layout = new AbsoluteLayout.LayoutParams(
							pageNum2.getWidth(), pageNum2.getHeight(), beginX+i%pageNumBean.getPageNumPerView()*60, beginY+20);
				}else{
					Bitmap pageNum1 = LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/pageNum1.png", DirType.assets);
					imageView.setImageBitmap(pageNum1);
					layout = new AbsoluteLayout.LayoutParams(
							pageNum1.getWidth(), pageNum1.getHeight(), beginX+i%pageNumBean.getPageNumPerView()*60, beginY+20);
				}

				imageView.setLayoutParams(layout);
				this.addView(imageView);
				pageNumViews.add(imageView);
				
				
			}
		}catch(Exception e){
			
		}
	}
	
	
	
	public PageNumBean getPageNumBean(){
		return pageNumBean;
	}
	
}
