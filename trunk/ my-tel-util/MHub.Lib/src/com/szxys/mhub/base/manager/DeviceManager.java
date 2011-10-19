package com.szxys.mhub.base.manager;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.stream.JsonReader;
import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.interfaces.Collector;
import com.szxys.mhub.interfaces.LightUser;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceManager {
	/**
	 * 本地移动终端设备。
	 */
	private static Mobile mLocalMobile = null;

	static {
		if (isEmulator()) {
			// 是模拟器则使用假的蓝牙地址
			mLocalMobile = new Mobile("FF:19:5D:24:CC:90");
		} else {
			// 不是模拟器则读取真机的蓝牙地址
			String localMacAdress;
			try {
				localMacAdress = BluetoothAdapter.getDefaultAdapter()
						.getAddress();
			} catch (Exception e) {
				Log.e("DeviceManager",
						"Failed to get local Bluetooth address!", e);
				localMacAdress = "";
			}
			if (localMacAdress == null) {
				localMacAdress = "";
			}
			mLocalMobile = new Mobile(localMacAdress);
		}
	}

	/**
	 * 设备管理类的构造函数。
	 */
	DeviceManager() {
	}

	/**
	 * 判断是否是模拟器。
	 */
	public static boolean isEmulator() {
		boolean isEmulator = false;
		try {
			TelephonyManager telmgr = (TelephonyManager) MhubApplication
					.getInstance().getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceID = telmgr.getDeviceId();
			isEmulator = "000000000000000".equalsIgnoreCase(deviceID);
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to check emulator!", e);
		}

		return isEmulator;
	}

	/**
	 * 获取本地移动终端设备。
	 */
	public static Mobile getLocalMobile() {
		return mLocalMobile;
	}

	/**
	 * 获取本地移动终端设备绑定的所有采集器信息。
	 */
	public static Map<Integer, Collector> getAllCollectors() {
		Map<Integer, Collector> map = null;
		MHubDBHelper helper = null;
		Cursor cur = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);
			cur = helper.query("select * from " + DBConstDef.TABLE_COLLECTOR);
			if (cur != null && cur.getCount() > 0) {
				map = new HashMap<Integer, Collector>();
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					int collectorId = cur.getInt(cur.getColumnIndex("ID"));
					map.put(collectorId,
							new Collector(
									collectorId,
									cur.getInt(cur
											.getColumnIndex("HeartBeatInterval")),
									cur.getString(cur
											.getColumnIndex("PhysicalCode")),
									(byte) cur.getInt(cur
											.getColumnIndex("DeviceType")),
									cur.getString(cur.getColumnIndex("Mac")),
									(byte) cur.getInt(cur
											.getColumnIndex("NumOfChannels")),
									cur.getString(cur
											.getColumnIndex("PairingCode")),
									cur.getInt(cur
											.getColumnIndex("PassiveMode")) == 0 ? false
											: true, cur.getInt(cur
											.getColumnIndex("ProtocolType")),
									cur.getString(cur.getColumnIndex("Desc"))));
				}
			}
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to getAllCollector!", e);
			map = null;
		} finally {
			if (cur != null) {
				cur.close();
			}
			if (helper != null) {
				helper.close();
			}
		}
		return map;
	}

	/**
	 * 获取指定用户、采集器类型的采集器。
	 * 
	 * @param userId
	 *            ：用户编号
	 * @param deviceType
	 *            ：采集器类型
	 */
	public static Collector getCollector(int userId, byte deviceType) {
		Collector collector = null;
		MHubDBHelper helper = null;
		Cursor cur = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);
			cur = helper.query("select * from " + DBConstDef.TABLE_COLLECTOR
					+ "where ID = (select CollectorID from "
					+ DBConstDef.TABLE_USER_COLLECTOR_RELATIONAL
					+ " where UserID = " + userId + " and DeviceType = "
					+ deviceType + ")");
			if (cur != null && cur.getCount() == 1) {
				cur.moveToFirst();
				collector = new Collector(
						cur.getInt(cur.getColumnIndex("ID")),
						cur.getInt(cur.getColumnIndex("HeartBeatInterval")),
						cur.getString(cur.getColumnIndex("PhysicalCode")),
						(byte) cur.getInt(cur.getColumnIndex("DeviceType")),
						cur.getString(cur.getColumnIndex("Mac")),
						(byte) cur.getInt(cur.getColumnIndex("NumOfChannels")),
						cur.getString(cur.getColumnIndex("PairingCode")),
						cur.getInt(cur.getColumnIndex("PassiveMode")) == 0 ? false
								: true, cur.getInt(cur
								.getColumnIndex("ProtocolType")),
						cur.getString(cur.getColumnIndex("Desc")));
			}
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to getCollector!", e);
			collector = null;
		} finally {
			if (cur != null) {
				cur.close();
			}
			if (helper != null) {
				helper.close();
			}
		}
		return collector;
	}

	/**
	 * 获取指定用户的所有采集器。
	 * 
	 * @param userId
	 *            ：用户编号
	 */
	public static Map<Integer, Collector> getCollectorsByUserId(int userId) {
		Map<Integer, Collector> map = null;
		MHubDBHelper helper = null;
		Cursor cur = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);
			cur = helper.query("select * from " + DBConstDef.TABLE_COLLECTOR
					+ "where ID in (select CollectorID from "
					+ DBConstDef.TABLE_USER_COLLECTOR_RELATIONAL
					+ " where UserID = " + userId + ")");
			if (cur != null && cur.getCount() > 0) {
				map = new HashMap<Integer, Collector>();
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					int collectorId = cur.getInt(cur.getColumnIndex("ID"));
					map.put(collectorId,
							new Collector(
									collectorId,
									cur.getInt(cur
											.getColumnIndex("HeartBeatInterval")),
									cur.getString(cur
											.getColumnIndex("PhysicalCode")),
									(byte) cur.getInt(cur
											.getColumnIndex("DeviceType")),
									cur.getString(cur.getColumnIndex("Mac")),
									(byte) cur.getInt(cur
											.getColumnIndex("NumOfChannels")),
									cur.getString(cur
											.getColumnIndex("PairingCode")),
									cur.getInt(cur
											.getColumnIndex("PassiveMode")) == 0 ? false
											: true, cur.getInt(cur
											.getColumnIndex("ProtocolType")),
									cur.getString(cur.getColumnIndex("Desc"))));
				}
			}
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to getCollector!", e);
			map = null;
		} finally {
			if (cur != null) {
				cur.close();
			}
			if (helper != null) {
				helper.close();
			}
		}
		return map;
	}

	/**
	 * 添加采集器。
	 * 
	 * @param collector
	 *            ：采集器
	 */
	public static boolean addCollector(Collector collector) {
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(false);
			return addCollectorInner(helper, collector);
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to addCollector!", e);
			return false;
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
	}

	/**
	 * 更新采集器。
	 * 
	 * @param collector
	 *            ：采集器
	 */
	public static boolean updateCollector(Collector collector) {
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(false);
			helper.execSQL("update " + DBConstDef.TABLE_COLLECTOR
					+ " set HeartBeatInterval = " + collector.HeartBeatInterval
					+ " , PhysicalCode = '" + collector.PhysicalCode
					+ "', DeviceType = " + collector.DeviceType + ", Mac = '"
					+ collector.Mac + "', NumOfChannels = "
					+ collector.NumOfChannels + ", PairingCode = '"
					+ collector.PairingCode + "', PassiveMode = "
					+ (collector.PassiveMode ? 1 : 0) + ", ProtocolType = "
					+ collector.ProtocolType + ", Desc = '"
					+ (collector.Desc == null ? "" : collector.Desc)
					+ "' where ID = " + collector.Id);
			return true;
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to updateCollector!", e);
			return false;
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
	}

	/**
	 * 删除采集器。
	 * 
	 * @param collectorId
	 *            ：要删除的采集器ID
	 */
	public static boolean removeCollector(int collectorId) {
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(false);
			helper.execSQL("delete from " + DBConstDef.TABLE_COLLECTOR
					+ " where ID = " + collectorId);
			return true;
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to removeCollector!", e);
			return false;
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
	}

	/**
	 * 是否已经进行过设备绑定。
	 * 
	 * @return boolean ：true表示已经绑定过，false表示未进行绑定。
	 */
	public static boolean isInitialized() {
		MHubDBHelper helper = null;
		Cursor cur = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);
			cur = helper.query("select * from " + DBConstDef.TABLE_USER);
			return cur != null && cur.getCount() > 0;
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to isInitialized()!", e);
			return false;
		} finally {
			if (cur != null) {
				cur.close();
			}
			if (helper != null) {
				helper.close();
			}
		}
	}

	/**
	 * 更新最新的绑定信息。
	 * 
	 * @param bindInfoData
	 *            ：最新的绑定数据。
	 * @param bindInfoDataLength
	 *            ：数据长度。
	 * @param configSameTypeCollectorsEventHandler
	 *            ：存在多个同类型采集器时的配置采集器回调函数。
	 * @return boolean ：true表示更新成功或不需更新，false表示更新失败。
	 */
	public static boolean updateLatestBindInfo(byte[] bindInfoData,
			int bindInfoDataLength,
			IConfigSameTypeCollectors configSameTypeCollectorsEventHandler) {
		try {
			// 数据异常
			if (bindInfoDataLength < 4) {
				return false;
			}

			byte[] tmpData = new byte[bindInfoDataLength - 4];
			System.arraycopy(bindInfoData, 4, tmpData, 0, tmpData.length);
			String latestBindInfo = new String(tmpData, "UTF-8");

			// 不需要更新
			if (latestBindInfo.equals(mLocalMobile.getBindInfo())) {
				return true;
			}

			// 解析失败，数据有问题
			Boolean[] needConfigSameTypeDevices = new Boolean[1];
			Mobile mobile = null;
			if ((mobile = parseBindInfo(latestBindInfo,
					needConfigSameTypeDevices)) == null) {
				return false;
			}

			// 需要配置同类采集器，弹出配置界面（略），设置 Mobile 对象中用户与采集器的关系
			if (needConfigSameTypeDevices[0]) {
				// 需要配置同类 采集器但是没有提供回调函数
				if (configSameTypeCollectorsEventHandler == null) {
					return false;
				}

				// 配置失败
				if (!configSameTypeCollectorsEventHandler.config(mobile)) {
					return false;
				}
			}

			// 更新数据库
			return updateMobile(mobile);
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to updateLatestBindInfo!", e);
			return false;
		}
	}

	/**
	 * 将绑定信息转化为 Mobile 对象。
	 * 
	 * @param bindInfo
	 *            ：绑定信息。
	 * @param needConfigSameTypeDevices
	 *            ：是否需要配置同类型采集器（需要传递一个容量为 1 的数组）。
	 */
	static Mobile parseBindInfo(String bindInfo,
			Boolean[] needConfigSameTypeDevices) {
		Mobile mobile = null;
		try {
			mobile = new Mobile(mLocalMobile.Mac);
			Map<Integer, LightUser> lightUsers = new HashMap<Integer, LightUser>();
			Map<Integer, Collector> collectors = new HashMap<Integer, Collector>();

			JsonReader reader = new JsonReader(new StringReader(bindInfo));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equals("DeviceList")) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginObject();
						Collector collector = new Collector();
						while (reader.hasNext()) {
							String name = reader.nextName();
							if (name.equals("Id")) {
								collector.Id = reader.nextInt();
							} else if (name.equals("HeartBeatInterval")) {
								collector.HeartBeatInterval = reader.nextInt();
							} else if (name.equals("PhysicalCode")) {
								collector.PhysicalCode = reader.nextString();
							} else if (name.equals("DeviceType")) {
								collector.DeviceType = (byte) reader.nextInt();
							} else if (name.equals("Mac")) {
								collector.Mac = reader.nextString();
							} else if (name.equals("NumOfChannels")) {
								collector.NumOfChannels = (byte) reader
										.nextInt();
							} else if (name.equals("PairingCode")) {
								collector.PairingCode = reader.nextString();
							} else if (name.equals("PassiveMode")) {
								collector.PassiveMode = reader.nextInt() == 0 ? false
										: true;
							} else if (name.equals("ProtocolType")) {
								collector.ProtocolType = reader.nextInt();
							} else if (name.equals("Desc")) {
								collector.Desc = reader.nextString();
							}
						}
						collectors.put(collector.Id, collector);
						reader.endObject();
					}
					reader.endArray();
				} else if (tagName.equals("PatientList")) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginObject();
						LightUser user = new LightUser();
						while (reader.hasNext()) {
							String name = reader.nextName();
							if (name.equals("UserID")) {
								user.ID = reader.nextInt();
							} else if (name.equals("MemberID")) {
								user.MemberId = reader.nextString();
							} else if (name.equals("FullName")) {
								user.Name = reader.nextString();
							}
						}
						lightUsers.put(user.ID, user);
						reader.endObject();
					}
					reader.endArray();
				}
			}
			reader.endObject();

			mobile.setLightUsers(lightUsers);
			mobile.setCollectors(collectors);

			if (lightUsers != null && !lightUsers.isEmpty()) {
				Collection<LightUser> users = lightUsers.values();
				Map<Byte, List<Collector>> map = mobile.getSameTypeCollectors();
				for (Byte deviceType : map.keySet()) {
					List<Collector> collectorList = map.get(deviceType);
					if (collectorList.size() == 1) {
						int collectorId = collectorList.get(0).Id;
						for (LightUser user : users) {
							if (user.Devices == null) {
								user.Devices = new HashMap<Byte, Integer>();
							}
							user.Devices.put(deviceType, collectorId);
						}
					}
				}
			}
			needConfigSameTypeDevices[0] = mobile.containsSameTypeCollectors();
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to parse bind info!", e);
			mobile = null;
		}

		return mobile;
	}

	/**
	 * 更新最新的移动终端信息。
	 * 
	 * @param mobile
	 *            ：最新的移动终端对象。
	 */
	static boolean updateMobile(Mobile mobile) {
		if (mobile != null) {
			if (mobile.save()) {
				mLocalMobile = mobile;
				return true;
			} else {
				Mobile.clear();
				mLocalMobile.save();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 添加采集器 的内部方法。
	 * 
	 * @param helper
	 *            ：DB操作对象
	 * @param collector
	 *            ：采集器
	 */
	static boolean addCollectorInner(MHubDBHelper helper, Collector collector) {
		try {
			helper.execSQL("replace into "
					+ DBConstDef.TABLE_COLLECTOR
					+ " (ID, HeartBeatInterval, PhysicalCode, DeviceType, Mac, NumOfChannels, PairingCode, PassiveMode, ProtocolType, Desc) values("
					+ collector.Id + ", " + collector.HeartBeatInterval + ", '"
					+ collector.PhysicalCode + "', " + collector.DeviceType
					+ ", '" + collector.Mac + "', " + collector.NumOfChannels
					+ ", '" + collector.PairingCode + "', "
					+ (collector.PassiveMode ? 1 : 0) + ", "
					+ collector.ProtocolType + ", '"
					+ (collector.Desc == null ? "" : collector.Desc) + "')");
			return true;
		} catch (Exception e) {
			Log.e("DeviceManager", "Failed to addCollectorInner!", e);
			return false;
		}
	}
}
