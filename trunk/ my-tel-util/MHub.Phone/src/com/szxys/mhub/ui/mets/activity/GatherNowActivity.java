package com.szxys.mhub.ui.mets.activity;

import com.szxys.mhub.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class GatherNowActivity extends Activity implements OnClickListener{

	private Button okButton;
	private Button cancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mets_gather_now);
		
		okButton = (Button) this.findViewById(R.id.mets_gatherNow_ok);
		cancelButton = (Button) this.findViewById(R.id.mets_gatherNow_cancel);
		
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.mets_gatherNow_ok:
			Toast.makeText(GatherNowActivity.this, "请打开采集器", Toast.LENGTH_SHORT).show();
			//------打开采集器-------
			
			//------打开采集器-------
			break;
			
		case R.id.mets_gatherNow_cancel:
			
			finish();
			break;
		}
		
	}

	
}
