package com.szxys.mhub.interfaces;

import java.util.Map;

/**
 * 轻量级用户。
 */
public class LightUser {
	/**
	 * 用户编码。
	 */
	public int ID;

	/**
	 * 用户姓名。
	 */
	public String Name;

	/**
	 * 用户的会员编号。
	 */
	public String MemberId;

	/**
	 * 用户关联的设备类型、编号字典。
	 */
	public Map<Byte, Integer> Devices;

	/**
	 * 初始化 LightUser 对象。
	 */
	public LightUser() {

	}

	/**
	 * 初始化 LightUser 对象。
	 * 
	 * @param id
	 *            ：编码。
	 * @param name
	 *            ：姓名。
	 * @param devices
	 *            ：设备类型、编号字典。
	 */
	public LightUser(int id, String name, String memberId,
			Map<Byte, Integer> devices) {
		this.ID = id;
		this.Name = name;
		this.MemberId = memberId;
		this.Devices = devices;
	}
}