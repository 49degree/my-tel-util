package com.skyeyes.base.cmd.bean.impl;

import java.util.ArrayList;
import java.util.List;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveAlarmList  extends ReceiveCmdBean {
	public String valueString = null;
	public List<AlarmBean> alarmBeans = new ArrayList<AlarmBean>();

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try{
			valueString = TypeConversion.asciiToString(body);
			String[] temp = valueString.split(";");
			alarmBeans.clear();
			for(String des:temp){
				AlarmBean alarmBean = new AlarmBean();
				alarmBean.des = des;
				alarmBeans.add(alarmBean);
			}
		}catch(Exception e){
			
		}

	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("valueString=").append(valueString);
		return buffer.toString();
	}
	
	public static class AlarmBean{
		public String des;
		
	}
	
}
