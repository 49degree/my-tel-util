package com.skyeyes.base.cmd.bean;

import com.skyeyes.base.cmd.CommandControl;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public abstract class SendCmdBean extends BaseCmdBean{
	static byte cmdId = 1;
	public SendCmdBean(){
		/*
		public byte[] header = {0x48,0x72,0x49,0x3c};
		public int packLen ;//低位在前	指登录ID+命令码+数据的长度	
		public int loginId ;// 登录ID 低位在前
		public byte cmdCode ;//命令码
		public byte cmdId ;//命令ID 由发送方自定义，返回时带此命令ID返回
		public byte resultCode;//只有反馈命令存在该字段，0=正常执行，数据是指定格式的数据,1=非正常执行，数据是UTF8编码的错误信息
		*/
		System.out.println("commandHeader.loginId = ："+CommandControl.getLoginId());
		commandHeader = CmdHeaderBean.getBlankHeader();
		commandHeader.loginId = CommandControl.getLoginId();
		
	}
	
	public abstract byte[] packageBody() throws CommandParseException;
	
	public byte getConstantCmdId(){
		return (byte)(++cmdId/127);
	}
	
	/**
	 * 解析上传数据头(请参照协议)
	 * 
	 * @return  byte[]
	 * @roseuid 4DF72FB00242
	 */
	public byte[] packageCommand() throws CommandParseException{
		try{
			
			byte[] body = packageBody();//获取命令体的字节码
			byte[] header = packageCommandHeader(body.length);
			byte[] sendByte = new byte[header.length+body.length+ending.length];
			
			System.arraycopy(header, 0, sendByte, 0, header.length);
			
			System.arraycopy(body, 0, sendByte, header.length, body.length);
			
			System.arraycopy(ending, 0, sendByte, header.length+body.length, ending.length);
			
			return sendByte;
		}catch(Exception ue){
			ue.printStackTrace();
			throw new CommandParseException("构造返回数据异常"); 
		}
	}
	
	
	
	
	
	/**
	 * 解析上传数据头(请参照协议)
	 * 
	 * @return  byte[]
	 * @roseuid 4DF72FB00242
	 */
	private byte[] packageCommandHeader(int bodyLen) throws CommandParseException{
		
		try {
			byte[] buffer = null;
			if(commandHeader.cmdCode==0x00){
				buffer = new byte[15];
			}else{
				buffer = new byte[14];
			}
			
			commandHeader.packLen = bodyLen+buffer.length-8;//命令体长度+命令头不包含开始标志和长度字段的长度
			
			System.arraycopy(commandHeader.header, 0, buffer, 0, 4);
			System.arraycopy(TypeConversion.intToBytes(commandHeader.packLen), 0, buffer, 4, 4);
			System.arraycopy(TypeConversion.intToBytes(commandHeader.loginId), 0, buffer, 8, 4);
			buffer[12] = commandHeader.cmdCode;
			buffer[13] = commandHeader.cmdId;
			if(commandHeader.cmdCode==0x00){
				buffer[14] = commandHeader.resultCode;
			}
			return buffer;
		}catch(Exception ue){
			throw new CommandParseException("解析数据异常"); 
		}
	}
}
