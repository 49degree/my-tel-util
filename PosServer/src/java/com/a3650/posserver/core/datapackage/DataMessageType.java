package com.a3650.posserver.core.datapackage;

/**
 * 消息类型父类
 * @author Administrator
 *
 */
public abstract class DataMessageType {
	private String messageType ;//消息类型
	// 报文长度
	private Short dataLength;
	
	public String getMessageType() {
		return messageType;
	}
	
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	

	
	public Short getDataLength() {
		return dataLength;
	}

	public void setDataLength(Short dataLength) {
		this.dataLength = dataLength;
	}

	/**
	 * 构消息数组
	 * @return
	 */
	public abstract byte[] parseValue();
	/**
	 * 数据头的长度，包括长度字
	 * @return
	 */
	public abstract int getMessageTypeLength();
	
	
	
}
