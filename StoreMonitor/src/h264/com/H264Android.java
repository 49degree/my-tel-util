package h264.com;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public abstract class H264Android extends Activity implements MediaPlayCallback {
	protected LinearLayout main;
	protected Display display;
	protected static H264MediaPlayer vv;
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
        H264MediaPlayer.setDisplay(display);
        
    	main=new LinearLayout(this);
        main.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        main.setOrientation(LinearLayout.VERTICAL);
       
        vv = H264MediaPlayer.getInstance(this,display.getWidth(),display.getHeight());
        vv.setCallback(this);
        vv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        main.addView(vv);
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
