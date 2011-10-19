package com.szxys.mhub.subsystem.mets.db;

import java.util.ArrayList;

import com.szxys.mhub.subsystem.mets.bean.Questionnairegrade;

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
public class QuestionnaireGrade {

private static final String TableName=MetsData.Tables.QuestionnaireGrade;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
		return cursor;
	}
	public static ArrayList<Questionnairegrade> getGradesByTopicId(Context context,String typeId,String topicId) {
		ArrayList<Questionnairegrade> gradeList=new ArrayList<Questionnairegrade>();
		Questionnairegrade objGrade=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id", "c_Q_TypeId", "c_Q_TopicId", "c_GradeId", "c_Describe", "c_Value"};
		String selection="c_Q_TypeId=? and c_Q_TopicId=?";
		String[] selectionArgs={typeId,topicId};
		String orderBy=" c_GradeId Asc";
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, orderBy);
		if (cursor!=null && cursor.getCount()>0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				objGrade=new Questionnairegrade();
				objGrade.setC_Id(cursor.getInt(0));
				objGrade.setC_Q_TypeId(cursor.getInt(1));
				objGrade.setC_Q_TopicId(cursor.getInt(2));
				objGrade.setC_GradeId(cursor.getInt(3));
				objGrade.setC_Describe(cursor.getString(4));
				objGrade.setC_Value(cursor.getString(5));
				gradeList.add(objGrade);
			}
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return gradeList;
	}
	public static long Insert(Context context,Questionnairegrade objQuestionnaireGrade) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objQuestionnaireGrade));		
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
	private static ContentValues getContentValues(Questionnairegrade objQuestionnaireGrade) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_Q_TypeId", objQuestionnaireGrade.getC_Q_TypeId());
		contentValues.put("c_Q_TopicId", objQuestionnaireGrade.getC_Q_TopicId());
		contentValues.put("c_GradeId", objQuestionnaireGrade.getC_GradeId());
		contentValues.put("c_Describe", objQuestionnaireGrade.getC_Describe());
		contentValues.put("c_Value", objQuestionnaireGrade.getC_Value());
		return contentValues;
	}
}