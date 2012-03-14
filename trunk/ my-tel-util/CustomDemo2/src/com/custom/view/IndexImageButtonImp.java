package com.custom.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.widget.LinearLayout;

import com.custom.bean.ResourceBean;

public abstract class IndexImageButtonImp extends LinearLayout{
	private static final String TAG = "IndexImageButtonImp";
	
	protected boolean imageCanMove = true;
	protected ResourceBean resourceBean = null;
	protected Context context;
	protected Bitmap bm=null;
	public IndexImageButtonImp(Context context,ResourceBean resourceBean) {
		super(context);
		this.context = context;
		this.resourceBean = resourceBean;
	}  
	
	protected abstract void initView();

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
