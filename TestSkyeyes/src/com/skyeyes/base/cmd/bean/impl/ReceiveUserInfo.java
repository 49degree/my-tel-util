package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveUserInfo  extends ReceiveCmdBean {
//	0-35Byte	36-99Byte	100-115Byte	116-131Byte	132byte	133byte	134-149byte	150-Nbyte
//	36byte	64byte	16Byte	16Byte	1Byte	1byte	16byte	nbyte
//	用户ID	用户名（UTF8）	姓名（UTF8）	电话（UTF8）	权限（1＝内置，2＝管理，3＝普通,4=联系人）	顺序号	称谓	照片

	public String userId;
	public String loginName;
	public String userName;
	public String userTel;
	public byte role;
	public byte seq;
	public String tiltleName;
	public byte[] pic;

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		try{
			userId = TypeConversion.asciiToString(body,0,36);
			loginName = TypeConversion.asciiToString(body,36,64);
			userName = TypeConversion.asciiToString(body,100,16);
			
			userTel = TypeConversion.asciiToString(body,116,16);
			role = body[132];
			seq  = body[133];
			tiltleName = TypeConversion.asciiToString(body,134,16);
			pic = new byte[body.length-150];
			System.arraycopy(body, 150, pic, 0, pic.length);
		}catch(Exception e){
			
		}

	}

	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		try{
			buffer.append("userId=").append(userId).append(";");
			buffer.append("loginName=").append(loginName).append(";");
			buffer.append("userName=").append(userName).append(";");
			buffer.append("userTel=").append(userTel).append(";");
			buffer.append("role=").append(role).append(";");
			buffer.append("seq=").append(seq).append(";");
			buffer.append("tiltleName=").append(tiltleName).append(";");
			buffer.append("pic len=").append(pic.length).append(";");
		}catch(Exception e){
			
		}
		return buffer.toString();
	}
	

	
}
