package com.h264.decoder;

import h264.com.H264PicView.DecodeSuccCallback;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class HD264Decoder{
	static String TAG = "HD264Decoder";
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
    	Log.i(TAG, "decodeNal================nalLen:"+nalLen);
    	int iTmp = DecodeNal(nalBuf, nalLen, mPixel);
    	Log.i(TAG, "decodeNal================iTmp:"+iTmp);
    	if( iTmp > 0 ) {
    		mVideoBit.copyPixelsFromBuffer(mBuffer);
    		if(mDecodeSuccCallback!=null)
    			mDecodeSuccCallback.onDecodeSucc(mVideoBit);
    	}
    	return iTmp;
    }


}
