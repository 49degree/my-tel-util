package com.xys.ecg.sqlite;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xys.ecg.bean.DoctorAdviceEntity;

public class DoctorAdviceDB {
	
	private DBHelper dbHelper=null;
	private SQLiteDatabase sqldatabase=null;
	
	public DoctorAdviceDB(Context context)
	{
		try
		{
		dbHelper=new DBHelper(context,CommDB.dataBaseName);
		sqldatabase=dbHelper.getWritableDatabase();
		}catch(Exception ex)
		{
			
		}
	}
	
	
	//通过  建议 Id 获取 Cursor
	public Cursor selectDoctorAdviceByAdviceId(int adviceId)
	{
		Cursor cursor=sqldatabase.query("tb_doctorAdvice",null, "AdviceID=?",new String[]{""+adviceId+""}, null, null, null);
		return cursor;
	}
	
	//通过  用户 Id 获取Cursor
	public Cursor selectDoctorAdviceByUserID(int userId)
	{
		Cursor cursor=sqldatabase.query("tb_doctorAdvice",null, "UserID=?",new String[]{""+userId+""}, null, null, null);
		return cursor;
	}
	
	//通过是否读标记  获取Cursor
	public Cursor selectDoctorAdviceByReadFlag(int readFlag)
	{
		Cursor cursor=sqldatabase.query("tb_doctorAdvice",null, "ReadFlag=?",new String[]{""+readFlag}, null, null, null);
		return cursor;
	}
	
	//获取所有医嘱表信息
	public Cursor getAllDoctorAdvice()
	{
		Cursor cursor=sqldatabase.query("tb_doctorAdvice",null,null,null, null, null, null);
		return cursor;
	}
	
	//插入一条医嘱信息
	public long insertDoctorAdvice(int userID ,String publishTime ,String arriveTime ,String doctorName ,String content ,int readFlag)
	{
		long rowNum = -1;
		try
		{
			ContentValues cv=new ContentValues();
			cv.put("UserID",userID);
			cv.put("PublishTime",publishTime);
			cv.put("ArriveTime",arriveTime);
			cv.put("DoctorName",doctorName);
			cv.put("Content",content);
			cv.put("ReadFlag",readFlag);
			
			rowNum = sqldatabase.insert("tb_doctorAdvice",null,cv);
		}catch(Exception ex)
		{
		  Log.v("InsertDoctorAdvice(int adviceID ,int userID ,String publishTime ,String arriveTime ,String doctorName ,String content ,int readFlag)", ex.getMessage());
		  return -1;
		}
		return rowNum;
	}
	

	public long insertDoctorAdvice(DoctorAdviceEntity doctorAdviceEntity)
	{
		long rowNum = -1;
		try
		{
			rowNum = insertDoctorAdvice(doctorAdviceEntity.getUserID(),doctorAdviceEntity.getPublishTime() ,doctorAdviceEntity.getArriveTime() ,doctorAdviceEntity.getDoctorName() ,doctorAdviceEntity.getContent() ,doctorAdviceEntity.getReadFlag());
		}catch(Exception ex){
		   Log.v("InsertDoctorAdvice(DoctorAdviceEntity doctorAdviceEntity)",ex.getMessage());
		   return -1;
		}
		return rowNum;
	}
	
	
	public void insertDoctorAdvice(List<DoctorAdviceEntity> listDoctorAdviceEntity)
	{
		sqldatabase.beginTransaction();  //开始事物
		try
		{
			for(int i = 0 ;i < listDoctorAdviceEntity.size() ;i++)
			{
				ContentValues cv=new ContentValues();
				cv.put("UserID",listDoctorAdviceEntity.get(i).getUserID());
				cv.put("PublishTime", listDoctorAdviceEntity.get(i).getPublishTime());
				cv.put("ArriveTime", listDoctorAdviceEntity.get(i).getArriveTime());
				cv.put("DoctorName", listDoctorAdviceEntity.get(i).getDoctorName());
				cv.put("Content", listDoctorAdviceEntity.get(i).getContent());
				cv.put("ReadFlag", listDoctorAdviceEntity.get(i).getReadFlag());
				
				sqldatabase.insert("tb_doctorAdvice",null,cv);
			}
			//提交事物
			sqldatabase.setTransactionSuccessful();
		}finally{
		    //如果上面SQL语句发生了异常，没有执行db.setTransactionSuccessful()方法，db.endTransaction()即回滚
			sqldatabase.endTransaction();
		}
		
	}
	

	public void updateDoctorAdviceByFlag(int adviceID,int ReadFlag)
	{
		try
		{
			sqldatabase.execSQL("update tb_doctorAdvice set ReadFlag=" + ReadFlag + " where AdviceID="+adviceID);
		}catch(Exception ex){
			
		}
	}
	
	public int updateDoctorAdvice(int adviceID ,int userID ,String publishTime ,String arriveTime ,String doctorName ,String content ,int readFlag)
	{
		int rowNum = -1;
		try
		{
			ContentValues cv=new ContentValues();
			cv.put("AdviceID",adviceID);
			cv.put("UserID",userID);
			cv.put("PublishTime",publishTime);
			cv.put("ArriveTime",arriveTime);
			cv.put("DoctorName",doctorName);
			cv.put("Content",content);
			cv.put("ReadFlag",readFlag);
			
			rowNum = sqldatabase.update("tb_doctorAdvice", cv, "AdviceID=?", new String[]{""+adviceID+""});
		}catch(Exception ex){
			Log.v("UpdateDoctorAdvice(int adviceID ,int userID ,String publishTime ,String arriveTime ,String doctorName ,String content ,int readFlag)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	
	
	public int updateDoctorAdvice(int adviceID , DoctorAdviceEntity doctorAdviceEntity)
	{
		int rowNum = -1;
		try
		{
			rowNum = updateDoctorAdvice(adviceID ,doctorAdviceEntity.getUserID() ,doctorAdviceEntity.getPublishTime() ,doctorAdviceEntity.getArriveTime() ,doctorAdviceEntity.getDoctorName() ,doctorAdviceEntity.getContent() ,doctorAdviceEntity.getReadFlag());
		}catch(Exception ex){
			Log.v("UpdateDoctorAdvice(DoctorAdviceEntity doctorAdviceEntity)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	

	public int deleteDoctorAdviceByAdviceId(int adviceId)
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_doctorAdvice", "AdviceID=?", new String[]{""+adviceId+""});
		}catch(Exception ex){
			Log.v("DeleteDoctorAdviceByAdviceId(int adviceId)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	
	
	public int deleteDoctorAdviceByUserID(int userId)
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_doctorAdvice", "UserID=?", new String[]{userId+""});
		}catch(Exception ex){
			Log.v("DeleteDoctorAdviceByUserID(int userId)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	
	public int deleteDoctorAdviceByContent(String content)
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_doctorAdvice", "Content=?", new String[]{content+""});
		}catch(Exception ex){
			Log.v(" deleteDoctorAdviceByContent(String content)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}
	
	public int deleteAllDoctorAdvice()
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_doctorAdvice",null,null);
		}catch(Exception ex){
			Log.v("DeleteAllDoctorAdvice()", ex.getMessage());
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
