package com.guanri.android.jpos.iso;

/**
 * 消息类型父类
 * @author Administrator
 *
 */
public abstract class JposMessageType {
	private String messageType ;
	private int transactionCode ; 
	
	// 报文长度
	private Short pageLength;
	
	public String getMessageType() {
		return messageType;
	}


	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}


	public int getTransactionCode() {
		return transactionCode;
	}


	public void setTransactionCode(int transactionCode) {
		this.transactionCode = transactionCode;
	}

	public Short getPageLength() {
		return pageLength;
	}


	public void setPageLength(Short pageLength) {
		this.pageLength = pageLength;
	}


	/**
	 * 构造消息数组
	 * @return
	 */
	public abstract byte[] parseValue();
	/**
	 * 数据头的长度，包括长度字段
	 * @return
	 */
	public abstract int getMessageTypeLength();
	
	
	
}
