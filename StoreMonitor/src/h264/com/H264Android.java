package h264.com;



import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyeyes.base.util.VideoClarity;
import com.skyeyes.storemonitor.R;

public abstract class H264Android extends Activity implements MediaPlayCallback {
	protected FrameLayout main;
	protected LinearLayout notify;
	protected TextView notifyText;
	protected Display display;
	protected static H264VideoPlayer vv;
	private final static String tag="H264Android";
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏 
        requestWindowFeature(Window.FEATURE_NO_TITLE);    //全屏            
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,                
                                    WindowManager.LayoutParams. FLAG_FULLSCREEN); 
        
        WindowManager windowManager = getWindowManager();
        display = windowManager.getDefaultDisplay();
        H264VideoPlayer.setDisplay(display);
        
    	main=new FrameLayout(this);
        main.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
       
        vv = H264VideoPlayer.getInstance(this,VideoClarity.instance().getWith(),VideoClarity.instance().getHeight());
        vv.setCallback(this);
        vv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        main.addView(vv);
        
        notify = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.app_video_play_notify, null);
        notifyText = (TextView)notify.findViewById(R.id.play_video_notify_tv);
        main.addView(notify);
        
        setContentView(main);
        
    }
    public void h264Play()
    {
    	vv.play();
    }
    public void h264Stop()
    {
    	vv.stop();
    }
    public void sendStream(byte[] inbyte)
    {
    	Log.e("VideoPlayActivity", "sendStream================");
    	
    	vv.sendStream(inbyte);
    }
    
    public Bitmap getImage()
    {
    	return vv.getImage();
    }
    
    public void setMaxInterval(long maxInterval) {
    	vv.setMaxInterval(maxInterval);
	}
    
}
