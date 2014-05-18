package com.skyeyes.base.cmd.bean;

import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class CmdHeaderBean {
	public byte[] header = {0x48,0x72,0x49,0x3c};
	public int packLen ;//低位在前	指登录ID+命令码+数据的长度	
	public int loginId ;// 登录ID 低位在前
	public byte cmdCode ;//命令码
	public byte cmdId ;//命令ID 由发送方自定义，返回时带此命令ID返回
	public byte resultCode;//只有反馈命令存在该字段，0=正常执行，数据是指定格式的数据,1=非正常执行，数据是UTF8编码的错误信息
	public String errorInfo;
	
	public static CmdHeaderBean getBlankHeader(){
		return new CmdHeaderBean();
	}
	
	/**
	 * 解析上传数据头(请参照协议)
	 */
	public static CmdHeaderBean parseCommandHeader(byte[] receiveBuffer) throws CommandParseException{
		try {
			if(receiveBuffer.length-14<0)
				throw new CommandParseException("数据长度错误");
			// 解析命令头
			CmdHeaderBean commandHeader = CmdHeaderBean.getBlankHeader();
			System.arraycopy(receiveBuffer, 0, commandHeader.header, 0, 4);
			commandHeader.packLen = TypeConversion.bytesToInt(receiveBuffer, 4);
			commandHeader.loginId = TypeConversion.bytesToInt(receiveBuffer, 8);
			commandHeader.cmdCode = receiveBuffer[12];
			commandHeader.cmdId = receiveBuffer[13];
			if(commandHeader.cmdCode==0x00){
				commandHeader.resultCode = receiveBuffer[14];
				if(commandHeader.resultCode!=0){
					commandHeader.errorInfo = new String(receiveBuffer,15,receiveBuffer.length-19,"utf-8");
				}
			}
			
			return commandHeader;
		}catch(Exception ue){
			throw new CommandParseException("解析数据异常"); 
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("header=").append(TypeConversion.byte2hex(header)).append(";");
		buffer.append("packLen=").append(packLen).append(";");
		buffer.append("loginId=").append(loginId).append(";");
		buffer.append("cmdCode=").append(cmdCode).append(";");
		buffer.append("cmdId=").append(cmdId).append(";");
		buffer.append("resultCode=").append(resultCode).append(";");
		buffer.append("errorInfo=").append(errorInfo).append(";");
		return buffer.toString();
	}
	
}
