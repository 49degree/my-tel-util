package com.h264.decoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.ImageView;

import com.h264.decoder.HD264Decoder.DecodeSuccCallback;

class SkyeyePicView extends ImageView {

	
	private HD264Decoder mHD264Decoder;
	private Bitmap mVideoBit;  
       
    public SkyeyePicView(Context context, int width, int height) {
        super(context);
        setFocusable(true);
        mHD264Decoder = new HD264Decoder(width,height,new DecoderCallback());
    }
    
    // decode the NALU and display the picture
    public void decodeNalAndDisplay(byte[] nalBuf, int nalLen) {
    	mHD264Decoder.decodeNal(nalBuf, nalLen);
    	mHD264Decoder.UninitDecoder();
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
