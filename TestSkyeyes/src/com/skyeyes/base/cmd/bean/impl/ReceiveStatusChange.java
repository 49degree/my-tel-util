package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveStatusChange extends ReceiveCmdBean {
	public byte status;
	public String des;
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try{
			status = body[0];
			des = TypeConversion.asciiToString(body,1,body.length-1);
		}catch(Exception e){
			
		}

	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		try{
			buffer.append("status=").append(status).append(";");
			buffer.append("des=").append(des).append(";");
		}catch(Exception e){
			
		}
		return buffer.toString();
	}

}
