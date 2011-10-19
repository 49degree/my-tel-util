package com.szxys.mhub.subsystem.mets.db;

import java.util.ArrayList;

import com.szxys.mhub.subsystem.mets.bean.Questionnairetopic;

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
public class QuestionnaireTopic {

private static final String TableName=MetsData.Tables.QuestionnaireTopic;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
		return cursor;
	}
	public static ArrayList<Questionnairetopic> getTopicsByTypeId(Context context,String typeId) {
		ArrayList<Questionnairetopic> list=new ArrayList<Questionnairetopic>();
		Questionnairetopic topic=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_Q_TypeId","c_TopicId","c_Title","c_Describe","c_GradeType","c_GradeCalcType"};
		String selection="c_Q_TypeId=?";
		String[] selectionArgs={typeId};
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				topic=new Questionnairetopic();
				topic.setC_Id(cursor.getInt(0));
				topic.setC_Q_TypeId(cursor.getInt(1));
				topic.setC_TopicId(cursor.getInt(2));
				topic.setC_Title(cursor.getString(3));
				topic.setC_Describe(cursor.getString(4));
				topic.setC_GradeType(cursor.getInt(5));
				topic.setC_GradeCalcType(cursor.getInt(6));
			}			
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return list;
	}
	public static ArrayList<Questionnairetopic> getTopicById(Context context,String typeId) {
		ArrayList<Questionnairetopic> topicList=new ArrayList<Questionnairetopic>();
		Questionnairetopic objTopic=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id", "c_Q_TypeId", "c_TopicId", "c_Title", "c_Describe", "c_GradeType", "c_GradeCalcType"};
		String selection="c_Q_TypeId=?";
		String[] selectionArgs={typeId};
		String orderBy=" c_Id Asc";
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, orderBy);
		if (cursor!=null && cursor.getCount()>0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				objTopic=new Questionnairetopic();
				objTopic.setC_Id(cursor.getInt(0));
				objTopic.setC_Q_TypeId(cursor.getInt(1));
				objTopic.setC_TopicId(cursor.getInt(2));
				objTopic.setC_Title(cursor.getString(3));
				objTopic.setC_Describe(cursor.getString(4));
				objTopic.setC_GradeType(cursor.getInt(5));
				objTopic.setC_GradeCalcType(cursor.getInt(6));
				topicList.add(objTopic);
			}
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return topicList;
	}
	public static long Insert(Context context,Questionnairetopic objQuestionnaireTopic) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objQuestionnaireTopic));		
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
	private static ContentValues getContentValues(Questionnairetopic objQuestionnaireTopic) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_Q_TypeId", objQuestionnaireTopic.getC_Q_TypeId());
		contentValues.put("c_TopicId", objQuestionnaireTopic.getC_TopicId());
		contentValues.put("c_Title", objQuestionnaireTopic.getC_Title());
		contentValues.put("c_Describe", objQuestionnaireTopic.getC_Describe());
		contentValues.put("c_GradeType", objQuestionnaireTopic.getC_GradeType());
		contentValues.put("c_GradeCalcType", objQuestionnaireTopic.getC_GradeCalcType());
		return contentValues;
	}
}