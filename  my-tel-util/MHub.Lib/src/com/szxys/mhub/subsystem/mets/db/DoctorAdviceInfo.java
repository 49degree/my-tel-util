package com.szxys.mhub.subsystem.mets.db;

import com.szxys.mhub.subsystem.mets.bean.Doctoradvice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DoctorAdviceInfo {

private static final String TableName=MetsData.Tables.DoctorAdvice;
	
//	public Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
//		MetsDbHelper dbHelper=new MetsDbHelper(context);
//		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
//		return cursor;
//	}
	public static long Insert(Context context,Doctoradvice objDoctorAdvice) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objDoctorAdvice));		
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
	public static int getAllUnreadAdvice(Context context) {
		int count=0;
		String[] columns={"c_InfoId","c_RecvDt","c_SendDt"};
		String selection="c_IsRead=?";
		String[] selectionArgs={"0"};
		String orderBy="c_Id desc";
		Cursor cursor=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		try {
			cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, orderBy);
			if (cursor!=null && cursor.getCount()>0) {
				count=cursor.getCount();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		}		
		return count;
	}
	private static ContentValues getContentValues(Doctoradvice objDoctorAdvice) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_InfoId", objDoctorAdvice.getC_InfoId());
		contentValues.put("c_Content", objDoctorAdvice.getC_Content());
		contentValues.put("c_RecvDt", objDoctorAdvice.getC_RecvDt());
		contentValues.put("c_SendDt", objDoctorAdvice.getC_SendDt());
		contentValues.put("c_DoctorName", objDoctorAdvice.getC_DoctorName());
		contentValues.put("c_IsRead", objDoctorAdvice.getC_IsRead());
		contentValues.put("c_IsReply", objDoctorAdvice.getC_IsReply());
		return contentValues;
	}
}
