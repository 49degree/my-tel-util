package com.szxys.mhub.subsystem.mets.db;

import java.util.ArrayList;

import com.szxys.mhub.subsystem.mets.bean.Urinerecord;

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
public class UrineRecord {
	//尿流率统计项  tb_UrineRecord
	private static final String TableName=MetsData.Tables.UrineRateStatistics;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}
	public static long Insert(Context context,Urinerecord objUrineRecord) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objUrineRecord));		
		Log.d(TableName,"INSERT success");
		return result;
	}
	/**
	 * 获取未上传的UrineRecord
	 * StartTime开始时间; Duration采集时长
	 * VaryVal每秒的尿量, MeanFlow平均尿流率
	 * Q90	90％流量的平均尿流率, PeakFlow最大尿流率
	 * 2SecFlow	2秒时的尿流率, FlowTime	排尿时间
	 * StreamTime尿流时间,  T90	90％流量时的时间
	 * TimeToPeak达到最大尿流率的时间
	 * VoidVolume总尿量, AmountUnit总尿量单位
	 * Proportion尿液比重,  BeginPos间歇开始位置, EndPos间歇结束位置
	 * @param context
	 * @return ArrayList<Urinerecord>
	 */
	public static ArrayList<Urinerecord> getUploadUrinerecords(Context context) {
		ArrayList<Urinerecord> list=new ArrayList<Urinerecord>();
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_Units","c_DateTime","c_Duration","c_MeanFlow","c_Q90","c_PeakFlow","c_2SecFlow",
				"c_VoidingTime","c_FlowTime","c_T90","c_TimeToPeak","c_VoidVolume","c_StartPos","c_EndPos","c_IsUpload"};
		String selection="c_IsUpload=?";
		String[] selectionArgs={"0"};
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			Urinerecord objUrinerecord=null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				objUrinerecord=new Urinerecord();
				objUrinerecord.setC_Id(cursor.getInt(0));
				objUrinerecord.setC_Units(cursor.getInt(1));
				objUrinerecord.setC_DateTime(cursor.getString(2));
				objUrinerecord.setC_Duration(cursor.getInt(3));
				objUrinerecord.setC_MeanFlow(cursor.getFloat(4));
				objUrinerecord.setC_Q90(cursor.getFloat(5));
				objUrinerecord.setC_PeakFlow(cursor.getFloat(6));
				objUrinerecord.setC_2SecFlow(cursor.getFloat(7));
				objUrinerecord.setC_VoidingTime(cursor.getFloat(8));
				objUrinerecord.setC_FlowTime(cursor.getFloat(9));
				objUrinerecord.setC_T90(cursor.getFloat(10));
				objUrinerecord.setC_TimeToPeak(cursor.getFloat(11));
				objUrinerecord.setC_VoidVolume(cursor.getFloat(12));
				objUrinerecord.setC_StartPos(cursor.getInt(13));
				objUrinerecord.setC_EndPos(cursor.getInt(14));
				objUrinerecord.setC_IsUpload(0);
				list.add(objUrinerecord);
			}
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return list;
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
	/**
	 * 是否存在相同时间的记录
	 * @param context
	 * @param urineDataString="yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static boolean isExistDatetime(Context context,String urineDataString) {
		boolean has=false;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_DateTime"};
		String selection="c_DateTime=?";
		String[] selectionArgs={urineDataString.trim()};
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			has=true;
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return has;
	}
	public static Urinerecord getUrineRecordbyId(Context context,String c_id) {
		Urinerecord objurineRecord=null;
		String[] columns={"c_Id","c_PeakFlow","c_MeanFlow","c_VoidingTime","c_FlowTime","c_TimeToPeak","c_VoidVolume","c_2SecFlow",
				"c_Units","c_DateTime","c_Duration","c_Q90","c_T90","c_StartPos","c_EndPos","c_IsUpload"};
		String selection="c_Id=?";
		String[] selectionArgs={c_id.trim()};
		Cursor cursor=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		try {
			cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
			if (cursor!=null && cursor.getCount()>0) {
				objurineRecord=new Urinerecord();
				cursor.moveToFirst();
				objurineRecord.setC_Id(cursor.getInt(0));
				objurineRecord.setC_PeakFlow(cursor.getFloat(1));
				objurineRecord.setC_MeanFlow(cursor.getFloat(2));
				objurineRecord.setC_VoidingTime(cursor.getFloat(3));
				objurineRecord.setC_FlowTime(cursor.getFloat(4));
				objurineRecord.setC_TimeToPeak(cursor.getFloat(5));
				objurineRecord.setC_VoidVolume(cursor.getFloat(6));
				objurineRecord.setC_2SecFlow(cursor.getFloat(7));
				objurineRecord.setC_Units(cursor.getInt(8));
				objurineRecord.setC_DateTime(cursor.getString(9));
				objurineRecord.setC_Duration(cursor.getInt(10));
				objurineRecord.setC_Q90(cursor.getFloat(11));
				objurineRecord.setC_T90(cursor.getFloat(12));
				objurineRecord.setC_StartPos(cursor.getInt(13));
				objurineRecord.setC_EndPos(cursor.getInt(14));
				objurineRecord.setC_IsUpload(cursor.getInt(15));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		}		
		return objurineRecord;
	}
	private static ContentValues getContentValues(Urinerecord objUrineRecord) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_Units", objUrineRecord.getC_Units());
		contentValues.put("c_DateTime", objUrineRecord.getC_DateTime());
		contentValues.put("c_Duration", objUrineRecord.getC_Duration());
		contentValues.put("c_MeanFlow", objUrineRecord.getC_MeanFlow());
		contentValues.put("c_Q90", objUrineRecord.getC_Q90());
		contentValues.put("c_PeakFlow", objUrineRecord.getC_PeakFlow());
		contentValues.put("c_2SecFlow", objUrineRecord.getC_2SecFlow());
		contentValues.put("c_VoidingTime", objUrineRecord.getC_VoidingTime());
		contentValues.put("c_FlowTime", objUrineRecord.getC_FlowTime());
		contentValues.put("c_T90", objUrineRecord.getC_T90());
		contentValues.put("c_TimeToPeak", objUrineRecord.getC_TimeToPeak());
		contentValues.put("c_VoidVolume", objUrineRecord.getC_VoidVolume());
		contentValues.put("c_StartPos", objUrineRecord.getC_StartPos());
		contentValues.put("c_EndPos", objUrineRecord.getC_EndPos());
		contentValues.put("c_IsUpload", objUrineRecord.getC_IsUpload());
		return contentValues;
	}
}
