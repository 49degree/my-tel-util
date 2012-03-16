package com.custom.activity;

import java.lang.reflect.Constructor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.custom.utils.Constant;
import com.custom.view.IndexView;
import com.custom.view.ViewImp;

public class IndexActivity  extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = "IndexActivity";
	ViewImp v = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏标题栏

		Bundle bundle = this.getIntent().getExtras();
		if(bundle==null||bundle.get(Constant.foldPath)==null){
			v = new IndexView(this,Constant.path,Constant.fistFoldDepth);
			setContentView(v);
		}else{
			try{
				String foldPath = bundle.getString(Constant.foldPath);
				int foldDepth = bundle.getInt(Constant.foldDepth);
				String viewClassStr = bundle.getString(Constant.viewClass);
				Class viewClass = Class.forName(viewClassStr);
				
				//SecondView(Context context,String foldPath,int foldDepth)
				Constructor str = viewClass.getConstructor(Context.class,String.class,int.class);
				v = (ViewImp)str.newInstance(this,foldPath,foldDepth);
				
//				v = new IndexView(this,foldPath,foldDepth);
				setContentView(v);
				
				
				
			}catch(Exception e){
				
			}

		}
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
}
