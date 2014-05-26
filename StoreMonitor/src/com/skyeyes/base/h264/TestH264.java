package com.skyeyes.base.h264;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.skyeyes.base.h264.JavaH264Decoder.DecodeSuccCallback;

public class TestH264 extends Activity {
	H264View mH264View = null;
	RelativeLayout root_layout;
	
	JavaH264Decoder mJavaH264Decoder = null;
	Bitmap videoBitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		root_layout = new RelativeLayout(this);
		
		
		setContentView(root_layout);
		
		mH264View = new H264View(this);
		root_layout.addView(mH264View);
		
		try {
			mJavaH264Decoder = new JavaH264Decoder(new DecodeSuccCallback(){
				@Override
				public void onDecodeSucc(final JavaH264Decoder decoder,Bitmap bitmap) {
					// TODO Auto-generated method stub
					videoBitmap = Bitmap.createBitmap(bitmap);
					if(mH264View!=null){
						runOnUiThread(new Runnable(){
							public void run(){
								mH264View.postInvalidate();
								decoder.toStop();
							}
						});
					}
				}
			});
		} catch (H264DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public void onResume(){
		super.onResume();
		Log.e(this.getClass().getSimpleName(), "onResume=======================");
		if(mJavaH264Decoder!=null){
			new Thread(){
				public void run(){
					try {
						InputStream in = getResources().getAssets().open("0.jpg");
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

		

//		new Thread(){
//			public void run(){
//				try {
//					playFile(getResources().getAssets().open("video.data"));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}.start();
	}


	
	public class H264View extends View {

		public H264View(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		@Override 
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas); 
	        
	        
	          //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高  
	        if(videoBitmap!=null && videoBitmap!=null){
		        RectF rectF = new RectF(0, 0 , videoBitmap.getWidth(), videoBitmap.getHeight()); 
	        	canvas.drawBitmap(videoBitmap, null, rectF, null);
	        }
	        

	    }
	}
	
	
	
	
//	public static final int INBUF_SIZE = 65535;
//	private int[] buffer = null;
//	private Bitmap videoBitmap; 
//	private ByteBuffer byteBuffer;
//	
//	public boolean playFile(InputStream fin) {
//		Log.e(this.getClass().getSimpleName(), "playFile=======================");
//	    H264Decoder codec;
//	    MpegEncContext c= null;
//	    int frame, len;
//	    int[] got_picture = new int[1];
//	    AVFrame picture;
//	    //uint8_t inbuf[INBUF_SIZE + H264Context.FF_INPUT_BUFFER_PADDING_SIZE];
//	    byte[] inbuf = new byte[INBUF_SIZE + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE];
//	    int[] inbuf_int = new int[INBUF_SIZE + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE];
//	    //char buf[1024];
//	    byte[] buf = new byte[1024];
//	    AVPacket avpkt = new AVPacket();
//
//	    avpkt.av_init_packet();
//
//	    /* set end of buffer to 0 (this ensures that no overreading happens for damaged mpeg streams) */
//	    Arrays.fill(inbuf, INBUF_SIZE, MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE + INBUF_SIZE, (byte)0);
//
//	    System.out.println("Video decoding\n");
//
//	    /* find the mpeg1 video decoder */
//	    codec = new H264Decoder();
//	    if (codec == null) {
//	    	System.out.println("codec not found\n");
//	        System.exit(1);
//	    } // if
//
//	    c= MpegEncContext.avcodec_alloc_context();
//	    picture= AVFrame.avcodec_alloc_frame();
//
//	    if((codec.capabilities & H264Decoder.CODEC_CAP_TRUNCATED)!=0)
//	        c.flags |= MpegEncContext.CODEC_FLAG_TRUNCATED; /* we do not send complete frames */
//
//	    /* For some codecs, such as msmpeg4 and mpeg4, width and height
//	       MUST be initialized there because this information is not
//	       available in the bitstream. */
//
//	    /* open it */
//	    if (c.avcodec_open(codec) < 0) {
//	    	System.out.println("could not open codec\n");
//	        System.exit(1);
//	    }
//
//	    try {
//		    /* the codec gives us the frame size, in samples */
//		    
//	
//		    frame = 0;
//		    int dataPointer;
//
//		    // avpkt must contain exactly 1 NAL Unit in order for decoder to decode correctly.
//	    	// thus we must read until we get next NAL header before sending it to decoder.
//			// Find 1st NAL
//			int[] cacheRead = new int[3];
//			cacheRead[0] = fin.read();
//			cacheRead[1] = fin.read();
//			cacheRead[2] = fin.read();
//			
//			while(!(
//					cacheRead[0] == 0x00 &&
//					cacheRead[1] == 0x00 &&
//					cacheRead[2] == 0x01 
//					)) {
//				 cacheRead[0] = cacheRead[1];
//				 cacheRead[1] = cacheRead[2];
//				 cacheRead[2] = fin.read();
//			} // while
//	    	
//			boolean hasMoreNAL = true;
//			
//			// 4 first bytes always indicate NAL header
//			inbuf_int[0]=inbuf_int[1]=inbuf_int[2]=0x00;
//			inbuf_int[3]=0x01;
//			
//			while(hasMoreNAL) {
//				dataPointer = 4;
//				// Find next NAL
//				cacheRead[0] = fin.read();
//				while(cacheRead[0]==-1){
//					Thread.sleep(50);
//					cacheRead[0] = fin.read();
//				}
//				
//				
//				cacheRead[1] = fin.read();
//				
//				while(cacheRead[1]==-1){
//					Thread.sleep(50);
//					cacheRead[1] = fin.read();
//				}
//
//				cacheRead[2] = fin.read();
//				
//				while(cacheRead[2]==-1){
//					Thread.sleep(50);
//					cacheRead[2] = fin.read();
//				}
//				while(!(
//						cacheRead[0] == 0x00 &&
//						cacheRead[1] == 0x00 &&
//						cacheRead[2] == 0x01 
//						) && hasMoreNAL) {
//					 inbuf_int[dataPointer++] = cacheRead[0];
//					 cacheRead[0] = cacheRead[1];
//					 cacheRead[1] = cacheRead[2];
//					 cacheRead[2] = fin.read();
//					while(cacheRead[2]==-1){
//						Thread.sleep(50);
//						cacheRead[2] = fin.read();
//					}
//				} // while
//
//				avpkt.size = dataPointer;
//
//		        avpkt.data_base = inbuf_int;
//		        avpkt.data_offset = 0;
//
//		        try {
//			        while (avpkt.size > 0) {
//			            len = c.avcodec_decode_video2(picture, got_picture, avpkt);
//			            if (len < 0) {
//			                System.out.println("Error while decoding frame "+ frame);
//			                // Discard current packet and proceed to next packet
//			                break;
//			            } // if
//			            System.out.println("got_picture[0]!=0"+(got_picture[0]!=0));
//			            if (got_picture[0]!=0) {
//			            	picture = c.priv_data.displayPicture;
//		
//							int bufferSize = picture.imageWidth * picture.imageHeight;
//							
//	
//							if(byteBuffer==null || byteBuffer.capacity()!=bufferSize*4){
//								byteBuffer = ByteBuffer.allocate(bufferSize*32);
//							}
//							YUV2RGB(picture,byteBuffer);
//							
//							synchronized (this) {
//								if(videoBitmap==null||videoBitmap.getWidth()<picture.imageWidth||
//										videoBitmap.getHeight()<picture.imageHeight)
//									videoBitmap=Bitmap.createBitmap(picture.imageWidth, picture.imageWidth, Config.ARGB_8888);
//								videoBitmap.copyPixelsFromBuffer(byteBuffer);//makeBuffer(data565, N));
//								byteBuffer.position(0);
//								if(mH264View!=null){
//									this.runOnUiThread(new Runnable(){
//										public void run(){
//											mH264View.postInvalidate();
//										}
//									});
//									
//								}
//							}
//							//return true;
//							
////							if (buffer == null || bufferSize != buffer.length) {
////								buffer = new int[bufferSize];
////							}
////							YUV2RGB(picture, buffer);
////							displayPanel.lastFrame = displayPanel.createImage(new MemoryImageSource(picture.imageWidth
////									, picture.imageHeight, buffer, 0, picture.imageWidth));
////							displayPanel.invalidate();
////							displayPanel.updateUI();
////							writePic(buffer);
//							
//							
//			            }
//			            avpkt.size -= len;
//			            avpkt.data_offset += len;
//			        }
//		        } catch(Exception ie) {
//		        	// Any exception, we should try to proceed reading next packet!
//		        	ie.printStackTrace();
//		        } // try
//		        
////		        if(!hasMoreNAL)
////		        	Thread.sleep(50);
////		        
////		        hasMoreNAL = true;
//				
//			} // while
//					
//	
//	    } catch(Exception e) {
//	    	e.printStackTrace();
//	    } finally {
//	    	try { 
//	    		fin.close(); 
//	    	} catch(Exception ee) {}
//	    }
//
//	    c.avcodec_close();
//	    c = null;
//	    picture = null;
//	    System.out.println("Stop playing video.");
//	    
//	    return true;
//	}
//	
//	public static void YUV2RGB(AVFrame f, ByteBuffer rgb) {
//		rgb.clear();
//		int[] luma = f.data_base[0];
//		int[] cb = f.data_base[1];
//		int[] cr = f.data_base[2];
//		int stride = f.linesize[0];
//		int strideChroma = f.linesize[1];
//
//		for (int y = 0; y < f.imageHeight; y++) {
//			int lineOffLuma = y * stride;
//			int lineOffChroma = (y >> 1) * strideChroma;
//
//			for (int x = 0; x < f.imageWidth; x++) {
//				int c = luma[lineOffLuma + x] - 16;
//				int d = cb[lineOffChroma + (x >> 1)] - 128;
//				int e = cr[lineOffChroma + (x >> 1)] - 128;
//
//				int red = (298 * c + 409 * e + 128) >> 8;
//				red = red < 0 ? 0 : (red > 255 ? 255 : red);
//				int green = (298 * c - 100 * d - 208 * e + 128) >> 8;
//				green = green < 0 ? 0 : (green > 255 ? 255 : green);
//				int blue = (298 * c + 516 * d + 128) >> 8;
//				blue = blue < 0 ? 0 : (blue > 255 ? 255 : blue);
//				int alpha = 255;
//				
//				
//				
//				rgb.put((byte)(red & 0xff));
//				rgb.put((byte)(green & 0xff));
//				rgb.put((byte)(blue & 0xff));
//				rgb.put((byte)255);
//				
//			}
//		}
//	}
	
	
}
