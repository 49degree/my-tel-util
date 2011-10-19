package com.szxys.mhub.subsystem.mets.db;

import java.util.ArrayList;

import com.szxys.mhub.subsystem.mets.bean.Patientgrade;

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
public class PatientGrade {

private static final String TableName=MetsData.Tables.PatientGrade;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		//
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
		return cursor;
	}
	public static boolean getExistRecord(Context context,String surveyID,String topicID) {
		boolean isExist=false;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_P_SurveyId","c_Q_TopicId","c_Q_GradeId","c_Value"};
		String selection="c_P_SurveyId=? and c_Q_TopicId=?";
		String[] selectionArgs={surveyID,topicID};
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			isExist=true;
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return isExist;
	}
	public static ArrayList<Patientgrade> getGradesBySurveyId(Context context,String surveyID) {
		ArrayList<Patientgrade> gradeList=new ArrayList<Patientgrade>();
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_P_SurveyId","c_Q_TopicId","c_Q_GradeId","c_Value"};
		String selection="c_P_SurveyId=?";
		String[] selectionArgs={surveyID};
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			Patientgrade grade=null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				grade=new Patientgrade();
				grade.setC_Id(cursor.getInt(0));
				grade.setC_P_SurveyId(cursor.getInt(1));
				grade.setC_Q_TopicId(cursor.getInt(2));
				grade.setC_Q_GradeId(cursor.getInt(3));
				grade.setC_Value(cursor.getString(4));
				gradeList.add(grade);
			}			
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return gradeList;
	}
	public static int getSelectedItemID(Context context,String surveyID,String topicID) {
		int selectedID=0;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_P_SurveyId","c_Q_TopicId","c_Q_GradeId","c_Value"};
		String selection="c_P_SurveyId=? and c_Q_TopicId=?";
		String[] selectionArgs={surveyID,topicID};
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			cursor.moveToFirst();
			selectedID=cursor.getInt(3);
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return selectedID;
	}
	public static long Insert(Context context,Patientgrade objPatientGrade) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objPatientGrade));		
		Log.d(TableName,"INSERT success");
		return result;
	}
	public static long saveGrade(Context context,Patientgrade objPatientGrade) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objPatientGrade));		
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
	public static long getDataCount(Context context,String where,String[] whereArgs,String orderBy ) {
		return new MetsDbHelper(context).getTableCount(TableName, where, whereArgs, orderBy);
	}
	private static ContentValues getContentValues(Patientgrade objPatientGrade) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_P_SurveyId", objPatientGrade.getC_P_SurveyId());
		contentValues.put("c_Q_TopicId", objPatientGrade.getC_Q_TopicId());
		contentValues.put("c_Q_GradeId", objPatientGrade.getC_Q_GradeId());
		contentValues.put("c_Value", objPatientGrade.getC_Value());
		return contentValues;
	}
}
