package com.szxys.mhub.ui.virtualui;

import android.os.Bundle;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.ui.base.MHubActivity;

public class MessageDetailActivity extends MHubActivity {

	
	private TextView tvContent;
	@Override
	protected String getSysName() {
		// TODO Auto-generated method stub
		return "dsadas";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_messagedetail);
		setDisableMenu(1);
		setMenuAlwaysShow(true);
		Bundle bundle = getIntent().getExtras();
		tvContent =(TextView)findViewById(R.id.textContent);
		tvContent.setText(bundle.getString("Content"));
	}

	

}
