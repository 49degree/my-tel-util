/*
 * 文 件 名:  ConfigManager.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-4-18
 * 修改内容:  <修改内容>
 */

package com.szxys.mhub.base.manager;

import com.szxys.mhub.common.Logcat;

import android.database.Cursor;

/**
 * 配置管理类 提供write，read方法对数据库操作
 * 
 * @author yangzhao modify by supei
 * @version [版本号V01, 2011-4-18]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class ConfigManager {
	private static final String Tag = "ConfigManager";

	private static MHubDBHelper dbhelper;
	private static ConfigManager mInstance = null;

	static {
		mInstance = new ConfigManager();
	}

	/**
	 * 配置表中主服务器地址键值。
	 */
	public static final String CONFIG_KEY_SERVER_ADRESS = "ServerAdress";

	public static ConfigManager getInstance() {
		return mInstance;
	}

	private ConfigManager() {
		dbhelper = new MHubDBHelper();
	}

	/**
	 * 把数据以[key，value]形式写入数据库
	 * 把数据写入配置管理表，保证每种信息只有两条记录，一条为：原始记录。另一条为：最新记录。保证数据的安全性。
	 * 
	 * @param key
	 *            ：String 配置管理表的key值
	 * @param value
	 *            ：String 配置管理表的value值
	 * @return boolean 插入成功返回true，否则返回false
	 * @see [类、类#方法、类#成员]
	 */
	public boolean write(String key, String value) {
		boolean bResult = true;
		dbhelper.open(true);
		if (null != read(key)) {
			String strUpdateSQL = "update mb_config set Value = " + "'" + value
					+ "'" + " where Key = " + "'" + key + "'";
			try {

				dbhelper.execSQL(strUpdateSQL);
			} catch (Exception e) {
				bResult = false;
				Logcat.e(Tag, strUpdateSQL);
			}

		} else {
			String strInsertSQL = "insert into mb_config(Key,Value) values("
					+ "'" + key + "'" + ",'" + value + "')";
			try {
				dbhelper.execSQL(strInsertSQL);
			} catch (Exception e) {
				bResult = false;
				Logcat.e(Tag, strInsertSQL);
			}

		}
		return bResult;
	}

	/**
	 * 读取配置管理表中某种信息的值 读取column字段名为key
	 * ，它所对应的值。如：key="webservice",value="http://...."。返回两条key值相同的记录。
	 * 一条为：前一版本记录，另一条为：当前版本记录。
	 * 
	 * @param key
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String read(String key) {

		dbhelper.open(false);
		String strSelectSQL = "select Value from mb_config where Key =" + "'"
				+ key + "'";
		Cursor cur = null;
		try {
			cur = dbhelper.query(strSelectSQL);
			if (cur == null)
				return null;
			// cur.moveToNext();
			if (cur.isAfterLast())
				return null;
			else {
				return cur.getString(cur.getColumnIndex("Value"));
			}
		} catch (Exception e) {
			Logcat.e(Tag, strSelectSQL);
			return null;
		} finally {
			if (cur != null) {
				cur.close();
				cur = null;
			}
		}
	}
}
