package com.guanri.android.jpos.bean;

/**
 * 命令实体类，对应有命令头各个属性和命令体
 */
public class PosMessageBean {
	private int messageType = 0;//	报文类型	AN	HEX1	M	M	
	private int endFlag = 0;	//	结束标志	N	HEX1	M	M	注1
	private byte commandCode = 0;//命令ID
	private byte[] value = null;//	报文数据	VAR	VAR	M	M
	
	public PosMessageBean(){
		
	}

	public PosMessageBean(int messageType, int endFlag, byte commandCode,
			byte[] value) {
		super();
		this.messageType = messageType;
		this.endFlag = endFlag;
		this.commandCode = commandCode;
		this.value = value;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(int endFlag) {
		this.endFlag = endFlag;
	}

	public byte getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(byte commandCode) {
		this.commandCode = commandCode;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
	

}
