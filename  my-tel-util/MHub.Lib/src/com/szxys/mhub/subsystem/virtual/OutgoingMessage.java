package com.szxys.mhub.subsystem.virtual;

/**
 * 病人主诉消息实体类
 * 
 * @author 苏佩
 * 
 */
public class OutgoingMessage extends MhubMessage {
	private int isSend = 0;// 是否已经发送

	public int getIsSend() {
		return isSend;
	}

	public void setIsSend(int isSend) {
		this.isSend = isSend;
	}

}
