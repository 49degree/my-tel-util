package com.custom.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
	private UnitPageOnclick unitPageOnclick;
	public PageNumView(Context context,PageNumBean pageNumBean) {
		super(context);
		this.context = context;
		this.pageNumBean = pageNumBean;
	}
	/**
	 * 构建界面
	 */
	
	int beginX = 0;
	int beginY = 0;
	
	static Bitmap bm = null;
	static Bitmap pageNum1 =null;
	static Bitmap pageNum2 = null;
	static Bitmap nextunit =null;
	static Bitmap upunit = null;
	
	public static void realease(){
		if(bm==null||bm.isRecycled()){
			bm.recycle();
		}
		if(pageNum1==null||pageNum1.isRecycled()){
			pageNum1.recycle();
		}
		if(pageNum2==null||pageNum2.isRecycled()){
			pageNum2.recycle();
		}
		if(nextunit==null||nextunit.isRecycled()){
			nextunit.recycle();
		}
		if(upunit==null||upunit.isRecycled()){
			upunit.recycle();
		}
		bm = null;
	    pageNum1 =null;
	    pageNum2 = null;
	    nextunit =null;
	    upunit = null;
	}
	
	public void initView(){
		try{
			WindowManager manage = ((Activity)context).getWindowManager();
			Display display = manage.getDefaultDisplay();
			screenHeight = display.getHeight();
			screenWidth = display.getWidth();
			
			if(bm==null||bm.isRecycled()){
				bm = LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/tree.png", DirType.assets);	
			}
			ImageView imageView = new ImageView(context);
			imageView.setImageBitmap(bm);
			beginX = screenWidth-bm.getWidth();
			beginY = 20;
			AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(
					bm.getWidth(), bm.getHeight(),beginX, beginY);
			imageView.setLayoutParams(layout);
			this.addView(imageView);
			
			initPageNumView();
			
			//上行翻页按钮
			try{
				imageView = new ImageView(context);
				if(nextunit==null){
					nextunit = LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/nextunit.png", DirType.assets);
				}
				imageView.setImageBitmap(nextunit);
				AbsoluteLayout.LayoutParams nextlayout = new AbsoluteLayout.LayoutParams(
						200, 100, screenWidth-160,150);
				imageView.setLayoutParams(nextlayout);
				this.addView(imageView);
				imageView.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						unitPageOnclick.nextUnitOnclick();
					}
				});
				
				ImageView imageView2 = new ImageView(context);
				
				if(upunit==null){
					upunit = LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/upunit.png", DirType.assets);
				}
				imageView2.setImageBitmap(upunit);
				AbsoluteLayout.LayoutParams uplayout = new AbsoluteLayout.LayoutParams(
						200, 100, screenWidth-200,210);
				imageView2.setLayoutParams(uplayout);
				this.addView(imageView2);
				imageView2.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						unitPageOnclick.upUnitOnclick();
					}
				});
				
			}catch(Exception e){
				
			}
			
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
			

			if(pageNum2==null){
				pageNum2 = LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/pageNum2.png", DirType.assets);
		           	
			}
			if(pageNum1==null){
	    		 pageNum1 = LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/pageNum1.png", DirType.assets);	           	
			}


			
			WindowManager manage = ((Activity)context).getWindowManager();
			Display display = manage.getDefaultDisplay();
			screenHeight = display.getHeight();
			screenWidth = display.getWidth();
			Bitmap pageNum = null;
			
//            Paint p = new Paint();                                                                 
//            String familyName ="宋体";                                                             
//            Typeface font = Typeface.create(familyName,Typeface.BOLD);                             
//            p.setColor(Color.RED);                                                                 
//            p.setTypeface(font);                                                                   
//            p.setTextSize(30);  

            
			for(int i=pageNumBean.getStartPageNum();i<=pageNumBean.getEndPageNum();i++){
				logger.error("beginX:"+i+":"+pageNumBean.getCurPageNum());
				AbsoluteLayout.LayoutParams layout = null;
				ImageView imageView = new ImageView(context);
				if(i==pageNumBean.getCurPageNum()){
					pageNum = pageNum2;

				}else{
					pageNum = pageNum1;
				}
				if(pageNum!=null){
					//imageView.setImageBitmap(pageNum);
					layout = new AbsoluteLayout.LayoutParams(
							pageNum.getWidth(), pageNum.getHeight(), beginX+i%pageNumBean.getPageNumPerView()*60, beginY+20);
//					Bitmap newb = Bitmap.createBitmap( pageNum.getWidth(), pageNum.getHeight(), Config.ARGB_8888 );  
//		            Canvas canvasTemp = new Canvas( newb );  
//		            canvasTemp.drawColor(Color.TRANSPARENT);    
//		            canvasTemp.drawBitmap(pageNum, 0, 0, p);//画图
//		            canvasTemp.drawText(String.valueOf(i+1), pageNum.getWidth()/3, pageNum.getHeight(), p); 
		            imageView.setImageBitmap(pageNum);
					
					
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
	
	
	
	public void setUnitPageOnclick(UnitPageOnclick unitPageOnclick) {
		this.unitPageOnclick = unitPageOnclick;
	}



	public interface UnitPageOnclick{
		public void upUnitOnclick();
		public void nextUnitOnclick();
	}
	
}
