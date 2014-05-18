package com.skyeyes.base.cmd.bean;

import com.skyeyes.base.exception.CommandParseException;

public abstract class ReceiveCmdBean extends BaseCmdBean{

	public abstract void parseBody(byte[] body) throws CommandParseException;
	

}
