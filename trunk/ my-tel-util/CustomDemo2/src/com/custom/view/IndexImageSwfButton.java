package com.custom.view;


import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.custom.bean.ResourceBean;
import com.custom.utils.Logger;

public class IndexImageSwfButton extends IndexImageButtonImp{
	private static final String TAG = "IndexImageSwfButton";
	private static final Logger logger = Logger.getLogger(IndexView.class);
	private AbsoluteLayout mLayout = null;
    private float x;
    private float y;
	
	public IndexImageSwfButton(Context context,AbsoluteLayout mLayout,ResourceBean resourceBean) {
		super(context,resourceBean);
		this.mLayout = mLayout;
		initView();
	}
	

	@Override
	protected void initView() {
		super.initView();
	}
	

	
	int startTouchX = 0;
	int startTouchY = 0;
	int endTouchX = 0;
	int endTouchY = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getPointerCount()>1||!imageCanMove){
			return true;
		}
 
        switch (event.getAction()) {   
        case MotionEvent.ACTION_DOWN:   
            // 获取相对View的坐标，即以此View左上角为原点  
            // 获取相对屏幕的坐标，即以屏幕左上角为原点   
        	AbsoluteLayout.LayoutParams alayout = (AbsoluteLayout.LayoutParams)this.getLayoutParams();
            x = alayout.x;   
            // 25是系统状态栏的高度,也可以通过方法得到准确的值，自己微调就是了   
            y = alayout.y ;   
        	startTouchX = (int)event.getRawX();   
        	startTouchY = (int)event.getRawY();   
//        	x = x-(int)event.getX();   
//        	y = y-(int)event.getY();
        	//logger.error("startTouchX:"+startTouchX+":startTouchY:"+startTouchY);
        	//logger.error("X:"+x+":Y:"+y);
            break;   
        case MotionEvent.ACTION_MOVE:  
        	endTouchX = (int)event.getRawX();   
        	endTouchY = (int)event.getRawY();  
            updateViewPosition(false);   
            break;   
        case MotionEvent.ACTION_UP:   
            updateViewPosition(true);    
            break;   
        }   
        return true;
	}

	private void updateViewPosition(boolean saveIndexs) {
		// 更新浮动窗口位置参数

		AbsoluteLayout.LayoutParams alayout = (AbsoluteLayout.LayoutParams)this.getLayoutParams();
		alayout.x = (int) (x + (endTouchX-startTouchX));
		alayout.y = (int) (y + (endTouchY-startTouchY));
		
		if(alayout.x<0){
			alayout.x=0;
		}else if(alayout.x>mLayout.getWidth()-this.getWidth()){
			alayout.x=mLayout.getWidth()-alayout.width;
		}
		
		if(alayout.y<0){
			alayout.y=0;
		}else if(alayout.y>mLayout.getHeight()-this.getHeight()){
			alayout.y=mLayout.getHeight()-alayout.height;
		}
		IndexImageSwfButton.this.setLayoutParams(alayout);
		logger.error("alayout.x:"+alayout.x+":alayout.y :"+alayout.y );
		if(saveIndexs){
			MondifyIndexImageIndex.modifyImageIndexs(context, resourceBean.getBtnKey(), new int[]{alayout.x,alayout.y});
		}
		
	}
}
