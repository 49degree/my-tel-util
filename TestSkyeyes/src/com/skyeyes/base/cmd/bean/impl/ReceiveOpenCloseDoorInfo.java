package com.skyeyes.base.cmd.bean.impl;

import java.util.Date;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveOpenCloseDoorInfo  extends ReceiveCmdBean {
	public String eventCode = null;
	public long time;

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try{
			eventCode = TypeConversion.asciiToString(body,0,36);
			time = DateUtil.fileTime2Date(TypeConversion.bytesToLong(body, 56)).getTime();
		}catch(Exception e){
			
		}

	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("eventCode=").append(eventCode).append(";");
		buffer.append("time=").append(new Date(time));
		return buffer.toString();
	}
	

	
}
