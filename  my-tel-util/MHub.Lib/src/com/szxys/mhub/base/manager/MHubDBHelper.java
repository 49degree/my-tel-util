package com.szxys.mhub.base.manager;

import com.szxys.mhub.app.MhubApplication;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MHubDBHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "mhub.db";
	private final static int DATABASE_VERSION = 1;
	private SQLiteDatabase db = null;

	public MHubDBHelper() {
		super(MhubApplication.getInstance().getApplicationContext(),
				DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL(DBConstDef.TABLE_CONFIG_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_COLLETOR_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_USER_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_ORGANIZATION_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_USER_ORG_RELATION_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_USER_COLLECTOR_RELATION_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_ERRORMESSAGE_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_Monitoring_Parameters_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_INCOMMINGMSG_CREATE_SQL);
		db.execSQL(DBConstDef.TABLE_OUTGOINT_MSG_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * 建立一个数据库连接。
	 * 
	 * @param readonly
	 *            ：是否以只读方式建立。
	 */
	public void open(boolean readonly) {
		if (this.db == null) {
			if (readonly) {
				this.db = getWritableDatabase();
			} else {
				this.db = getReadableDatabase();
			}
		}
	}

	/**
	 * 执行数据库查询操作。
	 * 
	 * @param sql
	 *            ：要执行的查询语句。
	 */
	public Cursor query(String sql) {
		try {
			Cursor cur = this.db.rawQuery(sql, null);
			if (cur != null) {
				cur.moveToFirst();
			}
			return cur;
		} catch (Exception e) {
			Log.e("MHubDBHelper", "Failed to query!", e);
			return null;
		}
	}

	/**
	 * 执行数据库操作。
	 * 
	 * @param sql
	 *            ：要执行的Sql语句。
	 */
	public boolean execSQL(String sql) {
		try {
			this.db.execSQL(sql);
			return true;
		} catch (Exception e) {
			Log.e("MHubDBHelper", "Failed to execSQL!", e);
			return false;
		}
	}

	/**
	 * 关闭一个数据库连接。
	 */
	public void close() {
		if (this.db != null) {
			try {
				this.db.close();
			} catch (Exception e) {
				Log.e("MHubDBHelper", "Failed to close!", e);
			}
			this.db = null;
		}
	}
}
