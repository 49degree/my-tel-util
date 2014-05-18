package com.skyeyes.storemonitor.process;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.exception.NetworkException;

public interface DeviceProcessInterface {
	public void loginDevice(String userName,String userPsd,byte config);
	public void queryChannelList();
	public void sendCmd(SendCmdBean sendCmdBean,DeviceReceiveCmdProcess receiveCmdProcess);
	
//	public void registerCmdProcess(String className,DeviceReceiveCmdProcess receiveCmdProcess);
//	public void unRegisterCmdProcess(String className);
	
	public void setCmdProcessMaps(HashMap<String,DeviceReceiveCmdProcess> deviceReceiveCmdProcessMaps);
	
	
	public void stop();
	
	public static abstract class DeviceReceiveCmdProcess<T extends ReceiveCmdBean> {
		public abstract void onProcess(T receiveCmdBean);
		public abstract void onFailure(String errinfo);
		
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

