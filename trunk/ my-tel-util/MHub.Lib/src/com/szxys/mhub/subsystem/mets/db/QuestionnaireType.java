package com.szxys.mhub.subsystem.mets.db;

import java.util.ArrayList;

import com.szxys.mhub.subsystem.mets.bean.Questionnairetype;

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
public class QuestionnaireType {

private static final String TableName=MetsData.Tables.QuestionnaireType;
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
		return cursor;
	}
	public static ArrayList<Questionnairetype> getTitleList(Context context) {
		ArrayList<Questionnairetype> titles=new ArrayList<Questionnairetype>();
		Questionnairetype title=null;
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_TypeId", "c_Title", "c_Describe", "c_CreateTime", "c_UpdateTime", "c_IsEnable", "c_IsComplete"};
		String selection="c_IsEnable=1";
		String[] selectionArgs=null;
		String orderBy=" c_Id Asc";
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, orderBy);
		if (cursor!=null && cursor.getCount()>0) {
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				title=new Questionnairetype();
				title.setC_Id(cursor.getInt(0));
				title.setC_TypeId(cursor.getInt(1));
				title.setC_Title(cursor.getString(2));
				title.setC_Describe(cursor.getString(3));
				title.setC_CreateTime(cursor.getString(4));
				title.setC_UpdateTime(cursor.getString(5));
				title.setC_IsEnable(cursor.getInt(6));
				title.setC_IsComplete(cursor.getInt(7));
				titles.add(title);
			}
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return titles;
	}
	public static long Insert(Context context,Questionnairetype objQuestionnaireType) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objQuestionnaireType));		
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
	private static ContentValues getContentValues(Questionnairetype objQuestionnaireType) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_TypeId", objQuestionnaireType.getC_TypeId());
		contentValues.put("c_Title", objQuestionnaireType.getC_Title());
		contentValues.put("c_Describe", objQuestionnaireType.getC_Describe());
		contentValues.put("c_CreateTime", objQuestionnaireType.getC_CreateTime());
		contentValues.put("c_UpdateTime", objQuestionnaireType.getC_UpdateTime());
		contentValues.put("c_IsEnable", objQuestionnaireType.getC_IsEnable());
		contentValues.put("c_IsComplete", objQuestionnaireType.getC_IsComplete());
		return contentValues;
	}
}