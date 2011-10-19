package com.szxys.mhub.ui.mets.activity;

import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.bizmanager.IBusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.mets.business.MetsConstant;
import com.szxys.mhub.subsystem.mets.network.WebService;
import com.szxys.mhub.ui.mets.main.MetsMainActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SendNowActivity extends Activity implements OnClickListener{
	private ProgressBar gatherPB;
	private Button cancelButton;
	
	private boolean isSendSuccess = true;
	
	private final IBusinessManager bizManager = BusinessManager.getIBusinessManager();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mets_send_now);
		
		gatherPB = (ProgressBar) this.findViewById(R.id.mets_sendNow_pb);
		cancelButton = (Button) this.findViewById(R.id.mets_sendNow_cancel_btn);
		cancelButton.setOnClickListener(this);
		
		
		WebService ws=WebService.instance();
		int userId = MetsMainActivity.getUserId();
		ws.setUserAccount(userId);
		Object[] myObj = new Object[]{ws};

		//ws.setUserAccount(userAccount);
		//bizManager.control(userId, Platform.SUBBIZ_METS, MetsConstant.CTRL_TEST, null, null);
		bizManager.control(userId, Platform.SUBBIZ_METS, MetsConstant.CTRL_SEND_URINE, myObj, null);
		//bizManager.control(1, Platform.SUBBIZ_METS, MetsConstant.CTRL_SEND_WATER, myObj, null);

		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.mets_sendNow_cancel_btn:
			Toast.makeText(SendNowActivity.this, "取消发送", Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
		
	}
	
}
