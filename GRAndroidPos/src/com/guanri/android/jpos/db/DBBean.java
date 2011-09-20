package com.guanri.android.jpos.db;

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
	// 4.1.1	日志记录表(TB_OPERATE_LOG)
	public static final String TB_OPERATE_LOG = "TB_OPERATE_LOG";
	// 4.1.8	数据记录表(TB_SALE_RECORD)
	public static final String TB_SALE_RECORD = "TB_SALE_RECORD";
	//key为表名，value为表对应的实体类
	public static Map<String,String> needInitTables = new HashMap<String,String>(1);
	
	public static Map<String,String> primaryKey = new HashMap<String,String>(1);
	static{
		//定义表与实体类对应关系
		needInitTables.put(TB_OPERATE_LOG,"com.guanri.android.jpos.beans.OperateLogBean");
		needInitTables.put(TB_SALE_RECORD,"com.guanri.android.jpos.beans.SaleDataLogBean");
		//设置主键
		primaryKey.put(TB_OPERATE_LOG,"Log_id");
		primaryKey.put(TB_SALE_RECORD, "logid");

	}
	

}
