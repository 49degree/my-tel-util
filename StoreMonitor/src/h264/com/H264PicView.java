package h264.com;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.util.Log;
import android.view.Display;

import com.skyeyes.base.util.VideoClarity;

public class H264PicView{
	private DecodeSuccCallback mDecodeSuccCallback;
	
	private final static String tag="H264PicView";
	private static H264PicView sInstance;
	private static Display mDisplay = null;
	
	private int videoViewStartX = 0;
	private int videoViewStartY = 0;
	private int videoViewEndX = 0;
	private int videoViewEndY = 0;
	
	
    Bitmap  mBitQQ  = null;   
    Paint   mPaint = null;   
    Bitmap  mSCBitmap = null;
    
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
	
	H264MediaPlayer mH264MediaPlayer;
	
    public static void setDisplay(Display display){
    	mDisplay = display;
    }

	
	public H264PicView(DecodeSuccCallback decodeSuccCallback) {
        this(352,288,decodeSuccCallback);      	
    }    
    
    public H264PicView(int width,int height,DecodeSuccCallback decodeSuccCallback){
    	mDecodeSuccCallback = decodeSuccCallback;
    	mH264MediaPlayer = new H264MediaPlayer();
    	this.setDisplaySize(width, height);
    	this.init();
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
        	
        	if(playWidth>=mDisplay.getWidth()){
        		videoViewStartX = 0;
        		videoViewEndX = playWidth;
        	}else{
        		videoViewStartX = (mDisplay.getWidth()-playWidth)/2;
        		videoViewEndX = videoViewStartX+playWidth;
        	}
        	
        	if(playHeight>=mDisplay.getWidth()){
        		videoViewStartY = 0;
        		videoViewEndY = playHeight;
        	}else{
        		videoViewStartY = (mDisplay.getHeight()-playHeight)/2;
        		videoViewEndY = videoViewStartY+playHeight;
        	}
        }
        
    }
    
    public Bitmap getImage()
    {
    	return VideoBit;
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
    	SockBufUsed =0;
    	bytesRead=SockBuf.length;
		while(bytesRead-SockBufUsed>0)
		{
			nalLen = MergeBuffer(NalBuf, NalBufUsed, SockBuf, SockBufUsed, bytesRead-SockBufUsed);
			Log.e(tag, "SockBufUsed:"+SockBufUsed);
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
//					05-25 10:57:42.059: E/H264PicView(13367): SockBufUsed:3219
//					05-25 10:57:42.059: E/H264PicView(13367): NalBuf:409800:848:202752
					
//					05-25 11:14:43.726: E/H264PicView(18424): SockBufUsed:24147
//					05-25 11:14:43.726: E/H264PicView(18424): NalBuf:409800:742:202752


					Log.e(tag, "NalBuf:"+NalBuf.length+":"+(NalBufUsed-4)+":"+mPixel.length);
					iTemp=mH264MediaPlayer.DecoderNal(NalBuf, NalBufUsed-4, mPixel);   
					
		            if(iTemp>0){
		                buffer.position(0);
		                VideoBit.copyPixelsFromBuffer(buffer);//makeBuffer(data565, N));
		                Log.e(tag, "VideoBit:"+VideoBit.getWidth()+":"+VideoBit.getHeight());
		                
		                if(mDecodeSuccCallback!=null)
		                	mDecodeSuccCallback.onDecodeSucc(VideoBit);
		        		System.gc();
		        		mH264MediaPlayer.UninitDecoder();
		                return;
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
		System.gc();
		mH264MediaPlayer.UninitDecoder();
    	
    }
    
   
    public void init(){
    	//视频缓冲区
    	 NalBuf = new byte[40980];
    	 setDisplay();
    	 mH264MediaPlayer.InitDecoder(playWidth, playHeight);
    }
  
    public interface DecodeSuccCallback{
    	public void onDecodeSucc(Bitmap bitmap);
    }
    
}



