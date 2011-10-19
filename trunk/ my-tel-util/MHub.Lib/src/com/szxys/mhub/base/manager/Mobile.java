package com.szxys.mhub.base.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;

import com.szxys.mhub.base.manager.DeviceManager;
import com.szxys.mhub.base.manager.UserManager;
import com.szxys.mhub.interfaces.Collector;
import com.szxys.mhub.interfaces.LightUser;

/**
 * 移动终端。
 */
public class Mobile {
	/**
	 * 移动终端的轻量级用户集合。
	 */
	private Map<Integer, LightUser> mLightUsers;

	/**
	 * 移动终端的采集器集合。
	 */
	private Map<Integer, Collector> mCollectors;

	/**
	 * 移动终端的蓝牙地址。
	 */
	public String Mac;

	/**
	 * 构造函数。
	 * 
	 * @param mac
	 *            ：蓝牙地址。
	 */
	public Mobile(String mac) {
		this.Mac = mac;
	}

	/**
	 * 获取移动终端的轻量级用户集合。
	 */
	public Map<Integer, LightUser> getLightUsers() {
		if (this.mLightUsers == null) {
			this.mLightUsers = UserManager.getAllLightUsers();
		}
		return this.mLightUsers;
	}

	/**
	 * 设置移动终端的轻量级用户集合。
	 */
	public void setLightUsers(Map<Integer, LightUser> lightUsers) {
		this.mLightUsers = lightUsers;
	}

	/**
	 * 获取移动终端的采集器集合。
	 */
	public Map<Integer, Collector> getCollectors() {
		if (this.mCollectors == null) {
			this.mCollectors = DeviceManager.getAllCollectors();
		}
		return this.mCollectors;
	}

	/**
	 * 设置移动终端的采集器集合。
	 */
	public void setCollectors(Map<Integer, Collector> collectors) {
		this.mCollectors = collectors;
	}

	/**
	 * 获取移动终端的绑定信息。
	 */
	public String getBindInfo() {
		try {
			StringBuilder bs = new StringBuilder();
			bs.append("{\"DeviceList\":[");
			for (Collector collector : this.getCollectors().values()) {
				bs.append("{\"Desc\":\"").append(collector.Desc)
						.append("\",\"DeviceType\":")
						.append(collector.DeviceType)
						.append(",\"HeartBeatInterval\":")
						.append(collector.HeartBeatInterval).append(",\"Id\":")
						.append(collector.Id).append(",\"Mac\":\"")
						.append(collector.Mac).append("\",\"NumOfChannels\":")
						.append(collector.NumOfChannels)
						.append(",\"PairingCode\":\"")
						.append(collector.PairingCode)
						.append("\",\"PassiveMode\":")
						.append(collector.PassiveMode ? 1 : 0)
						.append(",\"PhysicalCode\":\"")
						.append(collector.PhysicalCode)
						.append("\",\"ProtocolType\":")
						.append(collector.ProtocolType).append("},");
			}
			int len = bs.length();
			if (bs.charAt(len - 1) == ',') {
				bs.deleteCharAt(len - 1).append("],");
			} else {
				bs.append("],");
			}

			bs.append("\"PatientList\":[");
			for (LightUser user : this.getLightUsers().values()) {
				bs.append("{\"FullName\":\"").append(user.Name)
						.append("\",\"MemberID\":\"").append(user.MemberId)
						.append("\",\"UserID\":").append(user.ID).append("},");
			}
			len = bs.length();
			if (bs.charAt(len - 1) == ',') {
				bs.deleteCharAt(len - 1).append("]}");
			} else {
				bs.append("]}");
			}

			return bs.toString();
		} catch (Exception e) {
			Log.e("Mobile", "Failed to get bind info!", e);
			return "";
		}
	}

	/**
	 * 将绑定的用户、采集器信息保存到数据库中。
	 */
	public boolean save() {
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(false);
			Map<Integer, LightUser> mapUsers = this.mLightUsers;
			Map<Integer, Collector> mapCollectors = this.mCollectors;

			boolean userFlag = mapUsers != null && !mapUsers.isEmpty();
			boolean collectorFlag = mapCollectors != null
					&& !mapCollectors.isEmpty();
			if (userFlag || collectorFlag) {
				clear(helper);

				if (userFlag) {
					for (LightUser lightUser : mapUsers.values()) {
						UserManager.addLightUserInner(helper, lightUser);
						Map<Byte, Integer> map = lightUser.Devices;
						if (map != null) {
							for (Byte deviceType : map.keySet()) {
								UserManager.addUserCollectorRelational(helper,
										lightUser.ID, deviceType,
										map.get(deviceType));
							}
						}
					}
				}

				if (collectorFlag) {
					for (Collector collector : mapCollectors.values()) {
						DeviceManager.addCollectorInner(helper, collector);
					}
				}
			}
			return true;
		} catch (Exception e) {
			Log.e("Mobile", "Failed to save!", e);
			return false;
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
	}

	/**
	 * 是否包含多个同类型采集器。
	 */
	boolean containsSameTypeCollectors() {
		try {
			if (this.mCollectors != null && !this.mCollectors.isEmpty()) {
				List<Byte> list = new ArrayList<Byte>();
				for (Collector collector : this.mCollectors.values()) {
					if (list.contains(collector.DeviceType)) {
						return true;
					} else {
						list.add(collector.DeviceType);
					}
				}
				list = null;
			}
		} catch (Exception e) {
			Log.e("Mobile", "Failed to containsSameTypeCollectors!", e);
		}
		return false;
	}

	/**
	 * 获取分类后的采集器字典。
	 */
	Map<Byte, List<Collector>> getSameTypeCollectors() {
		Map<Byte, List<Collector>> map = null;
		try {
			if (this.mCollectors != null && !this.mCollectors.isEmpty()) {
				map = new HashMap<Byte, List<Collector>>();
				for (Collector collector : this.mCollectors.values()) {
					if (map.containsKey(collector.DeviceType)) {
						map.get(collector.DeviceType).add(collector);
					} else {
						List<Collector> list = new ArrayList<Collector>();
						list.add(collector);

						map.put(collector.DeviceType, list);
					}
				}
			}
		} catch (Exception e) {
			Log.e("Mobile", "Failed to getSameTypeCollectors!", e);
			map = null;
		}
		return map;
	}

	/**
	 * 清空所有用户和采集器。
	 */
	static void clear() {
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(false);
			clear(helper);
		} catch (Exception e) {
			Log.e("Mobile", "Failed to clear!", e);
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
	}

	/**
	 * 通过 DB 操作对象清空所有用户和采集器。
	 * 
	 * @param helper
	 *            ：DB操作对象。
	 */
	private static void clear(MHubDBHelper helper) {
		try {
			helper.execSQL("delete from " + DBConstDef.TABLE_USER);
			helper.execSQL("delete from " + DBConstDef.TABLE_COLLECTOR);
			helper.execSQL("delete from "
					+ DBConstDef.TABLE_USER_COLLECTOR_RELATIONAL);
		} catch (Exception e) {
			Log.e("Mobile", "Failed to clear(MHubDBHelper helper)!", e);
		}
	}
}
