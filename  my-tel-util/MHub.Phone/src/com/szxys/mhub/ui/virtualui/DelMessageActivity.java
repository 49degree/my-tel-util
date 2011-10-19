package com.szxys.mhub.ui.virtualui;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.Ctrl_Com_Code;
import com.szxys.mhub.subsystem.virtual.MemoryDataFromDB;
import com.szxys.mhub.subsystem.virtual.MhubMessage;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DelMessageActivity extends ListActivity {

	
	private ArrayList<HashMap<String, Object>> data;
	private ArrayList<MhubMessage> arrayMhubMessage;
	private ButtonAdapter adapter;
	private Button btnDelete;
	private int startIndex;
	private int Size;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_del_container);
		startIndex =getIntent().getExtras().getInt("StartIndex");
		Size = getIntent().getExtras().getInt("Size");
		data = new ArrayList<HashMap<String, Object>>();
		arrayMhubMessage = MemoryDataFromDB.getMhubMessage();
		btnDelete =(Button)findViewById(R.id.btn_DEL);
		btnDelete.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Object param[] = new Object[2];
				ArrayList<Integer> DeleteMhubComplainMsg = new ArrayList<Integer>();
				ArrayList<Integer> DeleteMhubDoctorMsg = new ArrayList<Integer>();
				for(int i =0;i<adapter.getDeletList().size();i++)
				{
					if(Integer.valueOf(arrayMhubMessage.get(adapter.getDeletList().get(i)).getSourceMsgId()) ==0)
					{
						DeleteMhubComplainMsg.add(arrayMhubMessage.get(adapter.getDeletList().get(i)).getMhubmsgId());
					}
					else {
						DeleteMhubDoctorMsg.add(arrayMhubMessage.get(adapter.getDeletList().get(i)).getMhubmsgId());
					}
					
				}
				param[0] = DeleteMhubComplainMsg;
				param[1] = DeleteMhubDoctorMsg;
				adapter.removeItem(adapter.getDeletList());
				ArrayList<MhubMessage> complainDeleteList= new ArrayList<MhubMessage>();
				for(int i =0;i<adapter.getDeletList().size();i++)
				{
					complainDeleteList.add(arrayMhubMessage.get(adapter.getDeletList().get(i)));		
				}
				arrayMhubMessage.removeAll(complainDeleteList);
				BusinessManager.getIBusinessManager().control(0, Platform.SUBBIZ_VIRTUAL, Ctrl_Com_Code.DEL_MSG_FROMDB, param, null);
				
			}
		});
		
		for (int i =startIndex ; i <Size+startIndex; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("img", R.drawable.pfphone_msgicon);
			item.put("doctorName", arrayMhubMessage.get(i).getDoctorName());
			item.put("time", composeSpecialTime(arrayMhubMessage.get(i).getTime()));
			if(Integer.valueOf(arrayMhubMessage.get(i).getSourceMsgId()) == 0)
			{
				item.put("context"," 【主动诉说】"+ arrayMhubMessage.get(i).getContent());
			}
			else {
				item.put("context"," 【医生建议】"+ arrayMhubMessage.get(i).getContent());
			}	
			item.put("imgBtn",R.drawable.pfphone_delete_pressed);
			data.add(item);
		}
		adapter = new ButtonAdapter(
				DelMessageActivity.this, data, R.layout.pfphone_del_containeritem,
				new String[] {"img","doctorName","time","context","imgBtn"}, 
				new int[] {R.id.img2, R.id.txt_doctorName2,R.id.txt_Time2, R.id.txt_context2,R.id.imgBtn});
		setListAdapter(adapter);
	}
	
	private String composeSpecialTime(String dateTime)
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
	

}
