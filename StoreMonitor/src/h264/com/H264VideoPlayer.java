package h264.com;

import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.skyeyes.base.util.VideoClarity;

public class H264VideoPlayer extends View {
	private Context context;
	private final static String tag="H264VideoPlayer";
	private static H264VideoPlayer sInstance;
	private static Display mDisplay = null;
	
	private int videoViewStartX = 0;
	private int videoViewStartY = 0;
	private int videoViewEndX = 0;
	private int videoViewEndY = 0;
	
	private MediaPlayCallback callbackobj;
	private int frameCount=0;
	private long oldTime=0;
	private long currentTime=0;
	private long maxInterval=10;
	
    Bitmap  mBitQQ  = null;   
    Paint   mPaint = null;   
    Bitmap  mSCBitmap = null;   
//    int playWidth = VideoClarity.instance().getWith();  //此处设定不同的分辨率
//    int playHeight  = VideoClarity.instance().getHeight();
    
    int playWidth = VideoClarity.instance().getWith();  //此处设定不同的分辨率
    int playHeight = VideoClarity.instance().getHeight();

    byte [] mPixel ;
    
    ByteBuffer buffer;
	Bitmap VideoBit;           
   
	int mTrans=0x0F0F0F0F;
	
	
	int iTemp=0;
	int nalLen;
	boolean bFirst=true;
	boolean bFindPPS=true;
	int bytesRead=0;    	
	int NalBufUsed=0;
	int SockBufUsed=0;
	int escapeMax=5;
	int escapeLen=0;
	byte [] NalBuf;

	
	H264MediaPlayer mH264MediaPlayer = new H264MediaPlayer();
    
    public static void setDisplay(Display display){
    	mDisplay = display;
    }
    
    public static H264VideoPlayer getInstance(Context context)
    {
    	return getInstance(context,352,288);
    }
    public static H264VideoPlayer getInstance(Context context,int width,int height)
    {
    	synchronized (H264VideoPlayer.class) {
    		sInstance=new H264VideoPlayer(context,width,height);
    		sInstance.init();
		}
		return sInstance;
    	
    }
	
	public H264VideoPlayer(Context context) {
        this(context, 352,288);      	
    }    
    
    public H264VideoPlayer(Context context,int width,int height)
    {
    	super(context);
    	this.context=context;
    	this.setDisplaySize(width, height);
    	this.init();
    }
    
    public void setCallback(MediaPlayCallback obj)
    {
    	this.callbackobj=obj;
    }
    
    
	public void setMaxInterval(long maxInterval) {
		this.maxInterval = maxInterval;
	}
    
    public void setDisplaySize(int width,int height)
    {
    	playWidth=width;
    	playHeight=height;
    }
    
    private void setDisplay()
    {
    	mPixel = new byte[playWidth*playHeight*2];    	
    	buffer=ByteBuffer.wrap( mPixel );
    	Log.e(tag, "this.width, this.height:"+this.playWidth+":"+this.playHeight);
    	VideoBit=Bitmap.createBitmap(this.playWidth, this.playHeight, Config.RGB_565);
    	int i = mPixel.length;    	
        for(i=0; i<mPixel.length; i++)
        {
        	mPixel[i]=(byte)0x00;
        }

        
        if(mDisplay != null){
    		videoViewStartY = 0;
    		videoViewEndY = mDisplay.getHeight();
    		
    		float zoom = (videoViewEndY*1.0f)/playHeight;
    		int tmepWidth =  (int)(zoom*playWidth);
    		if(mDisplay.getWidth() - tmepWidth>0){
    			videoViewStartX = (mDisplay.getWidth() - tmepWidth) / 2;
    			videoViewEndX = videoViewStartX + tmepWidth;
    		}else{
    			videoViewStartX = 0;
    			videoViewEndX = mDisplay.getWidth();
    			
        		zoom = (videoViewEndX*1.0f)/playWidth;
        		int tmepHeight =  (int)zoom*playHeight;
        		
        		videoViewStartY = (mDisplay.getHeight()-tmepHeight)/2;
        		videoViewEndY = videoViewStartY+tmepHeight;
    		}

        	
//        	if(playWidth>=mDisplay.getWidth()){
//        		videoViewStartX = 0;
//        		videoViewEndX = mDisplay.getWidth();
//        	}else{
//        		videoViewStartX = (mDisplay.getWidth()-playWidth)/2;
//        		videoViewEndX = videoViewStartX+playWidth;
//        	}
//        	
//        	if(playHeight>=mDisplay.getHeight()){
//        		videoViewStartY = 0;
//        		videoViewEndY = mDisplay.getHeight();
//        	}else{
//        		videoViewStartY = (mDisplay.getHeight()-playHeight)/2;
//        		videoViewEndY = videoViewStartY+playHeight;
//        	}
        }
        
    }
    
    public Bitmap getImage()
    {
    	return VideoBit;
    }
    
    @SuppressLint("DrawAllocation")
	@Override 
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);           
    	//Bitmap tmpBit = Bitmap.createBitmap(mPixel, 320, 480, Bitmap.Config.RGB_565);//.ARGB_8888);

        RectF rectF = new RectF(videoViewStartX, videoViewStartY, videoViewEndX, videoViewEndY); 
        buffer.position(0);
        VideoBit.copyPixelsFromBuffer(buffer);//makeBuffer(data565, N));
    	
          //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高  
        canvas.drawBitmap(VideoBit, null, rectF, null);
       
    }
    
    int MergeBuffer(byte[] NalBuf, int NalBufUsed, byte[] SockBuf, int SockBufUsed, int SockRemain)
    {
    	int  i=0;
    	byte Temp;

    	for(i=0; i<SockRemain; i++)
    	{
    		Temp  =SockBuf[i+SockBufUsed];
    		NalBuf[i+NalBufUsed]=Temp;

    		mTrans <<= 8;
    		mTrans  |= Temp;

    		if(mTrans == 1) // 找到一个开始字 
    		{
    			i++;
    			break;
    		}	
    	}

    	return i;
    }
    
    
   
    
    public void sendStream(byte[] SockBuf)
    {
    	Log.e(tag, "sendStream:"+SockBuf.length);
    	SockBufUsed =0;
    	bytesRead=SockBuf.length;
		while(bytesRead-SockBufUsed>0)
		{
			nalLen = MergeBuffer(NalBuf, NalBufUsed, SockBuf, SockBufUsed, bytesRead-SockBufUsed);
					
			NalBufUsed += nalLen;
			SockBufUsed += nalLen;
			while(mTrans == 1)
			{
				mTrans = 0xFFFFFFFF;
				if(bFirst==true) // the first start flag
				{
					bFirst = false;
				}
				else // a complete NAL data, include 0x00000001 trail.
				{
					if(bFindPPS==true) // true
					{
						if( (NalBuf[4]&0x1F) == 7 )
						{
							bFindPPS = false;
						}
						else
						{
			   				NalBuf[0]=0;
		    				NalBuf[1]=0;
		    				NalBuf[2]=0;
		    				NalBuf[3]=1;
		    				
		    				NalBufUsed=4;
		    				
							break;
						}
					}
					
					iTemp=mH264MediaPlayer.DecoderNal(NalBuf, NalBufUsed-4, mPixel);   
					Log.d(tag,"iTemp="+iTemp);
		            frameCount++;
		            callbackobj.reviceFrame(frameCount+"");
		            if(iTemp>0)
		            {
		            	currentTime=System.currentTimeMillis();
		            	if(oldTime!=0)
		            	{
		            		long tempTimeLen=currentTime-oldTime;
		            		if(tempTimeLen<maxInterval)
		            		{
		            			Log.d(tag,"tempTimeLen="+tempTimeLen);
		            			try {
									Thread.sleep(maxInterval-tempTimeLen);
									
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}finally{}
		            		}
		            	}
		            	postInvalidate();  //使用postInvalidate可以直接在线程中更新界面    // postInvalidate();
		            	oldTime=currentTime;
		            }
		            	

				}
				
				escapeLen++;

				NalBuf[0]=0;
				NalBuf[1]=0;
				NalBuf[2]=0;
				NalBuf[3]=1;
				
				NalBufUsed=4;

				System.gc();
			}		
		} 
		
//		if(isDrawingCacheEnabled())
//	    	UninitDecoder();
    	
    }
    
   
    public void init()
    {
    	//视频缓冲区
    	 NalBuf = new byte[409800];
    	 setDisplay();
    	 mH264MediaPlayer.InitDecoder(playWidth, playHeight);
    }
    
    
    public void play()
    {
    	setDisplay();
    }
    
    
    public void stop()
    {
    	setDrawingCacheEnabled(true);
    	System.gc();
    }
    
  
}



