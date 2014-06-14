package com.skyeyes.base.h264;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.skyeyes.base.h264.H264Decoder.DecodeSuccCallback;
import com.twilight.h264.decoder.AVFrame;
import com.twilight.h264.decoder.AVPacket;
import com.twilight.h264.decoder.H264Decoder;
import com.twilight.h264.decoder.MpegEncContext;

public class JavaH264Decoder{
	public static final int INBUF_SIZE = 65535;
	private int[] buffer = null;
	private Bitmap videoBitmap; 
	private ByteBuffer byteBuffer;
	H264Decoder codec;
    MpegEncContext c= null;
    /* the codec gives us the frame size, in samples */
    int frame, len;
    int[] got_picture = new int[1];
    AVFrame picture;
    byte[] inbuf;
    int[] inbuf_int;
    byte[] buf;
    AVPacket avpkt;
    int dataPointer;
    int[] cacheRead = new int[3];
    boolean hasMoreNAL = true;
    int pauseStep = 0;
    boolean skipNalu;
    DecodeSuccCallback mDecodeSuccCallback;
    
    ExecutorService mExecutorService = Executors.newFixedThreadPool(5);
    
	public JavaH264Decoder(DecodeSuccCallback decodeSuccCallback) throws H264DecoderException{
		mDecodeSuccCallback = decodeSuccCallback;
		inbuf = new byte[INBUF_SIZE + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE];
		inbuf_int = new int[INBUF_SIZE + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE];
		buf = new byte[1024];
		avpkt = new AVPacket();
	    avpkt.av_init_packet();
	    /* set end of buffer to 0 (this ensures that no overreading happens for damaged mpeg streams) */
	    Arrays.fill(inbuf, INBUF_SIZE, MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE + INBUF_SIZE, (byte)0);
	    /* find the mpeg1 video decoder */
	    codec = new H264Decoder();
	    if (codec == null) {
	    	throw new H264DecoderException("codec not found\n");
	    }
	    c= MpegEncContext.avcodec_alloc_context();
	    picture= AVFrame.avcodec_alloc_frame();
	    
	    if((codec.capabilities & H264Decoder.CODEC_CAP_TRUNCATED)!=0)
	        c.flags |= MpegEncContext.CODEC_FLAG_TRUNCATED; /* we do not send complete frames */

	    /* For some codecs, such as msmpeg4 and mpeg4, width and height
	       MUST be initialized there because this information is not
	       available in the bitstream. */
	    /* open it */
	    if (c.avcodec_open(codec) < 0) {
	    	throw new H264DecoderException("could not open codec\n");
	    }
	    
		// 4 first bytes always indicate NAL header
		inbuf_int[0]=inbuf_int[1]=inbuf_int[2]=0x00;
		inbuf_int[3]=0x01;
	}
	
	public void toStop(){
		try{
			hasMoreNAL = false;
		    c.avcodec_close();
		    c = null;
		    picture = null;
		    if(videoBitmap!=null)
		    	videoBitmap.recycle();
		    videoBitmap = null;
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
			inbuf_int[0]=inbuf_int[1]=inbuf_int[2]=0x00;
			inbuf_int[3]=0x01;
			
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
							inbuf_int[dataPointer++] = cacheRead[0];
							cacheRead[0] = cacheRead[1];
							cacheRead[1] = cacheRead[2];
							cacheRead[2] = switchByte;
					}
				}
				pauseStep=0;
				if(skipNalu)
					continue;
				

				avpkt.size = dataPointer;
		        avpkt.data_base = inbuf_int;
		        avpkt.data_offset = 0;
		        try {
			        while (avpkt.size > 0) {
			            len = c.avcodec_decode_video2(picture, got_picture, avpkt);
			            if (len < 0) {
			                //System.out.println("Error while decoding frame "+ frame);
			                // Discard current packet and proceed to next packet
			                break;
			            } // if
			            if (got_picture[0]!=0) {
			            	picture = c.priv_data.displayPicture;
		
							int bufferSize = picture.imageWidth * picture.imageHeight;
							//Log.e("JavaH264Decoder","picture.imageWidth * picture.imageHeight:"+picture.imageWidth+"*"+picture.imageHeight);
							if(byteBuffer==null || byteBuffer.capacity()!=bufferSize*4){
								byteBuffer = ByteBuffer.allocate(bufferSize*32);
							}
							
							synchronized (this) {
								byteBuffer.position(0);
								YUV2RGB(picture,byteBuffer);
								//Log.e("JavaH264Decoder",byteBuffer.capacity()+":"+byteBuffer.position()+":"+byteBuffer.capacity()/byteBuffer.position());
								byteBuffer.position(0);
							}
							mExecutorService.execute(new Runnable(){
								public void run(){

									try{
										if(videoBitmap==null||videoBitmap.getWidth()<picture.imageWidth||
												videoBitmap.getHeight()<picture.imageHeight)
											videoBitmap=Bitmap.createBitmap(picture.imageWidth, picture.imageHeight, Config.ARGB_8888);
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
			            avpkt.size -= len;
			            avpkt.data_offset += len;
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
	
	public static void YUV2RGB(AVFrame f, ByteBuffer rgb) {
		rgb.clear();
		int[] luma = f.data_base[0];
		int[] cb = f.data_base[1];
		int[] cr = f.data_base[2];
		int stride = f.linesize[0];
		int strideChroma = f.linesize[1];

		for (int y = 0; y < f.imageHeight; y++) {
			int lineOffLuma = y * stride;
			int lineOffChroma = (y >> 1) * strideChroma;

			for (int x = 0; x < f.imageWidth; x++) {
				int c = luma[lineOffLuma + x] - 16;
				int d = cb[lineOffChroma + (x >> 1)] - 128;
				int e = cr[lineOffChroma + (x >> 1)] - 128;

				int red = (298 * c + 409 * e + 128) >> 8;
				red = red < 0 ? 0 : (red > 255 ? 255 : red);
				int green = (298 * c - 100 * d - 208 * e + 128) >> 8;
				green = green < 0 ? 0 : (green > 255 ? 255 : green);
				int blue = (298 * c + 516 * d + 128) >> 8;
				blue = blue < 0 ? 0 : (blue > 255 ? 255 : blue);
				int alpha = 255;
				
				
				
				rgb.put((byte)(red & 0xff));
				rgb.put((byte)(green & 0xff));
				rgb.put((byte)(blue & 0xff));
				rgb.put((byte)255);
				
			}
		}
	}
	
	public void setSkipNalu(boolean skipNalu){
		this.skipNalu = skipNalu;
	}
	public boolean getSkipNalu(){
		return this.skipNalu;
	}

}
