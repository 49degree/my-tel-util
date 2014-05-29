package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

public class ReceiveLoginOut  extends ReceiveCmdBean {


	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub


	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		return buffer.toString();
	}
	

	
}
