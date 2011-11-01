package com.guanri.android.jpos.db;

import java.util.HashMap;
import java.util.Map;
/**
 * 数据表对象
 * @author 
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
		needInitTables.put(TB_OPERATE_LOG,"com.guanri.android.jpos.bean.OperateLogBean");
		needInitTables.put(TB_SALE_RECORD,"com.guanri.android.jpos.bean.SaleDataLogBean");
		//设置主键
		primaryKey.put(TB_OPERATE_LOG,"Log_id");
		primaryKey.put(TB_SALE_RECORD, "logid");

	}
	

}
