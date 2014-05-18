package com.skyeyes.base.cmd.bean.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

public class ReceiveReadDeviceList extends ReceiveCmdBean{
	
	//UTF8编码后的设备码,用;号进行分隔
	public List<String> deviceCodeList = new ArrayList<String>();
	public String deviceListString = null;


	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		try {
			deviceListString = new String(body,"utf-8");
			
			String[] temp = deviceListString.split(";");
			for(String deviceId:temp){
				deviceCodeList.add(deviceId);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		for(String deviceId:deviceCodeList){
			buffer.append("deviceId=").append(deviceId).append(";");
		}
		
		return buffer.toString();
	}

}
