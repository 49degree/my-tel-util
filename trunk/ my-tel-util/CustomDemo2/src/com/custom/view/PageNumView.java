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
import android.view.MotionEvent;
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
	
	float zoom = 1f;
	
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
	static Bitmap nextunit1 =null;
	static Bitmap upunit1 = null;
	static Bitmap nextunit2 =null;
	static Bitmap upunit2 = null;
	static Bitmap title = null;
	
	public static void realease(){
		if(bm!=null&&!bm.isRecycled()){
			bm.recycle();
		}
		if(pageNum1!=null&&!pageNum1.isRecycled()){
			pageNum1.recycle();
		}
		if(pageNum2!=null&&!pageNum2.isRecycled()){
			pageNum2.recycle();
		}
		if(nextunit1!=null&&!nextunit1.isRecycled()){
			nextunit1.recycle();
		}
		if(upunit1!=null&&!upunit1.isRecycled()){
			upunit1.recycle();
		}
		if(nextunit2!=null&&!nextunit2.isRecycled()){
			nextunit2.recycle();
		}
		if(upunit2!=null&&!upunit2.isRecycled()){
			upunit2.recycle();
		}		
		if(title!=null&&!title.isRecycled()){
			title.recycle();
		}
		bm = null;
	    pageNum1 =null;
	    pageNum2 = null;
	    nextunit1 =null;
	    upunit1 = null;
	    nextunit2 =null;
	    upunit2 = null;
	    title = null;
	}
	
	public void initView(){
		try{
			if(pageNum2==null){
				pageNum2 = LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"pageNum2.png"); 
			}
			if(pageNum1==null){
	    		 pageNum1 = LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"pageNum1.png");        	
			}
			if(upunit1==null){
				upunit1 = LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"upunit1.png"); 
			}
			if(nextunit1==null){
				nextunit1 =  LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"nextunit1.png"); 
			}
			if(upunit2==null){
				upunit2 = LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"upunit2.png"); 
			}
			if(nextunit2==null){
				nextunit2 =  LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+"nextunit2.png"); 
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
			beginX = screenWidth-(int)(bm.getWidth()*zoom);
			beginY = (int)(20*zoom);
			AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(
					(int)(bm.getWidth()*zoom), (int)(bm.getHeight()*zoom),beginX, beginY);
			imageView.setLayoutParams(layout);
			this.addView(imageView);
			
			
			ImageView tileView = new ImageView(context);
			tileView.setImageBitmap(title);
			AbsoluteLayout.LayoutParams tileViewlayout = new AbsoluteLayout.LayoutParams(
					(int)(title.getWidth()*zoom), (int)(title.getHeight()*zoom),(int)(50*zoom), (int)(50*zoom));
			tileView.setLayoutParams(tileViewlayout);
			this.addView(tileView);
			
			initPageNumView();
			
			//上行翻页按钮
			try{
				imageView = new ImageView(context);
				imageView.setImageBitmap(nextunit1);
				AbsoluteLayout.LayoutParams nextlayout = new AbsoluteLayout.LayoutParams(
						(int)(nextunit1.getWidth()*zoom), (int)(nextunit1.getHeight()*zoom), screenWidth-(int)(100*zoom),(int)(188*zoom));
				imageView.setLayoutParams(nextlayout);
				this.addView(imageView);
				
				imageView.setOnTouchListener(new OnTouchListener(){

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						((ImageView)v).setImageBitmap(nextunit2);
			        	return onTouchEvent(event);
					}
					
				});
				imageView.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						((ImageView)v).setImageBitmap(nextunit1);
						unitPageOnclick.nextUnitOnclick();
						
					}
				});
				
				ImageView imageView2 = new ImageView(context);
				imageView2.setImageBitmap(upunit1);
				imageView2.setOnTouchListener(new OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						((ImageView)v).setImageBitmap(upunit2);
			        	return onTouchEvent(event);
					}
					
				});
				AbsoluteLayout.LayoutParams uplayout = new AbsoluteLayout.LayoutParams(
						(int)(upunit1.getWidth()*zoom), (int)(upunit1.getHeight()*zoom), screenWidth-(int)(150*zoom),(int)(295*zoom));
				imageView2.setLayoutParams(uplayout);
				this.addView(imageView2);
				imageView2.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						((ImageView)v).setImageBitmap(upunit1);
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
					imageView.setTextColor(0xFF8B0000);
					imageView.setPadding(0, (int)(50*zoom), 0, 0);

				}else{
					pageNum = pageNum1;
					imageView.setTextColor(0xFF006400);
					imageView.setPadding(0, (int)(30*zoom), 0, 0);
				}
				if(pageNum!=null){
					int x = 0;
					int y = 0;
					switch(i%pageNumBean.getPageNumPerView()){
					case 0:
						x = screenWidth-(int)(500*zoom);
						y = (int)(72*zoom);
						break;
					case 1:
						x = screenWidth-(int)(422*zoom);
						y = (int)(60*zoom);
						break;
					case 2:
						x = screenWidth-(int)(342*zoom);
						y = (int)(55*zoom);
						break;
					case 3:
						x = screenWidth-(int)(270*zoom);
						y = (int)(64*zoom);
						break;	
					case 4:
						x = screenWidth-(int)(196*zoom);
						y = (int)(60*zoom);
						break;	
					default:
						break;
						
					}
					layout = new AbsoluteLayout.LayoutParams(
							(int)(pageNum.getWidth()*zoom), (int)(pageNum.getHeight()*zoom), 
							x, y);
		            imageView.setBackgroundDrawable(new BitmapDrawable(pageNum));   
				}
				
				
				imageView.setText(String.valueOf(i+1));
				imageView.getPaint().setFakeBoldText(true);

				imageView.setGravity(Gravity.CENTER);
				
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

	public float getZoom() {
		return zoom;
	}


	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public interface UnitPageOnclick{
		public void upUnitOnclick();
		public void nextUnitOnclick();
		public void fistViewOnclick();
	}
	
}
