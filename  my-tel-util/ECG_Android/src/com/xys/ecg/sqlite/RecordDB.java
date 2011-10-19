package com.xys.ecg.sqlite;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xys.ecg.bean.RecordEntity;

public class RecordDB {
	private DBHelper dbHelper=null;
	private SQLiteDatabase sqldatabase=null;
	
	public RecordDB(Context context)
	{
		dbHelper=new DBHelper(context,CommDB.dataBaseName);
		sqldatabase=dbHelper.getWritableDatabase();
	}
	

	public Cursor selectRecordByWhere(long starttime ,boolean isAllStartTime ,boolean islead ,boolean istouch)
	{
		System.out.println(24*60*60*1000);
		Cursor cursor = null;
		int num = 0;
		String columnsValue = "";
		String columns = "";
		if(isAllStartTime)   //如果查询该时间的所有信息
		{
			columns += "StartTime>=? and StartTime<?";
			columnsValue += starttime ;
			columnsValue +=",";
			columnsValue +=(starttime + 24*60*60*1000);
			if(islead && istouch)
			{
				columns += " and Mode in(?,?)";
				columnsValue +=","+"7"+","+"8";
			}
			else if(islead && !istouch)
			{
				columns += " and Mode=? ";
				columnsValue +=","+"7";
			}
			else if(!islead && istouch)
			{
				columns += " and Mode=? ";
				columnsValue +=","+"8";
			}
			else if(!islead&&!istouch){
				return null;
				
			}
		}
		else
		{
			/*columns += "StartTime=? ";
			columnsValue += starttime ;*/
			columns += "StartTime>=? and StartTime<?";
			columnsValue += starttime ;
			columnsValue +=",";
			columnsValue +=(starttime + 24*60*60*1000);
			if(islead && istouch)
			{
				columns += " and Mode in(?,?)";
				columnsValue +=","+"7"+","+"8";
			}
			else if(islead && !istouch)
			{
				columns += " and Mode=? ";
				columnsValue +=","+"7";
			}
			else if(!islead && istouch)
			{
				columns += " and Mode=? ";
				columnsValue +=","+"8";
			}else if(!islead&&!istouch){
				String[] mode = new String[1];
				mode [0] = "1";
				return sqldatabase.query("tb_record",null, "Mode=", mode, null, null, null);
			}
			
		}
		if(columnsValue.equals(""))
		{
			columnsValue = null;
		}
		if(columns.equals(""))
		{
			columns = null;
		}
		/*String[] cols={"*"};
		String where="StartTime=? and Mode=? or Mode=?";
		String[] whereArgs={"61262265600000","7","8"};*/
		String[] whereArgs = columnsValue.split(",");
		for(int i=0;i<whereArgs.length;i++)
		{
		  System.out.println(whereArgs[i]);	
		}
		System.out.println(columns);
		System.out.println(whereArgs.toString());
		try
		{
			/*cursor=sqldatabase.query("tb_record",cols, where,whereArgs, null, null, null);
			if(cursor!=null && cursor.getCount()>0){
				int TEST=cursor.getCount();
			}*/
			
			 cursor=sqldatabase.query("tb_record",null, columns,whereArgs, null, null, null);
		}catch(Exception ex)
		{
			System.out.println("error");
			System.out.println(columns);
			System.out.println(columnsValue);
		}
		return cursor;
    
		
	}
	
	
	//通过 记录 Id 获取 Cursor
	public Cursor selectRecordByRecordID(int recordId)
	{
		Cursor cursor=sqldatabase.query("tb_record",null, "RecordID=?",new String[]{""+recordId+""}, null, null, null);
		return cursor;
	}
	
	
	
	//通过用户Id 获取Cursor
	public Cursor selectRecordByUserID(int userId)
	{
		Cursor cursor=sqldatabase.query("tb_record",null, "UserID=?",new String[]{""+userId+""}, null, null, null);
		return cursor;
	}
	
	//通过路径获取Cursor
	public Cursor selectRecordByFilePath(String filePath){
		Cursor cursor = sqldatabase.query("tb_record", null, "FilePath=?", new String[]{filePath}, null
				, null, null);
		return cursor;
	}
	
	//获取所有记录表信息
	public Cursor getAllRecord()
	{
		Cursor cursor=sqldatabase.query("tb_record",null,null,null, null, null, null);
		return cursor;
	}
	
	//插入一条记录信息
	public long insertRecord(int userID ,long startTime ,String filePath ,int mode ,int state ,int uploaded)
	{
		long rowNum = -1;
		try
		{
			ContentValues cv=new ContentValues();
			cv.put("UserID",userID);
			cv.put("StartTime",startTime);
			cv.put("FilePath",filePath);
			cv.put("Mode",mode);
			cv.put("State",state);
			cv.put("Uploaded", uploaded);
			
			rowNum = sqldatabase.insert("tb_record",null,cv);
		}catch(Exception ex)
		{
		  Log.v("InsertRecord(int userID ,long startTime ,String filePath ,int mode ,int state ,int uploaded)", ex.getMessage());
		  return -1;
		}
		return rowNum;
	}
	
	public long insertRecord(RecordEntity recordEntity)
	{
		long rowNum = -1;
		try
		{
			rowNum = insertRecord(recordEntity.getUserID() ,recordEntity.getStartTime() ,recordEntity.getFilePath() ,recordEntity.getMode() ,recordEntity.getState() ,recordEntity.getUploaded());
		}catch(Exception ex){
		   Log.v("InsertRecord(RecordEntity recordEntity)",ex.getMessage());
		   return -1;
		}
		return rowNum;
	}
	
	public void insertRecord(List<RecordEntity> listRecordEntity)
	{
		sqldatabase.beginTransaction();  //开始事物
		try
		{
			for(int i = 0 ;i < listRecordEntity.size() ;i++)
			{
				ContentValues cv=new ContentValues();
				cv.put("UserID",listRecordEntity.get(i).getUserID());
				cv.put("StartTime", listRecordEntity.get(i).getStartTime());
				cv.put("FilePath", listRecordEntity.get(i).getFilePath());
				cv.put("Mode", listRecordEntity.get(i).getMode());
				cv.put("State", listRecordEntity.get(i).getState());
				cv.put("Uploaded", listRecordEntity.get(i).getUploaded());
				
				sqldatabase.insert("tb_record",null,cv);
			}
			//提交事物
			sqldatabase.setTransactionSuccessful();
		}finally{
		    //如果上面SQL语句发生了异常，没有执行db.setTransactionSuccessful()方法，db.endTransaction()即回滚
			sqldatabase.endTransaction();
		}
		
	}

	public int updateRecord(int recordID ,int userID ,long startTime ,String filePath ,int mode ,int state ,int uploaded)
	{
		int rowNum = -1;
		try
		{
			ContentValues cv=new ContentValues();
			cv.put("RecordID",recordID);
			cv.put("UserID",userID);
			cv.put("StartTime",startTime);
			cv.put("FilePath",filePath);
			cv.put("Mode",mode);
			cv.put("State",state);
			cv.put("Uploaded", uploaded);
			
			rowNum = sqldatabase.update("tb_record", cv, "RecordID=?", new String[]{""+recordID+""});
		}catch(Exception ex){
			Log.v("UpdateRecord(int recordID ,int userID ,long startTime ,String filePath ,int mode ,int state,int uploaded)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}

	public int updateRecord(int recordID , RecordEntity recordEntity)
	{
		int rowNum = -1;
		try
		{
			rowNum = updateRecord(recordID ,recordEntity.getUserID() ,recordEntity.getStartTime() ,recordEntity.getFilePath() ,recordEntity.getMode() ,recordEntity.getState() ,recordEntity.getUploaded());
		}catch(Exception ex){
			Log.v("UpdateRecord(int recordID , RecordEntity recordEntity)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}

	public int deleteRecordByRecordID(int recordId)
	{
		int rowNum = -1;
		try
		{
			System.out.println("delele:::::" + recordId);
			rowNum = sqldatabase.delete("tb_record", "RecordID=?", new String[]{(recordId+"").trim()});
			
		}catch(Exception ex){
			Log.v("DeleteRecordByRecordID(int recordId)", ex.getMessage());
			return -1;
		}
		return rowNum;
		
	}
	
	public int deleteRecordByUserID(int userId)
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_record", "UserID=?", new String[]{""+userId+""});
		}catch(Exception ex){
			Log.v("DeleteRecordByUserID(int userId)", ex.getMessage());
			return -1;
		}
		return rowNum;
	}

	//根据路径删除记录
	public int deleteRecordByFilePath(String filePath){
		int rowNum = -1;
		try{
			rowNum = sqldatabase.delete("tb_record", "FilePath=?", new String[]{filePath});
		}catch(Exception e){
			Log.v("deleteRecordByFilePath", e.getMessage());
			return -1;
		}
		return rowNum;
	}
	public int deleteAllRecord()
	{
		int rowNum = -1;
		try
		{
			rowNum = sqldatabase.delete("tb_record",null,null);
		}catch(Exception ex){
			Log.v("DeleteAllRecord()", ex.getMessage());
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
