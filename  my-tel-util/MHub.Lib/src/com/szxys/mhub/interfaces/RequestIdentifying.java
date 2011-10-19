package com.szxys.mhub.interfaces;

/**
 * 
 * @author 黄仕龙
 * 
 */
public class RequestIdentifying {
	public int userID = 0; // 用户ID
	public int subSystemID = 0; // 子系统（业务）ID
	public byte devType = Platform.DATATYPE_XYS_NETWORK; // 设备类型
	public int extRequestID = 0; // 扩展码
	public int reserved1 = 0; // 保留字段，留待有需要时扩展使用
	public int reserved2 = 0; // 保留字段，留待有需要时扩展使用
}
