package com.skyeyes.base.cmd.bean.impl;

import java.io.UnsupportedEncodingException;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

public class ReceiveGetEquitIO extends ReceiveCmdBean {
	/**当前状态*/
	public byte status;
	/**手动 */
	public byte manual;
	public String name;
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
//		status = body[0];
//		manual = body[1];
		String b;
		try {
			name = new String(body,2,32,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("name ::  "+name);
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("status == ").append(status).append(";");
		buffer.append("manual == ").append(manual).append(";");
		buffer.append("name == ").append(name).append(";");
		return buffer.toString();
	}
}
