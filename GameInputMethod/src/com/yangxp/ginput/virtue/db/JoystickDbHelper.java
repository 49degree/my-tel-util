package com.yangxp.ginput.virtue.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yangxp.ginput.virtue.bean.KeyMapping;

public class JoystickDbHelper { 

	private static File f = new File("/data/data/com.qucii.gameconfig/databases/db_setting");
	private static String querySql = "select * from mappings where pageid in(" +
			"select _id from pages where mapid =(" +
			"select _id from appmap where appVersion=? and appid =(" +
			"select _id from apps where name=?)))";
	
	private static String querySql2 = "select * from mappings where pageid in(" +
			"select _id from pages where mapid =(" +
			"select _id from appmap where appVersion=(select max(appVersion)  from appmap where appid=(select _id from apps where name=?)) and appid =(" +
			"select _id from apps where name=?)))";
	public static synchronized List<KeyMapping> queryMappings(String pkg,int versionCode) throws Exception{
		SQLiteDatabase db = null;
		Cursor c = null;
		try{
			Log.i("JoystickDbHelper", "queryMappings+++++++++++");
			ArrayList<KeyMapping> result = new ArrayList<KeyMapping>();
			
			if(f.exists())
				Log.i("JoystickDbHelper", "database file is exists");
			else{
				Log.e("JoystickDbHelper", "database file is not exists");
				return null;
			}
			
			db = SQLiteDatabase.openDatabase(f.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY) ;
			c = db.rawQuery(querySql, new String[]{String.valueOf(versionCode),pkg}); 
			
			if(c.getCount()==0){
				try{
					c.close();
				}catch(Exception e){
					
				}
				//if the version has no config ,then qury max version config
				c = db.rawQuery(querySql2, new String[]{pkg,pkg}); 
			}
			while (c.moveToNext()) {  
				KeyMapping keyMapping = new KeyMapping();
				
				keyMapping.id = c.getInt(c.getColumnIndex("_id"));
				keyMapping.pageId = c.getInt(c.getColumnIndex("pageId"));
				keyMapping.toPage = c.getInt(c.getColumnIndex("toPage"));
				keyMapping.type = c.getInt(c.getColumnIndex("type"));
				keyMapping.key = c.getInt(c.getColumnIndex("key"));
				keyMapping.keyDrag = c.getInt(c.getColumnIndex("keyDrag"));
				keyMapping.keyClick = c.getInt(c.getColumnIndex("keyClick"));
				keyMapping.x = c.getInt(c.getColumnIndex("x"));
				keyMapping.y = c.getInt(c.getColumnIndex("y"));
				keyMapping.radius = c.getInt(c.getColumnIndex("radius"));
				keyMapping.record = c.getBlob(c.getColumnIndex("record"));
				
				result.add(keyMapping); 
				
				if((keyMapping.type==0||keyMapping.type==5)&&keyMapping.key==106){
					keyMapping.key = 220;
				}
				if((keyMapping.type==0||keyMapping.type==5)&&keyMapping.key==107){
					keyMapping.key = 221;
				}
				
//				Log.i("JoystickDbHelper",
//						keyMapping.toString()+
//						":keyMapping.record.length:"+(keyMapping.record!=null?keyMapping.record.length:0));
				
			}  
			return result;
		}catch(Exception e){
			throw e;
		}finally{
			try{
				c.close();
			}catch(Exception e){
				
			}
			try{
				db.close();
			}catch(Exception e){
				
			}
		}
	}
}