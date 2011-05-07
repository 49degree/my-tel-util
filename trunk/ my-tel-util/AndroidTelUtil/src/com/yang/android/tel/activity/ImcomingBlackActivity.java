package com.yang.android.tel.activity;

import com.yang.android.tel.R;

import android.app.Activity;
import android.os.Bundle;

public class ImcomingBlackActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.black_screen);
		MyApplication.getInstance().setActivity("ImcomingBlackActivity", this);

	}
	public void onDestroy(){
		super.onDestroy();
		MyApplication.getInstance().removeActivity("ImcomingBlackActivity");
	}	
}
