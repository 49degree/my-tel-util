package com.custom.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.custom.utils.Constant;
import com.custom.view.IndexView;

public class IndexActivity  extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = "IndexActivity";
	IndexView v = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		if(bundle==null||bundle.get(Constant.foldPath)==null){
			v = new IndexView(this,Constant.path,Constant.fistFoldDepth);
			setContentView(v);
		}else{
			String foldPath = bundle.getString(Constant.foldPath);
			int foldDepth = bundle.getInt(Constant.foldDepth);
			v = new IndexView(this,foldPath,foldDepth);
			setContentView(v);
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
