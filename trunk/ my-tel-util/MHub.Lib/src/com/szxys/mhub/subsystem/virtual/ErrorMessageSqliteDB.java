package com.szxys.mhub.subsystem.virtual;

import java.util.ArrayList;
import java.util.List;

import com.szxys.mhub.base.manager.MHubDBHelper;

import android.database.Cursor;
import android.util.Log;

/**
 * 异常信息DB操作
 * @author 张丹
 *
 */
public class ErrorMessageSqliteDB {
	private MHubDBHelper dbHelper;
	public ErrorMessageSqliteDB()
	{
		dbHelper=new MHubDBHelper();
	}
	/**
	 * 保存异常信息
	 * @param error
	 */
	public void  saveErrorInfo(ErrorMessageEntity error)
	{
		String tempSql="insert into errorInfo(appId,alarmType,alarmLevelId,alarmDescription,AlarmTime)values("
			          +String.valueOf(error.get_appId())+","
			          +"'"+error.get_alarmType()+"'"+","
			          +String.valueOf(error.get_alarmLevelId())+","
			          +"'"+error.get_alarmDescription()+"'"+","
			          +"'" +error.get_alarmTime()+"')";
		this.dbHelper.open(true);
		this.dbHelper.execSQL(tempSql);
	}
	/**
	 * 查询所有异常信息
	 * @return
	 */
	public List<ErrorMessageEntity> findErrorMessage()
	{
		List<ErrorMessageEntity> messageList=new ArrayList<ErrorMessageEntity>();
		String tempSql="select * from errorInfo order by AlarmTime desc";
		dbHelper.open(false);
		Cursor cursor =dbHelper.query(tempSql);
		if(cursor==null)
			return messageList;
		 while(!cursor.isAfterLast())
		 {
			 ErrorMessageEntity message=new ErrorMessageEntity();
			 if(cursor.isBeforeFirst())
			 {
				 cursor.moveToNext();
			 }
			 message.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
			 message.set_appId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("appId"))));
			 message.set_alarmLevelId(cursor.getColumnIndex("alarmLevelId"));
			 int indexAlarmType=cursor.getColumnIndex("alarmType");
			 message.set_alarmType(cursor.getString(indexAlarmType));
			 int index=cursor.getColumnIndex("alarmDescription");
			 message.set_alarmDescription(cursor.getString(index));
			 int indexAlarmTime=cursor.getColumnIndex("AlarmTime");
			 message.set_alarmTime(cursor.getString(indexAlarmTime));
			 messageList.add(message);
			 cursor.moveToNext();
		 }
		return messageList;
	}
	/**
	 * 根据ID删除异常信息
	 * @param id
	 */
	public boolean delErrorMessageByID(ArrayList<Integer> IDList)
	{
		boolean bResult = true;
		dbHelper.open(true);
		String strSQL = "delete from errorInfo where _id in (";
		for(int i =0;i<IDList.size();i++)
		{
			strSQL +=IDList.get(i)+",";
		}
		strSQL = strSQL.substring(0,strSQL.length()-1);
		strSQL +=")";
		try {
			dbHelper.execSQL(strSQL);
			
		} catch (Exception e) {
			bResult = false;
		}
		return bResult;
		
	}
	

	
	  
	  

}
