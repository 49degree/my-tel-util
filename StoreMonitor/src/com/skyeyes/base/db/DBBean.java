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
	public static final String TBAppCloud = "AppCloud";
	public static final String TBAppMap = "AppMap";
	public static final String TBApps= "Apps";
	public static final String TBMappings= "Mappings";
	public static final String TBPages= "Pages";
	
	//key为表名，value为表对应的实体类
	public static Map<String,String> needInitTables = new HashMap<String,String>(1);
	
	
	
	public static Map<String,String> primaryKey = new HashMap<String,String>(1);
	static{
		//定义表与实体类对应关系
		needInitTables.put(TBAppCloud,"com.homecare.controllermapper.bean.AppCloud");
		needInitTables.put(TBAppMap,"com.homecare.controllermapper.bean.AppMap");
		needInitTables.put(TBApps,"com.homecare.controllermapper.bean.Apps");
		needInitTables.put(TBMappings,"com.homecare.controllermapper.bean.Mappings");
		needInitTables.put(TBPages,"com.homecare.controllermapper.bean.Pages");
	
		//设置主键
		primaryKey.put(TBAppCloud,"_id");
		primaryKey.put(TBAppMap,"_id");
		primaryKey.put(TBApps,"_id");
		primaryKey.put(TBMappings,"_id");
		primaryKey.put(TBPages,"_id");
		
		
	}
	

}
