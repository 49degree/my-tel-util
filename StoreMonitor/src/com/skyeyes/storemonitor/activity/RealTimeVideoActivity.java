package com.skyeyes.storemonitor.activity;

import android.os.Bundle;
import android.util.Log;

import com.skyeyes.base.activity.BaseActivity;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceChannelListStatus;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

public class RealTimeVideoActivity extends BaseActivity {
	public static String TAG = "RealTimeVideoActivity";
	//TextView store_login_id_tv = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.setContentView(R.layout.app_video_page);
		//store_login_id_tv = (TextView)findViewById(R.id.store_login_id_tv);
    }
    
    protected void onStart(){
    	super.onStart();
    	//查询视频通道及通道视频截图
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdReqVideoChannelListStatus, params);
			Log.d("RealTimeVideoActivity","getChannelListStatus入参数：" + sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	DevicesService.sendCmd(sendObjectParams, new ReceiveDeviceChannelListStatusProcess());
    }


	
	private class ReceiveDeviceChannelListStatusProcess extends DeviceReceiveCmdProcess<ReceiveDeviceChannelListStatus>{

		@Override
		public void onProcess(ReceiveDeviceChannelListStatus receiveCmdBean) {
			// TODO Auto-generated method stub
			Log.i(TAG, receiveCmdBean.toString());
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
	


	}
    
    
}
