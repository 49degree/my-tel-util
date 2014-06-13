package com.skyeyes.base.h264;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.util.Log;

import com.skyeyes.base.h264.H264Decoder.DecodeSuccCallback;

public class CPPH264Decoder{

	boolean skipNalu;
    DecodeSuccCallback mDecodeSuccCallback;
    
    ExecutorService mExecutorService = Executors.newFixedThreadPool(5);
    
	public CPPH264Decoder(DecodeSuccCallback decodeSuccCallback) throws H264DecoderException{
		mDecodeSuccCallback = decodeSuccCallback;
		int i = mPixel.length;
		for (i = 0; i < mPixel.length; i++) {
			mPixel[i] = (byte) 0x00;
		}
		InitDecoder(width, height);
	}
	
	public void toStop(){
		try{
			hasMoreNAL = false;
		    if(VideoBit!=null)
		    	VideoBit.recycle();
		    VideoBit = null;
		}catch(Exception e){
			
		}
		UninitDecoder();
	    System.out.println("Stop playing video.");
	}
	
	public boolean isStop(){
		return !hasMoreNAL;
	}
	public void sendStream(byte[] data) {
		sendStream(data,0,data.length);
	}
	


	boolean hasMoreNAL = false;
	Bitmap mBitQQ = null;

	Paint mPaint = null;

	Bitmap mSCBitmap = null;

	int width = 580; // 此处设定不同的分辨率
	int height = 320;

	byte[] mPixel = new byte[width * height * 2];

	ByteBuffer buffer = ByteBuffer.wrap(mPixel);
	Bitmap VideoBit = Bitmap.createBitmap(width, height, Config.RGB_565);

	int mTrans = 0x0F0F0F0F;

	String PathFileName;

	public native int InitDecoder(int width, int height);

	public native int UninitDecoder();

	public native int DecoderNal(byte[] in, int insize, byte[] out);

	static {
		System.loadLibrary("H264Android");
	}




	int MergeBuffer(byte[] NalBuf, int NalBufUsed, byte[] SockBuf,
			int SockBufUsed, int SockRemain) {
		int i = 0;
		byte Temp;

		for (i = 0; i < SockRemain; i++) {
			Temp = SockBuf[i + SockBufUsed];
			NalBuf[i + NalBufUsed] = Temp;

			mTrans <<= 8;
			mTrans |= Temp;

			if (mTrans == 1) // 找到一个开始字
			{
				i++;
				break;
			}
		}

		return i;
	}

	public void sendStream(byte[] data,int start,int len) {

		int iTemp = 0;
		int nalLen;

		boolean bFirst = true;
		boolean bFindPPS = true;

		int bytesLen = len;
		int NalBufUsed = 0;
		int SockBufUsed = 0;

		byte[] NalBuf = new byte[40980]; // 40k
		byte[] SockBuf = data;



		
		Log.e(this.getClass().getSimpleName(), "decode begin");

		SockBufUsed = 0;

		while (hasMoreNAL && bytesLen - SockBufUsed > 0) {
			nalLen = MergeBuffer(NalBuf, NalBufUsed, SockBuf, SockBufUsed,
					bytesLen - SockBufUsed);

			NalBufUsed += nalLen;
			SockBufUsed += nalLen;

			while (mTrans == 1) {
				mTrans = 0xFFFFFFFF;

				if (bFirst == true) // the first start flag
				{
					bFirst = false;
				} else // a complete NAL data, include 0x00000001 trail.
				{
					if (bFindPPS == true) // true
					{
						if ((NalBuf[4] & 0x1F) == 7) {
							bFindPPS = false;
						} else {
							NalBuf[0] = 0;
							NalBuf[1] = 0;
							NalBuf[2] = 0;
							NalBuf[3] = 1;

							NalBufUsed = 4;

							break;
						}
					}
					// decode nal
					iTemp = DecoderNal(NalBuf, NalBufUsed - 4, mPixel);

					if (iTemp > 0){
						buffer.position(0);
						VideoBit.copyPixelsFromBuffer(buffer);// makeBuffer(data565, N));
					}
				}

				NalBuf[0] = 0;
				NalBuf[1] = 0;
				NalBuf[2] = 0;
				NalBuf[3] = 1;

				NalBufUsed = 4;
			}
		}
		Log.e(this.getClass().getSimpleName(), "decode complete");
	}

	
	public void setSkipNalu(boolean skipNalu){
		this.skipNalu = skipNalu;
	}
	public boolean getSkipNalu(){
		return this.skipNalu;
	}

}
