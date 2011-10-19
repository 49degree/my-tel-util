package com.xys.ecg.sqlite;


import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xys.ecg.bean.UserEntity;

public class UserDB {
	
	private DBHelper dbHelper=null;
	private SQLiteDatabase sqldatabase=null;
	
	public UserDB(Context context)
	{
		dbHelper=new DBHelper(context,CommDB.dataBaseName);
		sqldatabase=dbHelper.getWritableDatabase();
	}



	
	//通过用户 Id 获取 Cursor
	public Cursor selectUserById(int userId)
	{
		Cursor cursor=sqldatabase.query("tb_user",null, "UserID=?",new String[]{""+userId+""}, null, null, null);
		return cursor;
	}
	
	//通过用户名 UserName 获取 Cursor
	public Cursor selectUserByName(String userName)
	{
		Cursor cursor=sqldatabase.query("tb_user",null, "UserName=?",new String[]{""+userName+""}, null, null, null);
		return cursor;
	}
	

	
	//获取所有的用户信息
	public Cursor getAllUser()
	{
		Cursor cursor=sqldatabase.query("tb_user",null,null,null, null, null, null);
		return cursor;
	}
	
	//插入一条用户信息
	public long insertUser(int userId, String userName)
	{
		long rowNum = -1;
		try
		{
			ContentValues cv=new ContentValues();
			cv.put("UserID",userId);
			cv.put("UserName",userName);
			
			rowNum = sqldatabase.insert("tb_user",null,cv);
		}catch(Exception ex)
		{
		  Log.v("InsertUser(int userId, String userName)", ex.getMessage());
		  return -1;
		}
		return rowNum;
	}
	
	public long insertUser(UserEntity userEntity)
	{
		long rowNum = -1;
		try
		{
			rowNum = insertUser(userEntity.getUserID() ,userEntity.getUserName());
		}catch(Exception ex)
		{
		  Log.v("InsertUser(UserEntity userEntity)", ex.getMessage());
		  
		  return -1;
		}
		return rowNum;
	}
	
	
	public void insertUser(List<UserEntity> listUserEntity)
	{
		sqldatabase.beginTransaction();  //开始事物
		try
		{
			for(int i = 0 ;i < listUserEntity.size() ;i++)
			{
				ContentValues cv=new ContentValues();
				cv.put("UserID",listUserEntity.get(i).getUserID());
				cv.put("UserName",listUserEntity.get(i).getUserName());
				
				sqldatabase.insert("tb_user",null,cv);
			}
			//提交事物
			sqldatabase.setTransactionSuccessful();
		}finally{
		    //如果上面SQL语句发生了异常，没有执行db.setTransactionSuccessful()方法，db.endTransaction()即回滚
			sqldatabase.endTransaction();
		}
		
	}

	public int updateUser(UserEntity userEntity)
	{
		int rowNum = -1;
		try
		{
			rowNum =updateUser(userEntity.getUserID(), userEntity.getUserName());
		}catch(Exception ex){
			Log.v("UpdateUser(UserEntity userEntity)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}

	public int updateUser(int userId, String userName )
	{
		int rowNum = -1;
		try
		{
			ContentValues cv=new ContentValues();
			cv.put("UserID",userId);
			cv.put("UserName",userName);
			
			rowNum = sqldatabase.update("tb_user", cv, "UserID=?", new String[]{""+userId+""});
		}catch(Exception ex){
			Log.v("UpdateUser(int userId, String userName )", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	
	public int deleteUserById(int userId)
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_user", "UserID=?", new String[]{""+userId+""});
		}catch(Exception ex){
			Log.v("DeleteUserById(int userId)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	
	public int deleteUserByUserName(String userName)
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_user", "UserName=?", new String[]{""+userName+""});
		}catch(Exception ex){
			Log.v("DeleteUserByUserName(String userName)", ex.getMessage());
			return -1;
		}
		return rowNum;  
	}
	

	
	public int deleteAllUser()
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_user",null,null);
		}catch(Exception ex){
			Log.v("DeleteAllUser()", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	
	public void close()
	{
		dbHelper.close();
		sqldatabase.close();
	}
	
}
