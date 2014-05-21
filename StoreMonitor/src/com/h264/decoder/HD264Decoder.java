package com.h264.decoder;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.view.View;

public class HD264Decoder{
	
    private byte [] mPixel;    
    private ByteBuffer mBuffer;
	private Bitmap mVideoBit;  
	private DecodeSuccCallback mDecodeSuccCallback;
   
    public native int InitDecoder(int width, int height);
    public native int UninitDecoder(); 
    public native int DecodeNal(byte[] in, int insize, byte[] out);
    
    static {
        System.loadLibrary("H264Decoder");
    }
       
    public HD264Decoder(int width, int height,DecodeSuccCallback decodeSuccCallback) {
        mPixel = new byte[width*height*2];      
        Arrays.fill(mPixel, (byte) 0);

        mBuffer = ByteBuffer.wrap( mPixel );
        mVideoBit = Bitmap.createBitmap(width, height, Config.RGB_565); 
        mDecodeSuccCallback = decodeSuccCallback;
        InitDecoder(width, height);
    }
    
    // decode the NALU and display the picture
    public int decodeNal(byte[] nalBuf, int nalLen) {
    	int iTmp = DecodeNal(nalBuf, nalLen, mPixel);
    	if( iTmp > 0 ) {
    		mVideoBit.copyPixelsFromBuffer(mBuffer);
    		if(mDecodeSuccCallback!=null)
    			mDecodeSuccCallback.onDecodeSucc(mVideoBit);
    	}
    	return iTmp;
    }
    
    public interface DecodeSuccCallback{
    	public void onDecodeSucc(Bitmap bitmap);
    }

}
