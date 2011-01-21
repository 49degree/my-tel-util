package com.yang.android.tel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import android.os.RemoteException;
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

import com.android.internal.telephony.ITelephony;

public class MyTelUtil extends Activity {
	public static String TAG = "MyTelUtil";

	MyTelServices myTelServices = null;

	/**
	 * ��ȡservices�󶨶���
	 */
    private ServiceConnection sc = new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder binder) {
        	myTelServices = ((MyTelServices.MyServiceBinder)binder).getServices();
			EditText view = (EditText) findViewById(R.id.text_tel_num); //��ȡ�ĺ���
			String phoneNum = view.getText().toString();
        	myTelServices.startCall(phoneNum);
        	Toast.makeText(getApplicationContext(), "android service connected", Toast.LENGTH_LONG).show();
        }
        public void onServiceDisconnected(ComponentName name) {
        	myTelServices = null;
            Toast.makeText(getApplicationContext(), "android service disconnected", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * ���Ӳ˵���
     */
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

	/**
	 * ����˵���ť�¼�
	 */
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
		
		Button btnDial = (Button) findViewById(R.id.button_call); // ����Dialer����Button����
		btnDial.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "onClick btnDial:");
				EditText view = (EditText) findViewById(R.id.text_tel_num); //��ȡ�ĺ���
				String phoneNum = view.getText().toString();
				if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNum)) {//����������������Ĵ��Ƿ�����Ч�ĺ���
					bindService(new Intent(MyTelUtil.this, MyTelServices.class), sc, Service.BIND_AUTO_CREATE);
					if(myTelServices!=null){
						myTelServices.startCall(phoneNum);
					}
				} else {
					if(myTelServices!=null){
						myTelServices.endCall(); 
					}
					Toast.makeText(MyTelUtil.this, "���벻��ȷ������������",
							Toast.LENGTH_LONG).show();// ��Ч�ĺ��룬��ʾ�û��������
				}
			}
		});

		Button btnCancel = (Button) findViewById(R.id.button_cancel); // ȡ��Dialer����Button����
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)  {
				Log.d(TAG, "onClick btnCancel:");
				if(myTelServices!=null){
					myTelServices.endCall();
				}
			}
		});

	}


	// ���������¼�
	// @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("ȷ��Ҫ�˳���?");
			builder.setTitle("��ʾ");
			builder.setPositiveButton("ȷ��",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							myTelServices.endCall();
							finish();// �˳�����
						}
					});

			builder.setNegativeButton("ȡ��",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.create().show();
		}
		return false;
	}


	public static boolean isIntentAvailable(Context context, Intent intent ) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
}