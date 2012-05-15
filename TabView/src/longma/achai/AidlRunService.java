package longma.achai;

import java.util.List;

import android.app.Instrumentation;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;


public class AidlRunService extends Service{
	private static String TAG = "AidlRunService";
    @Override
    public void onCreate(){
    	super.onCreate();
    	Log.e(TAG,"onCreate~~~~~~~~~");
    }
    @Override
    public void onStart(Intent intent, int startId){
    	Log.e(TAG,"onStart~~~~~~~~~");
    	super.onStart(intent, startId);
    	//createMessageHandleThread();
    }
    @Override
    public void onDestroy(){
    	stop = true;
    	Log.e(TAG, "onDestroy");
    	super.onDestroy();
    }

    boolean stop = false;
    private Handler handler;
    int times = 0;
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
    	
		startApp(this,"longma.achai");

		try{
			Thread.sleep(1500);
		}catch(Exception e){
			
		}
		
		final int screenHeight = 960;
		final int screenWidth = 550;
		t = new Thread(){
			public void run(){
				times = 0;
	    		while (times++ < 100&&!stop) {
	    			if(handler!=null){
		        		handler.post(new Runnable() {
		        			public void run() {
		    					float x = (float)Math.random() * screenWidth;
		    					float y = (float)Math.random() * screenHeight;
		    					Log.e(TAG,times+ "Instrumentation:"+x+":"+y );
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
	    			}
	    			try{
	    				Thread.sleep(500);
	    			}catch(Exception e){
	    				
	    			}
	    		}
			}
		};
		t.setDaemon(true);
		t.start();

    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
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
}
