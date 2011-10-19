package com.szxys.mhub.base.communication;

import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 服务器通信数据单元。
 */
public class WebDataCell {
	/**
	 * 数据单元的优先级。
	 */
	public int Priority;

	/**
	 * 数据单元的请求标识。
	 */
	public RequestIdentifying ReqIdentifying;

	/**
	 * 数据单元的用户令牌。
	 */
	public long Token;

	/**
	 * 数据单元的循环发送周期（单位：秒），0 表示非循环发送数据。
	 */
	public int SendCycle = 0;

	/**
	 * 数据单元的发送标识。
	 */
	public int SendIndex;

	/**
	 * 数据单元的主码。
	 */
	public int MainCmd;

	/**
	 * 数据单元的扩展码。
	 */
	public int SubCmd;

	/**
	 * 数据单元的数据。
	 */
	public byte[] Data;

	/**
	 * 获取数据单元的 Hash 键值。
	 */
	public int getHashKey() {
		int hashCode = 1;
		hashCode = 31
				* hashCode
				+ (this.ReqIdentifying != null ? this.ReqIdentifying.userID : 0);
		hashCode = 31
				* hashCode
				+ (this.ReqIdentifying != null ? this.ReqIdentifying.subSystemID
						: 0);
		hashCode = 31 * hashCode + this.MainCmd;
		hashCode = 31 * hashCode + this.SubCmd;
		return hashCode;
	}
}