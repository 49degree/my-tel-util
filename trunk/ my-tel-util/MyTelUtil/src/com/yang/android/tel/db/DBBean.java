package com.yang.android.tel.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * 数据表对象
 * @author szluyl
 *
 */
public final class DBBean {
	public static Map<String,String> needInitTables = new HashMap<String,String>(1);
	
	static{
		needInitTables.put("RefuseTel","com.yang.android.tel.db.DBBean$RefuseTel");
	}
	
	private DBBean(){
		
	}
	
	/**
	 *拒绝来电表 
	 * @author szluyl
	 *
	 */
	public static final class RefuseTel implements BaseColumns{
		private RefuseTel(){
		}
		public static final String TABLE_NAME ="RefuseTelNum"; 
		public static final String REFUSE_TEL_NUM ="refuse_tel_num"; //电话号码
		public static final String REFUSE_CALL ="refuse_call"; //是否拒绝来电
		public static final String REFUSE_MESSAGE ="refuse_message"; //是否拒绝信息
		public static final String REFUSE_MODI_TIME ="refuse_modi_time"; //修改时间
		public static final String REFUSE_MEMO ="refuse_memo"; //备注
		
		public static final String DEFAULT_SORT_ORDER ="_id asc";
	}

}
