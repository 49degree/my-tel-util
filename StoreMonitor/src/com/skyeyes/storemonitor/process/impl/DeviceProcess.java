package com.skyeyes.storemonitor.process.impl;

import java.util.HashMap;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.skyeyes.base.BaseSocketHandler;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceChannelListStatus;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceRegisterInfo;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.network.SkyeyeNetworkClient;
import com.skyeyes.base.network.impl.SkyeyeSocketClient;
import com.skyeyes.base.util.PreferenceUtil;
import com.skyeyes.storemonitor.StoreMonitorApplication;
import com.skyeyes.storemonitor.process.ChannelProcessInterface;
import com.skyeyes.storemonitor.process.DeviceProcessInterface;

public class DeviceProcess implements DeviceProcessInterface {
	
	
	String TAG = "DeviceProcess";
	public HashMap<String,DeviceReceiveCmdProcess> mStaticCmdProcess;
	public HashMap<String,DeviceReceiveCmdProcess> mResponseCmdProcess = new HashMap<String,DeviceReceiveCmdProcess>();
	public HashMap<Integer,ChannelProcessInterface> mChannelProcess = new HashMap<Integer,ChannelProcessInterface>();
	protected final HashMap<Integer,ChannelProcessInterface> mChannelProcessMap= new HashMap<Integer,ChannelProcessInterface>();
	
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
	public void loginDevice(String userName, String userPsd, byte config) {
		// TODO Auto-generated method stub
		Log.d("SkyeyeNetworkClient","loginDevice"+mDeviceCode);
		SendObjectParams sendObjectParams = new SendObjectParams();
		sendObjectParams.getCommandHeader().loginId = 0;
		Object[] params = new Object[] { config, userName, userPsd,mDeviceCode};
		try {
			sendObjectParams.setParams(REQUST.cmdEquitLogin, params);
			Log.d("SkyeyeNetworkClient","loginStore入参数：" + sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mSkyeyeNetworkClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(mDeviceStatusChangeListener!=null){
				mDeviceStatusChangeListener.onSkyeyeNetworkException(mDeviceCode, e);
			}
		}
	}
	
	// 设备通道列表及状态
	@Override
	public void queryDeviceRegInfo() {
		// TODO Auto-generated method stub
		SendObjectParams sendObjectParams = new SendObjectParams();
		if(mReceivLogin!=null){
			sendObjectParams.getCommandHeader().loginId = mReceivLogin.getCommandHeader().loginId;
		}
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdEquitRegInfo, params);
			System.out.println("queryDeviceRegInfo入参数：" + sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mSkyeyeNetworkClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(mDeviceStatusChangeListener!=null){
				mDeviceStatusChangeListener.onSkyeyeNetworkException(mDeviceCode, e);
			}
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
		}
		
	}
	

	@Override
	public void setCmdProcessMaps(HashMap<String, DeviceReceiveCmdProcess> deviceReceiveCmdProcessMaps) {
		// TODO Auto-generated method stub
		mStaticCmdProcess = deviceReceiveCmdProcessMaps;
	}
	


	public void stop() {
		// TODO Auto-generated method stub
		if(mSkyeyeNetworkClient!=null)
			mSkyeyeNetworkClient.doClose();
		
		
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
			//System.out.println("解析报文成功:" + (receiveCmdBean!=null?receiveCmdBean.toString():"receiveCmdBean is null"));
			if(receiveCmdBean == null)
				return;
			
//			Log.e(TAG, mStaticCmdProcess.size()+":receiveCmdBean.getClass().getSimpleName():"+receiveCmdBean.getClass().getSimpleName()+":"
//					+mStaticCmdProcess.containsKey(receiveCmdBean.getClass().getSimpleName()));
			
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
			
			if(mResponseCmdProcess.containsKey(receiveCmdBean.getClass().getSimpleName())){
				mResponseCmdProcess.remove(receiveCmdBean.getClass().getSimpleName()).onReceiveCmdBean(receiveCmdBean);
			}else if(mStaticCmdProcess.containsKey(receiveCmdBean.getClass().getSimpleName())){
				mStaticCmdProcess.get(receiveCmdBean.getClass().getSimpleName()).onReceiveCmdBean(receiveCmdBean);
			}
		}

		@Override
		public void onCmdExceptionEx(CommandParseException ex) {
			// TODO Auto-generated method stub
			Toast.makeText(StoreMonitorApplication.getInstance(), ex.getMessage(), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSocketExceptionEx(NetworkException ex) {
			// TODO Auto-generated method stub
			Toast.makeText(StoreMonitorApplication.getInstance(), ex.getMessage(), Toast.LENGTH_SHORT).show();
			if(mDeviceStatusChangeListener!=null){
				mDeviceStatusChangeListener.onSkyeyeNetworkException(mDeviceCode, ex);
			}
		}

		@Override
		public void onSocketClosedEx() {
			// TODO Auto-generated method stub
			System.out.println("测试连接关闭111111:onFailure");
			Toast.makeText(StoreMonitorApplication.getInstance(), "连接已关闭", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
		}
	}

}
