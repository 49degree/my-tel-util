package com.xys.ecg.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class About extends Activity {
	private Button btBack;

	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.about);
	        btBack = (Button)findViewById(R.id.Btn_back);
	        btBack.setOnClickListener(new OnClickListener(){
	   		 public void onClick(View v){
	   			/* Intent inBack = new Intent(Config.this, ECG_Android.class);
	   				startActivity(inBack);
	   				*/
	   			 finish();
	   		 }
	   	 });
	 }
	
}
