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
	public static List<String> needInitTables = new ArrayList<String>(1);
	
	static{
		needInitTables.add("com.etelecom.android.iknow.db.DBBean$ProdList");
	}
	
	private DBBean(){
		
	}
	
	/**
	 *查询信息列表对象 
	 * @author szluyl
	 *
	 */
	public static final class RefuseTel implements BaseColumns{
		private RefuseTel(){
		}
		public static final String TABLE_NAME ="prod_query_history"; 
		public static final String REQUEST_URL ="request_url"; 
		public static final String REQUEST_PARAMS ="request_params"; 
		public static final String RESPONS_CP ="respons_cp"; 
		public static final String RESPONS_PS ="respons_ps"; 
		public static final String RESPONS_STR ="response_str"; 
//		public static final String RESPONS_NAME ="respons_name"; 
//		public static final String RESPONS_DTYPE ="respons_dtype"; 

		
		
		public static final String DEFAULT_SORT_ORDER ="_id asc";
		
		//判断当前数据在表中是否存在
		public static int queryRowNum(SQLiteDatabase sqlDb,String requestUrl,Map<String,String> params,int cp,int ps){
			int rowNum = 0;
			
			Cursor cursor = sqlDb.query(RefuseTel.TABLE_NAME, 
                    new String[]{"_id"}, 
                    "request_url=? and respons_cp=? and respons_ps=? and request_params=?", 
                    new String[]{requestUrl,String.valueOf(cp),String.valueOf(ps),RefuseTel.mapToString(params)}, null, null, null);
			rowNum = cursor.getCount();
			cursor.close();
			return rowNum;
		}
		
		//插入记录到当前表
		public static void insertIntoTable(SQLiteDatabase sqlDb,String requestUrl,Map<String,String> params,
				int cp,int ps,String responseStr){
				ContentValues values = new ContentValues();
				values.put(RefuseTel.REQUEST_URL, requestUrl);
				values.put(RefuseTel.REQUEST_PARAMS, RefuseTel.mapToString(params));
				values.put(RefuseTel.RESPONS_CP, cp);
				values.put(RefuseTel.RESPONS_PS, ps);
				values.put(RefuseTel.RESPONS_STR, responseStr);
				sqlDb.insert(RefuseTel.TABLE_NAME, null, values);

		}
		//查询记录
		public static List<String> queryList(SQLiteDatabase sqlDb,String requestUrl,Map<String,String> params,int cp,int ps){
			Cursor cursor = sqlDb.query(RefuseTel.TABLE_NAME, 
                    new String[]{RefuseTel.RESPONS_STR}, 
                    "request_url=? and respons_cp=? and respons_ps=? and request_params=?", 
                    new String[]{requestUrl,String.valueOf(cp),String.valueOf(ps),RefuseTel.mapToString(params)}, null, null, null);
			List<String> prodList = new ArrayList<String>(cursor.getCount());
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				prodList.add(cursor.getString(0));
				cursor.moveToNext();
			}
			cursor.close();
			
			return prodList;
		}
		
		public static String mapToString(Map<String,String> params){
			StringBuffer paramsStr = new StringBuffer("{");
			for(String key:params.keySet()){
				paramsStr.append(paramsStr.length()==1?"":",");
				paramsStr.append("'").append(key).append("':'").append(params.get(key)==null?"":params.get(key).trim()).append("'");
			}
			paramsStr.append("}");
			return paramsStr.toString();
		}
	}
	
	public static void main(String[] args){
		try{
			for(String tableName:DBBean.needInitTables){
				Class newoneClass = Class.forName(tableName);
				Field[] fs = newoneClass.getFields();
				for(Field f:fs){
					System.out.println(f.getGenericType());
				}
				
			}
		}catch(ClassNotFoundException e){
			
		}
	}
	
	
}
