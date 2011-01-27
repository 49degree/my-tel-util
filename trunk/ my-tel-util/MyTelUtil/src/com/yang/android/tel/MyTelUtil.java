package com.yang.android.tel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Contacts;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.yang.android.tel.db.DBBean;
import com.yang.android.tel.db.DBOperator;


public class MyTelUtil extends Activity {
	public static String TAG = "MyTelUtil";

	MyTelServices myTelServices = null;
	
	private DBOperator dbOperator = null;
	private SQLiteDatabase sqlDb = null;

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
		
		dbOperator = new DBOperator(this.getApplicationContext(),"RefuseTelDB.db",null,1);
		sqlDb = dbOperator.getReadableDatabase();
		
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
		

		// �������������ؽ���
		TabHost mTabHost = (TabHost) findViewById(R.id.rufuse_list);
		mTabHost.setup();
		LayoutInflater inflater_tab1 = LayoutInflater.from(this);
		inflater_tab1.inflate(R.layout.refuse_tel_tab, mTabHost.getTabContentView());
		inflater_tab1.inflate(R.layout.refuse_message_tab, mTabHost.getTabContentView());
		mTabHost.addTab(mTabHost.newTabSpec("refuse_tel_tab").setIndicator("��ȷ����")
				.setContent(R.id.LinearLayout01));
		mTabHost.addTab(mTabHost.newTabSpec("refuse_tel_tab").setIndicator("��������")
				.setContent(R.id.LinearLayout02));
		
		//��ѯ�ܽӵ绰����
		Map<String,String> params = new HashMap<String,String>(1);
		params.put(DBBean.RefuseTel.REFUSE_CALL, "1");
		String[] returnColumn = new String[]{DBBean.RefuseTel.REFUSE_TEL_NUM,"_id"};
		
		try{
			List<Map<String,String>> list= DBOperator.queryMapList(DBBean.needInitTables.get("RefuseTel"), 
					sqlDb, returnColumn, params);
			
			for(Map<String,String> value:list){
				Cursor c = this.managedQuery(Contacts.Phones.CONTENT_URI, 
						new String[]{Contacts.Phones.NAME,Contacts.Phones.NUMBER} ,
						Contacts.Phones.NUMBER+"=?",new String[]{value.get(DBBean.RefuseTel.REFUSE_TEL_NUM)}, null);
				c.moveToFirst();
				//Log.d(TAG,)
				while(!c.isAfterLast()){
					value.put("name", c.getString(0));
					c.moveToNext();
				}
				if(!value.containsKey("name")){
					value.put("name","İ����");
				}
				c.close();
			}

			
			
			SimpleAdapter simpleAdapter = new SimpleAdapter(this, list,R.layout.refuse_tel_list_items, 
					returnColumn,new int[]{R.id.frend_tel_num,R.id.frend_name});

			ListView listView = (ListView) findViewById(R.id.refuse_tel_list);
			listView.setCacheColorHint(0);//��ֹ�϶�ʱ������ɫ
			listView.setAdapter(simpleAdapter);

		}catch(Exception e){
			
		}finally{
			sqlDb.close();
			dbOperator.close();
		}
		
		
		
		// �޸�TAB�ĸ߶�
		TabWidget tabwidget = (TabWidget) this.findViewById(android.R.id.tabs);

		for (int i = 0; i < tabwidget.getChildCount(); i++) {
			tabwidget.getChildAt(i).getLayoutParams().height = 30;
			/*
			 * ����TAB��ͼƬ��ʽ��û�����׵�ͼƬȡ�� Drawable
			 * drTab1=(Drawable)getResources().getDrawable(R.drawable.tab1);
			 * Drawable
			 * drTab2=(Drawable)getResources().getDrawable(R.drawable.tab2);
			 * if(i==0){ tabwidget.getChildAt(i).setBackgroundDrawable(drTab1);
			 * }else{ tabwidget.getChildAt(i).setBackgroundDrawable(drTab2); }
			 */

			final ImageView iv = (ImageView) tabwidget.getChildAt(i)
					.findViewById(android.R.id.icon);
			iv.getLayoutParams().height = 0;
		}
		
		
//		Button btnExit = (Button) findViewById(R.id.button_exit); // ȡ��Dialer����Button����
//		btnExit.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v)  {
//				Log.d(TAG, "onClick btnExit:");
//				exitMethod();
//			}
//		});		

	}


	
	
	private void exitMethod(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("ȷ��Ҫ�˳���?");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(myTelServices!=null){
							myTelServices.endCall();
						}
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
	// ���������¼�
	// @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exitMethod();
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