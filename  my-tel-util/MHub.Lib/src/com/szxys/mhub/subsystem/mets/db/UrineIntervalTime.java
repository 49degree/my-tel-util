package com.szxys.mhub.subsystem.mets.db;

import com.szxys.mhub.subsystem.mets.bean.Urineintervaltime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


/**
 * 数据操作类(执行增、删、改、查)
 * 业务需要的数据均由【刀】com.szxys.mhub.subsystem.mets.dao提供
 * @author Administrator
 *
 */
public class UrineIntervalTime {

private static final String TableName=MetsData.Tables.UrineIntervalTime;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		return cursor;
	}
	public static long Insert(Context context,Urineintervaltime objUrineIntervalTime) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objUrineIntervalTime));		
		Log.d(TableName,"INSERT success");
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
	private static ContentValues getContentValues(Urineintervaltime objUrineIntervalTime) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_UrineId", objUrineIntervalTime.getC_UrineId());
		contentValues.put("c_BeginPos", objUrineIntervalTime.getC_BeginPos());
		contentValues.put("c_EndPos", objUrineIntervalTime.getC_Id());
		return contentValues;
	}
}
