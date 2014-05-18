package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;
	
public class ReceiveDeviceEnv extends ReceiveCmdBean {
	public byte center;
	public byte card;
	public byte watchDog;
	public byte analysis;
	public byte netStatus;
	public byte frontBoard;
	public byte collectBoard;
	public long matureTime;

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		center = body[0];
		card = body[1];
		watchDog = body[2];
		analysis = body[3];
		netStatus = body[4];
		frontBoard = body[5];
		collectBoard= body[6];
		matureTime = TypeConversion.bytesToLong(body, 7);
		System.out.println(" matureTime ::::::::::   "+matureTime);
	}

}
