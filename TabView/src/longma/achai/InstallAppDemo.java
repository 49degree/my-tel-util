package longma.achai;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import longma.achai.test.R;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class InstallAppDemo extends Activity implements OnClickListener{
	private static String logPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "guanri"+File.separator;

	private static String TAG = "InstallAppDemo";
	Button btn_install = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_test);
        btn_install = (Button)this.findViewById(R.id.btn_install);

        btn_install.setOnClickListener(this);
        createMessageHandleThread();
    }

    private Handler handler;
    private void createMessageHandleThread(){
        //need start a thread to raise looper, otherwise it will be blocked
            Thread t = new Thread() {
                public void run() {
                    Log.i( TAG,"Creating handler ..." );
                    Looper.prepare();
                    handler = new Handler(){
                        public void handleMessage(Message msg) {    
                        }
                    };
                    Looper.loop();
                    Log.i( TAG, "Looper thread ends" );
                }

            };
            t.start();

    }

    public void onDestroy(){
    	stop = true;
    	Log.e(TAG, "onDestroy");
    	super.onDestroy();
    }
    int times = 0;
    boolean stop = false;
    @Override
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.btn_install:
    		startApp(this,"com.testGrid");
    		
    		//打开屏幕并解锁
    		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
    		final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | 
    				PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
    		KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
    		final KeyguardLock mKeyguardLock  = mKeyguardManager.newKeyguardLock("");  
    		
			WindowManager manage = getWindowManager();
			Display display = manage.getDefaultDisplay();
			final int screenHeight = display.getHeight();
			final int screenWidth = display.getWidth();
			Thread t = new Thread(){
				public void run(){
					times = 0;
		    		while (times++ < 50&&!stop) {
		        		handler.post(new Runnable() {
		        			public void run() {
		    
		        				
		    					float x = (float)Math.random() * screenWidth;
		    					float y = (float)Math.random() * screenHeight;
		    					Log.e(TAG, times+"Instrumentation:"+x+":"+y );
		    					
		        				if(times>10&&times<20){
		        					wl.acquire();
		        					mKeyguardLock.disableKeyguard();
		        				}
		    					if(y<30)
		    						y+=30;
		    					try{
			        	    		Instrumentation inst=new Instrumentation();
			        	    		inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN, x, y, 0));
			        	    		inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_UP, x, y, 0));
			        	    		
		    					}catch(Exception e){
		    						e.printStackTrace();
		    					}

		        			}
		        		});
		    			try{
		    				Thread.sleep(500);
		    			}catch(Exception e){
		    				
		    			}
		    		}
		    		
		    		wl.release();
				}
			};
			t.setDaemon(true);
			t.start();

    		break;
    	default:
    		break;
    	}
    	
    }
    
    
	/**
	 * 启动应用
	 * @param context
	 * @param packageName
	 */
	public void startApp(Context context, String packageName) {
		try {
			/*
			 * 都知道，Context中有一个startActivity方法，
			 * Activity继承自Context，重载了startActivity方法。
			 * 如果使用Activity的startActivity方法，不会有任何限制，
			 * 而如果使用Context的startActivity方法的话，就需要开启一个新的task，
			 * 遇到上面那个异常的，都是因为使用了Context的startActivity方法。解决办法是，加一个flag。 
			 */
			PackageManager packageManager = context.getPackageManager();
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			intent = packageManager
					.getLaunchIntentForPackage(packageName);
			context.startActivity(intent);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 启动应用
	 * @param context
	 * @param packageName
	 */
	public static  void startApp1(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				ComponentName cn = new ComponentName(packageName, className);
				intent.setComponent(cn);
				context.startActivity(intent);
			}
		} catch (Exception e) {

		}
	}
	
	public String do_exec(String cmd) {  
	     String s = "/n";  
	     try {  
	         Process p = Runtime.getRuntime().exec(cmd);  
	         BufferedReader in = new BufferedReader(  
	                             new InputStreamReader(p.getInputStream()));  
	         String line = null;  
	         while ((line = in.readLine()) != null) {  
	             s += line + "/n";
	             Log.e("do_exec",line);
	         }  
	     } catch (IOException e) {  
	         // TODO Auto-generated catch block   
	         e.printStackTrace();  
	     }  
	       
	     return cmd;      
	 }

}