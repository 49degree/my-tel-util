package com.skyeyes.storemonitor.process;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.exception.NetworkException;

public interface DeviceProcessInterface {
	public void loginDevice(String userName,String userPsd,byte config);
	public void queryDeviceRegInfo();
	public void sendCmd(SendCmdBean sendCmdBean,DeviceReceiveCmdProcess receiveCmdProcess);
	public void setCmdProcessMaps(HashMap<String,DeviceReceiveCmdProcess> deviceReceiveCmdProcessMaps);
	public void stop();
	
	/**
	 * @author Administrator
	 *
	 * @param <T>
	 */
	public static abstract class DeviceReceiveCmdProcess<T extends ReceiveCmdBean> extends Handler{
		public static final int TIMEOUT_WHAT = 1;
		static String TAG = "DeviceReceiveCmdProcess";
		private HashMap<String,DeviceReceiveCmdProcess> mResponseCmdProcess ;
		
		public abstract void onProcess(T receiveCmdBean);

		public abstract void onFailure(String errinfo);
		
		public synchronized void  onReceiveCmdBean(T receiveCmdBean){
			removeMessages(TIMEOUT_WHAT);
			onProcess(receiveCmdBean);
		}
		/**
		 * 设置响应超时
		 * @param timeout Millis
		 */
		public synchronized void setTimeout(long timeout){
			sendEmptyMessageDelayed(TIMEOUT_WHAT, timeout);
		}

		public void handleMessage(Message msg){
			String name = getGenericTypeName();
			Log.e(TAG, "handleMessage"+msg.what);
			if(msg.what == TIMEOUT_WHAT){
				if(mResponseCmdProcess!=null &&
						mResponseCmdProcess.containsKey(name)){
					mResponseCmdProcess.remove(name);
				}
				synchronized(this){
					onResponsTimeout();
				}
				
			}
		}

		public void setmResponseCmdProcess(HashMap<String, DeviceReceiveCmdProcess> responseCmdProcess) {
			mResponseCmdProcess = responseCmdProcess;
		}
		
		public void onResponsTimeout(){
			
		}
		
		public String getGenericTypeName() { 
			Type genType = getClass().getGenericSuperclass(); 
			if (!(genType instanceof ParameterizedType)) {  
				return "";
			}  
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();  
			if (params.length<1) {   
				throw new RuntimeException("Index outof bounds");  }  
			if (!(params[0] instanceof Class)) {   
				return "";  
			}  
			return ((Class) params[0]).getSimpleName(); 
		}
	}
	
	public interface DeviceStatusChangeListener{
		public void onDeviceLogin(String deviceCode,ReceivLogin receivLogin);
		public void onDeviceStatusChange(String deviceCode,ReceiveCmdBean receiveCmdBean);
		public void onSkyeyeNetworkException(String deviceCode,NetworkException ex);
	}
}

