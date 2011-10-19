package com.szxys.mhub.subsystem.virtual;

/**
 * 医生回复消息实体类
 * 
 * @author 苏佩
 * 
 */
public class IncommingMessage extends MhubMessage {
	private String alertId; // 提醒ID
	private int isRead = 0; // 是否已读

	public String getAlertId() {
		return alertId;
	}

	public void setAlertId(String alertId) {
		this.alertId = alertId;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

}
