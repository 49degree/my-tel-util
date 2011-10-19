package com.guanri.android.insurance.db;

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
	// 4.1.2	业务方案记录表(TB_INSU_PLAN_RECORD) 
	public static final String TB_INSU_PLAN_RECORD = "TB_INSU_PLAN_RECORD";
	// 4.1.3	对账记录表(TB_SALE_CHECK)
	public static final String TB_SALE_CHECK = "TB_SALE_CHECK";
	// 4.1.4	保单销售记录表(TB_SALE_ORDER) 
	public static final String TB_SALE_ORDER = "TB_SALE_ORDER";
	// 4.1.5	废单记录表(TB_SALE_ORDER_USELESS) 
	public static final String TB_SALE_ORDER_USELESS = "TB_SALE_ORDER_USELESS";
	// 4.1.6	退单记录表(TB_SALE_ORDER_BACK)
	public static final String TB_SALE_ORDER_BACK = "TB_SALE_ORDER_BACK";
	// 4.1.7	登陆过终端的工号记录表(TB_OPERATOR_RECORD)
	public static final String TB_OPERATOR_RECORD = "TB_OPERATOR_RECORD";
	//key为表名，value为表对应的实体类
	public static Map<String,String> needInitTables = new HashMap<String,String>(1);
	
	public static Map<String,String> primaryKey = new HashMap<String,String>(1);
	static{
		//定义表与实体类对应关系
		needInitTables.put(TB_OPERATE_LOG,"com.guanri.android.insurance.bean.OperateLogBean");
		//needInitTables.put("TB_INSU_PLAN_RECORD","com.guanri.android.insurance.bean.InsuPlanRecordBean");
		needInitTables.put(TB_SALE_CHECK,"com.guanri.android.insurance.bean.SaleCheckBean");
		needInitTables.put(TB_INSU_PLAN_RECORD,"com.guanri.android.insurance.bean.InsuPlanRecordBean");
		needInitTables.put(TB_SALE_ORDER,"com.guanri.android.insurance.bean.SaleOrderBean");
		needInitTables.put(TB_SALE_ORDER_USELESS,"com.guanri.android.insurance.bean.SaleOrderUselessBean");
		needInitTables.put(TB_SALE_ORDER_BACK,"com.guanri.android.insurance.bean.SaleOrderBackBean");
		needInitTables.put(TB_OPERATOR_RECORD,"com.guanri.android.insurance.bean.OperatorRecordBean");

		//设置主键
		primaryKey.put(TB_OPERATE_LOG,"Log_id");
		primaryKey.put(TB_OPERATOR_RECORD, "Record_id");
	}
	

}
