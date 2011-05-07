package com.yang.android.tel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yang.android.tel.R;
import com.yang.android.tel.log.Logger;
import com.yang.android.tel.receiver.RefuseReceiver;
import com.yang.android.tel.service.MyTelServices;
import com.yang.android.tel.utils.Utils;

public class IncomingCallActivity extends Activity implements OnClickListener {
	public Logger logger = Logger.getLogger(IncomingCallActivity.class);
	ImageButton answerButton = null;
	ImageButton ignoreButton = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incoming_call_screen);
		MyApplication.getInstance().setActivity("IncomingCallActivity", this);

		Intent in = this.getIntent();
		Bundle ex = in.getExtras();
		String callNum = ex.getString("callNumber");
		String callName = ex.getString("callName");
		((TextView) this.findViewById(R.id.caller_number)).setText(callNum);
		((TextView) this.findViewById(R.id.caller_name)).setText(callName);
		
		answerButton = (ImageButton) this.findViewById(R.id.answer_button);
		ignoreButton = (ImageButton) this.findViewById(R.id.ignore_button);
		answerButton.setOnClickListener(this);
		ignoreButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.answer_button) {// 接电话
			try {
				TelephonyManager tManager = (TelephonyManager) this
						.getSystemService(Context.TELEPHONY_SERVICE);
				logger.error("answerRingingCall");
				Utils.getIPhone(tManager).answerRingingCall();
				logger.error("answerRingingCall11");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				Thread.sleep(500);
			}catch(Exception e){
				
			}
			
			this.finish();
		} else if (v.getId() == R.id.ignore_button) {// 挂电话
			try {
				TelephonyManager tManager = (TelephonyManager) this
						.getSystemService(Context.TELEPHONY_SERVICE);
				Utils.getIPhone(tManager).endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				Thread.sleep(500);
			}catch(Exception e){
				
			}
			
			this.finish();

		}
	}
	
	public void onDestroy(){
		
		super.onDestroy();
		MyApplication.getInstance().removeActivity("IncomingCallActivity");
	}
	
	// 监听键盘事件
	// @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
		}
		return false;
	}

}
