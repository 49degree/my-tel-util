package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveStopVideo  extends ReceiveCmdBean {
	public byte[] body = null;

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		this.body = body;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("body size=").append(body.length);
		return buffer.toString();
	}
}
