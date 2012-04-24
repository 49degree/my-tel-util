package com.custom.view;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.custom.utils.Constant;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;

public class BackButton extends LinearLayout{
	private static final Logger logger = Logger.getLogger(BackButton.class);
	BitmapDrawable back1 = null;
	BitmapDrawable back2 = null;
	static Bitmap backBitMap1 =null;
	static Bitmap backBitMap2 = null;
	float zoom = 1f;
	@SuppressWarnings("deprecation")
	public BackButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		try{
			if(backBitMap1==null){
				backBitMap1 = LoadResources.getBitmap(context, 
						Constant.pageNumPicPath+File.separator+Constant.backPicName1); 
			}
			if(backBitMap2==null){
				backBitMap2 = LoadResources.getBitmap(context, 
						Constant.pageNumPicPath+File.separator+Constant.backPicName2);      	
			}
		}catch(Exception e){
			
		}
		if(backBitMap1==null||backBitMap2==null){
			return;
		}
		
		back1 = new BitmapDrawable(backBitMap1);
		back2 = new BitmapDrawable(backBitMap2);
		

	}
	public void initView(final Context context){
		
		ImageView jpgView = new ImageView(context);
		jpgView.setImageDrawable(back1);
		int bmWidth = (int)(backBitMap1.getWidth()*zoom);
		int bmHeight = (int)(backBitMap1.getHeight()*zoom);
		LinearLayout.LayoutParams alayout = new LinearLayout.LayoutParams(bmWidth, bmHeight);
		jpgView.setLayoutParams(alayout);
		this.addView(jpgView);
		
		
		logger.error("this.setBackgroundDrawable(back1);");
		
		jpgView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				((ImageView)v).setImageDrawable(back2);
	        	return onTouchEvent(event);
			}
			
		});
		
		jpgView.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				((ImageView)v).setImageDrawable(back1);
				((Activity)context).finish();
				
			}
		});
		
		WindowManager manage = ((Activity)context).getWindowManager();
		Display display = manage.getDefaultDisplay();
		int screenHeight = display.getHeight();
		int screenWidth = display.getWidth();
		logger.error("this.setBackgroundDrawable(back1);"+screenHeight+":"+screenWidth);
		AbsoluteLayout.LayoutParams backlayout = new AbsoluteLayout.LayoutParams(
				bmWidth, bmHeight,screenWidth-bmWidth-(int)(30*zoom), screenHeight-bmHeight-(int)(30*zoom));
		this.setLayoutParams(backlayout);
	}
	public static void realease(){
		if(backBitMap1!=null&&!backBitMap1.isRecycled()){
			backBitMap1.recycle();
		}
		if(backBitMap2!=null&&!backBitMap2.isRecycled()){
			backBitMap2.recycle();
		}
		backBitMap1 = null;
		backBitMap2 = null;
	}
	public float getZoom() {
		return zoom;
	}


	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

}
