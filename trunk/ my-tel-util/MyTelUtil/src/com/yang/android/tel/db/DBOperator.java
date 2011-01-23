package com.yang.android.tel.db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库管理类
 * @author szluyl
 *
 */
public class DBOperator extends SQLiteOpenHelper { 
	public DBOperator(Context context,String dbName,String mName,int dbVersion) { 
		super(context, dbName, null, dbVersion);  
	}
	
	@Override  
	public void onCreate(SQLiteDatabase db) {  
		//遍历需要初始化的数据表
		try{
			for(String table:DBBean.needInitTables){
				StringBuffer sql = new StringBuffer();
				Class newoneClass = Class.forName(table);
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

}
