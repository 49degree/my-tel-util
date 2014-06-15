package com.skyeyes.base.h264;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.skyeyes.base.h264.H264Decoder.DecodeSuccCallback;

public class JavaH264Decoder{
	public final static int PIC_WIDTH = 352;
	public final static int PIC_HEIGHT = 288;
	public static final int INBUF_SIZE = 65535;
	private int[] buffer = null;
	private Bitmap videoBitmap; 
	private ByteBuffer byteBuffer;

    byte[] inbuf;
    //int[] inbuf_int;
    byte[] buf;

    int dataPointer;
    int[] cacheRead = new int[3];
    int[] picInfo = new int[2];
    boolean hasMoreNAL = true;
    int pauseStep = 0;
    boolean skipNalu;
    DecodeSuccCallback mDecodeSuccCallback;
    
    ExecutorService mExecutorService = Executors.newFixedThreadPool(5);
    
	private native int InitDecoder(int width, int height);

	private native int UninitDecoder(int ptr);

	private native int DecoderNal(int ptr,byte[] in, int insize, byte[] out,int[] picInfo);

	static {
        System.loadLibrary("avutil-52");
        System.loadLibrary("avcodec-55");
        System.loadLibrary("swresample-0");
        System.loadLibrary("avformat-55");
        System.loadLibrary("swscale-2");
        System.loadLibrary("avfilter-4");
        System.loadLibrary("avdevice-55");
        System.loadLibrary("TestFfmpegLib");
	}
	
	int width = 1024;
	int height = 768;
	int mPtr;
	public JavaH264Decoder(DecodeSuccCallback decodeSuccCallback,int width,int height) throws H264DecoderException{
		mDecodeSuccCallback = decodeSuccCallback;
		inbuf = new byte[INBUF_SIZE + 16];
		this.width = width;
		this.height = height;
		buf = new byte[1024];
		synchronized (this) {
			mPtr = InitDecoder(width,height);
		}
		
		
	}
	
	public void toStop(){
		try{
			hasMoreNAL = false;
		    if(videoBitmap!=null)
		    	videoBitmap.recycle();
		    videoBitmap = null;
			synchronized (this) {
			    if(mPtr>0)
			    	UninitDecoder(mPtr);
			    mPtr = 0;
			}

		}catch(Exception e){
			
		}

	    System.out.println("Stop playing video.");
	}
	
	public boolean isStop(){
		return !hasMoreNAL;
	}
	public void sendStream(byte[] data) {
		sendStream(data,0,data.length);
	}
	
	boolean hasBegin = false;
	public void sendStream(byte[] data,int start,int len) {
		
		ByteBuffer dataBuffer = ByteBuffer.wrap(data);
		dataBuffer.position(start);
		dataBuffer.limit((start+len));
		int switchByte = -1;
		
		if(!hasBegin){
			// Find first NAL
			try{
				if(pauseStep<3){
					if(pauseStep == 0){
						dataPointer = 0;
						cacheRead[0] = 0xFF&dataBuffer.get();
						pauseStep++;
					}
					if(pauseStep == 1){
						cacheRead[1] = 0xFF&dataBuffer.get();
						pauseStep++;
					}
					if(pauseStep == 2){
						cacheRead[2] = 0xFF&dataBuffer.get();
						pauseStep++;
					}				
				}
				
				if(pauseStep > 2){
					while(!(
							cacheRead[0] == 0x00 &&
							cacheRead[1] == 0x00 &&
							cacheRead[2] == 0x01 
							) && hasMoreNAL) {
							cacheRead[0] = cacheRead[1];
							cacheRead[1] = cacheRead[2];
							cacheRead[2] = 0xFF&dataBuffer.get();
					}
				}
		    }catch(java.nio.BufferUnderflowException ex){
		    	return;
		    } catch(Exception e) {
		    	e.printStackTrace();
		    	return;
		    }
			if(!hasMoreNAL)
				return;
			
			// 4 first bytes always indicate NAL header
			inbuf[0]=inbuf[1]=inbuf[2]=0x00;
			inbuf[3]=0x01;
			
			pauseStep = 0;
			hasBegin = true;
		}
		
	    try {
			while(hasMoreNAL) {
				if(pauseStep<3){
					if(pauseStep == 0){
						dataPointer = 4;
						// Find next NAL
						cacheRead[0] = 0xFF&dataBuffer.get();
						pauseStep++;
					}
					if(pauseStep == 1){
						cacheRead[1] = 0xFF&dataBuffer.get();
						pauseStep++;
					}
					if(pauseStep == 2){
						cacheRead[2] = 0xFF&dataBuffer.get();
						pauseStep++;
					}
				}
				
				if(pauseStep > 2 ){
					while(!(
							cacheRead[0] == 0x00 &&
							cacheRead[1] == 0x00 &&
							cacheRead[2] == 0x01 
							) && hasMoreNAL) {
							switchByte = 0xFF&dataBuffer.get();
							inbuf[dataPointer++] = (byte)cacheRead[0];
							cacheRead[0] = cacheRead[1];
							cacheRead[1] = cacheRead[2];
							cacheRead[2] = switchByte;
					}
				}
				pauseStep=0;
				if(skipNalu)
					continue;
				
		        try {
		        	int bufferSize = width * height * 6;
					//Log.e("JavaH264Decoder","picture.imageWidth * picture.imageHeight:"+picture.imageWidth+"*"+picture.imageHeight);
					if(byteBuffer==null || byteBuffer.capacity()!=bufferSize){
						byteBuffer = ByteBuffer.allocate(bufferSize);
					}
					
					synchronized (this) {
						int picNum = 0;
						synchronized (this) {
						    if(mPtr>0)
						    	picNum = DecoderNal(mPtr,inbuf,dataPointer,byteBuffer.array(),picInfo);
						}
		
						
						if(picNum>0){
							//Log.e("JavaH264Decoder","videoBitmap.imageWidth * videoBitmap.imageHeight:"+picInfo[0]+"*"+picInfo[1]);
							byteBuffer.position(0);
							mExecutorService.execute(new Runnable(){
								public void run(){
									try{
										if(videoBitmap==null||videoBitmap.getWidth()<width||
												videoBitmap.getHeight()<height)
											videoBitmap=Bitmap.createBitmap(width, height, Config.RGB_565);
										//Log.e("JavaH264Decoder","videoBitmap.imageWidth * videoBitmap.imageHeight:"+videoBitmap.getWidth()+"*"+videoBitmap.getHeight());
										synchronized (this) {
											videoBitmap.copyPixelsFromBuffer(byteBuffer);//makeBuffer(data565, N));
										}
										if(mDecodeSuccCallback!=null)
											mDecodeSuccCallback.onDecodeSucc(JavaH264Decoder.this,videoBitmap);
									}catch(Exception e){
										
									}
								}
							});
						}

					}
		        } catch(Exception ie) {
		        	// Any exception, we should try to proceed reading next packet!
		        	//ie.printStackTrace();
		        } 
			}
	    }catch(java.nio.BufferUnderflowException ex){
	    	
	    } catch(Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	
	    }


	}
	
	public void setSkipNalu(boolean skipNalu){
		this.skipNalu = skipNalu;
	}
	public boolean getSkipNalu(){
		return this.skipNalu;
	}

}
