package com.guanri.android.jpos.iso;

/**
 * 消息类型父类
 * @author Administrator
 *
 */
public abstract class JposMessageType {
	private int messageType ;
	private int transactionCode ;      

	
	public int getMessageType() {
		return messageType;
	}


	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}


	public int getTransactionCode() {
		return transactionCode;
	}


	public void setTransactionCode(int transactionCode) {
		this.transactionCode = transactionCode;
	}


	public abstract byte[] parseValue();
	
}
