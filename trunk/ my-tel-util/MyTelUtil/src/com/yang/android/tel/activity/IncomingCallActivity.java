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
import com.yang.android.tel.network.SocketClient;
import com.yang.android.tel.receiver.RefuseReceiver;
import com.yang.android.tel.service.MyTelServices;
import com.yang.android.tel.utils.SharedPreferencesUtils;
import com.yang.android.tel.utils.Utils;

public class IncomingCallActivity extends Activity implements OnClickListener {
	public Logger logger = Logger.getLogger(IncomingCallActivity.class);
	ImageButton answerButton = null;
	ImageButton ignoreButton = null;
	String callNum = null;
	String callName = null;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incoming_call_screen);
		MyApplication.getInstance().setActivity("IncomingCallActivity", this);

		Intent in = this.getIntent();
		Bundle ex = in.getExtras();
		callNum = ex.getString("callNumber");
		callName = ex.getString("callName");
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
				// 在这里发送消息
				String ip = SharedPreferencesUtils.getConfigString(this,"socket_ip");
				String port = SharedPreferencesUtils.getConfigString(this,"socket_port");
				SocketClient socket = new SocketClient(ip, port);
				StringBuffer msg = new StringBuffer();
				msg.append("name:").append(callName);
				msg.append(":tel:").append(callNum);
				boolean sendRst = socket.sendMessage(msg.toString());
				logger.error("send message:" + sendRst);
				
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
