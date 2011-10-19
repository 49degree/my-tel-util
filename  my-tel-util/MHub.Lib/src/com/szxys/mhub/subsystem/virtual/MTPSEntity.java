package com.szxys.mhub.subsystem.virtual;

/**
 * 监护参数实体类
 * 
 * @author 张丹
 * 
 */
public class MTPSEntity {
	public int userID;
	public int appID;
	public String downTime;
	public int isChange;

	public MTPSEntity(int userID, int appID, String downTime, int isChange) {
		super();
		this.userID = userID;
		this.appID = appID;
		this.downTime = downTime;
		this.isChange = isChange;
	}

	public MTPSEntity() {
		super();
	}
}
