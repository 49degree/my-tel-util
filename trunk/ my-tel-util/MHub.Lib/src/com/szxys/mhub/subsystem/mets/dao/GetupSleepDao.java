package com.szxys.mhub.subsystem.mets.dao;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.bean.Getupgotobed;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.GotobedGetup;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;

import android.R.integer;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class GetupSleepDao {
	private Handler mHandler = null;//消息处理对象
	private Activity mActivity = null;//消息处理对象
	
	public GetupSleepDao(Handler handler,Activity activity){
		this.mHandler = handler;
		this.mActivity = activity;
	}
	
	/**
	 * 获取起床时间
	 * 请根据需要格式化Date以适应不同显示需求
	 * @return Date
	 */
	public static Date getTodayGetupTime(){
		Date dt = new Date();
		String theDateString=new SimpleDateFormat("yyyy-MM-dd").format(dt);		
		String[] getGetUpGotobedTime = GotobedGetup.getGetUpGotobedTimes(MhubApplication.getInstance(), theDateString);
		if (getGetUpGotobedTime[0]!=null&&getGetUpGotobedTime[0].trim().length()>10) {
			dt=TimeUtils.convertToDate(getGetUpGotobedTime[0].trim());
		}
		return dt ;
	}
	/**
	 * 获取睡觉时间
	 * 请根据需要格式化Date以适应不同显示需求
	 * @return Date
	 */
	public static Date getTodaySleepTime(){
		Date dt = new Date();
		String theDateString=new SimpleDateFormat("yyyy-MM-dd").format(dt);		
		String[] getGetUpGotobedTime = GotobedGetup.getGetUpGotobedTimes(MhubApplication.getInstance(), theDateString);
		if (getGetUpGotobedTime[1]!=null&&getGetUpGotobedTime[1].trim().length()>10) {
			dt=TimeUtils.convertToDate(getGetUpGotobedTime[1].trim());
		}
		return dt ;
	}
	/**
	 * 保存今天的起床时间
	 * UI里请根据返回结果作出提示(成功/失败，或与某时间逻辑不符等)
	 * 建议UI里调用保存前作些基本判断(不能比当前时间晚，不能是昨天等)
	 * @param timesString "yyyy-MM-dd HH:mm:ss"
	 * @return long >0保存数据成功
	 * <=0保存数据失败(-2时间比现在晚  -3起床时间比今天早 -4 起床时间比今天睡觉时间晚  -5起床时间比昨天睡觉时间早)
	 */
	public static long saveGetupTime(String timesString) {
		long result=-1;
		boolean isRightTime=true;
		Date dtSaved=TimeUtils.convertToDate(timesString);
		Date dtNow=new Date();
		String nowString=new SimpleDateFormat("yyyy-MM-dd").format(dtSaved);
		if (dtSaved.after(dtNow)) {
			result=-2;//"later_than_now";
		}else { //判断提交的起床时间  今天的睡觉时间(如果有), 昨天的睡觉时间(如果有)
			Date dtToday=TimeUtils.convertToDate(nowString+" 00:00:00");
			if (dtSaved.before(dtToday)) {
				result=-3;//"earlier_than_today_zerotime";
				isRightTime=false;
			}else {
				if (isRightTime) {
					String[] dtTodayTimes=GotobedGetup.getGetUpGotobedTimes(MhubApplication.getInstance(), nowString);
					if (dtTodayTimes[1].trim().length()>10) {
						Date dtTodaySleep=TimeUtils.convertToDate(dtTodayTimes[1].trim());
						if (dtSaved.after(dtTodaySleep)) {
							result=-4;//"later_than_today_sleeptime";
							isRightTime=false;
						}
					}
				}				
				if (isRightTime) {
					String yestodaySleepString=GotobedGetup.getGetUpGotobedTimes(MhubApplication.getInstance(), TimeUtils.changeDateTime(nowString,-1))[1];
					if (yestodaySleepString.trim().length()>10) {
						Date dtYestodaySleep=TimeUtils.convertToDate(yestodaySleepString.trim());
						if (dtSaved.before(dtYestodaySleep)) {
							result=-5;//"earlier_than_yestoday_sleeptime";
							isRightTime=false;
						}
					}
				}			
				if (isRightTime) {
					result=GotobedGetup.SaveGetUpTime(MhubApplication.getInstance(),timesString);
				}	
			}					
		}
		return result;
	}
	/**
	 * 保存今天的睡觉时间
	 * UI里请根据返回结果作出提示(成功/失败，或与某时间逻辑不符等)
	 * 建议UI里调用保存前作些基本判断(不能比当前时间晚，不能是昨天等)
	 * @param timesString "yyyy-MM-dd HH:mm:ss"
	 * @return long >0保存成功
	 * <=0提交数据失败(-2提交时间比现在晚  -3 睡觉时间比起床时间早  -4 睡觉时间比今天早)
	 */
	public static long saveSleepTime(String timesString) {
		long result=-1;
		boolean isRightTime=true;
		Date dtSaved=TimeUtils.convertToDate(timesString);
		Date dtNow=new Date();
		String nowString=new SimpleDateFormat("yyyy-MM-dd").format(dtSaved);
		if (dtSaved.after(dtNow)) {
			result=-2;//"later_than_now";
		}else {			
			String dtTodayGetupString=GotobedGetup.getUpTime(MhubApplication.getInstance(), nowString);//							
			if (dtTodayGetupString.trim().length()>10) {
				Date dtTodayGetup=TimeUtils.convertToDate(dtTodayGetupString);
				if (dtSaved.before(dtTodayGetup)) {
					result=-3;//"earlier_than_today_getuptime";
					isRightTime=false;
				}
			}else {
				Date dtToday=TimeUtils.convertToDate(nowString+" 00:00:00");
				if (dtSaved.before(dtToday)) {
					result=-4;//"earlier_than_today_zerotime";
					isRightTime=false;
				}
			}
			if (isRightTime) {
				Date dtCurrentNoon=TimeUtils.convertToDate(nowString+" 12:00:00");
				if (dtSaved.before(dtCurrentNoon)) {
					Calendar calendar=Calendar.getInstance();
					calendar.setTime(dtCurrentNoon);
					calendar.add(Calendar.DATE, -1);
					String yestodayString=new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
					result=GotobedGetup.saveGotobedTime(MhubApplication.getInstance(), yestodayString, timesString);
				}else {
					result=GotobedGetup.SaveGotoBedTime(MhubApplication.getInstance(),timesString);
				}				
			}	
		}		
		return result;
	}
	/**
	 * 
	 * @param date="yyyy-MM-dd"
	 * @param dtGetup="yyyy-MM-dd HH:mm:ss"
	 * @param dtSleep="yyyy-MM-dd HH:mm:ss"
	 * @return int >0保存成功; <=0 保存失败(-2起床/睡觉时间比现在晚  -3 起床时间不是当天
	 *  -4起床时间比睡觉时间晚  -5起床时间比前一天睡觉时间早  -6睡觉时间比后一天起床时间早)
	 */
	public static int updateGetupSleepTimes(String date,String dtGetup,String dtSleep) {
		int result=-1;		
		Date nowDate=new Date();
		Date thedayDate=TimeUtils.getDateFromString(date, TimeUtils.formatDate2); //yyyy-MM-dd 00:00:00
		Date getupDate=TimeUtils.convertToDate(dtGetup);
		Date sleepDate=TimeUtils.convertToDate(dtSleep);
		
		String thedayBefore=TimeUtils.changeDateTime(date, -1);
		String theNextDay=TimeUtils.changeDateTime(date, 1);
		boolean isValidDateTime=true;
		if (isValidDateTime) {
			if (getupDate.after(nowDate) || sleepDate.after(nowDate)) {
				result=-2; //起床/睡觉时间比现在晚
				isValidDateTime=false;
			}
		}
		if (isValidDateTime) {
			if (getupDate.before(thedayDate)) {
				result=-3;//起床时间不是当天(晚于当天00:00:00)
				isValidDateTime=false;
			}
		}
		if (isValidDateTime) {
			if (getupDate.after(sleepDate)) {
				result=-4; //起床时间比睡觉时间晚
				isValidDateTime=false;
			}
		}		
		if (isValidDateTime) {
			String thePreviouSleepTime=GotobedGetup.getGotoBedTime(MhubApplication.getInstance(), thedayBefore);
			if (thePreviouSleepTime.trim().length()>10) {
				Date dtPreviouSleep=TimeUtils.convertToDate(thePreviouSleepTime.trim());
				if (getupDate.before(dtPreviouSleep)) {
					isValidDateTime=false;
					result=-5;//起床时间比前一天睡觉时间早
				}
			}
		}
		if (isValidDateTime) {
			String theNextDayGetupTime=GotobedGetup.getGetupTime(MhubApplication.getInstance(), theNextDay);
			if (theNextDayGetupTime.trim().length()>10) {
				Date dtNextGetup=TimeUtils.convertToDate(theNextDayGetupTime.trim());
				if (sleepDate.after(dtNextGetup)) {
					isValidDateTime=false;
					result=-6;//睡觉时间比后一天起床时间早
				}
			}			
		}
		if (isValidDateTime) {
			result=GotobedGetup.updateGetupSleepTimes(MhubApplication.getInstance(), date, dtGetup, dtSleep);
		}
		return result;
	}
	/** 
	 * 获取要上传的起床睡觉时间
	 * @return ArrayList<Getupgotobed>
	 */
	public static ArrayList<Getupgotobed> getUploadTimes() {
		//ArrayList<Drinkandurine> list=new ArrayList<Drinkandurine>();
		String where="c_IsUpload=?"; // and c_Status=?
		String[] whereArgs={"0"};
		ArrayList<Getupgotobed> list=GotobedGetup.getUploadData(MhubApplication.getInstance(), where, whereArgs, null, null, null);
		return list;
	}
	
}
