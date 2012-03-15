package com.custom.view;


import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.custom.bean.ResourceBean;

public abstract class IndexImageButtonImp extends LinearLayout{
	private static final String TAG = "IndexImageButtonImp";
	
	protected boolean imageCanMove = false;
	protected ResourceBean resourceBean = null;
	protected Context context;
	protected Bitmap bm=null;
	public IndexImageButtonImp(Context context,ResourceBean resourceBean) {
		super(context);
		this.context = context;
		this.resourceBean = resourceBean;
	}  
	
	protected void initView() {
		try{
			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open(resourceBean.getBtnPic());
			bm = BitmapFactory.decodeStream(in);
		}catch(Exception e){
			
		}
		ImageView jpgView = new ImageView(context);
		jpgView.setImageBitmap(bm);
		int with = bm.getWidth();
		int height = bm.getHeight();
		LinearLayout.LayoutParams alayout = new LinearLayout.LayoutParams(200, 200);
		jpgView.setLayoutParams(alayout);
		this.addView(jpgView);
		
		TextView text = new TextView(context);
		text.setText(resourceBean.getName());
		LinearLayout.LayoutParams tlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		text.setLayoutParams(tlayout);
		this.addView(text);

		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.CENTER);
		this.setBackgroundColor(Color.RED);
		
		int[] indexs = MondifyIndexImageIndex.getImageIndexs(resourceBean.getBtnKey());
		if(indexs==null||indexs.length<2){
			indexs = new int[2];
			indexs[0] = 100;
			indexs[1] = 100;
		}else{
			if(indexs[0] <0){
				indexs[0] =100;
			}
			if(indexs[1]<0){
				indexs[1] = 100;
			}
		}
		
		AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(
				300, 300, indexs[0], indexs[1]);
		
		this.setLayoutParams(layout);
		
	}

//	protected abstract void onPause() ;
//
//	protected abstract void onResume() ;
	
	
	public void setImageMove(boolean canMove){
    	imageCanMove = canMove;
    }
    
    public boolean getImageMove(){
    	return imageCanMove;
    }
}
