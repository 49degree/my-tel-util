package com.custom.view;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.custom.bean.PageNumBean;
import com.custom.utils.Constant;
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
	private String foldPath;
	private boolean isFirst = true;
	public PageNumView(Context context,PageNumBean pageNumBean,String foldPath) {
		super(context);
		this.context = context;
		this.pageNumBean = pageNumBean;
		this.foldPath = foldPath;

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
	static Bitmap title = null;
	
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
		
		if(title==null||title.isRecycled()){
			title.recycle();
		}
		bm = null;
	    pageNum1 =null;
	    pageNum2 = null;
	    nextunit =null;
	    upunit = null;
	}
	
	public void initView(){
		try{
			if(pageNum2==null){
				pageNum2 = LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"pageNum2.png"); 
			}
			if(pageNum1==null){
	    		 pageNum1 = LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"pageNum1.png");        	
			}
			if(upunit==null){
				upunit = LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"upunit.png"); 
			}
			if(nextunit==null){
				nextunit =  LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"nextunit.png"); 
			}
			if(bm==null){
				bm = LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"tree.png"); 
			}
			if(title==null){
				title = LoadResources.getBitmap(context, foldPath+File.separator+Constant.foldTilePic); 
			}
			
			WindowManager manage = ((Activity)context).getWindowManager();
			Display display = manage.getDefaultDisplay();
			screenHeight = display.getHeight();
			screenWidth = display.getWidth();
			

			ImageView imageView = new ImageView(context);
			imageView.setImageBitmap(bm);
			beginX = screenWidth-bm.getWidth();
			beginY = 20;
			AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(
					bm.getWidth(), bm.getHeight(),beginX, beginY);
			imageView.setLayoutParams(layout);
			this.addView(imageView);
			
			
			ImageView tileView = new ImageView(context);
			tileView.setImageBitmap(title);
			AbsoluteLayout.LayoutParams tileViewlayout = new AbsoluteLayout.LayoutParams(
					title.getWidth(), title.getHeight(),50, 50);
			tileView.setLayoutParams(tileViewlayout);
			this.addView(tileView);
			
			initPageNumView();
			
			//上行翻页按钮
			try{
				imageView = new ImageView(context);

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
				//logger.error("beginX:"+i+":"+pageNumBean.getCurPageNum());
				AbsoluteLayout.LayoutParams layout = null;
				TextView imageView = new TextView(context);
				if(i==pageNumBean.getCurPageNum()&&!this.isFirst){
					pageNum = pageNum2;
					imageView.setTextColor(Color.YELLOW);

				}else{
					pageNum = pageNum1;
					imageView.setTextColor(Color.GREEN);
				}
				if(pageNum!=null){
					//imageView.setImageBitmap(pageNum);
					boolean doubleView = (i/pageNumBean.getPageNumPerView()%2==0);
					layout = new AbsoluteLayout.LayoutParams(
							pageNum.getWidth(), pageNum.getHeight(), 
							beginX+115+i%pageNumBean.getPageNumPerView()*60, beginY+(((doubleView&&i%2==0)||(!doubleView&&i%2!=0))?65:55));
//					Bitmap newb = Bitmap.createBitmap( pageNum.getWidth(), pageNum.getHeight(), Config.ARGB_8888 );  
//		            Canvas canvasTemp = new Canvas( newb );  
//		            canvasTemp.drawColor(Color.TRANSPARENT);    
//		            canvasTemp.drawBitmap(pageNum, 0, 0, p);//画图
//		            canvasTemp.drawText(String.valueOf(i+1), pageNum.getWidth()/3, pageNum.getHeight(), p); 
		            imageView.setBackgroundDrawable(new BitmapDrawable(pageNum));
		            
				}
				imageView.setText(String.valueOf(i+1));
				imageView.setGravity(Gravity.CENTER);
				imageView.setPadding(0, 5, 0, 0);
				imageView.setLayoutParams(layout);
				this.addView(imageView);
				pageNumViews.add(imageView);
				imageView.setOnClickListener(new OnClickListener(){
					public void onClick(View v){
						int num = 0;
						try{
							num = Integer.parseInt(((TextView)v).getText().toString())-1;
						}catch(Exception e){
							
						}
						if(num!=pageNumBean.getCurPageNum()||isFirst){
							pageNumBean.setCurPageNum(num);
							unitPageOnclick.fistViewOnclick();
							isFirst = false;
						}

					}
				});
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



	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}



	public interface UnitPageOnclick{
		public void upUnitOnclick();
		public void nextUnitOnclick();
		public void fistViewOnclick();
	}
	
}