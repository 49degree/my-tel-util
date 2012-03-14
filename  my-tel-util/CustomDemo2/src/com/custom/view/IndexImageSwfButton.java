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

public class IndexImageSwfButton extends IndexImageButtonImp{
	private static final String TAG = "IndexImageSwfButton";
	private static final Logger logger = Logger.getLogger(IndexView.class);
	
	private WindowManager.LayoutParams wmParams =null;
	private WindowManager wm = null;
    private float x;
    private float y;
	
	public IndexImageSwfButton(Context context,ResourceBean resourceBean) {
		super(context,resourceBean);
		wmParams =new WindowManager.LayoutParams();
		initView();
	}
	
	

	@Override
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
		wmParams.x = 100;
		wmParams.y = 100;
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
            updateViewPosition();   
            break;   
        case MotionEvent.ACTION_UP:   
            updateViewPosition();    
            break;   
        }   
        return true;
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		// 获取WindowManager
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wmParams.x = (int) (x + (endTouchX-startTouchX));
		wmParams.y = (int) (y + (endTouchY-startTouchY));
		wm.updateViewLayout(this, wmParams);
	}
}
