package com.szxys.mhub.subsystem.mets.db;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import com.szxys.mhub.subsystem.mets.bean.Healthtips;

public class HealthTips {
	
	private static final String TableName=MetsData.Tables.HealthTips;
	
//	public Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
//	MetsDbHelper dbHelper=new MetsDbHelper(context);
//	Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
//	return cursor;
//}
	public static long Insert(Context context, Healthtips objHealthtips) {
		long result=-2;
		result=new MetsDbHelper(context).Add(TableName, getContentValues(objHealthtips));				
		Log.d(TableName,"INSERT into["+TableName+"]@"+result);
		return result;
	}
	
	public static int Update(Context context,ContentValues values,String where,String[] whereArgs) {
		int result=new MetsDbHelper(context).Update(TableName, values, where, whereArgs);
		Log.d(TableName,"MODIFY success");
		return result;
	}
	public static int Delete(Context context,String where,String[] whereArgs) {
		int result=new MetsDbHelper(context).Delete(TableName, where, whereArgs);
		Log.d(TableName,"DELETE success");
		return result;
	}
	public static long getDataCount(Context context,String where,String[] whereArgs,String orderBy ) {
		return new MetsDbHelper(context).getTableCount(TableName, where, whereArgs, orderBy);
	}
	
	private static ContentValues getContentValues(Healthtips objHealthtips) {
		ContentValues contentValues=new ContentValues();
		//contentValues.put("c_Id", objInAndOut.c_Units);
		contentValues.put("c_WebUniqueId", objHealthtips.getC_WebUniqueId());
		contentValues.put("c_Datetime", objHealthtips.getC_Datetime());
		contentValues.put("c_Sender", objHealthtips.getC_Sender());
		contentValues.put("c_Content", objHealthtips.getC_Content());
		return contentValues;
	}
}
