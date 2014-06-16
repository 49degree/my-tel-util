package com.skyeyes.storemonitor.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.impl.ReceivReadDeviceNetInfo;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;

public class SendHeartReceiver extends BroadcastReceiver{

	public static long lastConnectTime = 0;
	ReadDeviceNetInfoProcess readDeviceNetInfoProcess = new ReadDeviceNetInfoProcess();
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.e("SendHeartReceiver", "lastConnectTime:"+lastConnectTime+":SystemClock.elapsedRealtime():"+SystemClock.elapsedRealtime());
		if(lastConnectTime!=0&&SystemClock.elapsedRealtime()-lastConnectTime>90*1000){
			//超时
			if(DevicesService.getInstance()!=null){
				DevicesService.getInstance().showErrorNotification( "无法连接服务器，正在重新连接...");
				DevicesService.getInstance().clearLoginInfo();
				DevicesService.getInstance().initDeviceProcess(DevicesService.getInstance().mCurrentDeviceCode);

			}
			}else{
				testConnetStatus();
		}
		
		if(lastConnectTime==0)
			lastConnectTime = SystemClock.elapsedRealtime();
	}
	
	
	
	private void testConnetStatus(){
		//查询通道图片
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdReadDeviceIp, params);
			System.out.println("cmdReadDeviceIp入参数：" + sendObjectParams.toString());
			readDeviceNetInfoProcess.setTimeout(60*1000);
			DevicesService.sendCmd(sendObjectParams, readDeviceNetInfoProcess);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 查询设备IP信息，用于测试连接
	 * @author Administrator
	 *
	 */
	public class ReadDeviceNetInfoProcess extends DeviceReceiveCmdProcess<ReceivReadDeviceNetInfo>{

		public void onProcess(ReceivReadDeviceNetInfo receiveCmdBean) {
			updateLastconnectTime();
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static void updateLastconnectTime(){
		try{
			lastConnectTime = SystemClock.elapsedRealtime();
		}catch(Exception e){
			
		}
	}

}
