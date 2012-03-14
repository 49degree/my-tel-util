package com.custom.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.custom.view.IndexView;

public class IndexActivity  extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = "IndexActivity";
	IndexView v = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		//LinearLayout layout = (LinearLayout)this.findViewById(R.id.index_layout);
		v = new IndexView(this);
		setContentView(v);
		
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
