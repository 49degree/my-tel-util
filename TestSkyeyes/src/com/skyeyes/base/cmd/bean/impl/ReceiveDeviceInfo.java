package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

public class ReceiveDeviceInfo extends ReceiveCmdBean{

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		String deviceInfo = new String(body);
		System.out.println("deviceInfo ::: "+deviceInfo);
	}

}
