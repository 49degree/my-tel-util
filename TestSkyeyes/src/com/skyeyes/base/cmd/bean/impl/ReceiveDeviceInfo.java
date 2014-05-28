package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

public class ReceiveDeviceInfo extends ReceiveCmdBean{
	public byte status;
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try{
			status = body[0];
		}catch(Exception e){
			
		}
	}
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("status=").append(status);
		return buffer.toString();
	}
}
