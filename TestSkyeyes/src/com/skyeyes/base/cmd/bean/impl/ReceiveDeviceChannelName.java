package com.skyeyes.base.cmd.bean.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.skyeyes.base.cmd.Constants;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

/**
 * 获取设备通道名称
 * 
 * 可知设备有多少通道
 * @author Administrator
 *
 */
public class ReceiveDeviceChannelName extends ReceiveCmdBean{
	public String[] mChannelNames = null; 
	public String mChannelNamesStr = null; 

	
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try {
			mChannelNamesStr = new String(body,"utf-8");
			mChannelNames = mChannelNamesStr.split(";");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		if(mChannelNames!=null)
			for(int i = 0;i<mChannelNames.length;i++){
				buffer.append("chennel"+i).append(";").append(mChannelNames[i]);
			}
		return buffer.toString();
	}
}
