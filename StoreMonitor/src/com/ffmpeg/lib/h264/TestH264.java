package com.ffmpeg.lib.h264;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ffmpeg.lib.h264.H264Decoder.DecodeSuccCallback;

public class TestH264 extends Activity {
	H264View mH264View = null;
	RelativeLayout root_layout;
	
	NativeH264Decoder mJavaH264Decoder = null;
	Bitmap videoBitmap;
	
	Bitmap backgroud = null;
	RectF backgroudRectF = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		root_layout = new RelativeLayout(this);
		
		
		setContentView(root_layout);
		
		mH264View = new H264View(this);
		root_layout.addView(mH264View);
		
		try {
			mJavaH264Decoder = new NativeH264Decoder(new DecodeSuccCallback(){
				@Override
				public void onDecodeSucc(final H264Decoder decoder,Bitmap bitmap) {
					// TODO Auto-generated method stub
					videoBitmap = Bitmap.createBitmap(bitmap);
					if(mH264View!=null){
						runOnUiThread(new Runnable(){
							public void run(){
								mH264View.postInvalidate();
								//decoder.toStop();
							}
						});
					}
				}
			},100,100);
		} catch (H264DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int[]  colors={Color.WHITE,Color.BLACK,Color.BLUE};
		backgroud = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Config.RGB_565);
		backgroudRectF = new RectF(0, 0, dm.widthPixels, dm.heightPixels); 
	}
	public void onStop(){
		super.onStop();
		if(mJavaH264Decoder!=null)
			mJavaH264Decoder.toStop();
	}
	
	public void onResume(){
		super.onResume();
		Log.e(this.getClass().getSimpleName(), "onResume=======================");
		if(mJavaH264Decoder!=null){
			new Thread(){
				public void run(){
					try {
						InputStream in = getResources().getAssets().open("slamtv60.264");
						byte[] buffer = new byte[10240];
						int len;
						while((len=in.read(buffer))>0){
							if(mJavaH264Decoder.hasMoreNAL){
								mJavaH264Decoder.sendStream(buffer,0,len);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}

	}



	public class H264View extends View {

		public H264View(Context context) {
			super(context);
			// TODO Auto-generated constructor stub

		}
		@Override 
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas); 
	        
	        canvas.drawColor(Color.WHITE); 
	        //drawBitmap(backgroud, null, backgroudRectF, null);
	          //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高  
	        if(videoBitmap!=null && videoBitmap!=null){
		        RectF rectF = new RectF(0, 0 , videoBitmap.getWidth(), videoBitmap.getHeight()); 
	        	canvas.drawBitmap(videoBitmap, null, rectF, null);
	        }
	        

	    }
	}

	
	
}
