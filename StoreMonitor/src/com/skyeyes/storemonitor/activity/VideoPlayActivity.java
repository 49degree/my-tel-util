package com.skyeyes.storemonitor.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceiveHistoryVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveRealVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveStopVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveVideoData;
import com.skyeyes.base.cmd.bean.impl.ReceiveVideoFinish;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.h264.H264Decoder.DecodeSuccCallback;
import com.skyeyes.base.h264.JavaH264Decoder;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;
import com.skyeyes.storemonitor.view.H264VideoView;

public class VideoPlayActivity extends Activity {
	static String TAG = "VideoPlayActivity";
	
	boolean start = false;
	int videoType = -1;//0,实时视频，1，历史视频，2报警视频
	
	byte chennalId = -1;
	
	String startTime;
	short videoLong;
	
	String alarmId;//报警ID
	int isPushAlarm;
	
	
	

	protected FrameLayout main;
	private H264VideoView videoView;
	protected LinearLayout notify;
	protected TextView notifyText;
	protected Display display;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("VideoPlayActivity", "onCreate================");
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏 
        requestWindowFeature(Window.FEATURE_NO_TITLE);    //全屏            
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		chennalId = getIntent().getExtras().getByte("chennalId");
		videoType = getIntent().getExtras().getInt("videoType");
		if(videoType==1){
			startTime = getIntent().getExtras().getString("startTime");
			videoLong = getIntent().getExtras().getShort("videoLong");
		}else if(videoType==2){
			alarmId = getIntent().getExtras().getString("alarmId");
			isPushAlarm = getIntent().getExtras().getInt("isPushAlarm");
			if(isPushAlarm==1){
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// 取消的只是当前Context的Notification
				mNotificationManager.cancel(DevicesService.NOTIFICATION_ID);
			}
		}

		
		Log.e(TAG, videoType+":"+chennalId+":"+startTime+":"+videoLong+":"+alarmId);
		
		WindowManager windowManager = getWindowManager();
		display = windowManager.getDefaultDisplay();

		main = new FrameLayout(this);
		main.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));

		videoView =new H264VideoView(this,display,new DecodeSuccCallback(){
			@Override
			public void onDecodeSucc(JavaH264Decoder decoder ,Bitmap bitmap) {
				// TODO Auto-generated method stub
				//Log.i("DecoderCallback", "onDecodeSucc================"+(videoView.getVisibility() != View.VISIBLE));
				if (videoView.getVisibility() != View.VISIBLE)
					videoView.setVisibility(View.VISIBLE);
				if (notify.getVisibility() != View.GONE)
					notify.setVisibility(View.GONE);

			}
		});
		videoView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		main.addView(videoView);

		notify = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.app_video_play_notify, null);
		notifyText = (TextView) notify.findViewById(R.id.play_video_notify_tv);
		main.addView(notify);

		setContentView(main);

	}

	public void onResume() {
		super.onResume();
		notify.setVisibility(View.VISIBLE);
		videoView.setVisibility(View.GONE);
		
		DevicesService.registerCmdProcess(
				"ReceiveVideoData", new VideoDataReceive());
		DevicesService.registerCmdProcess(
				"ReceiveVideoFinish", new VideoFinsishReceive());
		DeviceReceiveCmdProcess deviceReceiveCmdProcess = null;
		SendObjectParams sendObjectParams = null;
		if (chennalId > -1) {
			if(videoType==1){//1，历史视频
				sendObjectParams = new SendObjectParams();
				Object[] params = new Object[] { chennalId,startTime,videoLong};
				try {
					sendObjectParams.setParams(REQUST.cmdReqHistoryVideo, params);
					System.out.println("cmdReqHistoryVideo入参数："
							+ sendObjectParams.toString());
					deviceReceiveCmdProcess = new HistoryVideoReceive();
				} catch (CommandParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(videoType==2){//，2报警视频
				sendObjectParams = new SendObjectParams();
				Object[] params = new Object[] { chennalId, alarmId};
				try {
					sendObjectParams.setParams(REQUST.cmdReqAlarmVideo, params);
					System.out.println("cmdReqAlarmVideo入参数："
							+ sendObjectParams.toString());
					deviceReceiveCmdProcess = new HistoryVideoReceive();
				} catch (CommandParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{//0,实时视频
				sendObjectParams = new SendObjectParams();
				Object[] params = new Object[] { chennalId };
				try {
					sendObjectParams.setParams(REQUST.cmdReqRealVideo, params);
					System.out.println("cmdReqRealVideo入参数："
							+ sendObjectParams.toString());
					deviceReceiveCmdProcess = new RealVideoReceive();
					
				} catch (CommandParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(deviceReceiveCmdProcess!=null){
				deviceReceiveCmdProcess.setTimeout(20*1000);
				DevicesService.sendCmd(sendObjectParams, deviceReceiveCmdProcess);
				start = true;
			}
			
			
		}

	}

	public void onPause() {
		super.onPause();
		if (start) {
			DevicesService.unRegisterCmdProcess("ReceiveVideoData");
			DevicesService.unRegisterCmdProcess("ReceiveVideoFinish");
			SendObjectParams sendObjectParams = new SendObjectParams();
			Object[] params = new Object[] {};
			try {
				sendObjectParams.setParams(REQUST.cmdReqStopVideo, params);
				DevicesService.getInstance().sendCmd(sendObjectParams,
						new StopVideoReceive());

			} catch (CommandParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void onDestroy() {
		Log.e("VideoPlayActivity", "onDestroy================");
		videoView.toStopPlay();
		super.onDestroy();
	}

	private class RealVideoReceive extends
			DeviceReceiveCmdProcess<ReceiveRealVideo> {
		@Override
		public void onProcess(ReceiveRealVideo receiveCmdBean) {
			// TODO Auto-generated method stub
			// 打开视频播放界面
			if(receiveCmdBean.getCommandHeader().resultCode==0){
				videoView.toStartPlay();
			}else{
				notifyText.setText(receiveCmdBean.getCommandHeader().errorInfo);
			}
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			notifyText.setText(errinfo);
		}
		
		public void onResponsTimeout(){
			notifyText.setText("连接超时....");
		}
	}


	
	private class HistoryVideoReceive extends
			DeviceReceiveCmdProcess<ReceiveHistoryVideo> {
		@Override
		public void onProcess(ReceiveHistoryVideo receiveCmdBean) {
			// TODO Auto-generated method stub
			// 打开视频播放界面
			if(receiveCmdBean.getCommandHeader().resultCode==0){
				videoView.toStartPlay();
			}else{
				notifyText.setText(receiveCmdBean.getCommandHeader().errorInfo);
			}
			
			
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			notifyText.setText(errinfo);
		}
		
		public void onResponsTimeout(){
			notifyText.setText("连接超时....");
		}
	}

	private class StopVideoReceive extends
			DeviceReceiveCmdProcess<ReceiveStopVideo> {
		@Override
		public void onProcess(ReceiveStopVideo receiveCmdBean) {
			Log.i("VideoPlayActivity", "ReceiveStopVideo================");
			// TODO Auto-generated method stub
			videoView.toStopPlay();
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}
	}
	
	private class VideoFinsishReceive extends
			DeviceReceiveCmdProcess<ReceiveVideoFinish> {
		@Override
		public void onProcess(ReceiveVideoFinish receiveCmdBean) {
			Log.i("VideoPlayActivity", "ReceiveStopVideo================");
			// TODO Auto-generated method stub
			videoView.toStopPlay();
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}
	}

	private class VideoDataReceive extends
			DeviceReceiveCmdProcess<ReceiveVideoData> {
		long lastDataTime = 0;

		@Override
		public void onProcess(ReceiveVideoData receiveCmdBean) {
			//Log.e("MainPageActivity", "VideoDataReceive================");
			// TODO Auto-generated method stub
			videoView.sendStream(receiveCmdBean.data);
			responseVideoData(receiveCmdBean);
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
		}

		// 回复视频数据
		public void responseVideoData(ReceiveCmdBean receiveCmdBean) {
			if (lastDataTime == 0)
				lastDataTime = System.currentTimeMillis();
			if (System.currentTimeMillis() - lastDataTime > 700) {
				byte cmdId = receiveCmdBean.getCommandHeader().cmdId;
				SendObjectParams sendObjectParams = new SendObjectParams();
				sendObjectParams.setCommandHeader(receiveCmdBean
						.getCommandHeader());
				Object[] params = new Object[] {};
				try {
					sendObjectParams.setParams(REQUST.cmdRevFrame, params);
					sendObjectParams.getCommandHeader().cmdCode = 0;
					sendObjectParams.getCommandHeader().cmdId = cmdId;
					System.out.println("testResponseVideoData入参数："
							+ sendObjectParams.toString());

					DevicesService.sendCmd(sendObjectParams, null);
				} catch (CommandParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lastDataTime = System.currentTimeMillis();
			}

		}
	}
	


	
}
