package com.skyeyes.base.db;

import java.util.HashMap;
import java.util.Map;
/**
 * 数据表对象
 * @author szluyl
 *
 */
public final class DBBean {
	// 4.1.1	日志记录表(TB_OPERATE_LOG)
	public static final String TBOpenCloseDoorIdBean = "OpenCloseDoorIdBean";
	public static final String TBOpenCloseDoorInfoBean = "OpenCloseDoorInfoBean";
	
	//key为表名，value为表对应的实体类
	public static Map<String,String> needInitTables = new HashMap<String,String>(1);
	
	
	
	public static Map<String,String> primaryKey = new HashMap<String,String>(1);
	static{
		//定义表与实体类对应关系
		needInitTables.put(TBOpenCloseDoorIdBean,"com.skyeyes.base.bean.OpenCloseDoorIdBean");
		needInitTables.put(TBOpenCloseDoorInfoBean,"com.skyeyes.base.bean.OpenCloseDoorInfoBean");
		//设置主键
		primaryKey.put(TBOpenCloseDoorIdBean,"_id");
		primaryKey.put(TBOpenCloseDoorInfoBean,"_id");
		
		
	}
	

}
