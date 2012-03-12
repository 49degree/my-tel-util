package com.custom.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.custom.view.IndexView;

public class IndexActivity  extends Activity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		View v = new IndexView(this);
		setContentView(v);
		
	}

}
