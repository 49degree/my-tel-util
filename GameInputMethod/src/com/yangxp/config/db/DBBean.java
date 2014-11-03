package com.yangxp.config.db;

import java.util.HashMap;
import java.util.Map;

import com.yangxp.config.bean.AppCloud;
import com.yangxp.config.bean.AppMap;
import com.yangxp.config.bean.Apps;
import com.yangxp.config.bean.Mappings;
import com.yangxp.config.bean.Pages;
/**
 * 数据表对象
 * @author szluyl
 *
 */
public final class DBBean {
	public static final String DB_SETTING = "db_setting";
	public static final String DB_FILE = "db_file";
	
	// 4.1.1	日志记录表(TB_OPERATE_LOG)
	public static final String TBAppCloud = "AppCloud";
	public static final String TBAppMap = "AppMap";
	public static final String TBApps= "Apps";
	public static final String TBMappings= "Mappings";
	public static final String TBPages= "Pages";
	
	public static final Map<String,Map<String,String>> DB_CONFIG = new HashMap<String,Map<String,String>>();
	public static final Map<String,Integer> DB_VERSION = new HashMap<String,Integer>();
	
	//key为表名，value为表对应的实体类
	public static Map<String,String> needInitTables = new HashMap<String,String>(1);
	public static Map<String,String> primaryKey = new HashMap<String,String>(1);
	static{
		//定义表与实体类对应关系
		needInitTables.put(TBAppCloud,AppCloud.class.getName());
		needInitTables.put(TBAppMap,AppMap.class.getName());
		needInitTables.put(TBApps,Apps.class.getName());
		needInitTables.put(TBMappings,Mappings.class.getName());
		needInitTables.put(TBPages,Pages.class.getName());
	
		//设置主键
		primaryKey.put(TBAppCloud,"_id");
		primaryKey.put(TBAppMap,"_id");
		primaryKey.put(TBApps,"_id");
		primaryKey.put(TBMappings,"_id");
		primaryKey.put(TBPages,"_id");
		//数据库名称
		DB_CONFIG.put(DB_SETTING, needInitTables);
		DB_CONFIG.put(DB_FILE, needInitTables);
		//数据库版本号
		DB_VERSION.put(DB_SETTING, 1);
		DB_VERSION.put(DB_FILE, 1);
	}
	

}
