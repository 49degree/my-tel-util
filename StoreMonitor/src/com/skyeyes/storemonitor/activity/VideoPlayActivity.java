package com.skyeyes.storemonitor.activity;

import h264.com.H264Android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.os.Bundle;
import android.util.Log;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceiveStopVideo;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;

public class VideoPlayActivity extends H264Android{
	
	private static VideoPlayActivity instance = null;
	
	public static VideoPlayActivity getInstance(){
		return instance;
	}
	
	boolean stop = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.e("VideoPlayActivity", "onCreate================");
        super.onCreate(savedInstanceState);     
        instance = this;
        
    }
    
    public void onResume(){
    	super.onResume();
    	h264Play();
    	//stop = false;
    	//new ReadData().start();
    	
    }
    
    public void onPause(){
    	super.onPause();
    	h264Stop();	
    	//stop = true;
    	
    }

    public void onDestroy(){
    	
    	Log.e("VideoPlayActivity", "onDestroy================");
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {  };
		try {
			sendObjectParams.setParams(REQUST.cmdReqStopVideo, params);
			DevicesService.getInstance().sendCmd(sendObjectParams, new StopVideoReceive());
		
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
    }

    
	@Override
	public void reviceFrame(String num) {
		// TODO Auto-generated method stub
		Log.e("VideoPlayActivity", "reviceFrame================"+num);
	}
	
	
	private class StopVideoReceive extends DeviceReceiveCmdProcess<ReceiveStopVideo>{

		@Override
		public void onProcess(ReceiveStopVideo receiveCmdBean) {
			Log.i("VideoPlayActivity", "ReceiveStopVideo================");
			// TODO Auto-generated method stub
			
			DevicesService.unRegisterCmdProcess("ReceiveVideoData");
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class ReadData extends Thread{
		public void run(){
			h264Play();
			//打开文件
			FileInputStream fin = null;
		    try {
			    /* the codec gives us the frame size, in samples */
		    	fin = new FileInputStream(new File("/sdcard/video.data"));
		    	byte[] buffer = new byte[1024];
		    	int len = 0;
		    	while(!stop && (len=fin.read(buffer))>-1){
		    		byte[] inBuffer = new byte[len];
		    		System.arraycopy(buffer, 0, inBuffer, 0, len);
		    		sendStream(inBuffer);
		    	}
		    }catch(Exception e){
		    	
		    }finally{
		    	try {
					fin.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		    h264Stop();	
		}
	}

}
