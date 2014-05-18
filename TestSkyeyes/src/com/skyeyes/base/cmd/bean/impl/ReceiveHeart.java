package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

public class ReceiveHeart extends ReceiveCmdBean{
	@Override
	public void parseBody(byte[] body) throws CommandParseException {

	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		return buffer.toString();
	}

}
