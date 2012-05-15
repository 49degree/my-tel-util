package longma.achai;

import jackpal.androidterm.Exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import longma.achai.test.R;
import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

    boolean stop = false;
    @Override
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.btn_install:

			//Context friendContext = this.createPackageContext("",Context.CONTEXT_IGNORE_SECURITY);
    		
//    		startInstrumentation(new ComponentName("longma.achai.test", "android.test.InstrumentationTestRunner"), null, null);
    		//startApp(this,"longma.achai");
//    		Intent i = new Intent();
//    		i.setClassName(this.getPackageName(), "longma.achai.TabViewActivity");
//    		this.startActivity(i);
//			try{
//				Thread.sleep(500);
//			}catch(Exception e){
//				
//			}
			
    		/*
			WindowManager manage = getWindowManager();
			Display display = manage.getDefaultDisplay();
			final int screenHeight = display.getHeight();
			final int screenWidth = display.getWidth();
			
			Log.e(TAG, "Instrumentation:"+screenWidth+":"+screenHeight );
			Thread t = new Thread(){
				public void run(){
					int times = 0;
		    		while (times++ < 1000&&!stop) {
		    			
		        		handler.post(new Runnable() {
		        			public void run() {
		    					float x = (float)Math.random() * screenWidth;
		    					float y = (float)Math.random() * screenHeight;
		    					Log.e(TAG, "Instrumentation:"+x+":"+y );
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
				}
			};
			t.setDaemon(true);
			t.start();
			*/

    		try {
    			Thread.sleep(1000);
				/* Missing read/write permission, trying to chmod the file */
				//Process su;
				String cmd = "monkey -p longma.achai -v --pct-touch 100 --throttle 300 50";
//				cmd="dalvikvm -cp /sdcard/Foo.jar Foo";
//				do_exec(cmd);
			
				cmd = "export CLASSPATH=/system/framework/monkey.jar";
				do_exec(cmd);
				Thread.sleep(1000);
				cmd = "app_process /system/bin com.android.commands.monkey.Monkey -p longma.achai -v --pct-touch 100 --throttle 300 50";
				do_exec(cmd);

			} catch (Exception e) {
				e.printStackTrace();
				//throw new SecurityException();
			}
    		//new Thread(new TestRunner(this)).start();
    		break;
    	default:
    		break;
    	}
    	
    }
    
	/**
	 * Æô¶¯Ó¦ÓÃ
	 * @param context
	 * @param packageName
	 */
	public static  void startApp(Context context, String packageName) {
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