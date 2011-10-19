package com.xys.ecg.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.ecg.bean.DoctorAdviceEntity;
import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.network.SoapQueryAdviceTool;
import com.xys.ecg.sqlite.DoctorAdviceDB;
import com.xys.ecg.sqlite.RecordDB;



public class Advice extends Activity {
	private ListView listAdvice;
	private Button btBack;
	private AdviceEventHandler mainHandler = null;
	private ProgressDialog btDialog  = null;
    private int adviceID;
    private String content = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advice);
		listAdvice = (ListView) findViewById(R.id.List_advice);
		mainHandler = new AdviceEventHandler();
		btDialog = ProgressDialog.show(Advice.this, // context     
				"医生建议", // title     
				"请等待加载数据 ...", // message     
				true); //进度是否是不确定的，这只和创建进度条有关
		SoapQueryAdviceTool.queryAdviceTool(this, mainHandler);
		btBack = (Button)findViewById(R.id.Back);
		
		btBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();

			}
		});
		
		listAdvice.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			    
				TextView tvuserId = (TextView)arg1.findViewById(R.id.Tv_userid);
				adviceID = Integer.parseInt(tvuserId.getText().toString());
				TextView tvname = (TextView)arg1.findViewById(R.id.Tv_name);
				TextView tvtime = (TextView)arg1.findViewById(R.id.Tv_time);				
				TextView tvadvice = (TextView)arg1.findViewById(R.id.Tv_advice);
				
				DoctorAdviceDB adviceDB = new DoctorAdviceDB(Advice.this);
				adviceDB.updateDoctorAdviceByFlag(adviceID,1);
				adviceDB.close();
                  
				  new AlertDialog.Builder(Advice.this)     
				                  .setTitle(tvname.getText().toString()) 
				                  .setMessage(tvadvice.getText().toString()+"\n\n\n" +"\n"+tvuserId.getText().toString()+"\n"+tvtime.getText().toString()) 
				                  .setPositiveButton("返回",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}})
				                  .setNegativeButton("删除",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										DoctorAdviceDB adviceDB = new DoctorAdviceDB(Advice.this);
										adviceDB.deleteDoctorAdviceByAdviceId(adviceID);
										adviceDB.close();
										showList();
										//dialog.cancel();
									}})
				                  .show(); 			 	 
			}});

	
		btBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}	
	    });

	}

	private void showList()
	{
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		
		//读取数据库
		DoctorAdviceDB adviceDB = new DoctorAdviceDB(Advice.this);
		Cursor cousor = adviceDB.getAllDoctorAdvice();
		
		while(cousor.moveToNext())
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.msg);
			map.put("ItemName",cousor.getString(cousor.getColumnIndex("DoctorName")));
			map.put("ItemTime", cousor.getString(cousor.getColumnIndex("ArriveTime")));	
			map.put("ItemAdvice", cousor.getString(cousor.getColumnIndex("Content")));
			map.put("ItemUserId",cousor.getInt(cousor.getColumnIndex("AdviceID")));
			listItem.add(map);
			
		}

		
		SimpleAdapter listItemAdapter = new SimpleAdapter(Advice.this, listItem,
				R.layout.list_advice_item, new String[] { "ItemImage",
						"ItemName", "ItemTime", "ItemAdvice" ,"ItemUserId"}, new int[] {
						R.id.Img_msg, R.id.Tv_name, R.id.Tv_time,
						R.id.Tv_advice,R.id.Tv_userid });
		listAdvice.setAdapter(listItemAdapter);
		cousor.close();
		adviceDB.close();
	}
	
	public class AdviceEventHandler extends Handler {
		public AdviceEventHandler() {
			super();
		}
		public void handleMessage(Message msg) {
			//logger.debug("Analysis2shapeline" + msg.what);
			// 可以根据msg.what执行不同的处理，这里没有这么做
			switch (msg.what) {
			case HandlerWhat.SoapQueryAdviceTool2Parents:// 通知查询结果
				break;
			case HandlerWhat.Tread2Notify:// 通知查询结果
				Toast.makeText(Advice.this,(String)msg.obj,Toast.LENGTH_LONG).show();
				try
				{
				showList();
				}catch(Exception ex)
				{
					
				}
				break;				
			default:
				break;
			}
			if(btDialog.isShowing()){
				btDialog.dismiss();
			}
		}
	}
	
}
	
	
	