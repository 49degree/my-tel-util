package com.skyeyes.storemonitor.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skyeyes.base.activity.BaseActivity;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceEnv;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceInfo;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceStatus;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;
/** 设备状态*/
public class DevicesStatusActivity extends BaseActivity implements OnClickListener{
	private TopTitleView topTitleView;
	private Button alermRecord;
	private Button protectBtn;
	private int currentStatus;
	
	private TextView ds_value;
	private TextView p_value;
	private TextView net_value;
	private TextView routing_value;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices_status_view);
		topTitleView = (TopTitleView) findViewById(R.id.ds_topView);
		alermRecord = (Button)findViewById(R.id.alermRecord);
		protectBtn = (Button)findViewById(R.id.protect);
		
		ds_value = (TextView)findViewById(R.id.ds_value);
		p_value = (TextView)findViewById(R.id.p_value);
		net_value = (TextView)findViewById(R.id.net_value);
		routing_value = (TextView)findViewById(R.id.routing_value);
		
		protectBtn.setOnClickListener(this);
		alermRecord.setOnClickListener(this);
		topTitleView.setOnMenuButtonClickListener(new OnClickListenerCallback() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				HomeActivity.getInstance().toggleMenu();

			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getDeviceStatus();
		queryDeviceEnv();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.alermRecord:
			Intent intent = new Intent();
			intent.setClass(DevicesStatusActivity.this, AlermRecorderActivity.class);
			startActivity(intent);
			break;
		case R.id.protect:

			switch (currentStatus) {
			case 0:		
				changeDeviceStatus(5);
				break;
			case 5:
				changeDeviceStatus(0);
				break;
			default:
				break;
			}

			break;
		default:
			break;
		}
	}
	
	private void changeDeviceStatus(int status) {
		SendObjectParams sendObjectParams = new SendObjectParams();

		Object[] params = new Object[] { status };
		try {
			sendObjectParams.setParams(REQUST.cmdSendActive, params);
			System.out
					.println("changeDeviceStatus入参数：" + sendObjectParams.toString());
			SetDeviceStatusReceive setdeviceStatusReceive;
			if(status==0){
				 setdeviceStatusReceive = new SetDeviceStatusReceive(true);
			} else {
				setdeviceStatusReceive = new SetDeviceStatusReceive(false);

			}
			DevicesService.sendCmd(sendObjectParams, setdeviceStatusReceive);
			
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void getDeviceStatus(){

		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdGetActive, params);
			DeviceStatusReceive deviceStatusReceive = new DeviceStatusReceive();

			DevicesService.sendCmd(sendObjectParams,deviceStatusReceive);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	@SuppressLint("HandlerLeak")
	private class SetDeviceStatusReceive extends DeviceReceiveCmdProcess<ReceiveDeviceInfo>{
		boolean isProtect;
		public SetDeviceStatusReceive(boolean protect) {
			// TODO Auto-generated constructor stub
			this.isProtect = protect;
		}
		@Override
		public void onProcess(ReceiveDeviceInfo receiveCmdBean) {
			// TODO Auto-generated method stub
				if(receiveCmdBean.getCommandHeader().resultCode == 0){
					Toast.makeText(DevicesStatusActivity.this, isProtect?"布防成功":"撤防成功", Toast.LENGTH_SHORT).show();
					currentStatus = (isProtect?0:5);
					
			        int sdk = android.os.Build.VERSION.SDK_INT;
			        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			        	protectBtn.setBackgroundDrawable(getResources().getDrawable(isProtect?R.drawable.device_rm_protect:R.drawable.device_set_protect));
			        } else {
			        	protectBtn.setBackground(getResources().getDrawable(isProtect?R.drawable.device_rm_protect:R.drawable.device_set_protect));
			        }
				}
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}
		
	}
	
	private class DeviceStatusReceive extends DeviceReceiveCmdProcess<ReceiveDeviceStatus>{
		public void onProcess(ReceiveDeviceStatus receiveCmdBean) {
			// TODO Auto-generated method stub

				if(receiveCmdBean.getCommandHeader().resultCode == 0){

					currentStatus = (int)receiveCmdBean.deviceStatus;
					Drawable res = null;
					if(currentStatus==5){
						res = getResources().getDrawable(R.drawable.device_set_protect);
						p_value.setText("撤防");
					} else {
						res =getResources().getDrawable(R.drawable.device_rm_protect);
						p_value.setText("布防");
					}
					
			        int sdk = android.os.Build.VERSION.SDK_INT;
			        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			        	protectBtn.setBackgroundDrawable(res);
			        } else {
			        	protectBtn.setBackground(res);
			        }
				}
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}
		
	}
	
	private void queryDeviceEnv(){
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdReadDeviceEnv, params);
			DeviceEnvReceive deviceStatusReceive = new DeviceEnvReceive();

			DevicesService.sendCmd(sendObjectParams,deviceStatusReceive);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private class DeviceEnvReceive extends DeviceReceiveCmdProcess<ReceiveDeviceEnv> {
		public void onProcess(ReceiveDeviceEnv receiveCmdBean) {
			// TODO Auto-generated method stub
//			ds_value = (TextView)findViewById(R.id.ds_value);
//			p_value = (TextView)findViewById(R.id.p_value);
//			net_value = (TextView)findViewById(R.id.net_value);
//			routing_value = (TextView)findViewById(R.id.routing_value);
			if (receiveCmdBean.getCommandHeader().resultCode == 0) {
				switch(receiveCmdBean.center){
				//服务中心状态 0＝欠费，1＝禁用，2＝故障，3＝正常
					case 0:
						ds_value.setText("欠费");
						break;
					case 1:
						ds_value.setText("禁用");
						break;
					case 2:
						ds_value.setText("故障");
						break;
					case 3:
						ds_value.setText("正常");
						break;
				}
				
				switch(receiveCmdBean.netStatus){
					//本地网络状态 0＝未知，1＝禁用，2＝故障，3＝正常
					case 0:
						net_value.setText("未知");
						break;
					case 1:
						net_value.setText("禁用");
						break;
					case 2:
						net_value.setText("故障");
						break;
					case 3:
						net_value.setText("正常");
						break;
				}
				
				routing_value.setText("有线");
			}
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}

	}
	
}
