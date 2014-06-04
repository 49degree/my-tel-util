package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

public class ReceiveDeviceStatus extends ReceiveCmdBean {
	public byte deviceStatus;	//返回状态
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		deviceStatus= body[0];
		System.out.println("deviceStatus :: "+deviceStatus);
	}
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		try{
			buffer.append("deviceStatus=").append(deviceStatus).append(";");
		}catch(Exception e){
			
		}
		return buffer.toString();
	}
}
