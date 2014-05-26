package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveHistoryVideo extends ReceiveCmdBean {
	public byte passageNum;
	public byte picFrom;
	public byte resolution;
	public long videoDataLen;
	public byte[] header;
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		passageNum = body[0];
		picFrom = body[1];
		resolution = body[2];
		videoDataLen = TypeConversion.bytesToLong(body,3);
		if(body.length-11>0){
			header = new byte[body.length-11];
			System.arraycopy(body, 11, header, 0, header.length);
		}else{
			header = new byte[0];
		}

	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("passageNum=").append(passageNum).append(";");
		buffer.append("picFrom=").append(picFrom).append(";");
		buffer.append("Resolution=").append(resolution).append(";");
		buffer.append("videoDataLen=").append(videoDataLen).append(";");
		buffer.append("header=").append(header).append(";");
		return buffer.toString();
	}
}
