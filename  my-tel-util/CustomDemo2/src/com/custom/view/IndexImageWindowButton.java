package com.custom.view;


import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.custom.bean.ResourceBean;
import com.custom.utils.Logger;
import com.custom.utils.MondifyIndexImageIndex;

public class IndexImageWindowButton extends IndexImageButtonImp{
	private static final String TAG = "IndexImageSwfButton";
	private static final Logger logger = Logger.getLogger(IndexView.class);
	
	private WindowManager.LayoutParams wmParams =null;
	private WindowManager wm = null;
    private float x;
    private float y;
	
	public IndexImageWindowButton(Context context,ResourceBean resourceBean) {
		super(context,resourceBean);
		wmParams =new WindowManager.LayoutParams();
		initView();
	}
	
	

	@Override
	protected void initView() {
		super.initView();

		// 获取WindowManager
		wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		/**
		 * 以下都是WindowManager.LayoutParams的相关属性 具体用途可参考SDK文档
		 */
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// 设置Window flag
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */
		// wmParams.gravity=Gravity.LEFT|Gravity.TOP; //调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值

		int[] indexs = MondifyIndexImageIndex.getImageIndexs(resourceBean.getBtnKey());
		if(indexs==null||indexs.length<2){
			wmParams.x = 100;
			wmParams.y = 100;
		}else{
			wmParams.x = indexs[0];
			wmParams.y = indexs[1];
			logger.error(indexs[0]+":"+indexs[1]);
		}

		
		// 设置悬浮窗口长宽数据
		wmParams.width = 300;
		wmParams.height = 300;
		// 显示myFloatView图像
		wm.addView(this, wmParams);
	}
	
	public void removeView(){
    	if(wm!=null){
    		Log.e(TAG, "wm.removeView(myFV)");
    		try{
    			wm.removeView(this);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		
    	}
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
            x = wmParams.x;   
            // 25是系统状态栏的高度,也可以通过方法得到准确的值，自己微调就是了   
            y = wmParams.y ;   
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
		// 获取WindowManager
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wmParams.x = (int) (x + (endTouchX-startTouchX));
		wmParams.y = (int) (y + (endTouchY-startTouchY));
		wm.updateViewLayout(this, wmParams);
		if(saveIndexs){
			MondifyIndexImageIndex.modifyImageIndexs(context, resourceBean.getBtnKey(), new int[]{wmParams.x,wmParams.y});
		}
		
	}
}
