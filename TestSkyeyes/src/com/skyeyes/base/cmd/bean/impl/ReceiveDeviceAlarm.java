package com.skyeyes.base.cmd.bean.impl;

import java.util.Date;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveDeviceAlarm  extends ReceiveCmdBean {
	public String eventCode = null;
	public byte alarmType;//报警类型，见备注
	public int startType;//是否报警触发，0＝提示触发，1＝报警触发
	public int channelId;
	public long time;
	public double localeX;
	public double localeY;
	public String des;
	public byte[] pic;

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try{
			eventCode = TypeConversion.asciiToString(body,0,36);
			alarmType = (byte)(body[36]&0x7F);
			startType = (byte)((body[36]&0x80)>>7);
			channelId = body[37];
			time = DateUtil.fileTime2Date(TypeConversion.bytesToLong(body, 37)).getTime();
			if(alarmType==109||alarmType==110){
				localeX = Double.longBitsToDouble(TypeConversion.bytesToLong(body, 46));
				localeX = Double.longBitsToDouble(TypeConversion.bytesToLong(body, 54));
				des = TypeConversion.asciiToString(body,62,255);
			}else{
				pic = new byte[body.length-46];
				System.arraycopy(body, 46, pic, 0, pic.length);
			}
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
			buffer.append("channelId=").append(channelId).append(";");
			buffer.append("localeX=").append(localeX).append(";");
			buffer.append("localeY=").append(localeY).append(";");
			buffer.append("startType=").append(startType).append(";");
			buffer.append("des=").append(des);
		}catch(Exception e){
			
		}
		return buffer.toString();
	}
	

	
}
