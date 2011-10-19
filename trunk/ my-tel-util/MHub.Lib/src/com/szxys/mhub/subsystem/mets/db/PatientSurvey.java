package com.szxys.mhub.subsystem.mets.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.szxys.mhub.subsystem.mets.bean.Patientsurvey;

import android.R.integer;
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
public class PatientSurvey {

private static final String TableName=MetsData.Tables.PatientSurvey;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		//
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
		return cursor;
	}
	/**
	 * 获得可以上传的答卷
	 * @param context
	 * @return ArrayList<Patientsurvey>
	 */
	public static ArrayList<Patientsurvey> getSurveyList(Context context) {
		ArrayList<Patientsurvey> surveyList=new ArrayList<Patientsurvey>();
		Patientsurvey survey=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_S_PatientId","c_Q_TypeId","c_BeginTime","c_EndTime","c_TotalScore","c_IsEdit","c_IsUpload"};
		String selection="c_IsEdit=? and c_IsUpload=?";
		String[] selectionArgs={"0","0"};		
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				survey=new Patientsurvey();
				survey.setC_Id(cursor.getInt(0));
				survey.setC_S_PatientId(cursor.getString(1));
				survey.setC_Q_TypeId(cursor.getInt(2));
				survey.setC_BeginTime(cursor.getString(3));
				survey.setC_EndTime(cursor.getString(4));
				survey.setC_TotalScore(cursor.getFloat(5));
				survey.setC_IsEdit(cursor.getInt(6));
				survey.setC_IsUpload(cursor.getInt(7));
			}
		}
		return surveyList;
	}
	/**
	 * 取问卷信息
	 * @param context
	 * @param String typeId
	 * @return Patientsurvey
	 */
	public static Patientsurvey getSurveyInfoByTypeId(Context context,String typeId) {
		//ArrayList<Patientsurvey> surveyList=new ArrayList<Patientsurvey>();
		Patientsurvey survey=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_S_PatientId","c_Q_TypeId","c_BeginTime","c_EndTime","c_TotalScore","c_IsEdit","c_IsUpload"};
		String selection="c_Q_TypeId=?"; // and c_IsUpload=?
		String[] selectionArgs={typeId};		
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			//for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			cursor.moveToFirst();
				survey=new Patientsurvey();
				survey.setC_Id(cursor.getInt(0));
				survey.setC_S_PatientId(cursor.getString(1));
				survey.setC_Q_TypeId(cursor.getInt(2));
				survey.setC_BeginTime(cursor.getString(3));
				survey.setC_EndTime(cursor.getString(4));
				survey.setC_TotalScore(cursor.getFloat(5));
				survey.setC_IsEdit(cursor.getInt(6));
				survey.setC_IsUpload(cursor.getInt(7));
			//}
		}
		return survey;
	}
	/**
	 * 修改答卷状态为"已上传"(上传完成后设置)
	 * @param String surveyId
	 * @return true(success)  false(failed)
	 */
	public boolean setPatientSurveyIsUploaded(Context context,String surveyId) {
		boolean isOk=false;
		int result=-1;
		ContentValues values=new ContentValues();
		//values.put("c_EndTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		values.put("c_IsUpload", 1);
		String where="c_Id=?";
		String[] whereArgs={surveyId};
		result=Update(context, values, where, whereArgs);
		if (result>0) {
			isOk=true;
		}
		return isOk;
	}
	public static long Insert(Context context,Patientsurvey objPatientSurvey) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objPatientSurvey));		
		Log.d(TableName,"INSERT success");
		return result;
	}
	public static int Update(Context context,ContentValues values,String where,String[] whereArgs) {
		int result=new MetsDbHelper(context).Update(TableName, values, where, whereArgs);
		Log.d(TableName,"MODIFY success");
		return result;
	}
	/**
	 * 编辑状态下锁定问卷
	 * @param context
	 * @param String Q_typeId
	 * @return 锁定成功 true 否则false 
	 */
	public static boolean setSurveyLocked(Context context,String Q_typeId) {
		boolean isLocked=false;
		ContentValues values=new ContentValues();
		values.put("c_IsEdit", 1);
		String where="c_Q_TypeId=?"; // and c_IsUpload=0
		String[] whereArgs={Q_typeId};
		int result=Update(context, values, where, whereArgs);
		if (result>0) {
			isLocked=true;
		}
		return isLocked;
	}
	/**
	 * 解除问卷锁定
	 * @param context
	 * @param String Q_typeId
	 * @return 解除锁定成功 true 否则false 
	 */
	public static boolean unLockSurvey(Context context,String Q_typeId) {
		boolean isLocked=false;
		ContentValues values=new ContentValues();
		values.put("c_IsEdit", 0);
		String where="c_Q_TypeId=?"; // and c_IsUpload=0
		String[] whereArgs={Q_typeId};
		int result=Update(context, values, where, whereArgs);
		if (result>0) {
			isLocked=true;
		}
		return isLocked;
	}
	public static int Delete(Context context,String where,String[] whereArgs) {
		int result=new MetsDbHelper(context).Delete(TableName, where, whereArgs);
		Log.d(TableName,"DELETE success");
		return result;
	}
	public static long getDataCount(Context context,String where,String[] whereArgs,String orderBy ) {
		return new MetsDbHelper(context).getTableCount(TableName, where, whereArgs, orderBy);
	}
	private static ContentValues getContentValues(Patientsurvey objPatientSurvey) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_S_PatientId", objPatientSurvey.getC_S_PatientId());
		contentValues.put("c_Q_TypeId", objPatientSurvey.getC_Q_TypeId());
		contentValues.put("c_BeginTime", objPatientSurvey.getC_BeginTime());
		contentValues.put("c_EndTime", objPatientSurvey.getC_EndTime());
		contentValues.put("c_TotalScore", objPatientSurvey.getC_TotalScore());
		contentValues.put("c_IsUpload", objPatientSurvey.getC_IsUpload());
		return contentValues;
	}
}
