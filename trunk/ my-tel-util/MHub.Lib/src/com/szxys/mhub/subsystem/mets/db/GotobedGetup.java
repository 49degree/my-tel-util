package com.szxys.mhub.subsystem.mets.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.szxys.mhub.subsystem.mets.bean.Getupgotobed;
import com.szxys.mhub.subsystem.mets.bean.Sysconfig;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.Log;

/**
 * 数据操作类(执行增、删、改、查)
 * 业务需要的数据均由【刀】com.szxys.mhub.subsystem.mets.dao提供
 * @author Administrator
 *
 */
public class GotobedGetup {
	
	private static final String TableName=MetsData.Tables.GotoBedGetUp;
	
//	public Cursor Select(Context context,String[] columns,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
//		MetsDbHelper dbHelper=new MetsDbHelper(context);
//		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);		
//		return cursor;
//	}
	/**
	 * 获取要上传的起床睡觉时间记录
	 * ArrayList<Getupgotobed>
	 */
	public static ArrayList<Getupgotobed> getUploadData(Context context,String selection, String[] selectionArgs,String groupBy,String having,String orderBy) {
		ArrayList<Getupgotobed> list=new ArrayList<Getupgotobed>();
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] columns={"c_Id","c_Date","c_GetUpTime","c_GotoBedTime","c_IsUpload","c_UniqueId","c_Status"};
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		if (cursor!=null && cursor.getCount()>0) {
			Getupgotobed objGetupgotobed=null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				objGetupgotobed=new Getupgotobed();
				objGetupgotobed.setC_Id(cursor.getInt(0));
				objGetupgotobed.setC_Date(cursor.getString(1));
				objGetupgotobed.setC_GetUpTime(cursor.getString(2));
				objGetupgotobed.setC_GotoBedTime(cursor.getString(3));
				objGetupgotobed.setC_IsUpload(cursor.getInt(4));
				objGetupgotobed.setC_UniqueId(cursor.getString(5));
				objGetupgotobed.setC_Status(cursor.getInt(6));
				list.add(objGetupgotobed);
			}
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return list;
	}
	public static long Insert(Context context,Getupgotobed objGotoBedGetUp) {
		long result=new MetsDbHelper(context).Add(TableName, getContentValues(objGotoBedGetUp));		
		Log.d(TableName,"INSERT success");
		return result;
	}
	public static long SaveGetUpTime(Context context,String getUpTime) {
		// Automatically insert or update
		ContentValues values=new ContentValues();
		long result=0;		
		if (getExistRecord(context)) {
			String where="c_Date=?";
			String[] whereArgs={getNowDate()};			
			values.put("c_GetUpTime", getUpTime);
			result=new MetsDbHelper(context).Update(TableName, values, where, whereArgs);
			Log.d("GetUp ", "Update");
		}else {
			values.put("c_Date", getNowDate());
			values.put("c_GetUpTime", getUpTime);
			values.put("c_UniqueId", SystemClock.elapsedRealtime());
			result=new MetsDbHelper(context).Add(TableName, values);
			Log.d("GetUp ", "Insert");
		}
		return result;
	}
	public static String getGotoBedTime(Context context){
		String dtGotobed="";
		String[] columns={"c_GotoBedTime"};
		String selection="c_Date=?";
		String[] selectionArgs={getNowDate()};
		//if (getExistRecord(context)) {
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 dtGotobed=cursor.getString(0);			 
		}
		 if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		 
		 if(dtGotobed.trim().length()>10) {
			 // the day Exist a gotoBedTime
		}else {
			new SysConfig();
			Sysconfig objConfig=SysConfig.getSysConfigObj(context);
			 if (objConfig!=null) {
				 if (objConfig.getC_GetUpAlarm()!=null && objConfig.getC_GetUpAlarm().trim().length()>0) {
					 dtGotobed=objConfig.getC_GetUpAlarm().trim();
					}
			}
			 if (dtGotobed.trim().length()<10) {
				 dtGotobed=new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime())+" 23:59:59";
			}
			
		}		 
		//}
		return dtGotobed; //"2011-04-24 02:09:02";
	}
	/**
	 * 
	 * @param context
	 * @param dateTimeString="yyyy-MM-dd"
	 * @return String="yyyy-MM-dd HH:mm:ss"
	 */
	public static String getGotoBedTime(Context context,String dateTimeString){
		String dtGotobed="";
		String[] columns={"c_GotoBedTime"};
		String selection="c_Date=?";
		String[] selectionArgs={dateTimeString.trim()};
		//if (getExistRecord(context)) {
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName,columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				 dtGotobed=cursor.getString(0);
			}
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		 if(dtGotobed!=null && dtGotobed.trim().length()>10) {
			 // the day Exist a gotoBedTime
		}else {
			new SysConfig();
			Sysconfig objConfig=SysConfig.getSysConfigObj(context);
			 if (objConfig!=null) {
				 if (objConfig.getC_GetUpAlarm()!=null && objConfig.getC_GetUpAlarm().trim().length()>0) {
					 dtGotobed=objConfig.getC_GetUpAlarm().trim();
					}
			}
		}	
		 if (dtGotobed.trim().length()<10) {
			 dtGotobed=new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime())+" 23:59:59";
		}
		//}
		return dtGotobed; //"2011-04-24 02:09:02";
	}
	public static String getGetupTime(Context context,String dateTimeString){
		String dtGetup="";
		String[] columns={"c_GetUpTime"};
		String selection="c_Date=?";
		String[] selectionArgs={dateTimeString.trim()};
		if (getExistRecord(context)) {
		 MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 dtGetup=cursor.getString(0);
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		}
		return dtGetup;
	}
	public static String getTodayGetupTime(Context context,String YYYY_MM_DD){ // YYYY-MM-DD
		String dtGetup="";
		String[] columns={"c_GetUpTime"};
		String selection="c_Date=?";
		String[] selectionArgs={YYYY_MM_DD.trim()};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 if (cursor.getString(0)!=null && cursor.getString(0).trim().length()>0) {
				 dtGetup=cursor.getString(0);
			}else {
				dtGetup=YYYY_MM_DD+" 12:00:00";
			}			 
		}else {
			dtGetup=YYYY_MM_DD+" 12:00:00";
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		return dtGetup;
	}
	public static String getGetupTime(Context context){
		String dtGetup="";
		String[] columns={"c_GetUpTime"};
		String selection="c_Date=?";
		String[] selectionArgs={getNowDate()};
		if (getExistRecord(context)) {
		 MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 dtGetup=cursor.getString(0);
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		}
		return dtGetup;
	}
	public static String getGetupTimeByDateString(Context context,String date){
		String dtGetup="";
		String[] columns={"c_GetUpTime"};
		String selection="c_Date=?";
		
//		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
//		Date thisDate=new Date();
//		try {
//			thisDate = curFormater.parse(currentDate.substring(0, 10).trim());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
		
		//Date currentDate=new Date(date);
		//String objDate=new SimpleDateFormat("yyyy-MM-dd").format(currentDate.getTime());		
		String[] selectionArgs={date.trim()};
		//if (getExistRecord(context)) {
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 dtGetup=cursor.getString(0);
		}else {
			dtGetup=date+" 12:00:00";
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		 
		//}
		return dtGetup;
	}
	public static String getGetupTimeByDay(Context context,String day){ //"yyyy-MM-dd"
		String dtGetup="";
		String[] columns={"c_GetUpTime"};
		String selection="c_Date=?";		
		String[] selectionArgs={day+" 00:00:00"};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);		 
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 dtGetup=cursor.getString(0);
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		return dtGetup;
	}
	public static String getUpTime(Context context,String dayYYYYmmdd){ //"yyyy-MM-dd"
		String dtGetup="";
		String[] columns={"c_GetUpTime"};
		String selection="c_Date=?";		
		String[] selectionArgs={dayYYYYmmdd.trim()};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);		 
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 dtGetup=cursor.getString(0);
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		return dtGetup;
	}
	public static String getUpTimeUnique(Context context,String dayYYYYmmdd){ //"yyyy-MM-dd"
		String dtGetup=""; //必须存在一个时间
		String[] columns={"c_GetUpTime"};
		String selection="c_Date=?";		
		String[] selectionArgs={dayYYYYmmdd.trim()};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);		 
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 dtGetup=cursor.getString(0);
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		 if (dtGetup.trim().length()<10) {
				dtGetup=SysConfig.getUpGotobedTimes(context)[0];
			if (dtGetup==null || dtGetup.trim().length()<10) {
				dtGetup=dayYYYYmmdd+" 12:00:00";
			}
		}
		return dtGetup;
	}
	public static String getSleepTime(Context context,String dayYYYYmmdd){ //"yyyy-MM-dd"
		String dtSleep="";
		String[] columns={"c_GotoBedTime"};
		String selection="c_Date=?";		
		String[] selectionArgs={dayYYYYmmdd.trim()};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);		 
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
			 dtSleep=cursor.getString(0);
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		return dtSleep;
	}
	public static ArrayList<String[]> getGotobedGetupList(Context context) {
		// “<CountDate>”\t”<SleepTime>”\t”<GetUpTime>”\r\n
		ArrayList<String[]> gotobedGetup=new ArrayList<String[]>();
		String[] columns={"c_Date","c_GotoBedTime","c_GetUpTime"};
		String selection="c_IsUpload=?";
		String[] selectionArgs={"0"};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			for(cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()){
				String[] records=new String[3];
				records[0]=cursor.getString(0);
				records[1]=cursor.getString(1);
				records[2]=cursor.getString(2);
				gotobedGetup.add(records);
			}			
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return gotobedGetup;
	}
	public static long SaveGotoBedTime(Context context,String getUpTime) {
		// Automatically insert or update
		ContentValues values=new ContentValues();
		long result=0;
		if (getExistRecord(context)) {
			String where="c_Date=?";
			String[] whereArgs={getNowDate()};			
			values.put("c_GotoBedTime", getUpTime);
			result=new MetsDbHelper(context).Update(TableName, values, where, whereArgs);
			Log.d("GotoBed ", "Update");
		}else {
			values.put("c_Date", getNowDate());
			values.put("c_GotoBedTime", getUpTime);
			values.put("c_UniqueId", SystemClock.elapsedRealtime());
			result=new MetsDbHelper(context).Add(TableName, values);
			Log.d("GotoBed ", "Insert");
		}
		return result;
	}
	/**
	 * 
	 * @param context
	 * @param theDateString 查询的日期
	 * @return String dtGetUpGotobed : 2个下表的数组，分别存储起床和睡觉时间
	 */
	public static String[] getGetUpGotobedTimes(Context context,String theDateString) { //yyyy-MM-dd
		String[] dtGetUpGotobed=new String[]{"",""};
		String[] columns={"c_GetUpTime","c_GotoBedTime"};
		String selection="c_Date=?";
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] selectionArgs={theDateString.trim()};
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
				 dtGetUpGotobed[0]= cursor.getString(0);
				 dtGetUpGotobed[1]= cursor.getString(1);
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		return dtGetUpGotobed;
	}
	public static String[] getStatisticsTimes(Context context,String theDateString) { //yyyy-MM-dd
		String[] dtGetUpGotobed=new String[]{"","",""}; // daytime  night  nextGetup of the day "yyyy-MM-dd"
		String[] columns={"c_GetUpTime","c_GotoBedTime"};
		String selection="c_Date=?";
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] selectionArgs={theDateString.trim()};
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
				 dtGetUpGotobed[0]= cursor.getString(0);
				 dtGetUpGotobed[1]= cursor.getString(1);
		}
		 String dtNextGetup=getUpTime(context, theDateString);
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		if (dtGetUpGotobed[0].trim().length()>10 && dtGetUpGotobed[1].trim().length()>10) {
			//有起床时间也有睡觉时间
		}else {
			if (dtGetUpGotobed[0].trim().length()>10) { //有起床时间，没有睡觉时间
				
			}
			if (dtGetUpGotobed[1].trim().length()>10) { //有睡觉时间，没有起床时间
				
			}
			//没有起床时间，也没有睡觉时间  【此情形暂不参与统计】
		}
		return dtGetUpGotobed;
	}
	public static String[] getNightTime(Context context,String theDateString) { //yyyy-MM-dd
		String[] dtGetUpGotobed=new String[]{"",""};
		String[] columns={"c_GetUpTime","c_GotoBedTime"};
		String selection="c_Date=?";
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		String[] selectionArgs={theDateString.trim()};
		//if (getExistRecord(context)) {
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();
				 dtGetUpGotobed[0]= cursor.getString(0);
				 dtGetUpGotobed[1]= cursor.getString(1);
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		//}
		 dbHelper.close();
		return dtGetUpGotobed;
	}
	public static String[] getOnedayTimes(Context context,String theDateString) { // 2011-03-25
		String[] dtOneday=new String[2];
		String[] columns={"c_GetUpTime","c_GotoBedTime"};
		String selection="c_Date=?";
		
		String[] selectionArgs={theDateString.trim()};
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");			
		Date onedayDt=new Date();
		try {
			onedayDt = curFormater.parse(theDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(onedayDt);
		Calendar calTmp = (Calendar)calStart.clone();
		calTmp.add(Calendar.DATE,1);//The next day
		Sysconfig objConfig=SysConfig.getSysConfigObj(context);
		 MetsDbHelper dbHelper=new MetsDbHelper(context);
		 Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();			 
			 if (cursor.getString(0)!=null && cursor.getString(0).trim().length()>10) {
				 dtOneday[0]= cursor.getString(0);
			 }else {
				 if (objConfig!=null) {
					 if (objConfig.getC_GetUpAlarm()!=null && objConfig.getC_GetUpAlarm().trim().length()>0) {
						 dtOneday[0]=theDateString+" "+objConfig.getC_GetUpAlarm().trim();
					}else {
						dtOneday[0]=theDateString+" 12:00:00";
					}
				}
			}
			 if (cursor.getString(1)!=null && cursor.getString(1).length()>10) {
				 dtOneday[1]= cursor.getString(1);
			}else {
				String nextGetup=getGetupTimeByDateString(context, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calTmp.getTime()));
				if (nextGetup!=null && nextGetup.trim().length()>10) {
					dtOneday[1]=nextGetup.trim();
				}else {
					if (objConfig!=null) {
						 if (objConfig.getC_GetUpAlarm()!=null && objConfig.getC_GetUpAlarm().trim().length()>0) {
							 dtOneday[1]=theDateString+" "+objConfig.getC_GetUpAlarm().trim();
						}else {
							dtOneday[1]=theDateString+" 12:00:00";
						}
					}
				}
			}
		}else {
			dtOneday[0]="";			
			dtOneday[1]=getGetupTimeByDateString(context, new SimpleDateFormat("yyyy-MM-dd").format(calTmp.getTime()));
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		return dtOneday;
	}
	public static String[] getThedayTimes(Context context,String theDateString) { // 2011-03-25
		String[] dtOneday={"",""};
		String[] columns={"c_GetUpTime","c_GotoBedTime"};
		String selection="c_Date=?";
		
		String[] selectionArgs={theDateString.trim()};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		 if (cursor!=null && cursor.getCount()>0) {
			 cursor.moveToFirst();			 
			 if (cursor.getString(0)!=null && cursor.getString(0).trim().length()>10) {
				 dtOneday[0]= cursor.getString(0);
			 }
			 if (cursor.getString(1)!=null && cursor.getString(1).length()>10) {
				 dtOneday[1]= cursor.getString(1);
			}
		}
		 if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
				dbHelper.close();
			}
		return dtOneday;
	}
	public static long updateYestodaySleepTime(Context context,String dtYestodayGotobed) { //dtYestodayGotobed 17:52
		long result=0;
		Date nowDate=new Date();
		Calendar calNow = Calendar.getInstance(); 
		calNow.setTime(nowDate);
		Calendar calTmp = (Calendar)calNow.clone();
		calTmp.add(Calendar.DATE,-1);
		//String now=new SimpleDateFormat("yyyy-MM-dd").format(nowDate.getTime());
		String yestoday=new SimpleDateFormat("yyyy-MM-dd").format(calTmp.getTime());
		String yesUniqueDt=new SimpleDateFormat("yyyyMMddHHmmss").format(calTmp.getTime());
		String where="c_Id=?";
		long c_Id=getExistCid(context,yestoday);
		if (c_Id>0) {
			ContentValues cv=new ContentValues();
			cv.put("c_GotoBedTime", yestoday+" "+dtYestodayGotobed+":00");
			result=Update(context, cv, where, new String[]{String.valueOf(c_Id)});
		}else {
			result=-7;//是否要加入一条新记录
			Getupgotobed model=new Getupgotobed();
			model.setC_Date(yestoday+" 00:00:00");
			model.setC_GotoBedTime(yestoday+" "+dtYestodayGotobed+":00");
			//model.c_IsUpload=0;
			model.setC_UniqueId(yesUniqueDt+SystemClock.uptimeMillis());
			new GotobedGetup();
			result=GotobedGetup.Insert(context, model);
		}
		return result;
	}
	/**
	 * 更新某日期的起床睡觉时间
	 * @param String day="yyyy-MM-dd"
	 * @param dtGetupString="yyyy-MM-dd HH:mm:ss"
	 * @param dtSleepString="yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static int updateGetupSleepTimes(Context context,String yyyyMMdd,String dtGetupString,String dtSleepString) {
		int row=0;
		String where="c_Date=?";
		String[] whereArgs={yyyyMMdd};
		ContentValues values=new ContentValues();
		values.put("c_GetUpTime", dtGetupString);
		values.put("c_GotoBedTime", dtSleepString);
		row=Update(context, values, where, whereArgs);
		return row;
	}
	/**
	 * 保存指定日期的睡觉时间(自动判断Insert or Update)
	 * [中午12点以前的算昨天的睡觉时间]
	 * @param dateString "2011-05-05"
	 * @param yyMMddHHmmss "2011-05-06 02:34:56"
	 * @return long >0保存成功
	 */
	public static long saveGotobedTime(Context context,String dateString,String yyMMddHHmmss) {
		long result=-1;
		String yesUniqueDt=new SimpleDateFormat("yyyyMMddHHmmss").format(TimeUtils.convertToDate(yyMMddHHmmss));
		String where="c_Id=?";
		long c_Id=getExistCid(context,dateString);
		if (c_Id>0) {
			ContentValues cv=new ContentValues();
			cv.put("c_GotoBedTime", yyMMddHHmmss);
			result=Update(context, cv, where, new String[]{String.valueOf(c_Id)});
		}else {
			//加入一条新记录
			Getupgotobed model=new Getupgotobed();
			model.setC_Date(dateString);
			model.setC_GotoBedTime(yyMMddHHmmss);
			//model.c_IsUpload=0;
			model.setC_UniqueId(yesUniqueDt+SystemClock.uptimeMillis());
			result=GotobedGetup.Insert(context, model);
		}		
		return result;
	}
	public static boolean getExistRecord(Context context) {
		//exist a record
		boolean hasRecord=false;
		String[] columns= {"c_Date","c_GetUpTime","c_GotoBedTime","c_IsUpload","c_UniqueId","c_Status"};
		String selection="c_Date=?";
		String[] selectionArgs={getNowDate()};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			hasRecord=true;
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return hasRecord;
	}
	public static boolean getExistRecord(Context context,String date) { // yyyy-MM-dd
		//exist a record
		boolean hasRecord=false;
		String[] columns= {"c_Date","c_GetUpTime","c_GotoBedTime","c_IsUpload","c_UniqueId","c_Status"};
		String selection="c_Date=?";
		String[] selectionArgs={date.trim()};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			hasRecord=true;
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return hasRecord;
	}
	public static String getNowDate() {
		Date nowDate=new Date();
		String now=new SimpleDateFormat("yyyy-MM-dd").format(nowDate.getTime());
		return now;
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
	private static long getExistCid(Context context,String dateString) {
		//boolean isExistGotoBedRecord=false;	
		long cid=0;
		//exist a GetupAndGotobed record
		//boolean hasRecord=false;
		String[] columns= {"c_Id","c_Date","c_GetUpTime","c_GotoBedTime"};
		String selection="c_Date=?";
		String[] selectionArgs={dateString};
		MetsDbHelper dbHelper=new MetsDbHelper(context);
		Cursor cursor=dbHelper.Select(TableName, columns, selection, selectionArgs, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			//isExistGotoBedRecord=true;
			cursor.moveToFirst();
			cid=cursor.getLong(0);
		}
		if (cursor!=null && !cursor.isClosed()) {
			cursor.close();
			dbHelper.close();
		}
		return cid;
	}
	private static ContentValues getContentValues(Getupgotobed objGotoBedGetUp) {
		ContentValues contentValues=new ContentValues();
		contentValues.put("c_Date", objGotoBedGetUp.getC_Date());
		contentValues.put("c_GetUpTime", objGotoBedGetUp.getC_GetUpTime());
		contentValues.put("c_GotoBedTime", objGotoBedGetUp.getC_GotoBedTime());
		contentValues.put("c_IsUpload", objGotoBedGetUp.getC_IsUpload());
		contentValues.put("c_UniqueId", objGotoBedGetUp.getC_UniqueId());
		contentValues.put("c_Status", objGotoBedGetUp.getC_Status());
		return contentValues;
	}
}