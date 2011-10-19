package com.szxys.mhub.ui.virtualui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.Ctrl_Com_Code;
import com.szxys.mhub.subsystem.virtual.ErrorMessageEntity;

public class ErrorMessageActivity extends Activity{
	private List<ErrorMessageEntity> errorMessageList;
	private SimpleAdapter adapter;
	private List<HashMap<String, Object>> data;
	private Object[] obj=new Object[1];
	private ListView list;
	private Handler mHandler = new UIhandler();
	private TextView text;
	private Button btnDel;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Object param[]=new Object[1];
		ErrorMessageEntity message = new ErrorMessageEntity();
		/*message.set_appId(10);
		message.set_alarmType("呼吸监护:");
		message.set_alarmLevelId(2);
		message.set_alarmDescription("正常");
		message.set_alarmTime("2011-5-16 6:15");
		param[0]=message;
		BusinessManager.getIBusinessManager().control(0, Platform.SUBBIZ_VIRTUAL, Ctrl_Com_Code.SAVE_DATA, param, null);
		message.set_appId(10);
		message.set_alarmType("血糖监护:");
		message.set_alarmLevelId(2);
		message.set_alarmDescription("血糖正常");
		message.set_alarmTime("2011-5-17 2:15");
		param[0]=message;
		BusinessManager.getIBusinessManager().control(0, Platform.SUBBIZ_VIRTUAL, Ctrl_Com_Code.SAVE_DATA, param, null);*/
		/*message.set_appId(10);
		message.set_alarmType("睡眠监护:");
		message.set_alarmLevelId(2);
		message.set_alarmDescription("总是做梦啊 -----");
		message.set_alarmTime("2011-5-17 2:15");
		param[0]=message;
		BusinessManager.getIBusinessManager().control(0, Platform.SUBBIZ_VIRTUAL, Ctrl_Com_Code.SAVE_DATA, param, null);*/	
		initListView();
	}
	
	private class UIhandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				break;
			case 1:
				renderListView();
				break;
			case 2:
				
			}
		}
	}

	protected void initListView() {
		setContentView(R.layout.pfphone_errormessagelist);
		btnDel=(Button)findViewById(R.id.btn_delerror_message);
		list=(ListView)findViewById(android.R.id.list);
		text=(TextView)findViewById(android.R.id.empty);
		data = new ArrayList<HashMap<String, Object>>();
		adapter = new SimpleAdapter(ErrorMessageActivity.this, data,
				R.layout.pfphone_errormessageitem, new String[] { "appId",
						"id", "alarmType", "alarmDescription", "alarmTime" },
				new int[] { R.id.txt_appId, R.id.txt_id, R.id.txt_alarmTypeId,
						R.id.txt_AlarmDescription, R.id.txt_AlarmTime });
		list.setAdapter(adapter);
		btnDel.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ErrorMessageActivity.this,DelErrorMessageActivity.class);
				startActivity(intent);
			}
		});
		sendRequest();
	}
	
	@SuppressWarnings("unchecked")
	protected void renderListView() {
		errorMessageList = (List<ErrorMessageEntity>) obj[0];
		data.clear();
		if (errorMessageList.size() > 0) {
			for (int i = 0; i < errorMessageList.size(); i++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				int id = errorMessageList.get(i).get_appId();
				item.put("id", errorMessageList.get(i).get_id());
				item.put("alarmType", errorMessageList.get(i).get_alarmType());
				item.put("alarmDescription", errorMessageList.get(i)
						.get_alarmDescription());
				item.put("alarmTime", composeSpecialTime(errorMessageList
						.get(i).get_alarmTime()));
				getDrawable(item, id);
				data.add(item);
			}
		} else {
			text.setText("暂无异常信息");
			btnDel.setEnabled(false);
		}
		SimpleAdapter adapter = (SimpleAdapter) list.getAdapter();
		adapter.notifyDataSetChanged();
	}
	public void sendRequest() {
		new Thread() {
			@Override
			public void run() {
				BusinessManager.getIBusinessManager().control(0, Platform.SUBBIZ_VIRTUAL, Ctrl_Com_Code.SEARCH_DATA,null,obj);
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
			}
		}.start();
		
		
	}
	static public void getDrawable(HashMap<String, Object> item, int id) {
		switch (id) {
		case Platform.SUBBIZ_VIRTUAL:
			item.put("appId",R.drawable.u33);
			break;
		case Platform.SUBBIZ_ECG:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_METS:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_UFR:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_DMFS:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_PFUS:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_PROSTATITISFU:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_BPHFU:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_ABPMS:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_HM:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_HEALTHRECORD:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_FETALHEART:
			item.put("appId",R.drawable.u47);
			break;
		case Platform.SUBBIZ_RMBGMS:
			item.put("appId",R.drawable.u47);
			break;	
		}
	}

	
	static public String composeSpecialTime(String dateTime)
	{
		String strResult ="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		String time = sdf.format(date);
		String nowTime[] =time.split("-| |:");
		String paramTime[] = dateTime.split("-| |:");
		int temp1 = Integer.valueOf(nowTime[2]);
		int temp2 = Integer.valueOf(paramTime[2]);
		switch (temp1 - temp2) {
		case 0:
			strResult += paramTime[3]+":"+paramTime[4];
			break;
		case 1:
			strResult +="昨天"+" "+paramTime[3]+":"+paramTime[4];
			break;
		case 2:
			strResult +="前天"+" "+paramTime[3]+":"+paramTime[4];
			break;
		default :
			strResult +=paramTime[1]+"-"+paramTime[2];
			break;
		}
		return strResult;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		sendRequest();
		super.onResume();
	}
}


