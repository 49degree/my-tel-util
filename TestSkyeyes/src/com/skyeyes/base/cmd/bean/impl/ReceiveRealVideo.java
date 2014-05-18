package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveRealVideo extends ReceiveCmdBean {
	public byte passageNum;
	public byte picFrom;
	public byte Resolution;
	public byte[] header;
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		passageNum = body[0];
		picFrom = body[1];
		Resolution = body[2];
		header = new byte[body.length-3];
		System.arraycopy(body, 3, header, 0, header.length);
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("passageNum=").append(passageNum).append(";");
		buffer.append("picFrom=").append(picFrom).append(";");
		buffer.append("Resolution=").append(Resolution).append(";");
		buffer.append("header=").append(header).append(";");
		return buffer.toString();
	}
}
