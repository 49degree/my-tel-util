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
	 * 获取services绑定对象
	 */
    private ServiceConnection sc = new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder binder) {
        	myTelServices = ((MyTelServices.MyServiceBinder)binder).getServices();
			EditText view = (EditText) findViewById(R.id.text_tel_num); //获取的号码
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
     * 增加菜单项
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
	 * 处理菜单按钮事件
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
		
		Button btnDial = (Button) findViewById(R.id.button_call); // 启动Dialer程序Button变量
		btnDial.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "onClick btnDial:");
				EditText view = (EditText) findViewById(R.id.text_tel_num); //获取的号码
				String phoneNum = view.getText().toString();
				if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNum)) {//方法用来检验输入的串是否是有效的号码
					bindService(new Intent(MyTelUtil.this, MyTelServices.class), sc, Service.BIND_AUTO_CREATE);
					if(myTelServices!=null){
						myTelServices.startCall(phoneNum);
					}
				} else {
					if(myTelServices!=null){
						myTelServices.endCall(); 
					}
					Toast.makeText(MyTelUtil.this, "号码不正确，请重新输入",
							Toast.LENGTH_LONG).show();// 无效的号码，提示用户输入错误
				}
			}
		});

		Button btnCancel = (Button) findViewById(R.id.button_cancel); // 取消Dialer程序Button变量
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)  {
				Log.d(TAG, "onClick btnCancel:");
				if(myTelServices!=null){
					myTelServices.endCall();
				}
			}
		});
		

		// 配置输入键盘相关界面
		TabHost mTabHost = (TabHost) findViewById(R.id.rufuse_list);
		mTabHost.setup();
		LayoutInflater inflater_tab1 = LayoutInflater.from(this);
		inflater_tab1.inflate(R.layout.refuse_tel_tab, mTabHost.getTabContentView());
		inflater_tab1.inflate(R.layout.refuse_message_tab, mTabHost.getTabContentView());
		mTabHost.addTab(mTabHost.newTabSpec("refuse_tel_tab").setIndicator("精确输入")
				.setContent(R.id.LinearLayout01));
		mTabHost.addTab(mTabHost.newTabSpec("refuse_tel_tab").setIndicator("快速输入")
				.setContent(R.id.LinearLayout02));
		
		//查询拒接电话号码
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
					value.put("name","陌生人");
				}
				c.close();
			}

			
			
			SimpleAdapter simpleAdapter = new SimpleAdapter(this, list,R.layout.refuse_tel_list_items, 
					returnColumn,new int[]{R.id.frend_tel_num,R.id.frend_name});

			ListView listView = (ListView) findViewById(R.id.refuse_tel_list);
			listView.setCacheColorHint(0);//防止拖动时背景变色
			listView.setAdapter(simpleAdapter);

		}catch(Exception e){
			
		}finally{
			sqlDb.close();
			dbOperator.close();
		}
		
		
		
		// 修改TAB的高度
		TabWidget tabwidget = (TabWidget) this.findViewById(android.R.id.tabs);

		for (int i = 0; i < tabwidget.getChildCount(); i++) {
			tabwidget.getChildAt(i).getLayoutParams().height = 30;
			/*
			 * 配置TAB的图片样式，没有配套的图片取消 Drawable
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
		
		
//		Button btnExit = (Button) findViewById(R.id.button_exit); // 取消Dialer程序Button变量
//		btnExit.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v)  {
//				Log.d(TAG, "onClick btnExit:");
//				exitMethod();
//			}
//		});		

	}


	
	
	private void exitMethod(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(myTelServices!=null){
							myTelServices.endCall();
						}
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
	// 监听键盘事件
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