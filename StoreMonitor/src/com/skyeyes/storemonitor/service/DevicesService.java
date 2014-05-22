package com.skyeyes.storemonitor.service;

import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.util.PreferenceUtil;
import com.skyeyes.storemonitor.process.DeviceProcessInterface;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceStatusChangeListener;
import com.skyeyes.storemonitor.process.impl.DeviceProcess;

public class DevicesService extends Service implements DeviceStatusChangeListener{
	static String TAG = "DevicesService";
	private static DevicesService instance = null;
	private HashMap<String,DeviceProcessInterface> mDeviceDeviceProcesss = new HashMap<String,DeviceProcessInterface>();
	private HashMap<String,DeviceProcessInterface> mTempDeviceDeviceProcesss = new HashMap<String,DeviceProcessInterface>();
	
	private HashMap<String,HashMap<String,DeviceReceiveCmdProcess>> mStaticDeviceReceiveCmdProcess = 
			new HashMap<String,HashMap<String,DeviceReceiveCmdProcess>>();
	
	private String mCurrentDeviceCode = null;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static DevicesService getInstance(){
		return instance;
	}
	
	public String getCurrentDeviceCode(){
		return mCurrentDeviceCode;
	}
	
	public void onCreate(){
		super.onCreate();
		
		Log.d(TAG, "onCreate................");
		
		String deviceListString = PreferenceUtil.getConfigString(PreferenceUtil.DEVICE_INFO,PreferenceUtil.device_code_list);
		String[] deviceCodes = deviceListString.split(";");
		Log.d("DeviceListConnectService", "start................"+deviceCodes.length+":"+deviceCodes[0]);
		for(String deviceCode:deviceCodes){
			if(!mDeviceDeviceProcesss.containsKey(deviceCode)){
				//初始化回调对象容器
				if(!mStaticDeviceReceiveCmdProcess.containsKey(deviceCode)){
					mStaticDeviceReceiveCmdProcess.put(deviceCode, new HashMap<String,DeviceReceiveCmdProcess>());
				}
				//初始化设备管理器,并登陆设备
				initDeviceProcess(deviceCode);
			}

		}
		instance = this;
	}
	
	public void onDestroy(){
		super.onDestroy();
		Log.d("DevicesService", "onDestroy................");
		instance = null;
		for(DeviceProcessInterface deviceProcess:mDeviceDeviceProcesss.values()){
			deviceProcess.stop();
		}
		mDeviceDeviceProcesss.clear();
	}
	
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);

	}
	
	/**
	 * 选择设备
	 * @param deviceCode
	 */
	public void selectDevice(String deviceCode){
		PreferenceUtil.setSingleConfigInfo(PreferenceUtil.DEVICE_INFO, 
				PreferenceUtil.device_current_code, deviceCode);
		mCurrentDeviceCode = deviceCode;
	}
	
	@Override
	public void onDeviceLogin(String deviceCode, ReceivLogin receivLogin) {
		// TODO Auto-generated method stub
		if(mTempDeviceDeviceProcesss.containsKey(receivLogin.deviceCode)){
			if(receivLogin.getCommandHeader().resultCode ==0){
				mDeviceDeviceProcesss.put(receivLogin.deviceCode, 
						mTempDeviceDeviceProcesss.remove(receivLogin.deviceCode));
				Log.i("DevicesService","mDeviceSocketClients:"+receivLogin.deviceCode);
			}else{
				Toast.makeText(this, "登陆失败:"+receivLogin.getCommandHeader().errorInfo, Toast.LENGTH_SHORT).show();
				mTempDeviceDeviceProcesss.remove(deviceCode).stop();
			}
		}
	}
	
	@Override
	public void onDeviceStatusChange(String deviceCode,ReceiveCmdBean receiveCmdBean) {
		// TODO Auto-generated method stub
		
	}
	
	public void onSkyeyeNetworkException(final String deviceCode,NetworkException ex){
		if(mDeviceDeviceProcesss.containsKey(deviceCode)){
			mDeviceDeviceProcesss.remove(deviceCode).stop();
		}
		new Thread(){
			public void run(){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				initDeviceProcess(deviceCode);
			}
		}.start();
	} 
	
	private void initDeviceProcess(String deviceCode){
		DeviceProcess deviceProcess = new DeviceProcess(deviceCode,this);
		mTempDeviceDeviceProcesss.put(deviceCode, deviceProcess);
		deviceProcess.setCmdProcessMaps(mStaticDeviceReceiveCmdProcess.get(deviceCode));
		//登陆
		String userName = PreferenceUtil.getConfigString(PreferenceUtil.ACCOUNT_IFNO, PreferenceUtil.account_login_name);
		String userPsd = PreferenceUtil.getConfigString(PreferenceUtil.ACCOUNT_IFNO, PreferenceUtil.account_login_psd);
		deviceProcess.loginDevice(userName, userPsd, (byte)1);
	}
	
	
	public static void sendCmd(SendCmdBean sendCmdBean) {
		sendCmd(sendCmdBean,null);
	}
	
	public static void sendCmd(SendCmdBean sendCmdBean,DeviceReceiveCmdProcess receiveCmdProcess) {
		if(instance == null)
			return;
		sendCmd(instance.getCurrentDeviceCode(),sendCmdBean,receiveCmdProcess);
	}
	
	public static void sendCmd(String deviceCode,SendCmdBean sendCmdBean,DeviceReceiveCmdProcess receiveCmdProcess) {
		if(instance == null || 
				instance.mDeviceDeviceProcesss.get(deviceCode) == null)
			return;
		instance.mDeviceDeviceProcesss.get(deviceCode).sendCmd(sendCmdBean,receiveCmdProcess);
	}
	
	public static void registerCmdProcess(String className,DeviceReceiveCmdProcess receiveCmdProcess){
		if(instance==null)
			return;
		String deviceListString = PreferenceUtil.getConfigString(PreferenceUtil.DEVICE_INFO,PreferenceUtil.device_code_list);
		String[] deviceCodes = deviceListString.split(";");
		for(String deviceCode:deviceCodes){
			Log.e(TAG, "registerCmdProcess:"+deviceCode+":"+className);
			instance.mStaticDeviceReceiveCmdProcess.get(deviceCode).put(className,receiveCmdProcess);
		}
	}
	
	public static void unRegisterCmdProcess(String className){
		if(instance==null)
			return;
		String deviceListString = PreferenceUtil.getConfigString(PreferenceUtil.DEVICE_INFO,PreferenceUtil.device_code_list);
		String[] deviceCodes = deviceListString.split(";");
		for(String deviceCode:deviceCodes){
			instance.mStaticDeviceReceiveCmdProcess.get(deviceCode).remove(className);
		}
	}

	public HashMap<String, DeviceProcessInterface> getDeviceDeviceProcesss() {
		return mDeviceDeviceProcesss;
	}

	public HashMap<String, DeviceProcessInterface> getTempDeviceDeviceProcesss() {
		return mTempDeviceDeviceProcesss;
	}

	
	
}


