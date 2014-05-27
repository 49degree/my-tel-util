package com.skyeyes.base.cmd.bean.impl;

import java.util.Date;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveAlarmInfo  extends ReceiveCmdBean {
	public String eventCode = null;
	public long time;
	public byte alarmType;
	public byte[] pic;

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try{
			eventCode = TypeConversion.asciiToString(body,0,36);
			alarmType = body[53];
			time = DateUtil.fileTime2Date(TypeConversion.bytesToLong(body, 56)).getTime();
			pic = new byte[body.length-64];
			System.arraycopy(body, 64, pic, 0, pic.length);
		}catch(Exception e){
			
		}

	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		try{
			buffer.append("eventCode=").append(eventCode).append(";");
			buffer.append("alarmType=").append(alarmType).append(";");
			buffer.append("pic len=").append(pic.length).append(";");
			buffer.append("time=").append(new Date(time));
		}catch(Exception e){
			
		}
		return buffer.toString();
	}
	

	
}
