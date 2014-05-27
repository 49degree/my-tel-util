package com.skyeyes.base.cmd.bean.impl;

import java.util.ArrayList;
import java.util.List;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveOpenCloseDoor  extends ReceiveCmdBean {
	public String valueString = null;
	public List<OpenCloseDoorBean> openCloseDoorBeans = new ArrayList<OpenCloseDoorBean>();

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try{
			valueString = TypeConversion.asciiToString(body);
			String[] temp = valueString.split(";");
			openCloseDoorBeans.clear();
			for(String des:temp){
				OpenCloseDoorBean openCloseDoorBean = new OpenCloseDoorBean();
				openCloseDoorBean.des = des;
				
				if("1".equals(openCloseDoorBean.des.substring(28, 29))){
					openCloseDoorBean.type = 1;
					openCloseDoorBeans.add(openCloseDoorBean);
				}else if("2".equals(openCloseDoorBean.des.substring(28, 29))){
					openCloseDoorBean.type = 2;
					openCloseDoorBeans.add(openCloseDoorBean);
				}
			}
		}catch(Exception e){
			
		}

	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("valueString=").append(valueString);
		return buffer.toString();
	}
	
	public static class OpenCloseDoorBean{
		public String des;
		public int type;//1开门，2关门
		
	}
	
}
