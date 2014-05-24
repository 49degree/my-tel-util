package com.skyeyes.storemonitor.activity;

import h264.com.H264Android;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceiveRealVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveStopVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveVideoData;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

public class VideoPlayActivity extends H264Android{
	boolean start = false;
	 byte chennalId = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.e("VideoPlayActivity", "onCreate================");
        super.onCreate(savedInstanceState);     

        chennalId = getIntent().getExtras().getByte("chennalId");

        
    }
    
    public void onResume(){
    	super.onResume();
		notify.setVisibility(View.VISIBLE);
		vv.setVisibility(View.GONE);
		if(chennalId>-1){
			VideoDataReceive video = new VideoDataReceive();
			DevicesService.getInstance().registerCmdProcess(ReceiveVideoData.class.getSimpleName(), video);
			SendObjectParams sendObjectParams = new SendObjectParams();
			Object[] params = new Object[] { chennalId};
			try {
				sendObjectParams.setParams(REQUST.cmdReqRealVideo, params);
				System.out.println("cmdReqRealVideo入参数：" + sendObjectParams.toString());
				DevicesService.sendCmd(sendObjectParams, new RealVideoReceive());
			} catch (CommandParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			start = true;
		}
		
    }
    
    public void onPause(){
    	super.onPause();
    	if(start){
        	
        	DevicesService.unRegisterCmdProcess("ReceiveVideoData");
    		SendObjectParams sendObjectParams = new SendObjectParams();
    		Object[] params = new Object[] {  };
    		try {
    			sendObjectParams.setParams(REQUST.cmdReqStopVideo, params);
    			DevicesService.getInstance().sendCmd(sendObjectParams, new StopVideoReceive());
    		
    		} catch (CommandParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}

    }

    public void onDestroy(){
    	Log.e("VideoPlayActivity", "onDestroy================");

		super.onDestroy();
    }

    
	@Override
	public void reviceFrame(String num) {
		// TODO Auto-generated method stub
		Log.e("VideoPlayActivity", "reviceFrame================"+num);
		if(vv.getVisibility()!=View.VISIBLE)
			vv.setVisibility(View.VISIBLE);
		if(vv.getVisibility()!=View.GONE)
			notify.setVisibility(View.GONE);
	}
	
	

	
	private class RealVideoReceive extends DeviceReceiveCmdProcess<ReceiveRealVideo>{
		@Override
		public void onProcess(ReceiveRealVideo receiveCmdBean) {
			// TODO Auto-generated method stub
			//打开视频播放界面
			Log.i("MainPageActivity", "MainPageActivity.this.startActivity(it)================");
			
			h264Play();
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}
	}
    
	private class StopVideoReceive extends DeviceReceiveCmdProcess<ReceiveStopVideo>{
		@Override
		public void onProcess(ReceiveStopVideo receiveCmdBean) {
			Log.i("VideoPlayActivity", "ReceiveStopVideo================");
			// TODO Auto-generated method stub
			h264Stop();
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}
		
	}
	
    private class VideoDataReceive extends DeviceReceiveCmdProcess<ReceiveVideoData>{
		long lastDataTime = 0;
		@Override
		public void onProcess(ReceiveVideoData receiveCmdBean) {
			Log.e("MainPageActivity", "VideoDataReceive================");
			// TODO Auto-generated method stub
			sendStream(receiveCmdBean.data);
			responseVideoData(receiveCmdBean);
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}
		
		// 回复视频数据
		public void responseVideoData(ReceiveCmdBean receiveCmdBean) {
			if(lastDataTime == 0)
				lastDataTime = System.currentTimeMillis();
			if(System.currentTimeMillis()-lastDataTime>700){
				byte cmdId = receiveCmdBean.getCommandHeader().cmdId;
				SendObjectParams sendObjectParams = new SendObjectParams();
				sendObjectParams.setCommandHeader(receiveCmdBean.getCommandHeader());
				Object[] params = new Object[] {};
				try {
					sendObjectParams.setParams(REQUST.cmdRevFrame, params);
					sendObjectParams.getCommandHeader().cmdCode = 0 ;
					sendObjectParams.getCommandHeader().cmdId  = cmdId;
					System.out.println("testResponseVideoData入参数："+ sendObjectParams.toString());
					
					DevicesService.sendCmd(sendObjectParams,null);
				} catch (CommandParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lastDataTime = System.currentTimeMillis();
			}
			
		}
	}
}
