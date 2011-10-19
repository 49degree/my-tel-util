package com.szxys.mhub.base.communication;

import android.database.Cursor;
import android.util.Log;

import com.szxys.mhub.base.manager.DBConstDef;
import com.szxys.mhub.base.manager.MHubDBHelper;
import com.szxys.mhub.interfaces.Collector;

/**
 * 蓝牙通信管理帮助类。
 */
public class BlueToothUtils {
	/**
	 * 获取蓝牙通信调度单例。
	 */
	public static ICommunication getBlueToothInstance() {
		return BthDeviceScheduler.getSingleton();
	}
}

/**
 * 蓝牙通信管理内部帮助类。
 */
class BlueToothUtilInner {
	/**
	 * 获取指定用户、采集器类型所对应的采集器（数据库操作）。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param deviceType
	 *            ：采集器类型。
	 */
	static Collector getCollector(int userId, byte deviceType) {
		Collector collector = null;
		MHubDBHelper helper = null;
		Cursor cur = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);

			cur = helper.query("select * from " + DBConstDef.TABLE_COLLECTOR
					+ " where ID = (select CollectorID from "
					+ DBConstDef.TABLE_USER_COLLECTOR_RELATIONAL
					+ " where UserID = " + userId + " and DeviceType = "
					+ deviceType + ")");
			if (cur != null && cur.getCount() == 1) {
				cur.moveToFirst();
				collector = new Collector();
				collector.Id = cur.getInt(cur.getColumnIndex("ID"));
				collector.HeartBeatInterval = cur.getInt(cur
						.getColumnIndex("HeartBeatInterval"));
				collector.PhysicalCode = cur.getString(cur
						.getColumnIndex("PhysicalCode"));
				collector.DeviceType = (byte) cur.getInt(cur
						.getColumnIndex("DeviceType"));
				collector.Mac = cur.getString(cur.getColumnIndex("Mac"));
				collector.NumOfChannels = (byte) cur.getInt(cur
						.getColumnIndex("NumOfChannels"));
				collector.PairingCode = cur.getString(cur
						.getColumnIndex("PairingCode"));
				collector.PassiveMode = cur.getInt(cur
						.getColumnIndex("PassiveMode")) == 0 ? false : true;
				collector.ProtocolType = cur.getInt(cur
						.getColumnIndex("ProtocolType"));
				collector.Desc = cur.getString(cur.getColumnIndex("Desc"));
			}
		} catch (Exception e) {
			Log.e("BlueToothUtilInner", "Failed to get Collector!", e);
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
}