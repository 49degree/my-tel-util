package com.skyeyes.storemonitor.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.skyeyes.base.bean.AlarmIdBean;
import com.skyeyes.base.bean.AlarmInfoBean;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveAlarmInfo;
import com.skyeyes.base.cmd.bean.impl.ReceiveAlarmList;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.db.DBBean;
import com.skyeyes.base.db.DBOperator;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.h264.H264DecoderException;
import com.skyeyes.base.h264.JavaH264Decoder;
import com.skyeyes.base.h264.JavaH264Decoder.DecodeSuccCallback;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.PreferenceUtil;
import com.skyeyes.base.util.TypeConversion;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.StoreMonitorApplication;
import com.skyeyes.storemonitor.activity.adapter.AlarmRecordViewAdapter;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

public class AlermRecorderActivity extends Activity {
	private TopTitleView topTitleView;
	private ListView alarm_record_list;
	private LinearLayout query_data_notify_ll;
	private TextView query_data_notify_tv;
	private ArrayList<AlarmInfoBean> alarmInfoBeans = new ArrayList<AlarmInfoBean>();
	private AlarmRecordViewAdapter pageAdapter;
	private String endTime;
	
	private boolean hasQuery = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.alerm_record_view);

		query_data_notify_ll = (LinearLayout)findViewById(R.id.query_data_notify_ll);
		query_data_notify_tv = (TextView)findViewById(R.id.query_data_notify_tv);
		alarm_record_list = (ListView)findViewById(R.id.alarm_record_list);
		topTitleView = (TopTitleView) findViewById(R.id.ar_topView);

		query_data_notify_ll.setVisibility(View.VISIBLE);
		alarm_record_list.setVisibility(View.GONE);

		topTitleView.setOnMenuButtonClickListener(new OnClickListenerCallback() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				finish();
			}
		});

		//testView();
	}

    public void onResume(){
    	super.onResume();
    	if(hasQuery)
    		return;
		if(StoreMonitorApplication.getInstance().getReceivLogin()==null){
			query_data_notify_tv.setText("未登陆设备");
		}else{
			hasQuery = true;
			query_data_notify_tv.setText("正在查询报警数据,请稍后...");
			pageAdapter=new AlarmRecordViewAdapter(this,alarmInfoBeans);
			alarm_record_list.setAdapter(pageAdapter);
			//查询通道图片
			endTime = DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YMDHMS);
			String lastTime = PreferenceUtil.getConfigString(PreferenceUtil.DEVICE_INFO, PreferenceUtil.device_alarm_query_last_time);
			String startTime = DateUtil.getTimeStringFormat(new Date(0), DateUtil.TIME_FORMAT_YMDHMS);//DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YMDHMS);
			if(!"".equals(lastTime)){
				startTime = lastTime;
			}else{
				startTime = "2014-05-26 00:00:00";
			}
			
			SendObjectParams sendObjectParams = new SendObjectParams();
			Object[] params = new Object[] {
					//DateUtil.getFrontMonthDateString()+" 00:00:00",
					startTime,
					endTime};
			try {
				sendObjectParams.setParams(REQUST.cmdReqAlarmList, params);
				System.out.println("cmdReqAlarmList入参数：" + sendObjectParams.toString());
				
				AlarmListReceive alarmListReceive = new AlarmListReceive();
				alarmListReceive.setTimeout(10*1000);
				DevicesService.sendCmd(sendObjectParams, alarmListReceive);
			} catch (CommandParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
	
	private AlarmIdBean alarmIdBean; 
	Thread t;
	private class AlarmListReceive extends DeviceReceiveCmdProcess<ReceiveAlarmList>{

		@Override
		public void onProcess(ReceiveAlarmList receiveCmdBean) {
			// TODO Auto-generated method stub
			if(receiveCmdBean.getCommandHeader().resultCode==0){
				PreferenceUtil.setSingleConfigInfo(PreferenceUtil.DEVICE_INFO, PreferenceUtil.device_alarm_query_last_time,endTime);
			}
			
			Log.i("AlarmListReceive", "AlarmListReceive================");
			if(receiveCmdBean.alarmBeans.size()>0){
				for(int i=receiveCmdBean.alarmBeans.size()-1;i>=0;i--){
					AlarmIdBean alarmIdBean = new AlarmIdBean();
					alarmIdBean.eventCode = receiveCmdBean.alarmBeans.get(i).des;
					DBOperator.getInstance().insert(DBBean.TBAlarmIdBean, alarmIdBean);
				}
			}
			
			queryDataFromDB();



		}
		
		public void onResponsTimeout(){
//			query_data_notify_ll.setVisibility(View.VISIBLE);
//			alarm_record_list.setVisibility(View.GONE);
//			query_data_notify_tv.setText("查询数据超时");
			queryDataFromDB();
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			query_data_notify_ll.setVisibility(View.VISIBLE);
			alarm_record_list.setVisibility(View.GONE);
			query_data_notify_tv.setText("查询数据失败");
		}
	}
	
	
	private class AlarmInfoReceive extends DeviceReceiveCmdProcess<ReceiveAlarmInfo>{

		@Override
		public void onProcess(ReceiveAlarmInfo receiveCmdBean) {
			// TODO Auto-generated method stub
			Log.i("MainPageActivity", "ReceiveOpenCloseDoorInfo================");
			if(receiveCmdBean.getCommandHeader().resultCode==0 && alarmIdBean!=null){
				final AlarmInfoBean alarmInfoBean = new AlarmInfoBean();
				alarmInfoBean.eventCode = receiveCmdBean.eventCode;
				alarmInfoBean.time = receiveCmdBean.time;
				alarmInfoBean.type = receiveCmdBean.alarmType;
				alarmInfoBean.chennalId = receiveCmdBean.chennalId;
				alarmInfoBean.hasLook = false;
				alarmInfoBean.des="没有描述信息";
				if(receiveCmdBean.pic==null||receiveCmdBean.pic.length<4){
				}else{
					if(TypeConversion.bytesToIntEx(receiveCmdBean.pic, 0)==1){//头4个字节为0x00000001的为hd264
					    try {
							JavaH264Decoder decoder = new JavaH264Decoder(new DecodeSuccCallback(){
								@Override
								public void onDecodeSucc(JavaH264Decoder decoder ,Bitmap bitmap) {
									// TODO Auto-generated method stub
									Log.i("DecoderCallback", "onDecodeSucc================");
									ByteArrayOutputStream os = new ByteArrayOutputStream();  
									/** 
									* Bitmap.CompressFormat.JPEG 和 Bitmap.CompressFormat.PNG 
									* JPEG 与 PNG 的是区别在于 JPEG是有损数据图像，PNG使用从LZ77派生的无损数据压缩算法。 
									* 这里建议使用PNG格式保存 
									* 100 表示的是质量为100%。当然，也可以改变成你所需要的百分比质量。 
									* os 是定义的字节输出流 
									*  
									* .compress() 方法是将Bitmap压缩成指定格式和质量的输出流 
									*/  
									bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);  
									alarmInfoBean.pic = os.toByteArray();
									decoder.toStop();
								}
								
							});
							decoder.sendStream(receiveCmdBean.pic);
							
						} catch (H264DecoderException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}else{
						int tempValue = 0;
						if(receiveCmdBean.pic.length>128){
							
							for(int i=0;i<128;i++){
								tempValue |=receiveCmdBean.pic[i];
							}
						}
						if(tempValue!=0){
							alarmInfoBean.pic = receiveCmdBean.pic;
						}

					}
				}

				
				alarmInfoBeans.add(alarmInfoBean);
				pageAdapter.notifyDataSetChanged();
				
				if(query_data_notify_ll.getVisibility() == View.VISIBLE){
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							query_data_notify_ll.setVisibility(View.GONE);
							alarm_record_list.setVisibility(View.VISIBLE);
						}
						
					});

				}
				
				alarmInfoBean.set_id((int)DBOperator.getInstance().insert(DBBean.TBAlarmInfoBean, alarmInfoBean));
			}
			alarmIdBean = null;
			synchronized(t){
				t.notify();
			}
			
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private void queryDataFromDB(){
		final AlarmInfoReceive alarmInfoReceive = new AlarmInfoReceive();
		t = new Thread(){
			public void run(){
				List<Object> alarmIdBeans = DBOperator.getInstance().queryBeanList(DBBean.TBAlarmIdBean,null);
				HashMap params = new HashMap<String,String>();
				
				SendObjectParams  sendObjectParams = new SendObjectParams();
				Object[] cmdParams = new Object[1];
				for(int i=alarmIdBeans.size()-1;i>=0;i--){
					params.put("eventCode=", ((AlarmIdBean)alarmIdBeans.get(i)).eventCode);
					final List<Object> temp = DBOperator.getInstance().queryBeanList(DBBean.TBAlarmInfoBean, params);
					Log.i("alarmIdBeans", "alarmIdBeans================temp:"+temp.size());
					if(temp.size()==0){
						//查询详细信息
						if(alarmIdBean != null){
							try {
								synchronized (this) {
									this.wait();
								}
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						alarmIdBean = ((AlarmIdBean)alarmIdBeans.get(i));
						cmdParams[0] = alarmIdBean.eventCode;
						try {
							sendObjectParams.setParams(REQUST.cmdReqAlarmInfo, cmdParams);
							System.out.println("cmdReqAlarmInfo入参数：" + sendObjectParams.toString());
							
							alarmInfoReceive.setTimeout(10*1000);
							DevicesService.sendCmd(sendObjectParams, alarmInfoReceive);
						} catch (CommandParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								alarmInfoBeans.add((AlarmInfoBean)temp.get(0));
								pageAdapter.notifyDataSetChanged();
								if(query_data_notify_ll.getVisibility() == View.VISIBLE){
									query_data_notify_ll.setVisibility(View.GONE);
									alarm_record_list.setVisibility(View.VISIBLE);
								}
							}
							
						});
					}
				}
				

				if(alarmInfoBeans.size()==0){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							if(alarmInfoBeans.size()==0){
								query_data_notify_ll.setVisibility(View.VISIBLE);
								alarm_record_list.setVisibility(View.GONE);
								query_data_notify_tv.setText("没有开关门数据");
							}
						}
						
					});
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}
}
