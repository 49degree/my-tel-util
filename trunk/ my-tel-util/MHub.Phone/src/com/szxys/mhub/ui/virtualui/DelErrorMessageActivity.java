package com.szxys.mhub.ui.virtualui;
import java.util.ArrayList;
import java.util.HashMap;
import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.Ctrl_Com_Code;
import com.szxys.mhub.subsystem.virtual.DataUtil;
//import com.szxys.mhub.subsystem.virtual.DataUtil;
import com.szxys.mhub.subsystem.virtual.ErrorMessageEntity;

import android.R.integer;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DelErrorMessageActivity extends ListActivity  {
	private ArrayList<ErrorMessageEntity> arrayMessage;
	private ArrayList<HashMap<String, Object>> data;
	private ButtonApapterExpansion adapter;
	private Button btnDel,cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_del_errormessage);
		data=new ArrayList<HashMap<String,Object>>();
		arrayMessage = DataUtil.getMsg();
		if (arrayMessage.size() > 0) {
			for (int i = 0; i < arrayMessage.size(); i++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				int id = arrayMessage.get(i).get_appId();
				item.put("alarmType", arrayMessage.get(i).get_alarmType());
				item.put("alarmTime", ErrorMessageActivity.composeSpecialTime(arrayMessage.get(i).get_alarmTime()));
				item.put("alarmDescription", arrayMessage.get(i).get_alarmDescription());
				
				ErrorMessageActivity.getDrawable(item, id);
				item.put("imgBtn",R.drawable.pfphone_delete_pressed);
				data.add(item);
			}
			adapter=new ButtonApapterExpansion(DelErrorMessageActivity.this, data, R.layout.pfphone_del_errormessageitem,
					new String[]{"appId","alarmType","alarmTime","alarmDescription","imgBtn"}, new int[]{
					R.id.txt_appId,R.id.txt_alarmTypeId,R.id.txt_AlarmTime,R.id.txt_AlarmDescription,R.id.imgBtn});
			setListAdapter(adapter);
		}
		cancel=(Button)findViewById(R.id.btn_cancel);
		btnDel=(Button)findViewById(R.id.btn_delMessage);
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Object param[] = new Object[1];
				ArrayList<Integer> DeleteMsg = new ArrayList<Integer>();
				for(int i =0;i<adapter.getDeletList().size();i++)
				{
					DeleteMsg.add(arrayMessage.get(adapter.getDeletList().get(i)).get_id());
				}
				param[0] = DeleteMsg;
				adapter.removeItem(adapter.getDeletList());
				ArrayList<ErrorMessageEntity> deleteList=new ArrayList<ErrorMessageEntity>();
				for(int i =0;i<adapter.getDeletList().size();i++)
				{
					deleteList.add(arrayMessage.get(adapter.getDeletList().get(i)));		
				}
				arrayMessage.removeAll(deleteList);
				BusinessManager.getIBusinessManager().control(0, Platform.SUBBIZ_VIRTUAL, Ctrl_Com_Code.DEL_DATA, param, null);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(DelErrorMessageActivity.this,ErrorMessageActivity.class);
				startActivity(i);
			}
		});
	}

}
