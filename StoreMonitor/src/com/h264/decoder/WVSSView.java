package com.h264.decoder;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.view.View;

import com.h264.decoder.HD264Decoder.DecodeSuccCallback;

class WVSSView extends View {

	
	private HD264Decoder mHD264Decoder;
	private Bitmap mVideoBit;  
       
    public WVSSView(Context context, int width, int height) {
        super(context);
        setFocusable(true);
        mHD264Decoder = new HD264Decoder(width,height,new DecoderCallback());
    }
    
    // decode the NALU and display the picture
    public void decodeNalAndDisplay(byte[] nalBuf, int nalLen) {
    	mHD264Decoder.decodeNal(nalBuf, nalLen);
    }
        
    @Override 
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);  
        if(mVideoBit!=null)
        	canvas.drawBitmap(mVideoBit, 0, 0, null); 
    }
    
    public class DecoderCallback implements DecodeSuccCallback{
		@Override
		public void onDecodeSucc(Bitmap bitmap) {
			// TODO Auto-generated method stub
			mVideoBit = bitmap;
			postInvalidate();
		}
    	
    }
}
