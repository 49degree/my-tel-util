package com.yang.android.tel;

import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyTelUtil extends Activity {
	public static String TAG = "MyTelUtil";
	public static int CALL_REQUEST_CODE = 1234;
	public static int operateCode = 0;
	EditText view = null;
	
	MyTelServices myTelServices = null;
	
    private ServiceConnection sc = new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder binder) {
        	myTelServices = ((MyTelServices.MyServiceBinder)binder).getServices();
        	myTelServices.play();
        	Toast.makeText(getApplicationContext(), "android service connected", Toast.LENGTH_LONG).show();
        }
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "android service disconnected", Toast.LENGTH_LONG).show();
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST + 1, 1, "bind start");
		menu.add(0, Menu.FIRST + 2, 2, "StartService");
		menu.add(0, Menu.FIRST + 3, 3, "StopService");
		menu.add(0, Menu.FIRST + 4, 4, "bind stop");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case Menu.FIRST + 1: {
			this.bindService(new Intent(this, MyTelServices.class), sc, Service.BIND_AUTO_CREATE);
			break;
		}
		case Menu.FIRST + 2: {
			this.setTitle("Start Service");
			startService(new Intent(this, MyTelServices.class));
			break;
		}
		case Menu.FIRST + 3: {
			this.setTitle("Stop Service");
			Intent i = new Intent(this, MyTelServices.class);
			this.stopService(i);
			break;
		}
		case Menu.FIRST + 4: {
			this.setTitle("Stop Service");
			this.unbindService(sc);
			break;
		}
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btnDial = (Button) findViewById(R.id.button_call); // 启动Dialer程序Button变量
		btnDial.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				view = (EditText) findViewById(R.id.text_tel_num); // 文本编辑变量，用于接收视图EditText获取的号码
				String phoneNum = view.getText().toString();

				TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				tm.getCallState();
				tm.listen(new TeleListener(),
						PhoneStateListener.LISTEN_CALL_STATE);

				if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNum)) {// isGlobalPhoneNumber方法用来检验输入的串是否是有效的号码
					operateCode = 0;
					tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					tm.getCallState();
					tm.listen(new TeleListener(),
							PhoneStateListener.LISTEN_CALL_STATE);

				} else {
					// 无效的号码，提示用户输入错误
					Toast.makeText(MyTelUtil.this, "号码不正确，请重新输入",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		Button btnCancel = (Button) findViewById(R.id.button_cancel); // 启动Dialer程序Button变量
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent queryIntent = new Intent("MyTelUtil");
				startActivity(queryIntent);
				TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				operateCode = 1;
			}
		});

	}

	// 拨号
	private void dialer(String tel) {
//		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
//		this.startActivityForResult(intent, CALL_REQUEST_CODE);
		Intent queryIntent = new Intent("com.yang.android.tel.MyTelUtil");
		startActivity(queryIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult:" + resultCode);
		if (requestCode == MyTelUtil.CALL_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard

		}
	}

	// 监听键盘事件
	// @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			operateCode = 1;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确定要退出吗?");
			builder.setTitle("提示");
			builder.setPositiveButton("确认",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();// 退出程序
						}
					});

			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.create().show();
		}
		return false;
	}

	class TeleListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: {
				Log.e(TAG, "CALL_STATE_IDLE");
				String phoneNum = view.getText().toString();
				Log.d(TAG, "onClick:" + phoneNum);
				if (operateCode == 0) {
					dialer(phoneNum);
				}

				// view.append("CALL_STATE_IDLE " + " ");
				break;
			}
			case TelephonyManager.CALL_STATE_OFFHOOK: {
				Log.e(TAG, "CALL_STATE_OFFHOOK");
				// view.append("CALL_STATE_OFFHOOK" + " ");
				break;
			}
			case TelephonyManager.CALL_STATE_RINGING: {
				Log.e(TAG, "CALL_STATE_RINGING");
				// view.append("CALL_STATE_RINGING" + " ");
				break;
			}
			default:
				break;
			}
		}
	}

	public static boolean isIntentAvailable(Context context, Intent intent ) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
}