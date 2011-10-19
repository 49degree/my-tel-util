package com.xys.ecg.sqlite;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xys.ecg.bean.ContactEntity;

public class ContactDB {

	private DBHelper dbHelper=null;
	private SQLiteDatabase sqldatabase=null;

	public ContactDB(Context context)
	{
		dbHelper=new DBHelper(context,CommDB.dataBaseName);
		sqldatabase=dbHelper.getWritableDatabase();
	}
	
	//通过  联系信息Id 获取 Cursor
	public Cursor selectContactByContactId(int contactId)
	{
		Cursor cursor=sqldatabase.query("tb_Contact",null, "ContactID=?",new String[]{""+contactId+""}, null, null, null);
		return cursor;
	}
	
	//通过  用户 Id 获取Cursor
	public Cursor selectContactByUserID(int userId)
	{
		Cursor cursor=sqldatabase.query("tb_Contact",null, "UserID=?",new String[]{""+userId+""}, null, null, null);
		return cursor;
	}

	//获取所有医嘱表信息
	public Cursor getAllContact()
	{
		Cursor cursor=sqldatabase.query("tb_Contact",null,null,null, null, null, null);
		return cursor;
	}
	
	//插入一条联系人信息表
	public long insertContact(int userID ,String contactName ,int sMS ,int phone ,String phoneNum ,String mSMContent)
	{
		long rowNum = -1;
		try
		{
			ContentValues cv=new ContentValues();
			cv.put("UserID",userID);
			cv.put("ContactName",contactName);
			cv.put("SMS",sMS);
			cv.put("Phone",phone);
			cv.put("PhoneNum",phoneNum);
			cv.put("MSMContent",mSMContent);
			
			rowNum = sqldatabase.insert("tb_Contact",null,cv);
		}catch(Exception ex)
		{
		  Log.v("InsertContact(int userID ,String contactName ,int sMS ,int phone ,String phoneNum ,String mSMContent)", ex.getMessage());
		  return -1;
		}
		return rowNum;
	}
	
	public long insertContact(ContactEntity contactEntity)
	{
		long rowNum = -1;
		try
		{
			rowNum = insertContact(contactEntity.getUserID() ,contactEntity.getContactName() ,contactEntity.getsMS() ,contactEntity.getPhone() ,contactEntity.getPhoneNum() ,contactEntity.getmSMContent());
		}catch(Exception ex){
		   Log.v("InsertContact(ContactEntity contactEntity)",ex.getMessage());
		   return -1;
		}
		return rowNum;
	}

	public void insertContact(List<ContactEntity> listContactEntity)
	{
		sqldatabase.beginTransaction();  //开始事物
		try
		{
			for(int i = 0 ;i < listContactEntity.size() ;i++)
			{
				ContentValues cv=new ContentValues();
				cv.put("UserID",listContactEntity.get(i).getUserID());
				cv.put("ContactName",listContactEntity.get(i).getContactName());
				cv.put("SMS",listContactEntity.get(i).getsMS());
				cv.put("Phone",listContactEntity.get(i).getPhone());
				cv.put("PhoneNum",listContactEntity.get(i).getPhoneNum());
				cv.put("MSMContent",listContactEntity.get(i).getmSMContent());
				
				
				sqldatabase.insert("tb_Contact",null,cv);
			}
			//提交事物
			sqldatabase.setTransactionSuccessful();
		}finally{
		    //如果上面SQL语句发生了异常，没有执行db.setTransactionSuccessful()方法，db.endTransaction()即回滚
			sqldatabase.endTransaction();
		}
		
	}

	public int updateContact(int contactID ,int userID ,String contactName ,int sMS ,int phone ,String phoneNum ,String mSMContent)
	{
		int rowNum = -1;
		try
		{
			ContentValues cv=new ContentValues();
			cv.put("UserID",userID);
			cv.put("ContactName",contactName);
			cv.put("SMS",sMS);
			cv.put("Phone",phone);
			cv.put("PhoneNum",phoneNum);
			cv.put("MSMContent",mSMContent);
			
			rowNum = sqldatabase.update("tb_Contact", cv, "ContactID=?", new String[]{""+contactID+""});
		}catch(Exception ex){
			Log.v("UpdateContact(int contactID ,int userID ,String contactName ,int sMS ,int phone ,String phoneNum ,String mSMContent)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	

	public int updateContact(int contactID , ContactEntity contactEntity)
	{
		int rowNum = -1;
		try
		{
			rowNum = updateContact(contactID ,contactEntity.getUserID() ,contactEntity.getContactName() ,contactEntity.getsMS() ,contactEntity.getPhone() ,contactEntity.getPhoneNum() ,contactEntity.getmSMContent());
		}catch(Exception ex){
			Log.v("UpdateContact(int contactID , ContactEntity contactEntity)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	
	public int deleteContactByContactID(int contactId)
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_Contact", "ContactID=?", new String[]{""+contactId+""});
		}catch(Exception ex){
			Log.v("DeleteContactByContactID(int contactId)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	

	public int deleteContactByUserID(int userId)
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_Contact", "UserID=?", new String[]{""+userId+""});
		}catch(Exception ex){
			Log.v("deleteContactByUserID(int userId)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}

	public int deleteAllContact()
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_Contact",null,null);
		}catch(Exception ex){
			Log.v("DeleteAllContact()", ex.getMessage());
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
