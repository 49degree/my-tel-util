package com.szxys.mhub.subsystem.mets.db;

import java.util.Date;

import com.szxys.mhub.subsystem.mets.bean.Sysconfig;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;

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
public class SysConfig {
	
	private static final String TableName=MetsData.Tables.SysConfig;	
	
	public static Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		
		Cursor cursor=new MetsDbHelper(context).Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}
	public static Sysconfig getSysConfigObj(Context context) {
		Sysconfig objSysConfig=null;
		String[] columns={"c_Hospital","c_DoctorsNo","c_DoctorsName","c_PatientNo","c_PatientName","c_DevType","c_MobileId","c_MobileName","c_CollectorId",
				"c_CollectorName","c_RationDateTime","c_RationGuDt","c_RecyDateTime","c_CollectDtInterval","c_MaxDuration","c_NoDataTime","c_SendDtInterval",
				"c_MeasuringCupWeight",	"c_Version","c_Copyright","c_WebServiceUrl", "c_IsRecycling", "c_IsRegister", "c_AutoCloseBt", "c_HaveSpecificGravity",
				"c_RebootTimer", "c_TimeInitialLead", "c_GetUpAlarm", "c_GotoBedAlarm", "c_GprsGuid", "c_LastNetCommTime"};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		Cursor cursorSysConfig=dbHelper.Select(TableName, columns, null, null, null, null, null);
		if (cursorSysConfig!=null && cursorSysConfig.getCount()>0) {
			objSysConfig=new Sysconfig();
			cursorSysConfig.moveToFirst();
			objSysConfig.setC_Hospital(cursorSysConfig.getString(0));
			objSysConfig.setC_DoctorsNo(cursorSysConfig.getString(1));
			objSysConfig.setC_DoctorsName(cursorSysConfig.getString(2));
			objSysConfig.setC_PatientNo(cursorSysConfig.getString(3));
			objSysConfig.setC_PatientName(cursorSysConfig.getString(4));
			objSysConfig.setC_DevType(cursorSysConfig.getInt(5));
			objSysConfig.setC_MobileId(cursorSysConfig.getString(6));
			objSysConfig.setC_MobileName(cursorSysConfig.getString(7));
			objSysConfig.setC_CollectorId(cursorSysConfig.getString(8));
			objSysConfig.setC_CollectorName(cursorSysConfig.getString(9));
			objSysConfig.setC_RationDateTime(cursorSysConfig.getString(10));
			objSysConfig.setC_RationGuDt(cursorSysConfig.getString(11));
			objSysConfig.setC_RecyDateTime(cursorSysConfig.getString(12));
			objSysConfig.setC_CollectDtInterval(cursorSysConfig.getInt(13));
			objSysConfig.setC_MaxDuration(cursorSysConfig.getInt(14));
			objSysConfig.setC_NoDataTime(cursorSysConfig.getInt(15));
			objSysConfig.setC_SendDtInterval(cursorSysConfig.getInt(16));
			objSysConfig.setC_MeasuringCupWeight(cursorSysConfig.getInt(17));
			objSysConfig.setC_Version(cursorSysConfig.getString(18));
			objSysConfig.setC_Copyright(cursorSysConfig.getString(19));
			objSysConfig.setC_WebServiceUrl(cursorSysConfig.getString(20)); 
			objSysConfig.setC_IsRecycling(cursorSysConfig.getInt(21));  
			objSysConfig.setC_IsRegister(cursorSysConfig.getInt(22));
			objSysConfig.setC_AutoCloseBt(cursorSysConfig.getInt(23));
			objSysConfig.setC_HaveSpecificGravity(cursorSysConfig.getInt(24));
			objSysConfig.setC_RebootTimer(cursorSysConfig.getInt(25));
			objSysConfig.setC_TimeInitialLead(cursorSysConfig.getInt(26));
			objSysConfig.setC_GetUpAlarm(cursorSysConfig.getString(27));
			objSysConfig.setC_GotoBedAlarm(cursorSysConfig.getString(28));
			objSysConfig.setC_GprsGuid(cursorSysConfig.getString(29));
			objSysConfig.setC_LastNetCommTime(cursorSysConfig.getString(30));
		}
		if (cursorSysConfig!=null && !cursorSysConfig.isClosed()) {
			cursorSysConfig.close();
			dbHelper.close();
		}
		return objSysConfig;
	}
	public static long Insert(Context context,Sysconfig objSysConfig) {		
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objSysConfig));		
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
	public static long ModifySysConfig(Context context,String webServiceUrl,int isAutoCloseBluetoothAfterComm) {
		long result=0;
		ContentValues values=new ContentValues();
		values.put("c_WebServiceUrl", webServiceUrl);
		values.put("c_AutoCloseBt", isAutoCloseBluetoothAfterComm);
		result=Update(context, values, null, null);
		return result;
	}
	public static String[] getUpGotobedTimes(Context context) {
		String[] defaultTimeStrings={"",""};
		String[] columns={"c_GetUpAlarm","c_GotoBedAlarm"};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		//String selection="";
		//String[] selectionArgs={""};
		Cursor cursor=dbHelper.Select(TableName, columns, null, null, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			cursor.moveToFirst();
			defaultTimeStrings[0]=cursor.getString(0);
			defaultTimeStrings[1]=cursor.getString(1);
		}
		return defaultTimeStrings;
	}
	private static ContentValues getContentValues(Sysconfig sysconfig) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_Hospital", sysconfig.getC_Hospital());
		contentValues.put("c_DoctorsNo", sysconfig.getC_DoctorsNo());
		contentValues.put("c_DoctorsName", sysconfig.getC_DoctorsName());
		contentValues.put("c_PatientNo", sysconfig.getC_PatientNo());
		contentValues.put("c_PatientName", sysconfig.getC_PatientName());
		contentValues.put("c_DevType", sysconfig.getC_DevType());
		contentValues.put("c_MobileId", sysconfig.getC_MobileId());
		contentValues.put("c_MobileName", sysconfig.getC_MobileName());
		contentValues.put("c_CollectorId", sysconfig.getC_CollectorId());
		contentValues.put("c_CollectorName", sysconfig.getC_CollectorName());
		contentValues.put("c_RationDateTime", sysconfig.getC_RationDateTime());
		contentValues.put("c_RationGuDt", sysconfig.getC_RationGuDt());
		contentValues.put("c_RecyDateTime", sysconfig.getC_RecyDateTime());
		contentValues.put("c_CollectDtInterval", sysconfig.getC_CollectDtInterval());
		contentValues.put("c_MaxDuration", sysconfig.getC_MaxDuration());
		contentValues.put("c_NoDataTime", sysconfig.getC_NoDataTime());
		contentValues.put("c_SendDtInterval", sysconfig.getC_SendDtInterval());
		contentValues.put("c_MeasuringCupWeight", sysconfig.getC_MeasuringCupWeight());
		contentValues.put("c_Version", sysconfig.getC_Version());
		contentValues.put("c_Copyright", sysconfig.getC_Copyright());
		contentValues.put("c_WebServiceUrl", sysconfig.getC_WebServiceUrl());
		contentValues.put("c_IsRecycling", sysconfig.getC_IsRecycling());
		contentValues.put("c_IsRegister", sysconfig.getC_IsRegister());
		contentValues.put("c_AutoCloseBt", sysconfig.getC_AutoCloseBt());
		contentValues.put("c_HaveSpecificGravity", sysconfig.getC_HaveSpecificGravity());
		contentValues.put("c_RebootTimer", sysconfig.getC_RebootTimer());
		contentValues.put("c_TimeInitialLead", sysconfig.getC_TimeInitialLead());
		contentValues.put("c_GetUpAlarm", sysconfig.getC_GetUpAlarm());
		contentValues.put("c_GotoBedAlarm", sysconfig.getC_GotoBedAlarm());
		contentValues.put("c_GprsGuid", sysconfig.getC_GprsGuid());
		contentValues.put("c_LastNetCommTime", sysconfig.getC_LastNetCommTime());
		return contentValues;
	}
	public static String getIsRecyling(Context context) {
		String objString="";
		Sysconfig objSysConfig=new Sysconfig();
		objSysConfig=getSysConfigObj(context);
		if (objSysConfig!=null) {
			objString=String.valueOf(objSysConfig.getC_IsRecycling());
		}
		return objString;
	}
	public static Date getDeviceGrantTime(Context context) {
		Date dtGrant=null;
		Sysconfig objSysConfig=new Sysconfig();
		objSysConfig=getSysConfigObj(context);
		String dtsString="";
		if (objSysConfig!=null) {
			dtsString=objSysConfig.getC_RationDateTime().trim();
		}
		if (dtsString.length()>10) {
			dtGrant=TimeUtils.convertToDate(dtsString);
		}
		return dtGrant;
	}
	public String[] getDefaultGetupGotobedTime(Context context) {
		String[] dtDefault=new String[2];
		Sysconfig objSysConfig=getSysConfigObj(context);
		if (objSysConfig!=null) {
			dtDefault[0]=(objSysConfig.getC_GetUpAlarm()==null) ? "1":String.valueOf(objSysConfig.getC_GetUpAlarm());
			dtDefault[1]=(objSysConfig.getC_GotoBedAlarm()==null) ? "1":String.valueOf(objSysConfig.getC_GotoBedAlarm());
		}else {
			dtDefault[0]="0";
			dtDefault[1]="0";
		}
		return dtDefault;
	}
	
}