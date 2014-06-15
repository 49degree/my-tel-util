package com.skyeyes.storemonitor.view;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.skyeyes.base.h264.H264Decoder.DecodeSuccCallback;
import com.skyeyes.base.h264.H264DecoderException;
import com.skyeyes.base.h264.JavaH264Decoder;

public class H264VideoView extends View implements Runnable{
	private int videoViewStartX = 0;
	private int videoViewStartY = 0;
	private int videoViewEndX = 0;
	private int videoViewEndY = 0;
	private Bitmap videoBitmap;  
	private JavaH264Decoder decoder;
	private LinkedList<byte[]> dataBufferList = new LinkedList<byte[]>();
	
	private Context mContext;
	private Display mDisplay;
	private DecodeSuccCallback mDecodeSuccCallback;
	
	private Thread decodeThread;
	
	private boolean play = false;
	
	private Handler handler;
	
	DisplayMetrics dm = null;
	
	public H264VideoView(Context context,Display display,DecodeSuccCallback decodeSuccCallback) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mDisplay = display;
		dm = new DisplayMetrics();
		mDisplay.getMetrics(dm);
		mDecodeSuccCallback = decodeSuccCallback;
		handler = new Handler(Looper.getMainLooper());
	    try {
			decoder = new JavaH264Decoder(new DecodeSuccCallback(){
				@Override
				public void onDecodeSucc(final JavaH264Decoder decoder ,final Bitmap bitmap) {
					// TODO Auto-generated method stub
					//Log.i("DecoderCallback", "onDecodeSucc================");
					if(videoBitmap == null){
						setVideoDisplay(bitmap.getWidth(),bitmap.getHeight());
					}
					videoBitmap = bitmap;
					handler.post(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							postInvalidate();
							if(mDecodeSuccCallback!=null){
								mDecodeSuccCallback.onDecodeSucc(decoder, bitmap);
							}
						}
					});
				}
				
			},JavaH264Decoder.PIC_WIDTH,JavaH264Decoder.PIC_HEIGHT);
		} catch (H264DecoderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        temp = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Config.RGB_565);
        rectF = new RectF(0, 0, dm.widthPixels, dm.heightPixels); 
	}
	
	
	public void toStopPlay(){
		play = false;
		if(decoder!=null)
			decoder.toStop();
		if(decodeThread!=null){
			decodeThread.interrupt();
			dataBufferList.clear();
		}

	}
	
	public void toStartPlay(){
		play = true;
		if(decodeThread==null){
			decodeThread = new Thread(this);
			decodeThread.setDaemon(true);
			decodeThread.start();
		}
	}
	
	
	public void sendStream(byte[] videoData){
		if(!play){
			return;
		}
		synchronized (this) {
			dataBufferList.offer(videoData);
		}
		Log.e("H264VideoView", "dataBufferList:"+dataBufferList.size());
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] tempData = null;
		while(play){
			if(dataBufferList.size()==0){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			synchronized (this) {
				tempData = dataBufferList.poll();

				if(dataBufferList.size()>100){//缓冲数据太多，不进行解码
					if(!decoder.getSkipNalu()){
						decoder.setSkipNalu(true);
					}
				}else{
					if(decoder.getSkipNalu()&&dataBufferList.size()<10){
						decoder.setSkipNalu(false);
					}
				}
			}
			
			decoder.sendStream(tempData);
		}
	}
	
	
	
    private void setVideoDisplay(int bitmapWidth,int bitmapHeight){
		videoViewStartY = 0;
		
		
		
		videoViewEndY = dm.heightPixels;
		
		float zoom = (videoViewEndY*1.0f)/bitmapHeight;
		int tmepWidth =  (int)(zoom*bitmapWidth);
		if(dm.widthPixels - tmepWidth>0){
			videoViewStartX = (dm.widthPixels - tmepWidth) / 2;
			videoViewEndX = videoViewStartX + tmepWidth;
		}else{
			videoViewStartX = 0;
			videoViewEndX = dm.widthPixels;
			
    		zoom = (videoViewEndX*1.0f)/bitmapWidth;
    		int tmepHeight =  (int)zoom*bitmapHeight;
    		
    		videoViewStartY = (dm.heightPixels-tmepHeight)/2;
    		videoViewEndY = videoViewStartY+tmepHeight;
		}
    }
    

    Bitmap temp = null;
    RectF rectF = null;
    
	@Override 
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); 
        
        
        if(videoBitmap!=null){
        	RectF tempRect = new RectF(videoViewStartX, videoViewStartY, videoViewEndX, videoViewEndY); 
        	//w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高  
        	canvas.drawBitmap(temp, null, rectF, null);
        	canvas.drawBitmap(videoBitmap, null, tempRect, null);
        }
        

        

    }

	


}
