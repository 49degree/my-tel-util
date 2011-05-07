package com.yang.android.tel.db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * 数据库管理类
 * @author szluyl
 *
 */
public class DBOperator extends SQLiteOpenHelper { 
	public static String TAG = "DBOperator";
	public DBOperator(Context context,String dbName,String mName,int dbVersion) { 
		super(context, dbName, null, dbVersion);  
	}
	
	@Override  
	public void onCreate(SQLiteDatabase db) {  
		//遍历需要初始化的数据表
		Log.i(TAG,"初始化的数据表 ***********************************************************");
		try{
			for(String table:DBBean.needInitTables.keySet()){
				Log.i(TAG,"***********************************************************"+table);
				StringBuffer sql = new StringBuffer();
				Class newoneClass = Class.forName(DBBean.needInitTables.get(table));
				Field[] fs = newoneClass.getFields();
				StringBuffer tableField = new StringBuffer("");
				String tableName = null;
				for(Field f:fs){
					if(Modifier.isStatic(f.getModifiers())){
						if(f.getName().indexOf("SORT_ORDER")<0&&!f.getName().equals("TABLE_NAME")&&!f.getName().equals("_COUNT")){
							if(f.getName().equals("_ID")){
								tableField.append(tableField.length()>0?",":"").append(f.get(newoneClass)).append(" INTEGER PRIMARY KEY");
							}else{
								tableField.append(tableField.length()>0?",":"").append(f.get(newoneClass)).append(" text");	
							}
						}
						if(f.getName().equals("TABLE_NAME")){
							tableName = String.valueOf(f.get(newoneClass));
						}
					}
				}
				sql.append("create table ").append(tableName).append("(").append(tableField).append(");");
				db.execSQL(sql.toString());
			}
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(IllegalAccessException ie){
			ie.printStackTrace();
		}
	}  
	@Override  
	public void onUpgrade(SQLiteDatabase db, int oldVersion,   int newVersion) {
	}  
	@Override  
	public void onOpen(SQLiteDatabase db) {
	} 

	/**
	 * 查询COUROR
	 * @return
	 */
	public static Cursor queryCursor(String tableBean,SQLiteDatabase sqlDb,String[] returnColumn,
			Map<String,String> params) throws ClassNotFoundException,NoSuchFieldException,IllegalAccessException{
		try{
			//params转换成可执行查询条件
			String queryParm = null;
			String[] queryParmValue = null;
			if(params!=null&&params.size()>0){
				StringBuffer queryKey = new StringBuffer("");
				queryParmValue = new String[params.size()];
				int i = 0;
				for(String key:params.keySet()){
					queryKey.append(queryKey.length()>0?" and ":"").append(key).append("=?");
					queryParmValue[i++]=params.get(key);
				}
				queryParm = queryKey.toString();
			}
			
			Class newoneClass = Class.forName(tableBean);//获取表名
			Log.i(TAG,queryParm+String.valueOf(newoneClass.getField("TABLE_NAME").get(newoneClass)));
			Cursor cursor = sqlDb.query(String.valueOf(newoneClass.getField("TABLE_NAME").get(newoneClass)), 
					returnColumn, queryParm, queryParmValue, null, null, null);
			return cursor;
		}catch(ClassNotFoundException ce){
			throw ce;
		}catch(NoSuchFieldException nfe){
			throw nfe;
		}catch(IllegalAccessException ile){
			throw ile;
		}
	}
	
	/**
	 * 判断当前数据在表中是否存在
	 * @return
	 */
	public static int queryRowNum(String tableBean,SQLiteDatabase sqlDb,
			Map<String,String> params) throws ClassNotFoundException,NoSuchFieldException,IllegalAccessException{
		int rowNum = 0;
		try{
			Cursor cursor = DBOperator.queryCursor(tableBean, sqlDb, new String[]{"_id"}, params);
			rowNum = cursor.getCount();
			cursor.close();
		}catch(ClassNotFoundException ce){
			throw ce;
		}catch(NoSuchFieldException nfe){
			throw nfe;
		}catch(IllegalAccessException ile){
			throw ile;
		}
		
		return rowNum;
	}
	
	/**
	 * 查询记录
	 * @param tableBean
	 * @param sqlDb
	 * @param params
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchFieldException
	 */
	public static List<String[]> queryList(String tableBean,SQLiteDatabase sqlDb,String[] returnColumn,
			Map<String,String> params) throws ClassNotFoundException,NoSuchFieldException,IllegalAccessException{
		 List<String[]> returnList = new ArrayList<String[]>();
		try{
			Cursor cursor = DBOperator.queryCursor(tableBean, sqlDb, returnColumn, params);
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				String[] columValue = new String[cursor.getColumnCount()];
				for(int i=0;i<cursor.getColumnCount();){
					columValue[i]=cursor.getString(i++);
				}
				returnList.add(columValue);
				cursor.moveToNext();
			}
			cursor.close();
		}catch(ClassNotFoundException ce){
			throw ce;
		}catch(NoSuchFieldException nfe){
			throw nfe;
		}catch(IllegalAccessException ile){
			throw ile;
		}
		
		return returnList;
	}
	
	/**
	 * 查询记录到MAP中
	 * @param tableBean
	 * @param sqlDb
	 * @param params
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchFieldException
	 */
	public static List<Map<String,String>> queryMapList(String tableBean,SQLiteDatabase sqlDb,String[] returnColumn,
			Map<String,String> params) throws ClassNotFoundException,NoSuchFieldException,IllegalAccessException{
		 List<Map<String,String>> returnList = new ArrayList<Map<String,String>>();
		try{
			Cursor cursor = DBOperator.queryCursor(tableBean, sqlDb, returnColumn, params);
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				Map<String,String> columValue = new HashMap<String,String>();
				for(int i=0;i<cursor.getColumnCount();){
					columValue.put(returnColumn[i], cursor.getString(i++));
				}
				returnList.add(columValue);
				cursor.moveToNext();
			}
			cursor.close();
		}catch(ClassNotFoundException ce){
			throw ce;
		}catch(NoSuchFieldException nfe){
			throw nfe;
		}catch(IllegalAccessException ile){
			throw ile;
		}
		
		return returnList;
	}	
	/**
	 * 插入记录到当前表
	 * @param sqlDb
	 * @param requestUrl
	 * @param params
	 * @param cp
	 * @param ps
	 * @param responseStr
	 */
	public static long insertIntoTable(String tableBean,SQLiteDatabase sqlDb,
			ContentValues values) throws ClassNotFoundException,NoSuchFieldException,IllegalAccessException{
		long insertRow = 0;
		try{
			Class newoneClass = Class.forName(tableBean);//获取表名
			insertRow = sqlDb.insert(String.valueOf(newoneClass.getField("TABLE_NAME").get(newoneClass)), null, values);
		}catch(ClassNotFoundException ce){
			throw ce;
		}catch(NoSuchFieldException nfe){
			throw nfe;
		}catch(IllegalAccessException ile){
			throw ile;
		}
		return insertRow;
	}
	

}
