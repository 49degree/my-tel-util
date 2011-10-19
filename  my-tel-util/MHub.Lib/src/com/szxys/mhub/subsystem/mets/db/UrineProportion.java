package com.szxys.mhub.subsystem.mets.db;

import com.szxys.mhub.subsystem.mets.bean.Urineproportion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class UrineProportion {
	private static final String TableName=MetsData.Tables.UrineProportion;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
		return cursor;
	}
	public static long Insert(Context context,Urineproportion objUrineProportion) {
		// 查找5分钟内的排尿信息
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objUrineProportion));		
		Log.d(TableName,"INSERT success");
		return result;
	}
	public static String getLatestProportion(Context context,String thisTimeString) {
		String proportionString="";
		String[] columns={"c_Id","c_Proportion"};
		String selection="c_DateTime>=? and c_DateTime<=?";
		Calendar calThistime = Calendar.getInstance(); //基准时间  前后5分钟
		SimpleDateFormat smpf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");		
		//calThistime.setTime(java.sql.Date.valueOf(thisTimeString));
		try {
			calThistime.setTime(smpf.parse(thisTimeString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Calendar calTmp = (Calendar)calThistime.clone();//get the copy of the begin date
		calTmp.add(Calendar.MINUTE,-5);
		Calendar calTnp = (Calendar)calThistime.clone();//get the copy of the begin date
		calTnp.add(Calendar.MINUTE,5);
		
		Calendar calStart = Calendar.getInstance(); 
		calStart.setTime(calTmp.getTime());		
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(calTnp.getTime());
		
		String[] selectionArgs=new String[2];
		selectionArgs[0]=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(calStart.getTime().getTime());
		selectionArgs[1]=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(calEnd.getTime().getTime());
		Cursor cursor=Select(context, columns, selection, selectionArgs, null, null, " c_Id desc");
		
		if (cursor!=null && cursor.getCount()>0) {
			cursor.moveToFirst(); //c_Id desc get first one
			proportionString=cursor.getString(1);
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
		}
		return proportionString;
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
	private static ContentValues getContentValues(Urineproportion objUrineProportion) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_DateTime", objUrineProportion.getC_DateTime());
		contentValues.put("c_Proportion", objUrineProportion.getC_Proportion());
		contentValues.put("c_IsMatch", objUrineProportion.getC_IsMatch());
		return contentValues;
	}
}