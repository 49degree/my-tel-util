package com.szxys.mhub.subsystem.mets.dao;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.R.integer;
import android.content.ContentValues;
import android.util.Log;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.bean.Urinerecord;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.GotobedGetup;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;

public class DrinkUrineDao {

	/**
	 * 保存采集器采集尿量信息(自动过滤时间有重复的)
	 * @param Drinkandurine objUrine
	 * @return long
	 */
	public static long saveUrineFromCollector(Drinkandurine objUrine){
		long result=-1;
		if (objUrine!=null) {
			String datetimeString=objUrine.getC_DateTime().trim();
			if (!DrinkAndUrine.existSameRecord(MhubApplication.getInstance(), datetimeString)) {
				objUrine.setC_CollectType(1);//1采集器，2病人输入，3医生输入
				result=DrinkAndUrine.Insert(MhubApplication.getInstance(), objUrine);
			}
		}
		return result;
	}
	/**
	 * 保存饮水、排尿(尿急、尿失禁)信息
	 * @param Drinkandurine objInAndOut
	 * @return long result>0 success
	 */
	public static long saveDrinkOrUrine(Drinkandurine objInAndOut) {
		long result=-1;
		if (objInAndOut!=null) {
			objInAndOut.setC_CollectType(2);//1采集器，2病人输入，3医生输入
			result=DrinkAndUrine.Insert(MhubApplication.getInstance(), objInAndOut);
		}
		return result;
	}
	/**
	 * 修改饮水、排尿(尿急、尿失禁)信息
	 * @param String id
	 * @param ContentValues values
	 * @return <=0 failed  >0 success
	 */
	public static int modifyDrinkOrUrine(String id,ContentValues values) {
		int result=-1;
		String where="c_Id=?";
		String[] whereArgs={id};
		result=DrinkAndUrine.Update(MhubApplication.getInstance(), values, where, whereArgs);		
		return result;
	}
	/**
	 * 返回某天的饮水排尿信息
	 * @param dateString "yyyy-MM-dd"
	 * @return ArrayList<Drinkandurine> 实体类
	 */
	public static ArrayList<Drinkandurine> getUrineList(String dateString) {
		String where="c_DateTime>=? and c_DateTime<=?";
		String[] whereArgs={dateString.trim()+" 00:00:00",dateString.trim()+" 23:59:59"};
		String[] thedayStrings=GotobedGetup.getGetUpGotobedTimes(MhubApplication.getInstance(), dateString);
		if (thedayStrings[0].trim().length()>10) {
			whereArgs[0]=thedayStrings[0].trim();
		}
		if (thedayStrings[1].trim().length()>10) {
			whereArgs[1]=thedayStrings[1].trim();
		}
		String orderBy=" c_Id desc";		
		ArrayList<Drinkandurine> list=DrinkAndUrine.Select(MhubApplication.getInstance(), where, whereArgs, null, null, orderBy);
		
		return list;
	}
	/**
	 * 根据id返回饮水排尿记录实体对象
	 * @param int cid
	 * @return Drinkandurine 实体类
	 */
	public static Drinkandurine getDrinkUrineObjectByCid(int cid) {
		return DrinkAndUrine.getInfoByCid(MhubApplication.getInstance(), cid);
	}
	/**
	 * 查询排尿统计(可以不必判断日期早晚顺序，这里会自动判断) split("#")
	 * @param dtFrom "yyyy-MM-dd"
	 * @param dtEnd "yyyy-MM-dd"
	 * @return ArrayList<String>
	 * String="统计日#(白/夜/总)排尿次数#(白/夜/总)排尿量#夜尿指数#尿急#尿失禁"
	 */
	public static ArrayList<String> getUrineStatisticsInfo(String dtFrom,String dtEnd) {
		ArrayList<String> listOfUrines=null;
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
		if (dtFrom.trim().length()>8 && dtEnd.trim().length()>8) {
			Date from=new Date();
			Date end=new Date();
			try {
				from = curFormater.parse(dtFrom);
				end = curFormater.parse(dtEnd);
			} catch (ParseException e) {
				e.printStackTrace();
				Log.d("Dao of DrinkUrine", "unable to parse "+dtFrom+" or "+dtEnd);
			}
			if (from.compareTo(end)>0) {
				listOfUrines=DrinkAndUrine.getMetsStatistics(MhubApplication.getInstance(), dtEnd, dtFrom);
			}else {
				listOfUrines=DrinkAndUrine.getMetsStatistics(MhubApplication.getInstance(), dtFrom, dtEnd);
			}
		}
		return listOfUrines;
	}
	/** 
	 * 获取要上传的饮水记录
	 * @return ArrayList<Drinkandurine>
	 */
	public static ArrayList<Drinkandurine> getUploadDrinkInfo() {
		//ArrayList<Drinkandurine> list=new ArrayList<Drinkandurine>();
		String where="c_IsUpload=? and c_Type=?";
		String[] whereArgs={"0","0"};
		ArrayList<Drinkandurine> list=DrinkAndUrine.Select(MhubApplication.getInstance(), where, whereArgs, null, null, null);
		return list;
	}
	/** 
	 * 获取要上传的排尿记录
	 * @return ArrayList<Drinkandurine>
	 */
	public static ArrayList<Drinkandurine> getUploadUrineInfo() {
		//ArrayList<Drinkandurine> list=new ArrayList<Drinkandurine>();
		String where="c_IsUpload=? and c_Type=?";
		String[] whereArgs={"0","1"};
		ArrayList<Drinkandurine> list=DrinkAndUrine.Select(MhubApplication.getInstance(), where, whereArgs, null, null, null);
		return list;
	}
	/** 
	 * 获取要上传的尿急记录
	 * @return ArrayList<Drinkandurine>
	 */
	public static ArrayList<Drinkandurine> getUploadEmergenturines() {
		//ArrayList<Drinkandurine> list=new ArrayList<Drinkandurine>();
		String where="c_IsUpload=? and c_Type=?";
		String[] whereArgs={"0","2"};
		ArrayList<Drinkandurine> list=DrinkAndUrine.Select(MhubApplication.getInstance(), where, whereArgs, null, null, null);
		return list;
	}
	/** 
	 * 获取要上传的尿失禁记录
	 * @return ArrayList<Drinkandurine>
	 */
	public static ArrayList<Drinkandurine> getUploadLossurines() {
		//ArrayList<Drinkandurine> list=new ArrayList<Drinkandurine>();
		String where="c_IsUpload=? and c_Type=?";
		String[] whereArgs={"0","3"};
		ArrayList<Drinkandurine> list=DrinkAndUrine.Select(MhubApplication.getInstance(), where, whereArgs, null, null, null);
		return list;
	}
}
