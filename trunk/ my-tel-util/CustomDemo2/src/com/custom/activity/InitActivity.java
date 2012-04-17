package com.custom.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.custom.utils.Constant;
import com.custom.view.InitView;

public class InitActivity  extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = "InitActivity";
	InitView v = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏标题栏

		v = new InitView(this,Constant.path,Constant.fistFoldDepth);
		
		setContentView(v);
	}
	
	@Override
	public void onRestart() {
		Log.e(TAG, "onStart");
		super.onRestart();
		v.onRestart();
	}
	
	@Override
	public void onStart() {
		Log.e(TAG, "onStart");
		super.onStart();
		v.onStart();
	}
    @Override
    public void onResume(){
    	Log.e(TAG, "onResume");
    	super.onResume();
    	v.onResume();
    	
    }
    
    @Override
    public void onPause(){
    	Log.e(TAG, "onPause");
    	v.onPause();
    	super.onPause();
    }
    @Override
    public void onStop(){
    	Log.e(TAG, "onStop");
    	super.onStop();
    	v.onStop();
    }
    @Override
    public void onDestroy(){
    	Log.e(TAG, "onDestroy");
    	v.onDestroy();
    	super.onDestroy();
    	
    }
}
