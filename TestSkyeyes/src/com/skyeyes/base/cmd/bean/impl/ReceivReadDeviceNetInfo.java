package com.skyeyes.base.cmd.bean.impl;

import java.util.ArrayList;
import java.util.List;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

public class ReceivReadDeviceNetInfo extends ReceiveCmdBean{
	
//	以;号分隔字段,依次为:本机IP;子网掩码;网关;主DNS;子DNS
//	对于具用多个IP的设备用','进行分隔
	public List<DeviceNetInfo> deviceNetInfos = new ArrayList<DeviceNetInfo>();
	String netInfo = null;
	public class DeviceNetInfo{
		public String ip;
		public String ipCode;
		public String ipProxy;
		public String mainDNS;
		public String subDNS;
	}

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		netInfo = new String(body);
		String[] temp = netInfo.split(",");
		for(String net:temp){
			String[] temp2 = net.split(";");
			DeviceNetInfo deviceNetInfo = new DeviceNetInfo();
			try{
				deviceNetInfo.ip = temp2[0];
				deviceNetInfo.ipCode = temp2[1];
				deviceNetInfo.ipProxy = temp2[2];
				deviceNetInfo.mainDNS = temp2[3];
				deviceNetInfo.subDNS = temp2[4];
			}catch(Exception e){
				//e.printStackTrace();
			}
			deviceNetInfos.add(deviceNetInfo);
		}


	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("netInfo=").append(netInfo).append(";");
		return buffer.toString();
	}

}
