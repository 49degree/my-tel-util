package com.szxys.mhub.subsystem.mets.db;

import com.szxys.mhub.subsystem.mets.bean.UFRrecord;

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
public class UFR_Record {

private static final String TableName=MetsData.Tables.UrineFlowRecord;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
		return cursor;
	}
	/**
	 * 根据UfrId取尿流数组数据 c_OrgData c_FinalData c_QuantityData c_RateData
	 * @param context
	 * @param ufrId
	 * @return
	 */
	public static UFRrecord getUFRbyUfrId(Context context,String ufrId) {
		UFRrecord objUFR_Record=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_UrineId","c_OrgData","c_FinalData","c_QuantityData","c_RateData"};
		String selection="c_UrineId=?";
		String[] selectionArgs={ufrId};
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			objUFR_Record=new UFRrecord();
			cursor.moveToFirst();
			objUFR_Record.setC_Id(cursor.getInt(0));
			objUFR_Record.setC_UrineId(cursor.getInt(1));
			objUFR_Record.setC_OrgData(cursor.getString(2));
			objUFR_Record.setC_FinalData(cursor.getString(3));
			objUFR_Record.setC_QuantityData(cursor.getString(4));
			objUFR_Record.setC_RateData(cursor.getString(5));
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return objUFR_Record;
	}
	public static long Insert(Context context,UFRrecord objUFR_Record) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objUFR_Record));		
		Log.d(TableName,"INSERT success");
		return result;
	}
	public static int Update(Context context,ContentValues values,String where,String[] whereArgs) {
		int result=new MetsDbHelper(context).Update(TableName, values, where, whereArgs);
		Log.d(TableName,"MODIFY success");
		return result;
	}
	public static int Delete(Context context,String where,String[] whereArgs) {
		int result=new MetsDbHelper(context).Delete(TableName,where, whereArgs);
		Log.d(TableName,"DELETE success");
		return result;
	}
	public static String getOrgDataByUrineId(Context context,String urineId) {
		String orgDataString="";
		String[] columns={"c_OrgData"};
		String selection="c_UrineId=?";
		String[] selectionArgs={urineId};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			cursor.moveToFirst();
			orgDataString=cursor.getString(0);
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return orgDataString;
	}
	/**
	 * 获取尿流率曲线数组 (由对象提供)
	 * @param context
	 * @param String ufrid
	 * @return
	 */
	public static UFRrecord getUFRbyId(Context context,String ufrid) {
		UFRrecord objUFR=null;
		String[] columns={"c_Id","c_UrineId","c_OrgData","c_FinalData","c_QuantityData","c_RateData"};
		String selection="c_UrineId=?";
		String[] selectionArgs={ufrid.trim()};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			objUFR=new UFRrecord();
			cursor.moveToFirst();
			objUFR.setC_Id(cursor.getInt(cursor.getInt(0)));
			objUFR.setC_UrineId(cursor.getInt(cursor.getInt(1)));
			objUFR.setC_OrgData(cursor.getString(2));
			objUFR.setC_FinalData(cursor.getString(3));
			objUFR.setC_QuantityData(cursor.getString(4));
			objUFR.setC_RateData(cursor.getString(5));
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return objUFR;
	}
	public static long getDataCount(Context context,String where,String[] whereArgs,String orderBy ) {
		return new MetsDbHelper(context).getTableCount(TableName, where, whereArgs, orderBy);
	}
	private static ContentValues getContentValues(UFRrecord objUFR_Record) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_UrineId", objUFR_Record.getC_UrineId());
		contentValues.put("c_OrgData", objUFR_Record.getC_OrgData());
		contentValues.put("c_FinalData", objUFR_Record.getC_FinalData());
		contentValues.put("c_QuantityData", objUFR_Record.getC_QuantityData());
		contentValues.put("c_RateData", objUFR_Record.getC_RateData());
		return contentValues;
	}
}
