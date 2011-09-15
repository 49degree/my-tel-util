package com.guanri.android.jpos.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.guanri.android.lib.log.Logger;

/**
 * 数据库管理类
 * 
 * @author 杨雪平
 * 
 */
public class DBOperator{
	public static Logger logger = Logger.getLogger(DBOperator.class);//日志对象
	public static String TAG = "DBOperator";
	public static String DB_NAME = "APSAI_DATABASE";
	public static int DB_VERSION = 1;
	
	private static DBOperator instance = null;
	
	static{
    	instance = new DBOperator();
	}
	
	public static DBOperator getInstance(){
		return instance;
	}
	

	private DBOperator() {
	}

	public void onCreate() {

	}

	public void onUpgrade() {

	}

	public void onOpen() {
	}

	/**
	 * 查询
	 * @param tableName 表名
	 * @param returnColumn 需要返回数据的列名称
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	private void queryCursor(String tableName, String[] returnColumn,
			Map<String, String> params) {

	}

	/**
	 * 判断当前数据在表中是否存在
	 * 
	 * @param tableName 表名
	 * @param returnColumn 需要返回数据的列名称
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public int queryRowNum(String tableName, Map<String, String> params){
		return 0;
	}

	/**
	 * 查询记录，返回数组列表
	 * 
	 * @param tableName 表名
	 * @param returnColumn 需要返回数据的列名称
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public List<String[]> queryArrayList(String tableName,
			String[] returnColumn, Map<String, String> params){
		List<String[]> returnList = new ArrayList<String[]>();

		return returnList;
	}

	/**
	 * 查询记录到MAP中
	 * @param tableName 表名
	 * @param returnColumn 需要返回数据的列名称
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public List<Map<String, String>> queryMapList(String tableName,
			String[] returnColumn, Map<String, String> params){
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();

		return returnList;
	}
	
	/**
	 * 查询记录到实体类列表中
	 * @param tableName 表名
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public List<Object> queryBeanList(String tableName,Map<String, String> params){
		List<Object> returnList = new ArrayList<Object>();
		
		

		return returnList;
	}
	
	/**
	 * 插入记录到当前表
	 * @param tableName 表名
	 * @param value 表对应实体类对象
	 * @return
	 */
	public long insert(String tableName,Object value){
		
		long insertRow = 0;
		
		return insertRow;
	}
	
	/**
	 * 数据删除
	 * @param tableName
	 * @param params
	 */
	public void del(String tableName,Map<String, String> params){
	}
	/**
	 * 释放资源
	 */
//	public void release(){
//    	if(sqlDb!=null){
//    		sqlDb.close();
//    		sqlDb = null;
//    	}
//    	close();
//	}

}
