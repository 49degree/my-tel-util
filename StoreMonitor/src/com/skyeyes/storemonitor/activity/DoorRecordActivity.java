package com.skyeyes.storemonitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveOpenCloseDoor;
import com.skyeyes.base.cmd.bean.impl.ReceiveOpenCloseDoor.OpenCloseDoorBean;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.StoreMonitorApplication;
import com.skyeyes.storemonitor.activity.adapter.DoorRecordViewAdapter;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

public class DoorRecordActivity extends Activity {

	private TopTitleView topTitleView;
	private ListView door_record_list;
	private LinearLayout query_data_notify_ll;
	private TextView query_data_notify_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.door_record_view);
		topTitleView = (TopTitleView)findViewById(R.id.dr_topView);
		topTitleView.setOnMenuButtonClickListener(new OnClickListenerCallback() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				HomeActivity.getInstance().toggleMenu();

			}
		});
		query_data_notify_ll = (LinearLayout)findViewById(R.id.query_data_notify_ll);
		query_data_notify_tv = (TextView)findViewById(R.id.query_data_notify_tv);
		door_record_list = (ListView)findViewById(R.id.door_record_list);
		query_data_notify_ll.setVisibility(View.VISIBLE);
		door_record_list.setVisibility(View.GONE);
		
		if(StoreMonitorApplication.getInstance().getReceivLogin()==null){
			query_data_notify_tv.setText("未登陆设备");
		}else{
			//查询通道图片
			SendObjectParams sendObjectParams = new SendObjectParams();
			Object[] params = new Object[] {
					//DateUtil.getFrontMonthDateString()+" 00:00:00",
					"2012-01-01 00:00:00",
					DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YMDHMS)};
			try {
				sendObjectParams.setParams(REQUST.cmdReqOpenCloseDoorList, params);
				System.out.println("cmdReqOpenCloseDoorList入参数：" + sendObjectParams.toString());
				
				DevicesService.sendCmd(sendObjectParams, new DoorRecordReceive());
			} catch (CommandParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//testView();
	}

	private class DoorRecordReceive extends DeviceReceiveCmdProcess<ReceiveOpenCloseDoor>{

		@Override
		public void onProcess(ReceiveOpenCloseDoor receiveCmdBean) {
			// TODO Auto-generated method stub
			Log.i("MainPageActivity", "DoorRecordReceive================");
			if(receiveCmdBean.openCloseDoorBeans.size()>0){
				DoorRecordViewAdapter pageAdapter=new DoorRecordViewAdapter(DoorRecordActivity.this,receiveCmdBean.openCloseDoorBeans);
				door_record_list.setAdapter(pageAdapter);
				query_data_notify_ll.setVisibility(View.GONE);
				door_record_list.setVisibility(View.VISIBLE);
			}else{
				query_data_notify_ll.setVisibility(View.VISIBLE);
				door_record_list.setVisibility(View.GONE);
				query_data_notify_tv.setText("最近一个月没有开关门数据");
			}
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public void testView(){
		query_data_notify_ll.setVisibility(View.GONE);
		door_record_list.setVisibility(View.VISIBLE);
		
		List<OpenCloseDoorBean> openCloseDoorBeans = new ArrayList<OpenCloseDoorBean>();

		if(openCloseDoorBeans.size()==0){
			for(int i =0;i<20;i++){
				OpenCloseDoorBean openCloseDoorBean = new OpenCloseDoorBean();
				openCloseDoorBean.des = "1234";
				openCloseDoorBean.type = i%2+1;
				openCloseDoorBean.time=i*10000;
				openCloseDoorBeans.add(openCloseDoorBean);
			}
		
		}
		
		DoorRecordViewAdapter pageAdapter=new DoorRecordViewAdapter(DoorRecordActivity.this,openCloseDoorBeans);
		door_record_list.setAdapter(pageAdapter);
		query_data_notify_ll.setVisibility(View.GONE);
		door_record_list.setVisibility(View.VISIBLE);
	}
}
