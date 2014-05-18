package com.skyeyes.base.cmd.bean.impl;

import java.io.UnsupportedEncodingException;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceivLogin extends ReceiveCmdBean{
	public byte userType;//权限
	public int userId;//用户ID
	public String deviceCode;
	public int radomCode;
	public long fileTime;

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		userType = body[0];
		userId = TypeConversion.bytesToInt(body, 1);
		try {
			deviceCode = new String(body,5,32,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		radomCode = TypeConversion.bytesToInt(body,37);
		fileTime = TypeConversion.bytesToLong(body, 41);
		System.out.println("fileTime ::::::::::   "+fileTime);

	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("userType=").append(userType).append(";");
		buffer.append("userId=").append(userId).append(";");
		buffer.append("deviceCode=").append(deviceCode).append(";");
		buffer.append("radomCode=").append(radomCode).append(";");
		buffer.append("fileTime=").append(fileTime).append(";");
		return buffer.toString();
	}

}
