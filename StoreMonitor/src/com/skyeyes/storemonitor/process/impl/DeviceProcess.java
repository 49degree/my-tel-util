package com.skyeyes.storemonitor.process.impl;

import java.util.HashMap;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.skyeyes.base.BaseSocketHandler;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceRegisterInfo;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.network.SkyeyeNetworkClient;
import com.skyeyes.base.network.impl.SkyeyeSocketClient;
import com.skyeyes.base.util.PreferenceUtil;
import com.skyeyes.base.util.ViewUtils;
import com.skyeyes.storemonitor.StoreMonitorApplication;
import com.skyeyes.storemonitor.process.DeviceProcessInterface;

public class DeviceProcess implements DeviceProcessInterface {
	
	
	String TAG = "DeviceProcess";
	public HashMap<String,DeviceReceiveCmdProcess> mStaticCmdProcess = null;
	public HashMap<String,DeviceReceiveCmdProcess> mResponseCmdProcess = new HashMap<String,DeviceReceiveCmdProcess>();

	protected ReceivLogin mReceivLogin;
	protected String mDeviceCode;
	protected SkyeyeNetworkClient mSkyeyeNetworkClient;
	private DeviceStatusChangeListener mDeviceStatusChangeListener;

	public DeviceProcess(String deviceCode,DeviceStatusChangeListener deviceStatusChangeListener){
		mDeviceCode = deviceCode;
		mDeviceStatusChangeListener = deviceStatusChangeListener;
		try {
    		String ip = PreferenceUtil.getConfigString(PreferenceUtil.SYSCONFIG, PreferenceUtil.sysconfig_server_ip);
    		String port = PreferenceUtil.getConfigString(PreferenceUtil.SYSCONFIG, PreferenceUtil.sysconfig_server_port);
			mSkyeyeNetworkClient = new SkyeyeSocketClient(new SocketHandlerImpl(deviceCode),false);
			mSkyeyeNetworkClient.setServerAddr(ip, Integer.parseInt(port));
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void loginDevice(String userName, String userPsd, byte config,DeviceReceiveCmdProcess<ReceivLogin> loginProcess) {
		// TODO Auto-generated method stub
		Log.d("SkyeyeNetworkClient","loginDevice"+mDeviceCode);
		SendObjectParams sendObjectParams = new SendObjectParams();
		sendObjectParams.getCommandHeader().loginId = 0;
		Object[] params = new Object[] { config, userName, userPsd,mDeviceCode};
		try {
			sendObjectParams.setParams(REQUST.cmdEquitLogin, params);
			Log.d(TAG,"loginStore入参数：" + sendObjectParams.toString());
			sendCmd(sendObjectParams,loginProcess);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 设备通道列表及状态
	private void queryDeviceRegInfo() {
		// TODO Auto-generated method stub
		SendObjectParams sendObjectParams = new SendObjectParams();
		if(mReceivLogin!=null){
			sendObjectParams.getCommandHeader().loginId = mReceivLogin.getCommandHeader().loginId;
		}
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdEquitRegInfo, params);
			System.out.println("queryDeviceRegInfo入参数：" + sendObjectParams.toString());
			mSkyeyeNetworkClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(mDeviceStatusChangeListener!=null){
				mDeviceStatusChangeListener.onSkyeyeNetworkException(mDeviceCode, e);
			}
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendCmd(SendCmdBean sendCmdBean,DeviceReceiveCmdProcess receiveCmdProcess) {
		// TODO Auto-generated method stub
		if(receiveCmdProcess!=null){
			receiveCmdProcess.setmResponseCmdProcess(mResponseCmdProcess);
			//注册返回信息监听
			mResponseCmdProcess.put(receiveCmdProcess.getGenericTypeName(),receiveCmdProcess);
		}
		try {
			//设置登陆ID
			if(mReceivLogin!=null){
				sendCmdBean.getCommandHeader().loginId = mReceivLogin.getCommandHeader().loginId;
			}
			mSkyeyeNetworkClient.sendCmd(sendCmdBean);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//receiveCmdProcess.removeMessages(DeviceReceiveCmdProcess.TIMEOUT_WHAT);
			if(mDeviceStatusChangeListener!=null){
				
				mDeviceStatusChangeListener.onSkyeyeNetworkException(mDeviceCode, e);
			}
		}
		
	}
	

	@Override
	public void setStaticCmdProcessMaps(HashMap<String, DeviceReceiveCmdProcess> deviceReceiveCmdProcessMaps) {
		// TODO Auto-generated method stub
		mStaticCmdProcess = deviceReceiveCmdProcessMaps;
	}
	


	public void stop() {
		// TODO Auto-generated method stub
		if(mSkyeyeNetworkClient!=null)
			mSkyeyeNetworkClient.doClose();
		mReceivLogin = null;
		mResponseCmdProcess.clear();
		mStaticCmdProcess = null;
		mDeviceStatusChangeListener = null;
		
	}
	

	
	private class SocketHandlerImpl extends BaseSocketHandler {
		private String mDeviceCode = null;
		public SocketHandlerImpl(String deviceCode) {
			super();
			// TODO Auto-generated constructor stub
			mDeviceCode = deviceCode;

		}
		@SuppressWarnings("unchecked")
		@Override
		public void onReceiveCmdEx(final ReceiveCmdBean receiveCmdBean) {
			// TODO Auto-generated method stub
			if(receiveCmdBean == null)
				return;
			if (receiveCmdBean instanceof ReceivLogin) {
				//登陆
				ReceivLogin receivLogin = (ReceivLogin)receiveCmdBean;
				if(mDeviceStatusChangeListener!=null){
					mDeviceStatusChangeListener.onDeviceLogin(mDeviceCode, receivLogin);
				}
				if(receivLogin.getCommandHeader().resultCode ==0){
					mReceivLogin = receivLogin;
					//查询通道信息
					queryDeviceRegInfo();
				}
			}else if(receiveCmdBean instanceof ReceiveDeviceRegisterInfo){
				if(mDeviceStatusChangeListener!=null){
					mDeviceStatusChangeListener.onDeviceStatusChange(mDeviceCode, receiveCmdBean);
				}
			}
			Log.e("SocketHandlerImpl",receiveCmdBean.getClass().getSimpleName()+":"+(mStaticCmdProcess==null?"null":mStaticCmdProcess.containsKey(receiveCmdBean.getClass().getSimpleName())));
			
			if(mResponseCmdProcess!=null&&
					mResponseCmdProcess.containsKey(receiveCmdBean.getClass().getSimpleName())){
				mResponseCmdProcess.remove(receiveCmdBean.getClass().getSimpleName()).onReceiveCmdBean(receiveCmdBean);
			}
			
			if(mStaticCmdProcess!=null&&
					mStaticCmdProcess.containsKey(receiveCmdBean.getClass().getSimpleName())){
				mStaticCmdProcess.get(receiveCmdBean.getClass().getSimpleName()).onReceiveCmdBean(receiveCmdBean);
			}
		}

		@Override
		public void onCmdExceptionEx(CommandParseException ex) {
			// TODO Auto-generated method stub
			ViewUtils.showErrorInfo(ex.getMessage());
		}

		@Override
		public void onSocketExceptionEx(NetworkException ex) {
			// TODO Auto-generated method stub
			ViewUtils.showErrorInfo(ex.getMessage());
			if(mDeviceStatusChangeListener!=null){
				mDeviceStatusChangeListener.onSkyeyeNetworkException(mDeviceCode, ex);
			}
			
			if(mResponseCmdProcess!=null){
				for(DeviceReceiveCmdProcess temp:mResponseCmdProcess.values()){
					temp.onFailure(ex.getMessage());
				}
			}
			mResponseCmdProcess.clear();
			
			if(mStaticCmdProcess!=null){
				for(DeviceReceiveCmdProcess temp:mStaticCmdProcess.values()){
					temp.onFailure(ex.getMessage());
				}
			}
		}

		@Override
		public void onSocketClosedEx() {
			// TODO Auto-generated method stub
			ViewUtils.showWrongInfo("连接已关闭");
			
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
		}
	}
}
