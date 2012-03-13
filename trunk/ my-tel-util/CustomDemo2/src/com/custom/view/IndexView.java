package com.custom.view;

import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;



public class IndexView extends LinearLayout{
	private Context context = null;


	public IndexView(Context context){
        super(context);
        this.context = context;
        initView();
	}
	
	public IndexView(Context context, AttributeSet attr){
        super(context, attr);
        this.context = context;
        initView();
	}


	
	/**
	 * 构建界面
	 */
	private void initView(){
		try {
			// 设置主界面布局
			this.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));
			this.setOrientation(LinearLayout.VERTICAL);
			BackgroundLinearLayout scrollView = new BackgroundLinearLayout(this.context);
			scrollView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));
			this.addView(scrollView);

			/**
			 * 背景视图
			 */
			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open("custom/test3.jpg");
			// BitmapDrawable backGroundDr = new BitmapDrawable(in);
			Bitmap bm = BitmapFactory.decodeStream(in);
			in.close();
			int with = bm.getWidth();
			int height = bm.getHeight();
			// 设置主布局
			AbsoluteLayout mLayout = new AbsoluteLayout(context);
			LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
					with, height);
			mLayout.setLayoutParams(mLayoutParams);
			mLayout.setBackgroundDrawable(new BitmapDrawable(bm));
			// 使背景获取焦点，焦点不要默认在输入框
			scrollView.setFocusable(true);
			scrollView.setFocusableInTouchMode(true);
			scrollView.addView(mLayout);

			/**
			 * 图片按钮
			 */
			for(int i=0;i<1;i++){
				in = assetManager.open("custom/bg.png");
				bm = BitmapFactory.decodeStream(in);
				IndexImageButton imageView = new IndexImageButton(context,scrollView,bm);

				mLayout.addView(imageView);
			}


		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
