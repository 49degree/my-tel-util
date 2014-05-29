package com.skyeyes.storemonitor.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.skyeyes.base.bean.OpenCloseDoorIdBean;
import com.skyeyes.base.bean.OpenCloseDoorInfoBean;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveOpenCloseDoor;
import com.skyeyes.base.cmd.bean.impl.ReceiveOpenCloseDoorInfo;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.db.DBBean;
import com.skyeyes.base.db.DBOperator;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.PreferenceUtil;
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
	private ArrayList<OpenCloseDoorInfoBean> openCloseDoorInfoBeans = new ArrayList<OpenCloseDoorInfoBean>();
	private DoorRecordViewAdapter pageAdapter;
	private String endTime;
	private OpenCloseDoorIdBean queryOpenCloseDoorIdBean; 
	Thread t;
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

		

		//testView();
	}

    public void onResume(){
    	super.onResume();
		if(StoreMonitorApplication.getInstance().getReceivLogin()==null){
			query_data_notify_tv.setText("未登陆设备");
		}else if(pageAdapter==null){
			query_data_notify_tv.setText("正在查询开关门数据,请稍后...");
			pageAdapter=new DoorRecordViewAdapter(this,openCloseDoorInfoBeans);
			door_record_list.setAdapter(pageAdapter);
			//查询通道图片
			endTime = DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YMDHMS);
			String lastTime = PreferenceUtil.getConfigString(PreferenceUtil.DEVICE_INFO, PreferenceUtil.device_door_query_last_time);
			String startTime = DateUtil.getTimeStringFormat(new Date(0), DateUtil.TIME_FORMAT_YMDHMS);//DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YMDHMS);
			if(!"".equals(lastTime)){
				startTime = lastTime;
			}
			SendObjectParams sendObjectParams = new SendObjectParams();
			Object[] params = new Object[] {
					//DateUtil.getFrontMonthDateString()+" 00:00:00",
					startTime,
					endTime};
			try {
				sendObjectParams.setParams(REQUST.cmdReqOpenCloseDoorList, params);
				System.out.println("cmdReqOpenCloseDoorList入参数：" + sendObjectParams.toString());
				
				DoorRecordReceive doorRecordReceive = new DoorRecordReceive();
				doorRecordReceive.setTimeout(10*1000);
				DevicesService.sendCmd(sendObjectParams, doorRecordReceive);
			} catch (CommandParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

	private class DoorRecordReceive extends DeviceReceiveCmdProcess<ReceiveOpenCloseDoor>{

		@Override
		public void onProcess(ReceiveOpenCloseDoor receiveCmdBean) {
			// TODO Auto-generated method stub
			if(receiveCmdBean.getCommandHeader().resultCode==0){
				PreferenceUtil.setSingleConfigInfo(PreferenceUtil.DEVICE_INFO, PreferenceUtil.device_door_query_last_time,endTime);
			}
			final DoorRecordInfoReceive doorRecordInfoReceive = new DoorRecordInfoReceive();
			
			Log.i("MainPageActivity", "DoorRecordReceive================");
			if(receiveCmdBean.openCloseDoorBeans.size()>0){
				for(int i=receiveCmdBean.openCloseDoorBeans.size()-1;i>=0;i--){
					OpenCloseDoorIdBean openCloseDoorIdBean = new OpenCloseDoorIdBean();
					openCloseDoorIdBean.eventCode = receiveCmdBean.openCloseDoorBeans.get(i).des;
					openCloseDoorIdBean.type = receiveCmdBean.openCloseDoorBeans.get(i).type;
					DBOperator.getInstance().insert(DBBean.TBOpenCloseDoorIdBean, openCloseDoorIdBean);
				}
			}
			
			t = new Thread(){
				public void run(){
					List<Object> openCloseDoorIdBeans = DBOperator.getInstance().queryBeanList(DBBean.TBOpenCloseDoorIdBean,null);
					HashMap params = new HashMap<String,String>();
					
					SendObjectParams  sendObjectParams = new SendObjectParams();
					Object[] cmdParams = new Object[1];
					for(int i=openCloseDoorIdBeans.size()-1;i>=0;i--){
						params.put("eventCode=", ((OpenCloseDoorIdBean)openCloseDoorIdBeans.get(i)).eventCode);
						final List<Object> temp = DBOperator.getInstance().queryBeanList(DBBean.TBOpenCloseDoorInfoBean, params);
						if(temp.size()==0){
							//查询详细信息
							if(queryOpenCloseDoorIdBean != null){
								try {
									synchronized (this) {
										this.wait();
									}
									
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							queryOpenCloseDoorIdBean = ((OpenCloseDoorIdBean)openCloseDoorIdBeans.get(i));
							cmdParams[0] = queryOpenCloseDoorIdBean.eventCode;
							try {
								sendObjectParams.setParams(REQUST.cmdReqOpenCloseDoorInfo, cmdParams);
								System.out.println("cmdReqOpenCloseDoorInfo入参数：" + sendObjectParams.toString());
								
								doorRecordInfoReceive.setTimeout(10*1000);
								DevicesService.sendCmd(sendObjectParams, doorRecordInfoReceive);
							} catch (CommandParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							runOnUiThread(new Runnable(){
								@Override
								public void run() {
									openCloseDoorInfoBeans.add((OpenCloseDoorInfoBean)temp.get(0));
									pageAdapter.notifyDataSetChanged();
									if(query_data_notify_ll.getVisibility() == View.VISIBLE){
										query_data_notify_ll.setVisibility(View.GONE);
										door_record_list.setVisibility(View.VISIBLE);
									}

								}
								
							});

						}
					}
					
					if(openCloseDoorInfoBeans.size()==0){
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								query_data_notify_ll.setVisibility(View.VISIBLE);
								door_record_list.setVisibility(View.GONE);
								query_data_notify_tv.setText("没有开关门数据");
							}
							
						});
					}
				}
			};
			t.setDaemon(true);
			t.start();



		}
		
		public void onResponsTimeout(){
			query_data_notify_ll.setVisibility(View.VISIBLE);
			door_record_list.setVisibility(View.GONE);
			query_data_notify_tv.setText("查询数据超时");
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			query_data_notify_ll.setVisibility(View.VISIBLE);
			door_record_list.setVisibility(View.GONE);
			query_data_notify_tv.setText("查询数据失败");
		}
	}
	
	
	private class DoorRecordInfoReceive extends DeviceReceiveCmdProcess<ReceiveOpenCloseDoorInfo>{

		@Override
		public void onProcess(ReceiveOpenCloseDoorInfo receiveCmdBean) {
			// TODO Auto-generated method stub
			Log.i("MainPageActivity", "ReceiveOpenCloseDoorInfo================");
			if(receiveCmdBean.getCommandHeader().resultCode==0 &&
					queryOpenCloseDoorIdBean!=null){
				OpenCloseDoorInfoBean openCloseDoorInfoBean = new OpenCloseDoorInfoBean();
				openCloseDoorInfoBean.eventCode = receiveCmdBean.eventCode;
				openCloseDoorInfoBean.time = receiveCmdBean.time;
				openCloseDoorInfoBean.type = queryOpenCloseDoorIdBean.type;
				
				openCloseDoorInfoBeans.add(openCloseDoorInfoBean);
				pageAdapter.notifyDataSetChanged();
				
				if(query_data_notify_ll.getVisibility() == View.VISIBLE){
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							query_data_notify_ll.setVisibility(View.GONE);
							door_record_list.setVisibility(View.VISIBLE);
						}
						
					});

				}
				
				DBOperator.getInstance().insert(DBBean.TBOpenCloseDoorInfoBean, openCloseDoorInfoBean);
			}
			queryOpenCloseDoorIdBean = null;
			synchronized(t){
				t.notify();
			}
			
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
	}
}
