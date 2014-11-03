package com.yangxp.config;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.yangxp.ginput.R;

public class MainActivity extends Activity{
	public void onCreate(Bundle savedInstanceState){
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		
		((Button)findViewById(R.id.start)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent in = new Intent();
//				in.setAction(MapperBroadcastReceiver.MAPPER_BROADCASTRECEIVER_SHOW_ACTION);
//				MainActivity.this.sendBroadcast(in);
				
				Intent ins = new Intent();
				ins.setClass(MainActivity.this, MapperService.class);
				ins.putExtra("action", MapperService.MAPPER_BROADCASTRECEIVER_SHOW_ACTION);
				MainActivity.this.startService(ins);
				
//				getTaskList();


			}
			
		});
	}
	

    
    public boolean onTouchEvent(MotionEvent event){
    	return super.onTouchEvent(event);
    }  
    
    
    private void getTaskList() {
		ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		PackageManager pm = this.getPackageManager();
		try {
			List<RecentTaskInfo> list = am.getRecentTasks(64, 0);
			if(list.size()>0)
				list.remove(0);
			
			for (RecentTaskInfo ti : list) {
				Intent intent = ti.baseIntent;
				ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
				if (resolveInfo != null) {
		               System.out.println("pid="+resolveInfo.toString());
		   
		               if(resolveInfo.activityInfo.packageName.indexOf("Launcher")>-1||
		            		   resolveInfo.activityInfo.packageName.equals(this.getPackageName()))
		            	   continue;
		              
				}
			}
		} catch (SecurityException se) {
			se.printStackTrace();
		}
	}
	
	private void getProccessList(){
		ActivityManager activityManger=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();
		if(list!=null){
	           for(int i=0;i<list.size();i++){
	               ActivityManager.RunningAppProcessInfo apinfo=list.get(i);
	               
	               System.out.println("pid            "+apinfo.pid);
	               System.out.println("processName              "+apinfo.processName);
	               System.out.println("importance            "+apinfo.importance);
	               String[] pkgList=apinfo.pkgList;
	               if(apinfo.importance>ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE){

	               }
	               
	                  // Process.killProcess(apinfo.pid);
                   for(int j=0;j<pkgList.length;j++) {
                       //2.2以上是过时的,请用killBackgroundProcesses代替
                       activityManger.killBackgroundProcesses(pkgList[j]);
                   } 
	           }
		}
	}
}
